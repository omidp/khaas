package com.omidbiz.khaas;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.RemoteViews;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.omidbiz.khaas.music.MusicJam;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author : Omid Pourhadi omidpourhadi [AT] gmail [dot] com
 */
public class MusicPlayerActivity extends MediaActivity implements MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnBufferingUpdateListener
{

    private static final String NOTIFICATION_DELETED_ACTION = "KHAAS_MUSIC_NOTIFICATION_DELETED";

    private MediaPlayer musicPlayer;

    public static final int MUSIC_NOTIFY_CODE = 5000;

    private String[] fuzzyTags = {"pop", "rock", "rap", "metal", "groove+rock", "rock+pop"};


    private final BroadcastReceiver receiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            cancelMusicNotificationAndStopPlayer();
            unregisterReceiver(this);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState, int layoutResId)
    {
        super.onCreate(savedInstanceState, layoutResId);
        musicPlayer = new MediaPlayer();
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent)
    {

    }

    @Override
    public void onCompletion(MediaPlayer mp)
    {

    }

    @Override
    public void onPrepared(MediaPlayer mp)
    {
        if (musicPlayer != null && musicPlayer.isPlaying() == false)
            musicPlayer.start();
    }

    protected void cancelMusicNotificationAndStopPlayer()
    {
        if (musicPlayer != null)
            musicPlayer.reset();
        if (notificationManager != null)
            notificationManager.cancel(MUSIC_NOTIFY_CODE);
    }

    private void startMusicNotification(MusicJam mj)
    {
        PendingIntent activity = PendingIntent.getActivity(getApplicationContext(), 0, getIntent(), 0);
        //
        Notification notification = new Notification(R.drawable.ic_launcher, null, System.currentTimeMillis());
        RemoteViews notificationView = new RemoteViews(getPackageName(), R.layout.music_notification);
        notification.contentView = notificationView;
        notification.contentIntent = activity;
        notification.deleteIntent = activity;
        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        //
        final Intent cancelIntent = new Intent(NOTIFICATION_DELETED_ACTION);
        final PendingIntent pendingSwitchIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, cancelIntent, 0);
        registerReceiver(receiver, new IntentFilter(NOTIFICATION_DELETED_ACTION));
        notificationView.setOnClickPendingIntent(R.id.music_cancel, pendingSwitchIntent);
        //
        notificationView.setTextViewText(R.id.music_albumName, mj.getAlbumName());
        notificationView.setTextViewText(R.id.music_artistName, mj.getArtistName());
        //
        notificationManager.notify(MUSIC_NOTIFY_CODE, notification);
    }


    protected String pickFuzzyTag()
    {
        int cnt = fuzzyTags.length;
        Random r = new Random();
        int index = 0;
        int randomIndex = r.nextInt(cnt - index) + index;
        return fuzzyTags[randomIndex];
    }

    private void playMusic(MusicJam mj)
    {
        musicPlayer.reset();
        musicPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        try
        {
            musicPlayer.setDataSource(mj.getStreamUrl());
            musicPlayer.setOnBufferingUpdateListener(this);
            musicPlayer.setOnPreparedListener(this);
            musicPlayer.prepareAsync();
        }
        catch (IllegalArgumentException e)
        {
            Log.i(KhaasConstant.LOG_TAG, e.getMessage());
        }
        catch (SecurityException e)
        {
            Log.i(KhaasConstant.LOG_TAG, e.getMessage());
        }
        catch (IllegalStateException e)
        {
            Log.i(KhaasConstant.LOG_TAG, e.getMessage());
        }
        catch (IOException e)
        {
            Log.i(KhaasConstant.LOG_TAG, e.getMessage());
        }
    }


    protected class MusicResponseHandler extends AsyncHttpResponseHandler
    {

        List<MusicJam> musics;

        @Override
        public void onSuccess(int i, Header[] headers, byte[] bytes)
        {
            try
            {
                String resp = new String(bytes, "UTF-8");
                JSONObject json = new JSONObject(resp);
                if (json.has("results"))
                {
                    musics = new ArrayList<MusicJam>();
                    String str = json.getString("results");
                    JSONArray results = new JSONArray(str);
                    for (int j = 0; j < results.length(); j++)
                    {
                        JSONObject jsonObject = results.getJSONObject(j);
                        MusicJam mj = new MusicJam();
                        if (jsonObject.has("audio"))
                        {
                            String audioStream = jsonObject.getString("audio");
                            mj.setStreamUrl(audioStream);
                        }
                        if (jsonObject.has("album_name"))
                        {
                            mj.setAlbumName(jsonObject.getString("album_name"));
                        }
                        if (jsonObject.has("artist_name"))
                        {
                            mj.setArtistName(jsonObject.getString("artist_name"));
                        }
                        if (jsonObject.has("id"))
                        {
                            mj.setId(jsonObject.getString("id"));
                        }
                        musics.add(mj);
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

        @Override
        public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable)
        {
            Log.i(KhaasConstant.LOG_TAG, "Unable to play Music");
        }

        public List<MusicJam> getMusics()
        {
            return musics;
        }
    }

    protected class MusicClickListener implements AdapterView.OnItemClickListener
    {

        List<MusicJam> musicList;

        public MusicClickListener(List<MusicJam> musicList)
        {
            this.musicList = musicList;
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {
            MusicJam mj = musicList.get(position);
            cancelMusicNotificationAndStopPlayer();
            playMusic(mj);
            startMusicNotification(mj);
        }
    }

}
