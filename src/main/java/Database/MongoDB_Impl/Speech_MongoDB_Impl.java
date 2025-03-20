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
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Implementation of SpeechInt. Stores speech-attributes to map for the database.
 * @author Amal
 */
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
        collection = database.getCollection("speeches");
        filter = new Document("id", id);
    }

    public Speech_MongoDB_Impl(MongoDatabase database, Document speechDoc) {
        this.database = database;
        this.speechDoc = speechDoc;
        collection = database.getCollection("speeches");
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
        MongoCollection<Document> speakerCollection = database.getCollection("speakers");
        Document speakerFilter = new Document("id", speechDoc.getString("speaker"));
        Document speakerDoc = speakerCollection.find(speakerFilter).first();
        return new Speaker_MongoDB_Impl(database, speakerDoc);
    }

    @Override
    public String getContent() {
        if (speechDoc != null) {
            return speechDoc.getString("content");
        }
        return collection.find(filter).first().getString("content");
    }

    @Override
    public List<CommentInt> getComments() {
        if (speechDoc != null) {
            List<CommentInt> comments = new ArrayList<>();
            List<Document> commentDocs = speechDoc.getList("comments", Document.class);
            for (Document commentDoc : commentDocs) {
                comments.add(new Comment_MongoDB_Impl(commentDoc, database));
            }
            return comments;
        }
        List<CommentInt> comments = new ArrayList<>();
        List<Document> commentDocs = speechDoc.getList("comments", Document.class);
        for (Document commentDoc : commentDocs) {
            comments.add(new Comment_MongoDB_Impl(database, commentDoc.getString("id"))
            );
        }
        return comments;

    }

    @Override
    public SessionInt getSession() {
        MongoCollection<Document> sessionCollection = database.getCollection("sessions");
        Document sessionFilter = new Document("id", speechDoc.getInteger("session"));
        Document sessionDoc = sessionCollection.find(sessionFilter).first();
        return new Session_MongoDB_Impl(sessionDoc, database);
    }

    public String getSessionId() {
        if (speechDoc != null) {
            return String.valueOf(speechDoc.getInteger("session"));
        } else {
            return String.valueOf(collection.find(filter).first().getInteger("session"));
        }
    }

    @Override
    public AgendaInt getAgenda() {
        String sessionId = getSessionId();
        String agendaID = sessionId + "-" + collection.find(filter).first().getString("agenda");
        Document agendaFilter = new Document("agendaId", agendaID);
        Document agendaDoc = database.getCollection("agendas").find(agendaFilter).first();
        return new Agenda_MongoDB_Impl(agendaDoc);
    }

    /**
     * Creates a document of a speech-object.
     * @return document mapped with speech-attributes.
     * @author Amal
     */
    @Override
    public Document toDocument() {
        Document speechDocument = new Document();
        List<Document> commentDocuments = new ArrayList<>();
        for (CommentInt comment : speech.getComments()) {
            CommentInt commentMongoDB = new Comment_MongoDB_Impl(comment);
            commentDocuments.add(commentMongoDB.toDocument());
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
     * This Method retrieves the date related to the speech-session from the database
     * If the speech Document is not found, the method returns a default date
     * The method finds the session related to the speech via the agenda and parses the date
     * @return a LocalDate representing the date of a session if found
     *
     * @author Amal
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
        Date date = session.getDate("date");
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public String getContentWithComments() {
        List<CommentInt> comments = getComments();
        if(comments != null) {
            comments.sort(Comparator.comparing(CommentInt::getIndex));
        }
        String content = getContent();
        int textIndex = 0;
        StringBuilder speechBuilder = new StringBuilder();
        if (comments != null) {
            for (CommentInt comment : comments) {
                int index = comment.getIndex();
                String commentContent = comment.getContent();

                if (index < 0 || index > content.length()) {
                    continue;
                }

                if (textIndex < index) {
                    speechBuilder.append(content, textIndex, index);
                    textIndex = index;
                }
                speechBuilder.append("<span id=\""+comment.getId()+"\" class=\"comment\">"+commentContent+"</span>");
            }
        }
        if (textIndex < content.length()) {
            speechBuilder.append(content.substring(textIndex));
        }
        return speechBuilder.toString();
    }


}
