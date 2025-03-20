package Rest.Controller;

import Database.MongoDBHandler;
import Rest.Service.SpeakerService;
import io.javalin.http.Context;
import io.javalin.openapi.HttpMethod;
import io.javalin.openapi.OpenApi;
import io.javalin.openapi.OpenApiParam;
import io.javalin.openapi.OpenApiResponse;

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
     * OpenApi added by Muhammed
     */
    @OpenApi(
            path = "/speakers",
            methods = HttpMethod.GET,
            summary = "Get speaker-list",
            description = "Fetches the speaker-list view.",
            tags = {"Speaker"},
            responses = {@OpenApiResponse(status = "200")}
    )
    public void getSpeakerList(Context ctx){
        speakerService.renderSpeakerList(ctx);
    }

    /**
     * Call of service-method for portfolio-page-rendering.
     * @param ctx
     *
     * @author Amal
     * OpenApi added by Muhammed
     */
    @OpenApi(
            path = "/speakers/portfolio",
            methods = HttpMethod.GET,
            summary = "Get portfolio for speaker",
            description = "Fetches the homepage view.",
            queryParams = {@OpenApiParam(name = "id", type = String.class, description = "The speaker-ID")},
            tags = {"Speaker"},
            responses = {@OpenApiResponse(status = "200")}
    )
    public void getPortfolio(Context ctx){
        speakerService.renderPortfolio(ctx);
    }
}
