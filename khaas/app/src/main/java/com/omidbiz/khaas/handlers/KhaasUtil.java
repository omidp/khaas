package com.omidbiz.khaas.handlers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.provider.Settings;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.omidbiz.khaas.BotResponse;
import com.omidbiz.khaas.KhaasActivity;
import com.omidbiz.khaas.KhaasConstant;
import com.omidbiz.khaas.R;
import com.omidbiz.khaas.music.MusicAdapter;
import com.omidbiz.khaas.speech.LanguageDetailsChecker;
import com.omidbiz.khaas.speech.OnLanguageDetailsChange;
import com.omidbiz.khaas.ui.BasicAdapter;
import com.omidbiz.khaas.ui.LayoutListView;
import com.omidbiz.khaas.ui.PlaceArrayAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Closeable;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;

/**
 * @author : Omid Pourhadi omidpourhadi [AT] gmail [dot] com
 */
public class KhaasUtil
{

    private static int[] colors = new int[]{Color.parseColor("#EEE9E9"), Color.parseColor("#FAF8F8")};


    public static int getRowColor(int index)
    {
        if (index > colors.length)
        {
            return colors[0];
        }
        return colors[index];
    }

    public static boolean isEmpty(String input)
    {
        if (input == null)
        {
            return true;
        }
        return input.trim().length() == 0;
    }

    public static boolean isJson(String input)
    {
        if (isEmpty(input))
            return false;
        return (input.trim().startsWith("{") && input.trim().endsWith("}"));
    }

    public static boolean isJsonArray(String input)
    {
        if (isEmpty(input))
            return false;
        return (input.trim().startsWith("[") && input.trim().endsWith("]"));
    }

    public static String buildJsonCommand(String cmd)
    {
        JSONObject json = new JSONObject();
        try
        {
            json.put("command", cmd);
            return json.toString();
        }
        catch (JSONException e)
        {
        }
        return null;
    }

    public static boolean isNotEmpty(String cmd)
    {
        return isEmpty(cmd) == false;
    }

    public static TextView createTextView(Context ctx, String msg)
    {
        TextView textView = new TextView(ctx);
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(18);
        textView.setTypeface(getTypeFace(ctx, KhaasConstant.FONT_NAME), Typeface.NORMAL);
        textView.setTextColor(Color.parseColor("#7f7f7f"));
        textView.setPadding(4, 7, 4, 4);
        textView.setBackgroundResource(R.drawable.msg_out);
        textView.setText(msg);
        textView.setVisibility(View.VISIBLE);
        return textView;
    }

    public static boolean hasInternet(Context ctx)
    {
        ConnectivityManager connManager = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (mWifi.isConnected())
            return true;
        //
        NetworkInfo mMobile = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (mMobile.isConnected())
            return true;
        return false;
    }

    public static void createToast(Context ctx, String msg, int duration)
    {
        Toast toast = Toast.makeText(ctx, msg, duration);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    public static String getAndroidId(Context ctx)
    {
        return Settings.Secure.getString(ctx.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public static boolean hasFroyo()
    {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO;
    }

    public static boolean hasGingerbread()
    {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD;
    }

    /**
     * Uses static final constants to detect if the device's platform version is Honeycomb or
     * later.
     */
    public static boolean hasHoneycomb()
    {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
    }

    public static void closeQuietly(Closeable io)
    {
        if (io != null)
        {
            try
            {
                io.close();
            }
            catch (IOException e)
            {

            }
        }
    }

    /**
     * Uses static final constants to detect if the device's platform version is Honeycomb MR1 or
     * later.
     */
    public static boolean hasHoneycombMR1()
    {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1;
    }

    /**
     * Uses static final constants to detect if the device's platform version is ICS or
     * later.
     */
    public static boolean hasICS()
    {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH;
    }


    public static void updateAdapter(Context ctx, LayoutListView layout, TextView textView)
    {
        ListAdapter listAdapter = layout.getAdapter();
        if (listAdapter instanceof KhaasActivity.ContactsAdapter)
        {
            listAdapter = null;
            layout.setAdapter(null);
        }
        if (listAdapter instanceof PlaceArrayAdapter)
        {
            listAdapter = null;
            layout.setAdapter(null);
        }
        if (listAdapter instanceof MusicAdapter)
        {
            listAdapter = null;
            layout.setAdapter(null);
        }
        BasicAdapter adapter = (BasicAdapter) listAdapter;//
        if (adapter == null)
            adapter = new BasicAdapter(ctx, R.layout.chat_row);
        layout.setAdapter(adapter);
        adapter.add(textView);
        adapter.notifyDataSetChanged();
    }

    public static void processResponse(Context ctx, BotResponse br, LayoutListView layoutListView)
    {
        if (br == null)
            return;
        BotResponse.ACTION resultAction = br.getAction();
        String msg = br.getMsg();

        if (resultAction.equals(BotResponse.ACTION.NOTHING))
        {
            Log.i(KhaasConstant.LOG_TAG, msg);
            TextView textView = KhaasUtil.createTextView(ctx, msg);
            updateAdapter(ctx, layoutListView, textView);
        }
    }

    public static boolean isSpeechAvailable(Context ctx)
    {
        PackageManager packageManager = ctx.getPackageManager();
        List<ResolveInfo> resolveInfos = packageManager.queryIntentActivities(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
        boolean available = true;
        if (resolveInfos.size() == 0)
            available = false;
        return available;
    }

    public static Intent getRecognizerIntent(boolean persian)
    {
        Intent recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

        if (persian)
        {
            recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "fa-IR");
            recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, new Locale("fa", "IR"));
        }
        else
        {
            recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US");
            recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, new Locale("en", "US"));
        }

        recognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, KhaasActivity.class.getPackage().getName());
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3);
        return recognizerIntent;
    }

    public static Intent getSimpleRecognizerIntent(String prompt)
    {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, prompt);
        return intent;
    }

    public static void getLanguageDetails(Context ctx, OnLanguageDetailsChange checker)
    {
        Intent details = new Intent(RecognizerIntent.ACTION_GET_LANGUAGE_DETAILS);
        LanguageDetailsChecker langDetail = new LanguageDetailsChecker(checker);
        ctx.sendOrderedBroadcast(details, null, langDetail, null, Activity.RESULT_OK, null, null);
    }


    public static boolean hasLocationProvider(LocationManager locMgr)
    {
        List<String> providers = locMgr.getProviders(true);
        boolean available = true;
        if (providers.size() == 0)
            available = false;
        return available;
    }

    /**
     * @param time : 10:10
     * @return
     */
    public static long parseTime(String time)
    {
        long milis = 0;
        if (time != null && time.length() > 0)
        {
            Calendar cal_now = Calendar.getInstance();
            Calendar cal_alarm = Calendar.getInstance();
            Date now = new Date();
            cal_now.setTime(now);
            cal_alarm.setTime(now);
            if (time.indexOf(" ") != -1)
            {
                String[] splitter = time.split(" ");
                int hour = Integer.parseInt(splitter[0]);
                int minute = Integer.parseInt(splitter[1]);
                cal_alarm.set(Calendar.HOUR_OF_DAY, hour);//set the alarm time
                cal_alarm.set(Calendar.MINUTE, minute);
                cal_alarm.set(Calendar.SECOND, 0);
                if (hour > 12)
                    cal_alarm.set(Calendar.AM_PM, Calendar.PM);
                else
                    cal_alarm.set(Calendar.AM_PM, Calendar.AM);
                milis = cal_alarm.getTimeInMillis();
            }
            else if (time.length() == 2)
            {
                int hour = Integer.parseInt(time);
                cal_alarm.set(Calendar.HOUR_OF_DAY, hour);//set the alarm time
                cal_alarm.set(Calendar.MINUTE, 0);
                cal_alarm.set(Calendar.SECOND, 0);
                if (hour > 12)
                    cal_alarm.set(Calendar.AM_PM, Calendar.PM);
                else
                    cal_alarm.set(Calendar.AM_PM, Calendar.AM);
                milis = cal_alarm.getTimeInMillis();
            }
        }

        return milis;
    }

    private static final Hashtable<String, Typeface> cache = new Hashtable<String, Typeface>();

    public static Typeface getTypeFace(Context c, String assetPath)
    {
        synchronized (cache)
        {
            if (!cache.containsKey(assetPath))
            {
                try
                {
                    Typeface t = Typeface.createFromAsset(c.getAssets(),
                            assetPath);
                    cache.put(assetPath, t);
                }
                catch (Exception e)
                {
                    Log.e(KhaasConstant.LOG_TAG, "Could not get typeface '" + assetPath
                            + "' because " + e.getMessage());
                    return null;
                }
            }
            return cache.get(assetPath);
        }
    }


}
