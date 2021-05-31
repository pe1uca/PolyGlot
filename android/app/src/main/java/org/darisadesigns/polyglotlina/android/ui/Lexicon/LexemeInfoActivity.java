package org.darisadesigns.polyglotlina.android.ui.Lexicon;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.tabs.TabLayout;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentContainerView;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.darisadesigns.polyglotlina.DictCore;
import org.darisadesigns.polyglotlina.Nodes.ConWord;
import org.darisadesigns.polyglotlina.android.MainActivity;
import org.darisadesigns.polyglotlina.android.PolyGlot;
import org.darisadesigns.polyglotlina.android.R;
import org.darisadesigns.polyglotlina.android.ui.PViewModel;

public class LexemeInfoActivity extends AppCompatActivity {

    private static final String TAG = "LexemeInfoActivity";
    public static final String CON_WORD_ID_EXTRA = "con-word-id";

    private ConWord conWord;
    private DictCore core;

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
        getSupportActionBar().setTitle(conWord.getValue());

        LexemeInfoViewModel viewModel= new ViewModelProvider(this).get(LexemeInfoViewModel.class);
        viewModel.updateWord(conWord);

        TextView sheetPeek = findViewById(R.id.sheetPeek);
        sheetPeek.setOnClickListener((view) -> {
            LinearLayout bottomSheet = findViewById(R.id.bottomSheet);
            BottomSheetBehavior<LinearLayout> bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        });

        Button btnConjugations = findViewById(R.id.btnConjugations);
        btnConjugations.setOnClickListener((view) -> {
            Intent conjugationsIntent = new Intent(LexemeInfoActivity.this, LexemeConjugationsActivity.class);
            conjugationsIntent.putExtra(LexemeConjugationsActivity.CON_WORD_ID_EXTRA, wordId);
            startActivity(conjugationsIntent);
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
            default:
                return super.onOptionsItemSelected(item);

        }
    }
}