package com.omidbiz.khaas.speech;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.omidbiz.khaas.KhaasConstant;
import com.omidbiz.khaas.LocationActivity;
import com.omidbiz.khaas.MediaActivity;
import com.omidbiz.khaas.R;
import com.omidbiz.khaas.handlers.KhaasUtil;

import java.util.List;

/**
 * @author : Omid Pourhadi omidpourhadi [AT] gmail [dot] com
 */
public abstract class SpeechRecognizerActivity extends LocationActivity implements RecognitionListener, CompoundButton.OnCheckedChangeListener
{

    private SpeechRecognizer speechRecognizer;
    private ProgressBar speechProgressBar;
    private Intent recognizerIntent;
    protected ToggleButton speechToggleButton;


    public void onCreate(Bundle savedInstanceState, int layoutResId)
    {
        super.onCreate(savedInstanceState, layoutResId);
        //content view set in super class
        boolean hasInternet = KhaasUtil.hasInternet(getApplicationContext());
        if (hasInternet == false)
            KhaasUtil.createToast(getApplicationContext(), getString(R.string.no_connection), Toast.LENGTH_LONG);
        boolean speechAvailable = KhaasUtil.isSpeechAvailable(this);
        if (speechAvailable == false)
        {
            speechNotAvailable();
        }
        boolean direct = SpeechRecognizer.isRecognitionAvailable(this);
        if (direct == false)
        {
            directSpeechNotAvailable();
        }
        boolean lang = pref.getBoolean("lang_checkbox", true);
        recognizerIntent = KhaasUtil.getRecognizerIntent(lang);
        //
        initSpeech();
        //
        speechProgressBar = (ProgressBar) findViewById(R.id.speechProgressBar);
        speechProgressBar.setVisibility(View.INVISIBLE);
        speechToggleButton = (ToggleButton) findViewById(R.id.speechToggleButton);
        speechToggleButton.setOnCheckedChangeListener(this);
        //

        //
    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
    {
        if (isChecked)
        {
            speechProgressBar.setVisibility(View.VISIBLE);
            speechProgressBar.setIndeterminate(true);
            speechRecognizer.startListening(recognizerIntent);
        }
        else
        {
            speechProgressBar.setIndeterminate(false);
            speechProgressBar.setVisibility(View.INVISIBLE);
            speechRecognizer.stopListening();
        }
    }

    private void initSpeech()
    {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        speechRecognizer.setRecognitionListener(this);


    }

    protected abstract void directSpeechNotAvailable();

    protected abstract void speechNotAvailable();

    @Override
    public void onReadyForSpeech(Bundle params)
    {

    }

    @Override
    public void onBeginningOfSpeech()
    {
        Log.i(KhaasConstant.LOG_TAG, "onBeginningOfSpeech");
        speechProgressBar.setIndeterminate(false);
        speechProgressBar.setMax(10);
    }

    @Override
    public void onRmsChanged(float rmsdB)
    {
        speechProgressBar.setProgress((int) rmsdB);
    }

    @Override
    public void onBufferReceived(byte[] buffer)
    {

    }

    @Override
    public void onEndOfSpeech()
    {
        Log.i(KhaasConstant.LOG_TAG, "onEndOfSpeech");
        speechProgressBar.setIndeterminate(true);
        speechToggleButton.setChecked(false);
    }

    @Override
    public void onError(int error)
    {
        Log.i(KhaasConstant.LOG_TAG, "onError : " + error);
        speechError(error);
        resetSpeech();
        initSpeech();
    }

    protected abstract void speechError(int error);

    @Override
    public void onResults(Bundle results)
    {
        receiveResults(results);
    }

    @Override
    public void onPartialResults(Bundle partialResults)
    {
        receiveResults(partialResults);
    }

    @Override
    public void onEvent(int eventType, Bundle params)
    {

    }

    private void receiveResults(Bundle results)
    {
        if ((results != null) && results.containsKey(SpeechRecognizer.RESULTS_RECOGNITION))
        {
            List<String> heard = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            float[] scores = results.getFloatArray(SpeechRecognizer.CONFIDENCE_SCORES);
            receiveWhatWasHeard(heard, scores);
        }
    }

    abstract protected void receiveWhatWasHeard(List<String> heard, float[] confidenceScores);

    @Override
    protected void onPause()
    {
        resetSpeech();
        super.onPause();
    }

    private void resetSpeech()
    {
        if (speechProgressBar != null)
        {
            speechProgressBar.setIndeterminate(true);
            speechProgressBar.invalidate();
        }
        if (speechToggleButton != null)
        {
            speechToggleButton.setChecked(false);
        }
        if (speechRecognizer != null)
        {
            speechRecognizer.stopListening();
            speechRecognizer.cancel();
            speechRecognizer.destroy();
            speechRecognizer = null;
            Log.i(KhaasConstant.LOG_TAG, "destroy");
        }
    }

    @Override
    protected void onResume()
    {
        initSpeech();
        super.onResume();
    }
}
