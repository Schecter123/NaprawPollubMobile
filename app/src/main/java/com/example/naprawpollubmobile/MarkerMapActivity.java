package com.example.naprawpollubmobile;

import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.maps.android.clustering.ClusterManager;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

public class MarkerMapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ClusterManager<MyItem> mClusterManager;
    private LatLngBounds POLITECHNIKA = new LatLngBounds(
            new LatLng(51.234801, 22.545508), new LatLng(51.237139, 22.550207));
    private int idPlace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_marker_map);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        UiSettings mapUiSettings = mMap.getUiSettings();
        mapUiSettings.setZoomControlsEnabled(true);
        mapUiSettings.setCompassEnabled(true);

        LatLng center = new LatLng(51.236185, 22.548115);
        // Constrain the camera target to the Adelaide bounds.
        mMap.setLatLngBoundsForCameraTarget(POLITECHNIKA);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(center, 17));
        mMap.setMinZoomPreference(15);
        mMap.setMaxZoomPreference(18);
        mClusterManager = new ClusterManager<MyItem>(this, mMap);
        mMap.setOnCameraIdleListener(mClusterManager);
        mMap.setOnMarkerClickListener(mClusterManager);

        //addItems();
        getAllDefects();
        mClusterManager.cluster();


    }

    private void addItems() {

        // Set some lat/lng coordinates to start with.
        double lat = 51.236185;
        double lng = 22.548115;

        // Add ten cluster items in close proximity, for purposes of this example.
        for (int i = 0; i < 10; i++) {
            double offset = i / 60d;
            lat = lat + offset;
            lng = lng + offset;
            MyItem offsetItem = new MyItem(lat, lng);
            mClusterManager.addItem(offsetItem);
        }
    }

    public void getAllDefects() {

        String URL = "http://" + getString(R.string.ip) + ":8000/api/v1/defects";

        Ion.with(MarkerMapActivity.this).load(URL).asJsonArray().setCallback(new FutureCallback<JsonArray>() {
            @Override
            public void onCompleted(Exception e, JsonArray result) {
                try {
                    for (int i = 0; i < result.size(); i++) {
                        JsonObject cli = result.get(i).getAsJsonObject();
                        if (cli.get("idPlace").getAsInt() < 17) {
                            idPlace = cli.get("idPlace").getAsInt();
                            setAllMarkers(cli.get("idPlace").getAsInt());
                        }
                    }
                } catch (Exception erro) {

                }
            }
        });
    }

    public void setAllMarkers(int id) {
        String URL = "http://" + getString(R.string.ip) + ":8000/api/v1/markers/" + id + "/place";
        Ion.with(MarkerMapActivity.this).load(URL).asJsonArray().setCallback(new FutureCallback<JsonArray>() {
            @Override
            public void onCompleted(Exception e, JsonArray result) {
                try {
                    for (int i = 0; i < result.size(); i++) {
                        JsonObject cli = result.get(i).getAsJsonObject();
                        MyItem offsetItem = new MyItem(cli.get("latitude").getAsDouble(), cli.get("longitude").getAsDouble());
                        mClusterManager.addItem(offsetItem);

                    }

                } catch (Exception erro) {

                }
            }
        });
    }
}
