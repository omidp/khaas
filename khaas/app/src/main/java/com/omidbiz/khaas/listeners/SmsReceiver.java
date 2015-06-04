package com.omidbiz.khaas.listeners;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.SmsMessage;
import android.widget.Toast;

import com.omidbiz.khaas.KhaasActivity;
import com.omidbiz.khaas.KhaasConstant;
import com.omidbiz.khaas.fragments.SmsActivity;

/**
 * @author : Omid Pourhadi omidpourhadi [AT] gmail [dot] com
 */
public class SmsReceiver extends BroadcastReceiver
{

    private static final String SMS_REC_ACTION = "android.provider.Telephony.SMS_RECEIVED";


    @Override
    public void onReceive(Context context, Intent intent)
    {
        if (intent.getAction().equals(SmsReceiver.SMS_REC_ACTION))
        {
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
            boolean startActivity = pref.getBoolean("lang_sms", true);
            if (startActivity)
            {
                StringBuilder sb = new StringBuilder();
                Bundle bundle = intent.getExtras();
                String phoneNumber = "";
                if (bundle != null)
                {
                    Object[] pdus = (Object[]) bundle.get("pdus");
                    for (Object pdu : pdus)
                    {
                        SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdu);
                        phoneNumber = smsMessage.getDisplayOriginatingAddress();
                        sb.append(smsMessage.getDisplayMessageBody());
                    }
                }

                Intent khaasIntent = new Intent(context, SmsActivity.class);
                khaasIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                //app doesn't reopen it
//                khaasIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                khaasIntent.putExtra(KhaasConstant.SMS_SEND_TO, phoneNumber);
                khaasIntent.putExtra(KhaasConstant.SMS_CONTENT, sb.toString());
                context.startActivity(khaasIntent);
            }

        }
    }


}
