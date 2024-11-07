package com.example.donona.adapter;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.donona.BlogPostActivity;
import com.example.donona.ReadBlogActivity;
import com.example.donona.databinding.ListBlogBinding;
import com.example.donona.databinding.ListPlaceBinding;
import com.example.donona.model.Blog;
import com.example.donona.transformation.CircleTransform;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class BlogAdapter extends RecyclerView.Adapter<BlogAdapter.BlogViewHolder> {

    private ArrayList<Blog> blogs;
    private OnReadBlogListener onReadBlogListener;


    public interface OnReadBlogListener {
        void onBlogRead(Blog blog);
    }

    public BlogAdapter(ArrayList<Blog> blogs, OnReadBlogListener onReadBlogListener) {
        this.blogs = blogs;
        this.onReadBlogListener = onReadBlogListener;

    }

    public void setBlogs(ArrayList<Blog> blogs) {
        this.blogs = blogs;
    }

    @NonNull
    @Override
    public BlogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ListBlogBinding binding = ListBlogBinding.inflate(inflater, parent, false);
        return new BlogViewHolder(binding);

    }

    @Override
    public void onBindViewHolder(@NonNull BlogViewHolder holder, int position) {
        Blog blog = blogs.get(position);
        holder.listBlogBinding.title.setText(blog.getTitle());
        holder.listBlogBinding.content.setText(blog.getThumbnailContent());
        holder.listBlogBinding.blogLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TEST", "Position is click");
                onReadBlogListener.onBlogRead(blog);
            }
        });
        Picasso.get().load(blog.getThumbnail()).transform(new CircleTransform()).resize(300, 0).into(holder.listBlogBinding.thumbnail);
    }

    @Override
    public int getItemCount() {
        return blogs.size();
    }

    public class BlogViewHolder extends RecyclerView.ViewHolder {
        private ListBlogBinding listBlogBinding;

        public BlogViewHolder(ListBlogBinding listBlogBinding) {
            super(listBlogBinding.getRoot());
            this.listBlogBinding = listBlogBinding;
        }

    }

}


