package Bundestag.Session.Int;

import org.bson.Document;

/**
 * Interface for agenda. Stores all attributes of comment.
 */
public interface CommentInt {

    String getId();

    /**
     * @return index of comment.
     */
    int getIndex();

    /**
     * @return content of comment.
     */
    String getContent();

    String getSpeechId();
    String getCommenterId();

    Document toDocument();
}
