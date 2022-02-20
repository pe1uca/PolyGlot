package org.darisadesigns.polyglotlina.android;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;

import com.google.android.material.textfield.TextInputEditText;

import org.darisadesigns.polyglotlina.InfoBox;

public class AndroidInfoBox implements InfoBox {

    private Activity activity;

    @Override
    public void info(String title, String message) {
        this.info(title, message, this.activity);
    }

    public void info(String title, String message, Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message)
                .setTitle(title)
                .show();
    }

    @Override
    public void error(String title, String message) {
        this.error(title, message, this.activity);
    }

    public void error(String title, String message, Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message)
                .setTitle(title)
                .show();
    }

    @Override
    public void warning(String title, String message) {
        this.warning(title, message, this.activity);
    }

    public void warning(String title, String message, Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message)
                .setTitle(title)
                .show();
    }

    @Override
    public Integer yesNoCancel(String title, String message) {
        return null;
    }

    public void yesNoCancel(String title, String message, DialogInterface.OnClickListener onClickListener) {
        this.yesNoCancel(title, message, this.activity, onClickListener);
    }

    public void yesNoCancel(String title, String message, Context context, DialogInterface.OnClickListener onClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message)
                .setTitle(title)
                .setPositiveButton(android.R.string.yes, onClickListener)
                .setNegativeButton(android.R.string.no, onClickListener)
                .show();
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

    public void stringInputDialog(String title, String message, String hint, StringInputCallback callback) {
        this.stringInputDialog(title, message, hint, this.activity, callback);
    }

    public void stringInputDialog(String title, String message, String hint, Context context, StringInputCallback callback) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message)
                .setTitle(title);

        // I'm using fragment here so I'm using getView() to provide ViewGroup
        // but you can provide here any other instance of ViewGroup from your Fragment / Activity
        View viewInflated = LayoutInflater.from(activity).inflate(R.layout.dialog_text_input, null, false);
        // Set up the input
        final TextInputEditText input = viewInflated.findViewById(R.id.input);
        input.setHint(hint);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        builder.setView(viewInflated);

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                callback.stringCallback(input.getText().toString());
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                callback.stringCallback(null);
            }
        });

        builder.show();
    }

    @Override
    public Double doubleInputDialog(String title, String message, String warningMessage) {
        return null;
    }

    public AndroidInfoBox(Activity _activity) {
        this.activity = _activity;
    }


    public interface StringInputCallback {
        void stringCallback(String result);
    }
}
