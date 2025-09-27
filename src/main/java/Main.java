import Bundestag.Factory.Impl.BundestagFactory;
import Database.MongoDBHandler;
import NLP.NLPStructureBuilder;
import Rest.RESTHandler;

import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String[] args) throws Exception {
        NLPStructureBuilder.runNLPAnalysis();
    }
}
