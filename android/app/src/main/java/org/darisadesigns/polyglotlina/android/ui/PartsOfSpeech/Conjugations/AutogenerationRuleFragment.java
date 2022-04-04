package org.darisadesigns.polyglotlina.android.ui.PartsOfSpeech.Conjugations;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.darisadesigns.polyglotlina.DictCore;
import org.darisadesigns.polyglotlina.Nodes.ConjugationGenRule;
import org.darisadesigns.polyglotlina.Nodes.TypeNode;
import org.darisadesigns.polyglotlina.android.PolyGlot;
import org.darisadesigns.polyglotlina.android.R;

public class AutogenerationRuleFragment extends Fragment {

    private static final String TAG = "AutogenerationRuleFragment";

    private DictCore core;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_autogeneration_rules, container, false);
        core = ((PolyGlot)requireActivity().getApplicationContext()).getCore();

        AutogenRulesViewModel rulesViewModel = new ViewModelProvider(requireActivity()).get(AutogenRulesViewModel.class);
        TypeNode posNode = rulesViewModel.getPosNode();

        rulesViewModel.getLiveData().observe(getViewLifecycleOwner(), conjugationPair -> {
            // if (null == conjugationPair) return;
        });
        return root;
    }
}