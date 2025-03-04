package Database.MongoDB_Impl;

import Bundestag.Session.Int.CommentInt;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class Comment_MongoDB_Impl implements CommentInt {

    private CommentInt comment;
    private String id;
    private MongoDatabase database;
    private MongoCollection<Document> collection;
    private Document filter;

    public Comment_MongoDB_Impl(CommentInt comment) {
        this.comment = comment;
    }

    public Comment_MongoDB_Impl(MongoDatabase database, String id) {
        this.database = database;
        this.id = id;
        collection = database.getCollection("comments");
        filter = new Document("id", id);
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public int getIndex() {
        return 0;
    }

    @Override
    public String getContent() {
        return "";
    }

    @Override
    public String getSpeechId() {
        return "";
    }

    @Override
    public String getCommenterId() {
        return "";
    }

    public Document toDocument() {
        Document commentDocument = new Document();
        commentDocument.append("id", comment.getId())
                .append("index", comment.getIndex())
                .append("content", comment.getContent())
                .append("speechId", comment.getSpeechId())
                .append("commenterId", comment.getCommenterId());
        return commentDocument;
    }
}
