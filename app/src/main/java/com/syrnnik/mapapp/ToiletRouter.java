package com.syrnnik.mapapp;

import android.util.Log;

import androidx.annotation.NonNull;

import com.yandex.mapkit.geometry.SubpolylineHelper;
import com.yandex.mapkit.map.PolylineMapObject;
import com.yandex.mapkit.transport.masstransit.Route;
import com.yandex.mapkit.transport.masstransit.Section;
import com.yandex.mapkit.transport.masstransit.Session;
import com.yandex.runtime.Error;

import java.util.List;

public class ToiletRouter implements Session.RouteListener {

    @Override
    public void onMasstransitRoutes(@NonNull List<Route> routes) {
        // TODO: change design of way line
        if (routes.size() > 0) {
            for (Section section : routes.get(0).getSections()) {
                PolylineMapObject way = MainActivity.mapView.getMap().getMapObjects().addPolyline(
                        SubpolylineHelper.subpolyline(
                                routes.get(0).getGeometry(), section.getGeometry()));
                way.setStrokeColor(0xcebb00ff);
            }
        }
    }

    @Override
    public void onMasstransitRoutesError(@NonNull Error error) {
        Log.e(MainActivity.TAG, error.toString());
    }

}
