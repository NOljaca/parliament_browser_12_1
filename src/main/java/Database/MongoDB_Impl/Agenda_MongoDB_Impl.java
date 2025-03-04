package Database.MongoDB_Impl;

import Bundestag.Session.Int.AgendaInt;
import Bundestag.Session.Int.SessionInt;
import Bundestag.Session.Int.SpeechInt;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class Agenda_MongoDB_Impl implements AgendaInt {

    private AgendaInt agenda;

    public Agenda_MongoDB_Impl(AgendaInt agenda) {
        this.agenda = agenda;
    }

    @Override
    public String getId() {
        return "";
    }

    @Override
    public String getTitle() {
        return "";
    }

    @Override
    public String getAgendaId() {
        return "";
    }

    @Override
    public SessionInt getSession() {
        return null;
    }

    @Override
    public List<SpeechInt> getSpeeches() {
        return List.of();
    }

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
}
