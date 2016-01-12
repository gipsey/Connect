package org.davidd.connect.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Date;

/**
 * @author David Debre
 *         on 2015/12/12
 */
public class DataUtils {

    // Logging related constants
    public static final String METHOD = "method name : ";

    public static boolean isEmpty(CharSequence text) {
        return isEmpty(text.toString());
    }

    public static boolean isEmpty(String text) {
        return text == null || text.trim().isEmpty();
    }

    public static Gson createGsonWithExcludedFields() {
        return new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
    }

//    public static boolean isNetworkAvailable(Context context) {
//        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//
//        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
//
//        return activeNetworkInfo != null &&
//                (activeNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI || activeNetworkInfo.getType() == ConnectivityManager.TYPE_MOBILE) &&
//                activeNetworkInfo.isConnected();
//    }

    public static Date getCurrentDate() {
        return new Date(System.currentTimeMillis());
    }
}
