package com.syrnnik.mapapp;

import static com.syrnnik.mapapp.MainActivity.mapView;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.yandex.mapkit.geometry.Point;
import com.yandex.runtime.image.ImageProvider;

public class AddToiletPointActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_toilet_point);
        setTitle(R.string.addPointTitle);

    }

    public void addToiletPoint(View view) {
        EditText lat = findViewById(R.id.pointLat);
        EditText lon = findViewById(R.id.pointLon);
        EditText title = findViewById(R.id.pointTitle);
        EditText desc = findViewById(R.id.pointDesc);

        Toilet newToilet = new Toilet(
                Double.parseDouble(lat.getText().toString()),
                Double.parseDouble(lon.getText().toString()),
                title.getText().toString(),
                desc.getText().toString());

        ToiletPoints.addToilet(newToilet);

        mapView.getMap().getMapObjects().addPlacemark(
                new Point(Double.parseDouble(lat.getText().toString()),
                        Double.parseDouble(lon.getText().toString())),
                ImageProvider.fromResource(this, R.drawable.toilet));

        finish();
    }
}