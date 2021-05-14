package org.darisadesigns.polyglotlina.android.ui;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import org.darisadesigns.polyglotlina.DictCore;
import org.jetbrains.annotations.NotNull;

public class PViewModelFactory implements ViewModelProvider.Factory {

    DictCore core;

    public PViewModelFactory(DictCore core) {
        this.core = core;
    }


    @NotNull
    @Override
    public <T extends ViewModel> T create(@NotNull Class<T> modelClass) {
        return (T) new PViewModel();
    }
}
