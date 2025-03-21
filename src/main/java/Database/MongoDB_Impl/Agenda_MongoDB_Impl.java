package Database.MongoDB_Impl;

import Bundestag.Session.Int.AgendaInt;
import Bundestag.Session.Int.SessionInt;
import Bundestag.Session.Int.SpeechInt;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of AgendaInt. Stores agenda-attributes to map for the database.
 * @author Amal
 */
public class Agenda_MongoDB_Impl implements AgendaInt {

    private AgendaInt agenda;
    private Document agendaDoc;
    private MongoDatabase mongoDatabase;

    public Agenda_MongoDB_Impl(AgendaInt agenda) {
        this.agenda = agenda;
    }
    public Agenda_MongoDB_Impl(Document agendaDoc) {
        this.agendaDoc = agendaDoc;
    }
    public Agenda_MongoDB_Impl(Document agendaDoc, MongoDatabase mongoDatabase) {
        this.agendaDoc = agendaDoc;
        this.mongoDatabase = mongoDatabase;
    }

    @Override
    public String getId() {
        return agendaDoc.getString("id");
    }

    @Override
    public String getTitle() {
        return agendaDoc.getString("title");
    }

    @Override
    public String getAgendaId() {
        return agendaDoc.getString("agendaId");
    }

    @Override
    public SessionInt getSession() {
        return null;
    }

    @Override
    public List<SpeechInt> getSpeeches() {
        Document filter = new Document("agendaId", getAgendaId());
        List<String> speechIds = mongoDatabase.getCollection("agendas").find().filter(filter).first().getList("speeches", String.class);
        MongoCollection<Document> speechCollection = mongoDatabase.getCollection("speeches");
        List<SpeechInt> speeches = new ArrayList<>();
        for (String speechId : speechIds) {
            Document speechFilter = new Document("id", speechId);
            Document speechDoc = speechCollection.find().filter(speechFilter).first();
            SpeechInt speech = new Speech_MongoDB_Impl(mongoDatabase, speechDoc);
            speeches.add(speech);
        }
        return speeches;
    }

    /**
     * Creates a document of a agenda-object.
     * @return document mapped with agenda-attributes.
     * @author Amal
     */
    @Override
    public Document toDocument() {
        Document agendaDocument = new Document();
        Session_MongoDB_Impl sessionMongoDB = new Session_MongoDB_Impl(agenda.getSession());
        List<String> speechIds = new ArrayList<>();
        for (SpeechInt speech : agenda.getSpeeches()) {
            speechIds.add(speech.getId());
        }

        agendaDocument.append("agendaId", agenda.getAgendaId())
                .append("id", agenda.getId())
                .append("title", agenda.getTitle())
                .append("session", sessionMongoDB.toDocument())
                .append("speeches", speechIds);
        return agendaDocument;
    }

    @Override
    public void addSpeech(SpeechInt speech) {
    }

    public String toTexIndex() {
        StringBuilder latex = new StringBuilder();
        latex.append(getId())
                .append(" - ")
                .append(getTitle())
                .append(" \\dotfill \\pageref{")
                .append(getAgendaId())
                .append("}\n");
        return latex.toString();
    }

    public String toTex() {
        StringBuilder latex = new StringBuilder();
        latex.append("\\subsection{").append(getId()).append("}\n");
        latex.append("\\label{").append(getAgendaId()).append("}\n");
        for (SpeechInt speech : getSpeeches()) {
            latex.append(speech.toTex());
        }
        return latex.toString();
    }
}
