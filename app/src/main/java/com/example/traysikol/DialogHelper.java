package com.example.traysikol;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

public class DialogHelper {
    public interface ConfirmationListener {
        void onConfirmation(boolean confirmed);
    }

    public static void showConfirmationDialog(Activity activity, final ConfirmationListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Confirmation");
        builder.setMessage("Do you want to save this destination?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                listener.onConfirmation(true);
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                listener.onConfirmation(false);
            }
        });
        builder.show();
    }
}
