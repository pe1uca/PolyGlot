package org.darisadesigns.polyglotlina.android.ui.Lexicon;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.darisadesigns.polyglotlina.Nodes.ConWord;
import org.darisadesigns.polyglotlina.android.AndroidPropertiesManager;
import org.darisadesigns.polyglotlina.android.R;

import java.util.List;

public class LexemeRecyclerViewAdapter extends RecyclerView.Adapter<LexemeRecyclerViewAdapter.ViewHolder> {

    private final List<ConWord> items;
    private final OnItemClickListener listener;

    public LexemeRecyclerViewAdapter(List<ConWord> items, OnItemClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_lexeme_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.conWord = items.get(position);
        holder.vConWord.setText(items.get(position).toString());
        ((AndroidPropertiesManager)holder.conWord.getCore().getPropertiesManager()).setConViewTypeface(holder.vConWord);
        holder.vLocalWord.setText(items.get(position).getLocalWord());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(holder.conWord);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView vConWord;
        public final TextView vLocalWord;
        public ConWord conWord;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            vConWord = (TextView) view.findViewById(R.id.lexeme_con_word);
            vLocalWord = (TextView) view.findViewById(R.id.lexeme_local_word);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + vConWord.getText() + "'";
        }
    }

    public interface OnItemClickListener {
        void onItemClick(ConWord item);
    }
}