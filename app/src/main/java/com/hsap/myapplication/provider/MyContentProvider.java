package com.hsap.myapplication.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import java.net.PortUnreachableException;
import java.net.URI;

public class MyContentProvider extends ContentProvider {
    private static String TAG="MyContentProvider";
    public static final String AUTHORITY="com.hsap.myapplication.book.provider";
    public static final Uri BOOK_CONTENT_URI=Uri.parse("content://"+AUTHORITY+"/book/");
    public static final Uri USER_CONTENT_URI=Uri.parse("content://"+AUTHORITY+"/user/");
    public static final int BOOK_CODE=0;
    public static final int USER_CODE=1;
    public static final int BOOK_ITEM=2;
    public static final int USER_ITEM=3;
   private static  final UriMatcher mUriMatcher=new UriMatcher(UriMatcher.NO_MATCH);
   static {
       mUriMatcher.addURI(AUTHORITY,"book",BOOK_CODE);
       mUriMatcher.addURI(AUTHORITY,"book/#",BOOK_ITEM);
       mUriMatcher.addURI(AUTHORITY,"user",USER_CODE);
       mUriMatcher.addURI(AUTHORITY,"user/#",USER_ITEM);
   }

    private SQLiteDatabase db;

    @Override
    public boolean onCreate() {
        Log.e(TAG, "onCreate: thread"+Thread.currentThread().getName());
        initProviderData(getContext());
        return true;
    }

    private void initProviderData(Context context) {
        db = new DbOpenHelper(context).getWritableDatabase();
        db.execSQL("delete from "+DbOpenHelper.BOOK_TABLE_NAME);
        db.execSQL("delete from "+DbOpenHelper.USER_TABLE_NAME);
        db.execSQL("insert into book VALUES(1,'Android');");
        db.execSQL("insert into book VALUES(2,'IOS');");
        db.execSQL("insert into book VALUES(3,'Html');");
        db.execSQL("insert into book VALUES(4,'Java');");
        db.execSQL("insert into user VALUES(1,'xiaoming');");
        db.execSQL("insert into user VALUES(2,'xiaohong');");
        db.execSQL("insert into user VALUES(3,'xiaojun');");
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Log.e(TAG, "query: thread"+Thread.currentThread().getName() );
        String table = getTable(uri);
        if(table==null){
            throw new IllegalArgumentException("no uri:"+uri);
        }
        Cursor cursor=null;
        switch (mUriMatcher.match(uri)){
            case BOOK_CODE:
                cursor=db.query(table,projection,selection,selectionArgs,null,null,sortOrder);
                break;
            case BOOK_ITEM:
                String bookId = uri.getPathSegments().get(1);
                cursor=db.query(table,projection,"id=?",new String[]{bookId},null,null,sortOrder);
                break;
            case USER_CODE:
                cursor=db.query(table,projection,selection,selectionArgs,null,null,sortOrder);
                break;
            case USER_ITEM:
                String userId = uri.getPathSegments().get(1);
                cursor=db.query(table,projection,"id=?",new String[]{userId},null,null,sortOrder);
                break;
                default:
        }
        return cursor;
    }



    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Log.e(TAG, "insert: " );
        String table = getTable(uri);
        if (table==null){
            throw new IllegalArgumentException("no uri:"+uri);
        }
        db.insert(table,null,values);
        getContext().getContentResolver().notifyChange(uri,null);
        switch (mUriMatcher.match(uri)){
            case BOOK_CODE:
            case BOOK_ITEM:
                long bookId = db.insert(table, null, values);
                uri= Uri.parse("content://"+AUTHORITY+"/book/"+bookId);
                break;
            case USER_CODE:
            case USER_ITEM:
                long userId = db.insert(table, null, values);
                uri= Uri.parse("content://"+AUTHORITY+"/user/"+userId);
                break;
                default:
        }
        return uri;
    }
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        Log.e(TAG, "delete: " );
        String table = getTable(uri);
        if (table==null){
            throw new IllegalArgumentException("no uri:"+uri);
        }
        int delete = 0;
        switch (mUriMatcher.match(uri)){
            case BOOK_CODE:
                delete= db.delete(table,selection,selectionArgs);
                break;
            case BOOK_ITEM:
                String bookId = uri.getPathSegments().get(1);
                delete=db.delete(table,"_id = ?",new String[]{bookId});
                break;
            case USER_CODE:
                delete= db.delete(table,selection,selectionArgs);
                break;
            case USER_ITEM:
                String userId = uri.getPathSegments().get(1);
                delete=db.delete(table,"_id = ?",new String[]{userId});
                break;
                default:
        }
        if (delete>0){
            getContext().getContentResolver().notifyChange(uri,null);
        }

        return delete;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {

        Log.e(TAG, "update: " );
        String table = getTable(uri);
        if (table==null){
            throw new IllegalArgumentException("no uri:"+uri);
        }
        int update = 0;
        db.update(table, values, selection, selectionArgs);
        switch (mUriMatcher.match(uri)){
            case BOOK_CODE:
                update= db.update(table, values, selection, selectionArgs);
                break;
            case BOOK_ITEM:
                String bookId = uri.getPathSegments().get(1);
                update=db.update(table,values,"_id=?",new String[]{bookId});
                break;
            case USER_CODE:
                update= db.update(table, values, selection, selectionArgs);
                break;
            case USER_ITEM:
                String userId = uri.getPathSegments().get(1);
                update=db.update(table,values,"_id=?",new String[]{userId});
                break;
                default:
        }
        if (update>0){
            getContext().getContentResolver().notifyChange(uri,null);
        }
        return update;
    }

    @Override
    public String getType(Uri uri) {
        Log.e(TAG, "getType: " );
        switch (mUriMatcher.match(uri)){
            case BOOK_CODE:
                return "vnd.android.cursor.dir/vnd."+AUTHORITY+".book";

            case BOOK_ITEM:
                return "vnd.android.cursor.item/vnd."+AUTHORITY+".book";

            case USER_CODE:

                return "vnd.android.cursor.dir/vnd."+AUTHORITY+".user";
            case USER_ITEM:
                return "vnd.android.cursor.item/vnd."+AUTHORITY+".user";

            default:
        }
        return null;
    }
    private String getTable(Uri uri) {
       String tableName=null;
        switch (mUriMatcher.match(uri)){
            case BOOK_CODE:
            case BOOK_ITEM:
                tableName=DbOpenHelper.BOOK_TABLE_NAME;
                break;
            case USER_CODE:
            case USER_ITEM:
                tableName=DbOpenHelper.USER_TABLE_NAME;
                break;
                default:break;
        }
    return tableName;
    }




}
