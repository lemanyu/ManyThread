package com.hsap.myapplication.socket;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

/**
 * Created by zhao on 2018/1/18.
 */

public class SocketService extends Service {
    private static String TAG="SocketService";
    private boolean mIsServiceDestoryed = false;
    private String[] mDefinedMessages = new String[] {
            "你好啊，哈哈",
            "请问你叫什么名字呀？",
            "今天北京天气不错啊，shy",
            "你知道吗？我可是可以和多个人同时聊天的哦",
            "给你讲个笑话吧：据说爱笑的人运气不会太差，不知道真假。"
    };

    @Override
    public void onCreate() {
        super.onCreate();
        new Thread(new TcpServer()).start();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    private class TcpServer implements Runnable{

        @Override
        public void run() {
            ServerSocket socket=null;
            try {
                socket=new ServerSocket(8688);
            } catch (IOException e) {
                e.printStackTrace();
            }
            while (!mIsServiceDestoryed){
                try {
                    final Socket client = socket.accept();
                    Log.e(TAG, "run: 链接成功 " );
                    new Thread(){
                        @Override
                        public void run() {
                            super.run();
                            try {
                                responseClient(client);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    private void responseClient(Socket client) throws IOException {
        //接受客户端的消息
        BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        //发送服务器的消息
        PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(client.getOutputStream())),true);
       while (!mIsServiceDestoryed){
           String s = in.readLine();
           if (s==null){
               return;
           }else {
               Log.e(TAG, "responseClient: "+s );
           }
           int i = new Random().nextInt(mDefinedMessages.length);
           String msg = mDefinedMessages[i];
           out.println(msg);
       }
       in.close();
       out.close();
       client.close();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mIsServiceDestoryed=true;
    }
}
