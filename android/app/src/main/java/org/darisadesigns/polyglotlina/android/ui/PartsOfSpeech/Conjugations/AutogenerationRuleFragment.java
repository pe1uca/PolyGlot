package org.darisadesigns.polyglotlina.android.ui.PartsOfSpeech.Conjugations;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.darisadesigns.polyglotlina.DictCore;
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

        TextView ruleName = root.findViewById(R.id.txtRuleName);
        TextView ruleRegex = root.findViewById(R.id.txtRuleRegex);

        AutogenRulesViewModel rulesViewModel = new ViewModelProvider(requireActivity()).get(AutogenRulesViewModel.class);

        rulesViewModel.getLiveData().observe(getViewLifecycleOwner(), conjugationGenRule -> {
             if (null == conjugationGenRule) return;
            ruleName.setText(conjugationGenRule.getName());
            ruleRegex.setText(conjugationGenRule.getRegex());
        });
        return root;
    }
}