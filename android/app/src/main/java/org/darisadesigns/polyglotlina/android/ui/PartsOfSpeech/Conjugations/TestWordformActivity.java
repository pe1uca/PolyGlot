package org.darisadesigns.polyglotlina.android.ui.PartsOfSpeech.Conjugations;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.darisadesigns.polyglotlina.DictCore;
import org.darisadesigns.polyglotlina.Nodes.ConWord;
import org.darisadesigns.polyglotlina.Nodes.ConjugationGenRule;
import org.darisadesigns.polyglotlina.Nodes.ConjugationPair;
import org.darisadesigns.polyglotlina.android.AndroidInfoBox;
import org.darisadesigns.polyglotlina.android.AndroidPropertiesManager;
import org.darisadesigns.polyglotlina.android.PolyGlot;
import org.darisadesigns.polyglotlina.android.R;

public class TestWordformActivity extends AppCompatActivity {

    public static final String POS_ID_EXTRA = "posId";
    public static final String WORDFORM_LABEL_EXTRA = "label";
    public static final String WORDFORM_ID_EXTRA = "combinedId";

    private DictCore core;

    private String combinedId = "";
    private int posId = -1;
    private ConjugationGenRule ruleClasses = new ConjugationGenRule();

    private TextView txtWord;
    private TextView txtGenerated;
    private TextView txtDebug;

    public static Intent getIntent(Context context, int posId, ConjugationPair conjugationPair) {
        Intent intent = new Intent(context, TestWordformActivity.class);
        intent.putExtra(POS_ID_EXTRA, posId);
        intent.putExtra(WORDFORM_LABEL_EXTRA, conjugationPair.label);
        intent.putExtra(WORDFORM_ID_EXTRA, conjugationPair.combinedId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_wordform);

        setSupportActionBar((Toolbar) findViewById(R.id.app_bar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        PolyGlot polyGlot = (PolyGlot)getApplicationContext();
        core = polyGlot.getCore();

        Intent intent = getIntent();
        combinedId = intent.getStringExtra(WORDFORM_ID_EXTRA);
        posId = intent.getIntExtra(POS_ID_EXTRA, -1);
        String conjugationLabel = intent.getStringExtra(WORDFORM_LABEL_EXTRA);

        getSupportActionBar().setTitle(conjugationLabel);
        ruleClasses.setTypeId(posId);

        this.txtWord = findViewById(R.id.txtTestWord);
        this.txtGenerated = findViewById(R.id.txtGenerated);
        this.txtDebug = findViewById(R.id.txtDebug);
        AndroidPropertiesManager manager = ((AndroidPropertiesManager)core.getPropertiesManager());
        manager.setConViewTypeface(this.txtWord);
        manager.setConViewTypeface(this.txtGenerated);
        Button btnTest = findViewById(R.id.btnRunTest);
        btnTest.setOnClickListener(v -> {
            testWord();
        });
    }

    private void testWord() {
        if (null == combinedId) {
            ((AndroidInfoBox)core.getOSHandler().getInfoBox()).error(
                    "No declension selected",
                    "There was an error setting the declension for the test word",
                    this
            );
            return;
        }

        ConWord testWord = new ConWord();
        testWord.setValue(txtWord.getText().toString());
        testWord.setWordTypeId(posId);
        testWord.setCore(core);
        // TODO: Set class of the test word

        String generatedWord = "";
        try {
            generatedWord = core.getConjugationManager().declineWord(testWord, combinedId);
        } catch (Exception e) {
            ((AndroidInfoBox)core.getOSHandler().getInfoBox()).error(
                    "Declension Test Error",
                    e.getLocalizedMessage(),
                    this
            );
        }
        txtGenerated.setText(generatedWord);

        StringBuilder sb = new StringBuilder();
        for (String line : core.getConjugationManager().getDecGenDebug()) {
            sb.append(line);
            sb.append("\n");
        }
        txtDebug.setText(sb.toString());
    }
}