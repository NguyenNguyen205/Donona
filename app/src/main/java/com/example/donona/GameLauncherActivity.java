package com.example.donona;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
//import com.unity3d.player.UnityPlayerActivity;

public class GameLauncherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_game_launcher);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Navigation
//        BottomNavigationView bottomNavigationView = findViewById(R.id.nav);
//        bottomNavigationView.setOnItemSelectedListener(item -> {
//            // can't use switch case due to non constant id
//            final int itemId = item.getItemId();
//            if (itemId == R.id.navigation_home) {
//                Intent intent = new Intent(GameLauncherActivity.this, HomeActivity.class);
//                startActivity(intent);
//                return true;
//            }
//
//            return false;
//        });

    }

    public void launchGame(View view) {
//        Intent intent = new Intent(GameLauncherActivity.this, UnityPlayerActivity.class);
//        startActivity(intent);
    }

    @Override
    public void finish() {
        super.finish();
        Log.d("HELLOTESTNIG", "Activity is done");
    }
}