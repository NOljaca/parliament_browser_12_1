package Bundestag.Factory.Helper;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class TagGetterUtil {

    /**
     * Util class for getting text-values of specific tags.
     */
    public TagGetterUtil() {}

    /**
     * Gets the text-value for given Element-tag.
     * @param element Element in which the given tag is.
     * @param tag Needed tag for the text-value.
     * @return text-value of given tag.
     */
    public static String getTextValueOfTagFromElement(Element element, String tag) {
        NodeList nodeList = element.getElementsByTagName(tag);

        if(nodeList.getLength() > 0) {
            return nodeList.item(0).getTextContent();
        }

        return null;
    }

    /**
     * Gets the text-value for given Document-tag.
     * @param document Document in which the given tag is.
     * @param tag Needed tag for the text-value.
     * @return text-value of given tag.
     */
    public static String getTextValueOfTagFromDocument(Document document, String tag) {
        NodeList nodeList = document.getElementsByTagName(tag);

        if(nodeList.getLength() > 0) {
            return nodeList.item(0).getTextContent();
        }

        return null;
    }
}
