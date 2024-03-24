package com.example.traysikol;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class ConfirmDialog {
    public interface ConfirmDialogListener {
        void onYesClicked();
        void onNoClicked();
    }

    public static void showDialog(Context context, String title, String message, final ConfirmDialogListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (listener != null) {
                    listener.onYesClicked();
                }
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (listener != null) {
                    listener.onNoClicked();
                }
            }
        });
        builder.setCancelable(false); // Prevent dialog from being dismissed by tapping outside of it
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
