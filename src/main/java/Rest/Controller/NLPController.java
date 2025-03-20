package Rest.Controller;

import Database.MongoDBHandler;
import Rest.Service.NLPService;
import io.javalin.http.Context;
import io.javalin.openapi.HttpMethod;
import io.javalin.openapi.OpenApi;
import io.javalin.openapi.OpenApiParam;
import io.javalin.openapi.OpenApiResponse;

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
     * OpenAPI added by Muhammed
     */
    @OpenApi(
            path = "/nlpcharts",
            methods = HttpMethod.GET,
            summary = "Get nlp-charts",
            description = "Fetches the nlp-charts view.",
            tags = {"NLP"},
            responses = {@OpenApiResponse(status = "200")}
    )
    public void getNLPCharts(Context ctx){
        nlpService.renderNLPChartsPage(ctx);
    }

    /**
     * Call of service-method for fetching speeches for the selected session.
     * @param ctx
     *
     * @author Amal
     * OpenAPI added by Muhammed
     */
    @OpenApi(
            path = "/nlpcharts/sessionchange",
            methods = HttpMethod.GET,
            summary = "Get speeches for selected session",
            description = "Fetches the sessions for selected session",
            queryParams = {@OpenApiParam(name = "id", type = String.class, description = "The session-ID")},
            tags = {"NLP"},
            responses = {@OpenApiResponse(status = "200")}
    )
    public void getSpeechesForSession(Context ctx){
        nlpService.fetchSpeechesForSession(ctx);
    }

    /**
     * Call of service-method for fetching the nlp-chart-data for the selected filters.
     * Filters can either be one speech or multiple speeches or a combined filter of dates, fractions and speakers.
     * @param ctx
     *
     * @author Amal
     * OpenAPI added by Muhammed
     */
    @OpenApi(
            path = "/nlpcharts/chartdata",
            methods = HttpMethod.GET,
            summary = "Get chart-data for selected filter(s)",
            description = "Fetches the chart-data for selected filter(s)",
            queryParams = {@OpenApiParam(name = "speechids", type = String.class, description = "The speech-IDs selected by the user"),
                    @OpenApiParam(name = "fractions", type = String.class, description = "The fraction-names selected by the user"),
                    @OpenApiParam(name = "speakers", type = String.class, description = "The speakers selected by the user"),
                    @OpenApiParam(name = "dates", type = String.class, description = "The dates selected by the user")},
            tags = {"NLP"},
            responses = {@OpenApiResponse(status = "200")}
    )
    public void getChartData(Context ctx){
        nlpService.fetchChartData(ctx);
    }

    /**
     * Call of service-method for fetching the nlp-chart-data for all speeches.
     * @param ctx
     *
     * @author Amal
     * OpenAPI added by Muhammed
     */
    @OpenApi(
            path = "/nlpcharts/fetchchartdataforallspeeches",
            methods = HttpMethod.GET,
            summary = "Get chart-data for all speeches",
            description = "Fetches the chart-data for all speeches",
            tags = {"NLP"},
            responses = {@OpenApiResponse(status = "200")}
    )
    public void getChartDataForAllSpeeches(Context ctx) {
        nlpService.fetchChartDataForAllSpeeches(ctx);
    }
}
