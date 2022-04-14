package org.darisadesigns.polyglotlina.android.ui.PartsOfSpeech.Conjugations;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.FragmentContainerView;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import org.darisadesigns.polyglotlina.DictCore;
import org.darisadesigns.polyglotlina.Nodes.ConjugationGenRule;
import org.darisadesigns.polyglotlina.Nodes.ConjugationPair;
import org.darisadesigns.polyglotlina.Nodes.TypeNode;
import org.darisadesigns.polyglotlina.android.AndroidInfoBox;
import org.darisadesigns.polyglotlina.android.PolyGlot;
import org.darisadesigns.polyglotlina.android.R;

import java.util.Arrays;
import java.util.List;

public class AutogenerationActivity extends AppCompatActivity implements ConjugationRuleRecyclerViewAdapter.OnItemClickListener {

    private static final String TAG = "AutogenerationActivity";
    public static final String POS_ID_EXTRA = "part-of-speech-id";

    private RecyclerView rulesView;

    private DictCore core;
    private TypeNode posNode;
    private ConjugationPair conjugationPair;

    private AutogenRulesViewModel rulesViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_autogeneration);

        setSupportActionBar((Toolbar) findViewById(R.id.app_bar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        Intent intent = getIntent();
        PolyGlot polyGlot = (PolyGlot)getApplicationContext();
        core = polyGlot.getCore();
        int posId = intent.getIntExtra(POS_ID_EXTRA, -1);
        posNode = core.getTypes().getNodeById(posId);
        rulesViewModel = new ViewModelProvider(this).get(AutogenRulesViewModel.class);
        rulesViewModel.updateData(null);

        getSupportActionBar().setTitle(posNode.getValue());

        ConjugationGenRule[] deprecatedRules = core.getConjugationManager().getAllDepGenerationRules(posNode.getId());
        ConjugationPair[] conjugationPairs = core.getConjugationManager().getAllCombinedIds(posNode.getId());

        rulesView = findViewById(R.id.rulesList);
        rulesView.setLayoutManager(new LinearLayoutManager(this));
        RecyclerView.ItemDecoration itemDecoration = new
                DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        rulesView.addItemDecoration(itemDecoration);

        Button addRuleBtn = findViewById(R.id.btnAddRule);
        addRuleBtn.setOnClickListener(view -> addRule());

        CheckBox chkDisableForm = findViewById(R.id.chkDisableForm);
        chkDisableForm.setOnCheckedChangeListener((buttonView, isChecked) -> {
            core.getConjugationManager().setCombinedConjSuppressed(conjugationPair.combinedId, posNode.getId(), isChecked);
        });

        AutoCompleteTextView autogenConjugations = findViewById(R.id.autogenConjugations);
        ArrayAdapter<ConjugationPair> spinnerArrayAdapter = new ArrayAdapter<>
                (this, R.layout.list_item, conjugationPairs);
        autogenConjugations.setAdapter(spinnerArrayAdapter);
        conjugationPair = conjugationPairs[0];
        autogenConjugations.setText(conjugationPair.toString(), false);
        autogenConjugations.setOnItemClickListener((parent, view, position, id) -> {
            conjugationPair = spinnerArrayAdapter.getItem(position);
            chkDisableForm.setChecked(
                    core.getConjugationManager().isCombinedConjlSurpressed(
                            conjugationPair.combinedId,
                            posNode.getId()
                    )
            );
            updateRulesList();
        });
        updateRulesList();

        CoordinatorLayout coordinatorLayout = findViewById(R.id.coordinatorLayout);
        ScrollView scrollView = findViewById(R.id.scrollView);
        FragmentContainerView ruleContainerView = findViewById(R.id.rule_info_fragment_container_view);
        AutogenerationRuleFragment ruleFragment = (AutogenerationRuleFragment) getSupportFragmentManager().findFragmentById(R.id.rule_info_fragment_container_view);
        AppBarLayout appBarLayout = findViewById(R.id.appBarLayout);
        TextView sheetPeek = findViewById(R.id.sheetPeek);
        LinearLayout bottomSheetLayout = findViewById(R.id.bottomSheet);
        BottomSheetBehavior<LinearLayout> bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetLayout);
        sheetPeek.setOnClickListener((view) -> {
            switch (bottomSheetBehavior.getState()) {
                case BottomSheetBehavior.STATE_COLLAPSED:
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    break;
                case BottomSheetBehavior.STATE_EXPANDED:
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    break;
            }
        });
        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (BottomSheetBehavior.STATE_COLLAPSED != newState) return;
                if (ruleFragment == null || !ruleFragment.isRuleValid()) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    return;
                }
                updateRulesList();
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
        var ref = new Object() {
            boolean firstEvent = true;
        };
        coordinatorLayout.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            if (!ref.firstEvent) return;
            /* Hack to fix some heights of elements to use most of the screen */
            LayoutParams prevLayoutParams = bottomSheetLayout.getLayoutParams();
            prevLayoutParams.height = bottomSheetLayout.getHeight() - appBarLayout.getHeight();
            bottomSheetLayout.setLayoutParams(prevLayoutParams);
            prevLayoutParams = scrollView.getLayoutParams();
            prevLayoutParams.height = coordinatorLayout.getHeight() - appBarLayout.getHeight() - bottomSheetBehavior.getPeekHeight();
            scrollView.setLayoutParams(prevLayoutParams);
            prevLayoutParams = ruleContainerView.getLayoutParams();
            prevLayoutParams.height = coordinatorLayout.getHeight() - appBarLayout.getHeight();
            ruleContainerView.setLayoutParams(prevLayoutParams);
            /* The code above fires this event again so it causes infinite calls */
            ref.firstEvent = false;
        });
    }

    @Override
    public void onItemClick(ConjugationGenRule item) {
        rulesViewModel.updateData(item);
    }

    @Override
    public void onItemDeleteClick(ConjugationGenRule item) {
        ((AndroidInfoBox)core.getOSHandler().getInfoBox()).yesNoCancel(
                "Are you sure?",
                "Do you want to delete this rule?\nThis action can't be undone.",
                this,
                (dialog, which) -> {
                    if (which != DialogInterface.BUTTON_POSITIVE) {
                        return;
                    }
                    core.getConjugationManager().deleteConjugationGenRule(item);
                    rulesViewModel.updateData(null);
                    updateRulesList();
                }
        );
    }

    private void addRule() {
        ConjugationGenRule newRule = new ConjugationGenRule(posNode.getId(), conjugationPair.combinedId);
        newRule.setRegex(".*");
        core.getConjugationManager().addConjugationGenRule(newRule);
        rulesViewModel.updateData(newRule);
        updateRulesList();
    }

    private void updateRulesList() {
        int prevSelectedPos = RecyclerView.NO_POSITION;
        if (rulesView.getAdapter() != null) {
            prevSelectedPos = ((ConjugationRuleRecyclerViewAdapter)rulesView.getAdapter()).getSelectedPos();
        }
        ConjugationGenRule[] ruleList = core.getConjugationManager()
                .getConjugationRulesForTypeAndCombId(posNode.getId(), conjugationPair.combinedId);
        List<ConjugationGenRule> rules = Arrays.asList(ruleList);
        ConjugationRuleRecyclerViewAdapter adapter = new ConjugationRuleRecyclerViewAdapter(core, rules, this);
        adapter.setSelectedPos(prevSelectedPos);
        rulesView.setAdapter(adapter);
    }
}