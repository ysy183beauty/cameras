package com.camers.entity;

import java.io.Serializable;

public class DistanceEntity implements Serializable {
    private String cameraName;//摄像头名称
    private double distance;//距离

    public String getCameraName() {
        return cameraName;
    }

    public void setCameraName(String cameraName) {
        this.cameraName = cameraName;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
}
