package org.darisadesigns.polyglotlina.android.ui;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.darisadesigns.polyglotlina.DictCore;
import org.darisadesigns.polyglotlina.android.AndroidPropertiesManager;
import org.darisadesigns.polyglotlina.android.R;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class BaseRecyclerViewAdapter<T extends BaseRecyclerViewAdapter.Items> extends RecyclerView.Adapter<BaseRecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "BaseRecyclerViewAdapter";

    protected final DictCore core;
    protected List<T> items;
    protected final List<T> itemsBackup;
    protected final BaseRecyclerViewAdapter.OnItemClickListener<T> listener;

    public BaseRecyclerViewAdapter(DictCore core, List<T> items, BaseRecyclerViewAdapter.OnItemClickListener<T> listener) {
        this.core = core;
        this.items = items;
        this.itemsBackup = new ArrayList<>(items);
        this.listener = listener;
    }

    @NonNull
    @Override
    public BaseRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_base_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final BaseRecyclerViewAdapter.ViewHolder holder, int position) {
        holder.vDisplay.setText(items.get(position).getDisplay());
        holder.vGloss.setText(items.get(position).getGloss());
        holder.itemView.setOnClickListener(v -> listener.onItemClick(items.get(position)));
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
        this.items = this.itemsBackup.stream().filter(item -> {
            if (core.getPropertiesManager().isIgnoreCase())
                return patternI.matcher(item.getDisplay()).matches() ||
                        patternI.matcher(item.getGloss()).matches();
            return pattern.matcher(item.getDisplay()).matches() ||
                    pattern.matcher(item.getGloss()).matches();
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

    public interface Items {
        String getDisplay();
        String getGloss();
    }

    public interface OnItemClickListener<T> {
        void onItemClick(T item);
    }
}