package org.darisadesigns.polyglotlina.android.ui.PartsOfSpeech.Conjugations;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.darisadesigns.polyglotlina.DictCore;
import org.darisadesigns.polyglotlina.Nodes.ConjugationGenRule;
import org.darisadesigns.polyglotlina.Nodes.ConjugationNode;
import org.darisadesigns.polyglotlina.Nodes.ConjugationPair;
import org.darisadesigns.polyglotlina.Nodes.TypeNode;
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
        AutogenRulesViewModel rulesViewModel = new ViewModelProvider(this).get(AutogenRulesViewModel.class);
        rulesViewModel.setPosNode(posNode);

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

        AutoCompleteTextView autogenConjugations = findViewById(R.id.autogenConjugations);
        ArrayAdapter<ConjugationPair> spinnerArrayAdapter = new ArrayAdapter<>
                (this, R.layout.list_item, conjugationPairs);
        autogenConjugations.setAdapter(spinnerArrayAdapter);
        conjugationPair = conjugationPairs[0];
        autogenConjugations.setText(conjugationPair.toString(), false);
        rulesViewModel.updateData(conjugationPair);
        autogenConjugations.setOnItemClickListener((parent, view, position, id) -> {
            conjugationPair = spinnerArrayAdapter.getItem(position);
            updateRulesList();
            // rulesViewModel.updateData(conjugationPair);
        });
        updateRulesList();
    }

    @Override
    public void onItemClick(ConjugationGenRule item) {

    }

    @Override
    public void onItemDeleteClick(ConjugationGenRule item) {

    }

    private void addRule() {

    }

    private void updateRulesList() {
        ConjugationGenRule[] ruleList = core.getConjugationManager()
                .getConjugationRulesForTypeAndCombId(posNode.getId(), conjugationPair.combinedId);
        List<ConjugationGenRule> rules = Arrays.asList(ruleList);
        ConjugationRuleRecyclerViewAdapter adapter = new ConjugationRuleRecyclerViewAdapter(core, rules, this);
        rulesView.setAdapter(adapter);
        for (var tmp: ruleList) {
            Log.e(TAG, "updateRulesList: " + tmp.getName());
        }
    }
}