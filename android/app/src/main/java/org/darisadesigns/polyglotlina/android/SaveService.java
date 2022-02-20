package org.darisadesigns.polyglotlina.android;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.darisadesigns.polyglotlina.DictCore;

import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

public class SaveService extends Service {

    private static final String TAG = "SaveService";

    private DictCore core;

    public SaveService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        core = ((PolyGlot)getApplicationContext()).getCore();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand: ");
        super.onStartCommand(intent, flags, startId);
        try {
            Log.e(TAG, "onStartCommand: 1");
            ExecutorService executorService = Executors.newFixedThreadPool(1);
            executorService.execute(() -> {
                try {
                    Log.e(TAG, "onStartCommand: execute");
                    saveToTmpFile(this.core);
                } catch (ParserConfigurationException | TransformerException | IOException e) {
                    core.getOSHandler().getIOHandler().writeErrorLog(e, "couldn't save tmp file");
                }
                finally {
                    Log.e(TAG, "onStartCommand: stopself");
                    stopSelf();
                }
            });
        } catch(Exception e) {
            Log.e(TAG, "onStartCommand: catch", e);
        }
        finally {
            Log.e(TAG, "onStartCommand: finally");
        }
        return START_NOT_STICKY;
    }

    private void saveToTmpFile(DictCore core) throws TransformerException, ParserConfigurationException, IOException {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String tmpFilePath = preferences.getString(getString(R.string.settings_key_tmp_file), "");
        if (tmpFilePath.isEmpty()) {
            File tmpFile = File.createTempFile("conlang", ".pgd");
            tmpFilePath = tmpFile.getPath();
            SharedPreferences.Editor editor =  preferences.edit();
            editor.putString(getString(R.string.settings_key_tmp_file), tmpFilePath);
            editor.apply();
        }
        Log.e(TAG, "saveToTmpFile: saving");
        core.writeFile(tmpFilePath, false);
        Log.e(TAG, "saveToTmpFile: saved");
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy: ");
        super.onDestroy();
    }
}