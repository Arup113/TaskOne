package com.arupkumar.taskone.Utils;

import android.content.Context;
import android.net.ConnectivityManager;

/**
 * Created by Arup Kumar Pramanik on 04/10/2018.
 */

public class CommonUtils {

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }
}
