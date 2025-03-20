package Bundestag.Fractions.Int;

import Bundestag.Persons.Impl.Member_File_Impl;
import Bundestag.Persons.Int.MemberInt;
import org.bson.Document;

import java.util.Set;

/**
 * Fraction interface - To store fraction-attributes.
 * @author Muhammed
 */
public interface FractionInt {

    /**
     * @return short name of fraction
     */
    String getShortName();

    /**
     * @return long name of fraction
     */
    String getLongName();

    /**
     * @return set with instances of {@link MemberInt}
     */
    Set<MemberInt> getMembers();

    /**
     * Add given member to the existing set of members.
     * @param member instance of {@link MemberInt} which should be added to the existing set.
     */
    void addMember(MemberInt member);

    /**
     * Create Document with key-value pairs that match the attributes of speaker.
     * @return created document with the attributes in the specific fields.
     */
    Document toDocument();
}
