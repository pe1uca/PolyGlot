package org.darisadesigns.polyglotlina.android.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;

import com.google.android.material.textfield.TextInputEditText;

import org.darisadesigns.polyglotlina.DictCore;
import org.darisadesigns.polyglotlina.ManagersCollections.PropertiesManager;
import org.darisadesigns.polyglotlina.android.R;

public class LangPropertiesFragment extends Fragment {

    private PViewModel pViewModel;

    private TextInputEditText txtLangName;
    private TextInputEditText txtLocalLang;
    private TextInputEditText txtAuthor;
    private TextInputEditText txtKerning;

    private CheckBox chkPosMandatory;
    private CheckBox chkLocalUniqueness;
    private CheckBox chkWordUniqueness;
    private CheckBox chkIgnoreCase;
    private CheckBox chkDisableOrthoRegex;
    private CheckBox chkEnforceRTL;
    private CheckBox chkLocalMandatory;
    private CheckBox chkOverrideRegexFont;
    private CheckBox chkLocalWordLexiconDisplay;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        pViewModel = new ViewModelProvider(requireActivity()).get(PViewModel.class);
        View root = inflater.inflate(R.layout.fragment_lang_properties, container, false);

        txtLangName = root.findViewById(R.id.txtLangName);
        txtLocalLang = root.findViewById(R.id.txtLocalLang);
        txtAuthor = root.findViewById(R.id.txtAuthor);
        txtKerning = root.findViewById(R.id.txtKerning);

        chkPosMandatory = root.findViewById(R.id.chkPosMandatory);
        chkLocalUniqueness = root.findViewById(R.id.chkLocalUniqueness);
        chkWordUniqueness = root.findViewById(R.id.chkWordUniqueness);
        chkIgnoreCase = root.findViewById(R.id.chkIgnoreCase);
        chkDisableOrthoRegex = root.findViewById(R.id.chkDisableOrthoRegex);
        chkEnforceRTL = root.findViewById(R.id.chkEnforceRTL);
        chkLocalMandatory = root.findViewById(R.id.chkLocalMandatory);
        chkOverrideRegexFont = root.findViewById(R.id.chkOverrideRegexFont);
        chkLocalWordLexiconDisplay = root.findViewById(R.id.chkLocalWordLexiconDisplay);

        pViewModel.getLiveCore().observe(getViewLifecycleOwner(), new Observer<DictCore>() {
            @Override
            public void onChanged(@Nullable DictCore core) {
                if (core != null) {
                    PropertiesManager manager = core.getPropertiesManager();
                    txtLangName.setText(manager.getLangName());
                    txtLocalLang.setText(manager.getLocalLangName());
                    txtAuthor.setText(manager.getCopyrightAuthorInfo());
                    txtKerning.setText(Double.toString(manager.getKerningSpace()));

                    chkPosMandatory.setChecked(manager.isTypesMandatory());
                    chkLocalUniqueness.setChecked(manager.isLocalUniqueness());
                    chkWordUniqueness.setChecked(manager.isWordUniqueness());
                    chkIgnoreCase.setChecked(manager.isIgnoreCase());
                    chkDisableOrthoRegex.setChecked(manager.isDisableProcRegex());
                    chkEnforceRTL.setChecked(manager.isEnforceRTL());
                    chkLocalMandatory.setChecked(manager.isLocalMandatory());
                    chkOverrideRegexFont.setChecked(manager.isOverrideRegexFont());
                    chkLocalWordLexiconDisplay.setChecked(manager.isUseLocalWordLex());
                }
            }
        });

        return root;
    }

    @Override
    public void onPause() {
        super.onPause();
        saveProperties();
    }

    public void saveProperties() {
        DictCore core = pViewModel.getLiveCore().getValue();
        PropertiesManager manager = core.getPropertiesManager();
        manager.setLangName(txtLangName.getText().toString());
        manager.setLocalLangName(txtLocalLang.getText().toString());
        manager.setCopyrightAuthorInfo(txtAuthor.getText().toString());
        manager.setKerningSpace(Double.parseDouble(txtKerning.getText().toString()));

        manager.setTypesMandatory(chkPosMandatory.isChecked());
        manager.setLocalUniqueness(chkLocalUniqueness.isChecked());
        manager.setWordUniqueness(chkWordUniqueness.isChecked());
        manager.setIgnoreCase(chkIgnoreCase.isChecked());
        manager.setDisableProcRegex(chkDisableOrthoRegex.isChecked());
        manager.setEnforceRTL(chkEnforceRTL.isChecked());
        manager.setLocalMandatory(chkLocalMandatory.isChecked());
        manager.setOverrideRegexFont(chkOverrideRegexFont.isChecked());
        manager.setUseLocalWordLex(chkLocalWordLexiconDisplay.isChecked());
    }
}