package org.darisadesigns.polyglotlina.android.ui.Lexicon;

import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.darisadesigns.polyglotlina.DictCore;
import org.darisadesigns.polyglotlina.ManagersCollections.ConWordCollection;
import org.darisadesigns.polyglotlina.Nodes.ConWord;
import org.darisadesigns.polyglotlina.android.PolyGlot;
import org.darisadesigns.polyglotlina.android.R;

import java.util.List;

public class LexemePickerFragment extends Fragment implements LexemeRecyclerViewAdapter.OnItemClickListener {

    private static String TAG = "LexemePickerFragment";
    public static String SELECTED_WORD_ID = "selected-word-id";

    private LexemePickerViewModel mViewModel;

    private RecyclerView lexiconView;
    private DictCore core;

    public static LexemePickerFragment newInstance() {
        return new LexemePickerFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.lexeme_picker_fragment, container, false);
        mViewModel = new ViewModelProvider(requireActivity()).get(LexemePickerViewModel.class);
        PolyGlot polyGlot = (PolyGlot)requireActivity().getApplicationContext();
        core = polyGlot.getCore();

        lexiconView = view.findViewById(R.id.lexiconList);
        lexiconView.setLayoutManager(new LinearLayoutManager(getContext()));

        RecyclerView.ItemDecoration itemDecoration = new
                DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        lexiconView.addItemDecoration(itemDecoration);

        ConWordCollection collection = core.getWordCollection();
        List<ConWord> words = collection.getWordNodesList();
        lexiconView.setAdapter(new LexemeRecyclerViewAdapter(core, words, this));
        return view;
    }

    @Override
    public void onItemClick(ConWord item) {
        mViewModel.updateWord(item);
    }

}