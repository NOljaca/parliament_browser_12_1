package Bundestag.Factory.Impl;

import Bundestag.Factory.Helper.MdBStammdatenReader;
import Bundestag.Factory.Helper.SessionReader;
import Bundestag.Factory.Helper.XMLScraper;
import Bundestag.Factory.Int.BundestagFactoryInt;
import Bundestag.Fractions.Int.FractionInt;
import Bundestag.Persons.Int.MemberInt;
import Bundestag.Persons.Int.SpeakerInt;
import Bundestag.Session.Int.AgendaInt;
import Bundestag.Session.Int.CommentInt;
import Bundestag.Session.Int.SessionInt;
import Bundestag.Session.Int.SpeechInt;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of {@link BundestagFactoryInt} that reads xml-files, creates instances of classes
 * and creates HTML-files.
 *
 * @author Muhammed
 */
public class BundestagFactory implements BundestagFactoryInt {

    private MdBStammdatenReader mdBStammdatenReader; // To read the stammdaten-files
    private SessionReader sessionReader; // To read the session-files
    private Map<String, MemberInt> memberMap;  // To store the member-instances
    private Map<String, SpeakerInt> speakerMap; // To store the speaker-instances
    private Map<String, FractionInt> fractionMap; // To store the fraction-instances
    private List<SpeechInt> speechList; // To store the speech-instances
    private List<AgendaInt> agendaList; // To store the agenda-instances
    private Map<Integer, SessionInt> sessionMap; // To store the session-instances
    private List<CommentInt> commentList;

    /**
     * Class for handling the execution of all actions in the program (Reading xmls, mapping to classes, creating HTMLs).
     *
     * @author Muhammed
     */
    public BundestagFactory() {
        this.mdBStammdatenReader = new MdBStammdatenReader();
        this.sessionReader = new SessionReader();
        this.fractionMap = new HashMap<>();
        this.speechList = new ArrayList<>();
        this.agendaList = new ArrayList<>();
        this.memberMap = new HashMap<>();
        this.speakerMap = new HashMap<>();
        this.sessionMap = new HashMap<>();
        this.commentList = new ArrayList<>();
    }

    public List<CommentInt> getCommentList() {
        return commentList;
    }

    @Override
    public Map<String, SpeakerInt> getSpeakerMap() {
        return speakerMap;
    }

    @Override
    public MdBStammdatenReader getMdBStammdatenReader() {
        return mdBStammdatenReader;
    }

    @Override
    public SessionReader getSessionReader() {
        return sessionReader;
    }

    @Override
    public Map<String, MemberInt> getMemberMap() {
        return memberMap;
    }

    @Override
    public Map<String, FractionInt> getFractionMap() {
        return fractionMap;
    }

    @Override
    public List<SpeechInt> getSpeechList() {
        return speechList;
    }

    @Override
    public List<AgendaInt> getAgendaList() {
        return agendaList;
    }

    @Override
    public Map<Integer, SessionInt> getSessionMap() {
        return sessionMap;
    }

    /**
     * Starts the scraping, parses protocol-documents.
     * @throws FileNotFoundException
     * @author Muhammed
     */
    @Override
    public void createBundestag() throws FileNotFoundException {
        XMLScraper.downloadAndExtractStammdatenZip();
        XMLScraper.scrapeAndDownload();
        System.out.println("reading sessions");
        sessionReader.readSessionXMLs();
        System.out.println("read sessions");
        mdBStammdatenReader.createMembersAndFractions(memberMap, fractionMap);
        System.out.println("stammdaten read");
        sessionReader.createSessions(sessionMap);
        sessionReader.createAgenda(sessionMap, agendaList);
        sessionReader.createSpeechesAndSpeakers(speechList, memberMap, speakerMap, sessionMap, agendaList, fractionMap, commentList);
        XMLScraper.scrapeXMLsPeriodically();
    }
}