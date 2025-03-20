package Rest.Controller;

import Database.MongoDBHandler;
import Rest.Service.SpeechService;
import io.javalin.http.Context;

/**
 * Controller of speech-related api-requests. Service methods are called for each endpoint.
 * @author Amal
 */
public class SpeechController {
    private MongoDBHandler mongoDBHandler;
    private SpeechService speechService;

    /**
     * Constructor.
     * @param mongoDBHandler for database-connection
     *
     * @author Amal
     */
    public SpeechController(MongoDBHandler mongoDBHandler) {
        this.mongoDBHandler = mongoDBHandler;
        this.speechService = new SpeechService(mongoDBHandler);
    }

    /**
     * Call of service-method for speeches-page-rendering.
     * @param ctx
     *
     * @author Amal
     */
    public void getSessionsPage(Context ctx) {
        speechService.renderSpeechesPage(ctx);
    }

    /**
     * Call of service-method for speech-details-page-rendering.
     * @param ctx
     *
     * @author Amal
     */
    public void getSpeechDetailsPage(Context ctx) {
        speechService.renderSpeechDetailsPage(ctx);
    }
}
