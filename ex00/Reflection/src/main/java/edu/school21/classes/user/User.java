package edu.school21.classes.user;

import java.util.StringJoiner;

public class User {
    private String name;
    private int age;
    private double height;
    private boolean male;
    private long passportId;

    public User() {
        this.name = "Andrei";
        this.age = 27;
        this.height = 1.85;
        this.male = true;
        this.passportId = 12348910111224L;
    }

    public User(String name, int age, double height, boolean male, long passportId) {
        this.name = name;
        this.age = age;
        this.height = height;
        this.male = male;
        this.passportId = passportId;
    }

    public double grow(double value) {
        this.height += value;
        return height;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", User.class.getSimpleName() + "[", "]")
                .add("Name='" + name + "'")
                .add("Age='" + age + "'")
                .add("height='" + height + "'")
                .add("male='" + male + "'")
                .add("passportId='" + passportId + "'")
                .toString();
    }
}
