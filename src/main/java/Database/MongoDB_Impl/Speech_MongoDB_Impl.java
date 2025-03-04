package Database.MongoDB_Impl;

import Bundestag.Persons.Int.SpeakerInt;
import Bundestag.Session.Int.AgendaInt;
import Bundestag.Session.Int.CommentInt;
import Bundestag.Session.Int.SessionInt;
import Bundestag.Session.Int.SpeechInt;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.apache.uima.jcas.JCas;
import org.bson.Document;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Speech_MongoDB_Impl implements SpeechInt {
    private SpeechInt speech;
    private MongoDatabase database;
    private MongoCollection<Document> collection;
    private String id;
    private Document filter;
    private Document speechDoc;

    public Speech_MongoDB_Impl(SpeechInt speech) {
        this.speech = speech;
    }

    public Speech_MongoDB_Impl(MongoDatabase database, String id) {
        this.database = database;
        this.id = id;
        collection = database.getCollection("reden");
        filter = new Document("id", id);
    }

    public Speech_MongoDB_Impl(MongoDatabase database, Document speechDoc) {
        this.database = database;
        this.speechDoc = speechDoc;
        collection = database.getCollection("reden");
        filter = new Document("id", speechDoc.getString("id"));
    }

    @Override
    public String getId() {
        if (speechDoc != null) {
            return speechDoc.getString("id");
        }
        return id;
    }

    @Override
    public SpeakerInt getSpeaker() {
        return null;
    }

    @Override
    public String getContent() {
        return "";
    }

    @Override
    public List<CommentInt> getComments() {
        return List.of();
    }

    @Override
    public SessionInt getSession() {
        return null;
    }

    @Override
    public AgendaInt getAgenda() {
        return null;
    }

    @Override
    public Document toDocument() {
        Document speechDocument = new Document();
        List<Document> commentDocuments = new ArrayList<>();
        for (CommentInt comment : speech.getComments()) {
            commentDocuments.add(comment.toDocument());
        }
        speechDocument.append("id", speech.getId())
                .append("content", speech.getContent())
                .append("comments", commentDocuments)
                .append("speaker", speech.getSpeaker().getId())
                .append("agenda", speech.getAgenda().getId())
                .append("session", speech.getSession().getId());
        return speechDocument;

    }

    @Override
    public String getSpeakerId() {
        return "";
    }

    @Override
    public JCas toCas() {
        return null;
    }

    /**
     * This Method retrieves the Datum related to the Rede Sitzung from the database
     * If the Rede Document is not found, the method returns a default Datum
     * The method finds the Sitzung related to the Rede via the Tagesordnung and parses the Datum
     * @return a LocalDate representing the Datum of a Sitzung if found
     */

    public LocalDate getDate() {
        Document doc;
        if (speechDoc != null) {
            doc = speechDoc;
        } else {
            doc = collection.find(filter).first();
        }
        if (doc == null) {
            String nullDate = "01.01.9999";
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            return LocalDate.parse(nullDate, formatter);
        }
        MongoCollection<Document> agendas = database.getCollection("agendas");
        String agendaFilterString = collection.find(filter).first().getString("agenda");
        Document agendaFilter = new Document("id", agendaFilterString);
        Document session = agendas.find(agendaFilter).first().get("session", Document.class);
        String dateString = session.getString("date");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        return LocalDate.parse(dateString, formatter);
    }


}
