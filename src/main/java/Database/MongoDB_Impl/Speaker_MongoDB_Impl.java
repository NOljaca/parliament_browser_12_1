package Database.MongoDB_Impl;

import Bundestag.Fractions.Impl.Fraction_File_Impl;
import Bundestag.Fractions.Int.FractionInt;
import Bundestag.Persons.Int.SpeakerInt;
import Bundestag.Session.Impl.Speech_File_Impl;
import Bundestag.Session.Int.SpeechInt;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

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
    }

    public Speaker_MongoDB_Impl(MongoDatabase database, Document speakerDoc) {
        this.speakerDoc = speakerDoc;
        this.database = database;
        id = speakerDoc.getString("id");
    }

    public String getIdDocument() {return speakerDoc.getString("id");}
    public String getNameDocument() {return speakerDoc.getString("name");}
    public String getNachnameDocument() {return speakerDoc.getString("surname");}
    public String getFraktionDocument() {return speakerDoc.getString("fraction");}
    public List<String> getRedeIdsDocument() {return speakerDoc.getList("speechIds", String.class);}
    public int getRedenSizeDocument() {
        try {
            return speakerDoc.getInteger("speeches");
        } catch (Exception e) {
            return speakerDoc.getList("speeches", String.class).size();
        }

    }
    public String getBildUrlDocument() {return speakerDoc.getString("bildUrl");}

    /**
     * This method retrieves a list of RedeIDs sorted by date
     * @return a sorted by date RedeIDs
     */

    public List<Map.Entry<String, LocalDate>> getSpeechIds() {
        List<String> speechIds;
        if (collection == null) {
            speechIds = getRedeIdsDocument();
        } else {
            speechIds = collection.find(filter).first().getList("speeches", String.class);
        }
        Map<String, LocalDate> sortedRedeIdsMap = new HashMap<>();
        for (String speechId : speechIds) {
            String agendaId = database.getCollection("speeches").find(new Document("id", speechId)).first().getString("agenda");
            String sessionDate = database.getCollection("agendas").find(new Document("id", agendaId)).first().get("session", Document.class).getString("date");
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            LocalDate sitzungsdatumDate = LocalDate.parse(sessionDate, dtf);
            sortedRedeIdsMap.put(speechId, sitzungsdatumDate);
        }

        return sortedRedeIdsMap.entrySet().stream().sorted(Map.Entry.comparingByValue()).toList();
    }

    public String getBildUrl() {
        return collection.find(filter).first().getString("bildUrl");
    }

    public int getRedeIdsSize() {
        return getSpeechIds().size();
    }

    @Override
    public List<SpeechInt> getSpeech() {
        return List.of();
    }

    @Override
    public String getId() {
        return "";
    }

    @Override
    public String getName() {
        return "";
    }

    @Override
    public String getSurname() {
        return "";
    }

    @Override
    public int getAge() {
        return 0;
    }

    @Override
    public String getAcademicTitle() {
        return "";
    }

    @Override
    public LocalDate getBirthDate() {
        return null;
    }

    @Override
    public String getGender() {
        return "";
    }

    @Override
    public String getProfession() {
        return "";
    }

    @Override
    public FractionInt getFraction() {
        return null;
    }

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

    @Override
    public String toHTML() {
        return "";
    }

    @Override
    public void addSpeech(Speech_File_Impl speech) {

    }
}
