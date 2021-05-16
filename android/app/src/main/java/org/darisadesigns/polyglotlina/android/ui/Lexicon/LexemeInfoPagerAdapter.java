package org.darisadesigns.polyglotlina.android.ui.Lexicon;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import org.darisadesigns.polyglotlina.android.R;
import org.jetbrains.annotations.NotNull;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class LexemeInfoPagerAdapter extends FragmentPagerAdapter {

    @StringRes
    private static final int[] TAB_TITLES = new int[]{
            R.string.lexeme_info_tab_general,
            R.string.lexeme_info_tab_conjugations,
            R.string.lexeme_info_tab_etymology,
            R.string.lexeme_info_tab_logographs
    };
    private final Context mContext;

    public LexemeInfoPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @NotNull
    @Override
    public Fragment getItem(int position) {
        Fragment tabView = LexemeGeneralFragment.newInstance();;
        switch (position) {
            case 1:
                tabView = LexemeConjugationsFragment.newInstance();
                break;
        }
        return tabView;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_TITLES[position]);
    }

    @Override
    public int getCount() {
        return TAB_TITLES.length;
    }
}