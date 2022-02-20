package org.darisadesigns.polyglotlina.android.ui.PartsOfSpeech.Conjugations;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import com.google.android.material.textfield.TextInputEditText;

import org.darisadesigns.polyglotlina.DictCore;
import org.darisadesigns.polyglotlina.Nodes.ConjugationDimension;
import org.darisadesigns.polyglotlina.Nodes.ConjugationNode;
import org.darisadesigns.polyglotlina.Nodes.DictNode;
import org.darisadesigns.polyglotlina.Nodes.TypeNode;
import org.darisadesigns.polyglotlina.android.AndroidInfoBox;
import org.darisadesigns.polyglotlina.android.PolyGlot;
import org.darisadesigns.polyglotlina.android.R;
import org.darisadesigns.polyglotlina.android.ui.EditorViewModel;
import org.darisadesigns.polyglotlina.android.ui.HTMLEditorFragment;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class POSConjugationInfoActivity extends AppCompatActivity implements POSConjugationDimensionRecyclerViewAdapter.OnItemClickListener {

    private static final String TAG = "POSConjugationInfoActivity";
    public static final String POS_ID_EXTRA = "pos-id";
    public static final String CONJUGATION_ID_EXTRA = "conjugation-id";

    private EditorViewModel editorViewModel;
    private DictCore core;
    private ConjugationNode conjugationNode;
    private TypeNode posNode;

    private TextInputEditText txtConjugationName;
    private CheckBox chkDimensionless;
    private RecyclerView dimensionsView;

    private Button addButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pos_conjugation_info);

        setSupportActionBar((Toolbar) findViewById(R.id.app_bar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        Intent intent = getIntent();
        PolyGlot polyGlot = (PolyGlot)getApplicationContext();
        core = polyGlot.getCore();
        int posId = intent.getIntExtra(POS_ID_EXTRA, -1);
        int conjugationId = intent.getIntExtra(CONJUGATION_ID_EXTRA, -1);
        conjugationNode = core.getConjugationManager().getConjugationTemplate(posId, conjugationId);
        posNode = core.getTypes().getNodeById(posId);

        getSupportActionBar().setTitle(posNode.getValue() + ": " + conjugationNode.getValue());

        editorViewModel = new ViewModelProvider(this).get(EditorViewModel.class);

        txtConjugationName = findViewById(R.id.txtConjugationName);
        chkDimensionless = findViewById(R.id.chkDimensionless);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        HTMLEditorFragment htmlEditor = HTMLEditorFragment.newInstance(getResources().getString(R.string.label_pos_notes));
        transaction.replace(R.id.fragment_container_view, htmlEditor).commit();

        txtConjugationName.setText(conjugationNode.getValue());
        editorViewModel.updateText(conjugationNode.getNotes());
        chkDimensionless.setChecked(conjugationNode.isDimensionless());

        dimensionsView = findViewById(R.id.dimensionsList);
        dimensionsView.setLayoutManager(new LinearLayoutManager(this));

        RecyclerView.ItemDecoration itemDecoration = new
                DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        dimensionsView.addItemDecoration(itemDecoration);

        addButton = findViewById(R.id.btnAddDimension);

        addButton.setOnClickListener(v -> {
            addDimension();
        });

        chkDimensionless.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                ((AndroidInfoBox)core.getOSHandler().getInfoBox()).yesNoCancel(
                        "Are you sure?",
                        "Are you sure you wish to make this dimensionless?\n"
                                + "The dimensions for this declension/conjugation will be erased permanently.",
                        POSConjugationInfoActivity.this,
                        (dialog, which) -> {
                            if (which != DialogInterface.BUTTON_POSITIVE) {
                                buttonView.setChecked(false);
                                return;
                            }
                            saveConjugation();
                            core.getConjugationManager().deprecateAllConjugations(posNode.getId());
                            updateDimensionsList();
                        }
                );
            }
            else
                updateDimensionsList();
        });

        updateDimensionsList();
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveConjugation();
    }

    @Override
    public void onItemClick(ConjugationDimension item) {
        ((AndroidInfoBox)core.getOSHandler().getInfoBox()).stringInputDialog(
                "Rename dimension",
                "What is the new name for this dimension?",
                item.getValue(),
                this,
                s -> {
                    if (null != s && !s.isEmpty()) {
                        item.setValue(s);
                        updateDimensionsList();
                    }
                }
        );
    }

    @Override
    public void onItemDeleteClick(ConjugationDimension item) {
        conjugationNode.deleteDimension(item.getId());
        updateDimensionsList();
    }

    private void addDimension() {
        ((AndroidInfoBox)core.getOSHandler().getInfoBox()).stringInputDialog(
                "New dimension",
                "What is the name for this new dimension?",
                "Name",
                this,
                s -> {
                    if (null == s && s.isEmpty()) return;
                    conjugationNode.clearBuffer();
                    ConjugationDimension buffer = conjugationNode.getBuffer();
                    buffer.setValue(s);
                    conjugationNode.getDimensions()
                            .stream()
                            .max(Comparator.comparing(DictNode::getId))
                            .ifPresent(last -> buffer.setId(last.getId() + 1));
                    conjugationNode.addDimension(buffer);

                    updateDimensionsList();
                }
        );
    }

    private void saveConjugation() {
        ConjugationNode newNode = new ConjugationNode(-1, core.getConjugationManager());
        ConjugationNode tmp = core.getConjugationManager().getConjugation(posNode.getId(), conjugationNode.getId());

        newNode.setEqual(tmp);

        newNode.setValue(txtConjugationName.getText().toString());
        newNode.setDimensionless(chkDimensionless.isChecked());
        newNode.setNotes(editorViewModel.getLiveText().getValue());

        core.getConjugationManager().updateConjugationTemplate(posNode.getId(), conjugationNode.getId(), newNode);
    }

    private void updateDimensionsList() {
        if (chkDimensionless.isChecked()) {
            addButton.setVisibility(View.GONE);
            dimensionsView.setVisibility(View.GONE);
            return;
        }
        List<ConjugationDimension> dimensionList = new ArrayList<>(conjugationNode.getDimensions());
        POSConjugationDimensionRecyclerViewAdapter adapter = new POSConjugationDimensionRecyclerViewAdapter(core, dimensionList, this);
        dimensionsView.setAdapter(adapter);

        addButton.setVisibility(View.VISIBLE);
        dimensionsView.setVisibility(View.VISIBLE);
    }
}