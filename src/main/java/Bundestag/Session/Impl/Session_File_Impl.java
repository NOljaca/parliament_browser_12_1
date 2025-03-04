package Bundestag.Session.Impl;

import Bundestag.Session.Int.AgendaInt;
import Bundestag.Session.Int.SessionInt;
import org.bson.Document;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of {@link SessionInt}. Stores all attributes of a session from the xml-file.
 */
public class Session_File_Impl implements SessionInt {

    private int id;
    private String title;
    private LocalDate date;
    private List<AgendaInt> agenda;

    /**
     * Class for storing all attributes (from xml-file) of a session.
     * @param id id of session
     * @param title title of session
     * @param date date of session
     */
    public Session_File_Impl(int id, String title, LocalDate date) {
        this.id = id;
        this.title = title;
        this.date = date;
        this.agenda = new ArrayList<>();
    }

    public Session_File_Impl() {}

    @Override
    public int getId() {
        return id;
    }

    public void setId(int id) {
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
    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    @Override
    public List<AgendaInt> getAgenda() {
        return agenda;
    }

    public void setAgenda(List<AgendaInt> agenda) {
        this.agenda = agenda;
    }

    public void addAgenda(AgendaInt agenda) {
        this.agenda.add(agenda);
    }

    @Override
    public Document toDocument() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
