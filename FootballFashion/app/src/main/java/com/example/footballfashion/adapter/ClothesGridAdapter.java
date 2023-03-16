package com.example.footballfashion.adapter;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.footballfashion.constant.Constant;
import com.example.footballfashion.databinding.ItemClothGridBinding;
import com.example.footballfashion.listener.IOnClickClothItemListener;
import com.example.footballfashion.model.Sport;
import com.example.footballfashion.util.GlideUtils;

import java.util.List;

public class ClothesGridAdapter extends RecyclerView.Adapter<ClothesGridAdapter.ClothGridViewHolder> {

    private final List<Sport> mListClothes;
    public final IOnClickClothItemListener iOnClickClothItemListener;

    public ClothesGridAdapter(List<Sport> mListClothes, IOnClickClothItemListener iOnClickClothItemListener) {
        this.mListClothes = mListClothes;
        this.iOnClickClothItemListener = iOnClickClothItemListener;
    }

    @NonNull
    @Override
    public ClothGridViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemClothGridBinding itemClothGridBinding = ItemClothGridBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ClothGridViewHolder(itemClothGridBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ClothGridViewHolder holder, int position) {
        Sport Sport = mListClothes.get(position);
        if (Sport == null) {
            return;
        }
        GlideUtils.loadUrl(Sport.getImage(), holder.mItemClothGridBinding.imgCloth);
        if (Sport.getSale() <= 0) {
            holder.mItemClothGridBinding.tvSaleOff.setVisibility(View.GONE);
            holder.mItemClothGridBinding.tvPrice.setVisibility(View.GONE);

            String strPrice = Sport.getPrice() + Constant.CURRENCY;
            holder.mItemClothGridBinding.tvPriceSale.setText(strPrice);
        } else {
            holder.mItemClothGridBinding.tvSaleOff.setVisibility(View.VISIBLE);
            holder.mItemClothGridBinding.tvPrice.setVisibility(View.VISIBLE);

            String strSale = "Giảm " + Sport.getSale() + "%";
            holder.mItemClothGridBinding.tvSaleOff.setText(strSale);

            String strOldPrice = Sport.getPrice() + Constant.CURRENCY;
            holder.mItemClothGridBinding.tvPrice.setText(strOldPrice);
            holder.mItemClothGridBinding.tvPrice.setPaintFlags(holder.mItemClothGridBinding.tvPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

            String strRealPrice = Sport.getRealPrice() + Constant.CURRENCY;
            holder.mItemClothGridBinding.tvPriceSale.setText(strRealPrice);
        }
        holder.mItemClothGridBinding.tvClothName.setText(Sport.getName());

        holder.mItemClothGridBinding.layoutItem.setOnClickListener(v -> iOnClickClothItemListener.onClickItemCloth(Sport));
    }

    @Override
    public int getItemCount() {
        return null == mListClothes ? 0 : mListClothes.size();
    }

    public static class ClothGridViewHolder extends RecyclerView.ViewHolder {

        private final ItemClothGridBinding mItemClothGridBinding;

        public ClothGridViewHolder(ItemClothGridBinding itemClothGridBinding) {
            super(itemClothGridBinding.getRoot());
            this.mItemClothGridBinding = itemClothGridBinding;
        }
    }
}
