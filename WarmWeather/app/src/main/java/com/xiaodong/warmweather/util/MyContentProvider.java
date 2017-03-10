package com.xiaodong.warmweather.util;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import org.litepal.crud.DataSupport;

public class MyContentProvider extends ContentProvider {
    private static UriMatcher uriMatcher;
    private static final String AUTHORITY = "com.xiaodong.warmweather.provider";
    private MyDataBaseHelper myDataBaseHelper;
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY,"county",0);
        uriMatcher.addURI(AUTHORITY,"county/#",1);
        uriMatcher.addURI(AUTHORITY,"suggestion",2);
        uriMatcher.addURI(AUTHORITY,"suggestion/#",3);
    }

    @Override
    public boolean onCreate() {
        myDataBaseHelper = Utility.getDefaultDb(getContext());
        return true;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int num = 0;
        SQLiteDatabase sqLiteDatabase = myDataBaseHelper.getWritableDatabase();
        switch (uriMatcher.match(uri)) {
            case 2:
                num=sqLiteDatabase.delete("suggestion",selection,selectionArgs);
                break;
            case 3:
                String seleId = uri.getPathSegments().get(1);
                num=sqLiteDatabase.delete("suggestion","id=?",new String[]{seleId});
                break;
        }
        return num;
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
            case 3:
                mime = "vnd.android.cursor.dir/vnd."+AUTHORITY+"suggestion";
                break;
            case 4:
                mime = "vnd.android.cursor.item/vnd."+AUTHORITY+"suggestion";
                break;
        }
        return mime;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase sqLiteDatabase = myDataBaseHelper.getWritableDatabase();
        Uri uri1=null;
        switch (uriMatcher.match(uri)) {
            case 2:
            case 3:
                long suggestionId = sqLiteDatabase.insert("suggestion",null,values);
                uri1 = Uri.parse("content://"+AUTHORITY+"/suggestion/"+suggestionId);
                break;
        }
        return uri1;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Cursor cursor = null;
        SQLiteDatabase sqLiteDatabase = myDataBaseHelper.getWritableDatabase();
        switch (uriMatcher.match(uri)){
            case 0:
                cursor=DataSupport.findBySQL("select * from county ");
                break;
            case 1:
                String ctId = uri.getPathSegments().get(1);
                cursor=DataSupport.findBySQL("select * from county where id=? " ,ctId);
                break;
            case 2:
                cursor = sqLiteDatabase.query("suggestion", projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case 3:
                String suggestionId = uri.getPathSegments().get(1);
                cursor = sqLiteDatabase.query("suggestion", projection, "id=?", new String[]{suggestionId}, null, null, sortOrder);
                break;
        }

        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        SQLiteDatabase sqLiteDatabase = myDataBaseHelper.getWritableDatabase();
        int num = 0;
        switch (uriMatcher.match(uri)) {
            case 2:
                num = sqLiteDatabase.update("suggestion",values,selection,selectionArgs);
                break;
            case 3:
                String seleId = uri.getPathSegments().get(1);
                num = sqLiteDatabase.update("suggestion",values,"id=?",new String[]{seleId});
                break;
        }
        return num;
    }
}
