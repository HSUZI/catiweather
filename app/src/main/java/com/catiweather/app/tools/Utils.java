package com.catiweather.app.tools;

import android.util.Log;

/**
 * Created by Administrator on 2016/9/29.
 */

//#define DEBUG
public class Utils {
    public static boolean isDebug = true;
    public static String TAG = "Catiweather";
    public Utils() {}

    public final static void print(String s) {
        // #ifdef DEBUG
        if (isDebug) {
            if (null != s) {
                int length = s.length();
                int offset = 3000;
                if (length > offset) {// 解决报文过长，打印不全的问题
                    int n = 0;
                    for (int i = 0; i < length; i += offset) {
                        n += offset;
                        if (n > length)
                            n = length;
                        Log.e( TAG,s.substring(i, n));
                    }
                } else {
                    Log.e(TAG, s);
                }
            }
            // #endif
        }
    }
}
