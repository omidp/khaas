package com.omidbiz.khaas;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;

import com.loopj.android.http.RequestParams;
import com.omidbiz.khaas.handlers.CommandResponseHandler;
import com.omidbiz.khaas.handlers.PlaceResponseHandler;
import com.omidbiz.khaas.util.RestUtil;

import java.util.List;

/**
 * @author : Omid Pourhadi omidpourhadi [AT] gmail [dot] com
 */
public class LocationActivity extends MusicPlayerActivity implements LocationListener
{

    protected LocationManager locationManager;
    protected String typeForLocationUpdate;
    protected String locationProvider;


    @Override
    public void onCreate(Bundle savedInstanceState, int layoutResId)
    {
        super.onCreate(savedInstanceState, layoutResId);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        List<String> providers = locationManager.getProviders(true);
        if(providers != null && providers.size() >0)
        {
            locationProvider = providers.get(0);
            locationManager.requestLocationUpdates(locationProvider, 0, 0, this);
        }
    }




    @Override
    public void onLocationChanged(Location location)
    {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras)
    {
        Log.i(KhaasConstant.LOG_TAG, "onStatusChanged");
    }

    @Override
    public void onProviderEnabled(String provider)
    {
        Log.i(KhaasConstant.LOG_TAG, provider);
    }

    @Override
    public void onProviderDisabled(String provider)
    {
        Log.i(KhaasConstant.LOG_TAG, provider);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        if (locationManager != null)
            locationManager.removeUpdates(this);
    }


}
