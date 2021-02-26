package com.camers.entity;

import java.io.Serializable;

public class CameraLocation implements Serializable {
    private static final long serialVersionUID = -7272905486020904055L;
    private double latitude;
    private double longtitude;
    private String name;

    public CameraLocation(){}

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongtitude() {
        return longtitude;
    }

    public void setLongtitude(double longtitude) {
        this.longtitude = longtitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CameraLocation(double latitude, double longtitude, String name) {
        this.latitude = latitude;
        this.longtitude = longtitude;
        this.name = name;
    }
}
