package NLP;

import Database.MongoDBHandler;
import NLP.DataNLP.Initialize;
import com.mongodb.MongoCursorNotFoundException;
import com.mongodb.MongoSocketReadException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import NLP.JCas.Jcasconverter;
import NLP.Model.Speech;
import org.apache.uima.jcas.JCas;
import NLP.DataNLP.Extraction;
import NLP.JCas.Caseserialization;

import java.io.File;
import java.util.List;
/**
 * The NLPMain class is responsible for running the natural language processing (NLP) pipeline,
 * which processes speeches from a MongoDB database, performs linguistic analysis,
 * and stores the results back into the database.
 * <p>
 * It performs the following steps:
 * <ul>
 *   <li>Fetches speech data from a MongoDB collection.</li>
 *   <li>Checks whether the speech has already been analyzed or serialized.</li>
 *   <li>Converts speech data into a JCAS object.</li>
 *   <li>Performs various NLP tasks such as tokenization, POS tagging, dependency parsing, named entity recognition, etc.</li>
 *   <li>Stores the results back into the MongoDB database.</li>
 * </ul>
 * </p>
 */
public class NLPMain {
    private static final String SERIALIZED_DIR = "serialized";
    /**
     * The main entry point of the NLP application.
     * <p>
     * This method runs an infinite loop to process speeches, and it handles various types of MongoDB-related exceptions.
     * If the NLP process encounters an error (e.g., MongoDB connection issues or cursor not found), it will retry after a brief pause.
     * </p>
     *
     *
     */
    public static void main(String[] args) {
        while (true) {
            try {
                runNLPProcess();
                break; // Erfolgreich durchlaufen, Schleife beenden
            } catch (MongoSocketReadException e) {
                System.err.println("MongoDB-Verbindungsfehler: " + e.getMessage());
            } catch (MongoCursorNotFoundException e) {
                System.err.println("MongoDB-Cursor wurde unerwartet geschlossen: " + e.getMessage());
                System.out.println("Starte den Prozess neu...");
            } catch (Exception e) {
                e.printStackTrace();
                break; // Beende bei anderen Fehlern
            }
            try {
                Thread.sleep(5000); // Warte 5 Sekunden, bevor neu gestartet wird
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            }
        }
    }
    /**
     * Runs the entire NLP pipeline.
     * <p>
     * This method performs the following tasks:
     * <ul>
     *   <li>Fetches speeches from the MongoDB database.</li>
     *   <li>Checks if the speech has already been analyzed or serialized.</li>
     *   <li>Converts the speech content into a JCAS (Java Common Analysis Structure) object.</li>
     *   <li>Performs NLP tasks like sentence extraction, tokenization, POS tagging, dependency parsing, named entity recognition, topic extraction, sentiment analysis, and lemma extraction.</li>
     *   <li>Saves the results back into MongoDB with updates or insertions as necessary.</li>
     * </ul>
     * </p>
     *
     * @throws Exception If any error occurs during the process, such as MongoDB connection issues or analysis failures.
     */
    public static void runNLPProcess() throws Exception {
        // Initialize MongoDB handler and collections
        MongoDBHandler mongoDBHandler = new MongoDBHandler();
        MongoCollection<Document> speechColl = mongoDBHandler.getDatabase().getCollection("speeches");
        MongoCollection<Document> casDataColl = mongoDBHandler.getDatabase().getCollection("casData");

        MongoCursor<Document> cursor = speechColl.find().projection(new Document("id", 1).append("content", 1)).iterator();

        Initialize initialize = new Initialize();

        File serDir = new File(SERIALIZED_DIR);
        if (!serDir.exists()) {
            serDir.mkdir();
        }

        while (cursor.hasNext()) {
            Document doc = cursor.next();
            String id = doc.getString("id");
            String content = doc.getString("content");

            if (content != null && !content.isEmpty()) {
                // ErstelL Serialized Directory
                String fileName = SERIALIZED_DIR + File.separator + id + ".xmi";
                File xmiFile = new File(fileName);
                // Prüfe, ob die Datei im serialized-Ordner existiert
                if (xmiFile.exists()) {
                    System.out.println("Überspringe " + id + ": Datei existiert bereits im serialized-Ordner.");
                    continue;
                }
                // Check if the speech has already been analyzed
                Document existingAnalysis = casDataColl.find(new Document("speechId", id)).first();
                if (existingAnalysis != null) {
                    System.out.println("Analyse für Rede mit ID " + id + " existiert bereits. Überspringe Verarbeitung.");
                    continue;
                }
                // Create a Speech object and convert it into a JCAS object
                Speech speech = new Speech();
                speech.setId(id);
                speech.setText(content);
                System.out.println("Verarbeite Rede: " + speech);

                JCas jcas = Jcasconverter.convert(speech);
                System.out.println("JCAS erstellt: " + jcas);
                // Save the JCAS object to a file
                Caseserialization.saveCas(jcas, xmiFile);
                System.out.println("CAS für Dokument " + speech.getId() + " wurde neu erzeugt und gespeichert.");
                // Process the JCAS object
                initialize.processJCas(jcas);
                // Extract various linguistic features
                List<String> sentenceResults = Extraction.extractSentences(jcas);
                List<String> tokenResults = Extraction.extractTokens(jcas);
                List<String> posResults = Extraction.extractPOSTags(jcas);
                List<String> dependencyResults = Extraction.extractDependencies(jcas);
                List<String> neResults = Extraction.extractNamedEntities(jcas);
                List<String> topicResults = Extraction.extractTopics(jcas);
                List<String> sentimentResults = Extraction.extractAnalysisResults(jcas);
                List<String> lemmaResults = Extraction.extractLemmas(jcas);
                // Prepare the analysis results as a MongoDB document
                Document analysisResults = new Document();
                analysisResults.append("speechId", id)
                        .append("tokens", tokenResults)
                        .append("posTags", posResults)
                        .append("dependencies", dependencyResults)
                        .append("namedEntities", neResults)
                        .append("topics", topicResults)
                        .append("sentiment", sentimentResults)
                        .append("lemma", lemmaResults);
                // Insert or update the analysis results in the MongoDB collection
                UpdateOptions options = new UpdateOptions().upsert(true);
                UpdateResult result = casDataColl.updateOne(
                        new Document("speechId", id),
                        new Document("$set", analysisResults),
                        options
                );

                if (result.getMatchedCount() > 0) {
                    System.out.println("Dokument mit speechId " + id + " wurde aktualisiert.");
                } else if (result.getUpsertedId() != null) {
                    System.out.println("Neues Dokument mit speechId " + id + " wurde eingefügt. ID: " + result.getUpsertedId());
                } else {
                    System.out.println("Keine Änderungen vorgenommen für speechId " + id);
                }
            }
        }
    }
}





