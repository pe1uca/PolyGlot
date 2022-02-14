package org.darisadesigns.polyglotlina.android.ui.PartsOfSpeech;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.darisadesigns.polyglotlina.Nodes.TypeNode;

public class POSInfoViewModel extends ViewModel {

    private final MutableLiveData<TypeNode> liveData = new MutableLiveData<>();

    public void updateData(TypeNode node) {
        this.liveData.setValue(node);
    }

    public LiveData<TypeNode> getLiveData() {
        return this.liveData;
    }
}