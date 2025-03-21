package Bundestag.Session.Int;

import org.bson.Document;

import java.util.List;

/**
 * Interface for agenda. Stores all attributes of agenda.
 * @author Muhammed
 */
public interface AgendaInt {

    /**
     * @return id of agenda
     */
    String getId();

    /**
     * @return title of agenda
     */
    String getTitle();

    /**
     * @return id of agenda and specific session
     */
    String getAgendaId();

    /**
     * @return session of agenda
     */
    SessionInt getSession();

    /**
     * @return the list of speeches of agenda
     */
    List<SpeechInt> getSpeeches();

    /**
     * Create Document with key-value pairs that match the attributes of agenda.
     * @return created document with the attributes in the specific fields.
     */
    Document toDocument();

    /**
     * Add instance of {@link SpeechInt} to the list of speeches.
     * @param speech instance of {@link SpeechInt} which should be added to the list.
     */
    void addSpeech(SpeechInt speech);

    String toTex();

    String toTexIndex();
}
