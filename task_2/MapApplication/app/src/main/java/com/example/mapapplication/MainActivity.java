package com.example.mapapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;


import android.view.LayoutInflater;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;

public class MainActivity extends AppCompatActivity {

    private MapView mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);

        //map api = https://map.barikoi.com/styles/barikoi-bangla/style.json?key=bkoi_1a742b1c6c2e94c2996f6ae252581746050713c3d51cebbee67f7a186d9a24a1
        //nearby api = https://barikoi.xyz/v1/api/search/nearby/bkoi_1a742b1c6c2e94c2996f6ae252581746050713c3d51cebbee67f7a186d9a24a1

        //https://api.maptiler.com/maps/streets-v2/style.json?key=D0MPu1Os80X4XMI8Lp2L
        // Get the API Key by app's BuildConfig
        String key = "bkoi_1a742b1c6c2e94c2996f6ae252581746050713c3d51cebbee67f7a186d9a24a1";

        // Find other maps at https://cloud.maptiler.com/maps/
        String mapId = "barikoi-bangla";

        String styleUrl = "https://map.barikoi.com/styles/" + mapId + "/style.json?key=" + key;

        // Init MapLibre
        Mapbox.getInstance(this);

        // Init layout view
        LayoutInflater inflater = LayoutInflater.from(this);
        @SuppressLint("InflateParams") android.view.View rootView = inflater.inflate(R.layout.activity_main, null);
        setContentView(rootView);

        // Init the MapView
        mapView = rootView.findViewById(R.id.mapView);
        mapView.getMapAsync(mapboxMap -> {
            mapboxMap.setStyle(styleUrl);
            mapboxMap.setCameraPosition(
                    new CameraPosition.Builder()
                            .target(new LatLng(0.0, 0.0))
                            .zoom(1.0)
                            .build()
            );
        });
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

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);

    }
}