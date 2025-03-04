package Bundestag.Factory.Helper;

import Bundestag.Fractions.Int.FractionInt;
import Bundestag.Persons.Impl.Member_File_Impl;
import Bundestag.Persons.Impl.Speaker_File_Impl;
import Bundestag.Persons.Int.MemberInt;
import Bundestag.Persons.Int.SpeakerInt;
import Bundestag.Session.Impl.Agenda_File_Impl;
import Bundestag.Session.Impl.Comment_File_Impl;
import Bundestag.Session.Impl.Session_File_Impl;
import Bundestag.Session.Impl.Speech_File_Impl;
import Bundestag.Session.Int.AgendaInt;
import Bundestag.Session.Int.CommentInt;
import Bundestag.Session.Int.SessionInt;
import Bundestag.Session.Int.SpeechInt;
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

import static Bundestag.Factory.Helper.TagGetterUtil.getTextValueOfTagFromDocument;
import static Bundestag.Factory.Helper.TagGetterUtil.getTextValueOfTagFromElement;

public class SessionReader {

    //To access the path from filepaths.properties
    Properties properties;
    //To store and for later usage of every session-xml.
    List<Document> sessionXMLs;

    /**
     * Contains methods for reading the session-xml-files.
     * Also contains the methods to create instances of {@link SpeechInt}, {@link SpeakerInt} and {@link AgendaInt}.
     * Used by {@link Bundestag.Factory.Impl.BundestagFactory}.
     */
    public SessionReader() {
        properties = new Properties();
    }

    /**
     * Reads the xml-files and returns it as a List of Documents.
     * list of documents with class Document --> Needed for parsing.
     */
    public void readSessionXMLs() {
        try (InputStream inputStream = new FileInputStream("filepaths.properties")) {
            properties.load(inputStream);
            String folderPath = properties.getProperty("sessions");
            List<Document> sessionXMLs = new ArrayList<>();
            File folder = new File(folderPath);

            for (File file : Objects.requireNonNull(folder.listFiles())) {
                try {
                    if(file.isFile() && file.getName().endsWith(".xml")) {
                        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                        Document doc = dBuilder.parse(file);
                        doc.getDocumentElement().normalize();
                        sessionXMLs.add(doc);
                    }
                } catch (ParserConfigurationException | IOException | SAXException e) {
                    System.err.println("Error while reading file: " + file.getName() + " Errormessage: " +e.getMessage());
                }
            }
            this.sessionXMLs = sessionXMLs;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Creates all sessions of class {@link SessionInt} and stores them within a map.
     * @param sessionMap To store the sessions. Given by {@link Bundestag.Factory.Impl.BundestagFactory}
     */
    public void createSessions(Map<Integer, SessionInt> sessionMap) {
        for (Document document : sessionXMLs) {
            SessionInt session = mapSessionAttributes(document);
            if (session != null) {
                sessionMap.put(session.getId(), session);
            }
        }
    }

    /**
     * Maps the attributes of a session to the class {@link SessionInt}.
     * @param document The session-xml-file as Document.
     * @return instance of class {@link Session_File_Impl} for given xml-file.
     */
    private Session_File_Impl mapSessionAttributes(Document document) {
        try {
            NodeList sessionTitle = document.getElementsByTagName("sitzungstitel");
            Node sessionTitleNode = sessionTitle.item(0);
            Element sessionTitleElement = (Element) sessionTitleNode;
            int id = Integer.parseInt(Objects.requireNonNull(getTextValueOfTagFromElement(sessionTitleElement, "sitzungsnr")).trim());
            String title = Objects.requireNonNull(getTextValueOfTagFromDocument(document, "sitzungstitel")).trim();
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

            NodeList dateList = document.getElementsByTagName("datum");
            Element dateElement = (Element) dateList.item(0);
            String date = dateElement.getAttribute("date");

            LocalDate sessionDate = LocalDate.parse(date, dateTimeFormatter);

            return new Session_File_Impl(id, title, sessionDate);
        } catch (Exception e) {
            System.err.println("Problem occured while creating session: " + e.getMessage());
            return null;
        }
    }

    /**
     * Creates all sessions of class {@link AgendaInt} and stores them within a list.
     * @param sessionMap To put the created agenda instance into the specific session instance and vice versa.
     * @param agendaList To store the agendas. Given by {@link Bundestag.Factory.Impl.BundestagFactory}
     */
    public void createAgenda(Map<Integer, SessionInt> sessionMap, List<AgendaInt> agendaList) {
        for (Document document : sessionXMLs) {
            NodeList agendaNodeList = document.getElementsByTagName("tagesordnungspunkt");

            NodeList sessionTitle = document.getElementsByTagName("sitzungstitel");
            Node sessionTitleNode = sessionTitle.item(0);
            Element sessionTitleElement = (Element) sessionTitleNode;

            try {
                int sessionId = Integer.parseInt(Objects.requireNonNull(getTextValueOfTagFromElement(sessionTitleElement, "sitzungsnr")).trim());

                for (int i = 0; i < agendaNodeList.getLength(); i++) {
                    Node agendaNode = agendaNodeList.item(i);
                    if (agendaNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element agendaElement = (Element) agendaNode;
                        Agenda_File_Impl agenda = mapAgendaAttributes(agendaElement);
                        if (agenda != null) {
                            agenda.setSession(sessionMap.get(sessionId));
                            sessionMap.get(sessionId).addAgenda(agenda);
                        }
                        agendaList.add(agenda);
                    }
                }
            } catch (NullPointerException | NumberFormatException e) {
                System.err.println("Problem occured while creating agenda: " + e.getMessage());
            }
        }
    }

    /**
     * Maps the attributes of an agenda to the class {@link AgendaInt}.
     * @param agendaElement The specific Element for the needed agenda.
     * @return instance of {@link Agenda_File_Impl} with all attributes mapped.
     */
    private Agenda_File_Impl mapAgendaAttributes(Element agendaElement) {
        StringBuilder titleBuilder = new StringBuilder();
        try {
            String id = agendaElement.getAttribute("top-id");
            for (int i = 0; i < agendaElement.getChildNodes().getLength(); i++) {
                if (agendaElement.getChildNodes().item(i).getNodeType() == Node.ELEMENT_NODE) {
                    Element child = (Element) agendaElement.getChildNodes().item(i);
                    if("p".equals(child.getTagName()) && "T_NaS".equals(child.getAttribute("klasse"))) {
                        titleBuilder.append(child.getTextContent().trim()).append(" ");
                    }
                }
            }
            String title = titleBuilder.toString().trim();
            if(title.isEmpty()) {
                title = "Kein Titel";
            }
            return new Agenda_File_Impl(id, title);
        } catch (Exception e) {
            System.err.println("Problem occured while creating member: " + e.getMessage());
            return null;
        }
    }

    /**
     * Creates instances of both {@link SpeechInt} and {@link SpeakerInt}.
     * @param speechList To store all speech instances.
     * @param memberMap To map member attributes to speaker attributes.
     * @param speakerMap To store all member instances.
     * @param sessionMap To map session attributes to speech attributes.
     * @param agendaList To map agenda attributes to speech attributes and vice versa.
     * @param fractionMap To map fraction attributes to speaker attributes and vice versa.
     */
    public void createSpeechesAndSpeakers(List<SpeechInt> speechList, Map<String, MemberInt> memberMap, Map<String, SpeakerInt> speakerMap, Map<Integer, SessionInt> sessionMap, List<AgendaInt> agendaList, Map<String, FractionInt> fractionMap, List<CommentInt> commentsList) {
        for (Document document : sessionXMLs) {
            SessionInt speechSession = getSpeechSession(sessionMap, document);

            NodeList speechAgendaList = document.getElementsByTagName("tagesordnungspunkt");

            for (int i = 0; i < speechAgendaList.getLength(); i++) {
                Node speechAgendaNode = speechAgendaList.item(i);
                if (speechAgendaNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element speechAgendaElement = (Element) speechAgendaNode;
                    String speechAgendaId = speechAgendaElement.getAttribute("top-id");
                    AgendaInt speechAgenda = getSpeechAgenda(agendaList, speechAgendaId, speechSession);

                    NodeList speechNodeList = speechAgendaElement.getElementsByTagName("rede");

                    for (int j = 0; j < speechNodeList.getLength(); j++) {
                        Node speechNode = speechNodeList.item(j);
                        if (speechNode.getNodeType() == Node.ELEMENT_NODE) {
                            Element speechElement = (Element) speechNode;

                            SpeakerInt speaker = getSpeakerOfSpeech(speechElement, memberMap, fractionMap, speakerMap);

                            String speechId = speechElement.getAttribute("id");
                            StringBuilder speechContent = new StringBuilder();
                            List<CommentInt> speechCommentList = new ArrayList<>();
                            NodeList speechElementChilds = speechElement.getChildNodes();
                            for (int k = 0; k < speechElementChilds.getLength(); k++) {
                                if (speechElementChilds.item(k).getNodeType() == Node.ELEMENT_NODE) {
                                    Element speechElementChild = (Element) speechElementChilds.item(k);

                                    if (speechElementChild.getTagName().equals("p") && (speechElementChild.getAttribute("klasse").startsWith("J") || speechElementChild.getAttribute("klasse").startsWith("O"))) {
                                        speechContent.append(speechElementChild.getTextContent().trim()).append(" ");
                                    }

                                    else if (speechElementChild.getTagName().equals("kommentar")) {
                                        String commentContent = speechElementChild.getTextContent().trim();
                                        int index =  speechContent.length();
                                        String commentId = speechId + "-" + index;
                                        CommentInt comment = new Comment_File_Impl(commentId, index, commentContent, speechId);
                                        speechCommentList.add(comment);
                                        commentsList.add(comment);
                                    }
                                }
                            }
                            Speech_File_Impl speech = new Speech_File_Impl(speechId, speaker, speechContent.toString().trim(), speechCommentList, speechSession, speechAgenda);
                            speaker.addSpeech(speech);
                            if (!speakerMap.containsKey(speaker.getId())) {
                                speakerMap.put(speaker.getId(), speaker);
                            }
                            if (speechAgenda != null) {
                                speechAgenda.addSpeech(speech);
                            }
                            speechList.add(speech);
                        }
                    }
                }
            }
        }
    }

    /**
     * Gets specific session instance of the speech.
     * @param sessionMap To get the specific stored session instance.
     * @param document To get attributes of the document in which the speech is in.
     * @return instance of {@link SessionInt} for specific speech.
     */
    private SessionInt getSpeechSession(Map<Integer, SessionInt> sessionMap, Document document) {
        NodeList sessionTitle = document.getElementsByTagName("sitzungstitel");
        Node sessionTitleNode = sessionTitle.item(0);
        Element sessionTitleElement = (Element) sessionTitleNode;
        int sessionId = Integer.parseInt(Objects.requireNonNull(getTextValueOfTagFromElement(sessionTitleElement, "sitzungsnr")).trim());
        SessionInt speechSession;
        speechSession = sessionMap.get(sessionId);
        return speechSession;
    }

    /**
     * Gets specific session instance of the speech.
     * @param agendaList To get the specific agenda instance in which the speech is.
     * @param speechAgendaId ID of agenda from session-xml-file for matching purposes.
     * @param speechSession Session instance for this specific speech.
     * @return instance of {@link AgendaInt} for specific speech.
     */
    private AgendaInt getSpeechAgenda(List<AgendaInt> agendaList, String speechAgendaId, SessionInt speechSession) {
        for (AgendaInt agenda : agendaList) {
            if (agenda.getId().equals(speechAgendaId) && agenda.getSession().getId() == speechSession.getId()) {
                return agenda;
            }
        }
        return null;
    }

    /**
     * Gets speaker attributes of the specific speech and also creates the instance of class {@link SpeakerInt} for it.
     * @param speechElement Element for speech from session-xml-file.
     * @param memberMap To map member attributes to speaker attributes as speaker inherits from member.
     * @param fractionMap To put speakers who are not in the memberMap into the fraction "Plos" as we don't have the info.
     * @param speakerMap To store the created {@link SpeakerInt} instances.
     * @return instance of class {@link SpeakerInt} for specific speech.
     */
    private SpeakerInt getSpeakerOfSpeech(Element speechElement, Map<String, MemberInt> memberMap, Map<String, FractionInt> fractionMap, Map<String, SpeakerInt> speakerMap) {
        Element speakerElement = (Element) speechElement.getElementsByTagName("redner").item(0);
        String speakerId = speakerElement.getAttribute("id");
        if (speakerMap.containsKey(speakerId)) {
            return speakerMap.get(speakerId);
        }
        if (!memberMap.containsKey(speakerId)) {
            String name = speakerElement.getElementsByTagName("vorname").item(0).getTextContent().trim();
            String surname = speakerElement.getElementsByTagName("nachname").item(0).getTextContent().trim();
            String fraction;
            if (speakerElement.getElementsByTagName("fraktion").item(0) != null) {
                fraction = speakerElement.getElementsByTagName("fraktion").item(0).getTextContent().trim();
                if (fraction.contains("LINKE") && !fraction.endsWith(".")){fraction = fraction + ".";}
            } else {
                fraction = "SSW";
                Member_File_Impl speakerMember = new Member_File_Impl(speakerId, name, surname, 0, null, null, null, null, fractionMap.get(fraction));
                fractionMap.get(fraction).addMember(speakerMember);
                memberMap.put(speakerId, speakerMember);
            }
            return new Speaker_File_Impl(speakerId, name, surname, 0, null, null, null, null, fractionMap.get(fraction));
        }
        MemberInt member = memberMap.get(speakerId);
        return new Speaker_File_Impl(member.getId(), member.getName(), member.getSurname(), member.getAge(), member.getGender(), member.getAcademicTitle(), member.getBirthDate(), member.getProfession(), member.getFraction());
    }
}
