package org.darisadesigns.polyglotlina.android.ui.PartsOfSpeech.Conjugations;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.darisadesigns.polyglotlina.DictCore;
import org.darisadesigns.polyglotlina.Nodes.ConjugationNode;
import org.darisadesigns.polyglotlina.android.R;

import java.util.List;

public class POSConjugationsRecyclerViewAdapter extends RecyclerView.Adapter<POSConjugationsRecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "POSRecyclerViewAdapter";

    private final DictCore core;
    private List<ConjugationNode> items;
    private final POSConjugationsRecyclerViewAdapter.OnItemClickListener listener;

    public POSConjugationsRecyclerViewAdapter(DictCore core, List<ConjugationNode> items, POSConjugationsRecyclerViewAdapter.OnItemClickListener listener) {
        this.core = core;
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public POSConjugationsRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_text_delete_row, parent, false);
        return new POSConjugationsRecyclerViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final POSConjugationsRecyclerViewAdapter.ViewHolder holder, int position) {
        ConjugationNode conjugationNode = items.get(position);
        holder.vDisplay.setText(conjugationNode.getValue());
        holder.itemView.setOnClickListener(v -> listener.onItemClick(conjugationNode));
        holder.vDelete.setOnClickListener(v -> listener.onItemDeleteClick(conjugationNode));
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
        void onItemClick(ConjugationNode item);
        void onItemDeleteClick(ConjugationNode item);
    }
}