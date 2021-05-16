package org.darisadesigns.polyglotlina.android.ui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.darisadesigns.polyglotlina.DictCore;

public class PViewModel extends ViewModel {
    private final MutableLiveData<DictCore> liveCore = new MutableLiveData<>();

    public void updateCore(DictCore core) {
        liveCore.setValue(core);
    }

    public LiveData<DictCore> getLiveCore() {
        return liveCore;
    }
}
