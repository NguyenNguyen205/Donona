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
import java.util.List;

public class TestAdapter extends RecyclerView.Adapter<TestAdapter.TestViewHolder> {

    private ArrayList<String> suggestion;
    private OnSearchListener onSearchListener;


//    public interface OnReadBlogListener {
//        void onBlogRead(Blog blog);
//    }

    public interface OnSearchListener {
        void onSearch(String place);
    }

//    public TestAdapter(ArrayList<Blog> blogs, OnReadBlogListener onReadBlogListener) {
//        this.suggestion = blogs;
//        this.onReadBlogListener = onReadBlogListener;
//
//    }


//    public void setBlogs(ArrayList<Blog> blogs) {
//        this.blogs = blogs;
//    }

    public TestAdapter(ArrayList<String> suggestion, OnSearchListener onSearchListener) {
        this.suggestion = suggestion;
        this.onSearchListener = onSearchListener;
    }

    public void setSuggestion(ArrayList<String> suggestion) {
        this.suggestion = suggestion;
    }


    @NonNull
    @Override
    public TestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ListPlaceBinding binding = ListPlaceBinding.inflate(inflater, parent, false);
        return new TestViewHolder(binding);

    }

    @Override
    public void onBindViewHolder(@NonNull TestViewHolder holder, int position) {
        String val = suggestion.get(position);
        holder.listPlaceBinding.name.setText(val);
        holder.listPlaceBinding.searchItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TEST", "Search item clicked");
                onSearchListener.onSearch(val);
            }
        });
    }

    @Override
    public int getItemCount() {
        return suggestion.size();
    }

    public class TestViewHolder extends RecyclerView.ViewHolder {
        private ListPlaceBinding listPlaceBinding;

        public TestViewHolder(ListPlaceBinding listPlaceBinding) {
            super(listPlaceBinding.getRoot());
            this.listPlaceBinding = listPlaceBinding;
        }

    }

}


