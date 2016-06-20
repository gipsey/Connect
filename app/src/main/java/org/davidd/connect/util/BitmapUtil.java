package org.davidd.connect.util;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import org.davidd.connect.ConnectApp;
import org.davidd.connect.R;

import java.util.Random;


/**
 *
 */
public class BitmapUtil {

    private static int[] colors900 = new int[]{
            Color.parseColor("#B71C1C"),
            Color.parseColor("#880E4F"),
            Color.parseColor("#4A148C"),
            Color.parseColor("#311B92"),
            Color.parseColor("#1A237E"),
            Color.parseColor("#0D47A1"),
            Color.parseColor("#01579B"),
            Color.parseColor("#006064"),
            Color.parseColor("#004D40"),
            Color.parseColor("#1B5E20"),
            Color.parseColor("#33691E"),
            Color.parseColor("#827717"),
            Color.parseColor("#F57F17"),
            Color.parseColor("#FF6F00"),
            Color.parseColor("#E65100"),
            Color.parseColor("#BF360C"),
            Color.parseColor("#3E2723"),
            Color.parseColor("#212121"),
            Color.parseColor("#263238")
    };

    private static Random random = new Random();

    public static Bitmap drawTextToBitmap(String text) {
        return drawTextToBitmap(R.drawable.notification_white_bg, text);
    }

    public static Bitmap drawTextToBitmap(int resId, String text) {
        if (DataUtils.isEmpty(text)) {
            text = "?";
        } else {
            if (text.length() > 2) {
                text = text.substring(0, 2);
            }
            text = text.toUpperCase();
        }

        Resources resources = ConnectApp.instance().getResources();

        Bitmap bitmap = BitmapFactory.decodeResource(resources, resId);

        Bitmap.Config bitmapConfig = bitmap.getConfig();
        // set default bitmap config if none
        if (bitmapConfig == null) {
            bitmapConfig = Bitmap.Config.ARGB_8888;
        }
        // resource bitmaps are immutable,
        // so we need to convert it to mutable one
        bitmap = bitmap.copy(bitmapConfig, true);

        // new antialised Paint
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        // text color
        paint.setColor(colors900[random.nextInt(colors900.length)]);
        // text size in pixels
        paint.setTextSize((int) (30 * resources.getDisplayMetrics().density));
        // text shadow
        paint.setShadowLayer(1f, 0f, 1f, Color.WHITE);

        // draw text to the Canvas center
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);
        int x = (bitmap.getWidth() - bounds.width()) / 2;
        int y = (bitmap.getHeight() + bounds.height()) / 2;

        new Canvas(bitmap).drawText(text, x, y, paint);

        return bitmap;
    }
}
