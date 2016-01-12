package org.davidd.connect.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

/**
 * @author David Debre
 *         on 2015/12/13
 */
public class ActivityUtils {

    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    public static void navigate(Activity activity, Class<?> activityClass, boolean finish) {
        navigate(activity, activityClass, null, finish);
    }

    public static void navigate(Activity activity, Class<?> activityClass, Bundle bundle, boolean finish) {
        navigate(activity, activityClass, bundle, null, finish);
    }

    public static void navigate(Activity activity, Class<?> activityClass, Bundle bundle, Integer flags, boolean finish) {
        Intent intent = new Intent();
        intent.setClass(activity, activityClass);

        if (flags != null) {
            intent.setFlags(flags);
        }

        if (bundle != null) {
            intent.putExtras(bundle);
        }

        activity.startActivity(intent);

        if (finish) {
            activity.finish();
        }
    }

    private void waitSomeTime(Runnable runnable, int millis) {
        new Handler().postDelayed(runnable, millis);
    }
}
