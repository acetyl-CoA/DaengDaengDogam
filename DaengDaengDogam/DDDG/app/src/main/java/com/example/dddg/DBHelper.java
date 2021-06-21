package com.example.dddg;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    public DBHelper(Context content,String name,SQLiteDatabase.CursorFactory factory,int version){
        super(content,name,factory,version);
    }

    public void onCreate(SQLiteDatabase db) {
        String sql = "create table if not exists dog(_id integer primary key autoincrement,title text,image img, summary text,ex text);";
        db.execSQL(sql);
    }

    public void onUpgrade(SQLiteDatabase db,int oldVersion,int newVersion){
        String sql = "drop table if exists dog";
        db.execSQL(sql);
        onCreate(db);
    }
}
