package org.darisadesigns.polyglotlina.android;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.UriPermission;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.documentfile.provider.DocumentFile;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.darisadesigns.polyglotlina.DictCore;
import org.darisadesigns.polyglotlina.OSHandler;
import org.darisadesigns.polyglotlina.android.ui.LangPropertiesFragment;
import org.darisadesigns.polyglotlina.android.ui.PViewModel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.concurrent.Executor;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Main";
    private AppBarConfiguration mAppBarConfiguration;
    private View progressOverlay;
    private TextView headerTitle;
    private static final int STORAGE_PERMISSION_CODE = 3;
    private DictCore core;
    private OSHandler osHandler;
    private String newFileName = "";
    /**
     * if true opens the file to read, if false it opens it to save
     */
    private boolean shouldReadFile = true;
    ActivityResultLauncher<Intent> openDocumentActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK &&
                    result.getData() != null) {
                    // There are no request codes
                    Intent data = result.getData();
                    if (shouldReadFile) openFile(data.getData());
                    else saveToFile(data.getData());
                }
                else {
                    newFileName = "";
                }
            });

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
                R.id.nav_part_of_speech, R.id.nav_classes,
                R.id.nav_grammar, R.id.nav_logographs,
                R.id.nav_phonology)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        headerTitle = navigationView.getHeaderView(0).findViewById(R.id.header_title);

        osHandler = new AndroidOSHandler(
                new AndroidIOHandler(getApplicationContext(), this),
                new AndroidInfoBox(this),
                new AndroidHelpHandler(),
                new AndroidPFontHandler(),
                getApplicationContext()
        );
        core = new DictCore(new AndroidPropertiesManager(), osHandler, new AndroidPGTUtil(), new AndroidGrammarManager());
        core.getPropertiesManager().setFontStyle(0); // Fix for new file save
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
            case R.id.action_new:
                SharedPreferences.Editor editor =  PreferenceManager
                        .getDefaultSharedPreferences(getApplicationContext())
                        .edit();
                editor.remove(getString(R.string.settings_key_tmp_file));
                editor.remove(getString(R.string.settings_key_source_uri));
                if (editor.commit()) {
                    finish();
                    startActivity(getIntent());
                    overridePendingTransition(0, 0);
                }
                return true;
            case R.id.action_open:
                shouldReadFile = true;
                if (hasOrAskPermissions()) selectFile(false);
                return true;
            case R.id.action_save:
                shouldReadFile = false;
                if (hasOrAskPermissions()) startSavingFile();
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
                if (shouldReadFile) selectFile(false);
                else startSavingFile();
            }
            else {
                Toast.makeText(MainActivity.this, "Storage Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean hasOrAskPermissions() {
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
            return false;
        }
        return true;
    }

    private String copyToTmp(Uri contentUri, String fileName) throws IOException {
        File tmpFile = File.createTempFile(fileName.replace(".pgd", ""), ".pgd");
        InputStream inputStream = getContentResolver().openInputStream(contentUri);
        OutputStream outputStream = new FileOutputStream(tmpFile);

        ((AndroidIOHandler)core.getOSHandler().getIOHandler()).moveInputToOutput(inputStream, outputStream);

        return tmpFile.getPath();
    }

    private void selectFile(boolean create) {
        if (create && newFileName.isEmpty() && Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            this.runOnUiThread(() -> {
                AndroidInfoBox infoBox = (AndroidInfoBox)core.getOSHandler().getInfoBox();
                infoBox.stringInputDialog(
                        getString(R.string.title_save_language),
                        "",
                        getString(R.string.hint_file_name),
                        (String res) -> {
                            if (res == null) {
                                Toast.makeText(this, "Save file cancelled", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            newFileName = res;
                            startSavingFile();
                        }
                );
            });
            return;
        }
        String action = Intent.ACTION_OPEN_DOCUMENT;
        if (create && Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) action = Intent.ACTION_CREATE_DOCUMENT;
        else if (create) action = Intent.ACTION_OPEN_DOCUMENT_TREE;
        Intent intent = new Intent(action);
        if (!create || Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("*/*");

            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                    | Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                    | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        }

        // Optionally, specify a URI for the file that should appear in the
        // system file picker when it loads.
        // intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri);
        openDocumentActivityResultLauncher.launch(intent);
    }

    private void takeFilePermissions(Uri uri) {
        getContentResolver().takePersistableUriPermission(uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
                        | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
    }

    private void openFile(Uri uri) {
        try (Cursor cursor = getContentResolver().query(uri, null, null, null)) {
            if (cursor == null || !cursor.moveToFirst()) return;
            String display_name = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME));
            Executor executor = ((PolyGlot)getApplicationContext()).getExecutorService();
            AndroidOSHandler.animateView(progressOverlay, View.VISIBLE, 0.4f, 200);
            takeFilePermissions(uri);
            executor.execute(() -> {
                try {
                    deleteTempFiles();
                    String tmpFileName = copyToTmp(uri, display_name);
                    SharedPreferences.Editor editor =  PreferenceManager
                            .getDefaultSharedPreferences(getApplicationContext())
                            .edit();
                    editor.putString(getString(R.string.settings_key_tmp_file), tmpFileName);
                    editor.putString(getString(R.string.settings_key_source_uri), uri.toString());
                    editor.apply();
                    readFile(tmpFileName);
                } catch (IOException e) {
                    core.getOSHandler().getIOHandler().writeErrorLog(e);
                    core.getOSHandler().getInfoBox().error(
                            "File could not be open",
                            "The file selected could not be open."
                    );
                } finally {
                    /* copyToTmp might throw exception and readFile won't be able to remove block */
                    this.runOnUiThread(() -> {
                        AndroidOSHandler.animateView(progressOverlay, View.GONE, 0, 200);
                    });
                }
            });
        } catch (IllegalArgumentException e) {
            core.getOSHandler().getIOHandler().writeErrorLog(e);
            core.getOSHandler().getInfoBox().error(
                    "File could not be open",
                    "The file selected could not be open."
            );
        }
    }

    /**
     * This function reads a Polyglot file in the file system.
     * This should be called outside of the UI thread.
     *
     * @param path Path of the file to read
     */
    private void readFile(String path) {
        try {
            core = new DictCore(new AndroidPropertiesManager(), osHandler, new AndroidPGTUtil(), new AndroidGrammarManager());
            core.readFile(path);
        } catch (IOException e) {
            core.getOSHandler().getIOHandler().writeErrorLog(e);
        }
        this.runOnUiThread(() -> {
            fileReadingFinished(core);
        });
    }

    private void fileReadingFinished(DictCore core) {
        // Update core in model to update all fragments
        // Fragments must use requireActivity as provider owner
        PViewModel viewModel = new ViewModelProvider(this).get(PViewModel.class);
        viewModel.updateCore(core);
        ((PolyGlot)getApplicationContext()).setCore(core);
        headerTitle.setText(core.getPropertiesManager().getLangName());
        AndroidOSHandler.animateView(progressOverlay, View.GONE, 0, 200);
    }

    /**
     * Takes files permissions and saves core to file
     *
     * @param uri
     */
    private void saveToFile(Uri uri) {
        takeFilePermissions(uri);
        if (DocumentsContract.isTreeUri(uri)) {
            DocumentFile documentFile = DocumentFile.fromTreeUri(getApplicationContext(), uri);
            newFileName = newFileName.replace(".pgd", "").concat(".pgd");
            uri = documentFile.createFile("*/*", newFileName).getUri();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null)) {
                if (cursor == null || !cursor.moveToFirst()) return;
                String display_name = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME));
                if (!display_name.endsWith(".pgd")) {
                    uri = DocumentsContract.renameDocument(getContentResolver(), uri, display_name.concat(".pgd"));
                    takeFilePermissions(uri);
                }
            } catch (FileNotFoundException e) {
                core.getOSHandler().getIOHandler().writeErrorLog(e);
                Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT).show();
            }
        }
        // DocumentFile.fromSingleUri(getApplicationContext(), uri).renameTo("force.pdg");
        SharedPreferences.Editor editor =  PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext())
                .edit();
        editor.putString(getString(R.string.settings_key_source_uri), uri.toString());
        editor.apply();
        startSavingFile();
    }

    private String saveToTmpFile(DictCore core) throws TransformerException, ParserConfigurationException, IOException {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String tmpFilePath = preferences.getString(getString(R.string.settings_key_tmp_file), "");
        if (tmpFilePath.isEmpty()) {
            File tmpFile = File.createTempFile("conlang", ".pgd");
            tmpFilePath = tmpFile.getPath();
            SharedPreferences.Editor editor =  preferences.edit();
            editor.putString(getString(R.string.settings_key_tmp_file), tmpFilePath);
            editor.apply();
        }
        core.writeFile(tmpFilePath, false);
        return tmpFilePath;
    }

    /**
     * This function starts the save file process in a separate thread,
     * also displays a loading animation that should be removed in the saveFile method
     */
    private void startSavingFile() {
        AndroidOSHandler.animateView(progressOverlay, View.VISIBLE, 0.4f, 200);
        Executor executor = ((PolyGlot)getApplicationContext()).getExecutorService();
        executor.execute(this::saveFile);
    }

    /**
     * Saves core (from temp file) to uri.
     * Both paths should have been previously saved in preferences.
     * If we don't have permissions for uri we open to select the file again.
     *
     */
    private void saveFile() {
        /* If we are on lang properties the values might have not been saved yet */
        tryCallSaveLangProperties();

        boolean wasFileSaved = false;
        try {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            String sourceUri = preferences.getString(getString(R.string.settings_key_source_uri), "");
            Uri uri = Uri.parse(sourceUri);
            if (!hasWriteStoragePermissions(uri)) {
                selectFile(sourceUri.isEmpty());
                return;
            }
            String tmpFile = saveToTmpFile(this.core);
            FileInputStream inputStream = new FileInputStream(tmpFile);
            OutputStream outputStream = getContentResolver().openOutputStream(uri, "w");

            ((AndroidIOHandler)core.getOSHandler().getIOHandler()).moveInputToOutput(inputStream, outputStream);
            wasFileSaved = true;
        } catch (FileNotFoundException e) {
            selectFile(false);
        } catch (ParserConfigurationException | TransformerException | IOException e) {
            core.getOSHandler().getIOHandler().writeErrorLog(e);
            this.runOnUiThread(() -> {
                core.getOSHandler().getInfoBox().error(
                        "File could not be saved",
                        "The file '" + core.getCurFileName() + "' could not be saved."
                );
            });
        } finally {
            boolean finalWasFileSaved = wasFileSaved;
            this.runOnUiThread(() -> {
                AndroidOSHandler.animateView(progressOverlay, View.GONE, 0, 200);
                if (finalWasFileSaved)
                    Toast.makeText(this, R.string.toast_file_saved, Toast.LENGTH_SHORT).show();
            });
        }
    }

    private boolean hasWriteStoragePermissions(Uri myUri) {
        List<UriPermission> list = getContentResolver().getPersistedUriPermissions();
        for (UriPermission uriPermission: list){
            if(uriPermission.getUri().equals(myUri) && uriPermission.isWritePermission())
                return true;
        }
        return !newFileName.isEmpty() && myUri.getPath().endsWith(newFileName);
    }

    private void tryCallSaveLangProperties() {
        var fragment = getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        if (fragment == null) return;
        var navFragment = fragment.getChildFragmentManager().getPrimaryNavigationFragment();
        if (navFragment instanceof LangPropertiesFragment) {
            ((LangPropertiesFragment)navFragment).saveProperties();
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