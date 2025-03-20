package Database.MongoDB_Impl;

import Bundestag.Session.Int.AgendaInt;
import Bundestag.Session.Int.SessionInt;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Implementation of SessionInt. Stores session-attributes to map for the database.
 * @author Amal
 */
public class Session_MongoDB_Impl implements SessionInt {

    private SessionInt session;
    private Document sessionDoc;
    private MongoDatabase mongoDatabase;

    public Session_MongoDB_Impl(SessionInt session) {
        this.session = session;
    }

    public Session_MongoDB_Impl(Document sessionDoc, MongoDatabase mongoDatabase) {
        this.sessionDoc = sessionDoc;
        this.mongoDatabase = mongoDatabase;
    }

    @Override
    public int getId() {
        return sessionDoc.getInteger("id");
    }

    @Override
    public String getTitle() {
        System.out.println(sessionDoc.getString("title"));
        return sessionDoc.getString("title");
    }

    public String getDateString() {
        Date date = sessionDoc.getDate("date");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        LocalDateTime localDateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        return localDateTime.format(formatter);
    }

    @Override
    public LocalDate getDate() {
        return null;
    }

    @Override
    public List<AgendaInt> getAgenda() {
        List<AgendaInt> agendas = new ArrayList<>();
        MongoCollection<Document> agendaCollection = mongoDatabase.getCollection("agendas");
        for (String agendaName : sessionDoc.getList("agendas", String.class)) {
            String agendaId = getId() + "-" + agendaName;
            System.out.println(agendaId);
            Document agendaFilter = new Document("agendaId", agendaId);
            Document agendaDoc  = agendaCollection.find(agendaFilter).first();
            AgendaInt agenda = new Agenda_MongoDB_Impl(agendaDoc);
            agendas.add(agenda);
        }
        return agendas;
    }

    public int getAgendaSize() {
        List<String> agenda = sessionDoc.getList("agendas", String.class);
        return agenda.size();
    }

    @Override
    public void addAgenda(AgendaInt agenda) {

    }

    /**
     * Creates a document of a session-object.
     * @return document mapped with session-attributes.
     * @author Amal
     */
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
