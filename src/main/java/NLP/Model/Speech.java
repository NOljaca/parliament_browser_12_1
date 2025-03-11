package NLP.Model;

/**
 * Reine Datenklasse (Model) für eine Rede.
 * Hält ID, Speaker und den (ggf. zusammengefügten) Text.
 * Enthält KEINE UIMA-Logik!
 */
public class Speech {

    private String id;
    private String text;

    public Speech() {
        // no-args
    }

    public Speech(String id, String text) {
        this.id = id;
        this.text = text;
    }

    // Getter/Setter

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;

    }
}
