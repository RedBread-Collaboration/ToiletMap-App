package com.syrnnik.mapapp;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.yandex.mapkit.RequestPoint;
import com.yandex.mapkit.RequestPointType;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.transport.TransportFactory;
import com.yandex.mapkit.transport.masstransit.PedestrianRouter;
import com.yandex.mapkit.transport.masstransit.TimeOptions;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class ToiletInfoActivity extends AppCompatActivity {

    private static PedestrianRouter pedestrianRouter;
    ConstraintLayout constraintLayout;
    TextView titleView;
    TextView addressView;
    TextView descView;
    EditText titleEdit;
    EditText addressEdit;
    EditText descEdit;
    Button saveBtn;
    Button routeBtn;
    Button closeBtn;
    private int id;
    private String title;
    private String address;
    private String desc;
    private Double lat;
    private Double lon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toilet_info);

        Bundle extras = getIntent().getExtras();
        id = extras.getInt("id");
        title = extras.getString("title");
        address = extras.getString("address");
        desc = extras.getString("desc");
        lat = extras.getDouble("lat");
        lon = extras.getDouble("lon");

        constraintLayout = findViewById(R.id.activity_toilet_info);

        pedestrianRouter = TransportFactory.getInstance().createPedestrianRouter();

        // Title
        titleView = findViewById(R.id.title);
        titleView.setText(title);
        titleEdit = findViewById(R.id.titleInput);
        titleEdit.setText(title);
        titleEdit.setVisibility(View.GONE);
        // Address
        addressView = findViewById(R.id.address);
        addressView.setText(address);
        addressEdit = findViewById(R.id.addressInput);
        addressEdit.setText(address);
        addressEdit.setVisibility(View.GONE);
        // Desc
        descView = findViewById(R.id.desc);
        descView.setText(desc);
        descEdit = findViewById(R.id.descInput);
        descEdit.setText(desc);
        descEdit.setVisibility(View.GONE);
        // Save Btn
        saveBtn = findViewById(R.id.savePointInfoBtn);
        saveBtn.setVisibility(View.GONE);
        // Get There Btn
        routeBtn = findViewById(R.id.routeBtn);
        // Close Btn
        closeBtn = findViewById(R.id.closePointInfoBtn);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toilet_info_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    public void removePointById(MenuItem item) {
        MainActivity.sendAlert(
                this, getResources().getString(R.string.warning),
                getResources().getString(R.string.remove_warning_msg),
                getResources().getString(R.string.yes),
                (dialog, i) -> {
                    StringRequest request = new StringRequest(
                            Request.Method.DELETE, ServerUrls.REMOVE_POINT_BY_ID,
                            response -> {
//                                Log.e(MainActivity.TAG, "Removed successfully");
                                MainActivity.toiletPoints.removeToiletById(id);
                                MainActivity.showMsg(this, getResources().getString(R.string.remove));
                            }, error -> {
                        try {
                            MainActivity.checkErrors(this, error);
                        } catch (JSONException | IOException jsonException) {
                            jsonException.printStackTrace();
                        }
                    }
                    ) {
                        @Override
                        public java.util.Map<String, String> getHeaders() {
                            HashMap<String, String> headers = new HashMap<>();
                            headers.put("key", MainActivity.REQ_TOKEN);
                            headers.put("id", String.valueOf(id));
                            return headers;
                        }
                    };
                    request.setTag(MainActivity.TAG);
                    MainActivity.queue.add(request);

                    finish();
                },
                getResources().getString(R.string.no), null,
                R.drawable.warning);
    }

    public void changeMode(MenuItem item) {
        // Edit Info Mode
        if (item.getTitle().toString().equals(getResources().getString(R.string.mode_info))) {
            item.setTitle(getResources().getString(R.string.mode_edit));
            item.setIcon(R.drawable.info);

            titleView.setVisibility(View.GONE);
            addressView.setVisibility(View.GONE);
            descView.setVisibility(View.GONE);
            routeBtn.setVisibility(View.GONE);
            closeBtn.setVisibility(View.GONE);

            titleEdit.setVisibility(View.VISIBLE);
            addressEdit.setVisibility(View.VISIBLE);
            descEdit.setVisibility(View.VISIBLE);
            saveBtn.setVisibility(View.VISIBLE);

            // Connect TOP of DescText to BOTTOM of AddressEdit
            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(constraintLayout);
//            constraintSet.clear(R.id.descText, ConstraintSet.TOP);
            constraintSet.connect(
                    R.id.descText,
                    ConstraintSet.TOP,
                    R.id.addressInput,
                    ConstraintSet.BOTTOM, 32);
            constraintSet.applyTo(constraintLayout);
        }
        // Show Info Mode
        else if (item.getTitle().toString().equals(getResources().getString(R.string.mode_edit))) {
            item.setTitle(getResources().getString(R.string.mode_info));
            item.setIcon(R.drawable.edit);

            titleView.setVisibility(View.VISIBLE);
            titleView.setText(title);
            addressView.setVisibility(View.VISIBLE);
            addressView.setText(address);
            descView.setVisibility(View.VISIBLE);
            descView.setText(desc);
            routeBtn.setVisibility(View.VISIBLE);
            closeBtn.setVisibility(View.VISIBLE);

            titleEdit.setVisibility(View.GONE);
            addressEdit.setVisibility(View.GONE);
            descEdit.setVisibility(View.GONE);
            saveBtn.setVisibility(View.GONE);

            // Connect TOP of DescText to BOTTOM of Address
            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(constraintLayout);
            constraintSet.clear(R.id.descText, ConstraintSet.TOP);
            constraintSet.connect(
                    R.id.descText,
                    ConstraintSet.TOP,
                    R.id.address,
                    ConstraintSet.BOTTOM, 32);
            constraintSet.applyTo(constraintLayout);
        }
    }

    public void savePointInfo(View view) {
        MainActivity.sendAlert(
                this, getResources().getString(R.string.save),
                getResources().getString(R.string.save_warning_msg),
                getResources().getString(R.string.yes),
                (dialog, i) -> {
                    title = titleEdit.getText().toString();
                    address = addressEdit.getText().toString();
                    desc = descEdit.getText().toString();
                    JsonObjectRequest request = new JsonObjectRequest(
                            Request.Method.PUT, ServerUrls.UPDATE_POINT, null,
                            response -> {
//                                Log.e(MainActivity.TAG, "Saved successfully");
                                MainActivity.showMsg(this, getResources().getString(R.string.saved));
                            },
                            error -> {
                                try {
                                    MainActivity.checkErrors(this, error);
                                } catch (JSONException | IOException jsonException) {
                                    jsonException.printStackTrace();
                                }
                            }
                    ) {
                        @Override
                        public java.util.Map<String, String> getHeaders() {
                            HashMap<String, String> headers = new HashMap<>();
                            headers.put("key", MainActivity.REQ_TOKEN);
                            headers.put("id", String.valueOf(id));
                            headers.put("title", title);
//                             headers.put("address", MainActivity.userCity + " " + address);
                            headers.put("lat", Objects.requireNonNull(MainActivity.userCoords.get("lat")).toString());
                            headers.put("lon", Objects.requireNonNull(MainActivity.userCoords.get("lon")).toString());
                            headers.put("desc", desc);
                            return headers;
                        }
                    };
                    request.setTag(MainActivity.TAG);
                    MainActivity.queue.add(request);

                    closePointInfo(view);
                },
                getResources().getString(R.string.no), null,
                R.drawable.warning);
    }

    public void closePointInfo(View view) {
        if (!titleEdit.getText().toString().equals(title)
                || !addressEdit.getText().toString().equals(address)
                || !descEdit.getText().toString().equals(desc)) {
            MainActivity.sendAlert(
                    this, getResources().getString(R.string.warning),
                    getResources().getString(R.string.close_warning_msg),
                    getResources().getString(R.string.yes),
                    (dialog, i) -> finish(),
                    getResources().getString(R.string.no), null,
                    R.drawable.warning);
            return;
        }
        finish();
    }

    // TODO: select type of routing(how to get to toilet, way)
    public void requestDrive() {
        Double userLat = MainActivity.userCoords.get("lat");
        Double userLon = MainActivity.userCoords.get("lon");

        if (userLat != null && userLon != null) {
            List<RequestPoint> points = new ArrayList<>();
            points.add(new RequestPoint(
                    new Point(userLat, userLon),
                    RequestPointType.WAYPOINT, null));
            points.add(new RequestPoint(
                    new Point(lat, lon),
                    RequestPointType.WAYPOINT, null));
            MainActivity.route = pedestrianRouter.requestRoutes(points, new TimeOptions(), MainActivity.routeListener);
        } else MainActivity.showMsg(this, "Cannot find you, try again please");
    }

    public void getThere(View view) {
        requestDrive();
        closePointInfo(view);
    }

}