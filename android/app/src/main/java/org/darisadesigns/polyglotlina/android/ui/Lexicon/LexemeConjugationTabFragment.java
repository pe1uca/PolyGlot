package org.darisadesigns.polyglotlina.android.ui.Lexicon;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import org.darisadesigns.polyglotlina.DictCore;
import org.darisadesigns.polyglotlina.ManagersCollections.ConjugationManager;
import org.darisadesigns.polyglotlina.Nodes.ConWord;
import org.darisadesigns.polyglotlina.Nodes.ConjugationDimension;
import org.darisadesigns.polyglotlina.Nodes.ConjugationNode;
import org.darisadesigns.polyglotlina.Nodes.ConjugationPair;
import org.darisadesigns.polyglotlina.android.PolyGlot;
import org.darisadesigns.polyglotlina.android.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import ir.androidexception.datatable.DataTable;
import ir.androidexception.datatable.model.DataTableHeader;
import ir.androidexception.datatable.model.DataTableRow;

public class LexemeConjugationTabFragment extends Fragment {

    private static final String TAG = "ConjugationTab";
    private static final String ARG_PARTIAL_CONJUGATION = "partial-conjugation";
    private String partialConjugationId;
    private ConjugationViewModel viewModel;
    private DictCore core;
    private ConjugationManager conjugationManager;
    private String tabName = "";

    private DataTable table;

    public static LexemeConjugationTabFragment newInstance(String partialConjugation) {
        LexemeConjugationTabFragment fragment = new LexemeConjugationTabFragment();

        Bundle bundle = new Bundle();
        bundle.putString(ARG_PARTIAL_CONJUGATION, partialConjugation);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_data_table, container, false);
        partialConjugationId = getArguments().getString(ARG_PARTIAL_CONJUGATION);
        viewModel = new ViewModelProvider(requireActivity()).get(partialConjugationId, ConjugationViewModel.class);

        PolyGlot polyGlot = (PolyGlot)requireActivity().getApplicationContext();
        core = polyGlot.getCore();
        conjugationManager = core.getConjugationManager();

        table = root.findViewById(R.id.conjugationsTable);

        viewModel.getLiveConjugation().observe(getViewLifecycleOwner(), new Observer<ConjugationViewModel.Conjugation>() {
            @Override
            public void onChanged(ConjugationViewModel.Conjugation conjugation) {
                if(null != conjugation) {
                    if (null != conjugation.conjugationColumn && null != conjugation.conjugationRow) {
                        generateTabName(conjugation.conWord);
                        conjugateDimensional(conjugation);
                    }
                    else {
                        conjugateSimple(conjugation);
                    }
                }
            }
        });

        return root;
    }

    private void generateTabName(ConWord conWord) {
        String[] dimArray = partialConjugationId.split(",");
        dimArray = Arrays.copyOfRange(dimArray, 1, dimArray.length); // first value always empty

        tabName = "";

        for (int i = 0; i < dimArray.length; i++) {
            String curId = dimArray[i];
            ConjugationNode node = conjugationManager.getDimensionalConjugationTemplateByIndex(conWord.getWordTypeId(), i);
            // skips X and Y elements
            if (TextUtils.isDigitsOnly(curId) && !node.isDimensionless()) {
                tabName += node.getConjugationDimensionById(Integer.parseInt(curId)).getValue() + " ";
            }
        }
    }

    public String getTabName() {
        return this.tabName.trim().isEmpty() ? "Declensions/Conjugations" : this.tabName;
    }

    private void conjugateSimple(ConjugationViewModel.Conjugation conjugation) {
        ConjugationPair[] completeList = conjugationManager.getAllCombinedIds(conjugation.conWord.getWordTypeId());
        DataTableHeader header = new DataTableHeader.Builder()
                .item("Wordform", 1)
                .item("Value", 1)
                .build();
        ArrayList<DataTableRow> tableRows = new ArrayList<>();
        for(ConjugationPair pair: completeList) {
            String wordForm = conjugation.conWord.getWordForm(pair.combinedId);
            Log.e(TAG, wordForm);
            tableRows.add(
                    new DataTableRow.Builder()
                            .value(pair.label)
                            .value(wordForm)
                            .build()
            );
        }
        createTable(header, tableRows);
    }

    private void conjugateDimensional(ConjugationViewModel.Conjugation conjugation) {
        buildModel(conjugation);
    }

    private void buildModel(ConjugationViewModel.Conjugation conjugation) {
        int rowsIndex = getDeclensionIndexOf(partialConjugationId, "X");
        int columnsIndex = getDeclensionIndexOf(partialConjugationId, "Y");
        ConjugationNode columnsNode = conjugationManager
                .getDimensionalConjugationTemplateByIndex(
                        conjugation.conWord.getWordTypeId(),
                        columnsIndex);
        ConjugationNode rowsNode = conjugationManager
                .getDimensionalConjugationTemplateByIndex(
                        conjugation.conWord.getWordTypeId(),
                        rowsIndex);
        List<String> columnLabels = getLabels(columnsNode, true);
        List<String> rowLabels = getLabels(rowsNode, false);
        ArrayList<DataTableRow> tableRows = new ArrayList<>();

        DataTableHeader.Builder headerBuilder = new DataTableHeader.Builder();
        columnLabels.forEach((label) -> {
            headerBuilder.item(label, 1);
        });

        populateTableValues(rowLabels, tableRows, conjugation);
        createTable(headerBuilder.build(), tableRows);
    }

    private int getDeclensionIndexOf(String partialDeclensionsId, String marker) {
        String[] dimArray = partialDeclensionsId.split(",");
        dimArray = Arrays.copyOfRange(dimArray, 1, dimArray.length); // first value always empty

        int ret = -1;

        for (int i = 0; i < dimArray.length; i++) {
            if (dimArray[i].equals(marker)) {
                ret = i;
                break;
            }
        }

        return ret;
    }

    private List<String> getLabels(ConjugationNode node, boolean skipFirst) {
        List<String> labels = new ArrayList<>();

        if (skipFirst) {
            labels.add("");
        }

        node.getDimensions().forEach((dimension)->{
            labels.add(dimension.getValue());
        });

        return labels;
    }

    private void populateTableValues(List<String> rowLabels, ArrayList<DataTableRow> tableRows, ConjugationViewModel.Conjugation conjugation) {

        Iterator<ConjugationDimension> rowsIterator = conjugation.conjugationRow.getDimensions().iterator();
        for (int rowPos = 0; rowsIterator.hasNext(); rowPos++) {
            ConjugationDimension conjugationDimensionRow = rowsIterator.next();
            Iterator<ConjugationDimension> columnsIterator = conjugation.conjugationColumn.getDimensions().iterator();
            DataTableRow.Builder rowBuilder = new DataTableRow.Builder();
            rowBuilder.value(rowLabels.get(rowPos));
            for (int columnPos = 0; columnsIterator.hasNext(); columnPos++) {
                ConjugationDimension conjugationDimensionColumn = columnsIterator.next();
                String fullDecId = partialConjugationId.replace("Y", conjugationDimensionColumn.getId().toString());
                fullDecId = fullDecId.replace("X", conjugationDimensionRow.getId().toString());
                if (conjugationManager.isCombinedConjlSurpressed(fullDecId, conjugation.conWord.getWordTypeId())) {
                    rowBuilder.value("");
                }
                else {
                    String wordForm = conjugation.conWord.getWordForm(fullDecId);
                    rowBuilder.value(wordForm);
                }
            }
            tableRows.add(rowBuilder.build());
        }
    }

    private void createTable(DataTableHeader header, ArrayList<DataTableRow> tableRows) {
        table.setHeader(header);
        table.inflate(requireActivity());
        table.setRows(tableRows);
    }
}
