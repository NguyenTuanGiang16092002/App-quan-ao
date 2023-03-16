package com.example.footballfashion.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.footballfashion.constant.Constant;
import com.example.footballfashion.databinding.ItemCartBinding;
import com.example.footballfashion.model.Sport;
import com.example.footballfashion.util.GlideUtils;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private final List<Sport> mListClothes;
    private final IClickListener iClickListener;

    public interface IClickListener {
        void clickDeteteCloth(Sport Sport, int position);

        void updateItemCloth(Sport Sport, int position);
    }

    public CartAdapter(List<Sport> mListClothes, IClickListener iClickListener) {
        this.mListClothes = mListClothes;
        this.iClickListener = iClickListener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCartBinding itemCartBinding = ItemCartBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new CartViewHolder(itemCartBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        Sport Sport = mListClothes.get(position);
        if (Sport == null) {
            return;
        }
        GlideUtils.loadUrl(Sport.getImage(), holder.mItemCartBinding.imgClothCart);
        holder.mItemCartBinding.tvClothNameCart.setText(Sport.getName());


        String strClothPriceCart = Sport.getPrice() + Constant.CURRENCY;
        if (Sport.getSale() > 0) {
            strClothPriceCart = Sport.getRealPrice() + Constant.CURRENCY;
        }
        holder.mItemCartBinding.tvClothPriceCart.setText(strClothPriceCart);
        holder.mItemCartBinding.tvCount.setText(String.valueOf(Sport.getCount()));

        holder.mItemCartBinding.tvSubtract.setOnClickListener(v -> {
            String strCount = holder.mItemCartBinding.tvCount.getText().toString();
            int count = Integer.parseInt(strCount);
            if (count <= 1) {
                return;
            }
            int newCount = count - 1;
            holder.mItemCartBinding.tvCount.setText(String.valueOf(newCount));

            int totalPrice = Sport.getRealPrice() * newCount;
            Sport.setCount(newCount);
            Sport.setTotalPrice(totalPrice);

            iClickListener.updateItemCloth(Sport, holder.getAdapterPosition());
        });

        holder.mItemCartBinding.tvAdd.setOnClickListener(v -> {
            int newCount = Integer.parseInt(holder.mItemCartBinding.tvCount.getText().toString()) + 1;
            holder.mItemCartBinding.tvCount.setText(String.valueOf(newCount));

            int totalPrice = Sport.getRealPrice() * newCount;
            Sport.setCount(newCount);
            Sport.setTotalPrice(totalPrice);

            iClickListener.updateItemCloth(Sport, holder.getAdapterPosition());
        });

        holder.mItemCartBinding.tvDelete.setOnClickListener(v
                -> iClickListener.clickDeteteCloth(Sport, holder.getAdapterPosition()));
    }

    @Override
    public int getItemCount() {
        return null == mListClothes ? 0 : mListClothes.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {

        private final ItemCartBinding mItemCartBinding;

        public CartViewHolder(ItemCartBinding itemCartBinding) {
            super(itemCartBinding.getRoot());
            this.mItemCartBinding = itemCartBinding;
        }
    }
}
