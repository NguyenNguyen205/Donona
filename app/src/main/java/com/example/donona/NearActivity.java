package com.example.donona;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


import com.example.donona.util.IconUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.Permission;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import vn.vietmap.vietmapsdk.Vietmap;
import vn.vietmap.vietmapsdk.annotations.Marker;
import vn.vietmap.vietmapsdk.annotations.MarkerOptions;
import vn.vietmap.vietmapsdk.annotations.Polygon;
import vn.vietmap.vietmapsdk.annotations.Polyline;
import vn.vietmap.vietmapsdk.annotations.PolylineOptions;
import vn.vietmap.vietmapsdk.camera.CameraPosition;
import vn.vietmap.vietmapsdk.camera.CameraUpdateFactory;
import vn.vietmap.vietmapsdk.geometry.LatLng;
import vn.vietmap.vietmapsdk.location.LocationComponent;
import vn.vietmap.vietmapsdk.location.LocationComponentActivationOptions;
import vn.vietmap.vietmapsdk.location.LocationComponentOptions;
import vn.vietmap.vietmapsdk.location.engine.LocationEngine;
import vn.vietmap.vietmapsdk.location.engine.LocationEngineCallback;
import vn.vietmap.vietmapsdk.location.engine.LocationEngineDefault;
import vn.vietmap.vietmapsdk.location.engine.LocationEngineResult;
import vn.vietmap.vietmapsdk.location.modes.CameraMode;
import vn.vietmap.vietmapsdk.location.modes.RenderMode;
import vn.vietmap.vietmapsdk.maps.MapView;
import vn.vietmap.vietmapsdk.maps.OnMapReadyCallback;
import vn.vietmap.vietmapsdk.maps.Style;
import vn.vietmap.vietmapsdk.maps.VietMapGL;


public class NearActivity extends AppCompatActivity {
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
    private LocationComponent locationComponent;
    private LocationEngine locationEngine;

    private List<String> suggesstionName = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private OkHttpClient client = new OkHttpClient();
    private HashMap<String, String> suggestionMap = new HashMap<>();
    private Handler handler;
    private int zoom = 15;
    private int tilt = 20;

    private String apiKey = "77080684e9ccee64241cc6682a316130a475ee2eb26bb04d";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /// Initialization
        Vietmap.getInstance(this);
        adapter = new ArrayAdapter<>(this, R.layout.list_place, suggesstionName);
        handler = new Handler();

        // Setting up layout
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_viet_map_map_view);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        
        mapView = findViewById(R.id.vmMapView);
        mapView.onCreate(savedInstanceState);

        // Ask for map permission
        if (!checkPermission()) {
            askPermission();
            return;
        }
        createMap();
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

    private Marker addMarker(LatLng position, String name) {
        return vietMapGL.addMarker(
                new MarkerOptions()
                        .position(position)
                        .title(name)
                        .snippet("Sai lam qua lon anh thua nen anh chiu")
                        .icon(
                                IconUtils.drawableToIcon(
                                        this,
                                        R.drawable.pin_drop_24px,
                                        ResourcesCompat.getColor(getResources(), R.color.red, getTheme())
                                )
                        )
        );
    }

    private void createMap() {

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(VietMapGL map) {
                vietMapGL = map;

                // Add VietMap raster style to VietMapSDK - Can't add vector style
                String url = "https://maps.vietmap.vn/api/maps/light/styles.json?apikey=";
                vietMapGL.setStyle(new Style.Builder()
                                .fromUri(url + apiKey)
                        , style -> {
                            initLocationEngine();
                            enableLocationComponent(style);
                            addNearPlace();
                        });

                // ?? about usefulness
                vietMapGL.setOnPolylineClickListener(new VietMapGL.OnPolylineClickListener() {
                    @Override
                    public void onPolylineClick(Polyline polyline) {
                        Toast.makeText(
                                NearActivity.this,
                                "You clicked on polyline with id = " + polyline.getId(),
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });

                // Custom marker click event handler
//                vietMapGL.setOnMarkerClickListener(new VietMapGL.OnMarkerClickListener() {
//                    @Override
//                    public boolean onMarkerClick(@NonNull Marker marker) {
//                        // if set a custom marker click listener handler, the tool tip will not display
//                        Log.d("TEST MARKER CLICK", marker.getTitle());
//                        return true;
//                    }
//                });


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
                if (s.toString().length() < 6) {
                    return;
                }
                // May cause exceed in API calls, focusing on ho chi minh city
                String url = "https://maps.vietmap.vn/api/autocomplete/v3?apikey=" + apiKey + "&text=" + s.toString() + "&cityId=12";
                // Limit the calling of API for now
                getVietMapSuggestion(url, false);

            }

            @Override
            public void afterTextChanged(Editable s) {
                return;
            }
        });
    }


    public void onClickReturn(View view) {
        Intent intent = new Intent(NearActivity.this, HomeActivity.class);
        startActivity(intent);
    }

    public void onClickSearch(View view) {
        Log.d("TEST", "Search button click");
        AutoCompleteTextView atct = (AutoCompleteTextView) findViewById(R.id.search_bar);
        String searchName = atct.getText().toString();
        Log.d("TEST", searchName);
        if (!suggestionMap.containsKey(searchName)) {
            closeKeyboard();
            atct.clearFocus();
            return;
        }
        closeKeyboard();
        atct.clearFocus();
        String refID = suggestionMap.get(searchName);
        Log.d("TEST", refID);
        // Fetch api
        String url = "https://maps.vietmap.vn/api/place/v3?apikey=" + apiKey + "&refid=" + refID;
        addMarkerToMap(url, true);
    }

    private void addMarkerToMap(String url, boolean focus) {
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
                String body = response.body().string();
                Log.d("TEST", body);
                try {
                    JSONObject res = new JSONObject(body);
                    String placeName = res.getString("name");
                    LatLng pos = new LatLng(res.getDouble("lat"), res.getDouble("lng"));
                    handler.post(() -> {
                        addMarker(pos, placeName);
                        if (focus) focusCamera(pos);
                        if (focus) displayPlaceInfo(res);
                    });
                }
                catch (Exception e) {
                    Log.e("ERROR", e.getMessage().toString());
                }

            }
        });

    }

    private void focusCamera(LatLng location) {
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(location)
                .zoom(zoom)
                .tilt(tilt)
                .build();
        vietMapGL.animateCamera(
                CameraUpdateFactory.newCameraPosition(cameraPosition), 3000
        );

    }

    private void displayPlaceInfo(JSONObject data) {
        CardView cardView = (CardView) findViewById(R.id.card_view);
        cardView.setVisibility(View.VISIBLE);
        TextView nameView = (TextView) findViewById(R.id.place_name);
        TextView addressView = (TextView) findViewById(R.id.place_address);
        String name = "";
        String address = "";
        try {
            name = data.getString("name");
            address = data.getString("address");
        } catch (JSONException e) {
            Log.e("ERROR", e.getMessage());
        }
        nameView.setText(name);
        addressView.setText(address);
    }

    private void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void getVietMapSuggestion(String url, boolean placeMarker) {
        if (suggesstionName.size() >= 10) {
            return;
        }
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
                String body = response.body().string();
                try {
                    JSONArray res = new JSONArray(body);

                    // If place marker is true, then just loop through the result and add marker to map
                    if (placeMarker) {
                        for (int i = 0; i < res.length(); i++) {
                            JSONObject mid = res.getJSONObject(i);
                            String placeUrl = "https://maps.vietmap.vn/api/place/v3?apikey=" + apiKey + "&refid=" + mid.getString("ref_id");
                            addMarkerToMap(placeUrl, false);
                        }
                        return;
                    }
                    // loop through the result
                    for (int i = 0; i < res.length(); i++) {
                        JSONObject mid = res.getJSONObject(i);
                        // add name
                        suggesstionName.add(mid.getString("name"));
                        // add mapping
                        suggestionMap.put(mid.getString("name"), mid.getString("ref_id"));
                    }
                } catch (JSONException e) {
                    Log.e("JSON", e.getMessage());
                }
                Log.d("TEST", String.valueOf(suggesstionName.size()));
                handler.post(() -> {
                    // Notify changes in suggesstion
                    adapter.clear();
                    adapter.addAll(suggesstionName);
                    adapter.notifyDataSetChanged ();
                });


            }
        });

    }

    private void askPermission() {
        ActivityResultLauncher<String[]> locationPermissionRequest =
                registerForActivityResult(new ActivityResultContracts
                                .RequestMultiplePermissions(), result -> {
                            Boolean fineLocationGranted = result.getOrDefault(
                                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                                    false);
                            Boolean coarseLocationGranted = result.getOrDefault(
                                    android.Manifest.permission.ACCESS_COARSE_LOCATION,false);
                            if (fineLocationGranted != null && fineLocationGranted) {
                                // Precise location access granted.
                                createMap();
                            } else if (coarseLocationGranted != null && coarseLocationGranted) {
                                // Only approximate location access granted.
                                createMap();
                            } else {
                                Log.d("TEST", "No permission granted");
                                // No location access granted.
                                finish();
                                Intent intent = new Intent(NearActivity.this, HomeActivity.class);
                                startActivity(intent);

                            }
                        }
                );
        // Ask for both fine and coarse at the same time
        locationPermissionRequest.launch(new String[] {
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
        });

    }

    private void enableLocationComponent(Style style) {
        locationComponent = vietMapGL.getLocationComponent();
        LocationComponentOptions customIcon = LocationComponentOptions.builder(this)
                .foregroundDrawable(R.drawable.logo)
                .build();

        if (locationComponent != null) {
            Log.d("TEST", "OH HI");
            locationComponent.activateLocationComponent(
                    LocationComponentActivationOptions.builder(
                            this, style
                    ).locationComponentOptions(customIcon)
                    .build()
            );
            if (!checkPermission()) return;
            locationComponent.setLocationComponentEnabled(true);
            locationComponent.setCameraMode(
                    CameraMode.TRACKING_GPS_NORTH, 750L, (double) zoom, 0.0, (double) tilt, null
            );
            locationComponent.zoomWhileTracking(19.0);
            locationComponent.setRenderMode(RenderMode.GPS);
            locationComponent.setLocationEngine(locationEngine);
        }
        updateMyLocationTrackingMode();
        updateMyLocationRenderMode();
    }

    private void updateMyLocationTrackingMode() {
        int[] vietmapTrackingMode = {CameraMode.NONE, CameraMode.TRACKING, CameraMode.TRACKING_COMPASS, CameraMode.TRACKING_GPS};
        locationComponent.setCameraMode(vietmapTrackingMode[0]);
    }

    private void updateMyLocationRenderMode() {
        int[] vietmapRenderMode = {RenderMode.NORMAL, RenderMode.COMPASS, RenderMode.GPS};
        locationComponent.setRenderMode(vietmapRenderMode[0]);

    }

    private boolean checkPermission() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;

    }

    private void initLocationEngine() {
        LocationEngineDefault led = LocationEngineDefault.INSTANCE;
        locationEngine = led.getDefaultLocationEngine(this);
    }

    private void addNearPlace() {
        if (locationEngine == null) return;
        if (!checkPermission()) return;
        locationEngine.getLastLocation(new LocationEngineCallback<LocationEngineResult>() {
            @Override
            public void onSuccess(LocationEngineResult locationEngineResult) {
                // Get lat long
                Location location = locationEngineResult.getLastLocation();
                if (location == null) return;
                Log.d("TEST", "GET location successful");
                LatLng userLoc = new LatLng(location.getLatitude(), location.getLongitude());

                // find near coffee place (update find near place method later)
                String text = "coffee";
                String focus = String.valueOf(userLoc.getLatitude()) + "," + String.valueOf(userLoc.getLongitude());
                String circleRadius = "200";
                String circleCenter = focus;
                String url = "https://maps.vietmap.vn/api/autocomplete/v3?apikey=" + apiKey + "&text=" + text + "&circle_center=" + circleCenter + "&circle_radius=" + circleRadius;
                // fetch api
                getVietMapSuggestion(url, true);

            }

            @Override
            public void onFailure(@NonNull Exception e) {}
        });
    }


}