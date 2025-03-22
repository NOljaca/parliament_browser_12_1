package Bundestag.Session.Int;

import Bundestag.Session.Impl.Agenda_File_Impl;
import org.bson.Document;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

/**
 * Interface for session. Stores all attributes of session.
 * @author Muhammed
 */
public interface SessionInt {

    /**
     * @return id of session
     */
    int getId();

    /**
     * @return title of session
     */
    String getTitle();

    /**
     * @return date of session
     */
    LocalDate getDate();

    /**
     * @return agenda of session
     */
    List<AgendaInt> getAgenda();

    /**
     * Add instance of {@link AgendaInt} to the list of agendas.
     * @param agenda instance of {@link AgendaInt} which should be added to the list.
     */
    void addAgenda(AgendaInt agenda);

    String getDateString();
    int getAgendaSize();

    /**
     * Create Document with key-value pairs that match the attributes of session.
     * @return created document with the attributes in the specific fields.
     */
    Document toDocument();

    String toTex() throws IOException;
}
