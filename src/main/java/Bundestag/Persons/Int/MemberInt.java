package Bundestag.Persons.Int;

import Bundestag.Fractions.Int.FractionInt;
import org.bson.Document;

import java.time.LocalDate;

/**
 * Interface for members. Stores all attributes of a member.
 */
public interface MemberInt {

    /**
     * @return id of member
     */
    String getId();

    /**
     * @return name of member
     */
    String getName();

    /**
     * @return surname of member
     */
    String getSurname();

    /**
     * @return age of member
     */
    int getAge();

    /**
     * @return academic title of member
     */
    String getAcademicTitle();

    /**
     * @return birthdate of member
     */
    LocalDate getBirthDate();

    /**
     * @return gender of member
     */
    String getGender();

    /**
     * @return profession of member
     */
    String getProfession();

    /**
     * @return fraction-instance of member
     */
    FractionInt getFraction();

    /**
     * Create Document with key-value pairs that match the attributes of member.
     * @return created document with the attributes in the specific fields.
     */
    Document toDocument();

    String getNameAndSurname();

    /**
     * @return HTML-snippet for this member for the homepage.
     */
    String toHTML();
}
