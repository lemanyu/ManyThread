package com.hsap.myapplication.binder;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.hsap.myapplication.R;


public class BinderActivity extends AppCompatActivity implements View.OnClickListener{


    private static final String TAG = "BinderActivity";
    private BinderPool mBinderPool;
    private ICalculate mCalculate;
    private int mRect;
    @SuppressLint("HandlerLeak")
    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {

            bt1.setOnClickListener(BinderActivity.this);
            bt2.setOnClickListener(BinderActivity.this);
            bt3.setOnClickListener(BinderActivity.this);
            bt4.setOnClickListener(BinderActivity.this);
        }
    };
    private Button bt1;
    private Button bt2;
    private Button bt3;
    private Button bt4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_binder);
        getBinderPool();
        initView();

    }

    private void getBinderPool() {
        Log.e(TAG, "getBinderPool" );
        new Thread(new Runnable() {
            @Override
            public void run() {
                mBinderPool = BinderPool.getInstance(BinderActivity.this);
                Log.e(TAG, "run: mHandler" );
                mHandler.obtainMessage().sendToTarget();
            }
        }).start();
       /* new Thread(){
            @Override
            public void run() {
                mBinderPool = BinderPool.getInstance(BinderActivity.this);
                mHandler.obtainMessage().sendToTarget();
            }
        }.start();*/
    }

    private void initView() {
        bt1 = findViewById(R.id.bt1);
        bt2 = findViewById(R.id.bt2);
        bt3 = findViewById(R.id.bt3);
        bt4 = findViewById(R.id.bt4);
        bt1.setOnClickListener(BinderActivity.this);
        bt2.setOnClickListener(BinderActivity.this);
        bt3.setOnClickListener(BinderActivity.this);
        bt4.setOnClickListener(BinderActivity.this);

    }

    @Override
    public void onClick(View v) {
        try {
        switch (v.getId()){
            case R.id.bt1:
                //求和
                IBinder iBinder = mBinderPool.queryBinder(BinderPool.BINDER_CALCULATE);
                ICalculate calculate = ICalculateImpl.asInterface(iBinder);
                Log.e(TAG, "add:"+(calculate.add(3,4)));
                break;
            case R.id.bt2:
                //求差
                IBinder binder = mBinderPool.queryBinder(BinderPool.BINDER_CALCULATE);
                mCalculate=ICalculateImpl.asInterface(binder);
                Log.e(TAG, "sub: "+(mCalculate.sub(3,5)));
                break;
            case R.id.bt3:
                //求面积
                try {
                 mRect= IRectImpl.asInterface(mBinderPool.queryBinder(BinderPool.BINDER_RECT)).area(3,5);
                    Log.e(TAG, "area: "+(mRect));
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.bt4:
                //求周长
                try {
                    mRect= IRectImpl.asInterface(mBinderPool.queryBinder(BinderPool.BINDER_RECT)).perimeter(3,4);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                Log.e(TAG, "perimeter: "+mRect);
                break;
            default:break;
        }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
