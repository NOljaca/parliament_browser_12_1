package Rest.Controller;

import Database.MongoDBHandler;
import Rest.Service.SpeakerService;
import io.javalin.http.Context;

/**
 * Controller of speaker-related api-requests. Service methods are called for each endpoint.
 * @author Amal
 */
public class SpeakerController {
    private SpeakerService speakerService;
    private MongoDBHandler mongoDBHandler;

    /**
     * Constructor.
     * @param mongoDBHandler for database-connection
     *
     * @author Amal
     */
    public SpeakerController(MongoDBHandler mongoDBHandler) {
        this.mongoDBHandler = mongoDBHandler;
        this.speakerService = new SpeakerService(mongoDBHandler);
    }

    /**
     * Call of service-method for speaker-list-page-rendering.
     * @param ctx
     *
     * @author Amal
     */
    public void getSpeakerList(Context ctx){
        speakerService.renderSpeakerList(ctx);
    }

    /**
     * Call of service-method for portfolio-page-rendering.
     * @param ctx
     *
     * @author Amal
     */
    public void getPortfolio(Context ctx){
        speakerService.renderPortfolio(ctx);
    }
}
