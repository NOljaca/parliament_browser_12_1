package Helper;

import Bundestag.Persons.Int.SpeakerInt;
import Database.MongoDBHandler;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * Class for fetching the image-urls for the database and also downloading the images for the pdf-generation.
 * @author Muhammed
 */
public class PictureExtractor {
    private MongoDBHandler mongoDBHandler;

    public PictureExtractor(MongoDBHandler mongoDBHandler) {
        this.mongoDBHandler = mongoDBHandler;
    }

    /**
     * Fetches the picture-url for given speaker.
     * @param id speaker-id
     * @return picture-url of speaker
     * @throws IOException
     */
    public String getPictureUrl(String id) throws IOException {
        String pictureUrl = getPictureUrlForSpeaker(id);
        Document document = Jsoup.connect(pictureUrl).get();
        Element imgDiv = document.select("div.rowGridContainer div.item").first();
        if (imgDiv != null) {
            Element imgA = imgDiv.select("a").first();
            if (imgA != null) {
                Element img = imgA.select("img").first();
                if (img != null) {
                    return "https://bilddatenbank.bundestag.de" + img.attr("src");
                } else {
                    return "https://www.google.com/url?sa=i&url=https%3A%2F%2Fcommons.wikimedia.org%2Fwiki%2FFile%3APortrait_Placeholder.png&psig=AOvVaw0K6kGr_kD2h6r06yhwL7AK&ust=1742730517835000&source=images&cd=vfe&opi=89978449&ved=0CBEQjRxqFwoTCLDS5p3PnYwDFQAAAAAdAAAAABAE";
                }
            } else {
                return "https://www.google.com/url?sa=i&url=https%3A%2F%2Fcommons.wikimedia.org%2Fwiki%2FFile%3APortrait_Placeholder.png&psig=AOvVaw0K6kGr_kD2h6r06yhwL7AK&ust=1742730517835000&source=images&cd=vfe&opi=89978449&ved=0CBEQjRxqFwoTCLDS5p3PnYwDFQAAAAAdAAAAABAE";
            }
        } else {
            return "https://www.google.com/url?sa=i&url=https%3A%2F%2Fcommons.wikimedia.org%2Fwiki%2FFile%3APortrait_Placeholder.png&psig=AOvVaw0K6kGr_kD2h6r06yhwL7AK&ust=1742730517835000&source=images&cd=vfe&opi=89978449&ved=0CBEQjRxqFwoTCLDS5p3PnYwDFQAAAAAdAAAAABAE";
        }
    }

    /**
     * Puts the speaker name + surname into the query of the url.
     * @param id
     * @return
     */
    private String getPictureUrlForSpeaker(String id) {
        String speakerNameSurname = getSpeakerNameSurname(id);
        return "https://bilddatenbank.bundestag.de/search/picture-result?query="+speakerNameSurname+"&sortVal=3";
    }

    /**
     * Fetches speaker name and surname and puts it into the query format
     * @param id speaker id
     * @return speaker-name + speaker-surname
     */
    private String getSpeakerNameSurname(String id) {
        SpeakerInt speaker = mongoDBHandler.getSpeakerByID(id);
        String speakerName = speaker.getName();
        String speakerSurname = speaker.getSurname();
        return speakerName + "+" + speakerSurname;
    }

    /**
     * Downloads picture from picture-url for the pdf-generation.
     * @param pictureUrl picture-url
     * @param id speaker-id
     * @throws IOException
     */
    public static void downloadPicture(String pictureUrl, String id) throws IOException {
        String fileName = "src/main/resources/public/static/tex_output/"+id+".jpg";
        try (InputStream in = new URL(pictureUrl).openStream()) {
            Files.copy(in, Paths.get(fileName), StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Image downloaded and saved as " + fileName);
        } catch (IOException e) {
            System.err.println("Error downloading the image: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
