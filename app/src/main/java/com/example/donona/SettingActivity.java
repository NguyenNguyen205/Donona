package com.example.donona;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;

public class SettingActivity extends AppCompatActivity {
    private Button logoutButton;

    //Download and play music file
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private MediaPlayer mediaPlayer;
    private File localFile;

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
            }
        });

        // Khởi tạo FirebaseStorage và StorageReference
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        // Tải file MP3 từ Firebase Storage về
        String fileName = "Music/beautyandabeat.mp3";
        downloadFile(fileName);

        // Thiết lập sự kiện cho nút phát nhạc
        Button playMusicButton = findViewById(R.id.playMusicButton);
        playMusicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (localFile != null && localFile.exists()) {
                    playAudio(localFile);  // Phát nhạc khi nhấn nút
                } else {
                    Log.e("MediaPlayer", "File nhạc chưa được tải về.");
                }
            }
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

    private void downloadFile(String fileName) {
        // Tạo đường dẫn tạm thời trên thiết bị
        localFile = new File(getExternalFilesDir(Environment.DIRECTORY_MUSIC), "my_song.mp3");

        // Tải file từ Firebase Storage về
        storageReference.child(fileName).getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                Log.d("Firebase", "Tải file thành công: " + localFile.getAbsolutePath());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@androidx.annotation.NonNull Exception exception) {
                Log.e("Firebase", "Tải file thất bại", exception);
            }
        });
    }

    private void playAudio(File audioFile) {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            // Nếu nhạc đang phát, ngừng phát và giải phóng MediaPlayer
            mediaPlayer.stop();
            mediaPlayer.reset();  // Reset để sử dụng lại MediaPlayer
            Log.d("MediaPlayer", "Nhạc đã dừng");
        } else {
            // Nếu không có nhạc nào đang phát, khởi tạo và phát nhạc
            try {
                if (mediaPlayer == null) {
                    mediaPlayer = new MediaPlayer();
                }
                mediaPlayer.setDataSource(audioFile.getAbsolutePath());
                mediaPlayer.prepare();  // Chuẩn bị MediaPlayer
                mediaPlayer.start();    // Phát nhạc
                Log.d("MediaPlayer", "Đang phát nhạc: " + audioFile.getAbsolutePath());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();  // Giải phóng MediaPlayer khi không sử dụng nữa
            mediaPlayer = null;
        }
    }
}