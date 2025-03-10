package com.example.donona;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.donona.adapter.CoffeePlaceAdapter;
import com.example.donona.databinding.ActivityNearMeBinding;
import com.example.donona.model.CoffeePlace;
import com.example.donona.util.NetworkUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class NearMeActivity extends AppCompatActivity {
    private List<CoffeePlace> coffeePlaceList;
    private ActivityNearMeBinding binding;
    private CoffeePlaceAdapter coffeePlaceAdapter;
    private String refId ;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNearMeBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());

        // Khởi tạo danh sách
        coffeePlaceList = new ArrayList<>();
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Khởi tạo adapter
        coffeePlaceAdapter = new CoffeePlaceAdapter(coffeePlaceList, this::onClickNearMe, this::onBookmarkClick);
        binding.recyclerView.setAdapter(coffeePlaceAdapter); // Đặt adapter vào RecyclerView

        fetchCoffeePlaces();
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
                            coffeePlaceAdapter.notifyDataSetChanged(); // Update RecyclerView
                        } else {
                            Log.w("Firestore", "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private void onClickNearMe(CoffeePlace coffeePlace) {
        if (!NetworkUtils.isWifiConnected(this)) {
            // Wi-Fi is not connected, do something here
            Toast.makeText(this, "Wi-Fi is not connected", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(NearMeActivity.this, NearActivity.class);
        String key = coffeePlace.getName() + " " + coffeePlace.getAddress();
        refId = coffeePlace.getRef_id();
        intent.putExtra("key", key);
        intent.putExtra("refId", refId);
        startActivity(intent);
    }

    public void onBookmarkClick(int position, boolean isBookmarked) {
        // Cập nhật trạng thái trên Firebase
        CoffeePlace coffeePlace = coffeePlaceList.get(position);
        updateBookmarkInFirebase(coffeePlace.getRef_id(), isBookmarked);
    }

    private void updateBookmarkInFirebase(String coffeePlaceId, boolean isBookmarked) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference coffeePlaceRef = db.collection("coffeePlaces").document(coffeePlaceId);
        coffeePlaceRef.update("isBookMark", isBookmarked)
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firebase", "Bookmark updated successfully.");
                })
                .addOnFailureListener(e -> {
                    Log.w("Firebase", "Error updating bookmark", e);
                });
    }

}