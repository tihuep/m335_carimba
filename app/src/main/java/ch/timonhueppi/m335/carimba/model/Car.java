package ch.timonhueppi.m335.carimba.model;

import java.util.UUID;

public class Car {
    private String carId;
    private String userId;
    private String year;
    private String make;
    private String model;
    private String trim;

    public Car(String carId, String userId, String year, String make, String model, String trim) {
        this.carId = carId;
        this.userId = userId;
        this.year = year;
        this.make = make;
        this.model = model;
        this.trim = trim;
    }

    public Car(String userId, String year, String make, String model, String trim) {
        this.userId = userId;
        this.year = year;
        this.make = make;
        this.model = model;
        this.trim = trim;
    }

    public String getCarId() {
        return carId;
    }

    public void setCarId(String carId) {
        this.carId = carId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getTrim() {
        return trim;
    }

    public void setTrim(String trim) {
        this.trim = trim;
    }
}
