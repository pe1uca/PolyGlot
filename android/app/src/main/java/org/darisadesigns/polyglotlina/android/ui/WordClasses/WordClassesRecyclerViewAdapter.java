package org.darisadesigns.polyglotlina.android.ui.WordClasses;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.darisadesigns.polyglotlina.DictCore;
import org.darisadesigns.polyglotlina.Nodes.WordClass;
import org.darisadesigns.polyglotlina.android.R;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class WordClassesRecyclerViewAdapter extends RecyclerView.Adapter<WordClassesRecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "POSRecyclerViewAdapter";

    private final DictCore core;
    private List<WordClass> items;
    private final List<WordClass> itemsBackup;
    private final WordClassesRecyclerViewAdapter.OnItemClickListener listener;

    public WordClassesRecyclerViewAdapter(DictCore core, List<WordClass> items, WordClassesRecyclerViewAdapter.OnItemClickListener listener) {
        this.core = core;
        this.items = items;
        this.itemsBackup = new ArrayList<>(items);
        this.listener = listener;
    }

    @NonNull
    @Override
    public WordClassesRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_text_delete_row, parent, false);
        return new WordClassesRecyclerViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final WordClassesRecyclerViewAdapter.ViewHolder holder, int position) {
        WordClass pos = items.get(position);
        holder.vDisplay.setText(pos.getValue());
        holder.itemView.setOnClickListener(v -> listener.onItemClick(pos));
        holder.vDelete.setOnClickListener(v -> listener.onItemDeleteClick(pos));
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
        void onItemClick(WordClass item);
        void onItemDeleteClick(WordClass item);
    }
}