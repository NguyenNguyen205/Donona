package com.example.donona;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.donona.databinding.DisplayBlogBinding;
import com.example.donona.transformation.CircleTransform;
import com.squareup.picasso.Picasso;

public class ReadBlogActivity extends AppCompatActivity {

    private DisplayBlogBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = DisplayBlogBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        String content = intent.getStringExtra("content");
        String thumbnail = intent.getStringExtra("thumbnail");

        binding.content.setText(content);
        binding.title.setText(title);
        Picasso.get().load(thumbnail).resize(700, 0).into(binding.thumbnail);

    }

    public void onClickReturnFromBlogRead(View view) {
        Log.d("TEST", "Return button click");
        finish();
    }
}