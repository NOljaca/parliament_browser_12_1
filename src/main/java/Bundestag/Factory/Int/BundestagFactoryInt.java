package Bundestag.Factory.Int;

import Bundestag.Factory.Helper.MdBStammdatenReader;
import Bundestag.Factory.Helper.SessionReader;
import Bundestag.Fractions.Int.FractionInt;
import Bundestag.Persons.Int.MemberInt;
import Bundestag.Persons.Int.SpeakerInt;
import Bundestag.Session.Int.AgendaInt;
import Bundestag.Session.Int.SessionInt;
import Bundestag.Session.Int.SpeechInt;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;

/**
 * Factory interface - All the actions in the program are executed from here (Reading xmls, mapping to classes, creating HTMLs)
 */
public interface BundestagFactoryInt {

    /**
     * @return map with keys of id's from speaker and values of instances of {@link SpeakerInt}
     */
    Map<String, SpeakerInt> getSpeakerMap();

    /**
     * @return instance of {@link MdBStammdatenReader}
     */
    MdBStammdatenReader getMdBStammdatenReader();

    /**
     * @return instance of {@link SessionReader}
     */
    SessionReader getSessionReader();

    /**
     * @return map with keys of id's from member and values of instances of {@link MemberInt}
     */
    Map<String, MemberInt> getMemberMap();

    /**
     * @return map with keys of short-names from fraction and values of instances of {@link FractionInt}
     */
    Map<String, FractionInt> getFractionMap();

    /**
     * @return list with instances of {@link SpeechInt}
     */
    List<SpeechInt> getSpeechList();

    /**
     * @return list with instances of {@link AgendaInt}
     */
    List<AgendaInt> getAgendaList();

    /**
     * @return map with keys of id's from session and values of instances of {@link SessionInt}
     */
    Map<Integer, SessionInt> getSessionMap();

    /**
     * Reads xmls with the help of {@link MdBStammdatenReader} and {@link SessionReader}.
     * Maps the data to the classes and stores them in the respective list/map.
     * @throws FileNotFoundException if xmls do not exist
     */
    void createBundestag() throws FileNotFoundException;
}
