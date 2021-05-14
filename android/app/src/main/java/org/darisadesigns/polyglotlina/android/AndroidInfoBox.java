package org.darisadesigns.polyglotlina.android;

import android.app.Activity;
import android.app.AlertDialog;

import org.darisadesigns.polyglotlina.InfoBox;

public class AndroidInfoBox implements InfoBox {

    private Activity activity;

    @Override
    public void info(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.activity);
        builder.setMessage(message)
                .setTitle(title);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void error(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.activity);
        builder.setMessage(message)
                .setTitle(title);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void warning(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.activity);
        builder.setMessage(message)
                .setTitle(title);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public Integer yesNoCancel(String title, String message) {
        return null;
    }

    @Override
    public boolean deletionConfirmation() {
        return false;
    }

    @Override
    public boolean actionConfirmation(String title, String message) {
        return false;
    }

    @Override
    public String stringInputDialog(String title, String message) {
        return null;
    }

    @Override
    public Double doubleInputDialog(String title, String message, String warningMessage) {
        return null;
    }

    public AndroidInfoBox(Activity _activity) {
        this.activity = _activity;
    }
}
