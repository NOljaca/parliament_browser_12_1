package Bundestag.Session.Impl;

import Bundestag.Persons.Impl.Speaker_File_Impl;
import Bundestag.Persons.Int.SpeakerInt;
import Bundestag.Session.Int.AgendaInt;
import Bundestag.Session.Int.CommentInt;
import Bundestag.Session.Int.SessionInt;
import Bundestag.Session.Int.SpeechInt;
import org.apache.uima.jcas.JCas;
import org.bson.Document;

import java.util.List;

/**
 * Implementation of {@link SpeechInt}. Stores all attributes of a speech from the xml-file.
 */
public class Speech_File_Impl implements SpeechInt {

    private String id;
    private SpeakerInt speaker;
    private String content;
    private List<CommentInt> comments;
    private SessionInt session;
    private AgendaInt agenda;

    /**
     * Class for storing all attributes (from xml-file) of a speech.
     * @param id id of speech
     * @param speaker speaker of speech
     * @param content content of speech
     * @param comments comments of speech
     * @param session session of speech
     * @param agenda agenda of speech
     */
    public Speech_File_Impl(String id, SpeakerInt speaker, String content, List<CommentInt> comments, SessionInt session, AgendaInt agenda) {
        this.id = id;
        this.speaker = speaker;
        this.content = content;
        this.comments = comments;
        this.session = session;
        this.agenda = agenda;
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public SpeakerInt getSpeaker() {
        return speaker;
    }

    public void setSpeaker(Speaker_File_Impl speaker) {
        this.speaker = speaker;
    }

    @Override
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public List<CommentInt> getComments() {
        return comments;
    }

    public void setComments(List<CommentInt> comments) {
        this.comments = comments;
    }

    @Override
    public SessionInt getSession() {
        return session;
    }

    public void setSession(SessionInt session) {
        this.session = session;
    }

    @Override
    public AgendaInt getAgenda() {
        return agenda;
    }

    @Override
    public Document toDocument() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getSpeakerId() {
        return getSpeaker().getId();
    }

    @Override
    public JCas toCas() {
        return null;
    }

    public void setAgenda(AgendaInt agenda) {
        this.agenda = agenda;
    }

    public void addComment(Comment_File_Impl comment) {
        comments.add(comment);
    }
}
