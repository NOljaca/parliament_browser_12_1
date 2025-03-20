package Rest.Controller;

import Database.MongoDBHandler;
import Rest.Service.HomepageService;
import io.javalin.http.Context;
import io.javalin.openapi.HttpMethod;
import io.javalin.openapi.OpenApi;
import io.javalin.openapi.OpenApiResponse;


/**
 * Controller of homepage-related api-requests. Service methods are called for each endpoint.
 * @author Amal
 */
public class HomepageController {
    private MongoDBHandler mongoDBHandler;
    private HomepageService homepageService;

    /**
     * Constructor.
     * @param mongoDBHandler for database-connection
     *
     * @author Amal
     */
    public HomepageController(MongoDBHandler mongoDBHandler) {
        this.mongoDBHandler = mongoDBHandler;
        this.homepageService = new HomepageService(mongoDBHandler);
    }

    /**
     * Call of service-method for homepage-redirection.
     * @param ctx
     *
     * @author Amal
     */
    public void redirectToHomePage(Context ctx) {
        homepageService.redirectToHomePage(ctx);
    }

    /**
     * Call of service-method for homepage-rendering.
     * @param ctx
     *
     * @author Amal
     * OpenApi added by Muhammed
     */
    @OpenApi(
            path = "/home",
            methods = HttpMethod.GET,
            summary = "Get homepage",
            description = "Fetches the homepage view.",
            tags = {"Homepage"},
            responses = {@OpenApiResponse(status = "200")}
    )
    public void getHomepage(Context ctx) {
        homepageService.renderHomePage(ctx);
    }
}
