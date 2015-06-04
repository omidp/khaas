package com.omidbiz.khaas.handlers;

import android.util.Log;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.omidbiz.khaas.BotResponse;
import com.omidbiz.khaas.KhaasConstant;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;
import org.omidbiz.core.axon.Axon;
import org.omidbiz.core.axon.AxonBuilder;

import java.io.UnsupportedEncodingException;

/**
 * @author : Omid Pourhadi omidpourhadi [AT] gmail [dot] com
 */
public class JokeResponseHandler extends AsyncHttpResponseHandler
{

    private BotResponse result;
    Axon axon = new AxonBuilder().create();

    @Override
    public void onSuccess(int i, Header[] headers, byte[] responseBody)
    {
        if (responseBody != null)
        {
            try
            {
                String json = new String(responseBody, "UTF-8");
                JSONObject jsonObject = new JSONObject(json);
                String content = jsonObject.getString("content");
                if (KhaasUtil.isNotEmpty(content))
                {
                    result = new BotResponse(content, BotResponse.ACTION.NOTHING, 0);
                }
            }
            catch (UnsupportedEncodingException e)
            {
                Log.i(KhaasConstant.LOG_TAG, e.getMessage());
            }
            catch (JSONException e)
            {
                Log.i(KhaasConstant.LOG_TAG, e.getMessage());
            }
        }
    }

    @Override
    public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable)
    {
        Log.i(KhaasConstant.LOG_TAG, "unable to tell joke");
    }

    public BotResponse getResult()
    {
        return result;
    }
}
