package com.example.android.movies.app.data;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "movieDB.db";

    private ContentResolver myCR;

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        myCR = context.getContentResolver();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Contract.SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + Contract.TABLE_MOVIES);
    }

    public int deleteProduct(String title) {

        boolean result = false;

        String selection = "title = \"" + title + "\"";
        MyContentProvider provider = new MyContentProvider();
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("Delete from " + Contract.TABLE_MOVIES + " WHERE " + Contract.Product.COLUMN_TITLE + " =  \"" + title + "\"");

        //int rowsDeleted = provider.delete(MyContentProvider.CONTENT_URI, selection, null);

        return 1;

    }

    public void addProduct(String title, String year, String image, String plot) {

        ContentValues values = new ContentValues();
        values.put(Contract.Product.COLUMN_TITLE, title);
        values.put(Contract.Product.COLUMN_YEAR, year);
        values.put(Contract.Product.COLUMN_IMAGE, image);
        values.put(Contract.Product.COLUMN_PLOT, plot);



        SQLiteDatabase db = this.getWritableDatabase();

        db.insert(Contract.TABLE_MOVIES, null, values);
    }

    public Cursor findProduct(String title) {
        String query = "Select * FROM " + Contract.TABLE_MOVIES + " WHERE " + Contract.Product.COLUMN_TITLE + " =  \"" + title + "\"";

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        return cursor;
    }
    public Cursor findAll() {
        String query = "Select * FROM " + Contract.TABLE_MOVIES;

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        return cursor;
    }

}
