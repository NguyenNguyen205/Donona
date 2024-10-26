package com.example.donona;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.media.Image;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.donona.music.MusicService;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Locale;


public class SettingActivity extends AppCompatActivity {
    private ImageButton logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_setting);

        ImageButton appInforButton = findViewById(R.id.appInfor);
        appInforButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingActivity.this, AppInforActivity.class));
            }
        });

        ImageButton contactButton = findViewById(R.id.contactNavigate);
        contactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingActivity.this, ContactActivity.class));
            }
        });

        //Lấy nút và xử lý logic khi ấn nút nhạc
        ImageButton musicButton = findViewById(R.id.musicButton);
        musicButton.setOnClickListener(v -> {
            // Gửi Intent để gọi toggleMusic trong MusicService
            Intent toggleMusicIntent = new Intent(this, MusicService.class);
            toggleMusicIntent.setAction("TOGGLE_MUSIC");
            startService(toggleMusicIntent);  // Gửi lệnh để Service thực hiện toggleMusic
        });

        //Hiển thị nút logout
        logoutButton = findViewById(R.id.logoutButton);
        logoutButton.setVisibility(View.VISIBLE);

        //Nếu user chưa đăng nhập sẽ ẩn nút logout đi
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null){
            logoutButton.setVisibility(View.GONE);
        }

        // Navigate user to change language
        ImageButton lanBtn = findViewById(R.id.switchLang);
        lanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Settings.ACTION_LOCALE_SETTINGS));
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.nav);
        bottomNavigationView.setSelectedItemId(R.id.navigation_setting);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            // can't use switch case due to non constant id
            final int itemId = item.getItemId();
            if (itemId == R.id.navigation_setting) {
                return true;
            }
            if (itemId == R.id.navigation_account) {
                startActivity(new Intent(SettingActivity.this, ProfileActivity.class));
                finish();
                return true;
            }
            if (itemId == R.id.navigation_home) {
                startActivity(new Intent(SettingActivity.this, HomeActivity.class));
                finish();
                return true;
            }
            return false;
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Trong phương thức onCreate
        SharedPreferences preferences = getSharedPreferences("AppSettingsPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
    }

    public void logout(View view) {
        Log.d("TEST", "Logging out...");
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(SettingActivity.this, ProfileActivity.class));
    }

}