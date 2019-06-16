package com.example.finalproject.Helper;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseAccessHelper {
    private SQLiteOpenHelper openHelper;
    private SQLiteDatabase db;
    private static DatabaseAccessHelper instance;

    Cursor c = null;
    Cursor curl = null;

    private String itemname, itemimage;

    private DatabaseAccessHelper(Context context){
        this.openHelper = new DatabaseOpenHelper(context);
    }

    public static DatabaseAccessHelper getInstance(Context context){
        if(instance == null){
            instance = new DatabaseAccessHelper(context);
        }
        return instance;
    }

    //open db
    public void open(){
        this.db=openHelper.getReadableDatabase();
    }

    //closing db connection
    public void close(){
       if(db!=null)
         this.db.close();

    }

    //쿼리
    public String getNAME(String name, String color, String print){ //shape

        c=db.rawQuery("select ITEM_NAME from pill where DRUG_SHAPE ='"+name+"'AND COLOR_CLASS1 = '"+color+"'AND PRINT_FRONT = '"+print+"' OR PRINT_BACK = '"+print+"'"   , new String[]{});//shape

        StringBuffer buffer = new StringBuffer();

        while(c.moveToNext()){
            itemname = c.getString(0); //itemname가져옴
            buffer.append(""+itemname);
        }

        return buffer.toString();
    }

    public String npgetNAME(String name, String print){ //shape

        c=db.rawQuery("select ITEM_NAME from pill where DRUG_SHAPE ='"+name+"'AND PRINT_FRONT = '"+print+"' OR PRINT_BACK = '"+print+"'"   , new String[]{});//shape

        StringBuffer buffer = new StringBuffer();

        while(c.moveToNext()){
            itemname = c.getString(0); //itemname가져옴
            buffer.append(""+itemname);
        }

        return buffer.toString();
    }

    public String cpgetNAME(String color, String print){ //shape

        c=db.rawQuery("select ITEM_NAME from pill where  COLOR_CLASS1 = '"+color+"'AND PRINT_FRONT = '"+print+"' OR PRINT_BACK = '"+print+"'"   , new String[]{});//shape

        StringBuffer buffer = new StringBuffer();

        while(c.moveToNext()){
            itemname = c.getString(0); //itemname가져옴
            buffer.append(""+itemname);
        }

        return buffer.toString();
    }

    public String ncgetNAME(String name, String color){ //shape

        c=db.rawQuery("select ITEM_NAME from pill where DRUG_SHAPE ='"+name+"'AND COLOR_CLASS1 = '"+color+"'", new String[]{});//shape

        StringBuffer buffer = new StringBuffer();

        while(c.moveToNext()){
            itemname = c.getString(0); //itemname가져옴
            buffer.append(""+itemname);
        }

        return buffer.toString();
    }

    public String getURL(String item){

        curl=db.rawQuery("select ITEM_IMAGE from pill where ITEM_NAME ='"+item+"'", new String[]{});//shape

        StringBuffer buffer = new StringBuffer();

        while(curl.moveToNext()){
            itemimage = curl.getString(0); //itemimage가져옴
            buffer.append(""+itemimage);
        }
        return buffer.toString();
    }

}
