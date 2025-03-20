package Bundestag.Persons.Impl;

import Bundestag.Fractions.Impl.Fraction_File_Impl;
import Bundestag.Fractions.Int.FractionInt;
import Bundestag.Persons.Int.MemberInt;
import org.bson.Document;

import java.time.LocalDate;

/**
 * Implementation of {@link MemberInt}. Stores all attributes of a member from the xml-file.
 */
public class Member_File_Impl implements MemberInt {

    private String id;
    private String name;
    private String surname;
    private int age;
    private String gender;
    private String academicTitle;
    private LocalDate birthDate;
    private String profession;
    private FractionInt fraction;

    /**
     * Class for storing all attributes (from xml-file) of a member.
     * @param id id of member
     * @param name name of member
     * @param surname surname of member
     * @param age age of member
     * @param gender gender of member
     * @param academicTitle academic title of member
     * @param birthDate birthdate of member
     * @param profession profession of member
     * @param fraction fraction of member
     */
    public Member_File_Impl(String id, String name, String surname, int age, String gender, String academicTitle, LocalDate birthDate, String profession, FractionInt fraction) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.age = age;
        this.gender = gender;
        this.academicTitle = academicTitle;
        this.birthDate = birthDate;
        this.profession = profession;
        this.fraction = fraction;
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    @Override
    public int getAge() {
        return age;
    }

    public void setAge() {
        this.age = LocalDate.now().getYear() - birthDate.getYear();
    }

    @Override
    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    @Override
    public String getAcademicTitle() {
        return academicTitle;
    }

    public void setAcademicTitle(String academicTitle) {
        this.academicTitle = academicTitle;
    }

    @Override
    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    @Override
    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    @Override
    public FractionInt getFraction() {
        return fraction;
    }

    @Override
    public String getNameAndSurname() {
        return getName() + " " + getSurname();
    }

    @Override
    public Document toDocument() {
        throw new UnsupportedOperationException("File-Class does not support Document operations!");
    }

    @Override
    public String toHTML() {
        return "Failed";
    }

    public void setFraction(Fraction_File_Impl fraction) {
        this.fraction = fraction;
    }
}
