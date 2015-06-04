package com.omidbiz.khaas.handlers;

import org.apache.http.Header;

import android.util.Log;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.omidbiz.khaas.KhaasConstant;

public class CheckUserResponseHandler extends AsyncHttpResponseHandler{

    @Override
    public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3)
    {
        Log.i(KhaasConstant.LOG_TAG, "unable to connect to server");
    }

    @Override
    public void onSuccess(int arg0, Header[] arg1, byte[] arg2)
    {
        Log.i(KhaasConstant.LOG_TAG, "connect to server");
    }

}
