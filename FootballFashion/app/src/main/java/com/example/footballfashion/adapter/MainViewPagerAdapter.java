package com.example.footballfashion.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.footballfashion.fragment.CartFragment;
import com.example.footballfashion.fragment.ContactFragment;
import com.example.footballfashion.fragment.FeedbackFragment;
import com.example.footballfashion.fragment.HomeFragment;
import com.example.footballfashion.fragment.OrderFragment;

public class MainViewPagerAdapter extends FragmentStateAdapter {

    public MainViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {

            case 1:
                return new CartFragment();

            case 2:
                return new FeedbackFragment();

            case 3:
                return new ContactFragment();

            case 4:
                return new OrderFragment();

            default:
                return new HomeFragment();
        }
    }

    // This error
    @Override
    public int getItemCount() {
        return 5;
    }
}
