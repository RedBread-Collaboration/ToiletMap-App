package com.syrnnik.mapapp;

import org.json.JSONException;
import org.json.JSONObject;

public class Toilet {

    private final int id;
    private double lat;
    private double lon;
    private String title;
    private String address;
    private String desc;

    public Toilet(int id, double lat, double lon, String title, String address, String desc) {
        this.id = id;
        this.lat = lat;
        this.lon = lon;
        this.title = title;
        this.address = address;
        this.desc = desc;
    }

    public int getId() {
        return this.id;
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

    public void setCoords(double lat, double lon) {
        this.lat = lat;
        this.lon = lon;
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

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject toilet = new JSONObject();
        toilet.put("id", this.getId());
        toilet.put("lat", this.getLat());
        toilet.put("lon", this.getLon());
        toilet.put("title", this.getTitle());
        toilet.put("address", this.getAddress());
        toilet.put("desc", this.getDesc());
        return toilet;
    }

}
