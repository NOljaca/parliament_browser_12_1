import Bundestag.Factory.Helper.XMLScraper;
import Bundestag.Factory.Impl.BundestagFactory;
import Bundestag.Factory.Int.BundestagFactoryInt;

public class Main {

    public static void main(String[] args) throws Exception {
        BundestagFactory bundestagFactory = new BundestagFactory();
        bundestagFactory.createBundestag();
        System.out.println(bundestagFactory.getMemberMap());
    }
}
