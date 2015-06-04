package com.omidbiz.khaas.util;


import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;
import com.omidbiz.khaas.KhaasActivity;
import com.omidbiz.khaas.KhaasConstant;

import org.apache.http.HttpEntity;

import java.util.Map;

/**
 * @author : Omid Pourhadi omidpourhadi [AT] gmail [dot] com
 */
public class RestUtil
{
    private SyncHttpClient client = new SyncHttpClient();

    private AsyncHttpClient asyncClient = new AsyncHttpClient();

    public void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler)
    {
//        client.setTimeout(90000);
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public void get(String url, AsyncHttpResponseHandler responseHandler)
    {
//        client.setTimeout(90000);
        client.get(getAbsoluteUrl(url), responseHandler);
    }

    public void getExternalService(String url, AsyncHttpResponseHandler responseHandler)
    {
//        client.setTimeout(90000);
        client.get(url, responseHandler);
    }

    public void getAsync(String url, RequestParams params, AsyncHttpResponseHandler responseHandler)
    {
//        asyncClient.setTimeout(90000);
        asyncClient.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public void put(String url, Context context, HttpEntity entity, AsyncHttpResponseHandler responseHandler)
    {
        client.put(context, getAbsoluteUrl(url), entity, "application/json", responseHandler);
    }

    public void post(String url, AsyncHttpResponseHandler responseHandler, Context context, HttpEntity entity, Map<String, Object> params)
    {
        if (params != null)
        {
            StringBuffer sb = new StringBuffer("?");
            int i = 0;
            for (Map.Entry<String, Object> entry : params.entrySet())
            {
                if (i > 0)
                    sb.append("&");
                sb.append(entry.getKey()).append("=").append(entry.getValue());
                i++;
            }
            url = url + sb;
        }
        post(url, responseHandler, context, entity);
    }

    public void post(String url, AsyncHttpResponseHandler responseHandler, Context context, HttpEntity entity)
    {
        client.post(context, getAbsoluteUrl(url), entity, "application/json", responseHandler);
    }

    private String getAbsoluteUrl(String relativeUrl)
    {
        return KhaasConstant.BASE_URL + relativeUrl;
    }
}
