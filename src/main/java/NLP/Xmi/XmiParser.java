package NLP.Xmi;

import org.bson.Document;
import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * A utility class to parse XMI (XML Metadata Interchange) files and extract linguistic annotations.
 * The extracted annotations include sentences, tokens, part-of-speech (POS) tags, named entities,
 * dependency relations, lemmas, topics, and sentiment.
 */
public class XmiParser {

    /**
     * Parses an XMI file to extract linguistic data and converts it into a MongoDB-compatible Document.
     *
     * @param filePath The path to the XMI file to be parsed.
     * @param speechId A unique identifier for the speech or document being processed.
     * @return A MongoDB Document containing the parsed linguistic data.
     * @throws IOException If there is an error while parsing the file or extracting data.
     */
    public static Document parseXmiToDocument(String filePath, String speechId) throws IOException {
        try {
            File file = new File(filePath);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            org.w3c.dom.Document xmlDoc = builder.parse(file);
            xmlDoc.getDocumentElement().normalize();

            // Lists to store linguistic annotations
            List<String> sentences = new ArrayList<>();
            List<String> tokens = new ArrayList<>();
            List<String> posTags = new ArrayList<>();
            List<String> namedEntities = new ArrayList<>();
            List<String> dependencies = new ArrayList<>();
            List<String> topics = new ArrayList<>();
            List<String> lemmas = new ArrayList<>();

            // Extract relevant nodes from XML
            NodeList sentenceNodes = xmlDoc.getElementsByTagName("type7:Sentence");
            NodeList tokenNodes = xmlDoc.getElementsByTagName("type7:Token");
            NodeList posNodes = xmlDoc.getElementsByTagName("pos:POS");
            NodeList lemmaNodes = xmlDoc.getElementsByTagName("type7:Lemma");
            NodeList dependencyNodes = xmlDoc.getElementsByTagName("dependency:Dependency");
            NodeList namedEntityNodes = xmlDoc.getElementsByTagName("type5:NamedEntity");
            NodeList sentimentNodes = xmlDoc.getElementsByTagName("type18:GerVaderSentiment");
            NodeList topicNodes = xmlDoc.getElementsByTagName("category:CategoryCoveredTagged");

            // Extract specific annotations
            Map<String, String> tokenMap = extractTokens(xmlDoc, tokenNodes, tokens);
            extractPosTags(xmlDoc, posNodes, tokenMap, posTags);
            extractLemmas(lemmaNodes, lemmas);
            extractDependencies(dependencyNodes, tokenMap, dependencies);
            extractNamedEntities(xmlDoc, namedEntityNodes, namedEntities);
            extractTopics(xmlDoc, topicNodes, topics);
            extractSentencesWithSentiment(xmlDoc, sentenceNodes, sentimentNodes, sentences);

            // Return parsed data as MongoDB Document
            return new Document("speechId", speechId)
                    .append("dependencies", dependencies)
                    .append("lemmas", lemmas)
                    .append("namedEntities", namedEntities)
                    .append("posTags", posTags)
                    .append("sentences", sentences)
                    .append("tokens", tokens)
                    .append("topics", topics);

        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("Error parsing XMI file: " + e.getMessage());
        }
    }

    /**
     * Extracts tokens from the XML document and maps them to their respective positions.
     *
     * @param xmlDoc The XML document containing the token data.
     * @param tokenNodes The node list of token elements in the XML.
     * @param tokens The list to store the extracted token texts.
     * @return A map that associates token XMI IDs with their corresponding text values.
     */
    private static Map<String, String> extractTokens(org.w3c.dom.Document xmlDoc, NodeList tokenNodes, List<String> tokens) {
        Map<String, String> tokenMap = new HashMap<>();
        for (int i = 0; i < tokenNodes.getLength(); i++) {
            Element element = (Element) tokenNodes.item(i);
            String xmiId = element.getAttribute("xmi:id");
            String sofaId = element.getAttribute("sofa");
            int begin = Integer.parseInt(element.getAttribute("begin"));
            int end = Integer.parseInt(element.getAttribute("end"));
            String tokenText = getTextFromSofa(xmlDoc, sofaId, begin, end);
            tokens.add(tokenText);
            tokenMap.put(xmiId, tokenText);
        }
        return tokenMap;
    }

    /**
     * Extracts part-of-speech (POS) tags from the XML document.
     *
     * @param xmlDoc The XML document containing POS tag data.
     * @param posNodes The node list of POS elements in the XML.
     * @param tokenMap A map that associates token XMI IDs with their corresponding text values.
     * @param posTags The list to store the extracted POS tags.
     */
    private static void extractPosTags(org.w3c.dom.Document xmlDoc, NodeList posNodes, Map<String, String> tokenMap, List<String> posTags) {
        for (int i = 0; i < posNodes.getLength(); i++) {
            Element element = (Element) posNodes.item(i);
            String sofaId = element.getAttribute("sofa");
            int begin = Integer.parseInt(element.getAttribute("begin"));
            int end = Integer.parseInt(element.getAttribute("end"));
            String posValue = element.getAttribute("PosValue");
            String tokenText = getTextFromSofa(xmlDoc, sofaId, begin, end);
            if (tokenText != null) {
                posTags.add(tokenText + " [" + posValue + "]");
            }
        }
    }

    /**
     * Extracts lemma values from the XML document.
     *
     * @param lemmaNodes The node list of lemma elements in the XML.
     * @param lemmas The list to store the extracted lemmas.
     */
    private static void extractLemmas(NodeList lemmaNodes, List<String> lemmas) {
        for (int i = 0; i < lemmaNodes.getLength(); i++) {
            Element element = (Element) lemmaNodes.item(i);
            lemmas.add(element.getAttribute("value"));
        }
    }

    /**
     * Extracts dependency relations from the XML document.
     *
     * @param dependencyNodes The node list of dependency elements in the XML.
     * @param tokenMap A map that associates token XMI IDs with their corresponding text values.
     * @param dependencies The list to store the extracted dependency relations.
     */
    private static void extractDependencies(NodeList dependencyNodes, Map<String, String> tokenMap, List<String> dependencies) {
        for (int i = 0; i < dependencyNodes.getLength(); i++) {
            Element element = (Element) dependencyNodes.item(i);
            String dependencyType = element.getAttribute("DependencyType");
            String governorId = element.getAttribute("Governor");
            String dependentId = element.getAttribute("Dependent");
            String governor = tokenMap.get(governorId);
            String dependent = tokenMap.get(dependentId);
            dependencies.add("Relation: " + dependencyType + " - " + dependent + " -> " + governor);
        }
    }

    /**
     * Extracts named entities from the XML document.
     *
     * @param xmlDoc The XML document containing named entity data.
     * @param namedEntityNodes The node list of named entity elements in the XML.
     * @param namedEntities The list to store the extracted named entities.
     */
    private static void extractNamedEntities(org.w3c.dom.Document xmlDoc, NodeList namedEntityNodes, List<String> namedEntities) {
        for (int i = 0; i < namedEntityNodes.getLength(); i++) {
            Element element = (Element) namedEntityNodes.item(i);
            String sofaId = element.getAttribute("sofa");
            int begin = Integer.parseInt(element.getAttribute("begin"));
            int end = Integer.parseInt(element.getAttribute("end"));
            String entityType = element.getAttribute("value");
            String entityText = getTextFromSofa(xmlDoc, sofaId, begin, end);
            namedEntities.add(entityText + " (" + entityType + ")");
        }
    }

    /**
     * Extracts topics from the XML document.
     *
     * @param xmlDoc The XML document containing topic data.
     * @param topicNodes The node list of topic elements in the XML.
     * @param sentencesWithTopics The list to store sentences with associated topics.
     */
    private static void extractTopics(org.w3c.dom.Document xmlDoc, NodeList topicNodes, List<String> sentencesWithTopics) {
        String previousSofaId = "";
        int previousBegin = -1;
        int previousEnd = -1;
        List<String> currentTopics = new ArrayList<>();

        for (int i = 0; i < topicNodes.getLength(); i++) {
            Element element = (Element) topicNodes.item(i);
            String sofaId = element.getAttribute("sofa");
            int begin = Integer.parseInt(element.getAttribute("begin"));
            int end = Integer.parseInt(element.getAttribute("end"));
            String topicValue = element.getAttribute("value");
            String topicScore = element.getAttribute("score");
            String sentenceText = getTextFromSofa(xmlDoc, sofaId, begin, end);

            if (sentenceText.trim().isEmpty()) {
                continue; // Skip empty sentences
            }

            if (!sofaId.equals(previousSofaId) || begin != previousBegin || end != previousEnd) {
                if (!currentTopics.isEmpty()) {
                    sentencesWithTopics.add("Sentence: " + getTextFromSofa(xmlDoc, previousSofaId, previousBegin, previousEnd) + " -> Topics: " + currentTopics.toString());
                }
                currentTopics = new ArrayList<>();
                currentTopics.add("[" + topicValue + ", " + topicScore + "]");
            } else {
                currentTopics.add("[" + topicValue + ", " + topicScore + "]");
            }

            previousSofaId = sofaId;
            previousBegin = begin;
            previousEnd = end;
        }

        if (!currentTopics.isEmpty()) {
            sentencesWithTopics.add("Sentence: " + getTextFromSofa(xmlDoc, previousSofaId, previousBegin, previousEnd) + " -> Topics: " + currentTopics.toString());
        }
    }

    /**
     * Extracts sentences along with their associated sentiment values.
     *
     * @param xmlDoc The XML document containing sentence data.
     * @param sentenceNodes The node list of sentence elements in the XML.
     * @param sentimentNodes The node list of sentiment elements in the XML.
     * @param sentences The list to store the sentences with sentiment information.
     */
    private static void extractSentencesWithSentiment(org.w3c.dom.Document xmlDoc, NodeList sentenceNodes, NodeList sentimentNodes, List<String> sentences) {
        for (int i = 0; i < sentenceNodes.getLength(); i++) {
            Element sentenceElement = (Element) sentenceNodes.item(i);
            String sofaId = sentenceElement.getAttribute("sofa");
            int begin = Integer.parseInt(sentenceElement.getAttribute("begin"));
            int end = Integer.parseInt(sentenceElement.getAttribute("end"));
            String sentenceText = getTextFromSofa(xmlDoc, sofaId, begin, end);

            if (sentenceText.trim().isEmpty()) {
                continue; // Skip empty sentences
            }

            String sentimentValue = null;
            for (int j = 0; j < sentimentNodes.getLength(); j++) {
                Element sentimentElement = (Element) sentimentNodes.item(j);
                String sentimentSofaId = sentimentElement.getAttribute("sofa");
                int sentimentBegin = Integer.parseInt(sentimentElement.getAttribute("begin"));
                int sentimentEnd = Integer.parseInt(sentimentElement.getAttribute("end"));

                if (sofaId.equals(sentimentSofaId) && begin == sentimentBegin && end == sentimentEnd) {
                    sentimentValue = sentimentElement.getAttribute("sentiment");
                    break;
                }
            }

            if (sentimentValue != null) {
                sentences.add("Satz: " + sentenceText + " -> Sentiment: " + sentimentValue + "\n");
            }
        }
    }

    /**
     * Retrieves a substring from the text of a "sofa" element in the XML document.
     *
     * @param xmlDoc The XML document containing the sofa elements.
     * @param sofaId The ID of the sofa element.
     * @param begin The beginning index of the substring.
     * @param end The ending index of the substring.
     * @return The extracted text or an error message if the indices are invalid.
     */
    private static String getTextFromSofa(org.w3c.dom.Document xmlDoc, String sofaId, int begin, int end) {
        NodeList sofaNodes = xmlDoc.getElementsByTagName("cas:Sofa");

        for (int i = 0; i < sofaNodes.getLength(); i++) {
            Element element = (Element) sofaNodes.item(i);

            if (element.getAttribute("xmi:id").equals(sofaId)) {
                String sofaString = element.getAttribute("sofaString");

                if (sofaString != null && begin >= 0 && end <= sofaString.length() && begin < end) {
                    return sofaString.substring(begin, end);
                } else {
                    return "Invalid begin/end indices for sofaString.";
                }
            }
        }
        return "Sofa ID not found.";
    }

}









