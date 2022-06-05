package org.darisadesigns.polyglotlina.android.ui.WordClasses;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.AutoTransition;
import androidx.transition.TransitionManager;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import org.darisadesigns.polyglotlina.DictCore;
import org.darisadesigns.polyglotlina.Nodes.TypeNode;
import org.darisadesigns.polyglotlina.Nodes.WordClass;
import org.darisadesigns.polyglotlina.Nodes.WordClassValue;
import org.darisadesigns.polyglotlina.android.AndroidInfoBox;
import org.darisadesigns.polyglotlina.android.PolyGlot;
import org.darisadesigns.polyglotlina.android.R;

import java.util.ArrayList;
import java.util.List;

public class WordClassInfoActivity extends AppCompatActivity implements ClassValuesRecyclerViewAdapter.OnItemClickListener {

    private static final String TAG = "WordClassInfoActivity";
    public static final String WORD_CLASS_ID_EXTRA = "word-class-id";

    private DictCore core;
    private WordClass wordClass;

    private TextInputEditText txtWordClass;

    private CheckBox chkFreeText;
    private CheckBox chkAssociative;
    private CheckBox chkAllPos;

    private ImageButton posArrow;
    private LinearLayout posLayout;
    private ImageButton valuesArrow;
    private RecyclerView valuesRecyclerView;
    private LinearLayout valuesArrowLayout;
    private MaterialCardView infoCardView;

    private FloatingActionButton fab;

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

        fab = findViewById(R.id.add_value);
        fab.setOnClickListener(eventView -> {
            ((AndroidInfoBox)core.getOSHandler().getInfoBox()).stringInputDialog(
                    "New value",
                    "What is the new value?",
                    "Value",
                    this,
                    s -> {
                        if (null == s || s.isEmpty()) return;
                        try {
                            wordClass.addValue(s);
                            updateValuesList();
                        } catch (Exception e) {
                            ((AndroidInfoBox)core.getOSHandler().getInfoBox()).error(
                                    "Couldn't add value",
                                    e.getLocalizedMessage(),
                                    WordClassInfoActivity.this
                            );
                        }
                    }
            );
        });

        txtWordClass = findViewById(R.id.txtWordClass);
        chkFreeText = findViewById(R.id.chkFreeText);
        chkAssociative = findViewById(R.id.chkAssociative);

        chkFreeText.setOnCheckedChangeListener(this::handleCheckboxes);
        chkAssociative.setOnCheckedChangeListener(this::handleCheckboxes);

        chkAllPos = findViewById(R.id.chkAllPos);
        posLayout = findViewById(R.id.posLayout);
        valuesRecyclerView = findViewById(R.id.classValuesList);
        infoCardView = findViewById(R.id.infoCardView);
        posArrow = findViewById(R.id.pos_arrow_button);
        valuesArrow = findViewById(R.id.values_arrow_button);
        LinearLayout posArrowLayout = findViewById(R.id.pos_arrow_layout);
        valuesArrowLayout = findViewById(R.id.values_arrow_layout);

        posArrow.setOnClickListener(view -> handlePosExpandable());
        posArrowLayout.setOnClickListener(view -> handlePosExpandable());
        valuesArrow.setOnClickListener(view -> handleValuesExpandable());
        valuesArrowLayout.setOnClickListener(view -> handleValuesExpandable());

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
        txtWordClass.setText(wordClass.getValue());
        chkFreeText.setChecked(wordClass.isFreeText());
        chkAssociative.setChecked(wordClass.isAssociative());
        chkAllPos.setChecked(wordClass.appliesToType(-1));
        updateValuesList();
    }

    @Override
    protected void onPause() {
        super.onPause();

        wordClass.setValue(txtWordClass.getText().toString());
        wordClass.setFreeText(chkFreeText.isChecked());
        wordClass.setAssociative(chkAssociative.isChecked());
    }

    private void handlePosExpandable() {
        if (posLayout.getVisibility() == View.VISIBLE) {
            TransitionManager.beginDelayedTransition(infoCardView,
                    new AutoTransition());
            posLayout.setVisibility(View.GONE);
            chkAllPos.setVisibility(View.GONE);
            posArrow.setImageResource(R.drawable.ic_baseline_expand_more_24);
        }
        else {
            TransitionManager.beginDelayedTransition(infoCardView,
                    new AutoTransition());
            posLayout.setVisibility(View.VISIBLE);
            chkAllPos.setVisibility(View.VISIBLE);
            posArrow.setImageResource(R.drawable.ic_baseline_expand_less_24);
        }
    }

    private void handleValuesExpandable() {
        if (valuesRecyclerView.getVisibility() == View.VISIBLE) {
            TransitionManager.beginDelayedTransition(infoCardView,
                    new AutoTransition());
            valuesRecyclerView.setVisibility(View.GONE);
            valuesArrow.setImageResource(R.drawable.ic_baseline_expand_more_24);
        }
        else {
            TransitionManager.beginDelayedTransition(infoCardView,
                    new AutoTransition());
            valuesRecyclerView.setVisibility(View.VISIBLE);
            valuesArrow.setImageResource(R.drawable.ic_baseline_expand_less_24);
        }
    }

    private void handleCheckboxes(CompoundButton compoundButton, boolean b) {
        if (compoundButton == chkFreeText && b) {
            chkAssociative.setChecked(false);
        }
        if (compoundButton == chkAssociative && b) {
            chkFreeText.setChecked(false);
        }
        if (chkFreeText.isChecked() || chkAssociative.isChecked()) {
            valuesArrowLayout.setVisibility(View.GONE);
            fab.setVisibility(View.GONE);
            valuesRecyclerView.setVisibility(View.GONE);
        }
        else {
            valuesArrowLayout.setVisibility(View.VISIBLE);
            fab.setVisibility(View.VISIBLE);
            handleValuesExpandable();
        }
    }

    private void updateValuesList() {
        List<WordClassValue> nodes = new ArrayList<>(wordClass.getValues());
        valuesRecyclerView.setAdapter(new ClassValuesRecyclerViewAdapter(core, nodes, this));
    }

    @Override
    public void onItemClick(WordClassValue item) {
        ((AndroidInfoBox)core.getOSHandler().getInfoBox()).stringInputDialog(
                "Update value",
                "What is the replacement for this value?",
                item.getValue(),
                this,
                s -> {
                    if (null != s && !s.isEmpty()) {
                        item.setValue(s);
                        updateValuesList();
                    }
                }
        );
    }

    @Override
    public void onItemDeleteClick(WordClassValue item) {
        ((AndroidInfoBox)core.getOSHandler().getInfoBox()).yesNoCancel(
                "Are you sure?",
                "Do you want to delete this value?\nThis action can't be undone.",
                this,
                (dialog, which) -> {
                    if (which != DialogInterface.BUTTON_POSITIVE) {
                        return;
                    }
                    try {
                        wordClass.deleteValue(item.getId());
                        updateValuesList();
                    } catch (Exception e) {
                      // Can be ignore
                    }
                }
        );
    }
}