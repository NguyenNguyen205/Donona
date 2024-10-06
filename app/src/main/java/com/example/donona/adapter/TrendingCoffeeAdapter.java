package com.example.donona.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.donona.databinding.ItemCardViewTrendingBinding;
import com.example.donona.databinding.ItemCoffeePlaceBinding;
import com.example.donona.model.CoffeePlace;
import com.squareup.picasso.Picasso;

import java.util.List;

public class TrendingCoffeeAdapter extends RecyclerView.Adapter<TrendingCoffeeAdapter.TrendingCoffeeViewHolder> {
    private List<CoffeePlace> coffeePlaceList;

    public TrendingCoffeeAdapter(List<CoffeePlace> coffeePlaceList) {
        this.coffeePlaceList = coffeePlaceList;
    }

    @NonNull
    @Override
    public TrendingCoffeeAdapter.TrendingCoffeeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemCardViewTrendingBinding binding = ItemCardViewTrendingBinding.inflate(inflater, parent, false);
        return new TrendingCoffeeAdapter.TrendingCoffeeViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull TrendingCoffeeAdapter.TrendingCoffeeViewHolder holder, int position) {
        CoffeePlace coffeePlace = coffeePlaceList.get(position);
        Picasso.get().load(coffeePlace.getImage()).into(holder.binding.trendingImage);
        holder.binding.title.setText(coffeePlace.getName());
        holder.binding.openingHours.setText(coffeePlace.getStartTime());
        holder.binding.closingHours.setText(coffeePlace.getEndtime());
        holder.binding.location.setText(coffeePlace.getAddress());

    }

    @Override
    public int getItemCount() {
        return coffeePlaceList.size();
    }

    public static class TrendingCoffeeViewHolder extends RecyclerView.ViewHolder {
        private final ItemCardViewTrendingBinding binding;

        public TrendingCoffeeViewHolder(ItemCardViewTrendingBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
