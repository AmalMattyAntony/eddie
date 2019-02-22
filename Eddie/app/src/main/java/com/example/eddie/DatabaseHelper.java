package com.example.eddie;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.TextView;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "contact_test.db";
    public static final String TABLE_NAME = "contact_table";
    public static final String COL_1 = "ID";
    public static final String COL_2 = "NAME";
    public static final String COL_3 = "MOBILE";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " +  TABLE_NAME +" (NAME TEXT, MOBILE STRING ,PRIMARY KEY (NAME,MOBILE))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
    }
    public boolean insertdata(String name , String mobile){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2,name);
        contentValues.put(COL_3,mobile);
        long result = db.insert(TABLE_NAME,null,contentValues);
        if(result == -1)
            return true;
        else
            return false;
    }
    public String getData(String name)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        //db.execSQL("select * from "+TABLE_NAME+";");
        String args[]=new String[1];
        args[0]="%"+name+"%";
        Cursor c=db.rawQuery("select MOBILE from "+TABLE_NAME+" where NAME like ?;",args);
        //tv.setText(c.getCount()+"");
        while(c.moveToNext())
        {
            return c.getString(0).replaceAll("\\D","");
        }
        return "did not find any matching contacts";
    }

}

