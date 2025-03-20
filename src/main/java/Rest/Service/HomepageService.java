package Rest.Service;

import Bundestag.Session.Int.SessionInt;
import Database.MongoDBHandler;
import io.javalin.http.Context;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service-Class for homepage-related api-requests.
 *
 * @author Amal
 */
public class HomepageService {
    private MongoDBHandler mongoDBHandler;

    /**
     * Constructor
     * @param mongoDBHandler for database-connection
     *
     * @author Amal
     */
    public HomepageService(MongoDBHandler mongoDBHandler) {
        this.mongoDBHandler = mongoDBHandler;
    }

    /**
     * Method for homepage-redirection
     * @param ctx
     *
     * @author Amal
     */
    public void redirectToHomePage(Context ctx) {
        ctx.redirect("/home");
    }

    /**
     * Method for homepage-rendering. Fetches all sessions.
     * @param ctx
     *
     * @author Amal
     */
    public void renderHomePage(Context ctx) {
        Map<String, Object> response = new HashMap<>();
        List<SessionInt> sessions = mongoDBHandler.getSessions();
        response.put("sessions", sessions);
        ctx.res().setCharacterEncoding("UTF-8");
        System.out.println("Rendering Homepage...");
        ctx.render("homepage.ftl", response);
        ctx.status(200);
        System.out.println("Done!");
    }
}
