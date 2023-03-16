package com.example.footballfashion.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.footballfashion.ControllerApplication;
import com.example.footballfashion.R;
import com.example.footballfashion.activity.MainActivity;
import com.example.footballfashion.adapter.CartAdapter;
import com.example.footballfashion.constant.Constant;
import com.example.footballfashion.constant.GlobalFunction;
import com.example.footballfashion.database.ClothDatabase;
import com.example.footballfashion.databinding.FragmentCartBinding;
import com.example.footballfashion.event.ReloadListCartEvent;
import com.example.footballfashion.model.Sport;
import com.example.footballfashion.model.Order;
import com.example.footballfashion.util.StringUtil;
import com.example.footballfashion.util.Utils;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class CartFragment extends BaseFragment {

    private FragmentCartBinding mFragmentCartBinding;
    private CartAdapter mCartAdapter;
    private List<Sport> mListSportCart;
    private int mAmount;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mFragmentCartBinding = FragmentCartBinding.inflate(inflater, container, false);

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        displayListClothInCart();
        mFragmentCartBinding.tvOrderCart.setOnClickListener(v -> onClickOrderCart());

        return mFragmentCartBinding.getRoot();
    }

    @Override
    protected void initToolbar() {
        if (getActivity() != null) {
            ((MainActivity) getActivity()).setToolBar(false, getString(R.string.cart));
        }
    }

    private void displayListClothInCart() {
        if (getActivity() == null) {
            return;
        }
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mFragmentCartBinding.rcvClothCart.setLayoutManager(linearLayoutManager);
        DividerItemDecoration itemDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
        mFragmentCartBinding.rcvClothCart.addItemDecoration(itemDecoration);

        initDataClothCart();
    }

    private void initDataClothCart() {
        mListSportCart = new ArrayList<>();
        mListSportCart = ClothDatabase.getInstance(getActivity()).clothDAO().getListClothCart();
        if (mListSportCart == null || mListSportCart.isEmpty()) {
            return;
        }

        mCartAdapter = new CartAdapter(mListSportCart, new CartAdapter.IClickListener() {
            @Override
            public void clickDeteteCloth(Sport Sport, int position) {
                deleteClothFromCart(Sport, position);
            }

            @Override
            public void updateItemCloth(Sport Sport, int position) {
                ClothDatabase.getInstance(getActivity()).clothDAO().updateCloth(Sport);
                mCartAdapter.notifyItemChanged(position);

                calculateTotalPrice();
            }
        });
        mFragmentCartBinding.rcvClothCart.setAdapter(mCartAdapter);

        calculateTotalPrice();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void clearCart() {
        if (mListSportCart != null) {
            mListSportCart.clear();
        }
        mCartAdapter.notifyDataSetChanged();
        calculateTotalPrice();
    }

    private void calculateTotalPrice() {
        List<Sport> listSportCart = ClothDatabase.getInstance(getActivity()).clothDAO().getListClothCart();
        if (listSportCart == null || listSportCart.isEmpty()) {
            String strZero = 0 + Constant.CURRENCY;
            mFragmentCartBinding.tvTotalPrice.setText(strZero);
            mAmount = 0;
            return;
        }

        int totalPrice = 0;
        for (Sport Sport : listSportCart) {
            totalPrice = totalPrice + Sport.getTotalPrice();
        }

        String strTotalPrice = totalPrice + Constant.CURRENCY;
        mFragmentCartBinding.tvTotalPrice.setText(strTotalPrice);
        mAmount = totalPrice;
    }

    private void deleteClothFromCart(Sport Sport, int position) {
        new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.confirm_delete_clothes))
                .setMessage(getString(R.string.message_delete_clothes))
                .setPositiveButton(getString(R.string.delete), (dialog, which) -> {
                    ClothDatabase.getInstance(getActivity()).clothDAO().deleteCloth(Sport);
                    mListSportCart.remove(position);
                    mCartAdapter.notifyItemRemoved(position);

                    calculateTotalPrice();
                })
                .setNegativeButton(getString(R.string.dialog_cancel), (dialog, which) -> dialog.dismiss())
                .show();
    }

    public void onClickOrderCart() {
        if (getActivity() == null) {
            return;
        }

        if (mListSportCart == null || mListSportCart.isEmpty()) {
            return;
        }

        @SuppressLint("InflateParams") View viewDialog = getLayoutInflater().inflate(R.layout.layout_bottom_sheet_order, null);

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getActivity());
        bottomSheetDialog.setContentView(viewDialog);
        bottomSheetDialog.getBehavior().setState(BottomSheetBehavior.STATE_EXPANDED);

        // init ui
        TextView tvClothsOrder = viewDialog.findViewById(R.id.tv_clothes_order);
        TextView tvPriceOrder = viewDialog.findViewById(R.id.tv_price_order);
        TextView edtNameOrder = viewDialog.findViewById(R.id.edt_name_order);
        TextView edtPhoneOrder = viewDialog.findViewById(R.id.edt_phone_order);
        TextView edtAddressOrder = viewDialog.findViewById(R.id.edt_address_order);
        TextView tvCancelOrder = viewDialog.findViewById(R.id.tv_cancel_order);
        TextView tvCreateOrder = viewDialog.findViewById(R.id.tv_create_order);

        // Set data
        tvClothsOrder.setText(getStringListClothsOrder());
        tvPriceOrder.setText(mFragmentCartBinding.tvTotalPrice.getText().toString());

        // Set listener
        tvCancelOrder.setOnClickListener(v -> bottomSheetDialog.dismiss());

        tvCreateOrder.setOnClickListener(v -> {
            String strName = edtNameOrder.getText().toString().trim();
            String strPhone = edtPhoneOrder.getText().toString().trim();
            String strAddress = edtAddressOrder.getText().toString().trim();

            if (StringUtil.isEmpty(strName) || StringUtil.isEmpty(strPhone) || StringUtil.isEmpty(strAddress)) {
                GlobalFunction.showToastMessage(getActivity(), getString(R.string.message_enter_infor_order));
            } else {
                long id = System.currentTimeMillis();
                Order order = new Order(id, strName, strPhone, strAddress,
                        mAmount, getStringListClothsOrder(), Constant.TYPE_PAYMENT_CASH);
                ControllerApplication.get(getActivity()).getBookingDatabaseReference()
                        .child(Utils.getDeviceId(getActivity()))
                        .child(String.valueOf(id))
                        .setValue(order, (error1, ref1) -> {
                            GlobalFunction.showToastMessage(getActivity(), getString(R.string.msg_order_success));
                            GlobalFunction.hideSoftKeyboard(getActivity());
                            bottomSheetDialog.dismiss();

                            ClothDatabase.getInstance(getActivity()).clothDAO().deleteAllCloth();
                            clearCart();
                        });
            }
        });

        bottomSheetDialog.show();
    }

    private String getStringListClothsOrder() {
        if (mListSportCart == null || mListSportCart.isEmpty()) {
            return "";
        }
        String result = "";
        for (Sport Sport : mListSportCart) {
            if (StringUtil.isEmpty(result)) {
                result = "- " + Sport.getName() + " (" + Sport.getRealPrice() + Constant.CURRENCY + ") "
                        + "- " + getString(R.string.quantity) + " " + Sport.getCount();
            } else {
                result = result + "\n" + "- " + Sport.getName() + " (" + Sport.getRealPrice() + Constant.CURRENCY + ") "
                        + "- " + getString(R.string.quantity) + " " + Sport.getCount();
            }
        }
        return result;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ReloadListCartEvent event) {
        displayListClothInCart();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }
}