package com.example.android.movies.app.data;

import android.provider.BaseColumns;

public class Contract {


    public static final String TABLE_MOVIES = "movies";
    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TABLE_MOVIES + " (" +
                    Product.COLUMN_ID + " INTEGER PRIMARY KEY," +
                    Product.COLUMN_TITLE + " TEXT NOT NULL, " +
                    Product.COLUMN_YEAR + " TEXT NOT NULL, " +
                    Product.COLUMN_IMAGE + " TEXT NOT NULL, " +
                    Product.COLUMN_PLOT + " TEXT NOT NULL " + " )";

    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + TABLE_MOVIES;

    public Contract() {
    }

    public static abstract class Product implements BaseColumns {

        public static final String COLUMN_ID = "_id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_YEAR = "year";
        public static final String COLUMN_IMAGE = "image";
        public static final String COLUMN_PLOT = "plot";

    }


}
