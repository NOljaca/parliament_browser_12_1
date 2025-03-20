package NLP.DataNLP;



import java.net.HttpURLConnection;
import java.net.URL;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.api.ner.type.NamedEntity;
import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.dependency.Dependency;
import org.hucompute.textimager.uima.type.Sentiment;
import org.apache.uima.jcas.JCas;
import org.apache.uima.fit.util.JCasUtil;
import java.io.OutputStream;
import java.io.InputStream;
import java.util.Scanner;

public class Extraction {

    /**
     * Führt eine Themenanalyse durch, indem es die ParlBERT-API verwendet.
     *
     * @param jcas Das zu analysierende CAS.
     * @return Eine Liste von extrahierten Themen.
     */
    public static List<String> extractTopics(JCas jcas) throws Exception {
        List<String> topics = new ArrayList<>();
        String apiUrl = "http://parlbert.lehre.texttechnologylab.org/v1/process";

        for (Sentence sentence : JCasUtil.select(jcas, Sentence.class)) {
            // Entfernen von Zeilenumbrüchen und unnötigen Leerzeichen, um den vollständigen Satz zu analysieren
            String sentenceText = sentence.getCoveredText().replaceAll("\\n", " ").trim();

            // JSON-Request für den aktuellen Satz
            String jsonInput = String.format(
                    "{\"doc_text\": \"%s\", \"sentences\": [{\"text\": \"%s\", \"iBegin\": 0, \"iEnd\": %d}]}",
                    sentenceText, sentenceText, sentenceText.length()
            );

            try {
                URL url = new URL(apiUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; utf-8");
                conn.setRequestProperty("Accept", "application/json");
                conn.setDoOutput(true);

                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = jsonInput.getBytes("utf-8");
                    os.write(input, 0, input.length);
                }

                if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    throw new Exception("HTTP request failed with code: " + conn.getResponseCode());
                }

                try (InputStream is = conn.getInputStream(); Scanner scanner = new Scanner(is, "utf-8")) {
                    String jsonResponse = scanner.useDelimiter("\\A").next();
                    ObjectMapper objectMapper = new ObjectMapper();
                    JsonNode rootNode = objectMapper.readTree(jsonResponse);
                    JsonNode labelsArray = rootNode.path("labels");

                    StringBuilder sentenceTopics = new StringBuilder("Sentence: ").append(sentenceText).append(" -> Topics: [");

                    if (labelsArray.isArray()) {
                        for (JsonNode labelNode : labelsArray) {
                            String label = labelNode.path("label").asText();
                            double score = labelNode.path("score").asDouble();
                            sentenceTopics.append(String.format("[%s, %.16f], ", label, score));
                        }
                    }

                    if (sentenceTopics.toString().endsWith(", ")) {
                        sentenceTopics.setLength(sentenceTopics.length() - 2); // Letztes Komma und Leerzeichen entfernen
                    }

                    sentenceTopics.append("]");
                    topics.add(sentenceTopics.toString());
                }
            } catch (Exception e) {
                System.err.println("Error processing sentence: " + sentenceText);
                e.printStackTrace();
            }
        }

        return topics;
    }
 


    public static List<String> extractTokens(JCas jcas) {
        List<String> tokens = new ArrayList<>();
        for (Token token : JCasUtil.select(jcas, Token.class)) {
            tokens.add(token.getCoveredText());
        }
        return tokens;
    }

    public static List<String> extractPOSTags(JCas jcas) {
        List<String> posList = new ArrayList<>();
        for (Token token : JCasUtil.select(jcas, Token.class)) {
            String tokenText = token.getCoveredText();
            String posTag = (token.getPos() != null) ? token.getPos().getPosValue() : "unbekannt";
            posList.add(tokenText + " [" + posTag + "]");
        }
        return posList;
    }

    public static List<String> extractDependencies(JCas jcas) {
        List<String> dependencies = new ArrayList<>();
        for (Dependency dep : JCasUtil.select(jcas, Dependency.class)) {
            String governor = (dep.getGovernor() != null) ? dep.getGovernor().getCoveredText() : "null";
            String dependent = (dep.getDependent() != null) ? dep.getDependent().getCoveredText() : "null";
            String relation = (dep.getDependencyType() != null) ? dep.getDependencyType() : "unbekannt";
            dependencies.add("Relation: " + relation + " - " + governor + " -> " + dependent);
        }
        return dependencies;
    }

    public static List<String> extractNamedEntities(JCas jcas) {
        List<String> entities = new ArrayList<>();
        for (NamedEntity ne : JCasUtil.select(jcas, NamedEntity.class)) {
            String neText = ne.getCoveredText();
            String neType = (ne.getValue() != null) ? ne.getValue() : "unbekannt";
            entities.add(neText + " (" + neType + ")");
        }
        return entities;
    }

    public static List<String> extractAnalysisResults(JCas jcas) {
        List<String> results = new ArrayList<>();
        for (Sentence sentence : JCasUtil.select(jcas, Sentence.class)) {
            StringBuilder sb = new StringBuilder();
            sb.append("Satz: ").append(sentence.getCoveredText()).append("\n");
            List<Sentiment> sentiments = JCasUtil.selectCovered(Sentiment.class, sentence);
            if (!sentiments.isEmpty()) {
                for (Sentiment s : sentiments) {
                    sb.append("  -> Sentiment: ").append(s.getSentiment()).append("\n"); // Anpassung
                }
            } else {
                sb.append("  -> (Keine Sentiment-Annotation gefunden)\n");
            }
            results.add(sb.toString());
        }
        return results;
    }

    public static List<String> extractLemmas(JCas jcas) {
        List<String> lemmas = new ArrayList<>();
        for (Token token : JCasUtil.select(jcas, Token.class)) {
            if (token.getLemma() != null) {
                lemmas.add(token.getLemma().getValue());
            } else {
                lemmas.add(token.getCoveredText());
            }
        }
        return lemmas;
    }

    public static List<String> extractSentences(JCas jcas) {
        List<String> sentences = new ArrayList<>();
        for (Sentence sentence : JCasUtil.select(jcas, Sentence.class)) {
            sentences.add(sentence.getCoveredText());
        }
        return sentences;
    }
}




