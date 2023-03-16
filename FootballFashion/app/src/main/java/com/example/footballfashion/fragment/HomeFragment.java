package com.example.footballfashion.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;

import com.example.footballfashion.ControllerApplication;
import com.example.footballfashion.R;
import com.example.footballfashion.activity.ClothesDetailActivity;
import com.example.footballfashion.activity.MainActivity;
import com.example.footballfashion.adapter.ClothesGridAdapter;
import com.example.footballfashion.adapter.ClothesPopularAdapter;
import com.example.footballfashion.constant.Constant;
import com.example.footballfashion.constant.GlobalFunction;
import com.example.footballfashion.databinding.FragmentHomeBinding;
import com.example.footballfashion.model.Sport;
import com.example.footballfashion.util.StringUtil;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends BaseFragment {

    private FragmentHomeBinding mFragmentHomeBinding;

    private List<Sport> mListClothes;
    private List<Sport> mListClothesPopular;
    private ClothesPopularAdapter mClothesPopularAdapter;
    private ClothesGridAdapter mClothesGridAdapter;

    private final Handler mHandlerBanner = new Handler();
    private final Runnable mRunnableBanner = new Runnable() {
        @Override
        public void run() {
            if (mListClothesPopular == null || mListClothesPopular.isEmpty()) {
                return;
            }
            if (mFragmentHomeBinding.viewpager2.getCurrentItem() == mListClothesPopular.size() - 1) {
                mFragmentHomeBinding.viewpager2.setCurrentItem(0);
                return;
            }
            mFragmentHomeBinding.viewpager2.setCurrentItem(mFragmentHomeBinding.viewpager2.getCurrentItem() + 1);
        }
    };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mFragmentHomeBinding = FragmentHomeBinding.inflate(inflater, container, false);

        getListClothesFromFirebase("");
        initListener();

        return mFragmentHomeBinding.getRoot();
    }

    @Override
    protected void initToolbar() {
        if (getActivity() != null) {
            ((MainActivity) getActivity()).setToolBar(true, getString(R.string.home));
        }
    }

    private void initListener() {
        mFragmentHomeBinding.edtSearchName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Do nothing
            }

            @Override
            public void afterTextChanged(Editable s) {
                String strKey = s.toString().trim();
                if (strKey.equals("") || strKey.length() == 0) {
                    if (mListClothes != null) mListClothes.clear();
                    getListClothesFromFirebase("");
                }
            }
        });

        mFragmentHomeBinding.imgSearch.setOnClickListener(view -> searchCloth());

        mFragmentHomeBinding.edtSearchName.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchCloth();
                return true;
            }
            return false;
        });
    }

    private void displayListClothPopular() {
        mClothesPopularAdapter = new ClothesPopularAdapter(getListClothPopular(), this::goToClothDetail);
        mFragmentHomeBinding.viewpager2.setAdapter(mClothesPopularAdapter);
        mFragmentHomeBinding.indicator3.setViewPager(mFragmentHomeBinding.viewpager2);

        mFragmentHomeBinding.viewpager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                mHandlerBanner.removeCallbacks(mRunnableBanner);
                mHandlerBanner.postDelayed(mRunnableBanner, 3000);
            }
        });
    }

    private void displayListClothSuggest() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        mFragmentHomeBinding.rcvCloth.setLayoutManager(gridLayoutManager);

        mClothesGridAdapter = new ClothesGridAdapter(mListClothes, this::goToClothDetail);
        mFragmentHomeBinding.rcvCloth.setAdapter(mClothesGridAdapter);
    }

    private List<Sport> getListClothPopular() {
        mListClothesPopular = new ArrayList<>();
        if (mListClothes == null || mListClothes.isEmpty()) {
            return mListClothesPopular;
        }
        for (Sport Sport : mListClothes) {
            if (Sport.isPopular()) {
                mListClothesPopular.add(Sport);
            }
        }
        return mListClothesPopular;
    }

    private void getListClothesFromFirebase(String key) {
        if (getActivity() == null) {
            return;
        }
        ControllerApplication.get(getActivity()).getClothesDatabaseReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mFragmentHomeBinding.layoutContent.setVisibility(View.VISIBLE);
                mListClothes = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Sport Sport = dataSnapshot.getValue(Sport.class);
                    if (Sport == null) {
                        return;
                    }

                    if (StringUtil.isEmpty(key)) {
                        mListClothes.add(0, Sport);
                    } else {
                        if (GlobalFunction.getTextSearch(Sport.getName()).toLowerCase().trim()
                                .contains(GlobalFunction.getTextSearch(key).toLowerCase().trim())) {
                            mListClothes.add(0, Sport);
                        }
                    }
                }
                displayListClothPopular();
                displayListClothSuggest();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                GlobalFunction.showToastMessage(getActivity(), getString(R.string.msg_get_date_error));
            }
        });
    }

    private void searchCloth() {
        String strKey = mFragmentHomeBinding.edtSearchName.getText().toString().trim();
        if (mListClothes != null) mListClothes.clear();
        getListClothesFromFirebase(strKey);
        GlobalFunction.hideSoftKeyboard(getActivity());
    }

    private void goToClothDetail(@NonNull Sport Sport) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constant.KEY_INTENT_CLOTH_OBJECT, Sport);
        GlobalFunction.startActivity(getActivity(), ClothesDetailActivity.class, bundle);
    }

    @Override
    public void onPause() {
        super.onPause();
        mHandlerBanner.removeCallbacks(mRunnableBanner);
    }

    @Override
    public void onResume() {
        super.onResume();
        mHandlerBanner.postDelayed(mRunnableBanner, 3000);
    }
}