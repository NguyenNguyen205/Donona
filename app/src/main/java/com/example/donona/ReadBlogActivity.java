package com.example.donona;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.donona.databinding.DisplayBlogBinding;
import com.squareup.picasso.Picasso;

public class ReadBlogActivity extends AppCompatActivity {

    private DisplayBlogBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = DisplayBlogBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
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
}