package org.darisadesigns.polyglotlina.android.ui.Lexicon;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Handler;
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
import android.widget.ListView;
import android.widget.TextView;

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
    private TextInputEditText txtRomanization;
    private TextInputEditText txtPronunciation;

    private CheckBox chkOverridePronunciation;
    private CheckBox chkOverrideRules;

    private LinearLayout classesLinearLayout;
    private TextInputLayout txtConWordLayout;
    private TextInputLayout txtLocalWordLayout;
    private TextInputLayout romanizationLayout;
    private TextInputLayout txtPronunciationLayout;
    private TextInputLayout posInputLayout;
    private AutoCompleteTextView posAutocomplete;

    private TextView errorMessage;

    private boolean forceUpdate = false;

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
        txtRomanization = root.findViewById(R.id.txtRomanization);
        txtPronunciation = root.findViewById(R.id.txtPronunciation);
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        HTMLEditorFragment htmlEditor = HTMLEditorFragment.newInstance(getResources().getString(R.string.label_definition));
        transaction.replace(R.id.fragment_container_view, htmlEditor).commit();

        chkOverridePronunciation = root.findViewById(R.id.chkOverridePronunciation);
        chkOverrideRules = root.findViewById(R.id.chkOverrideRules);

        classesLinearLayout = root.findViewById(R.id.classesLayout);
        txtConWordLayout = root.findViewById(R.id.txtConWordLayout);
        txtLocalWordLayout = root.findViewById(R.id.txtLocalWordLayout);
        romanizationLayout = root.findViewById(R.id.romanizationLayout);
        txtPronunciationLayout = root.findViewById(R.id.txtPronunciationLayout);
        posInputLayout = root.findViewById(R.id.posInputLayout);

        errorMessage = root.findViewById(R.id.errorMessage);

        core = ((PolyGlot)requireActivity().getApplicationContext()).getCore();
        TypeNode[] posItems = core.getTypes().getNodes();
        ArrayAdapter<TypeNode> spinnerArrayAdapter = new ArrayAdapter<>
                (requireActivity(), R.layout.list_item, posItems);
        posAutocomplete = root.findViewById(R.id.posAutocomplete);
        posAutocomplete.setAdapter(spinnerArrayAdapter);

        viewModel.getLiveWord().observe(getViewLifecycleOwner(), new Observer<ConWord>() {
            @Override
            public void onChanged(ConWord conWord) {
                if (null != conWord) {
                    forceUpdate = true;
                    txtConWord.setText(conWord.getValue());
                    txtLocalWord.setText(conWord.getLocalWord());
                    try {
                        txtPronunciation.setText(conWord.getPronunciation());
                    } catch (Exception e) {
                        core.getOSHandler().getIOHandler().writeErrorLog(e);
                    }
                    editorViewModel.updateText(conWord.getDefinition());

                    chkOverridePronunciation.setChecked(conWord.isProcOverride());
                    chkOverrideRules.setChecked(conWord.isRulesOverride());
                    TypeNode type = core.getTypes().getNodeById(conWord.getWordTypeId());
                    posAutocomplete.setText(type.getValue(), false);
                    posAutocomplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            TypeNode newType = spinnerArrayAdapter.getItem(position);
                            setupClassView(conWord, newType.getId());
                        }
                    });
                    if(core.getRomManager().isEnabled()) {
                        romanizationLayout.setVisibility(View.VISIBLE);
                        generateRomanization(conWord.getValue());
                    }
                    setupClassView(conWord, type.getId());
                    forceUpdate = false;
                }
            }
        });

        txtConWord.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if(core.getRomManager().isEnabled()) {
                    generateRomanization(s.toString());
                }
                if (chkOverridePronunciation.isChecked()) return;

                try {
                    String pronunciation = core.getPronunciationMgr().getPronunciation(s.toString());

                    if (!pronunciation.isEmpty() || s.toString().isEmpty()) {
                        txtPronunciation.setText(pronunciation);
                    }
                } catch (Exception e) {
                    core.getOSHandler().getIOHandler().writeErrorLog(e);
                }
            }
        });

        return root;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (!isLexemeValid()) return; // Don't save the word (Fix classes always being saved)

        ConWord word = viewModel.getLiveWord().getValue();
        word.setValue(txtConWord.getText().toString());
        word.setLocalWord(txtLocalWord.getText().toString());
        word.setDefinition(editorViewModel.getLiveText().getValue());
        word.setProcOverride(chkOverridePronunciation.isChecked());
        word.setRulesOverride(chkOverrideRules.isChecked());
        int posIndex = posAutocomplete.getListSelection();
        if (posIndex != ListView.INVALID_POSITION)
            word.setWordTypeId(((TypeNode)posAutocomplete.getAdapter().getItem(posIndex)).getId());
        /* Classes being saved with txtClass and classAutocomplete listeners. Check setupClassView() */
        word.setPronunciation(txtPronunciation.getText().toString());
    }

    public boolean isLexemeValid() {
        if (forceUpdate || chkOverrideRules.isChecked()) return true;

        errorMessage.setVisibility(View.GONE);
        ConWord currWord = viewModel.getLiveWord().getValue();

        ConWord testWord = new ConWord();
        testWord.setId(currWord.getId());

        int posId = currWord.getWordTypeId();
        int posIndex = posAutocomplete.getListSelection();
        if (posIndex != ListView.INVALID_POSITION)
            posId = ((TypeNode)posAutocomplete.getAdapter().getItem(posIndex)).getId();

        testWord.setValue(txtConWord.getText().toString());
        testWord.setLocalWord(txtLocalWord.getText().toString());
        testWord.setDefinition(editorViewModel.getLiveText().getValue());
        testWord.setPronunciation(txtPronunciation.getText().toString());
        testWord.setWordTypeId(posId);
        testWord.setRulesOverride(chkOverrideRules.isChecked());
        testWord.setCore(core);

        ConWord errors = core.getWordCollection().testWordLegality(testWord);
        String pronunciationError = "";
        try {
            pronunciationError = errors.getPronunciation();
        } catch (Exception e) {
            pronunciationError = e.getLocalizedMessage();
        }

        boolean isValid = setLayoutError(txtConWordLayout, errors.getValue());
        isValid = isValid && setLayoutError(txtLocalWordLayout, errors.getLocalWord());
        isValid = isValid && setLayoutError(txtPronunciationLayout, pronunciationError);
        isValid = isValid && setLayoutError(posInputLayout, errors.typeError);

        if (!errors.getDefinition().isEmpty()) {
            isValid = false;
            errorMessage.setError(errors.getDefinition());
            errorMessage.setText(errors.getDefinition());
            errorMessage.setVisibility(View.VISIBLE);
        }

        return isValid;
    }

    private boolean setLayoutError(TextInputLayout layout, String message) {
        layout.setError(null);
        if(message.isEmpty()) return true;

        layout.setError(message);
        return false;
    }

    private void generateRomanization(String conWord) {
        try {
            String rom = core.getRomManager().getPronunciation(conWord);
            txtRomanization.setText(rom);
        } catch (Exception e) {
            core.getOSHandler().getIOHandler().writeErrorLog(e);
        }
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