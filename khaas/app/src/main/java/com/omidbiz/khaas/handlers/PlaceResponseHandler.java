package com.omidbiz.khaas.handlers;

import android.util.Log;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.omidbiz.khaas.BotResponse;
import com.omidbiz.khaas.KhaasConstant;
import com.omidbiz.khaas.R;
import com.omidbiz.khaas.ui.GooglePlace;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author : Omid Pourhadi omidpourhadi [AT] gmail [dot] com
 */
public class PlaceResponseHandler extends AsyncHttpResponseHandler
{

    List<GooglePlace> resultList;

    @Override
    public void onSuccess(int size, Header[] headers, byte[] responseBody)
    {
        try
        {
            if (responseBody != null)
            {
                String json = new String(responseBody, "UTF-8");
                if (KhaasUtil.isJsonArray(json))
                {
                    resultList = new ArrayList<GooglePlace>();
                    JSONArray arr = new JSONArray(json);
                    for (int i = 0; i < arr.length(); i++)
                    {
                        GooglePlace gp = new GooglePlace();
                        JSONObject jsonObject = arr.getJSONObject(i);
                        if(jsonObject.has("name"))
                            gp.setName(jsonObject.getString("name"));
                        if(jsonObject.has("vicinity"))
                            gp.setVicinity(jsonObject.getString("vicinity"));
                        if(jsonObject.has("latitude"))
                            gp.setLatitude(jsonObject.getDouble("latitude"));
                        if(jsonObject.has("longitude"))
                            gp.setLongitude(jsonObject.getDouble("longitude"));
                        resultList.add(gp);
                    }
                }
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

    public List<GooglePlace> getResultList()
    {
        return resultList;
    }

    @Override
    public void onFailure(int i, Header[] headers, byte[] responseBody, Throwable throwable)
    {
        if (responseBody != null)
        {
            try
            {
                Log.i(KhaasConstant.LOG_TAG, new String(responseBody, "UTF-8"));
            }
            catch (UnsupportedEncodingException e)
            {
                Log.i(KhaasConstant.LOG_TAG, e.getMessage());
            }
        }
    }
}
