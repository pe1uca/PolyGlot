package org.darisadesigns.polyglotlina.android.ui.Lexicon;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import org.darisadesigns.polyglotlina.DictCore;
import org.darisadesigns.polyglotlina.ManagersCollections.ConWordCollection;
import org.darisadesigns.polyglotlina.Nodes.ConWord;
import org.darisadesigns.polyglotlina.android.PolyGlot;
import org.darisadesigns.polyglotlina.android.R;
import org.darisadesigns.polyglotlina.android.ui.PViewModel;

import java.util.List;

/**
 * A fragment representing a list of Items.
 */
public class LexiconFragment extends Fragment {

    private static final String TAG = "Lexicon";

    private RecyclerView lexiconView;
    private Parcelable recyclerViewState;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        PViewModel pViewModel = new ViewModelProvider(requireActivity()).get(PViewModel.class);
        View view = inflater.inflate(R.layout.fragment_lexicon, container, false);

        lexiconView = view.findViewById(R.id.lexiconList);
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
                    lexiconView.setAdapter(
                            new LexemeRecyclerViewAdapter(words, new LexemeRecyclerViewAdapter.OnItemClickListener() {
                                @Override
                                public void onItemClick(ConWord item) {
                                    /* Store list position */
                                    recyclerViewState = lexiconView.getLayoutManager().onSaveInstanceState();
                                    Intent intent = new Intent(LexiconFragment.this.requireActivity(), LexemeInfoActivity.class);
                                    intent.putExtra(LexemeInfoActivity.CON_WORD_ID_EXTRA, item.getId());
                                    startActivity(intent);
                                }
                            })
                    );
                }
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        /* Force lexicon list to redraw  */
        lexiconView.setAdapter(lexiconView.getAdapter());
        /* Restore lexicon position for better UX */
        lexiconView.getLayoutManager().onRestoreInstanceState(recyclerViewState);
    }
}