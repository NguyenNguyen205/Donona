package com.example.donona;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


import com.example.donona.util.IconUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import vn.vietmap.vietmapsdk.Vietmap;
import vn.vietmap.vietmapsdk.annotations.Icon;
import vn.vietmap.vietmapsdk.annotations.Marker;
import vn.vietmap.vietmapsdk.annotations.MarkerOptions;
import vn.vietmap.vietmapsdk.annotations.Polygon;
import vn.vietmap.vietmapsdk.annotations.Polyline;
import vn.vietmap.vietmapsdk.annotations.PolylineOptions;
import vn.vietmap.vietmapsdk.geometry.LatLng;
import vn.vietmap.vietmapsdk.maps.MapView;
import vn.vietmap.vietmapsdk.maps.OnMapReadyCallback;
import vn.vietmap.vietmapsdk.maps.Style;
import vn.vietmap.vietmapsdk.maps.VietMapGL;

public class VietMapMapViewActivity extends AppCompatActivity {

    private MapView mapView;
    private VietMapGL vietMapGL;

    private List<Polyline> polylines = null;
    private ArrayList<PolylineOptions> polylineOptions = new ArrayList<>();
    private Polygon polygon = null;

    private static final String STATE_POLYLINE_OPTIONS = "polylineOptions";
    private static final LatLng HOCHIMINH = new LatLng(10.791257, 106.669189);
    private static final LatLng NINHTHUAN = new LatLng(11.550254, 108.960579);
    private static final LatLng DANANG = new LatLng(16.045746, 108.202241);
    private static final LatLng HUE = new LatLng(16.469602, 107.577462);
    private static final LatLng NGHEAN = new LatLng(18.932151, 105.577207);
    private static final LatLng HANOI = new LatLng(21.024696, 105.833099);

    private List<String> autocompleteData = new ArrayList<>(Arrays.asList("Testing", "Test again"));
    private ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.list_place, autocompleteData);
    private OkHttpClient client = new OkHttpClient();

    private String apiKey = "";

    private Marker addMarker(LatLng position) {
        return vietMapGL.addMarker(
                new MarkerOptions()
                        .position(position)
                        .title("Arisu")
                        .snippet("Sai lam qua lon anh thua nen anh chiu")
                        .icon(
                                IconUtils.drawableToIcon(
                                        this,
                                        R.drawable.ic_launcher_foreground,
                                        ResourcesCompat.getColor(getResources(), R.color.black, getTheme())
                                )
                        )
        );
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /// Initialize vietmap SDK, you must add this line to ensure that the SDK works properly, without crashing
        Vietmap.getInstance(this);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_viet_map_map_view);


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mapView = findViewById(R.id.vmMapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(VietMapGL map) {
                vietMapGL = map;

                // Add VietMap vector style to VietMapSDK
                String url = "https://maps.vietmap.vn/api/maps/light/styles.json?apikey=";
                vietMapGL.setStyle(new Style.Builder()
                        .fromUri(url + apiKey)
                );

                vietMapGL.setOnPolylineClickListener(new VietMapGL.OnPolylineClickListener() {
                    @Override
                    public void onPolylineClick(Polyline polyline) {
                        Toast.makeText(
                                VietMapMapViewActivity.this,
                                "You clicked on polyline with id = " + polyline.getId(),
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });

                vietMapGL.addOnMapClickListener(new VietMapGL.OnMapClickListener() {
                    @Override
                    public boolean onMapClick(@NonNull LatLng latLng) {
                        addMarker(HOCHIMINH);
                        return true;
                    }
                });
            }
        });

        // Set autocomplete place, which is empty when first started
        AutoCompleteTextView autoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.search_bar);
        autoCompleteTextView.setThreshold(1);
        autoCompleteTextView.setAdapter(adapter);
        autoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                return;
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Focus around Ho Chi Minh city during development
                Log.d("TEST", s.toString());
                if (s.toString().length() <  6) {
                    return;
                }
                // May cause exceed in API calls
                String url = "https://maps.vietmap.vn/api/autocomplete/v3?apikey=" + apiKey + "&text=" + s.toString() + "&focus=10.791257,106.669189";
                getVietMapSuggestion(url);

                if (s.toString().equals("Hello")) {
                    autocompleteData.add("Hello");
                    adapter.insert(s.toString(), adapter.getCount());
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                return;
            }
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

    public void onClickReturn(View view) {
        Intent intent = new Intent(VietMapMapViewActivity.this, HomeActivity.class);
        startActivity(intent);
    }

    private String sendVietMapRequest(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();
        try (Response res = client.newCall(request).execute()) {
            return res.body().string();
        }
    }

    private void getVietMapSuggestion(String url) {
        Request req = new Request.Builder()
                .url(url)
                .build();
        Call call = client.newCall(req);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                return;
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                Log.d("TEST", response.body().string());

            }
        });

    }

}