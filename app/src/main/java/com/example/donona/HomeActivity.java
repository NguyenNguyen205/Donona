package com.example.donona;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;


public class HomeActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private String TAG = "TEST";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = FirebaseFirestore.getInstance();

        BottomNavigationView bottomNavigationView = findViewById(R.id.nav);
        bottomNavigationView.setSelectedItemId(R.id.navigation_home);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            // can't use switch case due to non constant id
            final int itemId = item.getItemId();
            if (itemId == R.id.navigation_home) {
                return true;
            }
            if (itemId == R.id.navigation_streaming) {
                startActivity(new Intent(HomeActivity.this, StreamingActivity.class));
                finish();
                return true;
            }
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

    public void onClickSearchPlace(View view) {
        Log.d(TAG, "Search button click");
        Intent intent = new Intent(HomeActivity.this, VietMapMapViewActivity.class);
        startActivity(intent);
    }

    public void onClickNearMe(View view) {
        Log.d(TAG, "Near me button click");
        Intent intent = new Intent(HomeActivity.this, NearActivity.class);
        startActivity(intent);
    }

    public void onClickGameLauncher(View view) {
        Log.d(TAG, "Game launcher button click");
        Intent intent = new Intent(HomeActivity.this, GameLauncherActivity.class);
        startActivity(intent);
    }

    public void onClickSuggest(View view) {
        Log.d(TAG, "Near me test button click");
        Intent intent = new Intent(HomeActivity.this, NearMeActivity.class);
        startActivity(intent);
    }
}
