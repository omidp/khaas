package com.omidbiz.khaas;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcel;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.omidbiz.khaas.handlers.KhaasUtil;
import com.omidbiz.khaas.speech.SpeechRecognizerActivity;
import com.omidbiz.khaas.util.RestUtil;

import org.apache.http.Header;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

/**
 * @author : Omid Pourhadi omidpourhadi [AT] gmail [dot] com
 */
public abstract class MediaActivity extends ActionBarActivity
{


    protected SharedPreferences pref;
    protected boolean voice;
    private MediaPlayer mediaPlayer;
    protected NotificationManager notificationManager;
    private AudioManager audioManager;
    private SoundPool soundPool;
    private int soundId;
    private float volume;
    protected RestUtil restUtil;

    public void onCreate(Bundle savedInstanceState, int layoutResId)
    {
        super.onCreate(savedInstanceState);
        setContentView(layoutResId);
        restUtil = new RestUtil();
        //
        pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        voice = pref.getBoolean("lang_voice", true);
        //
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        //
        // AudioManager audio settings for adjusting the volume
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        float actVolume = (float) audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        float maxVolume = (float) audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        volume = actVolume / maxVolume;
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 100);
        soundId = soundPool.load(this, R.raw.beep, 1);
    }

    protected void processResult(BotResponse result)
    {
        if (result.getAction().equals(BotResponse.ACTION.NOTHING))
        {
            if (voice)
            {
                PlayAudio pa = new PlayAudio();
                pa.execute(result.getMsg());
            }


        }
    }


    private class PlayAudio extends AsyncTask<String, Void, File> implements MediaPlayer.OnCompletionListener, MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener
    {

        @Override
        protected File doInBackground(String... params)
        {
            RequestParams rp = new RequestParams();
            rp.put("msg", params[0]);
            VoiceResponseHandler vrh = new VoiceResponseHandler();
            restUtil.get("ai/getVoice", rp, vrh);
            File audioFile = vrh.getAudiofile();

            return audioFile;
        }

        @Override
        protected void onPostExecute(File file)
        {
            if (file != null)
            {
                if (KhaasUtil.hasHoneycomb())
                {
                    mediaPlayer = MediaPlayer.create(getApplicationContext(), Uri.fromFile(file));
                    mediaPlayer.setOnBufferingUpdateListener(this);
                    mediaPlayer.setOnCompletionListener(this);
                    mediaPlayer.setOnErrorListener(this);
                    mediaPlayer.start();
                }
                else
                {
                    mediaPlayer = new MediaPlayer();
                    mediaPlayer.setOnBufferingUpdateListener(this);
                    mediaPlayer.setOnCompletionListener(this);
                    mediaPlayer.setOnErrorListener(this);
                    mediaPlayer.setOnPreparedListener(this);
                    FileInputStream fi = null;
                    try
                    {
                        fi = new FileInputStream(file);
                        mediaPlayer.setDataSource(fi.getFD());
                        mediaPlayer.prepareAsync();
                    }
                    catch (IOException e)
                    {
                        Log.i(KhaasConstant.LOG_TAG, e.getMessage());
                    }
                    finally
                    {
                        KhaasUtil.closeQuietly(fi);
                    }
                }
            }
            else
            {
                KhaasUtil.createToast(getApplicationContext(), getString(R.string.onlyPersian), Toast.LENGTH_SHORT);
            }
        }


        @Override
        public void onBufferingUpdate(MediaPlayer mp, int percent)
        {

        }

        @Override
        public void onCompletion(MediaPlayer mp)
        {
            notificationManager.cancel(Manager.NOTIFICATION_ID);
        }

        @Override
        public boolean onError(MediaPlayer mp, int what, int extra)
        {
            Log.i(KhaasConstant.LOG_TAG, "what : " + what + "extra : " + extra);
            return false;
        }

        @Override
        public void onPrepared(MediaPlayer mp)
        {
            mp.start();
        }

    }

    protected class VoiceResponseHandler extends FileAsyncHttpResponseHandler
    {

        File audiofile;
        Notification notification;
        PendingIntent pi;
//        Notification.Builder notyBuilder;

        public VoiceResponseHandler()
        {
            super(getApplicationContext());
            pi = PendingIntent.getActivity(getApplicationContext(), 1500, getIntent(), 0);
            // Uri soundUri = Uri.parse("android.resource://com.omidbiz.khaas/" + R.raw.beep);
            soundPool.play(soundId, volume, volume, 1, 0, 1f);
            notification = new Notification(android.R.drawable.stat_sys_download, getString(R.string.app_name), System.currentTimeMillis());
//            notyBuilder = new Notification.Builder(getApplicationContext())
//                    .setSmallIcon(android.R.drawable.stat_sys_download)
//                    .setAutoCancel(true)
//                    .setContentIntent(pi)
////                    .setSound(soundUri)
//                    .setContentTitle(getString(R.string.app_name));

        }

        @Override
        public void onFailure(int i, Header[] headers, Throwable throwable, File file)
        {
            Log.e(KhaasConstant.LOG_TAG, "unable to lad voice file");
        }

        @Override
        public void onProgress(int bytesWritten, int totalSize)
        {
            super.onProgress(bytesWritten, totalSize);
            float percent = (bytesWritten * 100) / totalSize;
            int progress = Math.round(percent);
            Log.i(KhaasConstant.LOG_TAG, String.valueOf(progress));
//            notyBuilder.setContentText(getString(R.string.audio_progress, progress) + " " + getString(R.string.percent));
//            notification = notyBuilder.getNotification();
            notification.setLatestEventInfo(getApplicationContext(), getString(R.string.app_name), getString(R.string.audio_progress, progress) + " " + getString(R.string.percent), pi);
            notificationManager.notify(Manager.NOTIFICATION_ID, notification);
        }

        @Override
        public void onSuccess(int i, Header[] headers, File file)
        {
            if (file != null)
            {
                this.audiofile = file;
            }
        }

        public File getAudiofile()
        {
            return audiofile;
        }
    }

    public interface Manager
    {
        final static int NOTIFICATION_ID = 1;
    }


    @Override
    protected void onPause()
    {
        super.onPause();
        if (mediaPlayer != null)
            mediaPlayer.release();
        if (notificationManager != null)
            notificationManager.cancelAll();
        if (soundPool != null)
            soundPool.release();
    }
}
