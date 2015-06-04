package com.omidbiz.khaas;

import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.util.Log;

import com.omidbiz.khaas.handlers.KhaasUtil;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p/>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends PreferenceActivity //implements SharedPreferences.OnSharedPreferenceChangeListener
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_notification);
        Preference voice_pref = findPreference("voice_pref");
        voice_pref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
        {
            @Override
            public boolean onPreferenceClick(Preference preference)
            {
                try
                {
                    if (KhaasUtil.hasHoneycomb())
                    {

                        final Intent gvoiceIntent = new Intent(Intent.ACTION_MAIN);
                        gvoiceIntent.setComponent(new ComponentName("com.google.android.googlequicksearchbox", "com.google.android.voicesearch.VoiceSearchPreferences"));
                        startActivity(gvoiceIntent);
                    }
                    else
                    {
                        final Intent gvoiceIntent = new Intent(Intent.ACTION_MAIN);
                        gvoiceIntent.setComponent(new ComponentName("com.google.android.voicesearch", "com.google.android.voicesearch.VoiceSearchPreferences"));
                        startActivity(gvoiceIntent);
                    }

                }
                catch (final Exception e)
                {
                    Log.i(KhaasConstant.LOG_TAG, e.getMessage());
                }
                return true;
            }
        });
    }


    @Override
    protected void onStop()
    {
        super.onStop();
        //getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }
}
