package Bundestag.Persons.Int;

import Bundestag.Session.Impl.Speech_File_Impl;
import Bundestag.Session.Int.SpeechInt;
import Database.MongoDB_Impl.Speech_MongoDB_Impl;
import org.bson.Document;

import java.util.List;

/**
 * Interface for speaker. Inherits from {@link MemberInt}. Stores all attributes of speaker.
 */
public interface SpeakerInt extends MemberInt{

    /**
     * @return all speeches from speaker as a list of {@link Speech_File_Impl} instances.
     */
    List<SpeechInt> getSpeech();

    /**
     * Create Document with key-value pairs that match the attributes of speaker.
     * @return created document with the attributes in the specific fields.
     */
    Document toDocument();

    String getBirthDateString();

    int getSpeechAmount();

    void addSpeech(Speech_File_Impl speech);

    List<Speech_MongoDB_Impl> getSortedSpeeches();
}
