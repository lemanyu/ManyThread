package com.hsap.myapplication.provider;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.hsap.myapplication.R;
import com.hsap.myapplication.aidl.Book;

/**
 * Created by zhao on 2018/1/18.
 */

public class ProviderActivity extends AppCompatActivity {
    private static final String TAG = "ProviderActivity";
    private Uri book=Uri.parse("content://com.hsap.myapplication.book.provider/book");
    private Uri user=Uri.parse("content://com.hsap.myapplication.book.provider/user");
    private String bookNewId;
    private String userNewId;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provider);
        Button query = findViewById(R.id.query);
        Button add = findViewById(R.id.add);
        Button delete = findViewById(R.id.delete);
        Button update = findViewById(R.id.update);
        query.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cursor bookCursor = getContentResolver().query(book, new String[]{"_id", "name"}, null, null, null);
                while (bookCursor.moveToNext()){
                    Book book = new Book();
                    book.bookId=bookCursor.getInt(0);
                    book.bookName=bookCursor.getString(1);
                    Log.e(TAG, "query_book"+book.toString());
                }
                bookCursor.close();
               /* Cursor userCursor = getContentResolver().query(user, new String[]{"_id", "name"}, null, null, null);
                while (userCursor.moveToNext()){
                    User user = new User();
                    user.userId=userCursor.getInt(0);
                    user.userName=userCursor.getString(1);
                    Log.e(TAG, "query_user"+user.toString());
                }
                userCursor.close();*/
            }
        });
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues values = new ContentValues();
                values.put("name","程序设计");
                Uri bookNewUri = getContentResolver().insert(book, values);
                bookNewId=bookNewUri.getPathSegments().get(1);
               /* Log.e(TAG, "insert: book成功");
                values.put("name","小军");
                Uri userNewUri=getContentResolver().insert(user,values);
                userNewId=userNewUri.getPathSegments().get(1);
                Log.e(TAG, "insert: user成功");*/
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri=Uri.parse(book+"/"+bookNewId);//删除添加的数据
                getContentResolver().delete(uri,null,null);
            }
        });
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uribook=Uri.parse(book+"/"+bookNewId);
                Uri uriuser=Uri.parse(user+"/"+userNewId);
                ContentValues values = new ContentValues();
                values.put("name","aaa");
                getContentResolver().update(uribook,values,null,null);
                /*values.put("name","bbb");
                getContentResolver().update(uriuser,values,null,null);*/
            }
        });

    }
}
