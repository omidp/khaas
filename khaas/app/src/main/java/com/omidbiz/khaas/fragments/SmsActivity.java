package com.omidbiz.khaas.fragments;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.omidbiz.khaas.BotResponse;
import com.omidbiz.khaas.KhaasActivity;
import com.omidbiz.khaas.KhaasConstant;
import com.omidbiz.khaas.R;
import com.omidbiz.khaas.handlers.KhaasUtil;
import com.omidbiz.khaas.manager.HandlerManager;
import com.omidbiz.khaas.speech.SpeechRecognizerActivity;

import java.util.List;

/**
 * @author : Omid Pourhadi omidpourhadi [AT] gmail [dot] com
 */
public class SmsActivity extends SpeechRecognizerActivity
{

    private EditText content;
    private Button smsSend;
    private String sendTo;
    private TextView txtNumber;
    private TextView smsTextContent;
    private Button readButton;
    private String smsContent;
    private TextView headerText;
    SmsManager smsManager;
    TelephonyManager tm;


    @Override
    protected void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);
        sendTo = intent.getStringExtra(KhaasConstant.SMS_SEND_TO);
        smsContent = intent.getStringExtra(KhaasConstant.SMS_CONTENT);
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState, R.layout.sms);
        //
        headerText = (TextView) findViewById(R.id.headerTxt);
        headerText.setTypeface(KhaasUtil.getTypeFace(getApplicationContext(), KhaasConstant.FONT_NAME), Typeface.NORMAL);
        //
        sendTo = getIntent().getStringExtra(KhaasConstant.SMS_SEND_TO);
        smsContent = getIntent().getStringExtra(KhaasConstant.SMS_CONTENT);
        //
        txtNumber = (TextView) findViewById(R.id.txtNumber);
        txtNumber.setText(sendTo);
        //
        content = (EditText) findViewById(R.id.smsContent);
        content.addTextChangedListener(new SmsTextWatcher());
        smsSend = (Button) findViewById(R.id.sendSmsButton);
        smsSend.setTypeface(KhaasUtil.getTypeFace(getApplicationContext(), KhaasConstant.FONT_NAME), Typeface.NORMAL);
        smsSend.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String text = content.getText().toString();
                if (KhaasUtil.isNotEmpty(text) && KhaasUtil.isNotEmpty(sendTo))
                {
                    Context ctx = getApplicationContext();
                    tm = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
                    smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(sendTo, tm.getLine1Number(), text, null, null);
                    content.setText("");
                    KhaasUtil.createToast(getApplicationContext(), getString(R.string.sms_send_successfully), Toast.LENGTH_SHORT);
                }
            }
        });

        //READ SMS

        smsTextContent = (TextView) findViewById(R.id.smsTextContent);
        readButton = (Button) findViewById(R.id.readButton);
        readButton.setTypeface(KhaasUtil.getTypeFace(getApplicationContext(), KhaasConstant.FONT_NAME), Typeface.NORMAL);
        if (KhaasUtil.isNotEmpty(smsContent))
        {
            smsTextContent.setText(smsContent);
            smsTextContent.setVisibility(View.VISIBLE);
            readButton.setVisibility(View.VISIBLE);
        }
        readButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                KhaasUtil.createToast(getApplicationContext(), getString(R.string.wait), Toast.LENGTH_LONG);
                String smsContentTv = smsTextContent.getText().toString();
                if (KhaasUtil.isNotEmpty(smsContentTv))
                {
                    voice = true;
                    BotResponse br = new BotResponse(smsContentTv, BotResponse.ACTION.NOTHING, 0);
                    processResult(br);
                }
            }
        });

    }

    @Override
    public void onBackPressed()
    {
        finish();
        Intent i = new Intent(SmsActivity.this, KhaasActivity.class);
        startActivity(i);
    }

    @Override
    protected void directSpeechNotAvailable()
    {
        KhaasUtil.createToast(getApplicationContext(), getString(R.string.speechError), Toast.LENGTH_SHORT);
    }

    @Override
    protected void speechNotAvailable()
    {
        KhaasUtil.createToast(getApplicationContext(), getString(R.string.speechError), Toast.LENGTH_SHORT);
    }

    @Override
    protected void speechError(int error)
    {
        KhaasUtil.createToast(getApplicationContext(), getString(R.string.speechError), Toast.LENGTH_SHORT);
    }

    @Override
    protected void receiveWhatWasHeard(List<String> heard, float[] confidenceScores)
    {
        StringBuilder sb = new StringBuilder();
        sb.append(heard.get(0));
        content.setText(sb.toString());
    }

    @Override
    public void onEndOfSpeech()
    {
        super.onEndOfSpeech();
        speechToggleButton.setVisibility(View.GONE);
        smsSend.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onPause()
    {
        if (smsManager != null)
        {
            smsManager = null;
        }
        if (tm != null)
        {
            tm = null;
        }
        getIntent().removeExtra(KhaasConstant.SMS_CONTENT);
        getIntent().removeExtra(KhaasConstant.SMS_SEND_TO);
        super.onPause();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
    }

    private class SmsTextWatcher implements TextWatcher
    {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after)
        {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count)
        {
            speechToggleButton.setVisibility(View.GONE);
            smsSend.setVisibility(View.VISIBLE);
        }

        @Override
        public void afterTextChanged(Editable s)
        {

        }
    }

}

