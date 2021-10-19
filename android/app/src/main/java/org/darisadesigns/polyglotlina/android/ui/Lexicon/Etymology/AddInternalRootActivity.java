package org.darisadesigns.polyglotlina.android.ui.Lexicon.Etymology;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import org.darisadesigns.polyglotlina.android.R;
import org.darisadesigns.polyglotlina.android.ui.Lexicon.LexemePickerFragment;
import org.darisadesigns.polyglotlina.android.ui.Lexicon.LexemePickerViewModel;

public class AddInternalRootActivity extends AppCompatActivity {

    private static String TAG = "AddInternalRootActivity";

    public static final String  ROOT_WORD_ID_EXTRA = "root-word-id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_internal_root);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        LexemePickerFragment pickerFragment = LexemePickerFragment.newInstance();
        transaction.replace(R.id.fragment_container_view, pickerFragment).commit();

        LexemePickerViewModel viewModel = new ViewModelProvider(this).get(LexemePickerViewModel.class);

        viewModel.getLiveWord().observe(this, (conWord) -> {
            if (null != conWord) {
                Intent intent = new Intent();
                intent.putExtra(ROOT_WORD_ID_EXTRA, conWord.getId());
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
}