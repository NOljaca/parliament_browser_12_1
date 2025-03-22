package Rest.Controller;

import Database.MongoDBHandler;
import Rest.Service.ExportService;
import io.javalin.http.Context;
import io.javalin.openapi.HttpMethod;
import io.javalin.openapi.OpenApi;
import io.javalin.openapi.OpenApiParam;
import io.javalin.openapi.OpenApiResponse;

import java.io.IOException;

/**
 * Controller of nlp-related api-requests. Service methods are called for each endpoint.
 * @author Amal
 */
public class ExportController {
    private MongoDBHandler mongoDBHandler;
    private ExportService exportService;

    /**
     * Constructor.
     * @param mongoDBHandler for database-connection
     *
     * @author Amal
     */
    public ExportController(MongoDBHandler mongoDBHandler) {
        this.mongoDBHandler = mongoDBHandler;
        this.exportService = new ExportService(mongoDBHandler);
    }

    /**
     * Call of service-method for exporting the pdf for selected sessions/protocols.
     * @param ctx
     *
     * @author Amal
     * OpenAPI added by Muhammed
     */
    @OpenApi(
            path = "/export/sessions",
            methods = HttpMethod.GET,
            summary = "Get pdf for selected session(s)",
            description = "Generates the pdfs for selected session(s)",
            queryParams = {@OpenApiParam(name = "ids", type = String.class, description = "The session-ID(s)")},
            tags = {"Export"},
            responses = {@OpenApiResponse(status = "200")}
    )
    public void getSessionExports(Context ctx) throws IOException {
        exportService.exportSessionPdfs(ctx);
    }

    /**
     * Call of service-method for exporting the pdf for the speaker.
     * @param ctx
     *
     * @author Amal
     * OpenAPI added by Muhammed
     */
    @OpenApi(
            path = "/export/speaker",
            methods = HttpMethod.GET,
            summary = "Get pdf for selected speaker",
            description = "Generates the pdf for selected speaker",
            queryParams = {@OpenApiParam(name = "id", type = String.class, description = "The speaker-ID")},
            tags = {"Export"},
            responses = {@OpenApiResponse(status = "200")}
    )
    public void getSpeakerExport(Context ctx) throws IOException {
        exportService.exportSpeakerPdf(ctx);
    }
}
