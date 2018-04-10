package com.arupkumar.taskone.Utils;

import android.util.Log;

import com.arupkumar.taskone.Config.BuildConfig;


/**
 * Created by Android Developer on 4/23/2017.
 */

public class ShowLog {

    public static void i(String tag, String string) {
        if (BuildConfig.KEY_IS_DEVELOPMENT_BUILD) Log.i(tag, string);
    }

    public static void e(String tag, String string) {
        if (BuildConfig.KEY_IS_DEVELOPMENT_BUILD)
            Log.e(tag, string);
    }

    public static void d(String tag, String string) {
        if (BuildConfig.KEY_IS_DEVELOPMENT_BUILD) Log.d(tag, string);
    }

    public static void v(String tag, String string) {
        if (BuildConfig.KEY_IS_DEVELOPMENT_BUILD) Log.v(tag, string);
    }

    public static void w(String tag, String string) {
        if (BuildConfig.KEY_IS_DEVELOPMENT_BUILD) Log.w(tag, string);
    }
}