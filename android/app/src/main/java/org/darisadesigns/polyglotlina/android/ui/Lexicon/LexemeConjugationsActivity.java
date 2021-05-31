package org.darisadesigns.polyglotlina.android.ui.Lexicon;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.android.material.textview.MaterialTextView;

import org.darisadesigns.polyglotlina.DictCore;
import org.darisadesigns.polyglotlina.ManagersCollections.ConjugationManager;
import org.darisadesigns.polyglotlina.Nodes.ConWord;
import org.darisadesigns.polyglotlina.Nodes.ConjugationNode;
import org.darisadesigns.polyglotlina.Nodes.ConjugationPair;
import org.darisadesigns.polyglotlina.android.PolyGlot;
import org.darisadesigns.polyglotlina.android.R;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ir.androidexception.datatable.DataTable;
import ir.androidexception.datatable.model.DataTableHeader;
import ir.androidexception.datatable.model.DataTableRow;

public class LexemeConjugationsActivity extends AppCompatActivity {

    private static final String TAG = "LexemeConjugations";
    public static final String CON_WORD_ID_EXTRA = "con-word-id";

    private LexemeInfoViewModel viewModel;
    private DictCore core;
    private ConWord conWord;
    private ConjugationManager conjugationManager;

    private boolean canConjugate;
    private ArrayAdapter<ConjugationNode> conjugationsArrayAdapter;
    private final List<String> conjugationIdsList = new ArrayList<>();
    private final Map<String, String> labelMap = new HashMap<>();

    private View noConjugationsLayout;
    private View conjugationsLayout;
    private LinearLayout conjugationsTablesLayout;
    private MaterialTextView noConjugationMessage;
    private TabLayout conjugationsTabs;
    private ViewPager viewPager;

    private View spinnersLayout;
    private AutoCompleteTextView rowsAutocomplete;
    private AutoCompleteTextView columnsAutocomplete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lexeme_conjugations);

        setSupportActionBar((Toolbar) findViewById(R.id.app_bar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        Intent intent = getIntent();
        PolyGlot polyGlot = (PolyGlot)getApplicationContext();
        core = polyGlot.getCore();
        int wordId = intent.getIntExtra(CON_WORD_ID_EXTRA, -1);
        conWord = core.getWordCollection().getNodeById(wordId);
        getSupportActionBar().setTitle(conWord.getValue());

        noConjugationsLayout = findViewById(R.id.noConjugationsLayout);
        noConjugationMessage = findViewById(R.id.error_message);
        conjugationsLayout = findViewById(R.id.conjugationsLayout);
        conjugationsTablesLayout = findViewById(R.id.conjugationsTablesLayout);
        /*conjugationsTabs = root.findViewById(R.id.conjugationsTabs);*/

        spinnersLayout = findViewById(R.id.spinnersLayout);
        rowsAutocomplete = findViewById(R.id.rowsAutocomplete);
        columnsAutocomplete = findViewById(R.id.columnsAutocomplete);

        AdapterView.OnItemSelectedListener spinnersListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.e(TAG, "item selected");
                populateConjugationsTable();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.e(TAG, "Nothing selected");
            }
        };

        rowsAutocomplete.setOnItemSelectedListener(spinnersListener);
        columnsAutocomplete.setOnItemSelectedListener(spinnersListener);

        setupInitialScreen();
    }

    private void setupInitialScreen() {
        int typeId = conWord.getWordTypeId();
        conjugationManager = core.getConjugationManager();
        if (typeId == 0) {
            noConjugationMessage.setText(R.string.message_no_word_type);
        }
        else if((conjugationManager.getDimensionalConjugationListTemplate(typeId) == null ||
                conjugationManager.getDimensionalConjugationListTemplate(typeId).length == 0) &&
                conjugationManager.getDimensionalConjugationListWord(conWord.getId()).length == 0 &&
                conjugationManager.getSingletonCombinedIds(typeId).length == 0
        ) {
            noConjugationMessage.setText(getString(R.string.message_no_conjugations, conWord.getWordTypeDisplay()));
        }
        else {
            noConjugationsLayout.setVisibility(View.GONE);
            conjugationsLayout.setVisibility(View.VISIBLE);

            setupSpinners();
            populateConjugationIdToValues();
            populateConjugationsTable();
        }
    }

    private void setupSpinners() {
        ConjugationNode[] conjugationNodes = conjugationManager.getDimensionalConjugationListTemplate(conWord.getWordTypeId());
        conjugationsArrayAdapter = new ArrayAdapter<>
                (this, R.layout.list_item, conjugationNodes);
        rowsAutocomplete.setAdapter(conjugationsArrayAdapter);
        columnsAutocomplete.setAdapter(conjugationsArrayAdapter);

        rowsAutocomplete.setText(conjugationsArrayAdapter.getItem(0).toString(), false);

        if (conjugationNodes.length > 1) {
            columnsAutocomplete.setText(conjugationsArrayAdapter.getItem(1).toString(), false);
        }
    }

    private void populateConjugationIdToValues() {
        ConjugationPair[] conjugationTemplateList = conjugationManager.getDimensionalCombinedIds(conWord.getWordTypeId());

        for (ConjugationPair curPair : conjugationTemplateList) {
            // skip forms that have been suppressed
            if (conjugationManager.isCombinedConjlSurpressed(curPair.combinedId, conWord.getWordTypeId())) {
                continue;
            }

            conjugationIdsList.add(curPair.combinedId);
            labelMap.put(curPair.combinedId, curPair.label);
        }
    }

    private void populateConjugationsTable() {

        if(shouldRenderDimensional()) {
            populateMultiConjugationTables();
        }
        else {
            populateSingleConjugationTable();
        }
    }

    private void populateMultiConjugationTables() {
        int row = rowsAutocomplete.getListSelection();
        int col = columnsAutocomplete.getListSelection();
        ConjugationNode conjugationRows;
        ConjugationNode conjugationsColumns;
        if(row == -1 || col == -1) {
            conjugationRows = conjugationsArrayAdapter.getItem(0);
            conjugationsColumns = conjugationsArrayAdapter.getItem(1);
        } else {
            conjugationRows = conjugationsArrayAdapter.getItem(row);
            conjugationsColumns = conjugationsArrayAdapter.getItem(col);
        }
        if (conjugationRows.equals(conjugationsColumns)) {
            //TODO: show "Please select different values"
            return;
        }
        ArrayList<LexemeConjugationTabFragment> fragmentArrayList = new ArrayList<>();
        getPanelPartialConjugationIds(conjugationRows, conjugationsColumns).forEach((partialConjugation) -> {
            Log.e(TAG, "Partial: " + partialConjugation);
            LexemeConjugationTabFragment fragment = LexemeConjugationTabFragment.newInstance(partialConjugation);
            ConjugationViewModel viewModel = new ViewModelProvider(LexemeConjugationsActivity.this)
                    .get(partialConjugation, ConjugationViewModel.class);
            viewModel.updateConjugation(new ConjugationViewModel.Conjugation(
                    conWord,
                    conjugationRows,
                    conjugationsColumns
            ));
            fragmentArrayList.add(fragment);
        });
        spinnersLayout.setVisibility(View.VISIBLE);
        conjugationsTablesLayout.setVisibility(View.GONE);
        LexemeConjugationsPagerAdapter lexemeInfoPagerAdapter = new LexemeConjugationsPagerAdapter(this, getLifecycle(), getSupportFragmentManager(), fragmentArrayList);
        ViewPager2 viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(lexemeInfoPagerAdapter);
        viewPager.setVisibility(View.VISIBLE);
        TabLayout tabs = findViewById(R.id.tabs);
        new TabLayoutMediator(tabs, viewPager, lexemeInfoPagerAdapter).attach();
        // tabs.setupWithViewPager(viewPager);
        tabs.setVisibility(View.VISIBLE);
    }

    private void populateSingleConjugationTable() {
        spinnersLayout.setVisibility(View.GONE);

//        ConjugationPair[] completeList = conjugationManager.getAllCombinedIds(conWord.getWordTypeId());
        String key = "single";
        LexemeConjugationTabFragment fragment = LexemeConjugationTabFragment.newInstance(key);
        ConjugationViewModel viewModel = new ViewModelProvider(LexemeConjugationsActivity.this)
                .get(key, ConjugationViewModel.class);
        viewModel.updateConjugation(new ConjugationViewModel.Conjugation(conWord));
        conjugationsTablesLayout.setVisibility(View.GONE);
        ArrayList<LexemeConjugationTabFragment> fragmentArrayList = new ArrayList<>();
        fragmentArrayList.add(fragment);
        LexemeConjugationsPagerAdapter lexemeInfoPagerAdapter = new LexemeConjugationsPagerAdapter(this, getLifecycle(), getSupportFragmentManager(), fragmentArrayList);
        ViewPager2 viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(lexemeInfoPagerAdapter);
        viewPager.setVisibility(View.VISIBLE);
        TabLayout tabs = findViewById(R.id.tabs);
        new TabLayoutMediator(tabs, viewPager, lexemeInfoPagerAdapter).attach();
        tabs.setVisibility(View.VISIBLE);
    }

    private boolean shouldRenderDimensional() {
        return conjugationManager.getDimensionalConjugationListTemplate(conWord.getWordTypeId()).length > 1;
    }

    private List<String> getPanelPartialConjugationIds(ConjugationNode rows, ConjugationNode columns) {
        List<String> partialIds = new ArrayList<>();
        int columnsNode = conjugationManager.getDimensionTemplateIndex(conWord.getWordTypeId(), rows);
        int rowsNode = conjugationManager.getDimensionTemplateIndex(conWord.getWordTypeId(), columns);

        for (String dimId : conjugationIdsList) {
            if (dimId == null) {
                continue;
            }

            String partialConjugationId = replaceDimensionByIndex(dimId, columnsNode, "X");
            partialConjugationId = replaceDimensionByIndex(partialConjugationId, rowsNode, "Y");

            if (!partialIds.contains(partialConjugationId)) {
                partialIds.add(partialConjugationId);
            }
        }

        return partialIds;
    }

    private String replaceDimensionByIndex(String dimensions, int index, String replacement) {
        String[] dimArray = dimensions.split(",");
        dimArray = Arrays.copyOfRange(dimArray, 1, dimArray.length); // first value always empty
        String ret = ",";

        // rebuild dimensionID, replacing the index values as appropriate
        for (int i = 0; i < dimArray.length; i++) {
            ret += (i == index ? replacement : dimArray[i]) + ",";
        }

        return ret;
    }
}