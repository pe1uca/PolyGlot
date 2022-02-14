package org.darisadesigns.polyglotlina.android.ui.Lexicon;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.darisadesigns.polyglotlina.DictCore;
import org.darisadesigns.polyglotlina.Nodes.ConWord;
import org.darisadesigns.polyglotlina.Nodes.TypeNode;
import org.darisadesigns.polyglotlina.android.AndroidPropertiesManager;
import org.darisadesigns.polyglotlina.android.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class LexemeRecyclerViewAdapter extends RecyclerView.Adapter<LexemeRecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "LexemeRecyclerViewAdapter";
    private static enum FILTER_TYPE {
        CONWORD,
        LOCAL_WORD,
        DEFINITION,
        PART_OF_SPEECH,
        PRONUNCIATION
    }
    public static Pattern FILTER_STARTS_PATTERN = Pattern.compile("^(local|definition|pos|pronunciation):.*");

    private final DictCore core;
    private List<ConWord> items;
    private final List<ConWord> itemsBackup;
    private final OnItemClickListener listener;

    public LexemeRecyclerViewAdapter(DictCore core, List<ConWord> items, OnItemClickListener listener) {
        this.core = core;
        this.items = items;
        this.itemsBackup = new ArrayList<>(items);
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_base_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.conWord = items.get(position);
        holder.vConWord.setText(items.get(position).toString());
        ((AndroidPropertiesManager)holder.conWord.getCore().getPropertiesManager()).setConViewTypeface(holder.vConWord);
        holder.vLocalWord.setText(items.get(position).getLocalWord());
        holder.itemView.setOnClickListener(v -> listener.onItemClick(holder.conWord));
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
        String[] tokens = filterText.split(":");
        final FILTER_TYPE[] filterType = {FILTER_TYPE.CONWORD};
        if (tokens.length >= 2) {
            switch (tokens[0]) {
                case "local":
                    filterType[0] = FILTER_TYPE.LOCAL_WORD;
                    break;
                case "definition":
                    filterType[0] = FILTER_TYPE.DEFINITION;
                    break;
                case "pos":
                    filterType[0] = FILTER_TYPE.PART_OF_SPEECH;
                    break;
                case "pronunciation":
                    filterType[0] = FILTER_TYPE.PRONUNCIATION;
                    break;
                default:
                    filterType[0] = FILTER_TYPE.CONWORD;
            }
            filterText = filterText.replaceFirst("^(local|definition|pos|pronunciation):", "");
        }
        String regex = ".*" + filterText + ".*";
        Pattern pattern = Pattern.compile(regex);
        Pattern patternI = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        this.items = this.itemsBackup.stream().filter(new Predicate<ConWord>() {
            @Override
            public boolean test(ConWord conWord) {
                switch (filterType[0]) {
                    case LOCAL_WORD:
                        if (core.getPropertiesManager().isIgnoreCase()) {
                            return patternI.matcher(conWord.getLocalWord()).matches();
                        }
                        return pattern.matcher(conWord.getLocalWord()).matches();
                    case DEFINITION:
                        return pattern.matcher(conWord.getDefinition()).matches();
                    case PART_OF_SPEECH:
                        Optional<TypeNode> optional =  Arrays.stream(core.getTypes().getNodes())
                                .filter(type -> pattern.matcher(type.getValue()).matches() ||
                                        pattern.matcher(type.getGloss()).matches())
                                .findFirst();
                        if (!optional.isPresent()) return false;
                        return conWord.getWordTypeId().equals(optional.get().getId());
                    case PRONUNCIATION:
                        try {
                            if (core.getPropertiesManager().isIgnoreCase()) {
                                return patternI.matcher(conWord.getPronunciation()).matches();
                            }
                            return pattern.matcher(conWord.getPronunciation()).matches();
                        } catch (Exception ignored) { }
                        break;
                    default:
                        return pattern.matcher(conWord.getValue()).matches();
                }
                return false;
            }
        }).collect(Collectors.toList());
        this.notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void coreFilter(ConWord filterWord) throws Exception {
        this.items.clear();
        if (filterWord.getValue().trim().isEmpty() ||
                filterWord.getLocalWord().trim().isEmpty() ||
                filterWord.getDefinition().trim().isEmpty() ||
                filterWord.getPronunciation().trim().isEmpty()) {
            this.items.addAll(this.itemsBackup);
            this.notifyDataSetChanged();
            return;
        }
        try {
            this.items.addAll(Arrays.asList(this.core.getWordCollection().filteredList(filterWord)));
        } catch (Exception e) {
            core.getOSHandler().getIOHandler().writeErrorLog(e);
            core.getOSHandler().getInfoBox().error("Filter Error", "Unable to apply filter.\n" + e.getMessage());
        }
        this.notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView vConWord;
        public final TextView vLocalWord;
        public ConWord conWord;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            vConWord = view.findViewById(R.id.display_text_view);
            vLocalWord = view.findViewById(R.id.gloss_text_view);
        }

        @NonNull
        @Override
        public String toString() {
            return super.toString() + " '" + vConWord.getText() + "'";
        }
    }

    public interface OnItemClickListener {
        void onItemClick(ConWord item);
    }
}