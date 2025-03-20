package Rest.Controller;

import Database.MongoDBHandler;
import Rest.Service.SpeechService;
import io.javalin.http.Context;
import io.javalin.openapi.HttpMethod;
import io.javalin.openapi.OpenApi;
import io.javalin.openapi.OpenApiContent;
import io.javalin.openapi.OpenApiParam;
import io.javalin.openapi.OpenApiRequestBody;
import io.javalin.openapi.OpenApiResponse;

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
     * OpenAPI added by Muhammed
     */
    @OpenApi(
            path = "/speeches",
            methods = HttpMethod.GET,
            summary = "Get speech-list",
            description = "Fetches the speech-list view.",
            queryParams = {@OpenApiParam(name = "id", type = String.class, description = "The session-ID")},
            tags = {"Speech"},
            responses = {@OpenApiResponse(status = "200")}
    )
    public void getSessionsPage(Context ctx) {
        speechService.renderSpeechesPage(ctx);
    }

    /**
     * Call of service-method for speech-details-page-rendering.
     * @param ctx
     *
     * @author Amal
     * OpenAPI added by Muhammed
     */
    @OpenApi(
            path = "/speeches/speechdetails",
            methods = HttpMethod.GET,
            summary = "Get speech-details",
            description = "Fetches the speech-details view.",
            queryParams = {@OpenApiParam(name = "id", type = String.class, description = "The speech-ID")},
            tags = {"Speech"},
            responses = {@OpenApiResponse(status = "200")}
    )
    public void getSpeechDetailsPage(Context ctx) {
        speechService.renderSpeechDetailsPage(ctx);
    }

    /**
     * Method for speech-search-page-rendering is called.
     * @param ctx
     * @author Muhammed
     */
    @OpenApi(
            path = "/speeches/searchspeeches",
            methods = HttpMethod.GET,
            summary = "Get speech-search-page.",
            description = "Fetches the speech-search view.",
            tags = {"Speech"},
            responses = {@OpenApiResponse(status = "200")}
    )
    public void getSpeechSearch(Context ctx) {
        speechService.renderSpeechSearchPage(ctx);
    }

    /**
     * Method for fetching speeches by substring is called.
     * @param ctx
     * @author Muhammed
     */
    @OpenApi(
            path = "/speeches/searchspeeches/fetchspeeches",
            methods = HttpMethod.GET,
            summary = "Get List of speech-id's",
            description = "Fetches a list of the id's of speeches which mach with given text.",
            queryParams = {@OpenApiParam(name = "text", type = String.class, description = "The substring of wanted speech")},
            tags = {"Speech"},
            responses = {@OpenApiResponse(status = "200")}
    )
    public void getSearchSpeeches(Context ctx) {
        speechService.fetchSpeeches(ctx);
    }
}
