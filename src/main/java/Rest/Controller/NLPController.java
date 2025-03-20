package Rest.Controller;

import Database.MongoDBHandler;
import Rest.Service.NLPService;
import io.javalin.http.Context;

/**
 * Controller of nlp-related api-requests. Service methods are called for each endpoint.
 * @author Amal
 */
public class NLPController {
    private MongoDBHandler mongoDBHandler;
    private NLPService nlpService;

    /**
     * Constructor.
     * @param mongoDBHandler for database-connection
     *
     * @author Amal
     */
    public NLPController(MongoDBHandler mongoDBHandler) {
        this.mongoDBHandler = mongoDBHandler;
        this.nlpService = new NLPService(mongoDBHandler);
    }

    /**
     * Call of service-method for nlp-charts-page-rendering.
     * @param ctx
     *
     * @author Amal
     */
    public void getNLPCharts(Context ctx){
        nlpService.renderNLPChartsPage(ctx);
    }

    /**
     * Call of service-method for fetching speeches for the selected session.
     * @param ctx
     *
     * @author Amal
     */
    public void getSpeechesForSession(Context ctx){
        nlpService.fetchSpeechesForSession(ctx);
    }

    /**
     * Call of service-method for fetching the nlp-chart-data for the selected filters.
     * Filters can either be one speech or multiple speeches or a combined filter of dates, fractions and speakers.
     * @param ctx
     *
     * @author Amal
     */
    public void getChartData(Context ctx){
        nlpService.fetchChartData(ctx);
    }

    /**
     * Call of service-method for fetching the nlp-chart-data for all speeches.
     * @param ctx
     *
     * @author Amal
     */
    public void getChartDataForAllSpeeches(Context ctx) {
        nlpService.fetchChartDataForAllSpeeches(ctx);
    }
}
