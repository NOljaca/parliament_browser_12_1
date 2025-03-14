package NLP.Xmi;

import Database.MongoDBHandler;
import com.mongodb.MongoTimeoutException;
import com.mongodb.MongoSocketReadException;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.*;
import java.util.zip.GZIPInputStream;

/**
 * Die Klasse XmiProcessor verarbeitet XMI-Dateien, die von einem externen GitLab-Repository heruntergeladen werden.
 * Sie speichert die verarbeiteten Daten in einer MongoDB-Datenbank und serialisiert sie lokal.
 */
public class XmiProcessor {

    /**
     * Die URL des GitLab-Repositories, von dem die XMI-Dateien heruntergeladen werden.
     */
    private static final String REPO_URL = "https://ppr.gitlab.texttechnologylab.org/abrami/materialienabschlussprojekt/-/raw/main/Reden/reden/20/";

    /**
     * Der Verzeichnis-Pfad, in dem die heruntergeladenen und serialisierten Dateien gespeichert werden.
     */
    private static final String OUTPUT_DIR = "./serialized/";

    /**
     * Der Name der MongoDB-Sammlung, in der die verarbeiteten Daten gespeichert werden.
     */
    private static final String COLLECTION_NAME = "casData";

    /**
     * Ein CookieManager zum Verwalten von Cookies für HTTP-Anfragen.
     */
    private static final CookieManager cookieManager = new CookieManager();

    /**
     * Eine Instanz des MongoDB-Handlers, der die Verbindung zur Datenbank verwaltet.
     */
    private static MongoDBHandler mongoDBHandler;

    /**
     * Die MongoDB-Sammlung, in der die Cas-Daten gespeichert werden.
     */
    private static MongoCollection<Document> casDataColl;

    /**
     * Der Einstiegspunkt des Programms, das den gesamten NLP-Prozess steuert.
     * Es wird in einer Schleife ausgeführt, bis der Prozess erfolgreich abgeschlossen wurde.
     * Bei einem MongoDB-Verbindungsfehler wird der Prozess nach einer Wartezeit erneut gestartet.
     *
     * @param args Kommandozeilenargumente
     */
    public static void main(String[] args) {
        while (true) {
            try {
                runNLPProcess();
                break; // Erfolgreich durchlaufen, beende die Schleife
            } catch (MongoSocketReadException e) {
                System.err.println("MongoDB-Verbindungsfehler: " + e.getMessage());
                System.out.println("Starte den Prozess neu...");
                try {
                    Thread.sleep(5000); // Warte 5 Sekunden, bevor neu gestartet wird
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            } catch (Exception e) {
                e.printStackTrace();
                break; // Brich bei anderen Fehlern ab
            }
        }
    }

    /**
     * Führt den NLP-Prozess aus: Verbindet sich mit der MongoDB, lädt XMI-Dateien herunter,
     * parst sie und speichert die resultierenden Daten in der Datenbank.
     *
     * @throws Exception Wenn beim Herunterladen, Verarbeiten oder Speichern der Daten ein Fehler auftritt.
     */
    private static void runNLPProcess() throws Exception {
        establishMongoConnection();

        // Überprüft, ob das Ausgabeverzeichnis existiert, andernfalls wird es erstellt
        Path outputDirPath = Paths.get(OUTPUT_DIR);
        if (Files.notExists(outputDirPath)) {
            Files.createDirectories(outputDirPath);
            System.out.println("Created directory: " + OUTPUT_DIR);
        } else {
            System.out.println("Directory already exists: " + OUTPUT_DIR);
        }

        // Setzt die Cookies für die HTTP-Anfragen
        setCookies();

        // Verarbeitet eine Reihe von XMI-Dateien
        for (int id = 20000000; id <= 209999999; id += 100) {
            String speechId = "ID" + id;
            String fileUrl = REPO_URL + speechId + ".xmi.gz";
            String outputFile = OUTPUT_DIR + speechId + ".xmi";

            // Überspringt die Datei, wenn sie bereits existiert
            File serializedFile = new File(outputFile);
            if (serializedFile.exists()) {
                System.out.println("Skipping " + speechId + ": File already exists in serialized folder.");
                continue;
            }

            int retryCount = 0;
            final int maxRetries = 3;
            boolean success = false;

            // Versucht, die Datei herunterzuladen und zu verarbeiten, bis der maximale Retry-Wert erreicht ist
            while (retryCount < maxRetries && !success) {
                try {
                    downloadFileWithCookies(fileUrl, outputFile);
                    System.out.println("Processed: " + speechId);

                    Document casData = XmiParser.parseXmiToDocument(outputFile, speechId);
                    if (casData != null) {
                        if (!existsInDatabase(speechId)) {
                            casDataColl.insertOne(casData);
                            System.out.println("Inserted into DB: " + speechId);
                        } else {
                            System.out.println("SpeechId already exists in DB: " + speechId);
                        }
                    }
                    success = true;
                } catch (MongoTimeoutException e) {
                    System.err.println("MongoDB timeout for " + speechId + ": " + e.getMessage());
                    retryCount++;
                    establishMongoConnection();
                } catch (Exception e) {
                    System.err.println("Failed to process " + speechId + ": " + e.getMessage());
                    break;
                }
            }
        }
    }

    /**
     * Stellt eine Verbindung zur MongoDB-Datenbank her und initialisiert die Sammlung.
     *
     * @throws IOException Wenn die Verbindung zur MongoDB fehlschlägt.
     */
    private static void establishMongoConnection() throws IOException {
        mongoDBHandler = new MongoDBHandler();
        casDataColl = mongoDBHandler.getDatabase().getCollection("casData");
    }

    /**
     * Setzt die Cookies für HTTP-Anfragen, die zur Authentifizierung und zum Speichern von Sitzungsinformationen erforderlich sind.
     */
    private static void setCookies() {
        Map<String, String> cookies = new HashMap<>();
        cookies.put("_gitlab_session", "a915ac47e129067759ba2efa6b8b14dc");
        cookies.put("known_sign_in", "alNWM2NnVVZyVmlhSWpMbzlEL2xvNGpZT2tlZ2xKVHhITHFDV0N2bDhOR3V2emxlSkNVVmc1NDVEYXV5c1hQYkZ1QjNkbVVBQ0dvcnRqYldVVmlGYkpLOFl1cFl4RS9Qd1pmc0g5Z1ErVlBUVlZhVGVZZmZ4TlI0OTdhd0ZSR2wtLUdZc1g0L3JaQ1ZGTXRZaWd5dXlzbUE9PQ%3D%3D--1a4eb1693cade0bb57b0850f59f74ffdb6e839d7");
        cookies.put("preferred_language", "de");

        cookies.forEach((name, value) -> {
            HttpCookie cookie = new HttpCookie(name, value);
            cookieManager.getCookieStore().add(null, cookie);
        });
    }

    /**
     * Lädt eine XMI-Datei von der angegebenen URL herunter und speichert sie im angegebenen Verzeichnis.
     * Dabei werden Cookies für die Anfrage gesetzt.
     *
     * @param fileUrl Die URL der herunterzuladenden XMI-Datei.
     * @param outputFile Der Pfad zur Ausgabedatei, in der die heruntergeladene Datei gespeichert wird.
     * @throws IOException Wenn ein Fehler beim Herunterladen oder Speichern der Datei auftritt.
     */
    private static void downloadFileWithCookies(String fileUrl, String outputFile) throws IOException {
        URL url = new URL(fileUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        List<HttpCookie> cookies = cookieManager.getCookieStore().getCookies();
        if (!cookies.isEmpty()) {
            String cookieString = cookies.stream()
                    .map(cookie -> cookie.getName() + "=" + cookie.getValue())
                    .reduce((cookie1, cookie2) -> cookie1 + ";" + cookie2)
                    .orElse("");
            connection.setRequestProperty("Cookie", cookieString);
        }

        connection.setInstanceFollowRedirects(false);

        int responseCode = connection.getResponseCode();
        System.out.println("HTTP Response Code: " + responseCode);

        if (responseCode == HttpURLConnection.HTTP_MOVED_TEMP || responseCode == HttpURLConnection.HTTP_MOVED_PERM) {
            String redirectUrl = connection.getHeaderField("Location");
            System.out.println("Redirected to: " + redirectUrl);
            connection = (HttpURLConnection) new URL(redirectUrl).openConnection();
            connection.setRequestMethod("GET");
        }

        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            try (InputStream in = new GZIPInputStream(connection.getInputStream());
                 FileOutputStream out = new FileOutputStream(outputFile)) {
                byte[] buffer = new byte[1024];
                int len;
                while ((len = in.read(buffer)) > 0) {
                    out.write(buffer, 0, len);
                }
            }
        } else {
            throw new FileNotFoundException("File not found: " + fileUrl);
        }

        connection.disconnect();
    }

    private static boolean existsInDatabase(String speechId) {
        return casDataColl.find(Filters.eq("speechId", speechId)).first() != null;
    }
}














