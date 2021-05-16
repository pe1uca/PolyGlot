package org.darisadesigns.polyglotlina.android.ui.Lexicon;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.darisadesigns.polyglotlina.Nodes.ConWord;

public class LexemeInfoViewModel extends ViewModel {

    private final MutableLiveData<ConWord> liveWord = new MutableLiveData<>();

    public void updateWord(ConWord word) {
        this.liveWord.setValue(word);
    }

    public LiveData<ConWord> getLiveWord() {
        return this.liveWord;
    }
}