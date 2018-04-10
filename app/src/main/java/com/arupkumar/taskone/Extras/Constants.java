package com.arupkumar.taskone.Extras;


/**
 * Created by Arup Kumar Pramanik on 04/10/2018.
 */

public class Constants {


    public static final int NOTIFICATION_ID = 0;
    public static final int REQUEST_ID_FOR_STORAGE_WRITE_PERMISSIONS = 2002;
    public static final String PERCENTAGE = "percentage";
    public static final String RESULTCODE = "resultCode";
    public static boolean IS_SERVICE_RUNNING = false;
    public static final String NOTIFICATION = "com.arupkumar.taskone.receiver";


    public interface ACTION {
        public static String MAIN_ACTION = "com.arupkumar.taskone.action.main";
        public static String INIT_ACTION = "com.arupkumar.taskone.action.init";
        public static String STARTFOREGROUND_ACTION = "com.arupkumar.taskone.action.startforeground";
        public static String STOPFOREGROUND_ACTION = "com.arupkumar.taskone.action.stopforeground";
    }

    public interface NOTIFICATIONID {
        public static int FOREGROUND_SERVICE = 101;
    }
}