package org.darisadesigns.polyglotlina.android.ui.WordClasses;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.darisadesigns.polyglotlina.DictCore;
import org.darisadesigns.polyglotlina.Nodes.WordClass;
import org.darisadesigns.polyglotlina.Nodes.WordClassValue;
import org.darisadesigns.polyglotlina.android.R;

import java.util.ArrayList;
import java.util.List;

public class ClassValuesRecyclerViewAdapter extends RecyclerView.Adapter<ClassValuesRecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "POSRecyclerViewAdapter";

    private final DictCore core;
    private List<WordClassValue> items;
    private final ClassValuesRecyclerViewAdapter.OnItemClickListener listener;

    public ClassValuesRecyclerViewAdapter(DictCore core, List<WordClassValue> items, ClassValuesRecyclerViewAdapter.OnItemClickListener listener) {
        this.core = core;
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ClassValuesRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_text_delete_row, parent, false);
        return new ClassValuesRecyclerViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ClassValuesRecyclerViewAdapter.ViewHolder holder, int position) {
        WordClassValue classValue = items.get(position);
        holder.vDisplay.setText(classValue.getValue());
        holder.itemView.setOnClickListener(v -> listener.onItemClick(classValue));
        holder.vDelete.setOnClickListener(v -> listener.onItemDeleteClick(classValue));
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
        void onItemClick(WordClassValue item);
        void onItemDeleteClick(WordClassValue item);
    }
}