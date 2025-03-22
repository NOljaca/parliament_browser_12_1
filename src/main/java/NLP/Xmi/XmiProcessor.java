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

    private static final String REPO_URL = "https://ppr.gitlab.texttechnologylab.org/abrami/materialienabschlussprojekt/-/raw/main/Reden/reden/20/";
    private static final String OUTPUT_DIR = "./serialized/";
    private static final String COLLECTION_NAME = "casData";
    private static final CookieManager cookieManager = new CookieManager();
    private static MongoDBHandler mongoDBHandler;
    private static MongoCollection<Document> casDataColl;

    public static void startProcess() {
        Scanner scanner = new Scanner(System.in);
        setCookies(scanner); // Benutzer zur Eingabe der Cookies auffordern

        while (true) {
            try {
                runNLPProcess();
                break; // Erfolgreich durchlaufen, beende die Schleife
            } catch (MongoSocketReadException e) {
                System.err.println("MongoDB-Verbindungsfehler: " + e.getMessage());
                System.out.println("Starte den Prozess neu...");
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
        }
        scanner.close();
    }

    private static void runNLPProcess() throws Exception {
        establishMongoConnection();
        Path outputDirPath = Paths.get(OUTPUT_DIR);
        if (Files.notExists(outputDirPath)) {
            Files.createDirectories(outputDirPath);
            System.out.println("Created directory: " + OUTPUT_DIR);
        }

        for (int id = 2010000000; id <= 2099999999; id += 100) {
            String speechId = "ID" + id;
            String fileUrl = REPO_URL + speechId + ".xmi.gz";
            String outputFile = OUTPUT_DIR + speechId + ".xmi";

            if (new File(outputFile).exists()) {
                System.out.println("Skipping " + speechId + ": File already exists.");
                continue;
            }

            int retryCount = 0;
            final int maxRetries = 3;
            boolean success = false;

            while (retryCount < maxRetries && !success) {
                try {
                    downloadFileWithCookies(fileUrl, outputFile);
                    System.out.println("Processed: " + speechId);

                    Document casData = XmiParser.parseXmiToDocument(outputFile, speechId);
                    if (casData != null && !existsInDatabase(speechId)) {
                        casDataColl.insertOne(casData);
                        System.out.println("Inserted into DB: " + speechId);
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

    private static void establishMongoConnection() throws IOException {
        mongoDBHandler = new MongoDBHandler();
        casDataColl = mongoDBHandler.getDatabase().getCollection(COLLECTION_NAME);
    }

    /**
     * Fordert den Benutzer zur Eingabe der Cookies auf und speichert sie im CookieManager.
     *
     * @param scanner Ein Scanner-Objekt zur Benutzereingabe.
     */
    private static void setCookies(Scanner scanner) {
        Map<String, String> cookies = new HashMap<>();

        System.out.println("Bitte geben Sie die erforderlichen Cookies ein:");

        System.out.print("_gitlab_session: ");
        cookies.put("_gitlab_session", scanner.nextLine());

        System.out.print("known_sign_in: ");
        cookies.put("known_sign_in", scanner.nextLine());

        System.out.print("preferred_language: ");
        cookies.put("preferred_language", scanner.nextLine());

        cookies.forEach((name, value) -> {
            HttpCookie cookie = new HttpCookie(name, value);
            cookieManager.getCookieStore().add(null, cookie);
        });

        System.out.println("Cookies erfolgreich gesetzt.");
    }

    private static void downloadFileWithCookies(String fileUrl, String outputFile) throws IOException {
        URL url = new URL(fileUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        List<HttpCookie> cookies = cookieManager.getCookieStore().getCookies();
        if (!cookies.isEmpty()) {
            String cookieString = cookies.stream()
                    .map(cookie -> cookie.getName() + "=" + cookie.getValue())
                    .reduce((cookie1, cookie2) -> cookie1 + "; " + cookie2)
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
            System.out.println("Download abgeschlossen: " + outputFile);
        } else {
            throw new FileNotFoundException("File not found: " + fileUrl);
        }

        connection.disconnect();
    }

    private static boolean existsInDatabase(String speechId) {
        return casDataColl.find(Filters.eq("speechId", speechId)).first() != null;
    }
}















