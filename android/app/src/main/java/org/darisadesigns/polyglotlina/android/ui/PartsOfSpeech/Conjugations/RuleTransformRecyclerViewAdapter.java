package org.darisadesigns.polyglotlina.android.ui.PartsOfSpeech.Conjugations;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.darisadesigns.polyglotlina.DictCore;
import org.darisadesigns.polyglotlina.Nodes.ConjugationGenTransform;
import org.darisadesigns.polyglotlina.android.AndroidPropertiesManager;
import org.darisadesigns.polyglotlina.android.R;

import java.util.List;

public class RuleTransformRecyclerViewAdapter extends RecyclerView.Adapter<RuleTransformRecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "RuleTransformRecyclerViewAdapter";

    private final DictCore core;
    private final List<ConjugationGenTransform> items;
    private final RuleTransformRecyclerViewAdapter.OnItemClickListener listener;

    public RuleTransformRecyclerViewAdapter(DictCore core, List<ConjugationGenTransform> items, RuleTransformRecyclerViewAdapter.OnItemClickListener listener) {
        this.core = core;
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RuleTransformRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_inputs_delete_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        ConjugationGenTransform transform = items.get(position);
        holder.vTxtLeft.setText(transform.regex);
        ((AndroidPropertiesManager)core.getPropertiesManager()).setConViewTypeface(holder.vTxtLeft);
        holder.vTxtLeft.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                transform.regex = s.toString();
            }
        });
        holder.vTxtRight.setText(transform.replaceText);
        ((AndroidPropertiesManager)core.getPropertiesManager()).setConViewTypeface(holder.vTxtRight);
        holder.vTxtRight.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                transform.replaceText = s.toString();
            }
        });
        holder.vDelete.setOnClickListener(v -> listener.onItemDeleteClick(transform));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public List<ConjugationGenTransform> getItems() {
        return this.items;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView vTxtLeft;
        public final TextView vTxtRight;
        public final ImageView vDelete;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            vTxtLeft = view.findViewById(R.id.txtLeft);
            vTxtRight = view.findViewById(R.id.txtRight);
            vDelete = view.findViewById(R.id.delete_icon);
        }

        @NonNull
        @Override
        public String toString() {
            return super.toString() + " '" + vTxtLeft.getText() + "' + '" + vTxtRight.getText() + "'";
        }
    }

    public interface OnItemClickListener {
        void onItemDeleteClick(ConjugationGenTransform item);
    }
}
