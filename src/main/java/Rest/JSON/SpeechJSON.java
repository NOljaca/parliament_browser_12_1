package Rest.JSON;

/**
 * Class for representing a speech as a JSON-object for javascript-compatibility
 * @author Amal
 */
public class SpeechJSON {
    private String sessionId;
    private String speaker;
    private String speechId;
    private String agendaId;
    private String speakerId;

    public SpeechJSON(String sessionId, String speaker, String speechId, String agendaId, String speakerId) {
        this.sessionId = sessionId;
        this.speaker = speaker;
        this.speechId = speechId;
        this.agendaId = agendaId;
        this.speakerId = speakerId;
    }

    public SpeechJSON(String id, String speakerId) {
        this.speakerId = speakerId;
        this.speechId = id;
    }

    public String getSpeakerId() {
        return speakerId;
    }

    public void setSpeakerId(String speakerId) {
        this.speakerId = speakerId;
    }

    public String getAgendaId() {
        return agendaId;
    }

    public void setAgendaId(String agendaId) {
        this.agendaId = agendaId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getSpeaker() {
        return speaker;
    }

    public void setSpeaker(String speaker) {
        this.speaker = speaker;
    }

    public String getSpeechId() {
        return speechId;
    }

    public void setSpeechId(String speechId) {
        this.speechId = speechId;
    }
}
