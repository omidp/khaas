package com.omidbiz.khaas.listeners;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.omidbiz.khaas.KhaasConstant;
import com.omidbiz.khaas.handlers.KhaasUtil;

/**
 * @author : Omid Pourhadi omidpourhadi [AT] gmail [dot] com
 */
public class AlarmReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        Log.i(KhaasConstant.LOG_TAG, "ALARM SERVICE");
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone r = RingtoneManager.getRingtone(context, notification);
        r.play();
    }
}
