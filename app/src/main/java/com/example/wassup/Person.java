package com.example.wassup;

public class Person {
    String uid;

    Person()
    {}
    public Person(String uid)
    {
        this.uid=uid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
