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
        Picasso.get().load(coffeePlace.getImage()).into(holder.binding.coffeeImage);
        holder.binding.coffeeName.setText(coffeePlace.getName());
//        holder.binding.coffeeDisplay.setText("Coffee Name:" + coffeePlace.getDisplay());
        holder.binding.coffeeAddress.setText("Address:" + coffeePlace.getAddress());
//        holder.binding.coffeeStreet.setText("Street:" + coffeePlace.getStreet());
//        holder.binding.coffeeWard.setText("Ward:" + coffeePlace.getWard());
//        holder.binding.coffeeDistrict.setText("District:" + coffeePlace.getDistrict());
//        holder.binding.coffeeCity.setText("City:" + coffeePlace.getCity());
        holder.binding.coffeePriceRange.setText("Price:" + coffeePlace.getPriceRange());
        holder.binding.coffeeStarttime.setText(coffeePlace.getStartTime() + " A.M.");
        holder.binding.coffeeEndtime.setText(coffeePlace.getEndtime() + " P.M.");
//        holder.binding.coffeeHsNum.setText("Hs Number:" + coffeePlace.getHs_num());
//        holder.binding.coffeeLat.setText(String.valueOf("Latitude:" + coffeePlace.getLat()));
//        holder.binding.coffeeLng.setText(String.valueOf("Longitude:" + coffeePlace.getLng()));
        boolean hasWifi = coffeePlace.isWifi();
        holder.binding.coffeeWifi.setText(hasWifi ? "Wifi: Yes" : "Wifi: No");
//        holder.binding.coffeeThumbnail.setText(coffeePlace.getThumbnail());
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

