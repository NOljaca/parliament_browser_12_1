import Bundestag.Factory.Impl.BundestagFactory;
import Database.MongoDBHandler;
import NLP.NLPStructureBuilder;
import Rest.RESTHandler;

import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String[] args) throws Exception {
        BundestagFactory bundestagFactory = new BundestagFactory();
        bundestagFactory.createBundestag();
        MongoDBHandler mongoDBHandler = new MongoDBHandler();
        mongoDBHandler.createAgendas(bundestagFactory.getAgendaList());
        mongoDBHandler.createComments(bundestagFactory.getCommentList());
        mongoDBHandler.createFractions(bundestagFactory.getFractionMap());
        mongoDBHandler.createSessions(bundestagFactory.getSessionMap());
        mongoDBHandler.createSpeakers(bundestagFactory.getSpeakerMap());
        mongoDBHandler.createMembers(bundestagFactory.getMemberMap());
        mongoDBHandler.createSpeeches(bundestagFactory.getSpeechList());
        mongoDBHandler.putSpeakerPictures();
        NLPStructureBuilder.runNLPAnalysis();
        RESTHandler restHandler = new RESTHandler(mongoDBHandler);
    }
}
