package com.syrnnik.mapapp;

public class Toilet {

    private double lat;
    private double lon;
    private String title;
    private String address;
    private String desc;

    public Toilet(double lat, double lon, String title, String address, String desc) {
        this.lat = lat;
        this.lon = lon;
        this.title = title;
        this.address = address;
        this.desc = desc;
    }

    public Toilet(Object lat, Object lot, Object title, Object address, Object desc) {
    }

    public double getLat() {
        return this.lat;
    }

    public double getLon() {
        return this.lon;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setCoords(double _lat, double _lon) {
        this.lat = _lat;
        this.lon = _lon;
    }

    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDesc() {
        return this.desc;
    }

    public void setDesc(String newDesc) {
        this.desc = newDesc;
    }

}
