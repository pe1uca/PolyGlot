package org.darisadesigns.polyglotlina.android.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputEditText;

import org.darisadesigns.polyglotlina.DictCore;
import org.darisadesigns.polyglotlina.android.MainActivity;
import org.darisadesigns.polyglotlina.android.R;
import org.darisadesigns.polyglotlina.android.ui.PViewModel;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";

    private PViewModel pViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        pViewModel =
                new ViewModelProvider(requireActivity()).get(PViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        final TextInputEditText txtLangName = root.findViewById(R.id.txtLangName);
        pViewModel.getLiveCore().observe(getViewLifecycleOwner(), new Observer<DictCore>() {
            @Override
            public void onChanged(@Nullable DictCore c) {
                if (c != null) {
                    txtLangName.setText(c.getPropertiesManager().getLangName());
                }
            }
        });

        return root;
    }
}