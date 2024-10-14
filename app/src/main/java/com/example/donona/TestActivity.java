package com.example.donona;

import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.donona.adapter.TestAdapter;
import com.example.donona.databinding.ActivityTestBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TestActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private ArrayAdapter<String> adapter;
    private Handler handler;
//    private List<String> suggestionName = new ArrayList<>();
    private HashMap<String, String> suggestionMap = new HashMap<>();
    private Call call;
    private OkHttpClient client = new OkHttpClient();
    private String apiKey = "77080684e9ccee64241cc6682a316130a475ee2eb26bb04d";
    private String searchApi = "http://10.0.2.2:5528/api/search?text=";


    // Test search view
    private ActivityTestBinding binding;
    private ArrayList<String> suggestion = new ArrayList<>();
    private TestAdapter testAdapter;
    private boolean loading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityTestBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = FirebaseFirestore.getInstance();
        adapter = new ArrayAdapter<>(this, R.layout.list_place, new ArrayList<>());
        handler = new Handler();

        // Recycler view
        testAdapter = new TestAdapter(suggestion, this::onSearch);
        binding.recyclerView2.setAdapter(testAdapter);
        binding.recyclerView2.setLayoutManager(new LinearLayoutManager(this));
        binding.searchResult.setOnClickListener(this::onSearchResultClick);
    }

    @Override
    protected void onStart() {
        super.onStart();
        testAdapter.setSuggestion(suggestion);
        testAdapter.notifyItemInserted(testAdapter.getItemCount() - 1);
        binding.search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                submitSearchRequest(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                suggestion.clear();
                suggestionMap.clear();
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
    }

    private void getSearchSuggestion(String url, String text, boolean placeMarker) {
        if (text.isEmpty()) {
            suggestion.clear();
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
                            suggestion.add(key);
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

    private void onSearch(String val) {
        binding.search.setQuery(val, true);
    }

    private void submitSearchRequest(String val) {
        if (!suggestionMap.containsKey(val)) {
            Log.d("TESTINGHELLO", "Val not found");
            // closeKeyboard()
            return;
        }
//        if (navigationMapRoute != null) {
//            navigationMapRoute.removeRoute();
//        }
//        stopNavigation();
//        closeKeyboard();

        binding.search.setVisibility(View.INVISIBLE);
        binding.recyclerView2.setVisibility(View.INVISIBLE);
        binding.loading.setVisibility(View.INVISIBLE);
        binding.searchResult.setVisibility(View.VISIBLE);
        binding.searchResult.setText(val);
//        Log.d("TESTINGHELLO", suggestionMap.get(val));

        // Add marker to map
//        String refID = suggestionMap.get(val);
//        String url = "https://maps.vietmap.vn/api/place/v3?apikey=" + apiKey + "&refid=" + refID;
//        addMarkerToMap(url, refID, true);
    }

    private void onSearchResultClick(View view) {
        binding.search.setVisibility(View.VISIBLE);
        binding.recyclerView2.setVisibility(View.VISIBLE);
        binding.searchResult.setVisibility(View.INVISIBLE);
    }






}