package com.example.donona.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.donona.R;
import com.example.donona.databinding.ItemBookMarkBinding;
import com.example.donona.databinding.ItemCoffeePlaceBinding;
import com.example.donona.model.CoffeePlace;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.List;

public class BookMarkAdapter extends RecyclerView.Adapter<BookMarkAdapter.BookMarkViewHolder> {
    private List<CoffeePlace> coffeePlaceList;
    private BookMarkAdapter.OnClickNearMeListener onClickNearMeListener;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public interface OnClickNearMeListener {
        void onClickNearMe(CoffeePlace coffeePlace);
    }

    public BookMarkAdapter(List<CoffeePlace> coffeePlaceList, BookMarkAdapter.OnClickNearMeListener onClickNearMeListener) {
        this.coffeePlaceList = coffeePlaceList;
        this.onClickNearMeListener = onClickNearMeListener;
    }

    @NonNull
    @Override
    public BookMarkAdapter.BookMarkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemBookMarkBinding binding = ItemBookMarkBinding.inflate(inflater, parent, false);
        return new BookMarkAdapter.BookMarkViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull BookMarkAdapter.BookMarkViewHolder holder, int position) {
        CoffeePlace coffeePlace = coffeePlaceList.get(position);
        Log.d("TESTING", coffeePlace.getName());

        if (!coffeePlace.getImage().isEmpty()) {
            Picasso.get().load(coffeePlace.getImage()).into(holder.binding.coffeeImage);
        }
        else {
            Picasso.get().load(R.drawable.logo).into(holder.binding.coffeeImage);
        }

        holder.binding.coffeeName.setText(coffeePlace.getName());
        holder.binding.coffeeAddress.setText("Address:" + coffeePlace.getAddress());
        holder.binding.coffeePriceRange.setText("Price:" + coffeePlace.getPriceRange());
        holder.binding.coffeeStarttime.setText(coffeePlace.getStartTime() + " A.M.");
        holder.binding.coffeeEndtime.setText(coffeePlace.getEndtime() + " P.M.");
        boolean hasWifi = coffeePlace.isWifi();
        holder.binding.coffeeWifi.setText(hasWifi ? "Wifi: Yes" : "Wifi: No");
        holder.binding.nearMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TEST", "View place clicked");
                onClickNearMeListener.onClickNearMe(coffeePlace);
            }
        });

//        holder.binding.bookmarkButton.setOnClickListener(v -> {
//            String refId = coffeePlace.getRef_id().replace("fb:", "");  // Loại bỏ "fb:" khỏi ref_id
//            boolean currentBookMarkState = coffeePlace.isBookMark();    // Lấy trạng thái hiện tại
//            boolean newBookMarkState = !currentBookMarkState;           // Đảo ngược trạng thái isBookMark
//
//            Log.d("Bookmark", "Current state: " + currentBookMarkState);  // Log trạng thái hiện tại
//            Log.d("Bookmark", "New state: " + newBookMarkState);         // Log trạng thái sau khi đảo ngược
//
//            // Cập nhật Firestore với trạng thái mới
//            db.collection("coffeePlace")
//                    .document(refId)
//                    .update("isBookMark", newBookMarkState)
//                    .addOnSuccessListener(aVoid -> {
//                        Log.d("Firestore", "Bookmark updated successfully");
//
//                        // Cập nhật lại trạng thái cục bộ sau khi Firestore thành công
//                        coffeePlace.setBookMark(newBookMarkState);  // Cập nhật lại trạng thái cục bộ
//                        Log.d("Bookmark", "Updated state locally: " + coffeePlace.isBookMark());
//
//                        notifyItemChanged(position);  // Cập nhật lại RecyclerView sau khi thay đổi
//                    })
//                    .addOnFailureListener(e -> {
//                        Log.w("Firestore", "Error updating bookmark", e);
//                    });
//        });
    }

    @Override
    public int getItemCount() {
        return coffeePlaceList.size();
    }

    public static class BookMarkViewHolder extends RecyclerView.ViewHolder {
        private final ItemBookMarkBinding binding;

        public BookMarkViewHolder(ItemBookMarkBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
