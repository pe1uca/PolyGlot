package org.darisadesigns.polyglotlina.android.ui.PartsOfSpeech.Conjugations;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.AutoTransition;
import androidx.transition.TransitionManager;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.card.MaterialCardView;

import org.darisadesigns.polyglotlina.DictCore;
import org.darisadesigns.polyglotlina.Nodes.ConjugationGenRule;
import org.darisadesigns.polyglotlina.Nodes.ConjugationGenTransform;
import org.darisadesigns.polyglotlina.android.AndroidInfoBox;
import org.darisadesigns.polyglotlina.android.AndroidPropertiesManager;
import org.darisadesigns.polyglotlina.android.PolyGlot;
import org.darisadesigns.polyglotlina.android.R;
import org.darisadesigns.polyglotlina.android.ui.WordClasses.MatchClassesFragment;
import org.darisadesigns.polyglotlina.android.ui.WordClasses.MatchClassesViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class AutogenerationRuleFragment extends Fragment implements RuleTransformRecyclerViewAdapter.OnItemClickListener {

    private static final String TAG = "AutogenerationRuleFragment";

    private static final String POS_ID_EXTRA = "pos-id";

    private DictCore core;
    private ConjugationGenRule conjugationGenRule;

    private TextView ruleName;
    private TextView ruleRegex;
    private Button addTransformBtn;
    private RecyclerView transformsView;

    private ImageButton classesArrow;
    private FragmentContainerView classesFragment;
    private MaterialCardView infoCardView;

    private int posId;

    public static AutogenerationRuleFragment newInstance(Integer posId) {
        AutogenerationRuleFragment fragment = new AutogenerationRuleFragment();
        Bundle args = new Bundle();
        args.putInt(POS_ID_EXTRA, posId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            posId = getArguments().getInt(POS_ID_EXTRA);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_autogeneration_rules, container, false);
        core = ((PolyGlot)requireActivity().getApplicationContext()).getCore();

        classesFragment = root.findViewById(R.id.classesFragment);
        infoCardView = root.findViewById(R.id.infoCardView);
        addTransformBtn = root.findViewById(R.id.btnAddTransform);
        addTransformBtn.setOnClickListener(view -> addTransform());
        ruleName = root.findViewById(R.id.txtRuleName);
        ruleRegex = root.findViewById(R.id.txtRuleRegex);
        transformsView = root.findViewById(R.id.transformsList);
        transformsView.setLayoutManager(new LinearLayoutManager(getContext()));
        RecyclerView.ItemDecoration itemDecoration = new
                DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL);
        transformsView.addItemDecoration(itemDecoration);

        classesArrow = root.findViewById(R.id.classes_arrow_button);
        LinearLayout classesArrowLayout = root.findViewById(R.id.classes_arrow_layout);
        classesArrow.setOnClickListener(view -> handleClassesExpandable());
        classesArrowLayout.setOnClickListener(view -> handleClassesExpandable());

        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        MatchClassesFragment fragment = MatchClassesFragment.newInstance(posId, true);
        transaction.replace(R.id.classesFragment, fragment).commit();

        MatchClassesViewModel classesMatchViewModel = new ViewModelProvider(requireActivity()).get(MatchClassesViewModel.class);

        ((AndroidPropertiesManager)core.getPropertiesManager()).setConViewTypeface(ruleRegex);

        ruleName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (null == conjugationGenRule) return;
                conjugationGenRule.setName(s.toString());
            }
        });
        ruleRegex.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (null == conjugationGenRule) return;
                conjugationGenRule.setRegex(s.toString());
            }
        });

        AutogenRulesViewModel rulesViewModel = new ViewModelProvider(requireActivity()).get(AutogenRulesViewModel.class);

        rulesViewModel.getLiveData().observe(getViewLifecycleOwner(), conjugationGenRule -> {
            this.conjugationGenRule = conjugationGenRule;
            classesMatchViewModel.updateData(conjugationGenRule);
            if (null == conjugationGenRule) {
                ruleName.setText("");
                ruleRegex.setText("");
                updateIsEnabled(false);
                return;
            }
            ruleName.setText(conjugationGenRule.getName());
            ruleRegex.setText(conjugationGenRule.getRegex());
            updateIsEnabled(true);
        });
        return root;
    }

    private void handleClassesExpandable() {
        if (classesFragment.getVisibility() == View.VISIBLE) {
            TransitionManager.beginDelayedTransition(infoCardView,
                    new AutoTransition());
            classesFragment.setVisibility(View.GONE);
            classesArrow.setImageResource(R.drawable.ic_baseline_expand_more_24);
        }
        else {
            TransitionManager.beginDelayedTransition(infoCardView,
                    new AutoTransition());
            classesFragment.setVisibility(View.VISIBLE);
            classesArrow.setImageResource(R.drawable.ic_baseline_expand_less_24);
        }
    }

    private void updateIsEnabled(boolean isEnable) {
        this.ruleName.setEnabled(isEnable);
        this.ruleRegex.setEnabled(isEnable);
        this.addTransformBtn.setEnabled(isEnable);
        updateTransformsList();
    }

    private void updateTransformsList() {
        List<ConjugationGenTransform> transforms;
        if (null == conjugationGenRule) transforms = new ArrayList<>();
        else transforms = Arrays.asList(conjugationGenRule.getTransforms());

        RuleTransformRecyclerViewAdapter adapter = new RuleTransformRecyclerViewAdapter(core, transforms, this);
        transformsView.setAdapter(adapter);
    }

    public boolean isRuleValid() {
        if (null == this.conjugationGenRule) return true;
        StringBuilder sb = new StringBuilder();
        boolean isValid = true;
        try {
            Pattern.compile(this.ruleRegex.getText().toString());
        } catch (PatternSyntaxException e) {
            sb.append("Invalid filter regex: ");
            sb.append(e.getMessage());
            sb.append('\n');
            isValid = false;
        }
        for (ConjugationGenTransform transform :
                conjugationGenRule.getTransforms()) {
            try {
                Pattern.compile(transform.regex);
            } catch (PatternSyntaxException e) {
                sb.append("Invalid rule regex '");
                sb.append(transform.regex);
                sb.append("' -> '");
                sb.append(transform.replaceText);
                sb.append("': ");
                sb.append(e.getMessage());
                sb.append('\n');
                isValid = false;
            }
        }
        if (!isValid) {
            ((AndroidInfoBox)core.getOSHandler().getInfoBox()).error("Rule errors", sb.toString(), requireActivity());
        }
        return isValid;
    }

    private void addTransform() {
        conjugationGenRule.addTransform(new ConjugationGenTransform("", ""));
        updateTransformsList();
    }

    @Override
    public void onItemDeleteClick(ConjugationGenTransform item) {
        ((AndroidInfoBox)core.getOSHandler().getInfoBox()).yesNoCancel(
                "Are you sure?",
                "Do you want to delete this transform?\nThis action can't be undone.",
                requireActivity(),
                (dialog, which) -> {
                    if (which != DialogInterface.BUTTON_POSITIVE) {
                        return;
                    }
                    RuleTransformRecyclerViewAdapter adapter = (RuleTransformRecyclerViewAdapter) Objects.requireNonNull(transformsView.getAdapter());
                    List<ConjugationGenTransform> transforms = (adapter).getItems();
                    conjugationGenRule.wipeTransforms();
                    transforms.stream()
                            .filter(conjugationGenTransform -> conjugationGenTransform != item)
                            .forEach(conjugationGenTransform -> {
                                conjugationGenRule.addTransform(conjugationGenTransform);
                            });
                    updateTransformsList();
                }
        );

    }
}