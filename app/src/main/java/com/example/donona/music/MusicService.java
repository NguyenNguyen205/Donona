package com.example.donona.music;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;

public class MusicService extends Service {
    private MediaPlayer mediaPlayer;
    private File localFile;
    private boolean isPaused = false;

    @Override
    public void onCreate() {
        super.onCreate();
        // Tải nhạc từ Firebase Storage và phát nhạc
        downloadFileFromFirebase("Music/ball.mp3");
    }

    private void downloadFileFromFirebase(String fileName) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference().child(fileName);

        localFile = new File(getExternalFilesDir(Environment.DIRECTORY_MUSIC), "my_song.mp3");

        storageReference.getFile(localFile).addOnSuccessListener(taskSnapshot -> {
            try {
                if (mediaPlayer == null) {
                    mediaPlayer = new MediaPlayer();
                    mediaPlayer.setDataSource(localFile.getAbsolutePath());
                    mediaPlayer.setLooping(true); // Lặp lại nhạc
                    mediaPlayer.prepare();        // Chuẩn bị MediaPlayer
                    mediaPlayer.start();          // Phát nhạc
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).addOnFailureListener(exception -> {
            Log.e("Firebase", "Tải file thất bại", exception);
        });
    }

    public void toggleMusic() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause(); // Tạm dừng nhạc nếu đang phát
                isPaused = true;
                Log.d("MusicService", "Nhạc đã tạm dừng.");
            } else if (isPaused) {
                mediaPlayer.start(); // Phát lại nhạc nếu đang dừng
                isPaused = false;
                Log.d("MusicService", "Nhạc đã phát lại.");
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && "TOGGLE_MUSIC".equals(intent.getAction())) {
            toggleMusic();  // Thực hiện toggle giữa phát và tạm dừng nhạc
        } else if (mediaPlayer != null && !mediaPlayer.isPlaying() && !isPaused) {
            mediaPlayer.start(); // Phát nhạc nếu chưa phát
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
