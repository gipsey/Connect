package org.davidd.connect.util;

import android.content.Context;
import android.widget.Toast;

/**
 * @author David Debre
 *         on 2015/12/13
 */
public class ActivityUtils {

    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }
}
