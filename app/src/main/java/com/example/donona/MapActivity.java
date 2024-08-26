package com.example.donona;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import vn.vietmap.vietmapsdk.Vietmap;
import vn.vietmap.vietmapsdk.annotations.Polyline;
import vn.vietmap.vietmapsdk.geometry.LatLng;
import vn.vietmap.vietmapsdk.maps.MapView;
import vn.vietmap.vietmapsdk.maps.Style;
import vn.vietmap.vietmapsdk.maps.VietMapGL;

public class MapActivity extends AppCompatActivity {
    private MapView mapView;
    private VietMapGL vietMapGL;

    // polyline and polygons

    // location
    private LatLng HCM = new LatLng(10.791257, 106.669189);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_map);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Import navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.nav);
        bottomNavigationView.setSelectedItemId(R.id.navigation_home);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            // can't use switch case due to non constant id
            final int itemId = item.getItemId();
            if (itemId == R.id.navigation_home) {
                startActivity(new Intent(MapActivity.this, HomeActivity.class));
                return true;
            }
            if (itemId == R.id.navigation_streaming) {
                startActivity(new Intent(MapActivity.this, StreamingActivity.class));
                finish();
                return true;
            }
            if (itemId == R.id.navigation_map) {
                return true;
            }
            return false;
        });

        // Init vietmap
        Log.d("TEST","hello again once more");
        Vietmap.getInstance(this);
        mapView = findViewById(R.id.vmMapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync((VietMapGL vietMapGL) -> {
            this.vietMapGL = vietMapGL;

            Log.d("TEST", "hello again");
            // Call api
            this.vietMapGL.setStyle(new Style.Builder().fromUri("https://maps.vietmap.vn/api/maps/raster/styles.json?apikey="));
//            this.vietMapGL.setOnPolygonClickListener((Polyline polyline) -> {
//                Toast.makeText()
//            });
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
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        mapView.onSaveInstanceState(outState);
    }
}
