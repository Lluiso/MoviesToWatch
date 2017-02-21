package com.example.android.movies.app.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

public class MyContentProvider extends ContentProvider {

    private DBHelper myDB;

    private static final String AUTHORITY = "com.example.android.sunshine.app.data.MyContentProvider";
    public static final String MOVIES_TABLE = Contract.TABLE_MOVIES;
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + MOVIES_TABLE);

    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    public static final int MOVIES = 1;
    public static final int MOVIES_ID = 2;

    static {
        sURIMatcher.addURI(AUTHORITY, MOVIES_TABLE, MOVIES);
        sURIMatcher.addURI(AUTHORITY, MOVIES_TABLE + "/#", MOVIES_ID);
    }

    @Override
    public boolean onCreate() {
        myDB = new DBHelper(this.getContext(), "movieDB.db", null, 1);
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(Contract.TABLE_MOVIES);

        int uriType = sURIMatcher.match(uri);

        switch (uriType) {
            case MOVIES_ID:
                queryBuilder.appendWhere(Contract.Product.COLUMN_ID + "=" + uri.getLastPathSegment());
                break;
            case MOVIES:
                break;
            default:
                throw new IllegalArgumentException("Unknown URI");
        }

        Cursor cursor = queryBuilder.query(myDB.getReadableDatabase(),
                projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int uriType = sURIMatcher.match(uri);

        SQLiteDatabase sqlDB = myDB.getWritableDatabase();

        long id = 0;
        switch (uriType) {
            case MOVIES:
                id = sqlDB.insert(Contract.TABLE_MOVIES, null, values);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.parse(MOVIES_TABLE + "/" + id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = myDB.getWritableDatabase();
        int rowsDeleted = 0;

        switch (uriType) {
            case MOVIES:
                rowsDeleted = sqlDB.delete(Contract.TABLE_MOVIES,
                        selection,
                        selectionArgs);
                break;

            case MOVIES_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = sqlDB.delete(Contract.TABLE_MOVIES,
                            Contract.Product.COLUMN_ID + "=" + id,
                            null);
                } else {
                    rowsDeleted = sqlDB.delete(Contract.TABLE_MOVIES,
                            Contract.Product.COLUMN_ID + "=" + id
                                    + " and " + selection,
                            selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = myDB.getWritableDatabase();
        int rowsUpdated = 0;

        switch (uriType) {
            case MOVIES:
                rowsUpdated = sqlDB.update(Contract.TABLE_MOVIES,
                        values,
                        selection,
                        selectionArgs);
                break;
            case MOVIES_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated =
                            sqlDB.update(Contract.TABLE_MOVIES,
                                    values,
                                    Contract.Product.COLUMN_ID + "=" + id,
                                    null);
                } else {
                    rowsUpdated =
                            sqlDB.update(Contract.TABLE_MOVIES,
                                    values,
                                    Contract.Product.COLUMN_ID + "=" + id
                                            + " and "
                                            + selection,
                                    selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }
}
