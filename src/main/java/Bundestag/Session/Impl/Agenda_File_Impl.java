package Bundestag.Session.Impl;

import Bundestag.Session.Int.AgendaInt;
import Bundestag.Session.Int.SessionInt;
import Bundestag.Session.Int.SpeechInt;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of {@link AgendaInt}. Stores all attributes of an agenda from the xml-file.
 * @author Muhammed
 */
public class Agenda_File_Impl implements AgendaInt {

    private String id;
    private String title;
    private SessionInt session;
    private List<SpeechInt> speeches;
    private String agendaId;

    /**
     * Class for storing all attributes (from xml-file) of an agenda.
     * @param id id of agenda
     * @param title title of agenda
     */
    public Agenda_File_Impl(String id, String title) {
        this.id = id;
        this.title = title;
        this.session = null;
        this.speeches = new ArrayList<>();
        this.agendaId = null;
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public SessionInt getSession() {
        return session;
    }

    /**
     * Sets the specific session for agenda and also defines the agendaId.
     * @param session instance of {@link SessionInt}
     */
    public void setSession(SessionInt session) {
        this.session = session;
        this.agendaId = session.getId() + "-" + this.id;
    }

    @Override
    public List<SpeechInt> getSpeeches() {
        return speeches;
    }

    @Override
    public Document toDocument() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setSpeeches(List<SpeechInt> speeches) {
        this.speeches = speeches;
    }

    @Override
    public void addSpeech(SpeechInt speech) {
        this.speeches.add(speech);
    }

    public String getAgendaId() {
        return agendaId;
    }
}
