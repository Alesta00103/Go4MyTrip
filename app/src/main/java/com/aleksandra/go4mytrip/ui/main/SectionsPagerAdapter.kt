package com.aleksandra.go4mytrip.ui.main

import android.content.Context
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.aleksandra.go4mytrip.R
import com.aleksandra.go4mytrip.ui.main.clothes.PackingClothesFragment

class SectionsPagerAdapter(private val mContext: Context, fm: FragmentManager) : FragmentPagerAdapter(fm) {
    override fun getItem(position: Int): Fragment {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        ////  return PackingClothesFragment.newInstance(position + 1);
        return when (position) {
            0 -> PackingEssentialsFragment()
            1 -> PackingClothesFragment()
            2 -> PackingToiletriesFragment()
            3 -> PackingOthersFragment()
            else -> PackingEssentialsFragment()
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return mContext.resources.getString(TAB_TITLES[position])
    }

    override fun getCount(): Int {
        // Show 4 total pages.
        return 4
    }

    companion object {
        @StringRes
        private val TAB_TITLES = intArrayOf(R.string.tab_text_1, R.string.tab_text_2, R.string.tab_text_3, R.string.tab_text_4)
    }
}