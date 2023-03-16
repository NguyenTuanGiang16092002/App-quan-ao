package com.example.footballfashion.fragment;

import androidx.fragment.app.Fragment;

public abstract class BaseFragment extends Fragment {

    @Override
    public void onResume() {
        super.onResume();
        initToolbar();
    }

    protected abstract void initToolbar();
}
