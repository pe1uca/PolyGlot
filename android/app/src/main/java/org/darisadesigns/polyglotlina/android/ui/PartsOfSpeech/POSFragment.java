package org.darisadesigns.polyglotlina.android.ui.PartsOfSpeech;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.darisadesigns.polyglotlina.DictCore;
import org.darisadesigns.polyglotlina.ManagersCollections.ConWordCollection;
import org.darisadesigns.polyglotlina.Nodes.ConWord;
import org.darisadesigns.polyglotlina.Nodes.TypeNode;
import org.darisadesigns.polyglotlina.android.R;
import org.darisadesigns.polyglotlina.android.ui.Lexicon.LexemeInfoActivity;
import org.darisadesigns.polyglotlina.android.ui.Lexicon.LexemeRecyclerViewAdapter;
import org.darisadesigns.polyglotlina.android.ui.Lexicon.LexiconFragment;
import org.darisadesigns.polyglotlina.android.ui.PViewModel;

import java.util.Arrays;
import java.util.List;

/**
 * A fragment to show the list of Parts of Speech.
 */
public class POSFragment extends Fragment implements POSRecyclerViewAdapter.OnItemClickListener {

    private RecyclerView posRecyclerView;
    private Parcelable recyclerViewState;

    private DictCore core;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        PViewModel pViewModel = new ViewModelProvider(requireActivity()).get(PViewModel.class);
        View view = inflater.inflate(R.layout.fragment_pos, container, false);
        FloatingActionButton fab = view.findViewById(R.id.add_pos);
        fab.setOnClickListener(eventView -> {
            DictCore core = pViewModel.getLiveCore().getValue();
            core.getTypes().clear();
            try {
                startPOSActivity(core.getTypes().insert());
            } catch (Exception e) {
                core.getOSHandler().getIOHandler().writeErrorLog(e);
                core.getOSHandler().getInfoBox().error(
                        "Creation Error",
                        "Unable to create word: " + e.getLocalizedMessage()
                );
            }
        });

        posRecyclerView = view.findViewById(R.id.posList);
        Context context = view.getContext();
        posRecyclerView.setLayoutManager(new LinearLayoutManager(context));

        RecyclerView.ItemDecoration itemDecoration = new
                DividerItemDecoration(context, DividerItemDecoration.VERTICAL);
        posRecyclerView.addItemDecoration(itemDecoration);

        pViewModel.getLiveCore().observe(getViewLifecycleOwner(), core -> {
            if (core != null) {
                POSFragment.this.core = core;
                List<TypeNode> nodes = Arrays.asList(core.getTypes().getNodes());
                posRecyclerView.setAdapter(new POSRecyclerViewAdapter(core, nodes, POSFragment.this));
            }
        });
        return view;
    }

    private void startPOSActivity(int lexemeId) {
        /* Store list position */
        recyclerViewState = posRecyclerView.getLayoutManager().onSaveInstanceState();
        Intent intent = new Intent(this.requireActivity(), POSInfoActivity.class);
        intent.putExtra(POSInfoActivity.POS_ID_EXTRA, lexemeId);
        startActivity(intent);
    }

    @Override
    public void onItemClick(TypeNode item) {
        startPOSActivity(item.getId());
    }

    @Override
    public void onResume() {
        super.onResume();
        /* Re make adapter to update added/removed POS */
        List<TypeNode> nodes = Arrays.asList(core.getTypes().getNodes());
        posRecyclerView.setAdapter(new POSRecyclerViewAdapter(core, nodes, this));
        /* Restore lexicon position for better UX */
        posRecyclerView.getLayoutManager().onRestoreInstanceState(recyclerViewState);
    }
}