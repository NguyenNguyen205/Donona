package com.example.donona;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.donona.adapter.BookMarkAdapter;
import com.example.donona.adapter.CoffeePlaceAdapter;
import com.example.donona.databinding.ActivityBookMarkBinding;
import com.example.donona.databinding.ActivityNearMeBinding;
import com.example.donona.model.CoffeePlace;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class BookMarkActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private List<CoffeePlace> coffeePlaceList;
    private ActivityBookMarkBinding binding;
    private BookMarkAdapter bookMarkAdapter;


    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseUser user;

    // Vietmap api
    private String apiKey = "77080684e9ccee64241cc6682a316130a475ee2eb26bb04d";
    private OkHttpClient client = new OkHttpClient();
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBookMarkBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());

        // Khởi tạo danh sách
        coffeePlaceList = new ArrayList<>();

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Khởi tạo adapter
        bookMarkAdapter = new BookMarkAdapter(coffeePlaceList, this::onClickNearMe);
        binding.recyclerView.setAdapter(bookMarkAdapter); // Đặt adapter vào RecyclerView

        // Get user
        user = auth.getCurrentUser();
        if (user == null) {
            finish();
        }
        handler = new Handler();
//        fetchCoffeePlaces();

    }



    private void fetchCoffeePlaces() {
        // Fetch from firebase
        db.collection("user")
                .whereEqualTo("userID", user.getUid())
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        DocumentSnapshot doc = task.getResult().getDocuments().get(0);
                        ArrayList<String> placeIds = (ArrayList<String>) doc.get("bookmarks");
                        if (placeIds == null) return;
                        if (placeIds.isEmpty()) {
                            return;
                        }
                        for (int i = 0; i < placeIds.size(); i++) {
                            if (placeIds.get(i).charAt(0) == 'f') {
                                fetchFirebaseData(placeIds.get(i));
                                continue;
                            }
                            fetchVietmapData(placeIds.get(i));
                        }
                    }
                });
    }

    private void fetchFirebaseData(String refId) {
        Log.d("TESTING", refId);

        String docId = refId.substring(3);
        db.collection("coffeePlace")
                .document(docId).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        CoffeePlace coffeePlace = documentSnapshot.toObject(CoffeePlace.class);
                        coffeePlaceList.add(coffeePlace);
                        bookMarkAdapter.notifyItemInserted(bookMarkAdapter.getItemCount() - 1);
                    }
                });
    }

    private void fetchVietmapData(String refId) {
//        Log.d("TESTING", refId);
        String url = "https://maps.vietmap.vn/api/place/v3?apikey=" + apiKey + "&refid=" + refId;
        Request req = new Request.Builder()
                .url(url)
                .build();
        Call call = client.newCall(req);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) { return; }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String body = response.body().string();
//                Log.d("TESTING", body);
                try {
                    JSONObject res = new JSONObject(body);
                    CoffeePlace coffeePlace = new CoffeePlace();
                    coffeePlace.setName(res.getString("name"));
                    coffeePlace.setImage("");
                    coffeePlace.setAddress(res.getString("hs_num") + " " + res.getString("street") + " " + res.getString("city") + " " + res.getString("district") + res.getString("ward"));
                    coffeePlace.setWifi(false);
                    coffeePlace.setPriceRange("");
                    coffeePlace.setStartTime("00:00");
                    coffeePlace.setEndtime("00:00");
                    coffeePlace.setRef_id(refId);

                    handler.post(() -> {
                        coffeePlaceList.add(coffeePlace);
                        bookMarkAdapter.notifyItemInserted(bookMarkAdapter.getItemCount() - 1);
                    });

                }

                catch (Exception e) {
                    Log.d("TESING", e.toString());
                }
            }
        });
    }



    @Override
    public void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
    }

    private void onClickNearMe(CoffeePlace coffeePlace) {
        Intent intent = new Intent(BookMarkActivity.this, NearActivity.class);
        String key = coffeePlace.getName() + " " + coffeePlace.getAddress();
        String refId = coffeePlace.getRef_id();
        intent.putExtra("key", key);
        intent.putExtra("refId", refId);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        coffeePlaceList.clear();
        bookMarkAdapter.notifyDataSetChanged();
        fetchCoffeePlaces();
    }
}