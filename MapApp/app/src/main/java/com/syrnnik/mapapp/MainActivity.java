package com.syrnnik.mapapp;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static androidx.core.content.PermissionChecker.PERMISSION_GRANTED;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.yandex.mapkit.MapKit;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.layers.ObjectEvent;
import com.yandex.mapkit.location.FilteringMode;
import com.yandex.mapkit.location.Location;
import com.yandex.mapkit.location.LocationListener;
import com.yandex.mapkit.location.LocationManager;
import com.yandex.mapkit.location.LocationStatus;
import com.yandex.mapkit.map.CameraListener;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.CameraUpdateReason;
import com.yandex.mapkit.map.IconStyle;
import com.yandex.mapkit.map.Map;
import com.yandex.mapkit.map.PlacemarkMapObject;
import com.yandex.mapkit.map.RotationType;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.mapkit.transport.masstransit.Session;
import com.yandex.mapkit.user_location.UserLocationLayer;
import com.yandex.mapkit.user_location.UserLocationObjectListener;
import com.yandex.mapkit.user_location.UserLocationView;
import com.yandex.runtime.image.ImageProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

// https://github.com/yandex/mapkit-android-demo
public class MainActivity extends AppCompatActivity implements
        CameraListener,
        UserLocationObjectListener,
        LocationListener {

    public final static String REQ_TOKEN = "ba661e842cfe7b9dce1a5153c6e80d5e";
    public final static String TAG = "ToiletMap";
//    private final Point UUS_LOCATION = new Point(46.943721, 142.743442);
    public final static ToiletPoints toiletPoints = new ToiletPoints();
    public final static ToiletRouter routeListener = new ToiletRouter();
    private final static String MAPKIT_API_KEY = "389adb7c-8004-48c6-b26b-17a90bfd97e4";
    public static MapView mapView;
    public static RequestQueue queue;
    public static String userCity = "";
    public static HashMap<String, Double> userCoords = new HashMap<>();
    public static Session route;
    private static MapKit mapKit;
    private static LocationManager locationManager;
    public ListView pointsListView;

    public static void clearMap() {
        if (route != null)
            route.cancel();
        mapView.getMap().getMapObjects().clear();
        toiletPoints.getToilets().clear();
    }

    public static void updateMap(Context context) {
        clearMap();
        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET, ServerUrls.GET_ALL_POINTS, null,
                response -> {
//                    Log.e(MainActivity.TAG, response.toString());
                    try {
                        for (int i = 0; i < response.length(); i++)
                            showPoint(context, response.getJSONObject(i));
                        toiletPoints.writeToFile(context);
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    try {
                        checkErrors(context, error);
                    } catch (JSONException | IOException jsonException) {
                        jsonException.printStackTrace();
                    }
                }
        ) {
            @Override
            public java.util.Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("key", REQ_TOKEN);
                return headers;
            }
        };
        request.setTag(TAG);
        queue.add(request);
    }

    public static void showPoint(Context context, JSONObject point) throws JSONException {
        Toilet toilet = new Toilet(
                point.getInt("id"),
                point.getDouble("lat"),
                point.getDouble("lon"),
                point.getString("title"),
                point.getString("address"),
                point.getString("desc")
        );
        toiletPoints.addToilet(toilet);
        mapView.getMap().getMapObjects().addPlacemark(
                new Point(toilet.getLat(), toilet.getLon()),
                ImageProvider.fromResource(context, R.drawable.toilet32))
                .addTapListener((mapObject, toiletPoint) -> {
//                    Log.e(TAG, "CLICKED");
                    try {
                        Toilet t = toiletPoints.getToiletByCoords(
                                ((PlacemarkMapObject) mapObject).getGeometry().getLatitude(),
                                ((PlacemarkMapObject) mapObject).getGeometry().getLongitude());
                        openPointInfoActivity(context, t);
                    } catch (JSONException jsonException) {
                        jsonException.printStackTrace();
                    }
                    return false;
                });
    }

    public static void checkErrors(Context context, VolleyError error) throws JSONException, IOException {
        Log.e(TAG, error.toString());
        String msg = "UNKNOWN";
        if (error.networkResponse != null) {
            if (error.networkResponse.statusCode == 400)
                msg = "Bad Request";
            else if (error.networkResponse.statusCode == 403)
                msg = "Forbidden";
            else if (error.networkResponse.statusCode == 404)
                msg = "Not Found";
            else if (error.networkResponse.statusCode == 405)
                msg = "STUPID ADMINS";
        } else {
            if (error instanceof NoConnectionError)
                msg = "No Connection";
            else {
                assert false;
                msg = String.valueOf(error.networkResponse.statusCode);
            }
            showLocalPoints(context);
        }
        showMsg(context, msg);
        Log.e(TAG, msg);
    }

    public static void openPointInfoActivity(Context context, Toilet t) {
        Intent intent = new Intent(context, ToiletInfoActivity.class);
        if (t != null) {
            intent.putExtra("id", t.getId());
            intent.putExtra("title", t.getTitle());
            intent.putExtra("address", t.getAddress());
            intent.putExtra("desc", t.getDesc());
            intent.putExtra("lat", t.getLat());
            intent.putExtra("lon", t.getLon());
            context.startActivity(intent);
        } else updateMap(context);
    }

    public static void showLocalPoints(Context context) throws JSONException, IOException {
        JSONArray pointsArray = toiletPoints.readFromFile(context);
//        Log.e(TAG, pointsArray.toString());
        for (int i = 0; i < pointsArray.length(); i++)
            showPoint(context, pointsArray.getJSONObject(i));
    }

    public static String getUserCityByCoords(Context context, double lat, double lon) {
        StringRequest request = new StringRequest(
                Request.Method.GET, ServerUrls.GET_USER_CITY_BY_COORDS,
                response -> {
//                    Log.e(MainActivity.TAG, response);
                    userCity = response;
                },
                error -> {
                    try {
                        checkErrors(context, error);
                    } catch (JSONException | IOException jsonException) {
                        jsonException.printStackTrace();
                    }
                }
        ) {
            @Override
            public java.util.Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("key", REQ_TOKEN);
                headers.put("lat", Objects.requireNonNull(userCoords.get("lat")).toString());
                headers.put("lon", Objects.requireNonNull(userCoords.get("lon")).toString());
                return headers;
            }
        };
        request.setTag(TAG);
        queue.add(request);
        return userCity;
    }

    public static void showMsg(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public static void sendAlert(Context context, String title, String msg,
                                 String positiveBtnText,
                                 DialogInterface.OnClickListener positiveOnClickListener,
                                 String negativeBtnText,
                                 DialogInterface.OnClickListener negativeOnClickListener,
                                 int iconId) {
        AlertDialog.Builder alert = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(msg)
                .setPositiveButton(positiveBtnText, positiveOnClickListener)
                .setNegativeButton(negativeBtnText, negativeOnClickListener)
                .setIcon(iconId);
        alert.show();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        MapKitFactory.setApiKey(MAPKIT_API_KEY);
        MapKitFactory.initialize(this);
        mapKit = MapKitFactory.getInstance();
        queue = Volley.newRequestQueue(this);

        if (ContextCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION) != PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{ACCESS_COARSE_LOCATION}, 1);

        if (ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION}, 1);

//        ToiletPoints.addToilet(new Toilet(
//                46.964696, 142.728851,
//                "Комсомолец",
//                "Часто закрыт на уборку, поэтому тут уж как повезет. Да, есть риск попасть в не очень комфортную ситуацию, если охранник спросит, куда вы держите путь.  Избежать неудобных вопросов поможет простой прием: поднимитесь в буфет на втором этаже (если время терпит), купите там шоколадку, а на обратном пути как бы невзначай заверните в уборную."
//        ));
//        ToiletPoints.addToilet(new Toilet(
//                46.956565, 142.739514,
//                "Дом Торговли",
//                "Два туалета, мужской и женский. На первом этаже, в районе «Советской» столовой. Комфортные и ухоженные."
//        ));
//        ToiletPoints.addToilet(new Toilet(
//                46.952598, 142.736585,
//                "Славянский",
//                "Два туалета, мужской и женский. Комфортные и чистые. Располагаются возле лестницы у входа со стороны улицы Красной."
//        ));
//        ToiletPoints.addToilet(new Toilet(
//                46.957925, 142.733073,
//                "Октябрь",
//                "Вы можете легко пройти в туалет, и вас никто не остановит. Единственная сложность – остаться незамеченным в то время, когда фойе пустое. Персонал наверняка заметит, что кинотеатр вы посетили лишь по нужде,  а не для культурного обогащения."
//        ));
//        ToiletPoints.addToilet(new Toilet(
//                46.956215, 142.729758,
//                "Айсберг",
//                "Поднимайтесь на второй этаж , идите по коридору до конца. Сложность в том, что единственный туалет в торговом центре предназначен для персонала и поэтому на двери есть хитрая надпись для незваных гостей:  «Туалет не работает». Но не бойтесь - еще как работает. Вот только косых взглядов вам при его посещении не избежать."
//        ));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pointsListView = findViewById(R.id.pointListView);
        pointsListView.setVisibility(View.GONE);
        PointsListAdapter adapter = new PointsListAdapter(
                this, R.layout.points_list, toiletPoints.getToilets());
        pointsListView.setAdapter(adapter);

        pointsListView.setOnItemClickListener((adapterView, view, i, l) ->
                openPointInfoActivity(
                        this, toiletPoints.getToiletById(
                                Integer.parseInt(view.getContentDescription().toString()))));

        mapView = findViewById(R.id.mapview);
        UserLocationLayer userLocationLayer = mapKit.createUserLocationLayer(mapView.getMapWindow());
        userLocationLayer.setVisible(true);
        userLocationLayer.setHeadingEnabled(true);
        userLocationLayer.setObjectListener(this);
        userLocationLayer.setAutoZoomEnabled(true);
        mapView.getMap().addCameraListener(this);

        locationManager = MapKitFactory.getInstance().createLocationManager();


//        mapView.getMap().move(
//                new CameraPosition(
////                        new Point(p.getLatitude(), p.getLongitude()),
//                        UUS_LOCATION,
//                        14.0f, 0.0f, 0.0f));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
        // TODO: check onCreateOptionsMenu returns in all activities
        // ...
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStop() {
        mapView.onStop();
        mapKit.onStop();
        try {
            toiletPoints.writeToFile(this);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        super.onStop();
    }

    @Override
    public void onStart() {
        mapView.onStart();
        mapKit.onStart();
        super.onStart();

        final double DESIRED_ACCURACY = 0;
        final long MINIMAL_TIME = 100;
        final double MINIMAL_DISTANCE = 1;
        final boolean USE_IN_BACKGROUND = false;
        locationManager.subscribeForLocationUpdates(
                DESIRED_ACCURACY,
                MINIMAL_TIME,
                MINIMAL_DISTANCE,
                USE_IN_BACKGROUND,
                FilteringMode.OFF,
                this);
    }

    @Override
    public void onResume() {
        updateMap(this);
        if (route != null)
            route.retry(routeListener);
        super.onResume();
    }

    @Override
    public void onCameraPositionChanged(
            @NonNull Map map,
            @NonNull CameraPosition cameraPosition,
            @NonNull CameraUpdateReason cameraUpdateReason,
            boolean finished) {
    }

    @Override
    public void onObjectAdded(UserLocationView userLocationView) {
        userLocationView.getArrow().useCompositeIcon().setIcon(
                "icon",
                ImageProvider.fromResource(this, R.drawable.user_arrow),
                new IconStyle()
                        .setRotationType(RotationType.ROTATE)
                        .setZIndex(0f));
        userLocationView.getAccuracyCircle().setFillColor(Color.BLUE & 0x99ffffff);
    }

    @Override
    public void onObjectRemoved(@NonNull UserLocationView userLocationView) {
    }

    @Override
    public void onObjectUpdated(@NonNull UserLocationView userLocationView, @NonNull ObjectEvent objectEvent) {
    }

    public void openAddingToiletPoint(MenuItem menu) {
        startActivity(new Intent(this, AddPointActivity.class));
    }

    public void changeLayer(MenuItem item) {
        if (item.getTitle().toString().equals(getResources().getString(R.string.layer_map))) {
            item.setTitle(getResources().getString(R.string.layer_list));
            mapView.setVisibility(View.GONE);
            pointsListView.setVisibility(View.VISIBLE);
        } else if (item.getTitle().toString().equals(getResources().getString(R.string.layer_list))) {
            item.setTitle(getResources().getString(R.string.layer_map));
            mapView.setVisibility(View.VISIBLE);
            pointsListView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onLocationUpdated(@NonNull Location location) {
        Point p = location.getPosition();
        userCoords.put("lat", p.getLatitude());
        userCoords.put("lon", p.getLongitude());
//        Log.e(TAG, String.format("Your coords: %f %f", p.getLatitude(), p.getLongitude()));
    }

    @Override
    public void onLocationStatusUpdated(@NonNull LocationStatus locationStatus) {
    }

    public void findMe(View view) {
        Double userLat = userCoords.get("lat");
        Double userLon = userCoords.get("lon");
        if (userLat != null && userLon != null)
            mapView.getMap().move(
                    new CameraPosition(
                            new Point(
                                    Objects.requireNonNull(userCoords.get("lat")),
                                    Objects.requireNonNull(userCoords.get("lon"))
                            ), 14.0f, 0.0f, 0.0f));
        else showMsg(this, "Cannot find you, try again please");
    }
}