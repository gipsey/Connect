package org.davidd.connect.util;

/**
 * @author David Debre
 *         on 2015/12/12
 */
public class DataUtils {
    public static boolean isEmpty(String text) {
        return text == null || text.trim().isEmpty();
    }

    public static boolean isEmpty(CharSequence text) {
        return text == null || text.toString().trim().isEmpty();
    }
}
