package org.darisadesigns.polyglotlina.android.ui.Lexicon;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.textfield.TextInputEditText;

import org.darisadesigns.polyglotlina.Nodes.ConWord;
import org.darisadesigns.polyglotlina.android.R;
import org.darisadesigns.polyglotlina.android.ui.EditorViewModel;
import org.darisadesigns.polyglotlina.android.ui.HTMLEditorFragment;

import jp.wasabeef.richeditor.RichEditor;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LexemeGeneralFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LexemeGeneralFragment extends Fragment {

    private static final String TAG = "LexemeGeneral";
    private LexemeInfoViewModel viewModel;
    private EditorViewModel editorViewModel;

    private TextInputEditText txtConWord;
    private TextInputEditText txtLocalWord;

    public static LexemeGeneralFragment newInstance() {
        return new LexemeGeneralFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_lexeme_general, container, false);

        viewModel = new ViewModelProvider(requireActivity()).get(LexemeInfoViewModel.class);
        editorViewModel = new ViewModelProvider(this).get(EditorViewModel.class);

        txtConWord = root.findViewById(R.id.txtConWord);
        txtLocalWord = root.findViewById(R.id.txtLocalWord);
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        HTMLEditorFragment htmlEditor = HTMLEditorFragment.newInstance();
        transaction.replace(R.id.fragment_container_view, htmlEditor).commit();

        viewModel.getLiveWord().observe(getViewLifecycleOwner(), new Observer<ConWord>() {
            @Override
            public void onChanged(ConWord conWord) {
                if (null != conWord) {
                    txtConWord.setText(conWord.getValue());
                    txtLocalWord.setText(conWord.getLocalWord());
                    editorViewModel.updateText(conWord.getDefinition());
                }
            }
        });

        editorViewModel.getLiveText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String text) {
                viewModel.getLiveWord().getValue().setDefinition(text);
            }
        });

        return root;
    }
}