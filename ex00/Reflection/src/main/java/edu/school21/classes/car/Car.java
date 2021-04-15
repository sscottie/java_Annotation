package edu.school21.classes.car;

import java.util.StringJoiner;

public class Car {
    private String model;
    private int age;
    private double power;
    private boolean isDiesel;
    private Long carId;

    public Car() {
        this.model = "Mercedes-Benz";
        this.age = 1982;
        this.power = 2.4;
        this.isDiesel = true;
        this.carId = 1234567891011L;
    }

    public Car(String model, int age, double power, boolean isDiesel, Long carId) {
        this.model = model;
        this.age = age;
        this.power = power;
        this.isDiesel = isDiesel;
        this.carId = carId;
    }

    public double changeToTurbo(int value) {
        this.power += value;
        return power;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Car.class.getSimpleName() + "[", "]")
                .add("Model='" + model + "'")
                .add("Age='" + age + "'")
                .add("Power='" + power + "'")
                .add("isDiesel='" + isDiesel + "'")
                .add("carId='" + carId + "'")
                .toString();
    }
}
