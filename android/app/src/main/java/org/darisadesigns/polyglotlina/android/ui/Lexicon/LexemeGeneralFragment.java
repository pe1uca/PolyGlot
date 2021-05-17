package org.darisadesigns.polyglotlina.android.ui.Lexicon;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.darisadesigns.polyglotlina.DictCore;
import org.darisadesigns.polyglotlina.ManagersCollections.PropertiesManager;
import org.darisadesigns.polyglotlina.Nodes.ConWord;
import org.darisadesigns.polyglotlina.Nodes.TypeNode;
import org.darisadesigns.polyglotlina.Nodes.WordClass;
import org.darisadesigns.polyglotlina.Nodes.WordClassValue;
import org.darisadesigns.polyglotlina.android.PolyGlot;
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
    private DictCore core;

    private TextInputEditText txtConWord;
    private TextInputEditText txtLocalWord;

    private CheckBox chkOverridePronunciation;
    private CheckBox chkOverrideRules;

    private LinearLayout classesLinearLayout;

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
        HTMLEditorFragment htmlEditor = HTMLEditorFragment.newInstance(getResources().getString(R.string.label_definition));
        transaction.replace(R.id.fragment_container_view, htmlEditor).commit();

        chkOverridePronunciation = root.findViewById(R.id.chkOverridePronunciation);
        chkOverrideRules = root.findViewById(R.id.chkOverrideRules);

        classesLinearLayout = root.findViewById(R.id.classesLayout);

        core = ((PolyGlot)requireActivity().getApplicationContext()).getCore();
        TypeNode[] posItems = core.getTypes().getNodes();
        ArrayAdapter<TypeNode> spinnerArrayAdapter = new ArrayAdapter<>
                (requireActivity(), R.layout.list_item, posItems);
        AutoCompleteTextView posAutocomplete = root.findViewById(R.id.posAutocomplete);
        posAutocomplete.setAdapter(spinnerArrayAdapter);

        viewModel.getLiveWord().observe(getViewLifecycleOwner(), new Observer<ConWord>() {
            @Override
            public void onChanged(ConWord conWord) {
                if (null != conWord) {
                    txtConWord.setText(conWord.getValue());
                    txtLocalWord.setText(conWord.getLocalWord());
                    editorViewModel.updateText(conWord.getDefinition());

                    chkOverridePronunciation.setChecked(conWord.isProcOverride());
                    chkOverrideRules.setChecked(conWord.isRulesOverride());
                    TypeNode type = core.getTypes().getNodeById(conWord.getWordTypeId());
                    posAutocomplete.setText(type.getValue(), false);
                    posAutocomplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            TypeNode newType = spinnerArrayAdapter.getItem(position);
                            conWord.setWordTypeId(newType.getId());
                            setupClassView(conWord, newType.getId());
                        }
                    });
                    setupClassView(conWord, type.getId());
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

    @Override
    public void onPause() {
        super.onPause();
        ConWord word = viewModel.getLiveWord().getValue();
        word.setValue(txtConWord.getText().toString());
        word.setLocalWord(txtLocalWord.getText().toString());
        /* Definition saved with editorViewModel */
        word.setProcOverride(chkOverridePronunciation.isChecked());
        word.setRulesOverride(chkOverrideRules.isChecked());
        /* Part of speech saved with posAutocomplete listener */
        /* Classes being saved with txtClass and classAutocomplete listeners */
    }

    private void setupClassView(ConWord conWord, int typeId) {
        WordClass[] classesList = core.getWordClassCollection().getClassesForType(typeId);

        classesLinearLayout.removeAllViews();

        if (classesList.length == 0) {
            classesLinearLayout.setVisibility(View.GONE);
            return;
        }
        classesLinearLayout.setVisibility(View.VISIBLE);

        for (WordClass curClass : classesList) {
            int classId = curClass.getId();
            View classInputLayout;
            if (curClass.isFreeText()) {
                classInputLayout = getLayoutInflater().inflate(R.layout.class_free_text, classesLinearLayout);
                TextInputLayout txtInputLayout = classInputLayout.findViewById(R.id.textInputLayout);
                txtInputLayout.setHint(curClass.getValue());
                TextInputEditText txtClass = classInputLayout.findViewById(R.id.txtClass);

                txtClass.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {}

                    @Override
                    public void afterTextChanged(Editable s) {
                        conWord.setClassTextValue(classId, s.toString());
                    }
                });

                txtClass.setText(conWord.getClassTextValue(classId));
            }
            else {
                classInputLayout = getLayoutInflater().inflate(R.layout.class_spinner, classesLinearLayout);
                TextInputLayout txtInputLayout = classInputLayout.findViewById(R.id.textInputLayout);
                txtInputLayout.setHint(curClass.getValue());
                AutoCompleteTextView classAutocomplete = classInputLayout.findViewById(R.id.classAutocomplete);

                WordClassValue[] classValues = curClass.getValues().toArray(new WordClassValue[0]);
                ArrayAdapter<WordClassValue> spinnerArrayAdapter = new ArrayAdapter<>(requireActivity(), R.layout.list_item, classValues);
                classAutocomplete.setAdapter(spinnerArrayAdapter);

                classAutocomplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        conWord.setClassValue(classId, spinnerArrayAdapter.getItem(position).getId());
                    }
                });

                try {
                    classAutocomplete.setText(curClass.getValueById(conWord.getClassValue(classId)).getValue(), false);
                } catch (Exception e) {
                    Log.d(TAG, "No value for class", e);
                }
            }
        }
    }
}