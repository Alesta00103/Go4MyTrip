package com.aleksandra.go4mytrip.ui.main;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.aleksandra.go4mytrip.R;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.tab_text_1, R.string.tab_text_2, R.string.tab_text_3, R.string.tab_text_4};
    private final Context mContext;

    public SectionsPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
      ////  return PackingClothesFragment.newInstance(position + 1);
        switch (position){
            case 0:
                PackingEssentialsFragment packingEssentialsFragment = new PackingEssentialsFragment();
                return packingEssentialsFragment;
            case 1:
                PackingClothesFragment packingClothesFragment = new PackingClothesFragment();
                return packingClothesFragment;
            case 2:
                PackingToiletriesFragment packingToiletriesFragment = new PackingToiletriesFragment();
                return packingToiletriesFragment;
            case 3:
                PackingOthersFragment packingOthersFragment = new PackingOthersFragment();
                return packingOthersFragment;
            default:
                return null;
        }
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_TITLES[position]);
    }

    @Override
    public int getCount() {
        // Show 4 total pages.
        return 4;
    }
}