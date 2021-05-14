package org.darisadesigns.polyglotlina.android.ui.Lexicon;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.darisadesigns.polyglotlina.DictCore;
import org.darisadesigns.polyglotlina.ManagersCollections.ConWordCollection;
import org.darisadesigns.polyglotlina.Nodes.ConWord;
import org.darisadesigns.polyglotlina.android.R;
import org.darisadesigns.polyglotlina.android.ui.PViewModel;

import java.util.List;

/**
 * A fragment representing a list of Items.
 */
public class LexiconFragment extends Fragment {

    private static final String TAG = "Lexicon";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        PViewModel pViewModel = new ViewModelProvider(requireActivity()).get(PViewModel.class);
        View view = inflater.inflate(R.layout.fragment_lexicon, container, false);

        RecyclerView lexiconView = view.findViewById(R.id.lexiconList);
        Context context = view.getContext();
        lexiconView.setLayoutManager(new LinearLayoutManager(context));

        RecyclerView.ItemDecoration itemDecoration = new
                DividerItemDecoration(context, DividerItemDecoration.VERTICAL);
        lexiconView.addItemDecoration(itemDecoration);

        pViewModel.getLiveCore().observe(getViewLifecycleOwner(), new Observer<DictCore>() {
            @Override
            public void onChanged(@Nullable DictCore core) {
                if (core != null) {
                    ConWordCollection collection = core.getWordCollection();
                    List<ConWord> words = collection.getWordNodesList();
                    lexiconView.setAdapter(new LexemeRecyclerViewAdapter(words));
                }
            }
        });
        return view;
    }
}