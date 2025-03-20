package Rest;

import Database.MongoDBHandler;
import PropertyHandlers.ServerProperties;
import Rest.Controller.*;
import freemarker.template.Configuration;
import io.javalin.Javalin;
import io.javalin.openapi.plugin.swagger.SwaggerPlugin;
import io.javalin.rendering.template.JavalinFreemarker;
import io.javalin.openapi.plugin.OpenApiPlugin;

import java.io.File;
import java.io.IOException;

import static io.javalin.apibuilder.ApiBuilder.*;

/**
 * Class for handling the restful web-service and api-requests.
 * @author Amal
 */
public class RESTHandler {
    Javalin app;
    MongoDBHandler mongoDBHandler;
    ServerProperties serverProperties;
    HomepageController homepageController;
    SpeechController speechController;
    SpeakerController speakerController;
    NLPController nlpController;
    ExportController exportController;

    /**
     * Constructor sets the configuration and starts the javalin-webservice.
     * @param mdbHandler MongoDBHandler for handling the database-connected operations.
     * @throws IOException
     *
     * @author amal
     */
    public RESTHandler(MongoDBHandler mdbHandler) throws IOException {
        this.mongoDBHandler = mdbHandler;
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_33);
        configuration.setDirectoryForTemplateLoading(new File("./templates/"));

        serverProperties = new ServerProperties("server.properties");
        homepageController = new HomepageController(mongoDBHandler);
        speechController = new SpeechController(mongoDBHandler);
        speakerController = new SpeakerController(mongoDBHandler);
        nlpController = new NLPController(mongoDBHandler);
        exportController = new ExportController(mongoDBHandler);

        this.app = Javalin.create(config -> {
            config.fileRenderer(new JavalinFreemarker(configuration));
            config.staticFiles.add("/public");
            config.registerPlugin(new OpenApiPlugin(pluginConfig -> {
                pluginConfig.withDefinitionConfiguration((version, definition) -> {
                    definition.withInfo(info -> info.setTitle("Multimodal Parliament Browser"));
                });
            }));
            config.registerPlugin(new SwaggerPlugin());
            config.router.apiBuilder(() -> {
                path("/", () -> get(homepageController::redirectToHomePage));
                path("home", () -> get(homepageController::getHomepage));
                path("speeches", () -> {
                    get(speechController::getSessionsPage);
                    path("speechdetails", () -> get(speechController::getSpeechDetailsPage));
                    path("searchspeeches", () -> {
                        get(speechController::getSpeechSearch);
                        path("fetchspeeches", () -> get(speechController::getSearchSpeeches));
                    });
                });
                path("export", () -> {
                    path("sessions", () -> get(exportController::getSessionExports));
                    path("speaker", () -> get(exportController::getSpeakerExport));
                });
                path("speakers", () -> {
                    get(speakerController::getSpeakerList);
                    path("portfolio", () -> {
                        get(speakerController::getPortfolio);
                    });
                });
                path("nlpcharts", () -> {
                    get(nlpController::getNLPCharts);
                    path("sessionchange", () -> get(nlpController::getSpeechesForSession));
                    path("chartdata", () -> {
                        get(nlpController::getChartData);
                        path("fetchchartdataforallspeeches", () -> get(nlpController::getChartDataForAllSpeeches));
                    });
                });
            });
        });
        int port = serverProperties.getPort();
        this.app.start(port);
    }
}
