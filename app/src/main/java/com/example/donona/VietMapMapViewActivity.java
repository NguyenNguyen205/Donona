package com.example.donona;

import android.content.Context;
import android.content.Intent;
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
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


import com.example.donona.util.IconUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
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

    private List<String> suggesstionName = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private OkHttpClient client = new OkHttpClient();
    private HashMap<String, String> suggestionMap = new HashMap<>();
    private Handler handler;
    private Marker currentFocus = null;

    private String apiKey = "77080684e9ccee64241cc6682a316130a475ee2eb26bb04d";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /// Initialization
        Vietmap.getInstance(this);
        adapter = new ArrayAdapter<>(this, R.layout.list_place, suggesstionName);
        handler = new Handler();

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

//                vietMapGL.addOnMapClickListener(new VietMapGL.OnMapClickListener() {
//                    @Override
//                    public boolean onMapClick(@NonNull LatLng latLng) {
////                        addMarker(HOCHIMINH);
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
                getVietMapSuggestion(url);

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

    private Marker addMarker(LatLng position, String name) {
        if (currentFocus != null) {
            vietMapGL.removeMarker(currentFocus);
        }
        currentFocus = vietMapGL.addMarker(
                new MarkerOptions()
                        .position(position)
                        .title(name)
                        .snippet("Sai lam qua lon anh thua nen anh chiu")
                        .icon(
                                IconUtils.drawableToIcon(
                                        this,
//                                        R.drawable.pin_drop_24px,
                                        R.drawable.pin_drop_24px,
                                        ResourcesCompat.getColor(getResources(), R.color.red, getTheme())
                                )
                        )
        );
        return currentFocus;
    }

    public void onClickReturn(View view) {
        Intent intent = new Intent(VietMapMapViewActivity.this, HomeActivity.class);
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
        // Fetch lat and long and show on map
        String url = "https://maps.vietmap.vn/api/place/v3?apikey=" + apiKey + "&refid=" + refID;
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
                    LatLng pos = new LatLng(res.getDouble("lat"), res.getDouble("lng"));
                    handler.post(() -> {
                       addMarker(pos, searchName);
                       moveCamera(pos);
                       displayPlaceInfo(res);
                    });
                }
                catch (Exception e) {
                    Log.e("ERROR", e.getMessage().toString());
                }

            }
        });


    }

    private void moveCamera(LatLng location) {

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(location)
                .zoom(15)
                .tilt(30)
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
        if (view == null) {
//            Log.d("TEST", "Hello again");
        }
        if (view != null) {
            InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
    private void getVietMapSuggestion(String url) {
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
//                    JSONObject res = new JSONObject(response.body().string());
                    JSONArray res = new JSONArray(body);
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
//                Log.d("TEST", String.valueOf(suggesstionName.size()));
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

}