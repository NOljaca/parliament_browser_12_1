package Bundestag.Factory.Helper;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class XMLScraper {

    public XMLScraper() {}

    /**
     * Method for periodically scraping the bundestag-page for the protocol-xmls.
     * Scrapes the xmls every 1 hour.
     */
    public static void scrapeXMLsPeriodically() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        Runnable scrapingTask = () -> {
            scrapeAndDownload();
            downloadAndExtractStammdatenZip();
        };
        // Start first scrape in 1 hour and repeat periodically
        scheduler.scheduleAtFixedRate(scrapingTask, 1, 1, TimeUnit.HOURS);
    }

    /**
     * Method for scraping and downloading the protocol-xmls.
     */
    public static void scrapeAndDownload() {
        System.out.println("Scraping sessions");
        String baseAjaxUrl = "https://www.bundestag.de/ajax/filterlist/de/services/opendata/866354-866354";
        int limit = 10;
        int offset = 0;
        int totalProtocols = 0;

        // Create folder for xmls
        File outputDir = new File("src/main/resources/protocols");
        if (!outputDir.exists()) {
            if (outputDir.mkdirs()) {
                System.out.println("Ordner 'resources/protocols' wurde erstellt.");
            } else {
                System.err.println("Fehler beim Erstellen des Ordners 'resources/protocols'.");
                return;
            }
        }

        scrapeProtocolDtd(outputDir);

        boolean firstIteration = true;
        // Wiederhole, bis alle Protokolle verarbeitet sind.
        while (true) {
            String ajaxUrl = baseAjaxUrl + "?limit=" + limit + "&noFilterSet=true&offset=" + offset;
            System.out.println("Lade Inhalte von: " + ajaxUrl);
            Document ajaxDoc;
            try {
                ajaxDoc = Jsoup.connect(ajaxUrl).get();
            } catch (IOException e) {
                System.err.println("Fehler beim Abrufen von " + ajaxUrl + ": " + e.getMessage());
                break;
            }

            // For the first request we need every protocol.
            if (firstIteration) {
                Element metaSlider = ajaxDoc.selectFirst("div.meta-slider");
                if (metaSlider != null) {
                    try {
                        totalProtocols = Integer.parseInt(metaSlider.attr("data-hits"));
                        System.out.println("Gesamtanzahl Protokolle: " + totalProtocols);
                    } catch (NumberFormatException e) {
                        System.err.println("Fehler beim Parsen der Gesamtanzahl: " + e.getMessage());
                        break;
                    }
                } else {
                    System.err.println("meta-slider Element nicht gefunden.");
                    break;
                }
                firstIteration = false;
            }


            Elements xmlLinks = ajaxDoc.select("a[href$=.xml]");
            if (xmlLinks.isEmpty()) {
                System.out.println("Keine weiteren XML-Links gefunden bei offset " + offset);
                break;
            }

            for (Element link : xmlLinks) {
                String xmlUrl = link.absUrl("href");
                // Check for beginning of xml-file, it needs to start with 20 for the 20. wp.
                if (xmlUrl.matches(".*/20\\d+\\.xml$")) {
                    // Extract filename from url
                    String[] parts = xmlUrl.split("/");
                    String fileName = parts[parts.length - 1];
                    File outputFile = new File(outputDir, fileName);

                    // If file exists then skip
                    if (outputFile.exists()) {
                        System.out.println("Datei " + fileName + " existiert bereits. Überspringe Download.");
                        continue;
                    }

                    System.out.println("Lade XML: " + xmlUrl);
                    try {
                        Connection.Response response = Jsoup.connect(xmlUrl)
                                .ignoreContentType(true)
                                .execute();
                        byte[] bytes = response.bodyAsBytes();

                        try (OutputStream out = new FileOutputStream(outputFile)) {
                            out.write(bytes);
                        }
                        System.out.println("Gespeichert: " + outputFile.getAbsolutePath());
                    } catch (IOException e) {
                        System.err.println("Fehler beim Download von " + xmlUrl + ": " + e.getMessage());
                    }
                }
            }

            offset += limit;
            if (offset >= totalProtocols) {
                break;
            }
        }
    }

    /**
     * Scrapes and saves the dtd-file for the protocols as it is needed for parsing the xml files.
     * @param outputDir directory for saving the dtd-file
     */
    private static void scrapeProtocolDtd(File outputDir) {
        String dtdUrl = "http://www.bundestag.de/resource/blob/575720/100244acc72762143d8056e7d3190a96/dbtplenarprotokoll.dtd";
        String dtdFileName = "dbtplenarprotokoll.dtd";
        File dtdFile = new File(outputDir, dtdFileName);
        if (!dtdFile.exists()) {
            try {
                System.out.println("Lade DTD: " + dtdUrl);
                Connection.Response dtdResponse = Jsoup.connect(dtdUrl)
                        .ignoreContentType(true)
                        .execute();
                byte[] dtdBytes = dtdResponse.bodyAsBytes();
                try (OutputStream out = new FileOutputStream(dtdFile)) {
                    out.write(dtdBytes);
                }
                System.out.println("DTD gespeichert: " + dtdFile.getAbsolutePath());
            } catch (IOException e) {
                System.err.println("Fehler beim Download der DTD: " + e.getMessage());
            }
        } else {
            System.out.println("DTD-Datei existiert bereits. Überspringe Download.");
        }
    }

    /**
     * Method for downloading and extracting the zip file with the speaker-meta-data.
     */
    public static void downloadAndExtractStammdatenZip() {
        String zipUrl = "https://www.bundestag.de/resource/blob/472878/56d0514de78abb261d81ab940b9deed7/MdB-Stammdaten.zip";
        String targetDir = "src/main/resources/Mdb_Stammdaten";
        try {
            downloadAndExtractZip(zipUrl, targetDir);
        } catch (IOException e) {
            System.err.println("Fehler beim Download/Entpacken der Stammdaten-Zip: " + e.getMessage());
        }
    }

    /**
     * Helper method for downloading and extracting a zip-file
     * @param zipUrl url from where the zip-file needs to be downloaded
     * @param outputFolder url where the files should be extracted to
     * @throws IOException
     */
    private static void downloadAndExtractZip(String zipUrl, String outputFolder) throws IOException {
        System.out.println("Lade ZIP von: " + zipUrl);
        Connection.Response response = Jsoup.connect(zipUrl)
                .ignoreContentType(true)
                .execute();
        byte[] zipBytes = response.bodyAsBytes();

        // Create temporary zip-file
        File tempZip = File.createTempFile("MdBStammdaten", ".zip");
        try (FileOutputStream fos = new FileOutputStream(tempZip)) {
            fos.write(zipBytes);
        }
        System.out.println("ZIP-Datei heruntergeladen: " + tempZip.getAbsolutePath());

        // Create directory if it doesnt exist yet
        File destDir = new File(outputFolder);
        if (!destDir.exists()) {
            destDir.mkdirs();
        }
        unzip(tempZip, destDir);

        // Delete temporary zip-file
        if (tempZip.delete()) {
            System.out.println("Temporäre ZIP-Datei gelöscht.");
        } else {
            System.out.println("Temporäre ZIP-Datei konnte nicht gelöscht werden.");
        }
    }

    /**
     * Method for unzipping a zip-file.
     * @param zipFile zip-file which should be unzipped
     * @param destDir directory where the unzipped files should be saved
     * @throws IOException
     */
    public static void unzip(File zipFile, File destDir) throws IOException {
        byte[] buffer = new byte[1024];
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile))) {
            ZipEntry zipEntry = zis.getNextEntry();
            while (zipEntry != null) {
                File newFile = new File(destDir, zipEntry.getName());
                if (zipEntry.isDirectory()) {
                    if (!newFile.isDirectory() && !newFile.mkdirs()) {
                        throw new IOException("Failed to create directory " + newFile);
                    }
                } else {
                    File parent = newFile.getParentFile();
                    if (!parent.isDirectory() && !parent.mkdirs()) {
                        throw new IOException("Failed to create directory " + parent);
                    }
                    try (FileOutputStream fos = new FileOutputStream(newFile)) {
                        int len;
                        while ((len = zis.read(buffer)) > 0) {
                            fos.write(buffer, 0, len);
                        }
                    }
                }
                zis.closeEntry();
                zipEntry = zis.getNextEntry();
            }
        }
        System.out.println("ZIP-Datei entpackt nach: " + destDir.getAbsolutePath());
    }
}
