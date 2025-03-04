package Database.MongoDB_Impl;

import Bundestag.Fractions.Int.FractionInt;
import Bundestag.Persons.Int.MemberInt;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Fraction_MongoDB_Impl implements FractionInt {
    private FractionInt fraction;
    private MongoCollection<Document> collection;
    private String shortName;
    private Document filter;
    private MongoDatabase database;

    public Fraction_MongoDB_Impl(FractionInt fraction) {
        this.fraction = fraction;
    }

    public Fraction_MongoDB_Impl(MongoDatabase database, String name) {
        this.database = database;
        collection = database.getCollection("fractions");
        this.shortName = name;
        filter = new Document("shortName", name);
    }

    @Override
    public String getShortName() {
        return "";
    }

    @Override
    public String getLongName() {
        return "";
    }

    @Override
    public Set<MemberInt> getMembers() {
        return Set.of();
    }

    @Override
    public void addMember(MemberInt member) {

    }

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
