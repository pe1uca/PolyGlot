package org.darisadesigns.polyglotlina.android;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Main";
    private AppBarConfiguration mAppBarConfiguration;
    private static final int PICK_PGD_FILE = 2;
    private static final int STORAGE_PERMISSION_CODE = 3;
    private DictCore core;
    private OSHandler osHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
                new AndroidIOHandler(getApplicationContext()),
                new AndroidInfoBox(this),
                new AndroidHelpHandler(),
                new AndroidPFontHandler()
        );
        core = new DictCore(new AndroidPropertiesManager(), osHandler, new AndroidPGTUtil());
        ((PolyGlot)getApplicationContext()).setCore(core);
        PViewModel viewModel = new ViewModelProvider(this).get(PViewModel.class);
        viewModel.updateCore(core);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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
                Log.e("TAG", "onActivityResult: " + uri.getPath());
                try (Cursor cursor = getContentResolver().query(uri, null, null, null)) {
                    if (cursor != null && cursor.moveToFirst()) {
                        String path = cursor.getString(cursor.getColumnIndexOrThrow("path"));
                        readFile(path);
                    }
                } catch (IllegalArgumentException e) {
                    core.getOSHandler().getInfoBox().error(
                            "File could not be open",
                            "The file selected could not be open.\n" +
                                    "Provide a file in your local storage."
                    );
                }
                // Perform operations on the document using its URI.
            }
        }
    }

    private void openFile() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");

        // Optionally, specify a URI for the file that should appear in the
        // system file picker when it loads.
        // intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri);

        startActivityForResult(intent, PICK_PGD_FILE);
    }

    private void readFile(String path) {
        try {
            Log.e(TAG, path);
            core = new DictCore(new AndroidPropertiesManager(), osHandler, new AndroidPGTUtil());
            core.readFile(path);

            // Update core in model to update all fragments
            // Fragments must use requireActivity as provider owner
            PViewModel viewModel = new ViewModelProvider(this).get(PViewModel.class);
            viewModel.updateCore(core);
            ((PolyGlot)getApplicationContext()).setCore(core);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public DictCore getCore() {
        return this.core;
    }
}