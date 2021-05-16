package org.darisadesigns.polyglotlina.android.ui.Lexicon;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.darisadesigns.polyglotlina.android.R;

public class LexemeConjugationsFragment extends Fragment {

    private LexemeInfoViewModel mViewModel;

    public static LexemeConjugationsFragment newInstance() {
        return new LexemeConjugationsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.lexeme_conjugations_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(LexemeInfoViewModel.class);
        // TODO: Use the ViewModel
    }

}