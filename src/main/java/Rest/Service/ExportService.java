package Rest.Service;

import Database.MongoDBHandler;
import io.javalin.http.Context;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

/**
 * Service-Class for export-related api-requests.
 *
 * @author Amal
 */
public class ExportService {
    private MongoDBHandler mongoDBHandler;

    /**
     * Constructor
     * @param mongoDBHandler for database-connection
     *
     * @author Amal
     */
    public ExportService(MongoDBHandler mongoDBHandler) {
        this.mongoDBHandler = mongoDBHandler;
    }


    public void exportSessionPdfs(Context ctx) {
        String sessionIds = ctx.queryParam("ids");
        assert sessionIds != null;
        List<String> sessionIdList = Arrays.asList(sessionIds.split(","));
        InputStream pdfStream = getClass().getResourceAsStream("/public/static/file.pdf");
        if (pdfStream == null) {
            ctx.status(404).result("PDF not found");
            return;
        }
        ctx.contentType("application/pdf");
        ctx.header("Content-Disposition", "inline; filename=\"file.pdf\"");
        ctx.result(pdfStream);
    }

    public void exportSpeakerPdf(Context ctx) {
        String speakerId = ctx.queryParam("id");
        // Read the PDF from the resources folder.
        InputStream pdfStream = getClass().getResourceAsStream("/public/static/file.pdf");
        if (pdfStream == null) {
            ctx.status(404).result("PDF not found");
            return;
        }
        // Set headers to display PDF inline
        ctx.contentType("application/pdf");
        ctx.header("Content-Disposition", "inline; filename=\"yourfile.pdf\"");
        ctx.result(pdfStream);
    }
}
