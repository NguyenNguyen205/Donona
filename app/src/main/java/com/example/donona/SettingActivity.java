package com.example.donona;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
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


public class SettingActivity extends AppCompatActivity {
    private Button logoutButton;

    //Switch between dark and light mode
    private Switch switchTheme;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_setting);

        switchTheme = findViewById(R.id.switch_theme);

        // SharedPreferences để lưu trạng thái
        sharedPreferences = getSharedPreferences("AppSettingsPrefs", 0);
        editor = sharedPreferences.edit();

        // Kiểm tra trạng thái hiện tại của theme
        boolean isNightMode = sharedPreferences.getBoolean("NightMode", false);
        switchTheme.setChecked(isNightMode);

        // Xử lý khi người dùng thay đổi theme
        switchTheme.setOnCheckedChangeListener((buttonView, isChecked) -> {
            boolean currentMode = sharedPreferences.getBoolean("NightMode", false);
            if (isChecked != currentMode) {
                if (isChecked) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    editor.putBoolean("NightMode", true);
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    editor.putBoolean("NightMode", false);
                }
                editor.apply();  // Lưu trạng thái vào SharedPreferences
                Intent intent = getIntent();
                finish();
                startActivity(intent);
            }
        });

        //Lấy nút và xử lý logic khi ấn nút nhạc
        Button musicButton = findViewById(R.id.musicButton);
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
            if (itemId == R.id.navigation_streaming) {
                startActivity(new Intent(SettingActivity.this, StreamingActivity.class));
                finish();
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

    public void logout(View view) {
        Log.d("TEST", "Logging out...");
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(SettingActivity.this, ProfileActivity.class));
    }
}