package org.darisadesigns.polyglotlina.android.ui.PartsOfSpeech.Conjugations;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.darisadesigns.polyglotlina.DictCore;
import org.darisadesigns.polyglotlina.Nodes.ConjugationDimension;
import org.darisadesigns.polyglotlina.Nodes.ConjugationGenRule;
import org.darisadesigns.polyglotlina.android.R;

import java.util.List;

public class ConjugationRuleRecyclerViewAdapter extends RecyclerView.Adapter<ConjugationRuleRecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "ConjugationRuleRecyclerViewAdapter";

    private final DictCore core;
    private List<ConjugationGenRule> items;
    private final ConjugationRuleRecyclerViewAdapter.OnItemClickListener listener;
    private int selectedPos = RecyclerView.NO_POSITION;

    public ConjugationRuleRecyclerViewAdapter(DictCore core, List<ConjugationGenRule> items, ConjugationRuleRecyclerViewAdapter.OnItemClickListener listener) {
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
        holder.itemView.setSelected(selectedPos == position);
        ConjugationGenRule genRule = items.get(position);
        holder.vDisplay.setText(genRule.getName());
        holder.itemView.setOnClickListener(v -> {
            notifyItemChanged(selectedPos);
            selectedPos = holder.getAbsoluteAdapterPosition();
            notifyItemChanged(selectedPos);
            listener.onItemClick(genRule);
        });
        holder.vDelete.setOnClickListener(v -> listener.onItemDeleteClick(genRule));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setSelectedPos(int selectedPos) {
        this.selectedPos = selectedPos;
        notifyItemChanged(selectedPos);
    }

    public int getSelectedPos(){
        return this.selectedPos;
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
        void onItemClick(ConjugationGenRule item);
        void onItemDeleteClick(ConjugationGenRule item);
    }
}