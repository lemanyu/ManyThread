package com.hsap.myapplication.binder;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by zhao on 2018/1/19.
 */

public class BinderPoolService extends Service {
    private static String TAG="BinderPoolService";
    private Binder mBinderPool=new BinderPool.BinderPoolImpl() ;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.e(TAG, "onBind: ");
        return mBinderPool;
    }

}
