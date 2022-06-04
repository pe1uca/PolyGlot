package org.darisadesigns.polyglotlina.android.ui.WordClasses;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.transition.AutoTransition;
import androidx.transition.TransitionManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;

import org.darisadesigns.polyglotlina.DictCore;
import org.darisadesigns.polyglotlina.Nodes.TypeNode;
import org.darisadesigns.polyglotlina.Nodes.WordClass;
import org.darisadesigns.polyglotlina.android.PolyGlot;
import org.darisadesigns.polyglotlina.android.R;

public class WordClassInfoActivity extends AppCompatActivity {

    private static final String TAG = "WordClassInfoActivity";
    public static final String WORD_CLASS_ID_EXTRA = "word-class-id";

    private DictCore core;
    private WordClass wordClass;

    private TextInputEditText txtWordClass;

    private CheckBox chkFreeText;
    private CheckBox chkAssociative;
    private CheckBox chkAllPos;

    private ImageButton arrow;
    private LinearLayout posLayout;
    private MaterialCardView infoCardView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_class_info);

        setSupportActionBar((Toolbar) findViewById(R.id.app_bar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        PolyGlot polyGlot = (PolyGlot)getApplicationContext();
        core = polyGlot.getCore();
        Intent intent = getIntent();
        int nounClassId = intent.getIntExtra(WORD_CLASS_ID_EXTRA, -1);
        wordClass = core.getWordClassCollection().getNodeById(nounClassId);
        getSupportActionBar().setTitle(wordClass.getValue());

        txtWordClass = findViewById(R.id.txtWordClass);
        chkFreeText = findViewById(R.id.chkFreeText);
        chkAssociative = findViewById(R.id.chkAssociative);

        txtWordClass.setText(wordClass.getValue());
        chkFreeText.setChecked(wordClass.isFreeText());
        chkAssociative.setChecked(wordClass.isAssociative());

        chkAllPos = findViewById(R.id.chkAllPos);
        posLayout = findViewById(R.id.posLayout);
        infoCardView = findViewById(R.id.infoCardView);
        arrow = findViewById(R.id.arrow_button);
        LinearLayout arrowLayout = findViewById(R.id.arrow_layout);
        arrow.setOnClickListener(view -> handleExpandable());
        arrowLayout.setOnClickListener(view -> handleExpandable());

        chkAllPos.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                wordClass.addApplyType(-1);
            }
            else {
                wordClass.deleteApplyType(-1);
            }
            for (int i = 0; i < posLayout.getChildCount(); i++) {
                posLayout.getChildAt(i).setEnabled(!b);
            }
        });

        TypeNode[] posList = core.getTypes().getNodes();
        posLayout.removeAllViews();
        for (TypeNode pos : posList) {
            CheckBox posChk = new CheckBox(this);
            posChk.setText(pos.getValue());
            posChk.setChecked(wordClass.appliesToType(pos.getId()));

            posChk.setOnCheckedChangeListener((compoundButton, b) -> {
                if (b) {
                    wordClass.addApplyType(pos.getId());
                }
                else {
                    wordClass.deleteApplyType(pos.getId());
                }
            });

            posLayout.addView(posChk);
        }
        chkAllPos.setChecked(wordClass.appliesToType(-1));
    }

    @Override
    protected void onPause() {
        super.onPause();

        wordClass.setValue(txtWordClass.getText().toString());
        wordClass.setFreeText(chkFreeText.isChecked());
        wordClass.setAssociative(chkAssociative.isChecked());
    }

    private void handleExpandable() {
        if (posLayout.getVisibility() == View.VISIBLE) {
            TransitionManager.beginDelayedTransition(infoCardView,
                    new AutoTransition());
            posLayout.setVisibility(View.GONE);
            chkAllPos.setVisibility(View.GONE);
            arrow.setImageResource(R.drawable.ic_baseline_expand_more_24);
        }
        else {
            TransitionManager.beginDelayedTransition(infoCardView,
                    new AutoTransition());
            posLayout.setVisibility(View.VISIBLE);
            chkAllPos.setVisibility(View.VISIBLE);
            arrow.setImageResource(R.drawable.ic_baseline_expand_less_24);
        }
    }
}