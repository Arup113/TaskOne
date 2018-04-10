package com.arupkumar.taskone.Utils;

import android.content.Context;
import android.net.ConnectivityManager;

/**
 * Created by arup on 10/4/18.
 */

public class CommonUtils {

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }
}
