package Database.MongoDB_Impl;

import Bundestag.Session.Int.AgendaInt;
import Bundestag.Session.Int.SessionInt;
import org.bson.Document;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Session_MongoDB_Impl implements SessionInt {

    private SessionInt session;

    public Session_MongoDB_Impl(SessionInt session) {
        this.session = session;
    }

    @Override
    public int getId() {
        return 0;
    }

    @Override
    public String getTitle() {
        return "";
    }

    @Override
    public LocalDate getDate() {
        return null;
    }

    @Override
    public List<AgendaInt> getAgenda() {
        return List.of();
    }

    @Override
    public void addAgenda(AgendaInt agenda) {

    }

    @Override
    public Document toDocument() {
        Document sessionDocument = new Document();

        List<String> agendaIds = new ArrayList<>();
        for (AgendaInt agenda : session.getAgenda()) {
            agendaIds.add(agenda.getId());
        }

        sessionDocument.append("id", session.getId())
                .append("title", session.getTitle())
                .append("date", session.getDate())
                .append("agendas", agendaIds);
        return sessionDocument;
    }
}
