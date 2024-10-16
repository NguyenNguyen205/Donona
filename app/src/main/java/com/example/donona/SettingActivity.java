package com.example.donona;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

    //Switch between dark and light mode
    private Switch switchTheme;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    //Switch language
    private Switch switchLang;

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

        // Nút để chuyển chế độ sáng tối
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

        //Lấy nút chuyển của ngôn ngữ
        switchLang = findViewById(R.id.switch_lang);

        // Đặt trạng thái ban đầu cho switch (có thể lưu trong SharedPreferences)
        switchLang.setChecked(isVietnameseLanguage()); // Hàm kiểm tra ngôn ngữ hiện tại

        // Trong phương thức onCreate
        SharedPreferences preferences = getSharedPreferences("app_prefs", MODE_PRIVATE);
        switchLang.setChecked(preferences.getBoolean("isVietnamese", false));

        switchLang.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    setLocale("vi");
                } else {
                    setLocale("en");
                }

                // Lưu trạng thái ngôn ngữ
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("isVietnamese", isChecked);
                editor.apply();
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
//            if (itemId == R.id.navigation_streaming) {
//                startActivity(new Intent(SettingActivity.this, StreamingActivity.class));
//                finish();
//                return true;
//            }
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

    private void setLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());

        // Khởi động lại Activity để áp dụng ngôn ngữ mới
        recreate();
    }

    private boolean isVietnameseLanguage() {
        // Kiểm tra ngôn ngữ hiện tại
        return Locale.getDefault().getLanguage().equals("vi");
    }
}