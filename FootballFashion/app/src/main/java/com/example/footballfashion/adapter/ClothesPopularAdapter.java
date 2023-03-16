package com.example.footballfashion.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.footballfashion.databinding.ItemClothPopularBinding;
import com.example.footballfashion.listener.IOnClickClothItemListener;
import com.example.footballfashion.model.Sport;
import com.example.footballfashion.util.GlideUtils;

import java.util.List;

public class ClothesPopularAdapter extends RecyclerView.Adapter<ClothesPopularAdapter.ClothPopularViewHolder> {

    private final List<Sport> mListSports;
    public final IOnClickClothItemListener iOnClickClothItemListener;

    public ClothesPopularAdapter(List<Sport> mListSports, IOnClickClothItemListener iOnClickClothItemListener) {
        this.mListSports = mListSports;
        this.iOnClickClothItemListener = iOnClickClothItemListener;
    }

    @NonNull
    @Override
    public ClothPopularViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemClothPopularBinding itemClothPopularBinding = ItemClothPopularBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ClothPopularViewHolder(itemClothPopularBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ClothPopularViewHolder holder, int position) {
        Sport Sport = mListSports.get(position);
        if (Sport == null) {
            return;
        }
        GlideUtils.loadUrlBanner(Sport.getBanner(), holder.mItemClothPopularBinding.imageCloth);
        if (Sport.getSale() <= 0) {
            holder.mItemClothPopularBinding.tvSaleOff.setVisibility(View.GONE);
        } else {
            holder.mItemClothPopularBinding.tvSaleOff.setVisibility(View.VISIBLE);
            String strSale = "Giảm " + Sport.getSale() + "%";
            holder.mItemClothPopularBinding.tvSaleOff.setText(strSale);
        }
        holder.mItemClothPopularBinding.layoutItem.setOnClickListener(v -> iOnClickClothItemListener.onClickItemCloth(Sport));
    }

    @Override
    public int getItemCount() {
        if (mListSports != null) {
            return mListSports.size();
        }
        return 0;
    }

    public static class ClothPopularViewHolder extends RecyclerView.ViewHolder {

        private final ItemClothPopularBinding mItemClothPopularBinding;

        public ClothPopularViewHolder(@NonNull ItemClothPopularBinding itemClothPopularBinding) {
            super(itemClothPopularBinding.getRoot());
            this.mItemClothPopularBinding = itemClothPopularBinding;
        }
    }
}
