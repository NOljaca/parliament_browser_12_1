package Database.MongoDB_Impl;

import Bundestag.Fractions.Impl.Fraction_File_Impl;
import Bundestag.Fractions.Int.FractionInt;
import Bundestag.Persons.Int.SpeakerInt;
import Bundestag.Session.Impl.Speech_File_Impl;
import Bundestag.Session.Int.SpeechInt;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of SpeakerInt. Stores speaker-attributes to map for the database.
 * @author Amal
 */
public class Speaker_MongoDB_Impl implements SpeakerInt {
    private SpeakerInt speaker;
    private MongoDatabase database;
    private MongoCollection<Document> collection;
    private String id;
    private Document filter;

    private Document speakerDoc;


    public Speaker_MongoDB_Impl(SpeakerInt speaker) {
        this.speaker = speaker;
    }

    public Speaker_MongoDB_Impl(MongoDatabase database, String id) {
        this.database = database;
        this.id = id;
        collection = database.getCollection("speakers");
        filter = new Document("id", id);
        speakerDoc = collection.find(filter).first();
    }

    public Speaker_MongoDB_Impl(MongoDatabase database, Document speakerDoc) {
        this.speakerDoc = speakerDoc;
        this.database = database;
        id = speakerDoc.getString("id");
    }

    public List<String> getSpeechIdsDocument() {return speakerDoc.getList("speeches", String.class);}

    public int getRedenSizeDocument() {
        try {
            return speakerDoc.getInteger("speeches");
        } catch (Exception e) {
            return speakerDoc.getList("speeches", String.class).size();
        }

    }
    public String getBildUrlDocument() {return speakerDoc.getString("bildUrl");}

    public int getSpeechAmount() {
        return getSpeechIdsDocument().size();
    }

    public String getNameAndSurname() {
        String name = getName();
        String surname = getSurname();
        return name + " " + surname;
    }

    @Override
    public List<SpeechInt> getSpeech() {
        return List.of();
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        if (speakerDoc != null) {
            return speakerDoc.getString("name");
        }
        return collection.find(filter).first().getString("name");
    }

    @Override
    public String getSurname() {
        if (speakerDoc != null) {
            return speakerDoc.getString("surname");
        }
        return collection.find(filter).first().getString("surname");
    }

    @Override
    public int getAge() {
        Integer age;
        if (speakerDoc != null) {
            age = speakerDoc.getInteger("age");
            return age != null ? age : 0;
        }
        age = collection.find(filter).first().getInteger("age");
        return age != null ? age : 0;
    }

    @Override
    public String getAcademicTitle() {
        if (speakerDoc != null) {
            return speakerDoc.getString("academicTitle");
        }
        return collection.find(filter).first().getString("academicTitle");
    }

    @Override
    public LocalDate getBirthDate() {
        return null;
    }

    @JsonIgnore
    public String getBirthDateString() {
        Date date = speakerDoc.getDate("birthDate");
        if (date == null) {return "UNKNOWN";}
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        LocalDateTime localDateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        return localDateTime.format(formatter);
    }

    @Override
    public String getGender() {
        if (speakerDoc != null) {
            return speakerDoc.getString("gender");
        }
        return collection.find(filter).first().getString("gender");
    }

    @Override
    public String getProfession() {
        if (speakerDoc != null) {
            return speakerDoc.getString("profession");
        }
        return collection.find(filter).first().getString("profession");
    }

    @Override
    public FractionInt getFraction() {
        MongoCollection<Document> fractionCollection = database.getCollection("fractions");
        Document fractionFilter = new Document("shortName", speakerDoc.getString("fraction"));
        Document fractionDoc = fractionCollection.find(fractionFilter).first();
        if (fractionDoc == null) {
            fractionDoc = new Document("shortName", "UNKNOWN");
        }
        return new Fraction_MongoDB_Impl(database, fractionDoc);
    }

    /**
     * Creates a document of a speaker-object.
     * @return document mapped with speaker-attributes.
     * @author Amal
     */
    @Override
    public Document toDocument() {
        String birthDate;
        int age;
        FractionInt fraction;
        if (speaker.getBirthDate() == null) {
            birthDate = LocalDate.MAX.toString();
            age = 0;
        } else {
            birthDate = speaker.getBirthDate().toString();
            age = speaker.getAge();
        }
        if (speaker.getFraction() == null) {
            fraction = new Fraction_File_Impl("UNKNOWN", "UNKNOWN", new HashSet<>());
        } else {
            fraction = speaker.getFraction();
        }


        Document speakerDocument = new Document();
        List<String> speechIds = new ArrayList<>();
        for (SpeechInt speech : speaker.getSpeech()) {
            speechIds.add(speech.getId());
        }
        return speakerDocument.append("id", speaker.getId())
                .append("name", speaker.getName())
                .append("surname", speaker.getSurname())
                .append("age", age)
                .append("gender", speaker.getGender())
                .append("academicTitle", speaker.getAcademicTitle())
                .append("birthDate", speaker.getBirthDate())
                .append("profession", speaker.getProfession())
                .append("speeches", speechIds)
                .append("fraction", fraction.getShortName());
    }

    /**
     * Fetches speech-ids sorted by their dates (for the speech-id index in the portfolio)
     * @return speech-ids sorted by dates
     *
     * @author Amal
     */
    public List<Map.Entry<String, LocalDate>> sortSpeechesByDates() {
        List<String> speechIds = getSpeechIdsDocument();
        if (speechIds == null || speechIds.isEmpty()) {
            return Collections.emptyList();
        }
        Map<String, LocalDate> speechIdDates = new HashMap<>();
        for (String speechId : speechIds) {
            Document speechFilter = new Document("id", speechId);
            Document sessionIdFilter = new Document("id", database.getCollection("speeches").find(speechFilter).first().getInteger("session"));
            Date sessionDate = database.getCollection("sessions").find(sessionIdFilter).first().getDate("date");
            LocalDateTime sessionDateTime = sessionDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            speechIdDates.put(speechId, sessionDateTime.toLocalDate());
        }

        return speechIdDates.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toList());
    }

    /**
     * Fetches speeches sorted by their dates. JsonIgnore is because of javascript-incompatibility of nested objects.
     * @return list of speeches sorted by dates
     *
     * @author Amal
     */
    @JsonIgnore
    public List<Speech_MongoDB_Impl> getSortedSpeeches() {
        List<Map.Entry<String, LocalDate>> sortedSpeeches = sortSpeechesByDates();
        List<Speech_MongoDB_Impl> speeches = new ArrayList<>();
        for (Map.Entry<String, LocalDate> speechEntry : sortedSpeeches) {
            String speechId = speechEntry.getKey();
            Document speech = database.getCollection("speeches").find(new Document("id", speechId)).first();
            Speech_MongoDB_Impl speechMongoDB = new Speech_MongoDB_Impl(database, speech);
            speeches.add(speechMongoDB);
        }
        return speeches;
    }


    @Override
    public String toHTML() {
        return "";
    }

    @Override
    public void addSpeech(Speech_File_Impl speech) {
    }

    public String toTex() {
        StringBuilder latex = new StringBuilder();
        latex.append("\\textbf{Redner:}").append(getNameAndSurname()).append("\\\\\\\\");
        latex.append("\\textbf{Alter:}").append(getAge()).append("\\\\\\\\");
        latex.append("\\textbf{Geburtsdatum:}").append(getBirthDateString()).append("\\\\\\\\");
        latex.append("\\textbf{Beruf:}").append(getProfession()).append("\\\\\\\\");
        latex.append("\\textbf{Fraktion:}").append(getFraction().getShortName()).append("\\\\\\\\");
        return latex.toString();
    }

    public String toTexSpeaker() {
        StringBuilder latex = new StringBuilder();
        latex.append("\\textbf{Redner:} ").append(getNameAndSurname()).append("\\\\\\\\");
        latex.append("\\textbf{Alter:} ").append(getAge()).append("\\\\\\\\");
        latex.append("\\textbf{Geburtsdatum:} ").append(getBirthDateString()).append("\\\\\\\\");
        latex.append("\\textbf{Beruf:} ").append(getProfession()).append("\\\\\\\\");
        latex.append("\\textbf{Fraktion:} ").append(getFraction().getShortName()).append("\\\\\\\\");

        for (Speech_MongoDB_Impl speech : getSortedSpeeches()) {
            latex.append(speech.toTex());
        }

        return latex.toString();
    }
}
