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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;


import com.example.donona.adapter.BlogAdapter;
import com.example.donona.adapter.TestAdapter;
import com.example.donona.databinding.ActivityVietMapMapViewBinding;
import com.example.donona.model.CoffeePlace;
import com.example.donona.model.CurrentCenterPoint;
import com.example.donona.util.IconUtils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Point;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.Permission;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import vn.vietmap.services.android.navigation.ui.v5.camera.CameraOverviewCancelableCallback;
import vn.vietmap.services.android.navigation.ui.v5.listeners.NavigationListener;
import vn.vietmap.services.android.navigation.v5.location.replay.ReplayRouteLocationEngine;
import vn.vietmap.services.android.navigation.v5.milestone.Milestone;
import vn.vietmap.services.android.navigation.v5.milestone.MilestoneEventListener;
import vn.vietmap.services.android.navigation.v5.navigation.NavigationEventListener;
import vn.vietmap.services.android.navigation.v5.navigation.NavigationMapRoute;
import vn.vietmap.services.android.navigation.v5.navigation.NavigationRoute;
import vn.vietmap.services.android.navigation.v5.navigation.VietmapNavigation;
import vn.vietmap.services.android.navigation.v5.navigation.VietmapNavigationOptions;
import vn.vietmap.services.android.navigation.v5.offroute.OffRouteListener;
import vn.vietmap.services.android.navigation.v5.route.FasterRouteListener;
import vn.vietmap.services.android.navigation.v5.routeprogress.ProgressChangeListener;
import vn.vietmap.services.android.navigation.v5.routeprogress.RouteProgress;
import vn.vietmap.services.android.navigation.v5.snap.SnapToRoute;
import vn.vietmap.vietmapsdk.Vietmap;
import vn.vietmap.vietmapsdk.annotations.Marker;
import vn.vietmap.vietmapsdk.annotations.MarkerOptions;
import vn.vietmap.vietmapsdk.annotations.Polygon;
import vn.vietmap.vietmapsdk.annotations.Polyline;
import vn.vietmap.vietmapsdk.annotations.PolylineOptions;
import vn.vietmap.vietmapsdk.camera.CameraPosition;
import vn.vietmap.vietmapsdk.camera.CameraUpdate;
import vn.vietmap.vietmapsdk.camera.CameraUpdateFactory;
import vn.vietmap.vietmapsdk.geometry.LatLng;
import vn.vietmap.vietmapsdk.geometry.LatLngBounds;
import vn.vietmap.vietmapsdk.location.LocationComponent;
import vn.vietmap.vietmapsdk.location.LocationComponentActivationOptions;
import vn.vietmap.vietmapsdk.location.LocationComponentOptions;
import vn.vietmap.vietmapsdk.location.engine.LocationEngine;
import vn.vietmap.vietmapsdk.location.engine.LocationEngineCallback;
import vn.vietmap.vietmapsdk.location.engine.LocationEngineDefault;
import vn.vietmap.vietmapsdk.location.engine.LocationEngineResult;
import vn.vietmap.vietmapsdk.location.modes.CameraMode;
import vn.vietmap.vietmapsdk.location.modes.RenderMode;
import vn.vietmap.vietmapsdk.maps.Image;
import vn.vietmap.vietmapsdk.maps.MapView;
import vn.vietmap.vietmapsdk.maps.OnMapReadyCallback;
import vn.vietmap.vietmapsdk.maps.Style;
import vn.vietmap.vietmapsdk.maps.VietMapGL;
import vn.vietmap.vietmapsdk.maps.VietMapGLOptions;



// To do next: set up proper onmarker click listener
public class NearActivity extends AppCompatActivity implements NavigationEventListener, FasterRouteListener, ProgressChangeListener, MilestoneEventListener, OffRouteListener {
    private MapView mapView;
    private VietMapGL vietMapGL;

//    private List<Polyline> polylines = null;
//    private ArrayList<PolylineOptions> polylineOptions = new ArrayList<>();
//    private Polygon polygon = null;

//    private static final String STATE_POLYLINE_OPTIONS = "polylineOptions";
//    private static final LatLng HOCHIMINH = new LatLng(10.791257, 106.669189);
    private LocationComponent locationComponent;
    private LocationEngine locationEngine;

    private ArrayList<String> suggestionName = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private OkHttpClient client = new OkHttpClient();
    private Call call;
    private HashMap<String, String> suggestionMap = new HashMap<>();
    private Handler handler;
    private int zoom = 17;
    private int tilt = 23;
    private HashMap<String, String> markerRefid = new HashMap<>();
    private Marker focusMarker = null;
    private FirebaseFirestore db = null;
    private boolean isMarkerCurrentlyFocus = false;
    private FirebaseAuth auth;
    private ActivityVietMapMapViewBinding binding;
    private TestAdapter testAdapter;
    private String searchApi = "http://10.0.2.2:5528/api/search?text=";

    //Variables for startNavigation
    private boolean isOverviewing;
    private boolean isNavigationCanceled;
    private DirectionsRoute currentRoute = null;
    private boolean simulateRoute = false;
    private VietmapNavigation navigation;
    private boolean isRunning = false;
    private SnapToRoute snapEngine;
    private boolean isNavigationInProgress = false;
    private CurrentCenterPoint currentCenterPoint = null;
    private FusedLocationProviderClient fusedLocationClient = null;
    private VietMapGLOptions options = null;
    private VietmapNavigationOptions navigationOptions = VietmapNavigationOptions.builder().build();
    private List<DirectionsRoute> directionsRoutes = new ArrayList<>();
    private LatLng destination = null;
    private LatLng origin = null;
    private NavigationMapRoute navigationMapRoute = null;
    private boolean isBuildingRoute = false;
    private int[] padding = {150, 500, 150, 500};

    private String apiKey = "77080684e9ccee64241cc6682a316130a475ee2eb26bb04d";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /// Initialization
        Vietmap.getInstance(this);

        // Search featr
//        adapter = new ArrayAdapter<>(this, R.layout.list_place, new ArrayList<>());
//        adapter.setNotifyOnChange(true);


        handler = new Handler();

        // Setting up layout
        EdgeToEdge.enable(this);
        binding = ActivityVietMapMapViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        mapView = findViewById(R.id.vmMapView);
        mapView.onCreate(savedInstanceState);

        // Recycler view
        testAdapter = new TestAdapter(suggestionName, this::onSearch);
        binding.recyclerView2.setAdapter(testAdapter);
        binding.recyclerView2.setLayoutManager(new LinearLayoutManager(this));
        binding.searchResult.setOnClickListener(this::onSearchResultClick);


        // Ask for map permission
        if (!checkPermission()) {
            askPermission();
            return;
        }
        createMap();
    }

    // Base activity operation
    @Override
    protected void onStart() {
        super.onStart();
        testAdapter.setSuggestion(suggestionName);
        testAdapter.notifyItemInserted(testAdapter.getItemCount() - 1);
        binding.search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                submitSearchRequest(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                suggestionName.clear();
                testAdapter.notifyDataSetChanged();
                if (call != null) {
                    Log.d("TESTINGHELLO", "Cancel request");
                    call.cancel();
                }
                binding.loading.setVisibility(View.VISIBLE);
                String url = "https://maps.vietmap.vn/api/autocomplete/v3?apikey=" + apiKey + "&text=" + newText + "&cityId=12";
                getSearchSuggestion(url, newText, false);
                return true;
            }
        });
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

    
    private Marker addMarker(LatLng position, String name, String address, boolean focus) {
        int color = R.color.orange;
        if (focus) {
            color = R.color.red;
        }
        return vietMapGL.addMarker(
                new MarkerOptions()
                        .position(position)
                        .title(name)
                        .snippet(address)
                        .icon(
                                IconUtils.drawableToIcon(
                                        this,
                                        R.drawable.pin_drop_24px,
                                        ResourcesCompat.getColor(getResources(), color, getTheme())
                                )
                        )
        );
    }

    private void setFocusMarker(Marker marker, boolean focus) {
        isMarkerCurrentlyFocus = focus;
        handler.post(() -> {
            if (!focus) {
                marker.setIcon(
                        IconUtils.drawableToIcon(
                                this,
                                R.drawable.pin_drop_24px,
                                ResourcesCompat.getColor(getResources(), R.color.orange, getTheme())
                        )
                );
                return;
            }
            marker.setIcon(
                    IconUtils.drawableToIcon(
                            this,
                            R.drawable.location_on_24px,
                            ResourcesCompat.getColor(getResources(), R.color.red, getTheme())
                    )
            );
        });
    }

    private void createMap() {

        initLocationEngine();
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(VietMapGL map) {
                vietMapGL = map;

                // Setup navigation
                fusedLocationClient = LocationServices.getFusedLocationProviderClient(NearActivity.this);
                options = VietMapGLOptions.createFromAttributes(NearActivity.this).compassEnabled(false);
                mapView = new MapView(NearActivity.this, options);
                navigation = new VietmapNavigation(NearActivity.this, navigationOptions, locationEngine);
                mapView.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(@NonNull VietMapGL vietMapGL) {
                        return;
                    }
                });


                // Add VietMap raster style to VietMapSDK - Can't add vector style
                String url = "https://maps.vietmap.vn/api/maps/light/styles.json?apikey=";
                vietMapGL.setStyle(new Style.Builder()
                                .fromUri(url + apiKey)
                        , style -> {
                            initLocationEngine();
                            enableLocationComponent(style);
                            populateMap();
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

                // start navigation
                ImageButton navButton = (ImageButton) findViewById(R.id.startNavigation);
                navButton.setOnClickListener(new ImageButton.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d("TEST", "Navigation start");
                        isNavigationInProgress = true;
                        fetchRoute();
                    }
                });

                // Identify route
                ImageButton routeButton = (ImageButton) findViewById(R.id.findRoute);
                routeButton.setOnClickListener(new ImageButton.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d("TEST", "Find route");
                        isNavigationInProgress = false;
                        fetchRoute();
                    }
                });

                // Stop navigation
                ImageButton closeButton = (ImageButton) findViewById(R.id.stopNavigation);
                closeButton.setOnClickListener(new ImageButton.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d("TEST", "Close navigation");
                        isNavigationInProgress = false;
                        stopNavigation();
                    }
                });

                // Custom marker click event handler
                vietMapGL.setOnMarkerClickListener(new VietMapGL.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(@NonNull Marker marker) {
//                        if (focusMarker != null) {
//                            Log.d("TEST", "reset marker");
//                            setFocusMarker(focusMarker,false);
//                        }
                        stopNavigation();

                        focusMarker = marker;
                        setFocusMarker(focusMarker,true);
                        // if set a custom marker click listener handler, the tool tip will not display
                        String key = marker.getTitle() + " " + marker.getSnippet();
                        if (markerRefid.containsKey(key)) {
                            Log.d("TEST MARKER CLICK", markerRefid.get(key));
                            String refID = markerRefid.get(key);
                            if (refID.charAt(0) == 'f') {
                                addMarkerToMap("", refID, true);
                            }
                            else {
                                String url = "https://maps.vietmap.vn/api/place/v3?apikey=" + apiKey + "&refid=" + refID;
                                addMarkerToMap(url, "", true);
                            }
                        }
                        else {
                            Log.d("TEST MARKER CLICK", "Can't find ID");
                        }
                        return true;
                    }
                });


            }
        });
//        AutoCompleteTextView autoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.search_bar);
//        autoCompleteTextView.setThreshold(1);
//        autoCompleteTextView.setAdapter(adapter);

//        autoCompleteTextView.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//                return;
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                if (s.toString().length() < 6) {
//                    return;
//                }
                // May cause exceed in API calls, focusing on ho chi minh city
//                suggestionName.clear();
//                suggestionMap.clear();
//                adapter.clear();
//                Log.d("TESTHELLO", s.toString());
//                if (call != null) call.cancel();
//                String url = "https://maps.vietmap.vn/api/autocomplete/v3?apikey=" + apiKey + "&text=" + s.toString() + "&cityId=12";
//                getSearchSuggestion(url, s.toString(), false);
            }

//            @Override
//            public void afterTextChanged(Editable s) {
//                return;
//            }
//        });

//        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Log.d("TEST", suggestionName.get(position));
//            }
//        });
//    }

//    public void onClickSearch(View view) {
//        Log.d("TEST", "Search button click");
//        AutoCompleteTextView atct = (AutoCompleteTextView) findViewById(R.id.search_bar);
//        String searchName = atct.getText().toString();
//        Log.d("TEST", searchName);
//        if (!suggestionMap.containsKey(searchName)) {
//            closeKeyboard();
//            atct.clearFocus();
//            return;
//        }
//        if (navigationMapRoute != null) {
//            navigationMapRoute.removeRoute();
//        }
//        stopNavigation();
//        closeKeyboard();
//        atct.clearFocus();
//        String refID = suggestionMap.get(searchName);
//        Log.d("TEST", refID);
//        // Fetch api
//        String url = "https://maps.vietmap.vn/api/place/v3?apikey=" + apiKey + "&refid=" + refID;
//        addMarkerToMap(url, refID, true);
//    }

    private void getSearchSuggestion(String url, String text, boolean placeMarker) {
        if (text.isEmpty()) {
            suggestionName.clear();
            testAdapter.notifyDataSetChanged();
            binding.loading.setVisibility(View.INVISIBLE);
            return;
        }
        Request req = new Request.Builder()
                .url(searchApi + text)
                .build();
        call = client.newCall(req);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                return;
            }
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String body = response.body().string();

                try {
                    JSONObject res = new JSONObject(body);
                    Iterator<String> keys = res.keys();
                    while (true) {
                        try {
                            String key = keys.next();
                            suggestionName.add(key);
                            suggestionMap.put(key, res.getString(key));
                        } catch (Exception e) {
                            break;
                        }
                    }
                    handler.post(() -> {
                        binding.loading.setVisibility(View.INVISIBLE);
                        testAdapter.notifyDataSetChanged();
                    });

                }
                catch (Exception e) {
                    Log.d("TESTING", e.toString());
                }

            }
        });
    }


    private void addMarkerToMap(String url, String refId, boolean focus) {
        if (refId.isEmpty()) {
            addVietMapMarkerToMap(url, refId, focus);
            return;
        }
        if (refId.charAt(0) != 'f') {
            addVietMapMarkerToMap(url, refId, focus);
            return;
        }
        addFireMarkerToMap(refId, focus);
    }

    private void addVietMapMarkerToMap(String url, String refId, boolean focus) {
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
                    String address = res.getString("hs_num") + " " + res.getString("street") + " " + res.getString("city") + " " + res.getString("district") + res.getString("ward");
                    LatLng pos = new LatLng(res.getDouble("lat"), res.getDouble("lng"));
                    destination = new LatLng(res.getDouble("lat"), res.getDouble("lng")); // store potential destination for navigation
                    // Store ref Id for pre loaded marker
                    if (!refId.isEmpty()) {
                        markerRefid.put(placeName + " " + address, refId);
                    }
                    handler.post(() -> {
                        Marker mid = addMarker(pos, placeName, address, focus);
                        if (focusMarker != null) {
                            Log.d("TEST", "reset marker");
                            setFocusMarker(focusMarker,false);
                        }

                        focusMarker = mid;
                        if (focus) {
                            setFocusMarker(mid,true);
                        }
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

    private void addFireMarkerToMap(String refId, boolean focus) {
        db.collection("coffeePlace")
                .whereEqualTo("ref_id", refId)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (!task.isSuccessful()) {
                            Log.d("TEST", "Get data failed");
                            return;
                        }
                        if (task.getResult().isEmpty()) {
                            Log.d("TEST", "Data is empty");
                            return;
                        }
                        DocumentSnapshot res = task.getResult().getDocuments().get(0);
                        Log.d("TEST", res.getString("address"));
                        String placeName = res.getString("name");
                        String address = res.getString("address");
                        LatLng pos = new LatLng(res.getDouble("lat"), res.getDouble("lng"));
                        destination = new LatLng(res.getDouble("lat"), res.getDouble("lng")); // store potential destination for navigation
                        // Store ref Id for pre loaded marker
                        if (!refId.isEmpty()) {
                            markerRefid.put(placeName + " " + address, refId);
                        }
                        handler.post(() -> {
                            Marker mid = addMarker(pos, placeName, address, focus);
                            if (focusMarker != null) {
                                Log.d("TEST", "reset marker");
                                setFocusMarker(focusMarker,false);
                            }

                            focusMarker = mid;
                            if (focus) {
                                setFocusMarker(mid,true);
                            }
                            if (focus) focusCamera(pos);
                            if (focus) displayPlaceInfo(res);
                        });
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
        ImageView thumbnail = (ImageView) findViewById(R.id.coffee_thumbnail);

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
        thumbnail.setVisibility(View.INVISIBLE);


    }

    private void displayPlaceInfo(DocumentSnapshot doc) {
        CardView cardView = (CardView) findViewById(R.id.card_view);
        cardView.setVisibility(View.VISIBLE);

        TextView nameView = (TextView) findViewById(R.id.place_name);
        TextView addressView = (TextView) findViewById(R.id.place_address);
        ImageView thumbnail = (ImageView) findViewById(R.id.coffee_thumbnail);

        nameView.setText(doc.getString("name"));
        addressView.setText(doc.getString("address"));
        thumbnail.setVisibility(View.VISIBLE);
        Picasso.get().load(doc.getString("image")).resize(300, 0).into(thumbnail);
    }


    private void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

//    private void getSearchSuggestion(String url, String text, boolean placeMarker) {
//        if (text.isEmpty()) return;
//        db.collection("coffeePlace")
//                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (!task.isSuccessful() || task.getResult().isEmpty()) {
//                            Log.d("TEST", "Result not valid");
//                            return;
//                        }
//
//                        for (QueryDocumentSnapshot doc : task.getResult()) {
//                            if (suggestionName.size() >= 10) break;
//                            String name = doc.getString("name");
//                            if (name.toLowerCase().contains(text.toLowerCase().trim())) {
//                                String key = doc.getString("name") + " " + doc.getString("address");
//                                if (suggestionMap.containsKey(key)) continue;
////                                adapter.add(key);
//                                suggestionName.add(key);
//                                suggestionMap.put(key, doc.getString("ref_id"));
//                            }
//                        }
//
//                        // Call vietmap api
//                        Request req = new Request.Builder()
//                                .url(url)
//                                .build();
//                        call = client.newCall(req);
//                        call.enqueue(new Callback() {
//                            @Override
//                            public void onFailure(@NonNull Call call, @NonNull IOException e) {
//                                return;
//                            }
//
//                            @Override
//                            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
//                                String body = response.body().string();
//                                try {
//                                    JSONArray res = new JSONArray(body);
//                                    Log.d("TESTHELLO", "3");
//
//                                    for (int i = 0; i < res.length(); i++) {
//
//                                        if (suggestionName.size() >= 10) {
//                                            break;
//                                        }
//
//
//                                        JSONObject mid = res.getJSONObject(i);
//                                        String name = mid.getString("name") + " " + mid.getString("address");
//                                        if (placeMarker) {
//                                            String placeUrl = "https://maps.vietmap.vn/api/place/v3?apikey=" + apiKey + "&refid=" + mid.getString("ref_id");
//                                            addMarkerToMap(placeUrl, mid.getString("ref_id"), false);
//                                        }
//                                        if (suggestionMap.containsKey(name)) continue;
//                                        Log.d("TESTHELLO1", String.valueOf(adapter.getCount()));
////                                        adapter.addAll(suggestionName);
////                                        adapter.notifyDataSetChanged();
//                                        Log.d("TESTHELLO1", String.valueOf(adapter.getCount()));
//                                        suggestionName.add(name);
//                                        suggestionMap.put(name, mid.getString("ref_id"));
//                                    }
//
//
//                                } catch (JSONException e) {
//                                    Log.e("JSON", e.getMessage());
//                                }
//
////                                handler.post(() -> {
////                                    // Notify changes in suggesstion
////                                    Log.d("TESTHELLO", text);
////                                    Log.d("TESTHELLO", adapter.toString());
//////                                    adapter.clear();
//////                                    adapter.addAll(suggestionName);
////                                });
//                                Log.d("TESTHELLO", "2");
//                                Log.d("TESTHELLO", String.valueOf(adapter.getCount()));
//                                adapter.clear();
//                                adapter.addAll(suggestionName);
//                                adapter.notifyDataSetChanged ();
//                                Log.d("TESTHELLO", String.valueOf(adapter.getCount()));
//                            }
//                        });
//                    }
//                });
//
//
//    }

    // Add firebase coffee place to map as marker when first load
    private void getFirebaseSuggesstion() {
        db.collection("coffeePlace").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot doc : task.getResult()) {
                                CoffeePlace mid = new CoffeePlace();
                                mid.setAddress(doc.getString("address"));
                                addMarkerToMap("", doc.getString("ref_id"), false);
                            }
                        }
                        else {
                            Log.e("TEST", "Can't get data");
                        }
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
                .minZoomIconScale((float) 0.1)
                .build();

        if (locationComponent != null) {
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
//        int[] vietmapTrackingMode = {CameraMode.NONE, CameraMode.TRACKING, CameraMode.TRACKING_COMPASS, CameraMode.TRACKING_GPS};
        int[] vietmapTrackingMode = {CameraMode.NONE, CameraMode.TRACKING, CameraMode.TRACKING_GPS};
        locationComponent.setCameraMode(vietmapTrackingMode[0]);
    }

    private void updateMyLocationRenderMode() {
//        int[] vietmapRenderMode = {RenderMode.NORMAL, RenderMode.COMPASS, RenderMode.GPS};
        int[] vietmapRenderMode = {RenderMode.NORMAL, RenderMode.GPS};
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

    private void populateMap() {
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
                origin = new LatLng(location.getLatitude(), location.getLongitude()); // store user location as origin

                // Populate map
                getFirebaseSuggesstion();

                // find near coffee place (update find near place method later)
//                String text = "coffee";
//                String focus = String.valueOf(userLoc.getLatitude()) + "," + String.valueOf(userLoc.getLongitude());
//                String circleRadius = "200";
//                String circleCenter = focus;
//                String url = "https://maps.vietmap.vn/api/autocomplete/v3?apikey=" + apiKey + "&text=" + text + "&circle_center=" + circleCenter + "&circle_radius=" + circleRadius;
                // Fetch data from firestore
                // Get search suggestion
//                getSearchSuggestion(url, "Near me", true);

            }

            @Override
            public void onFailure(@NonNull Exception e) {}
        });
    }

    // Navigation function
    private void overViewRoute() {}

    private void clearRoute() {}

    private void startNavigation() {
        tilt = 60;
        zoom = 17;
        isOverviewing = false;
        isNavigationCanceled = false;

        if (vietMapGL != null && vietMapGL.getLocationComponent() != null) {
            vietMapGL.getLocationComponent().setCameraMode(CameraMode.TRACKING_GPS_NORTH);
        }

        if (currentRoute != null) {
            if (simulateRoute) {
                ReplayRouteLocationEngine mockLocationEngine = new ReplayRouteLocationEngine();
                mockLocationEngine.assign(currentRoute);
                navigation.setLocationEngine(mockLocationEngine);
            } else {
                if (locationEngine != null) {
                    navigation.setLocationEngine(locationEngine);
                }
            }

            isRunning = true;

            if (vietMapGL != null && vietMapGL.getLocationComponent() != null) {
                vietMapGL.getLocationComponent().setLocationEngine(null);
            }

            navigation.addNavigationEventListener(this);
            navigation.addFasterRouteListener(this);
            navigation.addMilestoneEventListener(this);
            navigation.addOffRouteListener(this);
            navigation.addProgressChangeListener(this);
            navigation.setSnapEngine(snapEngine);

            if (currentRoute != null) {
                isNavigationInProgress = true;
                navigation.startNavigation(currentRoute);
                recenter();
            }
        }
    }

    private void recenter() {
        isOverviewing = false;
        if (currentCenterPoint != null) {
            focusCamera(new LatLng(currentCenterPoint.getLatitude(), currentCenterPoint.getLongitude()));
//            moveCamera(
//                    new LatLng(currentCenterPoint.getLatitude(), currentCenterPoint.getLongitude()),
//                    currentCenterPoint.getBearing()
//            );
        }
    }

    private void fetchRoute() {
        if (origin == null || destination == null) {
            return;
        }
        Log.d("TEST", "Fetching route");
        NavigationRoute.builder(NearActivity.this).apikey(apiKey)
                .origin(Point.fromLngLat(origin.getLongitude(), origin.getLatitude()))
                .destination(Point.fromLngLat(destination.getLongitude(), destination.getLatitude()))
                .build().getRoute(new retrofit2.Callback<DirectionsResponse>() {
                    @Override
                    public void onResponse(retrofit2.Call<DirectionsResponse> call, retrofit2.Response<DirectionsResponse> response) {
                        if (response.body() == null) return;
                        Log.d("TEST", "responding body");
                        directionsRoutes = response.body().routes();
                        currentRoute = directionsRoutes.get(0);

                        // Only show 1 route for now
                        if (navigationMapRoute != null) {
                            navigationMapRoute.removeRoute();
                        }

                        navigationMapRoute = new NavigationMapRoute(mapView, vietMapGL);
                        navigationMapRoute.addRoute(currentRoute);

                        // allow user to choose route ??

                        // get route points
                        isBuildingRoute = false;
                        List<Point> routePoints = (List<Point>) currentRoute.routeOptions().coordinates();
                        animateVietmapGLForRouteOverview(padding, routePoints);

                        // start navigation
                        if (isNavigationInProgress) {
                            startNavigation();
                        }
                    }

                    @Override
                    public void onFailure(retrofit2.Call<DirectionsResponse> call, Throwable t) {
                        Log.d("TEST", "Fail to fetch route");
                    }
                });
    }

    private void stopNavigation() {
        isNavigationInProgress = false;
        isNavigationCanceled = true;
        navigation.stopNavigation();
        navigation.removeFasterRouteListener(this);
        navigation.removeMilestoneEventListener(this);
        navigation.removeNavigationEventListener(this);
        navigation.removeOffRouteListener(this);
        navigation.removeProgressChangeListener(this);

        if (navigationMapRoute != null) {
            navigationMapRoute.removeRoute();
            currentRoute = null;
        }
        CardView cardView = (CardView) findViewById(R.id.card_view);
        cardView.setVisibility(View.INVISIBLE);

        zoom = 17;
        tilt = 23;
//        focusCamera(origin);

        if (focusMarker != null) {
            Log.d("TEST", "reset marker");
            setFocusMarker(focusMarker,false);
        }

    }



    // Animate camera only
    private void animateVietmapGLForRouteOverview(int[] padding, List<Point> routePoints) {
        if (routePoints.size() <= 1) return;
        CameraUpdate resetUpdate = buildResetCameraUpdate();
        CameraUpdate overviewUpdate = buildOverviewCameraUpdate(padding, routePoints);
        vietMapGL.animateCamera(
                resetUpdate, 150, new CameraOverviewCancelableCallback(overviewUpdate, vietMapGL)
        );

    }

    private CameraUpdate buildResetCameraUpdate() {
        CameraPosition resetPosition = new CameraPosition.Builder().tilt(0).bearing(0.0).build();
        return CameraUpdateFactory.newCameraPosition(resetPosition);
    }

    private CameraUpdate buildOverviewCameraUpdate(int[] padding, List<Point> routePoints) {
        LatLngBounds routeBounds = convertRoutePointsToLatLngBounds(routePoints);
        return CameraUpdateFactory.newLatLngBounds(
                routeBounds, padding[0], padding[1], padding[2], padding[3]
        );
    }

    private LatLngBounds convertRoutePointsToLatLngBounds(List<Point> routePoints) {
        ArrayList<LatLng> latLngs = new ArrayList<>();
        for (Point routePoint: routePoints) {
            latLngs.add(new LatLng(routePoint.latitude(), routePoint.longitude()));
        }
        return new LatLngBounds.Builder().includes(latLngs).build();
    }

    public void onClickgetMyLocation(View view) {
        // Show current position if no focus marker
        if (origin != null && !isMarkerCurrentlyFocus) {
            focusCamera(origin);
            return;
        }
        // Show current position with focus marker
        isNavigationInProgress = false;
        NavigationRoute.builder(NearActivity.this).apikey(apiKey)
                .origin(Point.fromLngLat(origin.getLongitude(), origin.getLatitude()))
                .destination(Point.fromLngLat(destination.getLongitude(), destination.getLatitude()))
                .build().getRoute(new retrofit2.Callback<DirectionsResponse>() {
                    @Override
                    public void onResponse(retrofit2.Call<DirectionsResponse> call, retrofit2.Response<DirectionsResponse> response) {
                        if (response.body() == null) return;
                        directionsRoutes = response.body().routes();
                        currentRoute = directionsRoutes.get(0);

                        // get route points
                        isBuildingRoute = false;
                        List<Point> routePoints = (List<Point>) currentRoute.routeOptions().coordinates();
                        animateVietmapGLForRouteOverview(padding, routePoints);
                    }

                    @Override
                    public void onFailure(retrofit2.Call<DirectionsResponse> call, Throwable t) {
                        Log.d("TEST", "Fail to fetch route");
                    }
                });
    }

    private void onSearch(String val) {
        binding.search.setQuery(val, true);
    }


    private void submitSearchRequest(String val) {
        if (!suggestionMap.containsKey(val)) {
            Log.d("TESTINGHELLO", suggestionMap.toString());
             closeKeyboard();
            return;
        }
        if (navigationMapRoute != null) {
            navigationMapRoute.removeRoute();
        }
        stopNavigation();
        closeKeyboard();

        binding.search.setVisibility(View.INVISIBLE);
        binding.recyclerView2.setVisibility(View.INVISIBLE);
        binding.loading.setVisibility(View.INVISIBLE);
        binding.searchResult.setVisibility(View.VISIBLE);
        binding.searchResult.setText(val);
//        Log.d("TESTINGHELLO", suggestionMap.get(val));

        // Add marker to map
        String refID = suggestionMap.get(val);
        String url = "https://maps.vietmap.vn/api/place/v3?apikey=" + apiKey + "&refid=" + refID;
        addMarkerToMap(url, refID, true);
    }

    private void onSearchResultClick(View view) {
        binding.search.setVisibility(View.VISIBLE);
        binding.recyclerView2.setVisibility(View.VISIBLE);
        binding.searchResult.setVisibility(View.INVISIBLE);
    }


    @Override
    public void onRunning(boolean b) {

    }

    @Override
    public void fasterRouteFound(DirectionsRoute directionsRoute) {

    }

    @Override
    public void onProgressChange(Location location, RouteProgress routeProgress) {

    }

    @Override
    public void onMilestoneEvent(RouteProgress routeProgress, String s, Milestone milestone) {


    }

    @Override
    public void userOffRoute(Location location) {

    }
}