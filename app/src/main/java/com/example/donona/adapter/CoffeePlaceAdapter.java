package com.example.donona.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.donona.R;
import com.example.donona.databinding.ItemCoffeePlaceBinding;
import com.example.donona.model.CoffeePlace;
import com.example.donona.transformation.CircleTransform;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CoffeePlaceAdapter extends RecyclerView.Adapter<CoffeePlaceAdapter.CoffeePlaceViewHolder> {
    private List<CoffeePlace> coffeePlaceList;

    public CoffeePlaceAdapter(List<CoffeePlace> coffeePlaceList) {
        this.coffeePlaceList = coffeePlaceList;
    }

    @NonNull
    @Override
    public CoffeePlaceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemCoffeePlaceBinding binding = ItemCoffeePlaceBinding.inflate(inflater, parent, false);
        return new CoffeePlaceViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CoffeePlaceViewHolder holder, int position) {
        CoffeePlace coffeePlace = coffeePlaceList.get(position);
        holder.binding.coffeeDisplay.setText(coffeePlace.getDisplay());
        holder.binding.coffeeCity.setText(coffeePlace.getCity());
        holder.binding.coffeeAddress.setText(coffeePlace.getAddress());
        holder.binding.coffeeDistrict.setText(coffeePlace.getDistrict());
        holder.binding.coffeeEndtime.setText(coffeePlace.getEndtime());
        holder.binding.coffeeHsNum.setText(coffeePlace.getHs_num());
        holder.binding.coffeeLat.setText(String.valueOf(coffeePlace.getLat()));
        holder.binding.coffeeLng.setText(String.valueOf(coffeePlace.getLng()));
        holder.binding.coffeeName.setText(coffeePlace.getName());
        holder.binding.coffeePriceRange.setText(coffeePlace.getPriceRange());
        holder.binding.coffeeStarttime.setText(coffeePlace.getStartTime());
        holder.binding.coffeeStreet.setText(coffeePlace.getStreet());
        holder.binding.coffeeThumbnail.setText(coffeePlace.getThumbnail());
        holder.binding.coffeeWard.setText(coffeePlace.getWard());
        boolean hasWifi = coffeePlace.isWifi(); // Replace with your actual method to get the boolean value
        holder.binding.coffeeWifi.setText(hasWifi ? "Yes" : "No"); // Use a ternary operator for conversion
        Picasso.get().load(coffeePlace.getImage()).into(holder.binding.coffeeImage);
    }

    @Override
    public int getItemCount() {
        return coffeePlaceList.size();
    }

    public static class CoffeePlaceViewHolder extends RecyclerView.ViewHolder {
        private final ItemCoffeePlaceBinding binding;

        public CoffeePlaceViewHolder(ItemCoffeePlaceBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }


}

