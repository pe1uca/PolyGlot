package org.darisadesigns.polyglotlina.android.ui.Lexicon;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;

import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;

import org.darisadesigns.polyglotlina.DictCore;
import org.darisadesigns.polyglotlina.Nodes.ConWord;
import org.darisadesigns.polyglotlina.android.PolyGlot;
import org.darisadesigns.polyglotlina.android.R;
import org.darisadesigns.polyglotlina.android.ui.PViewModel;

public class LexemeInfoActivity extends AppCompatActivity {

    private static final String TAG = "LexemeInfoActivity";
    public static final String CON_WORD_ID_EXTRA = "con-word-id";

    private ConWord conWord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lexeme_info);
        LexemeInfoPagerAdapter lexemeInfoPagerAdapter = new LexemeInfoPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(lexemeInfoPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        setSupportActionBar((Toolbar) findViewById(R.id.app_bar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        Intent intent = getIntent();
        PolyGlot polyGlot = (PolyGlot)getApplicationContext();
        DictCore core = polyGlot.getCore();
        int wordId = intent.getIntExtra(CON_WORD_ID_EXTRA, -1);
        conWord = core.getWordCollection().getNodeById(wordId);
        getSupportActionBar().setTitle(conWord.getValue());

        LexemeInfoViewModel viewModel= new ViewModelProvider(this).get(LexemeInfoViewModel.class);
        viewModel.updateWord(conWord);

        /*FloatingActionButton fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
    }
}