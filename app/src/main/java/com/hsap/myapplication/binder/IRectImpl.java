package com.hsap.myapplication.binder;

import android.os.RemoteException;


/**
 * Created by zhao on 2018/1/19.
 */

public class IRectImpl extends IRect.Stub {
    @Override
    public int area(int length, int height) throws RemoteException {
        return length*height;
    }

    @Override
    public int perimeter(int lenght, int height) throws RemoteException {
        return (lenght+height)*2;
    }
}
