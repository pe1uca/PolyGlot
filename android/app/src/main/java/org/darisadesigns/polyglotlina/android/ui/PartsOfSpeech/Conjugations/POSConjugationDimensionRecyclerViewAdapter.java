package org.darisadesigns.polyglotlina.android.ui.PartsOfSpeech.Conjugations;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.darisadesigns.polyglotlina.DictCore;
import org.darisadesigns.polyglotlina.Nodes.ConjugationDimension;
import org.darisadesigns.polyglotlina.Nodes.ConjugationNode;
import org.darisadesigns.polyglotlina.android.R;

import java.util.List;

public class POSConjugationDimensionRecyclerViewAdapter extends RecyclerView.Adapter<POSConjugationDimensionRecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "POSRecyclerViewAdapter";

    private final DictCore core;
    private List<ConjugationDimension> items;
    private final POSConjugationDimensionRecyclerViewAdapter.OnItemClickListener listener;

    public POSConjugationDimensionRecyclerViewAdapter(DictCore core, List<ConjugationDimension> items, POSConjugationDimensionRecyclerViewAdapter.OnItemClickListener listener) {
        this.core = core;
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_text_delete_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        ConjugationDimension conjugationDimension = items.get(position);
        holder.vDisplay.setText(conjugationDimension.getValue());
        holder.itemView.setOnClickListener(v -> listener.onItemClick(conjugationDimension));
        holder.vDelete.setOnClickListener(v -> listener.onItemDeleteClick(conjugationDimension));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView vDisplay;
        public final ImageView vDelete;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            vDisplay = view.findViewById(R.id.display_text_view);
            vDelete = view.findViewById(R.id.delete_icon);
        }

        @NonNull
        @Override
        public String toString() {
            return super.toString() + " '" + vDisplay.getText() + "'";
        }
    }

    public interface OnItemClickListener {
        void onItemClick(ConjugationDimension item);
        void onItemDeleteClick(ConjugationDimension item);
    }
}