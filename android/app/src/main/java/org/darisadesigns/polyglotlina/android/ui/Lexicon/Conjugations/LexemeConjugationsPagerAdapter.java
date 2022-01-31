package org.darisadesigns.polyglotlina.android.ui.Lexicon.Conjugations;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.List;

public class LexemeConjugationsPagerAdapter extends FragmentStateAdapter implements TabLayoutMediator.TabConfigurationStrategy {

    private static final String TAG = "ConjugationPageAdapter";
    private final Context mContext;
    private final List<LexemeConjugationTabFragment> fragmentList;

    public LexemeConjugationsPagerAdapter(Context context, Lifecycle lifecycle, FragmentManager fm, List<LexemeConjugationTabFragment> fragmentList) {
        super(fm, lifecycle);
        mContext = context;
        this.fragmentList = fragmentList;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getItemCount() {
        return fragmentList.size();
    }

    @Override
    public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
        tab.setText(fragmentList.get(position).getTabName());
    }
}
