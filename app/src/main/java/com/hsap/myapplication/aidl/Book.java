package com.hsap.myapplication.aidl;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by zhao on 2018/1/16.
 */

public class Book implements Parcelable {
    public int bookId;
    public String bookName;
    public Book(int bookId,String bookName){
        this.bookId=bookId;
        this.bookName=bookName;
    }
    public Book(){

    }
    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    protected Book(Parcel in) {
        bookId = in.readInt();
        bookName = in.readString();
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(bookId);
        dest.writeString(bookName);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Book> CREATOR = new Creator<Book>() {
        @Override
        public Book createFromParcel(Parcel in) {
            return new Book(in);
        }

        @Override
        public Book[] newArray(int size) {
            return new Book[size];
        }
    };

    @Override
    public String toString() {
        return bookId+"---"+bookName;
    }
}
