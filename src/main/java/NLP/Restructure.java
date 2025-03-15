package NLP;

import Database.MongoDBHandler;
import com.mongodb.MongoBulkWriteException;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import org.bson.Document;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Restructure {
    private static final boolean DEBUG = true; // Debug-Modus aktivieren/deaktivieren

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
                    Map<String, String> namedEntitiesMap = processNamedEntities(casDoc);
                    Map<String, String> posTagsMap = processPosTags(casDoc);
                    Map<String, Double> sentenceSentimentMap = processSentences(casDoc);
                    Map<String, List<List<Object>>> topicsMap = processTopics(casDoc);

                    // Erstelle neues Dokument für speeches2
                    Document updateDocument = new Document("id", speechId)
                            .append("analysis", new Document()
                                    .append("dependencies", dependenciesMap)
                                    .append("lemmas", casDoc.getList("tokens" , String.class))
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

                    // Abhängigkeitstypen sammeln
                    dependenciesMap.putIfAbsent(relation, new ArrayList<>());
                    dependenciesMap.get(relation).add(word1 + " -> " + word2);
                }
            }
        }
        return dependenciesMap;
    }

    private static Map<String, String> processNamedEntities(Document casDoc) {
        List<String> namedEntities = casDoc.getList("namedEntities", String.class);
        Map<String, String> namedEntitiesMap = new HashMap<>();
        if (namedEntities != null) {
            for (String entity : namedEntities) {
                Matcher matcher = Pattern.compile("(.*?) \\((.*?)\\)").matcher(entity);
                if (matcher.find()) {
                    namedEntitiesMap.put(matcher.group(1), matcher.group(2));
                }
            }
        }
        return namedEntitiesMap;
    }

    private static Map<String, String> processPosTags(Document casDoc) {
        List<String> posTags = casDoc.getList("posTags", String.class);
        Map<String, String> posTagsMap = new HashMap<>();
        if (posTags != null) {
            for (String tag : posTags) {
                Matcher matcher = Pattern.compile("(.*?) \\[(.*?)\\]").matcher(tag);
                if (matcher.find()) {
                    posTagsMap.put(matcher.group(1), matcher.group(2));
                }
            }
        }
        return posTagsMap;
    }


    private static Map<String, Double> processSentences(Document casDoc) {
        List<String> sentences = casDoc.getList("sentences", String.class);
        List<String> sentiments = casDoc.getList("sentiment", String.class);
        Map<String, Double> sentenceSentimentMap = new HashMap<>();

        // Wenn "sentences" vorhanden ist, verarbeite sie
        if (sentences != null) {
            for (String sentence : sentences) {
                Matcher matcher = Pattern.compile("Satz: (.*?)\\s*-> Sentiment: (-?\\d+\\.\\d+|\\d+)").matcher(sentence);
                if (matcher.find()) {
                    String sentenceText = matcher.group(1).trim();
                    double sentimentValue = Double.parseDouble(matcher.group(2).trim());
                    sentenceSentimentMap.put(sentenceText, sentimentValue);
                }
            }
        }

        // Wenn "sentiment" vorhanden ist, gehe davon aus, dass es zu den Sätzen gehört
        else if (sentiments != null) {
            for (String sentiment : sentiments) {
                Matcher matcher = Pattern.compile("Satz: (.*?)\\s*-> Sentiment: (-?\\d+\\.\\d+|\\d+)").matcher(sentiment);
                if (matcher.find()) {
                    String sentenceText = matcher.group(1).trim();
                    double sentimentValue = Double.parseDouble(matcher.group(2).trim());
                    sentenceSentimentMap.put(sentenceText, sentimentValue);
                }
            }
        }

        return sentenceSentimentMap;
    }






    private static Map<String, List<List<Object>>> processTopics(Document casDoc) {
        List<String> topics = casDoc.getList("topics", String.class);
        Map<String, List<List<Object>>> topicsMap = new HashMap<>();

        if (topics != null) {
            Pattern sentencePattern = Pattern.compile("Sentence: (.*?) -> Topics: (.*)");
            Pattern topicPattern = Pattern.compile("\\[([^,]+),\\s*([\\d.,E-]+)]|([^,]+)\\s*->\\s*([\\d.,E-]+)");

            for (String topicEntry : topics) {
                Matcher sentenceMatcher = sentencePattern.matcher(topicEntry);
                if (sentenceMatcher.find()) {
                    String sentence = sentenceMatcher.group(1).trim();
                    String topicsString = sentenceMatcher.group(2).trim();
                    List<List<Object>> topicList = new ArrayList<>();

                    Matcher topicMatcher = topicPattern.matcher(topicsString);
                    while (topicMatcher.find()) {
                        try {
                            String topicName = topicMatcher.group(1) != null ? topicMatcher.group(1).trim() : topicMatcher.group(3).trim();
                            String topicValueStr = topicMatcher.group(2) != null ? topicMatcher.group(2).trim() : topicMatcher.group(4).trim();
                            double topicValue = Double.parseDouble(topicValueStr.replace(",", "."));

                            topicList.add(Arrays.asList(topicName, topicValue));
                        } catch (NumberFormatException | NullPointerException e) {
                            System.err.println("Fehler beim Parsen des Topics: " + topicMatcher.group());
                        }
                    }

                    topicsMap.put(sentence, topicList);
                }
            }
        }
        return topicsMap;
    }







    private static void log(String message) {
        if (DEBUG) {
            System.out.println("[DEBUG] " + message);
        }
    }

    private static void logError(String message, Exception e) {
        System.err.println("[ERROR] " + message);
        e.printStackTrace();
    }
}


