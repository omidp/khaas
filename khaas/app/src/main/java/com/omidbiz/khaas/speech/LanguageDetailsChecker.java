package com.omidbiz.khaas.speech;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @author : Omid Pourhadi omidpourhadi [AT] gmail [dot] com
 */
public class LanguageDetailsChecker extends BroadcastReceiver
{

    OnLanguageDetailsChange checker;

    private String languagePref;
    private List<String> supportedLangs;

    public LanguageDetailsChecker(OnLanguageDetailsChange checker)
    {
        this.checker = checker;
    }

    @Override
    public void onReceive(Context context, Intent intent)
    {
        Bundle bundle = getResultExtras(true);
        if (bundle.containsKey(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE))
        {
            languagePref = bundle.getString(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE);
        }
        if (bundle.containsKey(RecognizerIntent.EXTRA_SUPPORTED_LANGUAGES))
        {
            supportedLangs = bundle.getStringArrayList(RecognizerIntent.EXTRA_SUPPORTED_LANGUAGES);
        }
        if (checker != null)
            checker.recieveChange(this);
    }


    public String matchLanguage(Locale locale)
    {
        String lang = null;
        String targetLang = locale.toString().replace("_", "-");
        for (String language : supportedLangs)
        {
            if (targetLang.contains(language) || supportedLangs.contains(targetLang))
            {
                lang = language;
            }
        }
        return lang;
    }

}
