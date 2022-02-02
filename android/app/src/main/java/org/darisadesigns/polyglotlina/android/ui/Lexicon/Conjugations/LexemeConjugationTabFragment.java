package org.darisadesigns.polyglotlina.android.ui.Lexicon.Conjugations;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.evrencoskun.tableview.TableView;

import org.darisadesigns.polyglotlina.DictCore;
import org.darisadesigns.polyglotlina.ManagersCollections.ConjugationManager;
import org.darisadesigns.polyglotlina.Nodes.ConWord;
import org.darisadesigns.polyglotlina.Nodes.ConjugationDimension;
import org.darisadesigns.polyglotlina.Nodes.ConjugationNode;
import org.darisadesigns.polyglotlina.Nodes.ConjugationPair;
import org.darisadesigns.polyglotlina.android.PolyGlot;
import org.darisadesigns.polyglotlina.android.R;
import org.darisadesigns.polyglotlina.android.ui.Table.Cell;
import org.darisadesigns.polyglotlina.android.ui.Table.ColumnHeader;
import org.darisadesigns.polyglotlina.android.ui.Table.RowHeader;
import org.darisadesigns.polyglotlina.android.ui.Table.TableViewAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class LexemeConjugationTabFragment extends Fragment {

    private static final String TAG = "ConjugationTab";
    private static final String ARG_PARTIAL_CONJUGATION = "partial-conjugation";
    private String partialConjugationId;
    private ConjugationViewModel viewModel;
    private DictCore core;
    private ConjugationManager conjugationManager;
    private String tabName = "";

    private TableView tableView;
    private TableViewAdapter tableViewAdapter;

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

        tableView = root.findViewById(R.id.conjugationsTableView);

        tableViewAdapter = new TableViewAdapter(core);
        tableView.setAdapter(tableViewAdapter);

        viewModel.getLiveConjugation().observe(getViewLifecycleOwner(), new Observer<ConjugationViewModel.Conjugation>() {
            @Override
            public void onChanged(ConjugationViewModel.Conjugation conjugation) {
                if(null != conjugation) {
                    if (null != conjugation.conjugationColumn && null != conjugation.conjugationRow) {
                        generateTabName(conjugation.conWord, partialConjugationId, conjugationManager);
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

    public void generateTabName(ConWord conWord, String partialConjugationId, ConjugationManager conjugationManager) {
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
        if (tabName.trim().isEmpty()) tabName = "Declensions/Conjugations";
    }

    public String getTabName() {
        return this.tabName;
    }

    private void conjugateSimple(ConjugationViewModel.Conjugation conjugation) {
        ConjugationPair[] completeList = conjugationManager.getAllCombinedIds(conjugation.conWord.getWordTypeId());
        List<List<Cell>> cells = new ArrayList<>();
        List<RowHeader> rowHeaders = new ArrayList<>();
        List<ColumnHeader> columnHeaders = new ArrayList<>();
        columnHeaders.add(new ColumnHeader("Value"));
        for(ConjugationPair pair: completeList) {
            String wordForm = conjugation.conWord.getWordForm(pair.combinedId);
            rowHeaders.add(new RowHeader(pair.label));
            List<Cell> row = new ArrayList<>();
            row.add(new Cell(wordForm));
            cells.add(row);
        }
        tableViewAdapter.setCornerText("Wordform");
        tableViewAdapter.setAllItems(columnHeaders, rowHeaders, cells);
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
        List<String> columnLabels = getLabels(columnsNode);
        List<String> rowLabels = getLabels(rowsNode);
        List<ColumnHeader> columnHeaders = ColumnHeader.getColumnHeaderList(columnLabels);
        List<RowHeader> rowHeaders = RowHeader.getRowHeaderList(rowLabels);
        List<List<Cell>> cells = new ArrayList<>();

        populateTableValues(cells, conjugation);
        tableViewAdapter.setAllItems(columnHeaders, rowHeaders, cells);
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

    private List<String> getLabels(ConjugationNode node) {
        List<String> labels = new ArrayList<>();

        node.getDimensions().forEach((dimension)->{
            labels.add(dimension.getValue());
        });

        return labels;
    }

    private void populateTableValues(List<List<Cell>> cells, ConjugationViewModel.Conjugation conjugation) {

        Iterator<ConjugationDimension> rowsIterator = conjugation.conjugationRow.getDimensions().iterator();
        for (int rowPos = 0; rowsIterator.hasNext(); rowPos++) {
            ConjugationDimension conjugationDimensionRow = rowsIterator.next();
            Iterator<ConjugationDimension> columnsIterator = conjugation.conjugationColumn.getDimensions().iterator();
            List<Cell> rowCells = new ArrayList<>();
            for (int columnPos = 0; columnsIterator.hasNext(); columnPos++) {
                ConjugationDimension conjugationDimensionColumn = columnsIterator.next();
                String fullDecId = partialConjugationId.replace("Y", conjugationDimensionColumn.getId().toString());
                fullDecId = fullDecId.replace("X", conjugationDimensionRow.getId().toString());
                if (conjugationManager.isCombinedConjlSurpressed(fullDecId, conjugation.conWord.getWordTypeId())) {
                    rowCells.add(new Cell(""));
                }
                else {
                    String wordForm = conjugation.conWord.getWordForm(fullDecId);
                    rowCells.add(new Cell(wordForm));
                }
            }
            cells.add(rowCells);
        }
    }
}
