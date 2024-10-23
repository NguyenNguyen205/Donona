package com.example.donona;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.donona.adapter.CoffeePlaceAdapter;
import com.example.donona.adapter.TrendingCoffeeAdapter;
import com.example.donona.databinding.ActivityHomeBinding;
import com.example.donona.databinding.ActivityNearMeBinding;
import com.example.donona.model.CoffeePlace;
import com.example.donona.music.MusicService;
import com.example.donona.util.NetworkUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
//Auto Image Slider in homepage
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.models.SlideModel;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class HomeActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private String TAG = "TEST";

    private FirebaseAuth mAuth;
    private List<CoffeePlace> coffeePlaceList;
    private ActivityHomeBinding binding;
    private TrendingCoffeeAdapter trendingCoffeeAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        //setContentView(R.layout.activity_home);
        setContentView(binding.getRoot());
        Log.d("HELLOTESTING", Locale.getDefault().getLanguage());


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Khởi tạo danh sách
        coffeePlaceList = new ArrayList<>();
        binding.trendingRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Khởi tạo adapter
        trendingCoffeeAdapter = new TrendingCoffeeAdapter(coffeePlaceList, this::onClickNearMe);
        binding.trendingRecyclerView.setAdapter(trendingCoffeeAdapter); // Đặt adapter vào RecyclerView


        // Khởi động MusicService để phát nhạc
        Intent musicIntent = new Intent(this, MusicService.class);
        startService(musicIntent);

        //Get instance from Firebase
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        //fetch data from Firebase
        fetchCoffeePlaces();

        BottomNavigationView bottomNavigationView = findViewById(R.id.nav);
        bottomNavigationView.setSelectedItemId(R.id.navigation_home);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            // can't use switch case due to non constant id
            final int itemId = item.getItemId();
            if (itemId == R.id.navigation_home) {
                return true;
            }
//            if (itemId == R.id.navigation_streaming) {
//                startActivity(new Intent(HomeActivity.this, StreamingActivity.class));
//                finish();
//                return true;
//            }
            if (itemId == R.id.navigation_account) {
                startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
                finish();
                return true;
            }
            if (itemId == R.id.navigation_setting) {
                startActivity(new Intent(HomeActivity.this, SettingActivity.class));
                finish();
                return true;
            }
            return false;
        });

        ImageSlider imageSlider = findViewById(R.id.imageSlider);
        ArrayList<SlideModel> slideModels = new ArrayList<>();

        slideModels.add(new SlideModel(R.drawable.vitamin_coffee_img, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.next_tea_img, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.ca_phe_tung, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.ca_phe_vot, ScaleTypes.FIT));

        imageSlider.setImageList(slideModels, ScaleTypes.FIT);

    }

    private void fetchCoffeePlaces() {
        db.collection("coffeePlace")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        Log.d("Task", "isSuccess: " + task.isSuccessful());
                        if (task.isSuccessful()) {
                            coffeePlaceList.clear(); // Clear old data
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("Document", "DocumentCoffeePlace: " + document.toString());
                                CoffeePlace coffeePlace = document.toObject(CoffeePlace.class);
                                Log.d("CoffeePlace", "CoffeePlace: " + coffeePlace.toString());
                                coffeePlaceList.add(coffeePlace); // Add new data
                                Log.d("CoffeePlaceList", "Size: " + coffeePlaceList.size());
                            }
                            trendingCoffeeAdapter.notifyDataSetChanged(); // Update RecyclerView
                        } else {
                            Log.w("Firestore", "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Get user data
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String uid = extras.getString("userID"); // CAI NAY chi nay h, tu nho
            db.collection("user") // Muon lay collection nao thi khai bao ten do
                    .whereEqualTo("userID", uid) // Cai nay la no se luot qua toan bo collection, va tim kiem theo document thich hop, o day la tim theo userID
                    .get() // Ten field muon so sanh // Hieu toi day chua
                    // Tu day la xu ly ket qua dc tra ve
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                            if (task.isSuccessful()) { // Nay la check coi chay thanh cong hay thanh thang
//                                QuerySnapshot docs = task.getResult(); // nay chuyen ket qua ve dang doc duoc
//                                DocumentSnapshot doc = docs.getDocuments().get(0); // nay lay ket qua
//                                TextView text = (TextView) findViewById(R.id.welcome);
//                                String welcome = "Welcome " + doc.get("username"); // nay lay tung thuoc tinh cua ket qua
//                                text.setText(welcome); // nay set text cho thang frontend, set cai text cho cai textView
//                            }
                        }
                    });
        }
    }

//    public void onClickSearchPlace(View view) {
//        Log.d(TAG, "Search button click");
//        Intent intent = new Intent(HomeActivity.this, VietMapMapViewActivity.class);
//        startActivity(intent);
//    }

    public void onClickNearMe(View view) {
        if (!NetworkUtils.isWifiConnected(this)) {
            // Wi-Fi is not connected, do something here
            Toast.makeText(this, "Wi-Fi is not connected", Toast.LENGTH_SHORT).show();
            return;
        }
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "You have to login", Toast.LENGTH_LONG).show();
            return;
        }
        Log.d(TAG, "Near me button click");
        Intent intent = new Intent(HomeActivity.this, NearActivity.class);
//        Intent intent = new Intent(HomeActivity.this, TestActivity.class);
        startActivity(intent);
    }

    public void onClickGameLauncher(View view) {
        if (!NetworkUtils.isWifiConnected(this)) {
            // Wi-Fi is not connected, do something here
            Toast.makeText(this, "Wi-Fi is not connected", Toast.LENGTH_SHORT).show();
            return;
        }
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "You have to login", Toast.LENGTH_LONG).show();
            return;
        }
        Log.d(TAG, "Game launcher button click");
        Intent intent = new Intent(HomeActivity.this, GameLauncherActivity.class);
        startActivity(intent);
    }

    public void onClickSuggest(View view) {
        if (!NetworkUtils.isWifiConnected(this)) {
            // Wi-Fi is not connected, do something here
            Toast.makeText(this, "Wi-Fi is not connected", Toast.LENGTH_SHORT).show();
            return;
        }
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "You have to login", Toast.LENGTH_LONG).show();
            return;
        }
        Log.d(TAG, "Near me test button click");
        Intent intent = new Intent(HomeActivity.this, NearMeActivity.class);
        startActivity(intent);
    }

    public void onClickPost(View view) {
        if (!NetworkUtils.isWifiConnected(this)) {
            // Wi-Fi is not connected, do something here
            Toast.makeText(this, "Wi-Fi is not connected", Toast.LENGTH_SHORT).show();
            return;
        }
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "You have to login", Toast.LENGTH_LONG).show();
            return;
        }
        db.collection("user")
                .whereEqualTo("userID", user.getUid())
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (!task.isSuccessful() || task.getResult().isEmpty()) {
                            return;
                        }
                        DocumentSnapshot doc = task.getResult().getDocuments().get(0);
                        if (doc.getString("tier").equals("free")) {
                            Toast.makeText(HomeActivity.this, "Upgrade your account to read blog", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Log.d(TAG, "Post page launch");
                        Intent intent = new Intent(HomeActivity.this, BlogPostActivity.class);
                        startActivity(intent);
                    }
                });

    }

    public void onClickSubscription(View view) {
        if (!NetworkUtils.isWifiConnected(this)) {
            // Wi-Fi is not connected, do something here
            Toast.makeText(this, "Wi-Fi is not connected", Toast.LENGTH_SHORT).show();
            return;
        }
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "You have to login", Toast.LENGTH_LONG).show();
            return;
        }
        Log.d(TAG, "Subscription page launch");
        Intent intent = new Intent(HomeActivity.this, SubscriptionActivity.class);
        startActivity(intent);
    }

    private void onClickNearMe(CoffeePlace coffeePlace) {
        if (!NetworkUtils.isWifiConnected(this)) {
            // Wi-Fi is not connected, do something here
            Toast.makeText(this, "Wi-Fi is not connected", Toast.LENGTH_SHORT).show();
            return;
        }
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "You have to login", Toast.LENGTH_LONG).show();
            return;
        }
        Intent intent = new Intent(HomeActivity.this, NearActivity.class);
        String key = coffeePlace.getName() + " " + coffeePlace.getAddress();
        String refId = coffeePlace.getRef_id();
        intent.putExtra("key", key);
        intent.putExtra("refId", refId);
        startActivity(intent);
    }

    public void onClickSuggestReal(View view) {
        if (!NetworkUtils.isWifiConnected(this)) {
            // Wi-Fi is not connected, do something here
            Toast.makeText(this, "Wi-Fi is not connected", Toast.LENGTH_SHORT).show();
            return;
        }
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "You have to login", Toast.LENGTH_LONG).show();
            return;
        }
        Toast.makeText(this, "Comming Soon!", Toast.LENGTH_LONG).show();
    }

    public void onClickPromotion(View view) {
        if (!NetworkUtils.isWifiConnected(this)) {
            // Wi-Fi is not connected, do something here
            Toast.makeText(this, "Wi-Fi is not connected", Toast.LENGTH_SHORT).show();
            return;
        }
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "You have to login", Toast.LENGTH_LONG).show();
            return;
        }
        Toast.makeText(this, "Comming Soon!", Toast.LENGTH_LONG).show();
    }

    public void onClickBookMark(View view) {
        if (!NetworkUtils.isWifiConnected(this)) {
            // Wi-Fi is not connected, do something here
            Toast.makeText(this, "Wi-Fi is not connected", Toast.LENGTH_SHORT).show();
            return;
        }
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "You have to login", Toast.LENGTH_LONG).show();
            return;
        }
        db.collection("user")
                        .whereEqualTo("userID", user.getUid())
                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (!task.isSuccessful() || task.getResult().isEmpty()) {
                            return;
                        }
                        DocumentSnapshot doc = task.getResult().getDocuments().get(0);
                        if (doc.getString("tier").equals("free")) {
                            Toast.makeText(HomeActivity.this, "Upgrade your account to use bookmark", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Log.d(TAG, "Bookmark page launch");
                        Intent intent = new Intent(HomeActivity.this, BookMarkActivity.class);
                        startActivity(intent);
                    }
                });

    }
}
