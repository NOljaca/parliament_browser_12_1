package Rest.Service;

import Bundestag.Persons.Int.SpeakerInt;
import Bundestag.Session.Int.AgendaInt;
import Bundestag.Session.Int.SessionInt;
import Bundestag.Session.Int.SpeechInt;
import Database.MongoDBHandler;
import Rest.JSON.SpeechJSON;
import Rest.JSON.ToJSONUtil;
import io.javalin.http.Context;
import org.json.JSONArray;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service-Class for speech-related api-requests.
 *
 * @author Amal
 */
public class SpeechService {
    private MongoDBHandler mongoDBHandler;

    /**
     * Constructor
     * @param mongoDBHandler for database-connection
     *
     * @author Amal
     */
    public SpeechService(MongoDBHandler mongoDBHandler) {
        this.mongoDBHandler = mongoDBHandler;
    }

    /**
     * Method for speeches-page-rendering. Fetches all agendas and speeches for the selected sesion.
     * @param ctx
     *
     * @author Amal
     */
    public void renderSpeechesPage(Context ctx) {
        String sessionId = ctx.queryParam("id");
        Map<String, Object> response = new HashMap<>();
        List<SpeechJSON> speeches = mongoDBHandler.getSpeechesBySession(sessionId);
        SessionInt session = mongoDBHandler.getSessionById(sessionId);
        List<AgendaInt> agendas = session.getAgenda();
        response.put("speeches", speeches);
        response.put("session", session);
        response.put("agendas", agendas);
        ctx.res().setCharacterEncoding("UTF-8");
        System.out.println("Rendering Speeches Page...");
        ctx.render("speeches.ftl", response);
        ctx.status(200);
        System.out.println("Done!");
    }

    /**
     * Method for speech-details-rendering. Fetches all details for the selected speech (speaker-meta-data and nlp-analysis).
     * @param ctx
     *
     * @author Amal
     */
    public void renderSpeechDetailsPage(Context ctx) {
        String speechId = ctx.queryParam("id");
        Map<String, Object> response = new HashMap<>();
        SpeechInt speech = mongoDBHandler.getSpeechById(speechId);
        SpeakerInt speaker = speech.getSpeaker();
        List<Map<String, Object>> sentences = mongoDBHandler.getSentences(speechId);
        List<Map<String, Object>> namedEntities = mongoDBHandler.getNamedEntities(speechId);
        JSONArray sentencesJSON = ToJSONUtil.toJSONArray(sentences, "sentence", "sentiment");
        JSONArray namedEntitiesJSON = ToJSONUtil.toJSONArray(namedEntities, "namedEntity", "category");

        response.put("speaker", speaker);
        response.put("speech", speech);
        response.put("sentences", sentencesJSON);
        response.put("namedEntities", namedEntitiesJSON);
        ctx.res().setCharacterEncoding("UTF-8");
        System.out.println("Rendering SpeechDetails Page...");
        ctx.render("speechdetails.ftl", response);
        ctx.status(200);
        System.out.println("Done!");
    }
}
