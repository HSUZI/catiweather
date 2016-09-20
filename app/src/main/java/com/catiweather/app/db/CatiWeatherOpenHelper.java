package com.catiweather.app.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 创建数据库表，用于存放省市县的各种数据
 * Created by Administrator on 2016/9/19.
 */
public class CatiWeatherOpenHelper extends SQLiteOpenHelper{

    /**
    *   Province表建表语句
    * */
    public static final String CREATE_PROVINCE = "create table Province ("
            + "id integer primary key autoincrement, "
            + "province_name text, "
            + "province_code text)";

    /**
     *  City表建表语句
     * */
    public static final String CREATE_CITY = "create table City ("
            + "id integer primary key autoincrement, "
            + "city_name text, "
            + "city_code text, "
            + "province_id integer)";

    /**
     * Country表建表语句
     * */
    public static final String  CREATE_COUNTRY = "create table County ("
            + "id integer primary key autoincrement, "
            + "country_name text, "
            + "country_code text, "
            + "city_id integer)";

    public CatiWeatherOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_PROVINCE); //创建Province表
        sqLiteDatabase.execSQL(CREATE_CITY); //创建City表
        sqLiteDatabase.execSQL(CREATE_COUNTRY); //创建Country表
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
