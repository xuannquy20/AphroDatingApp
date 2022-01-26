package com.projectd.aphroapp.model;

public class User {
    int id;
    String name;
    int age;

    public User(){}

    public User(int id, String name, int age) {
        this.id = id;
        this.name = name;
        this.age = age;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    @Override
    public String toString() {
        return "{name:'" + name + "',\n" +
                "age: '" + age +"'\n" +
                "}";
    }
}
