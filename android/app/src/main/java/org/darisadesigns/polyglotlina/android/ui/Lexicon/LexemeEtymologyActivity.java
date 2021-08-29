package org.darisadesigns.polyglotlina.android.ui.Lexicon;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.darisadesigns.polyglotlina.DictCore;
import org.darisadesigns.polyglotlina.Nodes.ConWord;
import org.darisadesigns.polyglotlina.Nodes.EtyExternalParent;
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
        getSupportActionBar().setTitle(conWord.getValue());

        tabLayout = findViewById(R.id.tabs);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
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
                holder.vConWord.setText(Objects.requireNonNull(getNodeData(position)).toString());
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