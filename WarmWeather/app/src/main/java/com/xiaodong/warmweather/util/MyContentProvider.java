package com.xiaodong.warmweather.util;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

import org.litepal.crud.DataSupport;

public class MyContentProvider extends ContentProvider {
    private static UriMatcher uriMatcher;
    private static final String AUTHORITY = "com.xiaodong.warmweather.provider";
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY,"county",0);
        uriMatcher.addURI(AUTHORITY,"county/#",1);
    }

    @Override
    public boolean onCreate() {

        return true;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public String getType(Uri uri) {
        String mime = null;
        switch (uriMatcher.match(uri)){
            case 0:
                mime = "vnd.android.cursor.dir/vnd."+AUTHORITY+"county";
                break;
            case 1:
                mime = "vnd.android.cursor.item/vnd."+AUTHORITY+"county";
                break;
        }
        return mime;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return uri;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Cursor cursor = null;
        switch (uriMatcher.match(uri)){
            case 0:
                cursor=DataSupport.findBySQL("select * from county ");
                break;
            case 1:
                String ctId = uri.getPathSegments().get(1);
                cursor=DataSupport.findBySQL("select * from county where id=? " ,ctId);
                break;
        }

        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        return 0;
    }
}
