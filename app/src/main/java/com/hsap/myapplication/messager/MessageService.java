package com.hsap.myapplication.messager;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;

/**
 * Created by zhao on 2018/1/17.
 */

public class MessageService extends Service {
    public static final int MSG_SUM = 0x110;

    @SuppressLint("HandlerLeak")
    private Messenger messenger=new Messenger(new Handler(){
        @Override
        public void handleMessage(Message msgformClient) {
            Message msgtoClient = Message.obtain(msgformClient);
            switch (msgtoClient.what){
                case MSG_SUM:
                   // msgtoClient.what=MSG_SUM;
                    try {
                        Thread.sleep(2000);
                        msgtoClient.arg2=msgformClient.arg1+msgformClient.arg2;
                        //客户端的信使发送消息，这样客户端的Messenger可以接受处理
                        //客户端的Messager发，客户端可接受处理
                        msgformClient.replyTo.send(msgtoClient);
                    } catch (InterruptedException e) {
                        e.printStackTrace();

                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
            }
            super.handleMessage(msgformClient);
        }
    });

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return messenger.getBinder();
    }
}
