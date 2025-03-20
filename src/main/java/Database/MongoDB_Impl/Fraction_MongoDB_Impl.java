package Database.MongoDB_Impl;

import Bundestag.Fractions.Int.FractionInt;
import Bundestag.Persons.Int.MemberInt;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Implementation of FractionInt. Stores fraction-attributes to map for the database.
 * @author Amal
 */
public class Fraction_MongoDB_Impl implements FractionInt {
    private FractionInt fraction;
    private MongoCollection<Document> collection;
    private String shortName;
    private Document filter;
    private MongoDatabase database;
    private Document fractionDoc;

    public Fraction_MongoDB_Impl(FractionInt fraction) {
        this.fraction = fraction;
    }

    public Fraction_MongoDB_Impl(MongoDatabase database, String name) {
        this.database = database;
        collection = database.getCollection("fractions");
        this.shortName = name;
        filter = new Document("shortName", name);
        this.fractionDoc = collection.find(filter).first();
    }

    public Fraction_MongoDB_Impl(MongoDatabase database, Document fractionDoc) {
        this.database = database;
        this.fractionDoc = fractionDoc;
    }

    @Override
    public String getShortName() {
        return fractionDoc.getString("shortName");
    }

    @Override
    public String getLongName() {
        return fractionDoc.getString("longName");
    }

    @Override
    public Set<MemberInt> getMembers() {
        return Set.of();
    }

    @Override
    public void addMember(MemberInt member) {
    }

    /**
     * Creates a document of a fraction-object.
     * @return document mapped with fraction-attributes.
     * @author Amal
     */
    @Override
    public Document toDocument() {
        Document fractionDocument = new Document();
        List<String> memberIds = new ArrayList<>();
        for (MemberInt member : fraction.getMembers()){
            memberIds.add(member.getId());
        }
        return fractionDocument.append("shortName", fraction.getShortName())
                .append("longName", fraction.getLongName())
                .append("abgeordnete", memberIds);

    }
}
