package Rest.Service;

import Database.MongoDBHandler;
import Exporter.PDFExporter;
import Rest.RESTHandler;
import io.javalin.http.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
    private PDFExporter pdfExporter;

    /**
     * Constructor
     * @param mongoDBHandler for database-connection
     *
     * @author Amal
     */
    public ExportService(MongoDBHandler mongoDBHandler) {
        this.mongoDBHandler = mongoDBHandler;
        this.pdfExporter = new PDFExporter(mongoDBHandler);
    }


    public void exportSessionPdfs(Context ctx) {
        String sessionIds = ctx.queryParam("ids");
        assert sessionIds != null;
        List<String> sessionIdList = Arrays.asList(sessionIds.split(","));
        pdfExporter.exportSessionsToPDF(sessionIdList);

        File pdfFile = new File("src/main/resources/public/static/tex_output/sessions.pdf");
        if (!pdfFile.exists()) {
            ctx.status(404).result("PDF not found");
            return;
        }
        try {
            InputStream pdfStream = new FileInputStream(pdfFile);
            ctx.contentType("application/pdf");
            ctx.header("Content-Disposition", "inline; filename=\"sessions.pdf\"");
            ctx.result(pdfStream);
        } catch (FileNotFoundException e) {
            ctx.status(404).result("PDF not found");
        }
        pdfExporter.deleteFiles();
    }

    public void exportSpeakerPdf(Context ctx) {
        pdfExporter.deleteFiles();
        String speakerId = ctx.queryParam("id");
        pdfExporter.exportSpeakerPdf(speakerId);
        File pdfFile = new File("src/main/resources/public/static/tex_output/speaker.pdf");
        if (!pdfFile.exists()) {
            ctx.status(404).result("PDF not found");
            return;
        }
        try {
            InputStream pdfStream = new FileInputStream(pdfFile);
            ctx.contentType("application/pdf");
            ctx.header("Content-Disposition", "inline; filename=\"sessions.pdf\"");
            ctx.result(pdfStream);
        } catch (FileNotFoundException e) {
            ctx.status(404).result("PDF not found");
        }
        pdfExporter.deleteFiles();
    }
}
