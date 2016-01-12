package org.davidd.connect.util;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import org.davidd.connect.R;

public class DisplayUtils {

    public static final int DELAY = 1000;
    private static final String JID_EXPRESSION = "^.+@.+$";
    private static final String PASSWORD_EXPRESSION = "^.{4,30}$";


    public static void showSnack(View view, String message) {
        if (DataUtils.isEmpty(message)) {
            return;
        }
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show();
    }

    public static void showSoftKeyboard() {
        // TODO
    }

    public static void hideSoftKeyboard(Context context, View view) {
        if (view != null) {
            InputMethodManager im = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static void showOkAlertDialog(Context context, String message) {
        showOkAlertDialog(context, message, null);
    }

    public static void showOkAlertDialog(Context context, String message, @Nullable final Runnable actionOk) {
        showOkAlertDialog(context, message, context.getString(R.string.ok), actionOk);
    }

    public static void showOkAlertDialog(Context context, String message,
                                         String okButtonText, @Nullable final Runnable actionOk) {
        showOkAlertDialog(context, message, okButtonText, actionOk, null, null);
    }

    public static void showOkAlertDialog(Context context, String message,
                                         String okButtonText, @Nullable final Runnable actionOk,
                                         String negativeButtonText, @Nullable final Runnable actionNegative) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setMessage(message);
        builder.setCancelable(false);

        if (actionOk == null) {
            builder.setPositiveButton(okButtonText, null);
        } else {
            builder.setPositiveButton(okButtonText, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    actionOk.run();
                }
            });
        }

        if (!DataUtils.isEmpty(negativeButtonText)) {
            if (actionNegative == null) {
                builder.setNegativeButton(negativeButtonText, null);
            } else {
                builder.setNegativeButton(negativeButtonText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        actionNegative.run();
                    }
                });
            }
        }

        builder.create().show();
    }

    public static boolean isPasswordValid(String password) {
        return !DataUtils.isEmpty(password) && password.matches(PASSWORD_EXPRESSION);
    }

    public static boolean isEmailValid(String email) {
        return !DataUtils.isEmpty(email) && email.matches(JID_EXPRESSION);
    }
}
