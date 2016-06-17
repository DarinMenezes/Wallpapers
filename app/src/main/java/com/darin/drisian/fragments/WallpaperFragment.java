package com.darin.drisian.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.darin.drisian.data.AuthorData;
import com.darin.drisian.views.CustomImageView;
import com.darin.drisian.R;
import com.darin.drisian.Supplier;
import com.darin.drisian.adapters.ArtistPagerAdapter;
import com.darin.drisian.data.WallData;

import java.util.ArrayList;
import java.util.Random;


public class WallpaperFragment extends Fragment {

    TabLayout tabLayout;
    CustomImageView header, headerIcon;

    ArtistPagerAdapter adapter;
    ViewPager viewPager;
    CollapsingToolbarLayout collapsingToolbarLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_wallpapers, container, false);

        collapsingToolbarLayout = (CollapsingToolbarLayout) v.findViewById(R.id.collapsing_toolbar);
        header = (CustomImageView) v.findViewById(R.id.header);
        headerIcon = (CustomImageView) v.findViewById(R.id.headerIcon);
        viewPager = (ViewPager) v.findViewById(R.id.pager);
        tabLayout = (TabLayout) v.findViewById(R.id.tl);

        adapter = new ArtistPagerAdapter(getContext(), getActivity().getSupportFragmentManager());
        viewPager.setAdapter(adapter);

        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                refresh(position);
            }
        });

        refresh(0);
        return v;
    }

    public void refresh(int position) {
        Supplier supplier = (Supplier) getContext().getApplicationContext();

        AuthorData authorData = supplier.getAuthors(getContext()).get(position);
        Glide.with(getContext()).load(authorData.image).into(headerIcon);

        ArrayList<WallData> walls = supplier.getWallpapers(getContext(), position);
        Random rand = new Random();
        Glide.with(getContext()).load(walls.get(rand.nextInt(walls.size())).url).into(header);
    }
}