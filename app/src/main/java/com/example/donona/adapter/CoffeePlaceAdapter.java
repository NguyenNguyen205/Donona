package com.example.donona.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.donona.R;
import com.example.donona.databinding.ItemCoffeePlaceBinding;
import com.example.donona.model.CoffeePlace;
import com.example.donona.transformation.CircleTransform;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CoffeePlaceAdapter extends RecyclerView.Adapter<CoffeePlaceAdapter.CoffeePlaceViewHolder> {
    private List<CoffeePlace> coffeePlaceList;
    private OnClickNearMeListener onClickNearMeListener;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private OnBookmarkClickListener onBookmarkClickListener;

    public interface OnClickNearMeListener {
        void onClickNearMe(CoffeePlace coffeePlace);
    }

    public interface OnBookmarkClickListener {
        void onBookmarkClick(int position, boolean isBookmarked);
    }

    public CoffeePlaceAdapter(List<CoffeePlace> coffeePlaceList, OnClickNearMeListener onClickNearMeListener, OnBookmarkClickListener onBookmarkClickListener) {
        this.coffeePlaceList = coffeePlaceList;
        this.onClickNearMeListener = onClickNearMeListener;
        this.onBookmarkClickListener = onBookmarkClickListener;
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
        holder.binding.nearMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TEST", "View place clicked");
                onClickNearMeListener.onClickNearMe(coffeePlace);
            }
        });
//        holder.binding.coffeeThumbnail.setText(coffeePlace.getThumbnail());

        if (coffeePlace.isBookMark()) {
            holder.binding.bookmarkButton.setImageResource(R.drawable.icons8_bookmark_50_selected); // Tô đậm
        } else {
            holder.binding.bookmarkButton.setImageResource(R.drawable.icons8_bookmark_50); // Sáng
        }

        holder.binding.bookmarkButton.setOnClickListener(v -> {
            String refId = coffeePlace.getRef_id().replace("fb:", "");  // Loại bỏ "fb:" khỏi ref_id
            boolean currentBookMarkState = coffeePlace.isBookMark();    // Lấy trạng thái hiện tại
            boolean newBookMarkState = !currentBookMarkState;           // Đảo ngược trạng thái isBookMark

            Log.d("Bookmark", "Current state: " + currentBookMarkState);  // Log trạng thái hiện tại
            Log.d("Bookmark", "New state: " + newBookMarkState);         // Log trạng thái sau khi đảo ngược

            // Cập nhật trạng thái cục bộ ngay lập tức
            coffeePlace.setBookMark(newBookMarkState);
            notifyItemChanged(position);

            // Gọi listener để thông báo cho Activity về sự thay đổi
            if (onBookmarkClickListener != null) {
                onBookmarkClickListener.onBookmarkClick(position, newBookMarkState);
            }

            // Cập nhật Firestore với trạng thái mới
            db.collection("coffeePlace")
                    .document(refId)
                    .update("isBookMark", newBookMarkState)
                    .addOnSuccessListener(aVoid -> {
                        Log.d("Firestore", "Bookmark updated successfully");

                        // Cập nhật lại trạng thái cục bộ sau khi Firestore thành công
                        coffeePlace.setBookMark(newBookMarkState);  // Cập nhật lại trạng thái cục bộ
                        Log.d("Bookmark", "Updated state locally: " + coffeePlace.isBookMark());

                        notifyItemChanged(position);  // Cập nhật lại RecyclerView sau khi thay đổi
                    })
                    .addOnFailureListener(e -> {
                        Log.w("Firestore", "Error updating bookmark", e);
                    });
        });

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

