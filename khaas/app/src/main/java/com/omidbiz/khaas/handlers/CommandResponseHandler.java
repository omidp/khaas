package com.omidbiz.khaas.handlers;

import java.io.UnsupportedEncodingException;

import org.apache.http.Header;
import org.omidbiz.core.axon.Axon;
import org.omidbiz.core.axon.AxonBuilder;

import android.content.Context;
import android.util.Log;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.omidbiz.khaas.BotResponse;
import com.omidbiz.khaas.KhaasConstant;
import com.omidbiz.khaas.R;


/**
 * @author omidbiz
 */
public class CommandResponseHandler extends AsyncHttpResponseHandler
{

    private BotResponse result;
    Axon axon = new AxonBuilder().create();
    Context ctx;

    public CommandResponseHandler(Context ctx)
    {
        this.ctx = ctx;
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error)
    {
        if (responseBody != null)
        {
            String json = ctx.getString(R.string.fatal_error);
            result = new BotResponse(json, BotResponse.ACTION.NOTHING, 0);
        }
//        else
//        {
//            String json = ctx.getString(R.string.connection_error);
//            result = new BotResponse(json, BotResponse.ACTION.NOTHING, 0);
//        }
    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody)
    {
        try
        {
            if (responseBody != null)
            {
                String json = new String(responseBody, "UTF-8");
                if(KhaasUtil.isNotEmpty(json))
                    result = axon.toObject(json, BotResponse.class, null);
            }
        }
        catch (UnsupportedEncodingException e)
        {
            Log.i(KhaasConstant.LOG_TAG, e.getMessage());
        }
    }

    public BotResponse getResult()
    {
        return result;
    }

}
