package org.darisadesigns.polyglotlina.android.ui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class EditorViewModel extends ViewModel {

    private final MutableLiveData<String> editorText = new MutableLiveData<>();

    public void updateText(String txt) {
        this.editorText.setValue(txt);
    }

    public LiveData<String> getLiveText() {
        return this.editorText;
    }
}
