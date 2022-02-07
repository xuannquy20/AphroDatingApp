package com.projectd.aphroapp.model;

import java.util.Date;

public class User {
    private String id, name, idSoulmate, quan, huyen, city;
    int age, passwordSearch;
    Date birthDate;

    public User(){}

    public User(String id, String name, int age) {
        this.id = id;
        this.name = name;
        this.age = age;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

}
