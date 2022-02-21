package com.syrnnik.mapapp;

public class Toilet {

    private double lat;
    private double lon;
    private String title;
    private String desc;

    public Toilet(double _lat, double _lon, String _title, String _desc) {
        this.lat = _lat;
        this.lon = _lon;
        this.title = _title;
        this.desc = _desc;
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

    public String getDesc() {
        return this.desc;
    }

    public void setDesc(String newDesc) {
        this.desc = newDesc;
    }

}
