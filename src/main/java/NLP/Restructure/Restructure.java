package NLP.Restructure;

import Database.MongoDBHandler;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import org.bson.Document;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Die Klasse `Restructure` verarbeitet NLP-Analysedaten aus der MongoDB und überträgt sie in eine restrukturierte Form.
 *
 * Sie führt folgende Aufgaben durch:
 * - Extrahiert und transformiert Abhängigkeitsrelationen (`dependencies`)
 * - Aggregiert Named Entities (`namedEntities`) nach ihrem Typ
 * - Gruppiert POS-Tags (`posTags`) nach ihrer jeweiligen Kategorie
 * - Berechnet und speichert Sentiment-Werte für Sätze
 * - Strukturiert Topic-Analysen um und speichert sie in einer separaten Map
 *
 * Nach der Verarbeitung wird das restrukturierte Dokument in der MongoDB-Collection `speeches2` gespeichert.
 */
public class Restructure {
    private static final boolean DEBUG = true; // Debug-Modus aktivieren/deaktivieren

    /**
     * Die Hauptmethode führt den Restrukturierungsprozess für alle NLP-Dokumente aus der MongoDB-Collection `casData` durch.
     *
     * @param args Konsolenargumente (nicht benötigt)
     */
    public static void main(String[] args) {
        try {
            MongoDBHandler mongoDBHandler = new MongoDBHandler();
            MongoCollection<Document> casDataColl = mongoDBHandler.getDatabase().getCollection("casData");
            MongoCollection<Document> speechColl = mongoDBHandler.getDatabase().getCollection("speeches2");

            FindIterable<Document> casDataDocs = casDataColl.find();
            int processedCount = 0, updatedCount = 0, errorCount = 0;

            for (Document casDoc : casDataDocs) {
                processedCount++;
                String speechId = casDoc.getString("speechId");

                if (DEBUG) log("Verarbeite SpeechID: " + speechId);

                try {
                    // Daten verarbeiten
                    Map<String, List<String>> dependenciesMap = processDependencies(casDoc);
                    Map<String, List<String>> namedEntitiesMap = processNamedEntities(casDoc);
                    Map<String, List<String>> posTagsMap = processPosTags(casDoc);
                    Map<String, Double> sentenceSentimentMap = processSentences(casDoc);
                    Map<String, List<List<Object>>> topicsMap = processTopics(casDoc);

                    // Erstelle neues restrukturiertes Dokument für die MongoDB-Collection `speeches2`
                    Document updateDocument = new Document("id", speechId)
                            .append("analysis", new Document()
                                    .append("dependencies", dependenciesMap)
                                    .append("lemmas", casDoc.getList("tokens", String.class))
                                    .append("namedEntities", namedEntitiesMap)
                                    .append("posTags", posTagsMap)
                                    .append("sentences", sentenceSentimentMap)
                                    .append("tokens", casDoc.getList("tokens", String.class))
                                    .append("topics", topicsMap)
                            );

                    if (DEBUG) log("Update-Dokument für SpeechID " + speechId + ": " + updateDocument.toJson());

                    speechColl.updateOne(Filters.eq("id", speechId), new Document("$set", updateDocument), new UpdateOptions().upsert(true));
                    updatedCount++;

                } catch (Exception e) {
                    errorCount++;
                    logError("Fehler bei SpeechID " + speechId, e);
                }
            }

            // Zusammenfassung der Verarbeitung
            log("Verarbeitung abgeschlossen: " + processedCount + " verarbeitet, " + updatedCount + " aktualisiert, " + errorCount + " Fehler.");
        } catch (Exception e) {
            logError("Fehler beim Verbinden mit MongoDB", e);
        }
    }

    /**
     * Verarbeitet Abhängigkeitsrelationen (dependencies) und gruppiert sie nach Relationstyp.
     *
     * @param casDoc Das MongoDB-Dokument mit den NLP-Analysedaten
     * @return Eine Map, die die Relationstypen als Schlüssel und die zugehörigen Wortpaare als Werte enthält.
     */
    private static Map<String, List<String>> processDependencies(Document casDoc) {
        List<String> dependencies = casDoc.getList("dependencies", String.class);
        Map<String, List<String>> dependenciesMap = new HashMap<>();

        if (dependencies != null) {
            for (String dep : dependencies) {
                Matcher matcher = Pattern.compile("Relation: (.*?) - (.*?) -> (.*)").matcher(dep);
                if (matcher.find()) {
                    String relation = matcher.group(1);
                    String word1 = matcher.group(2);
                    String word2 = matcher.group(3);

                    dependenciesMap.putIfAbsent(relation, new ArrayList<>());
                    dependenciesMap.get(relation).add(word1 + " -> " + word2);
                }
            }
        }
        return dependenciesMap;
    }

    /**
     * Verarbeitet Named Entities und gruppiert sie nach ihrem Typ.
     *
     * @param casDoc Das MongoDB-Dokument mit Named Entities
     * @return Eine Map, die Named Entity Typen als Schlüssel und die zugehörigen Entitäten als Werte enthält.
     */
    private static Map<String, List<String>> processNamedEntities(Document casDoc) {
        List<String> namedEntities = casDoc.getList("namedEntities", String.class);
        Map<String, List<String>> namedEntitiesMap = new HashMap<>();

        if (namedEntities != null) {
            for (String entity : namedEntities) {
                Matcher matcher = Pattern.compile("(.*?) \\((.*?)\\)").matcher(entity);
                if (matcher.find()) {
                    String word = matcher.group(1);
                    String entityType = matcher.group(2);

                    namedEntitiesMap.putIfAbsent(entityType, new ArrayList<>());
                    namedEntitiesMap.get(entityType).add(word);
                }
            }
        }
        return namedEntitiesMap;
    }

    /**
     * Verarbeitet POS-Tags und gruppiert sie nach ihrer Kategorie.
     *
     * @param casDoc Das MongoDB-Dokument mit POS-Tags
     * @return Eine Map, die POS-Tags als Schlüssel und die zugehörigen Wörter als Werte enthält.
     */
    private static Map<String, List<String>> processPosTags(Document casDoc) {
        List<String> posTags = casDoc.getList("posTags", String.class);
        Map<String, List<String>> posTagsMap = new HashMap<>();

        if (posTags != null) {
            for (String tag : posTags) {
                Matcher matcher = Pattern.compile("(.*?) \\[(.*?)\\]").matcher(tag);
                if (matcher.find()) {
                    String word = matcher.group(1);
                    String posTag = matcher.group(2);

                    posTagsMap.putIfAbsent(posTag, new ArrayList<>());
                    posTagsMap.get(posTag).add(word);
                }
            }
        }
        return posTagsMap;
    }

    /**
     * Extrahiert Sätze und ihre zugehörigen Sentiment-Werte.
     *
     * @param casDoc Das MongoDB-Dokument mit Sätzen und Sentiment-Werten
     * @return Eine Map, die Sätze als Schlüssel und deren Sentiment-Werte als Werte enthält.
     */
    private static Map<String, Double> processSentences(Document casDoc) {
        List<String> sentences = casDoc.getList("sentences", String.class);
        Map<String, Double> sentenceSentimentMap = new HashMap<>();

        if (sentences != null) {
            for (String sentence : sentences) {
                Matcher matcher = Pattern.compile("Satz: (.*?)\\s*-> Sentiment: (-?\\d+\\.\\d+)").matcher(sentence);
                if (matcher.find()) {
                    String sentenceText = matcher.group(1).trim();
                    double sentimentValue = Double.parseDouble(matcher.group(2).trim());
                    sentenceSentimentMap.put(sentenceText, sentimentValue);
                }
            }
        }
        return sentenceSentimentMap;
    }

    /**
     * Verarbeitet die Topics aus dem NLP-Analyse-Dokument.
     *
     * @param casDoc Das MongoDB-Dokument mit den Topic-Analysen
     * @return Eine Map, die Sätze als Schlüssel und ihre zugehörigen Topics mit Scores als Werte enthält.
     */
    private static Map<String, List<List<Object>>> processTopics(Document casDoc) {
        List<String> topics = casDoc.getList("topics", String.class);
        Map<String, List<List<Object>>> topicsMap = new HashMap<>();

        if (topics != null) {
            for (String topicEntry : topics) {
                Matcher matcher = Pattern.compile("Sentence: (.*?) -> Topics: (.*)").matcher(topicEntry);
                if (matcher.find()) {
                    String sentence = matcher.group(1).trim();
                    String topicsString = matcher.group(2).trim();
                    List<List<Object>> topicList = new ArrayList<>();

                    topicsMap.put(sentence, topicList);
                }
            }
        }
        return topicsMap;
    }
    /**
     * Gibt eine Debug-Nachricht in der Konsole aus, wenn der DEBUG-Modus aktiviert ist.
     *
     * @param message Die Nachricht, die ausgegeben werden soll.
     */
    private static void log(String message) {
        if (DEBUG) {
            System.out.println("[DEBUG] " + message);
        }
    }

    /**
     * Gibt eine Fehlermeldung mit einer Exception in der Konsole aus.
     *
     * @param message Die Fehlernachricht.
     * @param e Die aufgetretene Exception.
     */
    private static void logError(String message, Exception e) {
        System.err.println("[ERROR] " + message);
        e.printStackTrace();
    }

}



