package com.example.shashidhar.may17th_1;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

/**
 * Created by shashidhar on 25/5/16.
 */
public class DbHelper extends SQLiteOpenHelper {
    public DbHelper(Context context) {
        super(context,"contacts.db",null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query;
        query="CREATE TABLE data (id INTEGER PRIMARY KEY AUTOINCREMENT, no TEXT, name TEXT)";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS data");
        onCreate(db);
    }
    public void insertData(String no,String name){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues val=new ContentValues();
        val.put("NO",no);
        val.put("NAME",name);
        long f=db.insert("data",null,val);
        if(f==-1)
            System.out.println("FAILED!!!");
        else
            System.out.println("SUCCEED!!!"+no);
    }
    public Cursor getData(String no){
        SQLiteDatabase db=this.getReadableDatabase();
        String selectQuery = "SELECT * FROM data WHERE no='"+no+"'";
        Cursor cursor=db.rawQuery(selectQuery,null);
        return cursor;
    }

    public void delete(){
        //  String name="Test";
        String tab="data";
        String nn="no";
        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL("DELETE FROM "+tab);//+" WHERE "+nn+"='"+name+"'");
        db.close();
    }
}
