package org.darisadesigns.polyglotlina.android.ui.PartsOfSpeech;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.darisadesigns.polyglotlina.DictCore;
import org.darisadesigns.polyglotlina.Nodes.ConWord;
import org.darisadesigns.polyglotlina.Nodes.TypeNode;
import org.darisadesigns.polyglotlina.android.AndroidPropertiesManager;
import org.darisadesigns.polyglotlina.android.PolyGlot;
import org.darisadesigns.polyglotlina.android.R;
import org.darisadesigns.polyglotlina.android.ui.EditorViewModel;
import org.darisadesigns.polyglotlina.android.ui.HTMLEditorFragment;
import org.darisadesigns.polyglotlina.android.ui.Lexicon.LexemeInfoViewModel;

public class POSGeneralFragment extends Fragment {

    private static final String TAG = "POSGeneralFragment";

    private POSInfoViewModel viewModel;
    private EditorViewModel editorViewModel;
    private DictCore core;

    private TextInputEditText txtPOSName;
    private TextInputEditText txtGloss;
    private TextInputEditText txtPattern;

    private CheckBox chkDefinitionMandatory;
    private CheckBox chkPronunciationMandatory;

    private TextInputLayout txtPOSNameLayout;
    private TextInputLayout txtPatternLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_pos_general, container, false);

        viewModel = new ViewModelProvider(requireActivity()).get(POSInfoViewModel.class);
        editorViewModel = new ViewModelProvider(this).get(EditorViewModel.class);

        txtPOSName = root.findViewById(R.id.txtPOSName);
        txtGloss = root.findViewById(R.id.txtGloss);
        txtPattern = root.findViewById(R.id.txtPattern);
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        HTMLEditorFragment htmlEditor = HTMLEditorFragment.newInstance(getResources().getString(R.string.label_pos_notes));
        transaction.replace(R.id.fragment_container_view, htmlEditor).commit();

        txtPOSNameLayout = root.findViewById(R.id.txtPOSNameLayout);
        txtPatternLayout = root.findViewById(R.id.txtPatternLayout);

        core = ((PolyGlot)requireActivity().getApplicationContext()).getCore();
        ((AndroidPropertiesManager)core.getPropertiesManager()).setConViewTypeface(txtPattern);

        viewModel.getLiveData().observe(getViewLifecycleOwner(), node -> {
            if (null != node) {
                txtPOSName.setText(node.getValue());
                txtGloss.setText(node.getGloss());
                txtPattern.setText(node.getPattern());
                editorViewModel.updateText(node.getNotes());

                chkDefinitionMandatory.setChecked(node.isDefMandatory());
                chkPronunciationMandatory.setChecked(node.isProcMandatory());
            }
        });
        return root;
    }

    public void savePOS() {
        TypeNode node = viewModel.getLiveData().getValue();
        node.setValue(txtPOSName.getText().toString());
        node.setGloss(txtGloss.getText().toString());
        node.setPattern(txtPattern.getText().toString(), core);
        node.setNotes(editorViewModel.getLiveText().getValue());

        node.setDefMandatory(chkDefinitionMandatory.isChecked());
        node.setProcMandatory(chkPronunciationMandatory.isChecked());
    }


    public boolean isDataValid() {
        txtPOSNameLayout.setError(null);
        if (!txtPOSName.getText().toString().isEmpty()) return true;
        txtPOSNameLayout.setError(getString(R.string.error_pos_name_empty));
        return false;
    }
}