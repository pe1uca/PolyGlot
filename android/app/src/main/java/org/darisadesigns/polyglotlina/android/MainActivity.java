package org.darisadesigns.polyglotlina.android;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.preference.PreferenceManager;
import android.provider.DocumentsContract;
import android.provider.OpenableColumns;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.darisadesigns.polyglotlina.DictCore;
import org.darisadesigns.polyglotlina.ManagersCollections.PropertiesManager;
import org.darisadesigns.polyglotlina.OSHandler;
import org.darisadesigns.polyglotlina.PGTUtil;
import org.darisadesigns.polyglotlina.android.ui.PViewModel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.Executor;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Main";
    private AppBarConfiguration mAppBarConfiguration;
    private View progressOverlay;
    private static final int PICK_PGD_FILE = 2;
    private static final int STORAGE_PERMISSION_CODE = 3;
    private DictCore core;
    private OSHandler osHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressOverlay = findViewById(R.id.progress_overlay);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_lang_properties, R.id.nav_lexicon,
                R.id.nav_gallery, R.id.nav_slideshow)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        osHandler = new AndroidOSHandler(
                new AndroidIOHandler(getApplicationContext(), this),
                new AndroidInfoBox(this),
                new AndroidHelpHandler(),
                new AndroidPFontHandler(),
                getApplicationContext()
        );
        core = new DictCore(new AndroidPropertiesManager(), osHandler, new AndroidPGTUtil(), new AndroidGrammarManager());
        ((PolyGlot)getApplicationContext()).setCore(core);
        PViewModel viewModel = new ViewModelProvider(this).get(PViewModel.class);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String tmpFile = preferences.getString(getString(R.string.settings_key_tmp_file), "");
        if (!tmpFile.isEmpty()) {
            Executor executor = ((PolyGlot)getApplicationContext()).getExecutorService();
            AndroidOSHandler.animateView(progressOverlay, View.VISIBLE, 0.4f, 200);
            executor.execute(() -> {
                    readFile(tmpFile);
            });
        }
        viewModel.updateCore(core);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        /* Hack to change text color, otherwise it stays white on white */
        for (int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);
            SpannableString s = new SpannableString(item.getTitle());
            int color = (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_YES) == 0 ?
                    R.color.design_default_color_on_secondary : R.color.design_default_color_on_primary;
            s.setSpan(new ForegroundColorSpan(getColor(color)), 0, s.length(), 0);
            item.setTitle(s);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_open:
                if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED)
                {
                    ActivityCompat.requestPermissions(
                            MainActivity.this,
                            new String[] {
                                    Manifest.permission.READ_EXTERNAL_STORAGE,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                            },
                            STORAGE_PERMISSION_CODE
                    );
                }
                else
                    openFile();
                return true;
            case R.id.action_save:
                AndroidOSHandler.animateView(progressOverlay, View.VISIBLE, 0.4f, 200);
                Executor executor = ((PolyGlot)getApplicationContext()).getExecutorService();
                executor.execute(this::saveFile);
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }


    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openFile();
            }
            else {
                Toast.makeText(MainActivity.this, "Storage Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {
        super.onActivityResult(requestCode, resultCode, resultData);
        if (requestCode == PICK_PGD_FILE
                && resultCode == Activity.RESULT_OK) {
            // The result data contains a URI for the document or directory that
            // the user selected.
            Uri uri = null;
            if (resultData != null) {
                uri = resultData.getData();

                try (Cursor cursor = getContentResolver().query(uri, null, null, null)) {
                    if (cursor != null && cursor.moveToFirst()) {
                        String display_name = cursor.getString(cursor.getColumnIndexOrThrow("_display_name"));
                        Executor executor = ((PolyGlot)getApplicationContext()).getExecutorService();
                        AndroidOSHandler.animateView(progressOverlay, View.VISIBLE, 0.4f, 200);
                        getContentResolver().takePersistableUriPermission(uri,
                                Intent.FLAG_GRANT_READ_URI_PERMISSION
                                        | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                        Uri finalUri = uri;
                        deleteTempFiles();
                        executor.execute(() -> {
                            try {
                                String tmpFileName = copyToTmp(finalUri, display_name);
                                SharedPreferences.Editor editor =  PreferenceManager
                                        .getDefaultSharedPreferences(getApplicationContext())
                                        .edit();
                                editor.putString(getString(R.string.settings_key_tmp_file), tmpFileName);
                                editor.putString(getString(R.string.settings_key_source_uri), finalUri.toString());
                                editor.apply();
                                readFile(tmpFileName);
                            } catch (IOException e) {
                                core.getOSHandler().getIOHandler().writeErrorLog(e);
                                core.getOSHandler().getInfoBox().error(
                                        "File could not be open",
                                        "The file selected could not be open."
                                );
                            }
                        });
                    }
                } catch (IllegalArgumentException e) {
                    core.getOSHandler().getIOHandler().writeErrorLog(e);
                    core.getOSHandler().getInfoBox().error(
                            "File could not be open",
                            "The file selected could not be open."
                    );
                }
                // Perform operations on the document using its URI.
            }
        }
    }

    private String copyToTmp(Uri contentUri, String fileName) throws IOException {
        File tmpFile = File.createTempFile(fileName.replace(".pgd", ""), ".pgd");
        InputStream inputStream = getContentResolver().openInputStream(contentUri);
        OutputStream outputStream = new FileOutputStream(tmpFile);

        ((AndroidIOHandler)core.getOSHandler().getIOHandler()).moveInputToOutput(inputStream, outputStream);

        return tmpFile.getPath();
    }

    private void openFile() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");

        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                | Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);

        // Optionally, specify a URI for the file that should appear in the
        // system file picker when it loads.
        // intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri);

        startActivityForResult(intent, PICK_PGD_FILE);
    }

    private void readFile(String path) {
        try {
            core = new DictCore(new AndroidPropertiesManager(), osHandler, new AndroidPGTUtil(), new AndroidGrammarManager());
        core.readFile(path);
        } catch (IOException e) {
            core.getOSHandler().getIOHandler().writeErrorLog(e);
        }
        Handler threadHandler = ((PolyGlot)getApplicationContext()).getMainThreadHandler();
        threadHandler.post(() -> {
            fileReadingFinished(core);
        });
    }

    private void fileReadingFinished(DictCore core) {
        // Update core in model to update all fragments
        // Fragments must use requireActivity as provider owner
        PViewModel viewModel = new ViewModelProvider(this).get(PViewModel.class);
        viewModel.updateCore(core);
        ((PolyGlot)getApplicationContext()).setCore(core);
        AndroidOSHandler.animateView(progressOverlay, View.GONE, 0, 200);
    }

    private void saveFile() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String tmpFile = preferences.getString(getString(R.string.settings_key_tmp_file), "");
        String sourceUri = preferences.getString(getString(R.string.settings_key_source_uri), "");
        if (tmpFile.isEmpty()) return;
        try {

            core.writeFile(tmpFile, false);
            FileInputStream inputStream = new FileInputStream(new File(tmpFile));
            Uri uri = Uri.parse(sourceUri);
            ParcelFileDescriptor pgdFile = getContentResolver().openFileDescriptor(uri, "w");
            OutputStream outputStream = new FileOutputStream(pgdFile.getFileDescriptor());

            ((AndroidIOHandler)core.getOSHandler().getIOHandler()).moveInputToOutput(inputStream, outputStream);
            pgdFile.close();
            Handler threadHandler = ((PolyGlot)getApplicationContext()).getMainThreadHandler();
            threadHandler.post(() -> {
                AndroidOSHandler.animateView(progressOverlay, View.GONE, 0, 200);
                Toast.makeText(this, R.string.toast_file_saved, Toast.LENGTH_SHORT).show();
            });
        } catch (ParserConfigurationException | TransformerException | IOException e) {
            core.getOSHandler().getIOHandler().writeErrorLog(e);
            core.getOSHandler().getInfoBox().error(
                    "File could not be saved",
                    "The file '" + core.getCurFileName() + "' could not be saved."
            );
        }
    }

    @Override
    protected void onDestroy() {
        if(!isChangingConfigurations()) {
            // deleteTempFiles(getCacheDir());
        }
        super.onDestroy();
    }

    private void deleteTempFiles() {
        deleteTempFiles(getCacheDir());
        File[] files = getCacheDir().listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.isDirectory()) {
                    deleteTempFiles(f);
                } else {
                    f.delete();
                }
            }
        }
    }

    private boolean deleteTempFiles(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File f : files) {
                    if (f.isDirectory()) {
                        deleteTempFiles(f);
                    } else {
                        f.delete();
                    }
                }
            }
        }
        return file.delete();
    }

    public DictCore getCore() {
        return this.core;
    }
}