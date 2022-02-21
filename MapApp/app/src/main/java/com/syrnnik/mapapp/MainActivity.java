package com.syrnnik.mapapp;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static androidx.core.content.PermissionChecker.PERMISSION_GRANTED;

import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.yandex.mapkit.MapKit;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.layers.ObjectEvent;
import com.yandex.mapkit.map.CameraListener;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.CameraUpdateReason;
import com.yandex.mapkit.map.Map;
import com.yandex.mapkit.map.MapObjectCollection;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.mapkit.user_location.UserLocationLayer;
import com.yandex.mapkit.user_location.UserLocationObjectListener;
import com.yandex.mapkit.user_location.UserLocationView;
import com.yandex.runtime.image.ImageProvider;

public class MainActivity extends AppCompatActivity implements CameraListener, UserLocationObjectListener, LocationListener {

    private final String MAPKIT_API_KEY = "389adb7c-8004-48c6-b26b-17a90bfd97e4";
    private final Point UUS_LOCATION = new Point(46.943721, 142.743442);

    public static MapView mapView;
    public static ListView pointsListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MapKitFactory.setApiKey(MAPKIT_API_KEY);
        MapKitFactory.initialize(this);

        if (ContextCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION) != PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{ ACCESS_COARSE_LOCATION }, 1);

        if (ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{ ACCESS_FINE_LOCATION }, 1);

        ToiletPoints.addToilet(new Toilet(
                46.964696, 142.728851,
                "Комсомолец",
                "Часто закрыт на уборку, поэтому тут уж как повезет. Да, есть риск попасть в не очень комфортную ситуацию, если охранник спросит, куда вы держите путь.  Избежать неудобных вопросов поможет простой прием: поднимитесь в буфет на втором этаже (если время терпит), купите там шоколадку, а на обратном пути как бы невзначай заверните в уборную."
        ));
        ToiletPoints.addToilet(new Toilet(
                46.956565, 142.739514,
                "Дом Торговли",
                "Два туалета, мужской и женский. На первом этаже, в районе «Советской» столовой. Комфортные и ухоженные."
        ));
        ToiletPoints.addToilet(new Toilet(
                46.952598, 142.736585,
                "Славянский",
                "Два туалета, мужской и женский. Комфортные и чистые. Располагаются возле лестницы у входа со стороны улицы Красной."
        ));
        ToiletPoints.addToilet(new Toilet(
                46.957925, 142.733073,
                "Октябрь",
                "Вы можете легко пройти в туалет, и вас никто не остановит. Единственная сложность – остаться незамеченным в то время, когда фойе пустое. Персонал наверняка заметит, что кинотеатр вы посетили лишь по нужде,  а не для культурного обогащения."
        ));
        ToiletPoints.addToilet(new Toilet(
                46.956215, 142.729758,
                "Айсберг",
                "Поднимайтесь на второй этаж , идите по коридору до конца. Сложность в том, что единственный туалет в торговом центре предназначен для персонала и поэтому на двери есть хитрая надпись для незваных гостей:  «Туалет не работает». Но не бойтесь - еще как работает. Вот только косых взглядов вам при его посещении не избежать."
        ));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pointsListView = findViewById(R.id.pointListView);
        pointsListView.setVisibility(View.GONE);
        PointsListAdapter adapter = new PointsListAdapter(
                this, R.layout.points_list, ToiletPoints.getToilets());

        pointsListView.setOnItemClickListener((adapterView, vw, i, l) -> {
            ImageView arrowView = vw.findViewById(R.id.arrow);
            TextView descView = vw.findViewById(R.id.desc);

            if (descView.getVisibility() == View.GONE) {
                descView.setVisibility(View.VISIBLE);
                arrowView.setRotation(0);
            }
            else if (descView.getVisibility() == View.VISIBLE) {
                descView.setVisibility(View.GONE);
                arrowView.setRotation(90);
            }
        });

        pointsListView.setAdapter(adapter);

        mapView = findViewById(R.id.mapview);
        mapView.getMap().addCameraListener(this);
        mapView.getMap().addTapListener(geoObjectTapEvent -> {
//            mapView.getMap().move(new CameraPosition(geoObjectTapEvent., 14.0f, 0.0f, 0.0f));
            Log.e("MAP", "CLICKED");
            return false;
        });

        MapObjectCollection mapObjects = mapView.getMap().getMapObjects();
        for (Toilet toilet : ToiletPoints.getToilets()) {
            mapObjects.addPlacemark(
                    new Point(toilet.getLat(), toilet.getLon()),
                    ImageProvider.fromResource(this, R.drawable.toilet));
        }

        MapKit mapKit = MapKitFactory.getInstance();
        UserLocationLayer userLocationLayer = mapKit.createUserLocationLayer(mapView.getMapWindow());
        userLocationLayer.setVisible(true);
        userLocationLayer.setHeadingEnabled(true);
        userLocationLayer.setObjectListener(this);

        mapView.getMap().move(new CameraPosition(UUS_LOCATION, 13.0f, 0.0f, 0.0f));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        mapView = findViewById(R.id.mapView);
//        pointsListView = findViewById(R.id.pointListView);
//
//        if (item.getContentDescription().toString().equals(
//                getResources().getString(R.string.layer_map))) {
//            item.setContentDescription(getResources().getString(R.string.layer_list));
//            mapView.setVisibility(View.GONE);
//            pointsListView.setVisibility(View.VISIBLE);
//        }
//
//        if (item.getContentDescription().toString().equals(
//                getResources().getString(R.string.layer_list))) {
//            item.setContentDescription(getResources().getString(R.string.layer_map));
//            mapView.setVisibility(View.VISIBLE);
//            pointsListView.setVisibility(View.GONE);
//        }
//
//        if (item.getContentDescription().toString().equals(
//                getResources().getString(R.string.desc_add))) {
//            startActivity(new Intent(this, AddToiletPoint.class));
//        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        mapView.onStop();
        MapKitFactory.getInstance().onStop();
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        MapKitFactory.getInstance().onStart();
        mapView.onStart();
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
//        userLocationLayer.setAnchor(
//                new PointF((float)(mapView.getWidth() * 0.5), (float)(mapView.getHeight() * 0.5)),
//                new PointF((float)(mapView.getWidth() * 0.5), (float)(mapView.getHeight() * 0.83)));

        userLocationView.getArrow().setIcon(ImageProvider.fromResource(this, R.drawable.user_arrow));

//        CompositeIcon pinIcon = userLocationView.getPin().useCompositeIcon();

//        pinIcon.setIcon(
//                "icon",
//                ImageProvider.fromResource(this, R.drawable.icon),
//                new IconStyle().setAnchor(new PointF(0f, 0f))
//                        .setRotationType(RotationType.ROTATE)
//                        .setZIndex(0f)
//                        .setScale(1f)
//        );

//        pinIcon.setIcon(
//                "pin",
//                ImageProvider.fromResource(this, R.drawable.search_result),
//                new IconStyle().setAnchor(new PointF(0.5f, 0.5f))
//                        .setRotationType(RotationType.ROTATE)
//                        .setZIndex(1f)
//                        .setScale(0.5f)
//        );

        userLocationView.getAccuracyCircle().setFillColor(Color.BLUE & 0x99ffffff);
    }

    @Override
    public void onObjectRemoved(@NonNull UserLocationView userLocationView) { }

    @Override
    public void onObjectUpdated(@NonNull UserLocationView userLocationView, @NonNull ObjectEvent objectEvent) { }

    @Override
    public void onLocationChanged(@NonNull Location loc) {
//        mapView.getMap().move(
//                new CameraPosition(
//                        new Point(loc.getLatitude(), loc.getLongitude()),
//                        14.0f, 0.0f, 0.0f));
    }

    public void openAddingToiletPoint(MenuItem menu) {
        startActivity(new Intent(this, AddToiletPointActivity.class));
    }

    public void changeLayer(MenuItem item) {
        if (item.getTitle().toString().equals(getResources().getString(R.string.layer_map))) {
            item.setTitle(getResources().getString(R.string.layer_list));
            mapView.setVisibility(View.GONE);
            pointsListView.setVisibility(View.VISIBLE);
        }
        else if (item.getTitle().toString().equals(getResources().getString(R.string.layer_list))) {
            item.setTitle(getResources().getString(R.string.layer_map));
            mapView.setVisibility(View.VISIBLE);
            pointsListView.setVisibility(View.GONE);
        }
    }
}