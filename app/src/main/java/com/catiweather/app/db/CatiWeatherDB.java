package com.catiweather.app.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/** 创建数据库cati_weather类
 * Created by Administrator on 2016/9/19.
 */
public class CatiWeatherDB {

    /**
     * 数据库名 cati_weather
     * */
    public static final String DB_NAME = "cati_weather";

    /**
     * 数据库版本
     * */
    public static final int VERSION = 1;

    private static CatiWeatherDB catiWeatherDB;

    private SQLiteDatabase db;

    /**
     * 将构造方法私有化
     * */
    private CatiWeatherDB(Context context) {
        CatiWeatherOpenHelper dbHelper = new CatiWeatherOpenHelper(context, DB_NAME, null, VERSION);
        db = dbHelper.getWritableDatabase();
    }

}
