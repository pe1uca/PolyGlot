package org.darisadesigns.polyglotlina.android.ui.WordClasses;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import org.darisadesigns.polyglotlina.android.MainActivity;
import org.darisadesigns.polyglotlina.android.R;
import org.darisadesigns.polyglotlina.android.ui.Lexicon.LexemePickerFragment;
import org.darisadesigns.polyglotlina.android.ui.Lexicon.LexemePickerViewModel;

public class SelectAssocLexemeActivity extends AppCompatActivity {

    private static final String TAG = "SelectAssocLexemeActivity";

    public static final String WORD_CLASS_EXTRA = "word-class-id";
    public static final String ASSOC_WORD_ID_EXTRA = "assoc-word-id";
    public static final String PARENT_ACTIVITY_CLASS_EXTRA = "parent-activity-class";

    private String className;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_assoc_lexeme);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        LexemePickerFragment pickerFragment = LexemePickerFragment.newInstance();
        transaction.replace(R.id.fragment_container_view, pickerFragment).commit();

        className = getIntent().getStringExtra(PARENT_ACTIVITY_CLASS_EXTRA);

        LexemePickerViewModel viewModel = new ViewModelProvider(this).get(LexemePickerViewModel.class);

        viewModel.getLiveWord().observe(this, (conWord) -> {
            if (null != conWord) {
                Intent intent = new Intent();
                intent.putExtra(WORD_CLASS_EXTRA, getIntent().getIntExtra(WORD_CLASS_EXTRA, -1));
                intent.putExtra(ASSOC_WORD_ID_EXTRA, conWord.getId());
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    @Override
    public Intent getSupportParentActivityIntent() {
        return getParentActivityIntentImpl();
    }

    @Override
    public Intent getParentActivityIntent() {
        return getParentActivityIntentImpl();
    }

    private Intent getParentActivityIntentImpl() {
        Intent i = null;

        // Check if we got a custom parent activity.
        if (className != null) {
            try {
                i = new Intent(this, Class.forName(className));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            // Default to MainActivity. Maybe not ideal, but there's no other default screen.
            i = new Intent(this, MainActivity.class);
        }
        // Since we are reusing the previous Activity (i.e. bringing it to the top
        // without re-creating a new instance) set these flags:
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        // We could add result extras in here.

        return i;
    }
}