package Rest.Service;

import Bundestag.Persons.Int.SpeakerInt;
import Database.MongoDBHandler;
import io.javalin.http.Context;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service-Class for speaker-related api-requests.
 *
 * @author Amal
 */
public class SpeakerService {
    private MongoDBHandler mongoDBHandler;

    /**
     * Constructor
     * @param mongoDBHandler for database-connection
     *
     * @author Amal
     */
    public SpeakerService(MongoDBHandler mongoDBHandler) {
        this.mongoDBHandler = mongoDBHandler;
    }

    /**
     * Method for speaker-list-page-rendering. Fetches all speakers.
     * @param ctx
     *
     * @author Amal
     */
    public void renderSpeakerList(Context ctx) {
        Map<String, Object> response = new HashMap<>();
        List<SpeakerInt> speakers = mongoDBHandler.getAllSpeakers();
        response.put("speakers", speakers);
        ctx.res().setCharacterEncoding("UTF-8");
        System.out.println("Rendering speaker-list...");
        ctx.render("speakerlist.ftl", response);
        ctx.status(200);
        System.out.println("Done!");
    }

    /**
     * Method for portfolio-page-rendering. Fetches the meta-data and all speeches for the selected speaker.
     * @param ctx
     *
     * @author Amal
     */
    public void renderPortfolio(Context ctx) {
        String id = ctx.queryParam("id");
        Map<String, Object> response = new HashMap<>();
        SpeakerInt speaker = mongoDBHandler.getSpeakerByID(id);
        response.put("speaker", speaker);
        response.put("speeches", speaker.getSortedSpeeches());
        response.put("mdbH", mongoDBHandler);
        ctx.res().setCharacterEncoding("UTF-8");
        ctx.render("portfolio.ftl", response);
        ctx.status(200);
    }

}
