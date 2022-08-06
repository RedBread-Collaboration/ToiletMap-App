package com.syrnnik.mapapp;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class ToiletPoints {

    public final static String FILE_NAME = "LOCAL_DB";

    public static final ArrayList<Toilet> toiletList = new ArrayList<>();

    public ToiletPoints() {
    }

    public ArrayList<Toilet> getToilets() {
        return toiletList;
    }

    public Toilet getToiletById(int id) {
        for (Toilet toilet : this.getToilets())
            if (toilet.getId() == id)
                return toilet;
        return null;
    }

    public Toilet getToiletByCoords(double lat, double lon) throws JSONException {
        for (Toilet toilet : this.getToilets())
            if (toilet.getLat() == lat && toilet.getLon() == lon)
                return toilet;
        return null;
    }

    public JSONArray getJsonArray() throws JSONException {
        JSONArray toiletsArray = new JSONArray();
        for (Toilet toilet : this.getToilets())
            toiletsArray.put(toilet.toJSON());
        return toiletsArray;
    }

    public void addToilet(Toilet toilet) {
        toiletList.add(toilet);
    }

    public void removeToiletById(int id) {
        this.getToilets().removeIf(toilet -> toilet.getId() == id);
    }

    public JSONArray readFromFile(Context context) throws IOException, JSONException {
        toiletList.clear();
        File file = new File(context.getFilesDir(), FILE_NAME);
        FileReader fileReader = new FileReader(file);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        StringBuilder stringBuilder = new StringBuilder();
        String line = bufferedReader.readLine();
        while (line != null) {
            stringBuilder.append(line).append("\n");
            line = bufferedReader.readLine();
        }
        bufferedReader.close();
        String response = stringBuilder.toString();
        return new JSONArray(response);
    }

    public void writeToFile(Context context) throws IOException, JSONException {
        File file = new File(context.getFilesDir(), FILE_NAME);
        FileWriter fileWriter = new FileWriter(file);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        bufferedWriter.write(this.getJsonArray().toString());
        bufferedWriter.close();
    }

}
