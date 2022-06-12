package org.darisadesigns.polyglotlina.android.ui.Lexicon.Etymology;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.darisadesigns.polyglotlina.DictCore;
import org.darisadesigns.polyglotlina.ManagersCollections.EtymologyManager;
import org.darisadesigns.polyglotlina.Nodes.ConWord;
import org.darisadesigns.polyglotlina.Nodes.EtyExternalParent;
import org.darisadesigns.polyglotlina.android.AndroidInfoBox;
import org.darisadesigns.polyglotlina.android.AndroidPropertiesManager;
import org.darisadesigns.polyglotlina.android.PolyGlot;
import org.darisadesigns.polyglotlina.android.R;

import java.util.Objects;

import dev.bandb.graphview.AbstractGraphAdapter;
import dev.bandb.graphview.graph.Graph;
import dev.bandb.graphview.graph.Node;
import dev.bandb.graphview.layouts.tree.BuchheimWalkerConfiguration;
import dev.bandb.graphview.layouts.tree.BuchheimWalkerLayoutManager;
import dev.bandb.graphview.layouts.tree.TreeEdgeDecoration;

public class LexemeEtymologyActivity extends AppCompatActivity {

    private static final String TAG = "LexemeEtymology";
    public static final String CON_WORD_ID_EXTRA = "con-word-id";

    private static final int TAB_PARENTS_POSITION = 0;
    private static final int TAB_CHILDREN_POSITION = 1;

    private ConWord conWord;
    private DictCore core;

    private AbstractGraphAdapter<NodeViewHolder> adapter;
    private TabLayout tabLayout;

    ActivityResultLauncher<Intent> resultInternalRoot = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK)
                    addInternalRoot(result.getData().getIntExtra(AddInternalRootActivity.ROOT_WORD_ID_EXTRA, -1));
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lexeme_etymology);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        Intent intent = getIntent();
        PolyGlot polyGlot = (PolyGlot)getApplicationContext();
        core = polyGlot.getCore();
        int wordId = intent.getIntExtra(CON_WORD_ID_EXTRA, -1);
        conWord = core.getWordCollection().getNodeById(wordId);
        ((AndroidPropertiesManager)core.getPropertiesManager()).setConActionBarTitle(getSupportActionBar(), conWord.toString());

        tabLayout = findViewById(R.id.tabs);

        FloatingActionButton fab_internal = findViewById(R.id.fab_add_etymon_internal);
        fab_internal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resultInternalRoot.launch(new Intent(LexemeEtymologyActivity.this, AddInternalRootActivity.class));
            }
        });

        FloatingActionButton fab_external = findViewById(R.id.fab_add_etymon_external);
        fab_external.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddExternalRoot();
            }
        });

        inflateEtymologyTree(TAB_PARENTS_POSITION);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                inflateEtymologyTree(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) { }

            @Override
            public void onTabReselected(TabLayout.Tab tab) { }
        });
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        conWord.setEtymNotes(conWord.getEtymNotes());
    }

    private void showAddExternalRoot() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.msg_new_external_etymon))
                .setTitle(getString(R.string.title_new_external_etymon));

        View viewInflated = LayoutInflater.from(this).inflate(R.layout.dialog_new_external_etymon, null, false);

        final TextInputEditText txtExternalEtymon = viewInflated.findViewById(R.id.txtExternalEtymon);
        final TextInputEditText txtSourceLanguage = viewInflated.findViewById(R.id.txtSourceLanguage);
        final TextInputEditText txtDefinition = viewInflated.findViewById(R.id.txtDefinition);

        builder.setPositiveButton(android.R.string.ok, (dialogInterface, i) -> {
            if (txtExternalEtymon.getText().toString().isEmpty()) {
                 return;
            }
            EtyExternalParent newExtEtymon = new EtyExternalParent();
            newExtEtymon.setValue(txtExternalEtymon.getText().toString());
            newExtEtymon.setExternalLanguage(txtSourceLanguage.getText().toString());
            newExtEtymon.setDefinition(txtDefinition.getText().toString());

            core.getEtymologyManager().addExternalRelation(newExtEtymon, conWord.getId());

            if (tabLayout.getSelectedTabPosition() == TAB_PARENTS_POSITION) inflateEtymologyTree(TAB_PARENTS_POSITION);
            else tabLayout.selectTab(tabLayout.getTabAt(TAB_PARENTS_POSITION));
        });
        builder.setNegativeButton(android.R.string.cancel, null);
        builder.setView(viewInflated);
        builder.setCancelable(false);

        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    private void addInternalRoot(int rootId) {
        if (rootId == -1) return;
        try {
            core.getEtymologyManager().addRelation(rootId, conWord.getId());
            if (tabLayout.getSelectedTabPosition() == TAB_PARENTS_POSITION) inflateEtymologyTree(TAB_PARENTS_POSITION);
            else tabLayout.selectTab(tabLayout.getTabAt(TAB_PARENTS_POSITION));
        } catch (EtymologyManager.IllegalLoopException e) {
            core.getOSHandler().getIOHandler().writeErrorLog(e);
            ((AndroidInfoBox)core.getOSHandler().getInfoBox()).error(
                    "Illegal Loop: Parent not Added",
                    e.getLocalizedMessage(),
                    this
            );
        }
    }

    private void inflateEtymologyTree(int tabPosition) {
        RecyclerView recyclerView = findViewById(R.id.recyclerParents);
        Node mainNode = new Node(conWord);
        Graph graph = new Graph();
        int orientation = BuchheimWalkerConfiguration.ORIENTATION_BOTTOM_TOP;
        if(tabPosition == TAB_PARENTS_POSITION) {
            addEtymologyParents(graph, mainNode);
        }
        else if(tabPosition == TAB_CHILDREN_POSITION) {
            orientation = BuchheimWalkerConfiguration.ORIENTATION_TOP_BOTTOM;
            addEtymologyChildren(graph, mainNode);
        }

        BuchheimWalkerConfiguration configuration = new BuchheimWalkerConfiguration.Builder()
                .setSiblingSeparation(100)
                .setLevelSeparation(100)
                .setSubtreeSeparation(100)
                .setOrientation(orientation)
                .build();
        recyclerView.setLayoutManager(new BuchheimWalkerLayoutManager(this, configuration));
        recyclerView.addItemDecoration(new TreeEdgeDecoration());

        adapter = new AbstractGraphAdapter<NodeViewHolder>() {
            @NonNull
            @Override
            public NodeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.etymology_node, parent, false);
                return new NodeViewHolder(view);
            }

            @Override
            public void onBindViewHolder(@NonNull NodeViewHolder holder, int position) {
                Object obj = Objects.requireNonNull(getNodeData(position));
                holder.vConWord.setText(obj.toString());
                if (!(obj instanceof EtyExternalParent))
                    ((AndroidPropertiesManager)core.getPropertiesManager()).setConViewTypeface(holder.vConWord);
            }
        };
        adapter.submitGraph(graph);
        recyclerView.setVisibility(View.VISIBLE);
        recyclerView.setAdapter(adapter);
    }

    private void addEtymologyParents(Graph graph, Node node) {
        ConWord word = (ConWord) node.getData();
        for (Integer currentParentId :
                core.getEtymologyManager().getWordParentsIds(word.getId())) {
            Node parentNode = new Node(core.getWordCollection().getNodeById(currentParentId));
            graph.addEdge(node, parentNode);
            addEtymologyParents(graph, parentNode);
        }

        for (EtyExternalParent extPar : core.getEtymologyManager().getWordExternalParents(word.getId())) {
            Node externalParentNode = new Node(extPar);
            graph.addEdge(node, externalParentNode);
        }
    }

    private void addEtymologyChildren(Graph graph, Node node) {
        ConWord word = (ConWord) node.getData();
        for (Integer currentChildId :
                core.getEtymologyManager().getChildren(word.getId())) {
            Node childNode = new Node(core.getWordCollection().getNodeById(currentChildId));
            graph.addEdge(node, childNode);
            addEtymologyChildren(graph, childNode);
        }
    }

    public static class NodeViewHolder extends RecyclerView.ViewHolder {
        public final TextView vConWord;

        public NodeViewHolder(View view) {
            super(view);
            vConWord = view.findViewById(R.id.textView);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + vConWord.getText() + "'";
        }
    }
}