package org.darisadesigns.polyglotlina.android.ui.PartsOfSpeech;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

import org.darisadesigns.polyglotlina.DictCore;
import org.darisadesigns.polyglotlina.Nodes.TypeNode;
import org.darisadesigns.polyglotlina.android.AndroidPGTUtil;
import org.darisadesigns.polyglotlina.android.PolyGlot;
import org.darisadesigns.polyglotlina.android.R;
import org.darisadesigns.polyglotlina.android.ui.Lexicon.Conjugations.LexemeConjugationsActivity;
import org.darisadesigns.polyglotlina.android.ui.Lexicon.LexemeGeneralFragment;
import org.darisadesigns.polyglotlina.android.ui.Lexicon.LexemeInfoActivity;
import org.darisadesigns.polyglotlina.android.ui.Lexicon.LexemeInfoViewModel;
import org.darisadesigns.polyglotlina.android.ui.PartsOfSpeech.Conjugations.AutogenerationActivity;
import org.darisadesigns.polyglotlina.android.ui.PartsOfSpeech.Conjugations.AutogenerationSimpleActivity;
import org.darisadesigns.polyglotlina.android.ui.PartsOfSpeech.Conjugations.POSConjugationInfoActivity;

public class POSInfoActivity extends AppCompatActivity {

    private static final String TAG = "POSInfoActivity";
    public static final String POS_ID_EXTRA = "part-of-speech-id";

    private DictCore core;
    private TypeNode posNode;

    private boolean errorsWarned = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pos_info);

        setSupportActionBar((Toolbar) findViewById(R.id.app_bar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        Intent intent = getIntent();
        PolyGlot polyGlot = (PolyGlot)getApplicationContext();
        core = polyGlot.getCore();
        int posId = intent.getIntExtra(POS_ID_EXTRA, -1);
        posNode = core.getTypes().getNodeById(posId);
        getSupportActionBar().setTitle(posNode.getValue());

        POSInfoViewModel viewModel = new ViewModelProvider(this).get(POSInfoViewModel.class);
        viewModel.updateData(posNode);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.delete, menu);
        getMenuInflater().inflate(R.menu.pos_autogeneration, menu);
        ((AndroidPGTUtil)core.getPGTUtil()).fixOptionsMenuTextColor(menu, this);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                try {
                    core.getTypes().deleteNodeById(posNode.getId());
                    finish();
                } catch (Exception e) {
                    core.getOSHandler().getIOHandler().writeErrorLog(e);
                    core.getOSHandler().getInfoBox().error(
                            "Deletion Error",
                            "Unable to delete part of speech: " + e.getLocalizedMessage()
                    );
                }
                return true;
            case R.id.action_autogenerate_simple:
                Intent autogenerateSimpleIntent = new Intent(this, AutogenerationSimpleActivity.class);
//                conjugationsIntent.putExtra(POSConjugationInfoActivity.POS_ID_EXTRA, typeNode.getId());
//                conjugationsIntent.putExtra(POSConjugationInfoActivity.CONJUGATION_ID_EXTRA, item.getId());
                startActivity(autogenerateSimpleIntent);
                return true;
            case R.id.action_autogenerate_classic:
                Intent autogenerateIntent = new Intent(this, AutogenerationActivity.class);
                autogenerateIntent.putExtra(AutogenerationActivity.POS_ID_EXTRA, posNode.getId());
                startActivity(autogenerateIntent);
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "Back pressed invoked");
        POSGeneralFragment fragment = (POSGeneralFragment)
                getSupportFragmentManager()
                        .findFragmentById(R.id.pos_general_fragment_container_view);

        boolean isValid = fragment.isDataValid();
        if (isValid) {
            fragment.savePOS();
            super.onBackPressed();
            return;
        }

        if (errorsWarned) {
            try {
                core.getTypes().deleteNodeById(posNode.getId());
            } catch (Exception e) {
                core.getOSHandler().getIOHandler().writeErrorLog(e);
                core.getOSHandler().getInfoBox().error(
                        "Deletion Error",
                        "Unable to delete type: " + e.getLocalizedMessage()
                );
            }
            super.onBackPressed();
        }
        else {
            errorsWarned = true;
            CharSequence text = getResources().getString(R.string.toast_lexeme_errors);
            Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
            /* Make the deletion reset when the toast disappears */
            new Handler().postDelayed(() -> errorsWarned = false, 2000);
        }
    }
}