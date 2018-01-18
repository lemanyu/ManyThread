package com.hsap.myapplication.aidl;
import com.hsap.myapplication.aidl.Book;
import com.hsap.myapplication.aidl.IOnNewBookArrivedListener;
interface IBookManager {
     List<Book> getBookList();
     void addBook(in Book book);
     void registerLister(IOnNewBookArrivedListener lister);
     void unregisterLister(IOnNewBookArrivedListener lister);
}