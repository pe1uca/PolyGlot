package org.darisadesigns.polyglotlina.android.ui.Lexicon.Conjugations;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;

import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.Toolbar;

import org.darisadesigns.polyglotlina.DictCore;
import org.darisadesigns.polyglotlina.ManagersCollections.ConjugationManager;
import org.darisadesigns.polyglotlina.Nodes.ConWord;
import org.darisadesigns.polyglotlina.Nodes.ConjugationDimension;
import org.darisadesigns.polyglotlina.Nodes.ConjugationNode;
import org.darisadesigns.polyglotlina.android.AndroidPropertiesManager;
import org.darisadesigns.polyglotlina.android.PolyGlot;
import org.darisadesigns.polyglotlina.android.R;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class LexemeConjugationsSpinnersActivity extends AppCompatActivity {

    private static final String TAG = "LexemeConjugations";
    public static final String CON_WORD_ID_EXTRA = "con-word-id";

    private DictCore core;
    private ConWord conWord;
    private ConjugationManager conjugationManager;

    private MaterialTextView wordFormView;
    private LinearLayout dimensionsLayout;
    private final LinkedHashMap<String, Integer> dimensionsSelectedMap = new LinkedHashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lexeme_conjugations_spinners);

        setSupportActionBar((Toolbar) findViewById(R.id.app_bar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        Intent intent = getIntent();
        PolyGlot polyGlot = (PolyGlot)getApplicationContext();
        core = polyGlot.getCore();
        int wordId = intent.getIntExtra(CON_WORD_ID_EXTRA, -1);
        conWord = core.getWordCollection().getNodeById(wordId);
        AndroidPropertiesManager propertiesManager = ((AndroidPropertiesManager)core.getPropertiesManager());
        propertiesManager.setConActionBarTitle(getSupportActionBar(), conWord.toString());

        dimensionsLayout = findViewById(R.id.dimensionsLayout);

        wordFormView = findViewById(R.id.txtConWord);
        propertiesManager.setConViewTypeface(wordFormView);

        setupSpinners();
        conjugateWord();
    }

    private void setupSpinners() {
        ConjugationNode[] nodes = core.getConjugationManager().getDimensionalConjugationListTemplate(conWord.getWordTypeId());
        for (ConjugationNode dimension :
                nodes) {
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            TextInputLayout inputLayout = new TextInputLayout(new ContextThemeWrapper(this, R.style.Widget_MaterialComponents_TextInputLayout_FilledBox_ExposedDropdownMenu));
            int marginBottom = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics());
            layoutParams.setMargins(4, 4, 4, marginBottom);
            inputLayout.setLayoutParams(layoutParams);

            AutoCompleteTextView autocomplete = new AutoCompleteTextView(inputLayout.getContext());
            autocomplete.setInputType(EditorInfo.TYPE_NULL);

            inputLayout.addView(autocomplete, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            inputLayout.setHint(dimension.getValue());
            dimensionsLayout.addView(inputLayout);
            List<Dimension> dimensions = new ArrayList<>();
            for (ConjugationDimension conjugationDimension : dimension.getDimensions()) {
                dimensions.add(new Dimension(conjugationDimension.getValue(), conjugationDimension.getId()));
            }
            ArrayAdapter<Dimension> dimensionAdapter = new ArrayAdapter<>(this, R.layout.list_item, dimensions);
            autocomplete.setAdapter(dimensionAdapter);

            autocomplete.setOnItemClickListener((parent, view, position, id) -> {
                dimensionsSelectedMap.put(dimension.getValue(), dimensionAdapter.getItem(position).getId());
                conjugateWord();
            });
            autocomplete.setText(dimensionAdapter.getItem(0).toString(), false);
            dimensionsSelectedMap.put(dimension.getValue(), dimensionAdapter.getItem(0).getId());
        }
    }

    private void conjugateWord() {
        StringBuilder combinedId = new StringBuilder(",");

        for (Integer id : dimensionsSelectedMap.values()) {
            combinedId.append(id).append(",");
        }

        try {
            String wordForm = core.getConjugationManager().declineWord(conWord, combinedId.toString());
            wordFormView.setText(wordForm);
        } catch (Exception e) {
            core.getOSHandler().getInfoBox().error("Composition error", "Unable to compose word form.\n" + e.getMessage());
            core.getOSHandler().getIOHandler().writeErrorLog(e);
        }
    }

    private static class Dimension {
        private final String label;
        private final int id;


        private Dimension(String label, int id) {
            this.label = label;
            this.id = id;
        }

        public int getId() {
            return this.id;
        }

        @Override
        public String toString() {
            return label;
        }
    }
}