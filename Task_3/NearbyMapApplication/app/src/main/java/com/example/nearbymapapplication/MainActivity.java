package com.example.nearbymapapplication;

import android.Manifest;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;


import android.content.pm.PackageManager;
import android.graphics.Color;
import android.util.Log;
import android.widget.Toast;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.LocationComponentOptions;
import com.mapbox.mapboxsdk.location.engine.LocationEngineRequest;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.maps.Style;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private MapboxMap mapboxMap;
    private MapView mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this);
        setContentView(R.layout.activity_main);

        String Barikoi_Bangla = "https://map.barikoi.com/styles/barikoi-bangla/style.json?key=bkoi_1a742b1c6c2e94c2996f6ae252581746050713c3d51cebbee67f7a186d9a24a1";
        //String Nearby_API = "https://barikoi.xyz/v1/api/search/nearby/bkoi_1a742b1c6c2e94c2996f6ae252581746050713c3d51cebbee67f7a186d9a24a1";

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        mapView.getMapAsync(map -> {
            mapboxMap=map;
            mapboxMap.setStyle(Barikoi_Bangla, style -> {
                //for test markers and infowindow.For run this program we have to comment belows condition
                showMarkerAndInfowindow(mapboxMap);
                //this is the main part (condition start)
                if(checkLocationPermission()){
                    setMapcurrentLocationLayer();
                }else{
                    requestLocationPermission();
                }
                // condition end
            });

        });

    }

    private void setMapcurrentLocationLayer() {
        if(ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
        ){

            return;
        }

        if(mapboxMap!=null){
            Style style = mapboxMap.getStyle();
            LocationComponent locationComponent = mapboxMap.getLocationComponent();
            LatLng latLng = new LatLng(23.0,90.7) ;
            LocationComponentOptions locationComponentOptions =
                    LocationComponentOptions.builder(this)
                            .pulseEnabled(true)
                            .bearingTintColor(Color.RED)
                            .compassAnimationEnabled(true)
                            .build();
            if (style != null) {
                LocationComponentActivationOptions locationComponentActivationOptions =
                        buildLocationComponentActivationOptions(style, locationComponentOptions);

                locationComponent.activateLocationComponent(locationComponentActivationOptions);
                locationComponent.setLocationComponentEnabled(true);
                locationComponent.setCameraMode(CameraMode.TRACKING_GPS);

            }
            fetchNearbyBanks(latLng);

        }

    }

    private LocationComponentActivationOptions buildLocationComponentActivationOptions(
            Style style,
            LocationComponentOptions locationComponentOptions
    ) {
        return LocationComponentActivationOptions
                .builder(this,style)
                .locationComponentOptions(locationComponentOptions)
                .useDefaultLocationEngine(true)
                .locationEngineRequest(
                        new LocationEngineRequest.Builder(750)
                                .setFastestInterval(750)
                                .setPriority(LocationEngineRequest.PRIORITY_HIGH_ACCURACY)
                                .build()
                )
                .build();
    }

    private boolean checkLocationPermission() {
        return ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED;

    }

    private void requestLocationPermission() {
        ActivityResultLauncher<String[]> locationPermissionRequest = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                permissions -> {
                    if (Boolean.TRUE.equals(permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false))) {
                        // After permission granted, set the current location layer in the map
                        setMapcurrentLocationLayer();
                    } else if (Boolean.TRUE.equals(permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false))) {
                        // Only approximate location access granted.
                        setMapcurrentLocationLayer();
                    } else {
                        // No location access granted.
                        Toast.makeText(this, "Location permission denied, cannot get nearby places", Toast.LENGTH_LONG).show();
                    }
                });

        locationPermissionRequest.launch(new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        });
    }

    private void fetchNearbyBanks(LatLng location) {
        OkHttpClient client = new OkHttpClient();
        String nearbyUrl = "https://barikoi.xyz/v1/api/search/nearby/bkoi_1a742b1c6c2e94c2996f6ae252581746050713c3d51cebbee67f7a186d9a24a1" +
                "?latitude=" + location.getLatitude() +
                "&longitude=" + location.getLongitude() +
                "&radius=1000"; // Adjust the radius as needed

        Request request = new Request.Builder()
                .url(nearbyUrl)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    runOnUiThread(() -> addMarkersFromApi(responseData));
                } else {
                    Log.e("API Error", "Error fetching nearby banks");
                }
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("API Error", "Failed to connect to the API");
            }
        });
    }

    private void addMarkersFromApi(String responseData) {
        try {
            JSONArray jsonArray = new JSONArray(responseData);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject place = jsonArray.getJSONObject(i);

                double lat = place.getDouble("latitude");
                double lng = place.getDouble("longitude");
                String name = place.getString("name");

                // Add marker to the map
                Marker bangladesh = mapboxMap.addMarker(new MarkerOptions()
                        .position(new LatLng(lat, lng))
                        .title(name));
                bangladesh.showInfoWindow(mapboxMap,mapView);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showMarkerAndInfowindow(MapboxMap map){
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng( 23.706237158171664, 90.46174629547735))
                .zoom(15.0)
                .build();

        map.setCameraPosition(cameraPosition);

        Marker Bangladesh = map.addMarker(
                new MarkerOptions()
                        .position(new LatLng(23.706237158171664, 90.46174629547735))
                        .title("Bangladesh"));
        Bangladesh.showInfoWindow(map,mapView);
    }



    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }
}