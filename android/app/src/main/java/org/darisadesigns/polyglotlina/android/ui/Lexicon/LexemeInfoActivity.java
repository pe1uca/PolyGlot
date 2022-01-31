package org.darisadesigns.polyglotlina.android.ui.Lexicon;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

import androidx.lifecycle.ViewModelProvider;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.darisadesigns.polyglotlina.DictCore;
import org.darisadesigns.polyglotlina.Nodes.ConWord;
import org.darisadesigns.polyglotlina.android.AndroidPropertiesManager;
import org.darisadesigns.polyglotlina.android.PolyGlot;
import org.darisadesigns.polyglotlina.android.R;
import org.darisadesigns.polyglotlina.android.ui.Lexicon.Conjugations.LexemeConjugationsActivity;
import org.darisadesigns.polyglotlina.android.ui.Lexicon.Etymology.LexemeEtymologyActivity;

public class LexemeInfoActivity extends AppCompatActivity {

    private static final String TAG = "LexemeInfoActivity";
    public static final String CON_WORD_ID_EXTRA = "con-word-id";

    private ConWord conWord;
    private DictCore core;

    private boolean errorsWarned = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lexeme_info);

        setSupportActionBar((Toolbar) findViewById(R.id.app_bar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        Intent intent = getIntent();
        PolyGlot polyGlot = (PolyGlot)getApplicationContext();
        core = polyGlot.getCore();
        int wordId = intent.getIntExtra(CON_WORD_ID_EXTRA, -1);
        conWord = core.getWordCollection().getNodeById(wordId);
        ((AndroidPropertiesManager)core.getPropertiesManager()).setConActionBarTitle(getSupportActionBar(), conWord.toString());

        LexemeInfoViewModel viewModel= new ViewModelProvider(this).get(LexemeInfoViewModel.class);
        viewModel.updateWord(conWord);

        TextView sheetPeek = findViewById(R.id.sheetPeek);
        sheetPeek.setOnClickListener((view) -> {
            LinearLayout bottomSheet = findViewById(R.id.bottomSheet);
            BottomSheetBehavior<LinearLayout> bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
            switch (bottomSheetBehavior.getState()) {
                case BottomSheetBehavior.STATE_COLLAPSED:
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    break;
                case BottomSheetBehavior.STATE_EXPANDED:
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    break;
            }
        });

        Button btnConjugations = findViewById(R.id.btnConjugations);
        btnConjugations.setOnClickListener((view) -> {
            Intent conjugationsIntent = new Intent(LexemeInfoActivity.this, LexemeConjugationsActivity.class);
            conjugationsIntent.putExtra(LexemeConjugationsActivity.CON_WORD_ID_EXTRA, wordId);
            startActivity(conjugationsIntent);
        });

        Button btnEtymology = findViewById(R.id.btnEtymology);
        btnEtymology.setOnClickListener((view) -> {
            Intent etymologyIntent = new Intent(LexemeInfoActivity.this, LexemeEtymologyActivity.class);
            etymologyIntent.putExtra(LexemeEtymologyActivity.CON_WORD_ID_EXTRA, wordId);
            startActivity(etymologyIntent);
        });

        /*FloatingActionButton fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.lexeme_info, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                try {
                    core.getWordCollection().deleteNodeById(conWord.getId());
                    finish();
                } catch (Exception e) {
                    core.getOSHandler().getIOHandler().writeErrorLog(e);
                    core.getOSHandler().getInfoBox().error(
                            "Deletion Error",
                            "Unable to delete word: " + e.getLocalizedMessage()
                    );
                }
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "Back pressed invoked");
        LexemeGeneralFragment fragment = (LexemeGeneralFragment)
               getSupportFragmentManager()
                       .findFragmentById(R.id.lexeme_general_fragment_container_view);

        boolean isValid = fragment.isLexemeValid();
        if (!isValid) {
            if (errorsWarned) {
                try {
                    core.getWordCollection().deleteNodeById(conWord.getId());
                } catch (Exception e) {
                    core.getOSHandler().getIOHandler().writeErrorLog(e);
                    core.getOSHandler().getInfoBox().error(
                            "Deletion Error",
                            "Unable to delete word: " + e.getLocalizedMessage()
                    );
                }
            }
            else {
                errorsWarned = true;
                CharSequence text = getResources().getString(R.string.toast_lexeme_errors);
                Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
                /* Make the deletion reset when the toast disappears */
                new Handler().postDelayed(() -> errorsWarned = false, 2000);
                return;
            }
        }
        super.onBackPressed();
    }
}