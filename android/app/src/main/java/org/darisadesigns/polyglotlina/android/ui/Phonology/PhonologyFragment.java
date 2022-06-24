package org.darisadesigns.polyglotlina.android.ui.Phonology;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import org.darisadesigns.polyglotlina.android.R;

import java.util.ArrayList;
import java.util.List;

public class PhonologyFragment extends Fragment {

    List<View> layouts = new ArrayList<>();
    List<ImageButton> arrows = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_phonology, container, false);

        LinearLayout phonologyLayout = view.findViewById(R.id.phonologyLayout);
        LinearLayout romanizationLayout = view.findViewById(R.id.romanizationLayout);
        LinearLayout replacementLayout = view.findViewById(R.id.replacementLayout);
        ImageButton phonologyArrow = view.findViewById(R.id.phonology_arrow_button);
        ImageButton romanizationArrow = view.findViewById(R.id.romanization_arrow_button);
        ImageButton replacementArrow = view.findViewById(R.id.replacement_arrow_button);

        layouts.add(phonologyLayout);
        layouts.add(romanizationLayout);
        layouts.add(replacementLayout);
        arrows.add(phonologyArrow);
        arrows.add(romanizationArrow);
        arrows.add(replacementArrow);

        phonologyArrow.setOnClickListener(v -> handleExpandable(phonologyArrow, phonologyLayout));
        romanizationArrow.setOnClickListener(v -> handleExpandable(romanizationArrow, romanizationLayout));
        replacementArrow.setOnClickListener(v -> handleExpandable(replacementArrow, replacementLayout));
        return view;
    }

    private void handleExpandable(ImageButton source, View expandable) {
        if (expandable.getVisibility() == View.VISIBLE) {
            expandable.setVisibility(View.GONE);
            source.setImageResource(R.drawable.ic_baseline_expand_more_24);
        }
        else {
            layouts.forEach((view -> {
                view.setVisibility(View.GONE);
            }));
            arrows.forEach(arrow -> {
                arrow.setImageResource(R.drawable.ic_baseline_expand_more_24);
            });
            expandable.setVisibility(View.VISIBLE);
            source.setImageResource(R.drawable.ic_baseline_expand_less_24);
        }
    }
}