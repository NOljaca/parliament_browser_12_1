package Rest.Controller;

import Database.MongoDBHandler;
import Rest.Service.ExportService;
import io.javalin.http.Context;

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
     */
    public void getSessionExports(Context ctx) {
        exportService.exportSessionPdfs(ctx);
    }

    /**
     * Call of service-method for exporting the pdf for the speaker.
     * @param ctx
     *
     * @author Amal
     */
    public void getSpeakerExport(Context ctx) {
        exportService.exportSpeakerPdf(ctx);
    }
}
