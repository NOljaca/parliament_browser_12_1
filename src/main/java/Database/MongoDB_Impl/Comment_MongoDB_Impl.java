package Database.MongoDB_Impl;

import Bundestag.Session.Int.CommentInt;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;


/**
 * Implementation of CommentInt. Stores comment-attributes to map for the database.
 * @author Amal
 */
public class Comment_MongoDB_Impl implements CommentInt {

    private CommentInt comment;
    private String id;
    private MongoDatabase database;
    private MongoCollection<Document> collection;
    private Document filter;
    private Document commentDoc;

    public Comment_MongoDB_Impl(CommentInt comment) {
        this.comment = comment;
    }

    public Comment_MongoDB_Impl(MongoDatabase database, String id) {
        this.database = database;
        this.id = id;
        collection = database.getCollection("comments");
        filter = new Document("id", id);
    }

    public Comment_MongoDB_Impl(Document commentDoc, MongoDatabase database) {
        this.database = database;
        this.commentDoc = commentDoc;
        this.id = commentDoc.getString("id");
    }

    @Override
    public String getId() {
        if (commentDoc != null) {
            return commentDoc.getString("id");
        }
        return id;
    }

    @Override
    public int getIndex() {
        if (commentDoc != null) {
            return commentDoc.getInteger("index");
        }
        return collection.find(filter).first().getInteger("index");
    }

    @Override
    public String getContent() {
        if (commentDoc != null) {
            return commentDoc.getString("content");
        }
        return collection.find(filter).first().getString("content");
    }

    @Override
    public String getSpeechId() {
        if (commentDoc != null) {
            return commentDoc.getString("speechId");
        }
        return collection.find(filter).first().getString("speechId");
    }

    /**
     * Creates a document of a comment-object.
     * @return document mapped with comment-attributes.
     * @author Amal
     */
    public Document toDocument() {
        Document commentDocument = new Document();
        commentDocument.append("id", comment.getId())
                .append("index", comment.getIndex())
                .append("content", comment.getContent())
                .append("speechId", comment.getSpeechId());
        return commentDocument;
    }
}
