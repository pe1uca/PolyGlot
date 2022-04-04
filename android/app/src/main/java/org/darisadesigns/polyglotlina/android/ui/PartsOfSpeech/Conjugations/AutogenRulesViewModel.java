package org.darisadesigns.polyglotlina.android.ui.PartsOfSpeech.Conjugations;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.darisadesigns.polyglotlina.Nodes.ConjugationPair;
import org.darisadesigns.polyglotlina.Nodes.TypeNode;

public class AutogenRulesViewModel extends ViewModel {

    private TypeNode posNode;
    private final MutableLiveData<ConjugationPair> liveData = new MutableLiveData<>();

    public void updateData(ConjugationPair node) {
        this.liveData.setValue(node);
    }

    public LiveData<ConjugationPair> getLiveData() {
        return this.liveData;
    }

    public TypeNode getPosNode() {
        return posNode;
    }

    public void setPosNode(TypeNode posNode) {
        this.posNode = posNode;
    }
}
