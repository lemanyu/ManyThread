package com.hsap.myapplication.aidl;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.hsap.myapplication.R;

import java.util.List;

public class AidlActivity extends AppCompatActivity {

    private static final String TAG = "AidlActivity";
    @SuppressLint("HandlerLeak")
    private Handler mHandler =new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
          Log.e(TAG, "handleMessage: "+msg.obj );
                    break;
                    default:
            }
            super.handleMessage(msg);
        }
    };
    private IBookManager iBookManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aidl);
        Intent intent = new Intent(this, IBookManagerService.class);
        bindService(intent,coon,BIND_AUTO_CREATE);
    }
    private ServiceConnection coon=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
           // new Messenger(service);
            iBookManager = IBookManager.Stub.asInterface(service);
            try {
                List<Book> bookList = iBookManager.getBookList();
                Log.e(TAG, "onServiceConnected: "+bookList.getClass().getCanonicalName());
                Log.e(TAG, "onServiceConnected: "+bookList.toString());
                bookList.add(new Book(3,"开发艺术"));
                Log.e(TAG, "onServiceConnected: "+bookList.toString());
                iBookManager.registerLister(mOnNewList);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            iBookManager=null;
            Log.e(TAG, "onServiceDisconnected: 解绑" );
        }
    };
 private IOnNewBookArrivedListener mOnNewList=new IOnNewBookArrivedListener.Stub(){

     @Override
     public void onNewBookArrived(Book newBook) throws RemoteException {
           mHandler.obtainMessage(1,newBook).sendToTarget();
     }
 };
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(iBookManager!=null&&iBookManager.asBinder().isBinderAlive()){
            try {
                iBookManager.unregisterLister(mOnNewList);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        unbindService(coon);
    }
}
