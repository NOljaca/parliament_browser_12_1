package Rest.JSON;

/**
 * Class for representing a speaker as a JSON-object for javascript-compatibility
 * @author Amal
 */
public class SpeakerJSON {
    private String id;
    private String nameSurname;
    private String fractionName;

    public SpeakerJSON(String id, String nameSurname, String fractionName) {
        this.id = id;
        this.nameSurname = nameSurname;
        this.fractionName = fractionName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNameSurname() {
        return nameSurname;
    }

    public void setNameSurname(String nameSurname) {
        this.nameSurname = nameSurname;
    }

    public String getFractionName() {
        return fractionName;
    }

    public void setFractionName(String fractionName) {
        this.fractionName = fractionName;
    }
}
