package org.darisadesigns.polyglotlina.android.ui.Lexicon;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.darisadesigns.polyglotlina.Nodes.ConWord;
import org.darisadesigns.polyglotlina.Nodes.ConjugationNode;

public class ConjugationViewModel extends ViewModel {

    private final MutableLiveData<Conjugation> liveConjugation = new MutableLiveData<>();

    public void updateConjugation(Conjugation conjugation) {
        this.liveConjugation.setValue(conjugation);
    }

    public LiveData<Conjugation> getLiveConjugation() {
        return this.liveConjugation;
    }

    public static class Conjugation {
        ConjugationNode conjugationColumn;
        ConjugationNode conjugationRow;
        ConWord conWord;

        Conjugation(
                ConWord conWord,
                ConjugationNode conjugationColumn,
                ConjugationNode conjugationRow
        ) {
            this.conjugationColumn = conjugationColumn;
            this.conjugationRow = conjugationRow;
            this.conWord = conWord;
        }

        Conjugation(ConWord conWord) {
            this.conWord = conWord;
        }
    }
}
