package com.hsap.myapplication.messager;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hsap.myapplication.R;

/**
 * Created by zhao on 2018/1/17.
 */

public class MessageActivity extends AppCompatActivity {
    private TextView tv_callback;
    private LinearLayout ll_message;
    private boolean isConn;
    private Messenger mService;
    private int mA;
    private Button btn_add;
    @SuppressLint("HandlerLeak")
    private Messenger messenger =new Messenger(new Handler(){
        @Override
        public void handleMessage(Message msgFromServer) {
            switch (msgFromServer.what){
                case MessageService.MSG_SUM:
                    TextView tv = ll_message.findViewById(msgFromServer.arg1);
                    tv.setText(tv.getText() + "=>" + msgFromServer.arg2);
                    break;
                    default:
            }
            super.handleMessage(msgFromServer);
        }
    });
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        tv_callback = findViewById(R.id.id_tv_callback);
        ll_message = findViewById(R.id.ll_message);
        btn_add = findViewById(R.id.id_btn_add);
        bindServiceInvoked();
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int a=mA++;
                int b=(int)(Math.random()*100);
                TextView tv = new TextView(MessageActivity.this);
                tv.setText(a+"+"+b+"=caculating ..");
                tv.setId(a);
                ll_message.addView(tv);
                Message msgFromClient = Message.obtain(null, MessageService.MSG_SUM, a, b);
               //将客户端的messager一起发送到服务器
                msgFromClient.replyTo=messenger;
                if(isConn){
                    try {
                        //服务器Messenger发送消息，服务器端就可以处理
                        mService.send(msgFromClient);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }

            }
        });

    }

    private void bindServiceInvoked() {
        Intent intent = new Intent();
        intent.setAction("com.zhao.message");
        intent.setPackage(getPackageName());
        bindService(intent,conn,BIND_AUTO_CREATE);

    }
    private ServiceConnection conn=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService=new Messenger(service);
            isConn=true;
            tv_callback.setText("绑定");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            messenger=null;
            isConn=false;
            tv_callback.setText("解绑");
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(conn);
    }
}
