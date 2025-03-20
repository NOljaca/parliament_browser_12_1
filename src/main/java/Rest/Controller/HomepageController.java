package Rest.Controller;

import Database.MongoDBHandler;
import Rest.Service.HomepageService;
import io.javalin.http.Context;

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
     */
    public void getHomepage(Context ctx) {
        homepageService.renderHomePage(ctx);
    }

}
