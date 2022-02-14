package org.darisadesigns.polyglotlina.android.ui.PartsOfSpeech;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.darisadesigns.polyglotlina.DictCore;
import org.darisadesigns.polyglotlina.Nodes.TypeNode;
import org.darisadesigns.polyglotlina.android.R;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class POSRecyclerViewAdapter extends RecyclerView.Adapter<POSRecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "POSRecyclerViewAdapter";

    private final DictCore core;
    private List<TypeNode> items;
    private final List<TypeNode> itemsBackup;
    private final POSRecyclerViewAdapter.OnItemClickListener listener;

    public POSRecyclerViewAdapter(DictCore core, List<TypeNode> items, POSRecyclerViewAdapter.OnItemClickListener listener) {
        this.core = core;
        this.items = items;
        this.itemsBackup = new ArrayList<>(items);
        this.listener = listener;
    }

    @NonNull
    @Override
    public POSRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_base_row, parent, false);
        return new POSRecyclerViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final POSRecyclerViewAdapter.ViewHolder holder, int position) {
        TypeNode pos = items.get(position);
        holder.vDisplay.setText(pos.getValue());
        holder.vGloss.setText(pos.getGloss());
        holder.itemView.setOnClickListener(v -> listener.onItemClick(pos));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void filter(String filterText) {
        this.items.clear();
        if (filterText.isEmpty()) {
            this.items.addAll(this.itemsBackup);
            this.notifyDataSetChanged();
            return;
        }
        String regex = ".*" + filterText + ".*";
        Pattern pattern = Pattern.compile(regex);
        Pattern patternI = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        this.items = this.itemsBackup.stream().filter(type -> {
            if (core.getPropertiesManager().isIgnoreCase())
                return patternI.matcher(type.getValue()).matches() ||
                        patternI.matcher(type.getGloss()).matches();
            return pattern.matcher(type.getValue()).matches() ||
                    pattern.matcher(type.getGloss()).matches();
        }).collect(Collectors.toList());
        this.notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView vDisplay;
        public final TextView vGloss;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            vDisplay = view.findViewById(R.id.display_text_view);
            vGloss = view.findViewById(R.id.gloss_text_view);
        }

        @NonNull
        @Override
        public String toString() {
            return super.toString() + " '" + vDisplay.getText() + "'";
        }
    }

    public interface OnItemClickListener {
        void onItemClick(TypeNode item);
    }
}