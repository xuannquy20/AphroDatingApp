package com.projectd.aphroapp.model;

import java.io.Serializable;

public class User implements Serializable {
    private String id, name, gender, day, month, year, city, district, ward, description, genderFinding, image;

    public User(){}

    public User(String id, String name, String gender, String day, String month, String year, String city, String district, String ward, String description, String genderFinding, String image) {
        this.id = id;
        this.name = name;
        this.gender = gender;
        this.day = day;
        this.month = month;
        this.year = year;
        this.city = city;
        this.district = district;
        this.ward = ward;
        this.description = description;
        this.genderFinding = genderFinding;
        this.image = image;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getWard() {
        return ward;
    }

    public void setWard(String ward) {
        this.ward = ward;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getGenderFinding() {
        return genderFinding;
    }

    public void setGenderFinding(String genderFinding) {
        this.genderFinding = genderFinding;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", gender='" + gender + '\'' +
                ", day='" + day + '\'' +
                ", month='" + month + '\'' +
                ", year='" + year + '\'' +
                ", city='" + city + '\'' +
                ", district='" + district + '\'' +
                ", ward='" + ward + '\'' +
                ", description='" + description + '\'' +
                ", genderFinding='" + genderFinding + '\'' +
                ", image='" + image + '\'' +
                '}';
    }
}
