package org.darisadesigns.polyglotlina.android.ui.PartsOfSpeech.Conjugations;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.darisadesigns.polyglotlina.DictCore;
import org.darisadesigns.polyglotlina.Nodes.ConjugationNode;
import org.darisadesigns.polyglotlina.Nodes.TypeNode;
import org.darisadesigns.polyglotlina.android.AndroidInfoBox;
import org.darisadesigns.polyglotlina.android.PolyGlot;
import org.darisadesigns.polyglotlina.android.R;
import org.darisadesigns.polyglotlina.android.ui.PartsOfSpeech.POSInfoViewModel;

import java.util.Arrays;
import java.util.List;

public class POSConjugationsListFragment extends Fragment implements POSConjugationsRecyclerViewAdapter.OnItemClickListener {

    private static final String TAG = "POSConjugationsListFragment";

    private RecyclerView conjugationsView;
    private POSInfoViewModel viewModel;

    private DictCore core;
    private TypeNode typeNode;
    private View addButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_pos_conjugations_list, container, false);

        viewModel = new ViewModelProvider(requireActivity()).get(POSInfoViewModel.class);

        conjugationsView = root.findViewById(R.id.conjugationsList);
        Context context = root.getContext();
        conjugationsView.setLayoutManager(new LinearLayoutManager(context));

        RecyclerView.ItemDecoration itemDecoration = new
                DividerItemDecoration(context, DividerItemDecoration.VERTICAL);
        conjugationsView.addItemDecoration(itemDecoration);

        core = ((PolyGlot)requireActivity().getApplicationContext()).getCore();

        addButton = inflater.inflate(R.layout.fragment_base_row, conjugationsView, false);
        ((TextView)addButton.findViewById(R.id.display_text_view)).setText("Add conjugation/declension");
        ((TextView)addButton.findViewById(R.id.display_text_view)).setGravity(Gravity.CENTER);
        addButton.findViewById(R.id.gloss_text_view).setVisibility(View.GONE);
        ((LinearLayout)root.findViewById(R.id.add_row)).addView(addButton);

        addButton.setOnClickListener(v -> {
            handleAdd();
        });

        viewModel.getLiveData().observe(getViewLifecycleOwner(), node -> {
            if (null != node) {
                this.typeNode = node;
                updateConjugationsList();
            }
        });
        return root;
    }

    @Override
    public void onItemClick(ConjugationNode item) {
        Intent conjugationsIntent = new Intent(requireActivity(), POSConjugationInfoActivity.class);
        conjugationsIntent.putExtra(POSConjugationInfoActivity.POS_ID_EXTRA, typeNode.getId());
        conjugationsIntent.putExtra(POSConjugationInfoActivity.CONJUGATION_ID_EXTRA, item.getId());
        startActivity(conjugationsIntent);
    }

    @Override
    public void onItemDeleteClick(ConjugationNode item) {
        confirmDeprecate(((dialog, which) -> {
            if (which != DialogInterface.BUTTON_POSITIVE) return;
            core.getConjugationManager().deprecateAllConjugations(typeNode.getId());
            core.getConjugationManager().deleteConjugationFromTemplate(typeNode.getId(), item.getId());
            updateConjugationsList();
        }));
    }

    private void updateConjugationsList() {
        List<ConjugationNode> nodes = Arrays.asList(core.getConjugationManager().getFullConjugationListTemplate(typeNode.getId()));
        POSConjugationsRecyclerViewAdapter adapter = new POSConjugationsRecyclerViewAdapter(core, nodes, this);
        conjugationsView.setAdapter(adapter);

        setRecyclerViewMinHeight(nodes);
    }

    private void setRecyclerViewMinHeight(List<ConjugationNode> nodes) {
        ViewGroup.LayoutParams layoutParams = conjugationsView.getLayoutParams();
        layoutParams.height = (int)(3.6 * addButton.getHeight());
        if (nodes.size() < 4) {
            layoutParams.height = nodes.size() * addButton.getHeight();
        }
    }

    private void confirmDeprecate(DialogInterface.OnClickListener onClickListener) {
        ((AndroidInfoBox)core.getOSHandler().getInfoBox()).yesNoCancel(
                "Confirm action",
                "This action will deprecate all currently filled out"
                        + " declensions/conjugations (they won't be lost, but set to a deprecated status).\nContinue?",
                requireActivity(),
                onClickListener
        );
    }

    private void handleAdd() {
        this.confirmDeprecate((dialog, which) -> {
            if (which != DialogInterface.BUTTON_POSITIVE) return;
            ((AndroidInfoBox)core.getOSHandler().getInfoBox()).stringInputDialog(
                    "New conjugation/declension",
                    "What's the name for the new conjugation/declension?",
                    "Name",
                    requireActivity(),
                    s -> {
                        if (null != s && !s.isEmpty()) deprecateAndAdd(s);
                    }
            );
        });
    }

    private void deprecateAndAdd(String name) {
        core.getConjugationManager().deprecateAllConjugations(typeNode.getId());

        core.getConjugationManager().addConjugationToTemplate(typeNode.getId(), name);
        updateConjugationsList();
    }
}