package com.hsap.myapplication.aidl;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;


import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by zhao on 2018/1/17.
 */

public class IBookManagerService extends Service {
   private String TAG="IBookManagerService";
   private AtomicBoolean mIsServiceDestoryed=new AtomicBoolean(false);
   private CopyOnWriteArrayList<Book> mBookList=new CopyOnWriteArrayList<>();
   private RemoteCallbackList<IOnNewBookArrivedListener> mListerList=new RemoteCallbackList<>();
   private Binder mBinder =new IBookManager.Stub(){

       @Override
       public List<Book> getBookList() throws RemoteException {
           return mBookList;
       }

       @Override
       public void addBook(Book book) throws RemoteException {
                  mBookList.add(book);
       }

       @Override
       public void registerLister(IOnNewBookArrivedListener lister) throws RemoteException {
            mListerList.register(lister);
           Log.e(TAG, "registerLister: 绑定成功" );
       }

       @Override
       public void unregisterLister(IOnNewBookArrivedListener lister) throws RemoteException {
           mListerList.unregister(lister);
           Log.e(TAG, "registerLister: 解绑成功" );
       }
        //服务器权限验证
       @Override
       public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
           int check = checkCallingOrSelfPermission("com.zhaogui.aaa.permission");
           if (check==PackageManager.PERMISSION_DENIED){
               return false;
           }
           //通过getCallingUid()得到客户端的uid， 再通过PackageManager根据uid查到package name进行检查.
           String packageName = null;
           String[] packages = getPackageManager().getPackagesForUid(
                   getCallingUid());
           if (packages != null && packages.length > 0) {
               packageName = packages[0];
           }
           Log.d(TAG, "onTransact: " + packageName);
           if (!packageName.startsWith("com.zhaogui")) {
               return false;
           }
           return super.onTransact(code, data, reply, flags);
       }
   };

    @Nullable
    @Override
    //客户端验证
    public IBinder onBind(Intent intent) {
        int check = checkCallingOrSelfPermission("com.zhaogui.aaa.permission");
        if (check== PackageManager.PERMISSION_DENIED){
            return null;
        }
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mBookList.add(new Book(1,"Android"));
        mBookList.add(new Book(2,"Ios"));
        new Thread(new ServiceWorker()).start();
    }
  private class ServiceWorker implements Runnable{

      @Override
      public void run() {
          while (!mIsServiceDestoryed.get()){
              try {
                  Thread.sleep(5000);

              } catch (InterruptedException e) {
                  e.printStackTrace();
              }
              int id = mBookList.size() + 1;
              Book newBook=new Book(id,"new Book#  "+id);
              try {
                  onNewBookArrived(newBook);
              } catch (RemoteException e) {
                  e.printStackTrace();
              }

          }
      }
  }

    private void onNewBookArrived(Book newBook) throws RemoteException{
        mBookList.add(newBook);
        int i = mListerList.beginBroadcast();
        for (int j = 0; j <i; j++) {
            IOnNewBookArrivedListener item = mListerList.getBroadcastItem(j);
            if (item!=null){
                item.onNewBookArrived(newBook);
            }
        }
        mListerList.finishBroadcast();
    }
}
