package Bundestag.Persons.Impl;

import Bundestag.Fractions.Int.FractionInt;
import Bundestag.Persons.Int.SpeakerInt;
import Bundestag.Session.Impl.Speech_File_Impl;
import Bundestag.Session.Int.SpeechInt;
import org.bson.Document;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of {@link SpeakerInt}. Inherits from {@link Bundestag.Persons.Int.MemberInt}. Stores all attributes of speaker from xml-file.
 */
public class Speaker_File_Impl extends Member_File_Impl implements SpeakerInt {

    private List<SpeechInt> speeches; // All speeches of speaker

    /**
     * Class for storing all attributes (from xml-file) of a speaker.
     * @param id id of speaker
     * @param name name of speaker
     * @param surname surname of speaker
     * @param age age of speaker
     * @param gender gender of speaker
     * @param academicTitle academic title of speaker
     * @param birthDate birthdate of speaker
     * @param profession profession of speaker
     * @param fraction fraction of speaker
     */
    public Speaker_File_Impl(String id, String name, String surname, int age, String gender, String academicTitle, LocalDate birthDate, String profession, FractionInt fraction) {
        super(id, name, surname, age, gender, academicTitle, birthDate, profession, fraction);
        this.speeches = new ArrayList<>();
    }

    @Override
    public List<SpeechInt> getSpeech() {
        return speeches;
    }

    @Override
    public Document toDocument() {
        throw new UnsupportedOperationException("File-Class does not support Document operations!");
    }

    public void setSpeeches(List<SpeechInt> speeches) {
        this.speeches = speeches;
    }

    /**
     * Adds given instance of {@link Speech_File_Impl} to the list of speeches.
     * @param speech instance of {@link Speech_File_Impl} which should be added to the list.
     */
    public void addSpeech(Speech_File_Impl speech) {
        this.speeches.add(speech);
    }
}
