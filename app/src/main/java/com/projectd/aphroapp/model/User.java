package com.projectd.aphroapp.model;



public class User{
    private String id, name, city, district, ward, description, image;
    private boolean gender, genderFinding;
    private int day, month, year, orderNumber, age;

    public User(){}

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public User(String id, String name, String city, String district, String ward, String description, String image, boolean gender, boolean genderFinding, int day, int month, int year) {
        this.id = id;
        this.name = name;
        this.city = city;
        this.district = district;
        this.ward = ward;
        this.description = description;
        this.image = image;
        this.gender = gender;
        this.genderFinding = genderFinding;
        this.day = day;
        this.month = month;
        this.year = year;
    }

    public int getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(int orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public boolean isGender() {
        return gender;
    }

    public void setGender(boolean gender) {
        this.gender = gender;
    }

    public boolean isGenderFinding() {
        return genderFinding;
    }

    public void setGenderFinding(boolean genderFinding) {
        this.genderFinding = genderFinding;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }
}
