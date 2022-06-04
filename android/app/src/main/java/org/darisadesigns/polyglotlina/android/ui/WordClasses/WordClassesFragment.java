package org.darisadesigns.polyglotlina.android.ui.WordClasses;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
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
import org.darisadesigns.polyglotlina.Nodes.WordClass;
import org.darisadesigns.polyglotlina.android.AndroidInfoBox;
import org.darisadesigns.polyglotlina.android.R;
import org.darisadesigns.polyglotlina.android.ui.PViewModel;

import java.util.Arrays;
import java.util.List;

public class WordClassesFragment extends Fragment implements WordClassesRecyclerViewAdapter.OnItemClickListener {

    private RecyclerView wordClassesRecyclerView;
    private Parcelable recyclerViewState;

    private DictCore core;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        PViewModel pViewModel = new ViewModelProvider(requireActivity()).get(PViewModel.class);
        View view = inflater.inflate(R.layout.fragment_word_classes, container, false);
        FloatingActionButton fab = view.findViewById(R.id.add_word_class);
        fab.setOnClickListener(eventView -> {
            DictCore core = pViewModel.getLiveCore().getValue();
            try {
                int newClassId = core.getWordClassCollection().addNode(new WordClass());
                startWordClassActivity(newClassId);
            } catch (Exception e) {
                core.getOSHandler().getIOHandler().writeErrorLog(e);
                core.getOSHandler().getInfoBox().error(
                        "Creation Error",
                        "Unable to create class: " + e.getLocalizedMessage()
                );
            }
        });

        wordClassesRecyclerView = view.findViewById(R.id.nounClassesList);
        Context context = view.getContext();
        wordClassesRecyclerView.setLayoutManager(new LinearLayoutManager(context));

        RecyclerView.ItemDecoration itemDecoration = new
                DividerItemDecoration(context, DividerItemDecoration.VERTICAL);
        wordClassesRecyclerView.addItemDecoration(itemDecoration);

        pViewModel.getLiveCore().observe(getViewLifecycleOwner(), core -> {
            if (core != null) {
                WordClassesFragment.this.core = core;
                updateList();
            }
        });

        return view;
    }

    private void startWordClassActivity(int wordClassId) {
        /* Store list position */
        recyclerViewState = wordClassesRecyclerView.getLayoutManager().onSaveInstanceState();
        Intent intent = new Intent(this.requireActivity(), WordClassInfoActivity.class);
        intent.putExtra(WordClassInfoActivity.WORD_CLASS_ID_EXTRA, wordClassId);
        startActivity(intent);
    }

    private void updateList() {
        List<WordClass> nodes = Arrays.asList(core.getWordClassCollection().getAllWordClasses());
        wordClassesRecyclerView.setAdapter(new WordClassesRecyclerViewAdapter(core, nodes, WordClassesFragment.this));
    }

    @Override
    public void onItemClick(WordClass item) {
        startWordClassActivity(item.getId());
    }

    @Override
    public void onItemDeleteClick(WordClass item) {
        ((AndroidInfoBox)core.getOSHandler().getInfoBox()).yesNoCancel(
                "Are you sure?",
                "Do you want to delete this word class?\nThe class will be removed from all words and all values will be lost.\nThis action can't be undone.",
                requireContext(),
                (dialog, which) -> {
                    if (which != DialogInterface.BUTTON_POSITIVE) {
                        return;
                    }
                    try {
                        core.getWordClassCollection().deleteNodeById(item.getId());
                        updateList();
                    } catch (Exception e) {
                        ((AndroidInfoBox)core.getOSHandler().getInfoBox()).error(
                                "Unable to delete",
                                "Unable to delete class: " + e.getLocalizedMessage(),
                                requireContext()
                        );
                    }
                }
        );
    }

    @Override
    public void onResume() {
        super.onResume();
        /* Re make adapter to update added/removed classes */
        updateList();
        /* Restore position for better UX */
        wordClassesRecyclerView.getLayoutManager().onRestoreInstanceState(recyclerViewState);
    }
}