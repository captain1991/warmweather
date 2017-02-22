package com.xiaodong.warmweather.util;

import android.util.Log;

/**
 * Created by yxd on 2017/2/22.
 */
public class LogUtil {
    public static final  int LEVEL_NONE = 0;
    public static final int LEVEL_ERROR_ONLY = 1;
    public static final int LEVEL_ERROR_WARN = 2;
    public static final int LEVEL_ERROR_WARN_INFO = 3;
    public static final int LEVEL_ERROR_WARN_INFO_DEBUG = 4;

    public static int level = LEVEL_ERROR_WARN_INFO_DEBUG;
    public static String LOG_TAG="WARMWEATHER";

    public static void d(String msg){
        if(level>=LEVEL_ERROR_WARN_INFO_DEBUG){
            Log.d(LOG_TAG,msg);
        }
    }

    public static void i(String msg){
        if(level>=LEVEL_ERROR_WARN_INFO){
            Log.i(LOG_TAG, msg);
        }
    }

    public static void w(String msg){
        if(level>=LEVEL_ERROR_WARN){
            Log.w(LOG_TAG,msg);
        }
    }

    public static void e(String msg){
        if(level>=LEVEL_ERROR_ONLY){
            Log.e(LOG_TAG,msg);
        }
    }
}
