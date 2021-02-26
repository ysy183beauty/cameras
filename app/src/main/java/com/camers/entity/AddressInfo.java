package com.camers.entity;

/**
 * yangqiangyu on 2017/9/9 18:03
 * email:168553877@qq.com
 * blog:http://blog.csdn.net/yissan
 */

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
