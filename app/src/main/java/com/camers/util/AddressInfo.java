package com.camers.util;
public class AddressInfo {

    private double lat;
    private double lng;

    public double getLat() {
        return lat;
    }

    public AddressInfo setLat(double lat) {
        this.lat = lat;
        return this;
    }

    public double getLng() {
        return lng;
    }

    public AddressInfo setLng(double lng) {
        this.lng = lng;
        return this;
    }
}
