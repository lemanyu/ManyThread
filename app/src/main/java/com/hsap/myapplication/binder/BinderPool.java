package com.hsap.myapplication.binder;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;


import java.util.concurrent.CountDownLatch;

/**
 * 用户端的aidl通过binderPool找到Binder连接池的相对应的binder再绑定service
 *  binder池
 */

public class BinderPool {
   private static String TAG="BinderPool";
    private Context mContext;
    private volatile  static BinderPool mInstance;
    private CountDownLatch mCountDownLatch;
    private  IBinderPool mBinderPool;

    public static final int BINDER_CALCULATE = 0;
    public static final int BINDER_RECT = 1;
    private BinderPool(Context context){
       this.mContext=context.getApplicationContext();
       connectBinderPoolService();
   }
  public static BinderPool getInstance(Context context){
       if (mInstance==null){
          synchronized (BinderPool.class){
              if (mInstance==null){
                  mInstance=new BinderPool(context);
              }
          }
       }
       return mInstance;
  }

    private synchronized void connectBinderPoolService() {
        mCountDownLatch = new CountDownLatch(1);
        Intent intent = new Intent(mContext,BinderPoolService.class);
        mContext.bindService(intent,coon,Context.BIND_AUTO_CREATE);
        try {
            //并发线程的工具类，等待状态
            mCountDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    private ServiceConnection coon=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBinderPool = IBinderPool.Stub.asInterface(service);
            try {
                mBinderPool.asBinder().linkToDeath(mDeathRecipient,0);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            mCountDownLatch.countDown();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
 private Binder.DeathRecipient mDeathRecipient=  new Binder.DeathRecipient(){
     @Override
     public void binderDied() {
         mBinderPool.asBinder().unlinkToDeath(mDeathRecipient,0);
         mBinderPool=null;
         connectBinderPoolService();
     }
 };
 public IBinder queryBinder(int binderCode) {
        IBinder binder = null;
        try {
            if (mBinderPool != null) {
                binder = mBinderPool.queryBinder(binderCode);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return binder;
    }
 public static class BinderPoolImpl extends IBinderPool.Stub{
     @Override
     public IBinder queryBinder(int binderCode) throws RemoteException {
         IBinder mIBinder=null;
          switch (binderCode){
              case BINDER_CALCULATE:
                  Log.e(TAG, "queryBinder: "+BINDER_CALCULATE);
                  mIBinder=new ICalculateImpl();
                  break;
              case BINDER_RECT:
                  mIBinder=new IRectImpl();
                  break;
              default:break;
          }
         return mIBinder;
     }
 }

}
