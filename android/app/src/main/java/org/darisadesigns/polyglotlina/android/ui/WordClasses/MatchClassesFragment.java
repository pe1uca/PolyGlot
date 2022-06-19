package org.darisadesigns.polyglotlina.android.ui.WordClasses;

import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.darisadesigns.polyglotlina.DictCore;
import org.darisadesigns.polyglotlina.Nodes.WordClass;
import org.darisadesigns.polyglotlina.Nodes.WordClassValue;
import org.darisadesigns.polyglotlina.android.PolyGlot;
import org.darisadesigns.polyglotlina.android.R;
import org.darisadesigns.polyglotlina.android.ui.ToggleableRadioButton;

import java.util.Collection;

public class MatchClassesFragment extends Fragment {

    private static final String TAG = "MatchClassesFragment";

    private static final String POS_ID_EXTRA = "pos-id";
    private static final String INCLUDE_ALL_FLAG_EXTRA = "include-all-flag";

    private int posId;
    private boolean includeAllFlag;

    public static MatchClassesFragment newInstance(Integer posId, Boolean includeAllCheckbox) {
        MatchClassesFragment fragment = new MatchClassesFragment();
        Bundle args = new Bundle();
        args.putInt(POS_ID_EXTRA, posId);
        args.putBoolean(INCLUDE_ALL_FLAG_EXTRA, includeAllCheckbox);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            posId = getArguments().getInt(POS_ID_EXTRA);
            includeAllFlag = getArguments().getBoolean(INCLUDE_ALL_FLAG_EXTRA);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_match_classes, container, false);

        PolyGlot polyGlot;
        if (null != getParentFragment()) {
            polyGlot = (PolyGlot)getParentFragment().getActivity().getApplicationContext();
        }
        else {
            polyGlot = (PolyGlot)requireActivity().getApplicationContext();
        }
        DictCore core = polyGlot.getCore();

        WordClass[] classes = core.getWordClassCollection().getClassesForType(posId);

        if (classes.length == 0) {
            view.findViewById(R.id.txtNoClasses).setVisibility(View.VISIBLE);
            return view;
        }

        LinearLayout checkboxesLayout = view.findViewById(R.id.checkboxesLayout);
        CheckBox chkAllClasses = view.findViewById(R.id.chkAllClasses);
        chkAllClasses.setVisibility(includeAllFlag ? View.VISIBLE : View.GONE);

        ViewModelStoreOwner owner = getParentFragment();
        if (null == owner) owner = getActivity();
        MatchClassesViewModel matchClassesViewModel = new ViewModelProvider(owner).get(MatchClassesViewModel.class);

        matchClassesViewModel.getLiveData().observe(getViewLifecycleOwner(), conjugationGenRule -> {
            checkboxesLayout.removeAllViews();
            if (null == conjugationGenRule) {
                return;
            }
            for(WordClass wordClass : classes) {
                Collection<WordClassValue> classValues = wordClass.getValues();
                if (classValues.isEmpty()) {
                    continue;
                }
                TextView txtView = new TextView(requireContext());
                txtView.setText(wordClass.getValue());
                checkboxesLayout.addView(txtView);
                RadioGroup group = new RadioGroup(requireContext());
                classValues.forEach((wordClassValue -> {
                    ToggleableRadioButton button = new ToggleableRadioButton(requireContext());
                    button.setText(wordClassValue.getValue());
                    button.setChecked(conjugationGenRule.doesRuleApplyToClassValue(wordClass.getId(), wordClassValue.getId(), true));
                    button.setOnCheckedChangeListener((compoundButton, b) -> {
                        if (b) {
                            conjugationGenRule.addClassToFilterList(wordClass.getId(), wordClassValue.getId());
                        }
                        else {
                            conjugationGenRule.removeClassFromFilterList(wordClass.getId(), wordClassValue.getId());
                        }
                    });
                    group.addView(button);
                }));
                checkboxesLayout.addView(group);
                View divider = new View(requireContext());
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.height = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics());
                divider.setLayoutParams(layoutParams);
                divider.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.teal_700));
                checkboxesLayout.addView(divider);
            }
        });

        chkAllClasses.setOnCheckedChangeListener((compoundButton, b) -> {
            for (int i = 0; i < checkboxesLayout.getChildCount(); i++) {
                View childView = checkboxesLayout.getChildAt(i);
                if (!(childView instanceof RadioGroup)) continue;
                RadioGroup group = (RadioGroup) childView;
                group.clearCheck();
                for (int j = 0; j < group.getChildCount(); j++) {
                    group.getChildAt(j).setEnabled(!b);
                }
            }
        });

        return view;
    }
}