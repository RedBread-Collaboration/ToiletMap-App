package com.syrnnik.mapapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.yandex.mapkit.geometry.Point;
import com.yandex.runtime.image.ImageProvider;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class AddPointActivity extends AppCompatActivity {

    EditText street;
    EditText title;
    EditText desc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_point);
        setTitle(R.string.addPointTitle);

    }

    public void addToiletPoint(View view) {
        street = findViewById(R.id.street);
        title = findViewById(R.id.pointTitle);
        desc = findViewById(R.id.pointDesc);

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                "https://infosakh.ru/wc/addPoint/", null,
                (Response.Listener<JSONObject>) response -> {
                    Log.e(MainActivity.TAG, response.toString());
                    try {
                        Toilet toilet = new Toilet(
                                response.get("lat"),
                                response.get("lon"),
                                response.get("title"),
                                response.get("address"),
                                response.get("desc")
                        );
                        ToiletPoints.toiletList.add(toilet);
                        MainActivity.mapView.getMap().getMapObjects().addPlacemark(
                                new Point(Double.parseDouble(response.get("lat").toString()),
                                        Double.parseDouble(response.get("lon").toString())),
                                ImageProvider.fromResource(this, R.drawable.toilet));
                    } catch (JSONException e) { e.printStackTrace(); }

                }, error -> {
                    if (error.networkResponse.statusCode == 400)
                        MainActivity.showMsg(this, "Bad Request");
                    else if (error.networkResponse.statusCode == 403)
                        MainActivity.showMsg(this, "Forbidden");
                    else if (error.networkResponse.statusCode == 404)
                        MainActivity.showMsg(this, "Not Found");
                    else if (error.networkResponse.statusCode == 405)
                        MainActivity.showMsg(this, "STUPID ADMINS");
            }
        ) {
            @Override
            public java.util.Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("key", MainActivity.REQ_TOKEN);
                headers.put("address", street.getText().toString());
                headers.put("title", title.getText().toString());
                headers.put("desc", desc.getText().toString());
                return headers;
            }
        };
        request.setTag(MainActivity.TAG);
        MainActivity.queue.add(request);

        finish();
    }
}