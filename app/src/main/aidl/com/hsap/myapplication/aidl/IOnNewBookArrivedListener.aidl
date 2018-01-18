package com.hsap.myapplication.aidl;
import com.hsap.myapplication.aidl.Book;
interface IOnNewBookArrivedListener{
void onNewBookArrived(in Book newBook);
}