package Bundestag.Fractions.Impl;

import Bundestag.Fractions.Int.FractionInt;
import Bundestag.Persons.Impl.Member_File_Impl;
import Bundestag.Persons.Int.MemberInt;
import org.bson.Document;

import java.util.Set;

/**
 * Implementation of {@link FractionInt}. Stores attributes of fractions which are fetched from the xml-file.
 */
public class Fraction_File_Impl implements FractionInt {

    private String shortName; // As an id
    private String longName; // Name of fraction
    private Set<MemberInt> members; // All members of this fraction

    /**
     * Class for storing all attributes (from xml-file) of a fraction.
     * @param shortName short-name of fraction - used as an id.
     * @param longName name of fraction
     * @param members members of this fraction
     */
    public Fraction_File_Impl(String shortName, String longName, Set<MemberInt> members) {
        this.shortName = shortName;
        this.longName = longName;
        this.members = members;
    }

    @Override
    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    @Override
    public String getLongName() {
        return longName;
    }

    public void setLongName(String longName) {
        this.longName = longName;
    }

    @Override
    public Set<MemberInt> getMembers() {
        return members;
    }

    public void setMembers(Set<MemberInt> members) {
        this.members = members;
    }

    @Override
    public void addMember(MemberInt member) {
        this.members.add(member);
    }

    @Override
    public Document toDocument() {
        throw new UnsupportedOperationException("Not supported for file-class.");
    }
}
