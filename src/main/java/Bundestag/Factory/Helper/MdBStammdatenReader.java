package Bundestag.Factory.Helper;

import Bundestag.Fractions.Impl.Fraction_File_Impl;
import Bundestag.Fractions.Int.FractionInt;
import Bundestag.Persons.Impl.Member_File_Impl;
import Bundestag.Persons.Int.MemberInt;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static Bundestag.Factory.Helper.TagGetterUtil.getTextValueOfTagFromElement;

/**
 * Helper-class for reading the speaker-meta-data
 * @author Muhammed
 */
public class MdBStammdatenReader {

    //To access the path from filepaths.properties
    Properties properties;

    /**
     * Contains methods for reading the MDB_STAMMDATEN.XML-file.
     * Also contains the methods to create instances of {@link FractionInt} and {@link MemberInt}.
     * Used by {@link Bundestag.Factory.Impl.BundestagFactory}.
     *
     * @author Muhammed
     */
    public MdBStammdatenReader() {
        properties = new Properties();
    }

    /**
     * Reads the xml-file and returns it as a Document.
     * @return document of class Document --> Needed for parsing.
     *
     * @author Muhammed
     */
    private Document readMdBStammdaten() {
        try (InputStream inputStream = new FileInputStream("filepaths.properties")) {
            properties.load(inputStream);
            String sessionsPath = properties.getProperty("mdbStammdaten");
            File mdbStammdaten = new File(sessionsPath);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document document = dBuilder.parse(mdbStammdaten);
            document.getDocumentElement().normalize();
            return document;

        } catch (ParserConfigurationException | IOException | SAXException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Parses and maps attributes to instances of the classes {@link FractionInt} and {@link MemberInt}.
     * @param memberMap Map with keys of {@link String} and values of {@link MemberInt}.
     *                  Given by {@link Bundestag.Factory.Impl.BundestagFactory} for storing purposes.
     * @param fractionMap Map with keys of {@link String} and values of {@link FractionInt}.
     *                  Given by {@link Bundestag.Factory.Impl.BundestagFactory} for storing purposes.
     *
     * @author Muhammed
     */
    public void createMembersAndFractions(Map<String, MemberInt> memberMap, Map<String, FractionInt> fractionMap){
        Document document = readMdBStammdaten();
        NodeList mdbs = document.getElementsByTagName("MDB");

        for (int i = 0; i < mdbs.getLength(); i++) {
            Node mdb = mdbs.item(i);

            if (mdb.getNodeType() == Node.ELEMENT_NODE) {
                Element mdbElement = (Element) mdb;
                boolean currentLegislaturePeriod = false;
                NodeList legislaturePeriods = mdbElement.getElementsByTagName("WP");

                for (int j = 0; j < legislaturePeriods.getLength(); j++) {
                    if(legislaturePeriods.item(j).getTextContent().equals("20")) {
                        currentLegislaturePeriod = true;
                        break;
                    }
                }

                if(currentLegislaturePeriod) {
                    FractionInt fraction = mapFractionAttributes(mdbElement, fractionMap);
                    Member_File_Impl member = mapMemberAttributes(mdbElement, fraction);
                    if (member != null) {
                        memberMap.put(member.getId(), member);
                    }
                    if(fraction != null) {
                        fraction.addMember(member);
                    }
                }
            }
        }
    }

    /**
     * Helper method for mapping the attributes of the class {@link FractionInt}
     * @param mdbElement Element tag for the specific member from which the fraction-attributes are needed.
     * @param fractionMap Map with keys of {@link String} and values of {@link FractionInt}.
     *                      Given by {@link Bundestag.Factory.Impl.BundestagFactory} for storing purposes.
     * @return FractionInt instance with all the attributes from fraction of member.
     *
     * @author Muhammed
     */
    private FractionInt mapFractionAttributes(Element mdbElement, Map<String, FractionInt> fractionMap) {
        try {
            String shortName = getTextValueOfTagFromElement(mdbElement, "PARTEI_KURZ");
            String longName = !Objects.requireNonNull(getTextValueOfTagFromElement(mdbElement, "INS_LANG")).isEmpty() ? getTextValueOfTagFromElement(mdbElement, "INS_LANG") : "Unbekannt";
            FractionInt fraction = fractionMap.get(shortName);

            if(!fractionMap.containsKey(shortName)) {
                fraction = new Fraction_File_Impl(shortName, longName, new HashSet<>());
                fractionMap.put(shortName, fraction);
            }

            return fraction;

        } catch (Exception e) {
            System.err.println("Problem occured while creating fraction: " + e.getMessage());
            return null;
        }
    }

    /**
     * Helper method for mapping the attributes of the class {@link Member_File_Impl}
     * @param mdbElement Element tag for the specific member from which the member-attributes are needed.
     * @param fraction fraction of member.
     * @return Member_File_Impl instance with all the attributes from member.
     *
     * @author Muhammed
     */
    private Member_File_Impl mapMemberAttributes(Element mdbElement, FractionInt fraction) {
        try {
            String id = getTextValueOfTagFromElement(mdbElement, "ID");
            String name = getTextValueOfTagFromElement(mdbElement, "VORNAME");
            String surname = getTextValueOfTagFromElement(mdbElement, "NACHNAME");
            String gender = getTextValueOfTagFromElement(mdbElement, "GESCHLECHT");
            String academicTitle = getTextValueOfTagFromElement(mdbElement, "AKAD_TITEL");
            String profession = getTextValueOfTagFromElement(mdbElement, "BERUF");

            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            LocalDate birthDate = LocalDate.parse(Objects.requireNonNull(getTextValueOfTagFromElement(mdbElement, "GEBURTSDATUM")), dateTimeFormatter);

            int age = LocalDate.now().getYear() - birthDate.getYear();

            return new Member_File_Impl(id, name, surname, age, gender, academicTitle, birthDate, profession, fraction);

        } catch (Exception e) {
            System.err.println("Problem occured while creating member: " + e.getMessage());
            return null;
        }
    }
}
