package com.syrnnik.mapapp;

import java.util.ArrayList;

public class ToiletPoints {

    public static final ArrayList<Toilet> toiletList = new ArrayList<>();

    public static ArrayList<Toilet> getToilets() {
        return toiletList;
    }

    public static void addToilet(Toilet toilet) {
        toiletList.add(toilet);
    }
}
