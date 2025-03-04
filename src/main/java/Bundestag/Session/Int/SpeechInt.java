package Bundestag.Session.Int;

import Bundestag.Persons.Int.SpeakerInt;
import org.apache.uima.jcas.JCas;
import org.bson.Document;

import java.util.List;

/**
 * Interface for session. Stores all attributes of session.
 */
public interface SpeechInt {

    /**
     * @return id of speech
     */
    String getId();

    /**
     * @return speaker of speech
     */
    SpeakerInt getSpeaker();

    /**
     * @return content of speech
     */
    String getContent();

    /**
     * @return list of comments of speech
     */
    List<CommentInt> getComments();

    /**
     * @return session of speech
     */
    SessionInt getSession();

    /**
     * @return agenda of speech
     */
    AgendaInt getAgenda();

    /**
     * Create Document with key-value pairs that match the attributes of speech.
     * @return created document with the attributes in the specific fields.
     */
    Document toDocument();

    String getSpeakerId();

    JCas toCas();
}
