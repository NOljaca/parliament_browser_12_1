package Bundestag.Session.Impl;

import Bundestag.Session.Int.CommentInt;
import org.bson.Document;

/**
 * Implementation of {@link CommentInt}. Stores all attributes of a comment from the xml-file.
 */
public class Comment_File_Impl implements CommentInt {

    String id;
    int index;
    String content;
    String speechId;
    String commenterId;

    /**
     * Class for storing all attributes (from xml-file) of a comment.
     * @param index index of comment
     * @param content content of comment
     */
    public Comment_File_Impl(String id, int index, String content, String speechId) {
        this.id = id;
        this.index = index;
        this.content = content;
        this.speechId = speechId;
        commenterId = "";
    }

    public Comment_File_Impl(String id, int index, String content, String speechId, String commenterId) {
        this.id = id;
        this.index = index;
        this.content = content;
        this.speechId = speechId;
        this.commenterId = commenterId;
    }

    public String getSpeechId() {
        return speechId;
    }

    @Override
    public Document toDocument() {
        return null;
    }

    public void setSpeechId(String speechId) {
        this.speechId = speechId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
