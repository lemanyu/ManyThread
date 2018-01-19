package com.hsap.myapplication.binder;

import android.os.RemoteException;


/**
 * Created by zhao on 2018/1/19.
 */

public class ICalculateImpl extends ICalculate.Stub {
    @Override
    public int add(int first, int second) throws RemoteException {
        return first+second;
    }

    @Override
    public int sub(int first, int second) throws RemoteException {
        return first-second;
    }
}
