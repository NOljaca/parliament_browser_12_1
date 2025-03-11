package NLP;

import Database.MongoDBHandler;
import NLP.DataNLP.Initialize;
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

public class NLPMain {
    private static final String SERIALIZED_DIR = "serialized";

    public static void main(String[] args) throws Exception {
        // Verbindung zur MongoDB herstellen
        MongoDBHandler mongoDBHandler = new MongoDBHandler();
        MongoCollection<Document> speechColl = mongoDBHandler.getDatabase().getCollection("speeches");
        MongoCollection<Document> casDataColl = mongoDBHandler.getDatabase().getCollection("casData");

        // MongoDB-Abfrage durchführen und nur die id und den content abrufen
        MongoCursor<Document> cursor = speechColl.find().projection(new Document("id", 1).append("content", 1)).iterator();

        // Erstelle eine Instanz der NLP-Pipeline
        //NlppipelineService nlpPipeline = new NlppipelineService();"
        Initialize initialize = new Initialize();

        // Erzeuge den Ordner für serialisierte CAS-Dateien, falls nicht vorhanden
        File serDir = new File(SERIALIZED_DIR);
        if (!serDir.exists()) {
            serDir.mkdir();
        }
        // Durchlaufe alle Dokumente und führe die NLP-Analyse aus
        while (cursor.hasNext()) {
            Document doc = cursor.next();
            String id = doc.getString("id");
            String content = doc.getString("content");

            // Überprüfe, ob content vorhanden ist
            if (content != null && !content.isEmpty()) {
                // Überprüfe, ob bereits eine Serialisierung für diese Rede existiert
                Document existingAnalysis = casDataColl.find(new Document("speechId", id)).first();
                if (existingAnalysis != null) {
                    System.out.println("Analyse für Rede mit ID " + id + " existiert bereits. Überspringe Verarbeitung.");
                    continue;
                }

                // Erstelle ein Speech-Objekt und setze die ID und den Text
                Speech speech = new Speech();
                speech.setId(id);
                speech.setText(content);
                System.out.println("Verarbeite Rede: " + speech);

                // Umwandlung des Speech-Objekts in ein JCAS
                JCas jcas = Jcasconverter.convert(speech);
                System.out.println("JCAS erstellt: " + jcas);
                String fileName = SERIALIZED_DIR + File.separator + speech.getId() + ".xmi";
                File xmiFile = new File(fileName);

                // Überprüfe, ob die XMI-Datei bereits existiert und lade sie, wenn sie existiert
                if (xmiFile.exists()) {
                    jcas = Caseserialization.loadCas(xmiFile);
                    System.out.println("CAS für Dokument " + speech.getId() + " wurde geladen.");
                } else {
                    // Speichere das JCAS, wenn es noch nicht existiert
                    Caseserialization.saveCas(jcas, xmiFile);
                    System.out.println("CAS für Dokument " + speech.getId() + " wurde neu erzeugt und gespeichert.");
                }

                // Führe die NLP-Analyse auf dem JCAS durch
                System.out.println("Analysiere Rede mit ID: " + id);
                //nlpPipeline.processJCas(jcas);
                initialize.processJCas(jcas);

                // Extrahiere die Ergebnisse der NLP-Analyse
                List<String> sentenceResults = Extraction.extractAnalysisResults(jcas);
                List<String> tokenResults = Extraction.extractTokens(jcas);
                List<String> posResults = Extraction.extractPOSTags(jcas);
                List<String> dependencyResults = Extraction.extractDependencies(jcas);
                List<String> neResults = Extraction.extractNamedEntities(jcas);
                List<String> topicResults = Extraction.extractTopics(jcas);

                // Erstelle ein Document mit den Ergebnissen der Analyse
                Document analysisResults = new Document();
                analysisResults.append("speechId", id)
                        .append("sentences", sentenceResults)
                        .append("tokens", tokenResults)
                        .append("posTags", posResults)
                        .append("dependencies", dependencyResults)
                        .append("namedEntities", neResults)
                        .append("topics", topicResults);

                // Speichere die Analyseergebnisse direkt in der CasData-Collection
                UpdateOptions options = new UpdateOptions().upsert(true); // Aktiviert das Upsert-Verhalten
                UpdateResult result = casDataColl.updateOne(
                        new Document("speechId", id), // Filterkriterium
                        new Document("$set", analysisResults), // Die zu setzenden Werte
                        options // Optionen mit Upsert aktiviert
                );

                // Überprüfe das Ergebnis der Operation
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







