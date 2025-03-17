package NLP.DataNLP;


import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.texttechnologylab.DockerUnifiedUIMAInterface.DUUIComposer;
import org.texttechnologylab.DockerUnifiedUIMAInterface.driver.DUUIDockerDriver;
import org.texttechnologylab.DockerUnifiedUIMAInterface.lua.DUUILuaContext;
import org.xml.sax.SAXException;
import org.apache.uima.UIMAException;
import java.io.IOException;
import java.util.List;


/**
 * Diese Klasse kapselt die NLP-Pipeline via DUUI (DockerDriver) und erweitert
 * die Extraktion um zusätzliche linguistische Merkmale:
 * - Sätze inkl. Sentiment
 * - Tokens
 * - POS-Tags
 * - Dependencies
 * - Named Entities (NER)
 *
 * Darüber hinaus wird in der Methode buildAnalysisDocument ein strukturiertes BSON‑Document erzeugt,
 * das alle extrahierten Ergebnisse enthält. Neben den Listen der einzelnen Elemente wird auch eine
 * Zählung der Named Entities (z. B. PER, LOC, ORG, MISC) als Unterdokument gespeichert.
 */



public class Initialize {

    private static final Logger logger = LogManager.getLogger(Initialize.class);
    private DUUIComposer composer;
    private DUUIDockerDriver dockerDriver;
    private int workers = 1; // Anzahl paralleler Worker (Threads)

    /**
     * Konstruktor: Initialisiert den DUUIComposer, DockerDriver und fügt die spaCy- und GerVader-Komponenten hinzu.
     */
    public Initialize() throws Exception {
        initComposer();
        initDockerDriver();
        addSpacyComponent();
        addGerVaderComponent();
        addParlBERTComponent();

    }

    /**
     * Fügt der Pipeline die ParlBERT-Komponente für die Topic-Analyse hinzu.
     */
    private void addParlBERTComponent() throws Exception {
        DUUIDockerDriver.Component parlBERT = new DUUIDockerDriver.Component(
                "docker.texttechnologylab.org/parlbert-topic-german:latest"
        )
                .withImageFetching()
                .withScale(workers);
        this.composer.add(parlBERT.build());
    }


    private void initComposer() throws Exception {
        DUUILuaContext ctx = new DUUILuaContext().withJsonLibrary();
        this.composer = new DUUIComposer()
                .withSkipVerification(true)
                .withLuaContext(ctx)
                .withWorkers(workers);
    }

    private void initDockerDriver() throws IOException, UIMAException, SAXException {
        this.dockerDriver = new DUUIDockerDriver();
        this.composer.addDriver(this.dockerDriver);
    }

    /**
     * Fügt der Pipeline die spaCy-Komponente hinzu, welche für Tokenisierung, Satzsegmentierung,
     * POS-Tagging, Dependency Parsing und Named Entity Recognition zuständig ist.
     */
    private void addSpacyComponent() throws Exception {
        DUUIDockerDriver.Component spacy = new DUUIDockerDriver.Component(
                "docker.texttechnologylab.org/textimager-duui-spacy-single-de_core_news_sm:0.1.4"
        )
                .withImageFetching()
                .withScale(workers);
        this.composer.add(spacy.build());
    }

    /**
     * Fügt der Pipeline die GerVader-Komponente für die Sentiment-Analyse (Deutsch) hinzu.
     */
    private void addGerVaderComponent() throws Exception {
        DUUIDockerDriver.Component gervader = new DUUIDockerDriver.Component(
                "docker.texttechnologylab.org/gervader_duui:1.0.2"
        )
                .withParameter("selection", "text")
                .withImageFetching()
                .withScale(workers);
        this.composer.add(gervader.build());
    }

    public void processJCas(JCas jcas) throws Exception {
        try {
            logger.info("Starte die Verarbeitung des JCAS...");
            this.composer.run(jcas);
            logger.info("Verarbeitung des JCAS abgeschlossen.");

            // Überprüfe, ob nach der Verarbeitung Annotationen vorhanden sind
            List<Sentence> sentences = (List<Sentence>) JCasUtil.select(jcas, Sentence.class);
            if (sentences.isEmpty()) {
                logger.warn("Keine Sätze im JCAS nach der Verarbeitung gefunden.");
            } else {
                logger.info("Anzahl der Sätze nach der Verarbeitung: " + sentences.size());
            }

            List<Token> tokens = (List<Token>) JCasUtil.select(jcas, Token.class);
            if (tokens.isEmpty()) {
                logger.warn("Keine Tokens im JCAS nach der Verarbeitung gefunden.");
            } else {
                logger.info("Anzahl der Tokens nach der Verarbeitung: " + tokens.size());
            }

        } catch (Exception e) {
            logger.error("Fehler bei der Verarbeitung des JCAS: " + e.getMessage(), e);
            throw e;
        }
    }

}
