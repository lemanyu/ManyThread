package com.hsap.myapplication.socket;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.hsap.myapplication.R;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;


public class SocketActivity extends AppCompatActivity {
    private static final String TAG = "SocketActivity";
    private EditText et;
    private Button send;
    private TextView wenben;
    private Socket mClient;
    private PrintWriter mPrintWriter;
    @SuppressLint("HandlerLeak")
    private Handler mHandler=new Handler(){
        @SuppressLint("SetTextI18n")
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
                    wenben.setText(wenben.getText()+(String)msg.obj);
                    break;
                case 1:
                    send.setEnabled(true);
                    break;
                    default:break;
            }
        }
    };
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_socket);
        et = findViewById(R.id.et);
        send = findViewById(R.id.send);
        wenben = findViewById(R.id.wenben);
       startService(new Intent(this,SocketService.class));
       new Thread(){
           @Override
           public void run() {
               super.run();
               contentTCPServer();
           }
       }.start();
        send.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                final String msg = et.getText().toString();
                if (!TextUtils.isEmpty(msg) && mPrintWriter != null) {
                    mPrintWriter.println(msg);
                    et.setText("");
                    String time = formatDateTime(System.currentTimeMillis());
                    final String showedMsg = "self " + time + ":" + msg + "\n";
                    wenben.setText(wenben.getText() + showedMsg);
                }
            }
        });
    }

    private void contentTCPServer() {
        Socket socket=null;
        while (socket==null) {
            try {
                socket = new Socket("localhost", 8688);
                mClient = socket;
                mPrintWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                mHandler.sendEmptyMessage(1);
                Log.e(TAG, "contentTCPServer: 链接成功");
            } catch (IOException e) {
                e.printStackTrace();
                SystemClock.sleep(1000);
                Log.e(TAG, "contentTCPServer: 链接失败");
            }
        }
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                while (!SocketActivity.this.isFinishing()){
                    String msg = br.readLine();
                    if (msg!=null){
                        String time = formatDateTime(System.currentTimeMillis());
                        final String showedMsg = "server " + time + ":" + msg
                                + "\n";
                        mHandler.obtainMessage(0,showedMsg).sendToTarget();
                    }
                }
                mPrintWriter.close();
                br.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    @SuppressLint("SimpleDateFormat")
    private String formatDateTime(long l) {
        return new SimpleDateFormat("(HH:mm:ss)").format(new Date(l));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mClient != null) {
            try {
                mClient.shutdownInput();
                mClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
