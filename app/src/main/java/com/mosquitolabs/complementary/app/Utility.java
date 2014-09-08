package com.mosquitolabs.complementary.app;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.math.BigDecimal;
import java.math.RoundingMode;


public class Utility {

    private final static int FPS = 35;

    public final static int NEXT = 1;
    public final static int PREVIOUS = 2;

    private static SharedPreferences sharedPreferences;


    public static void initUtility(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager CManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo NInfo = CManager.getActiveNetworkInfo();

        return NInfo != null && NInfo.isConnectedOrConnecting();
    }

    public static int getFPS() {
        return FPS;
    }

    public static int convertDpToPixels(Context context, float dp) {
        Resources resources = context.getResources();
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                resources.getDisplayMetrics()
        );
    }

    public static float pixelsToSp(Context context, float px) {
        float scaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
        return px / scaledDensity;
    }

    @TargetApi(14)
    public static int getWidth(Activity activity) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            display.getSize(size);
            return size.x;
        }
        return display.getWidth();
    }

    @TargetApi(14)
    public static int getHeight(Activity activity) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            display.getSize(size);
            return size.y;
        }

        return display.getHeight();

    }

    public static void setMargins(View v, int l, int t, int r, int b) {
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            p.setMargins(l, t, r, b);
            v.requestLayout();
        }
    }

    public static void setViewWidth(View view, int width) {
        view.getLayoutParams().width = width;
    }


    @TargetApi(14)
    public static void hideActionbar(Activity activity) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            activity.getActionBar().hide();
        }
    }

    public static void shortToast(String msg, Context context) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public static void longToast(String msg, Context context) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }


    public static synchronized void saveImageToDisk(Context context, String ID, Bitmap image) {
        try {
            String path = "IMG" + ID;
            java.io.FileOutputStream out = context.openFileOutput(path,
                    Context.MODE_PRIVATE);
            image.compress(Bitmap.CompressFormat.PNG, 90, out);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static synchronized Bitmap readImageFromDisk(Context context, String ID, boolean blurred) {
        try {
            String path = blurred ? "IMG_" + ID + "_blur" : "IMG_" + ID;
            java.io.FileInputStream in = context.openFileInput(path);
            return BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public static SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }


    @TargetApi(16)
    public static void setBackgroundToView(View view, Drawable background) {
        if (android.os.Build.VERSION.SDK_INT >= 16) {
            view.setBackground(background);
        } else {
            view.setBackgroundDrawable(background);
        }
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }


}
