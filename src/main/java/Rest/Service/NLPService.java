package Rest.Service;

import Bundestag.Fractions.Int.FractionInt;
import Bundestag.Persons.Int.SpeakerInt;
import Bundestag.Session.Int.SessionInt;
import Database.MongoDBHandler;
import Rest.JSON.SpeakerJSON;
import Rest.JSON.SpeechJSON;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.http.Context;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service-Class for nlp-related api-requests.
 *
 * @author Amal
 */
public class NLPService {
    private MongoDBHandler mongoDBHandler;

    /**
     * Constructor
     * @param mongoDBHandler for database-connection
     *
     * @author Amal
     */
    public NLPService(MongoDBHandler mongoDBHandler) {
        this.mongoDBHandler = mongoDBHandler;
    }

    /**
     * Method for nlp-charts-page-rendering. Fetches all speeches for the first session as default, all sessions,
     * all speech-dates, fractions and speakers.
     * @param ctx
     *
     * @author Amal
     */
    public void renderNLPChartsPage (Context ctx){
        Map<String, Object> response = new HashMap<>();
        List<SpeechJSON> speeches = mongoDBHandler.getSpeechesBySession("1");
        List<SessionInt> sessions = mongoDBHandler.getSessions();
        List<String> dates = mongoDBHandler.getAllSpeechDates();
        List<FractionInt> fractions = mongoDBHandler.getFractions();
        List<SpeakerInt> speakers = mongoDBHandler.getAllSpeakers();
        List<SpeakerJSON> speakersJson = new ArrayList<>();

        for (SpeakerInt speaker : speakers) {
            // Create json-structured speaker-object as freemarker and javascript cannot handle cross-connected classes.
            speakersJson.add(new SpeakerJSON(speaker.getId(), speaker.getNameAndSurname(), speaker.getFraction().getShortName()));
        }
        ObjectMapper mapper = new ObjectMapper();
        String speakersJsonString = "";
        try {
            // Convert the json-object to a string so javascript can handle it.
            speakersJsonString = mapper.writeValueAsString(speakersJson);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        response.put("speakersJsonString", speakersJsonString);

        response.put("sessions", sessions);
        response.put("speeches", speeches);
        response.put("dates", dates);
        response.put("fractions", fractions);
        response.put("speakers", speakers);
        response.put("speakersJson", speakersJson);
        ctx.res().setCharacterEncoding("UTF-8");
        ctx.render("nlpcharts.ftl", response);
    }

    /**
     * Method for fetching the speeches of the selected session.
     * @param ctx
     *
     * @author Amal
     */
    public void fetchSpeechesForSession (Context ctx){
        String sessionId = ctx.queryParam("id");
        List<SpeechJSON> speeches = mongoDBHandler.getSpeechesBySession(sessionId);
        ctx.status(200);
        ctx.json(speeches);
    }

    /**
     * Method for fetching the chart-data (nlp-analysis) for speeches which correspond to the selected filters.
     * @param ctx
     *
     * @author Amal
     */
    public void fetchChartData(Context ctx){
        Map<String, Object> response = new HashMap<>();
        String speechIds = ctx.queryParam("speechids");
        String fractions = ctx.queryParam("fractions");
        String speakers = ctx.queryParam("speakers");
        String dates = ctx.queryParam("dates");
        List<String> speechIdsList;
        List<String> fractionsList = new ArrayList<>();
        List<String> speakersList = new ArrayList<>();
        List<String> datesList = new ArrayList<>();
        Map<String, Integer> posAmounts;
        Map<String, Double> topics;
        if (speechIds != null && !speechIds.isEmpty()) {
            speechIdsList = Arrays.asList(speechIds.split(","));
            posAmounts = mongoDBHandler.getPOSAmountsForSpeeches(speechIdsList);
            topics = mongoDBHandler.getTopicsForSpeeches(speechIdsList);
        } else {
            if (fractions != null) {fractionsList = Arrays.asList(fractions.split(","));}
            if (speakers != null) {speakersList = Arrays.asList(speakers.split(","));}
            if (dates != null) {datesList = Arrays.asList(dates.split(","));}
            posAmounts = mongoDBHandler.getPosAmounts(fractionsList, speakersList, datesList);
            topics = mongoDBHandler.getTopics(fractionsList, speakersList, datesList);
        }
        response.put("posAmounts", posAmounts);
        response.put("topics", topics);
        ctx.status(200).json(response);
    }

    /**
     * Method for fetching the chart-data (nlp-analysis) for all speeches.
     * @param ctx
     *
     * @author Amal
     */
    public void fetchChartDataForAllSpeeches(Context ctx){
        Map<String, Object> response = new HashMap<>();
        Map<String, Integer> posAmounts = mongoDBHandler.getAggregatedPOSAmounts();
        Map<String, Double> topics = mongoDBHandler.getAggregatedTopics();
        response.put("posAmounts", posAmounts);
        response.put("topics", topics);
        ctx.status(200).json(response);
    }
}
