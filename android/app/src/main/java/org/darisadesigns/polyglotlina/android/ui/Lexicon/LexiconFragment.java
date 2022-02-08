package org.darisadesigns.polyglotlina.android.ui.Lexicon;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.darisadesigns.polyglotlina.DictCore;
import org.darisadesigns.polyglotlina.ManagersCollections.ConWordCollection;
import org.darisadesigns.polyglotlina.Nodes.ConWord;
import org.darisadesigns.polyglotlina.android.R;
import org.darisadesigns.polyglotlina.android.ui.PViewModel;

import java.util.List;

/**
 * A fragment representing a list of Items.
 */
public class LexiconFragment extends Fragment implements LexemeRecyclerViewAdapter.OnItemClickListener {

    private static final String TAG = "Lexicon";

    private RecyclerView lexiconView;
    private Parcelable recyclerViewState;

    private DictCore core;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        PViewModel pViewModel = new ViewModelProvider(requireActivity()).get(PViewModel.class);
        View view = inflater.inflate(R.layout.fragment_lexicon, container, false);

        FloatingActionButton fab = view.findViewById(R.id.add_lexeme);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DictCore core = pViewModel.getLiveCore().getValue();
                core.getWordCollection().clear();
                try {
                    startLexemeActivity(core.getWordCollection().insert());
                } catch (Exception e) {
                    core.getOSHandler().getIOHandler().writeErrorLog(e);
                    core.getOSHandler().getInfoBox().error(
                            "Creation Error",
                            "Unable to create word: " + e.getLocalizedMessage()
                    );
                }
            }
        });

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
                    LexiconFragment.this.core = core;
                    ConWordCollection collection = core.getWordCollection();
                    List<ConWord> words = collection.getWordNodesList();
                    lexiconView.setAdapter(new LexemeRecyclerViewAdapter(words, LexiconFragment.this));
                }
            }
        });
        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search, menu);
        MenuItem menuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.e(TAG, "onQueryTextSubmit: " + query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.e(TAG, "onQueryTextChange: " + newText);
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return item.getItemId() == R.id.action_search;
    }

    private void startLexemeActivity(int lexemeId) {
        /* Store list position */
        recyclerViewState = lexiconView.getLayoutManager().onSaveInstanceState();
        Intent intent = new Intent(LexiconFragment.this.requireActivity(), LexemeInfoActivity.class);
        intent.putExtra(LexemeInfoActivity.CON_WORD_ID_EXTRA, lexemeId);
        startActivity(intent);
    }

    @Override
    public void onItemClick(ConWord item) {
        startLexemeActivity(item.getId());
    }

    @Override
    public void onResume() {
        super.onResume();
        /* Re make adapter to update added/removed lexemes */
        ConWordCollection collection = core.getWordCollection();
        List<ConWord> words = collection.getWordNodesList();
        lexiconView.setAdapter(new LexemeRecyclerViewAdapter(words, LexiconFragment.this));
        /* Restore lexicon position for better UX */
        lexiconView.getLayoutManager().onRestoreInstanceState(recyclerViewState);
    }
}