package com.example.donona;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.donona.adapter.BlogAdapter;
import com.example.donona.databinding.ActivityBlogPostBinding;
import com.example.donona.model.Blog;
import com.example.donona.util.NetworkUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class BlogPostActivity extends AppCompatActivity {

    private ActivityBlogPostBinding binding;
    private ArrayList<Blog> blogs= new ArrayList<>();
    private BlogAdapter blogAdapter;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityBlogPostBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        db = FirebaseFirestore.getInstance();

        // Init recycler view
        blogAdapter = new BlogAdapter(blogs, this::onBlogRead);
        binding.recycler.setAdapter(blogAdapter);
        binding.recycler.setLayoutManager(new LinearLayoutManager(this));

        fetchData();
    }

    public void fetchData() {
        db.collection("blog").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        blogs.add(new Blog(doc.getString("thumbnail"), doc.getString("content"), doc.getString("title"), doc.getString("thumbnailText")));
                    }
                    if (!blogs.isEmpty()) {
                        blogAdapter.setBlogs(blogs);
                        blogAdapter.notifyDataSetChanged();
                    }
                }
            }
        });
    }

    private void onBlogRead(Blog blog) {
        if (!NetworkUtils.isWifiConnected(this)) {
            // Wi-Fi is not connected, do something here
            Toast.makeText(this, "Wi-Fi is not connected", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(BlogPostActivity.this, ReadBlogActivity.class);
        intent.putExtra("thumbnail", blog.getThumbnail());
        intent.putExtra("title", blog.getTitle());
        intent.putExtra("content", blog.getBlogContent());
        startActivity(intent);
    }
}