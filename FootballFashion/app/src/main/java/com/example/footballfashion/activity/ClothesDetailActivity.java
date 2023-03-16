package com.example.footballfashion.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;

import android.annotation.SuppressLint;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.footballfashion.R;
import com.example.footballfashion.adapter.MoreImageAdapter;
import com.example.footballfashion.constant.Constant;
import com.example.footballfashion.database.ClothDatabase;
import com.example.footballfashion.databinding.ActivityClothesDetailBinding;
import com.example.footballfashion.event.ReloadListCartEvent;
import com.example.footballfashion.model.Sport;
import com.example.footballfashion.util.GlideUtils;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

public class ClothesDetailActivity extends AppCompatActivity {

    private ActivityClothesDetailBinding mActivityClothesDetailBinding;
    private Sport mSport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityClothesDetailBinding = ActivityClothesDetailBinding.inflate(getLayoutInflater());
        setContentView(mActivityClothesDetailBinding.getRoot());

        getDataIntent();
        initToolbar();
        setDataClothesDetail();
        initListener();
    }

    private void getDataIntent() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mSport = (Sport) bundle.get(Constant.KEY_INTENT_CLOTH_OBJECT);
        }
    }

    private void initToolbar() {
        mActivityClothesDetailBinding.toolbar.imgBack.setVisibility(View.VISIBLE);
        mActivityClothesDetailBinding.toolbar.imgCart.setVisibility(View.VISIBLE);
        mActivityClothesDetailBinding.toolbar.tvTitle.setText(getString(R.string.clothes_detail_title));

        mActivityClothesDetailBinding.toolbar.imgBack.setOnClickListener(v -> onBackPressed());
    }

    private void setDataClothesDetail() {
        if (mSport == null) {
            return;
        }

        GlideUtils.loadUrlBanner(mSport.getBanner(), mActivityClothesDetailBinding.imageCloth);
        if (mSport.getSale() <= 0) {
            mActivityClothesDetailBinding.tvSaleOff.setVisibility(View.GONE);
            mActivityClothesDetailBinding.tvPrice.setVisibility(View.GONE);

            String strPrice = mSport.getPrice() + Constant.CURRENCY;
            mActivityClothesDetailBinding.tvPriceSale.setText(strPrice);
        } else {
            mActivityClothesDetailBinding.tvSaleOff.setVisibility(View.VISIBLE);
            mActivityClothesDetailBinding.tvPrice.setVisibility(View.VISIBLE);

            String strSale = "Giảm " + mSport.getSale() + "%";
            mActivityClothesDetailBinding.tvSaleOff.setText(strSale);

            String strPriceOld = mSport.getPrice() + Constant.CURRENCY;
            mActivityClothesDetailBinding.tvPrice.setText(strPriceOld);
            mActivityClothesDetailBinding.tvPrice.setPaintFlags(mActivityClothesDetailBinding.tvPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

            String strRealPrice = mSport.getRealPrice() + Constant.CURRENCY;
            mActivityClothesDetailBinding.tvPriceSale.setText(strRealPrice);
        }
        mActivityClothesDetailBinding.tvClothName.setText(mSport.getName());
        mActivityClothesDetailBinding.tvClothDescription.setText(mSport.getDescription());

        displayListMoreImages();

        setStatusButtonAddToCart();
    }

    private void displayListMoreImages() {
        if (mSport.getImages() == null || mSport.getImages().isEmpty()) {
            mActivityClothesDetailBinding.tvMoreImageLabel.setVisibility(View.GONE);
            return;
        }
        mActivityClothesDetailBinding.tvMoreImageLabel.setVisibility(View.VISIBLE);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        mActivityClothesDetailBinding.rcvImages.setLayoutManager(gridLayoutManager);

        MoreImageAdapter moreImageAdapter = new MoreImageAdapter(mSport.getImages());
        mActivityClothesDetailBinding.rcvImages.setAdapter(moreImageAdapter);
    }

    private void setStatusButtonAddToCart() {
        if (isClothInCart()) {
            mActivityClothesDetailBinding.tvAddToCart.setBackgroundResource(R.drawable.bg_gray_shape_corner_6);
            mActivityClothesDetailBinding.tvAddToCart.setText(getString(R.string.added_to_cart));
            mActivityClothesDetailBinding.tvAddToCart.setTextColor(ContextCompat.getColor(this, R.color.textColorPrimary));
            mActivityClothesDetailBinding.toolbar.imgCart.setVisibility(View.GONE);
        } else {
            mActivityClothesDetailBinding.tvAddToCart.setBackgroundResource(R.drawable.bg_green_shape_corner_6);
            mActivityClothesDetailBinding.tvAddToCart.setText(getString(R.string.add_to_cart));
            mActivityClothesDetailBinding.tvAddToCart.setTextColor(ContextCompat.getColor(this, R.color.white));
            mActivityClothesDetailBinding.toolbar.imgCart.setVisibility(View.VISIBLE);
        }
    }

    private boolean isClothInCart() {
        List<Sport> list = ClothDatabase.getInstance(this).clothDAO().checkClothInCart(mSport.getId());
        return list != null && !list.isEmpty();
    }

    private void initListener() {
        mActivityClothesDetailBinding.tvAddToCart.setOnClickListener(v -> onClickAddToCart());
        mActivityClothesDetailBinding.toolbar.imgCart.setOnClickListener(v -> onClickAddToCart());
    }

    public void onClickAddToCart() {
        if (isClothInCart()) {
            return;
        }

        @SuppressLint("InflateParams") View viewDialog = getLayoutInflater().inflate(R.layout.layout_bottom_sheet_cart, null);

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(viewDialog);

        ImageView imgClothCart = viewDialog.findViewById(R.id.img_cloth_cart);
        TextView tvClothNameCart = viewDialog.findViewById(R.id.tv_cloth_name_cart);
        TextView tvClothPriceCart = viewDialog.findViewById(R.id.tv_cloth_price_cart);
        TextView tvSubtractCount = viewDialog.findViewById(R.id.tv_subtract);
        TextView tvCount = viewDialog.findViewById(R.id.tv_count);
        TextView tvAddCount = viewDialog.findViewById(R.id.tv_add);
        TextView tvCancel = viewDialog.findViewById(R.id.tv_cancel);
        TextView tvAddCart = viewDialog.findViewById(R.id.tv_add_cart);

        GlideUtils.loadUrl(mSport.getImage(), imgClothCart);
        tvClothNameCart.setText(mSport.getName());

        int totalPrice = mSport.getRealPrice();
        String strTotalPrice = totalPrice + Constant.CURRENCY;
        tvClothPriceCart.setText(strTotalPrice);

        mSport.setCount(1);
        mSport.setTotalPrice(totalPrice);

        tvSubtractCount.setOnClickListener(v -> {
            int count = Integer.parseInt(tvCount.getText().toString());
            if (count <= 1) {
                return;
            }
            int newCount = Integer.parseInt(tvCount.getText().toString()) - 1;
            tvCount.setText(String.valueOf(newCount));

            int totalPrice1 = mSport.getRealPrice() * newCount;
            String strTotalPrice1 = totalPrice1 + Constant.CURRENCY;
            tvClothPriceCart.setText(strTotalPrice1);

            mSport.setCount(newCount);
            mSport.setTotalPrice(totalPrice1);
        });

        tvAddCount.setOnClickListener(v -> {
            int newCount = Integer.parseInt(tvCount.getText().toString()) + 1;
            tvCount.setText(String.valueOf(newCount));

            int totalPrice2 = mSport.getRealPrice() * newCount;
            String strTotalPrice2 = totalPrice2 + Constant.CURRENCY;
            tvClothPriceCart.setText(strTotalPrice2);

            mSport.setCount(newCount);
            mSport.setTotalPrice(totalPrice2);
        });

        tvCancel.setOnClickListener(v -> bottomSheetDialog.dismiss());

        tvAddCart.setOnClickListener(v -> {
            ClothDatabase.getInstance(ClothesDetailActivity.this).clothDAO().insertCloth(mSport);
            bottomSheetDialog.dismiss();
            setStatusButtonAddToCart();
            EventBus.getDefault().post(new ReloadListCartEvent());
        });

        bottomSheetDialog.show();
    }
}