package Database.MongoDB_Impl;

import Bundestag.Fractions.Int.FractionInt;
import Bundestag.Persons.Int.MemberInt;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.time.LocalDate;

/**
 * Implementation of MemberInt. Stores member-attributes to map for the database.
 * @author Amal
 */
public class Member_MongoDB_Impl implements MemberInt {
    private MemberInt member;
    private Document document;
    private MongoDatabase database;
    private MongoCollection<Document> collection;
    private String id;
    private Document filter;

    public Member_MongoDB_Impl(MemberInt member) {
        this.member = member;
    }

    public Member_MongoDB_Impl(MongoDatabase database, String id) {
        this.database = database;
        this.id = id;
        collection = database.getCollection("members");
        filter = new Document("id", id);
    }

    @Override
    public String getId() {
        return "";
    }

    @Override
    public String getName() {
        return "";
    }

    @Override
    public String getSurname() {
        return "";
    }

    @Override
    public int getAge() {
        return 0;
    }

    @Override
    public String getAcademicTitle() {
        return "";
    }

    @Override
    public LocalDate getBirthDate() {
        return null;
    }

    @Override
    public String getGender() {
        return "";
    }

    @Override
    public String getProfession() {
        return "";
    }

    @Override
    public FractionInt getFraction() {
        return null;
    }

    @Override
    public String getNameAndSurname() {
        return getName() + " " + getSurname();
    }

    /**
     * Creates a document of a member-object.
     * @return document mapped with member-attributes.
     * @author Amal
     */
    @Override
    public Document toDocument() {
        Document memberDocument = new Document();
        return memberDocument.append("id", member.getId())
                .append("name", member.getName())
                .append("surname", member.getSurname())
                .append("age", member.getAge())
                .append("gender", member.getGender())
                .append("academicTitle", member.getAcademicTitle())
                .append("birthDate", member.getBirthDate())
                .append("profession", member.getProfession())
                .append("fraction", member.getFraction().getShortName());

    }

    @Override
    public String toHTML() {
        return "";
    }
}
