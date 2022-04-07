package org.darisadesigns.polyglotlina.android.ui.PartsOfSpeech.Conjugations;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.darisadesigns.polyglotlina.Nodes.ConjugationGenRule;

public class AutogenRulesViewModel extends ViewModel {

    private final MutableLiveData<ConjugationGenRule> liveData = new MutableLiveData<>();

    public void updateData(ConjugationGenRule node) {
        this.liveData.setValue(node);
    }

    public LiveData<ConjugationGenRule> getLiveData() {
        return this.liveData;
    }
}
