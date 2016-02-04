package com.omidbiz.khaas;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.ContactsContract;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.RequestParams;
import com.omidbiz.khaas.fragments.SmsActivity;
import com.omidbiz.khaas.handlers.CommandResponseHandler;
import com.omidbiz.khaas.handlers.JokeResponseHandler;
import com.omidbiz.khaas.handlers.KhaasUtil;
import com.omidbiz.khaas.handlers.PlaceResponseHandler;
import com.omidbiz.khaas.listeners.AlarmReceiver;
import com.omidbiz.khaas.manager.HandlerManager;
import com.omidbiz.khaas.manager.StringEntity;
import com.omidbiz.khaas.music.MusicAdapter;
import com.omidbiz.khaas.music.MusicJam;
import com.omidbiz.khaas.speech.SpeechRecognizerActivity;
import com.omidbiz.khaas.ui.GooglePlace;
import com.omidbiz.khaas.ui.LayoutListView;
import com.omidbiz.khaas.ui.PlaceArrayAdapter;

import org.apache.http.HttpEntity;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author : Omid Pourhadi omidpourhadi [AT] gmail [dot] com
 */
public class KhaasActivity extends SpeechRecognizerActivity
{


    static Map<String, String> googlePlacesType;

    static
    {
        googlePlacesType = Collections.synchronizedMap(new HashMap<String, String>());
        googlePlacesType.put("رستوران", "food");
        googlePlacesType.put("بانک", "bank");
        googlePlacesType.put("فرودگاه", "airport");
        googlePlacesType.put("کافه", "cafe");
        googlePlacesType.put("سفارت", "embassy");
        googlePlacesType.put("بیمارستان", "hospital");
        googlePlacesType.put("مسجد", "mosque");
//        googlePlacesType.put("", "park");
        googlePlacesType.put("داروخانه", "pharmacy");
        googlePlacesType.put("دانشگاه", "university");
    }

    private String android_id;

    private ImageButton sendButton;
    private ImageButton callImageBtn;
    private ImageButton refreshImageBtn;
    private ImageButton smsImageBtn;
    private ImageButton jokeImageBtn;
    private ImageButton navigateImageBtn;
    private ImageButton alarmImageBtn;
    private LayoutListView layoutListView;
    private EditText commandText;

    private ContactsAdapter mAdapter;
    private HandlerManager handlerManager;

    private AlarmManager alarmManager;

    PendingIntent pendingIntent;


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState, R.layout.activity_khaas);
        //contentView has been set in super class
        //

        android_id = KhaasUtil.getAndroidId(getApplicationContext());

        //

        sendButton = (ImageButton) findViewById(R.id.sendButton);
        callImageBtn = (ImageButton) findViewById(R.id.callImageBtn);
        smsImageBtn = (ImageButton) findViewById(R.id.smsImageBtn);
        refreshImageBtn = (ImageButton) findViewById(R.id.refreshImageBtn);
        jokeImageBtn = (ImageButton) findViewById(R.id.jokeImageBtn);
        navigateImageBtn = (ImageButton) findViewById(R.id.navigateBtn);
        alarmImageBtn = (ImageButton) findViewById(R.id.alarmBtn);

        commandText = (EditText) findViewById(R.id.command);
        commandText.setTypeface(KhaasUtil.getTypeFace(getApplicationContext(), KhaasConstant.FONT_NAME), Typeface.NORMAL);

        commandText.addTextChangedListener(new CommandTextWatcher());

        layoutListView = (LayoutListView) findViewById(R.id.chat_list_view);


        callCheckUserService();
        mAdapter = new ContactsAdapter(getApplicationContext());

        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        callImageBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                cancelMediaPlayerIfPlaying();
                String jsonCommand = KhaasUtil.buildJsonCommand(getString(R.string.tamas));
                ExecuteHandler executeHandler = new ExecuteHandler();
                executeHandler.execute(jsonCommand);

            }
        });
        smsImageBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                cancelMediaPlayerIfPlaying();
                String jsonCommand = KhaasUtil.buildJsonCommand(getString(R.string.payam));
                ExecuteHandler executeHandler = new ExecuteHandler();
                executeHandler.execute(jsonCommand);

            }
        });
        jokeImageBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                cancelMediaPlayerIfPlaying();
                String jsonCommand = KhaasUtil.buildJsonCommand(getString(R.string.joke));
                ExecuteHandler executeHandler = new ExecuteHandler();
                executeHandler.execute(jsonCommand);

            }
        });
        refreshImageBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                layoutListView.setAdapter(null);
            }
        });
        sendButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                cancelMediaPlayerIfPlaying();
                String cmd = commandText.getText().toString();
                if (KhaasUtil.isNotEmpty(cmd))
                {
                    executerHelper(cmd);
                    //
                    commandText.setText("");
                }
            }
        });

        navigateImageBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                cancelMediaPlayerIfPlaying();
                String jsonCommand = KhaasUtil.buildJsonCommand(getString(R.string.navigate));
                ExecuteHandler executeHandler = new ExecuteHandler();
                executeHandler.execute(jsonCommand);

            }
        });

        alarmImageBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                cancelMediaPlayerIfPlaying();
                String jsonCommand = KhaasUtil.buildJsonCommand(getString(R.string.alarm));
                ExecuteHandler executeHandler = new ExecuteHandler();
                executeHandler.execute(jsonCommand);

            }
        });

    }


    private void executerHelper(String command)
    {

        String jsonCommand = KhaasUtil.buildJsonCommand(command);
        ExecuteHandler executeHandler = new ExecuteHandler();
        executeHandler.execute(jsonCommand);
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
        String cmd = heard.get(0);
        commandText.setText(cmd);
        executerHelper(cmd);
    }


    public class ContactsAdapter extends CursorAdapter
    {
        private LayoutInflater mInflater; // Stores the layout inflater


        public ContactsAdapter(Context context)
        {
            super(context, null, 0);
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            View view = super.getView(position, convertView, parent);
            if (view != null)
            {
                if (position % 2 == 0)
                {
                    view.setBackgroundColor(KhaasUtil.getRowColor(0));
                }
                else
                {
                    view.setBackgroundColor(KhaasUtil.getRowColor(1));
                }
            }
            return view;
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup viewGroup)
        {
            final View itemLayout =
                    mInflater.inflate(R.layout.contact_list_item, viewGroup, false);
            //
            final ViewHolder holder = new ViewHolder();
            holder.text1 = (TextView) itemLayout.findViewById(android.R.id.text1);
            holder.text2 = (TextView) itemLayout.findViewById(android.R.id.text2);

            itemLayout.setTag(holder);

            // Returns the item layout view
            return itemLayout;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor)
        {

            final ViewHolder holder = (ViewHolder) view.getTag();
            final String displayName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            final String phone = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            holder.text2.setText(displayName);
            holder.text1.setText(phone);
            holder.text1.setVisibility(View.VISIBLE);
            holder.text2.setVisibility(View.VISIBLE);

        }

        private class ViewHolder
        {
            TextView text1;
            TextView text2;
        }

    }

    private class ContactsLoader implements LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemClickListener
    {
        private String mSearchTerm;
        int mode;

        private ContactsLoader(String mSearchTerm, int mode)
        {
            this.mSearchTerm = mSearchTerm;
            this.mode = mode;
        }

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args)
        {
            return new CursorLoader(getApplicationContext(),
                    PhoneQuery.URI,
                    PhoneQuery.PROJECTION,
                    PhoneQuery.SELECTION,
                    new String[]{mSearchTerm + "%"},
                    PhoneQuery.SORT_ORDER);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data)
        {
            if (data.getCount() > 0)
            {
                mAdapter.swapCursor(data);
            }
            else
            {
                mAdapter.swapCursor(null);
                KhaasUtil.createToast(getApplicationContext(), getString(R.string.noResult), Toast.LENGTH_SHORT);
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader)
        {
            mAdapter.swapCursor(null);
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {
            switch (mode)
            {
                case 1:
                    call(position);
                    break;
                case 2:
                    sendSms(position);
                    break;
                default:
                    Log.i(KhaasConstant.LOG_TAG, "No Action");
            }
        }
    }


    private void sendSms(int pos)
    {
        String number = getNumber(pos);
        if (KhaasUtil.isNotEmpty(number))
        {
            Intent intent = new Intent(getApplicationContext(), SmsActivity.class);
            intent.putExtra(KhaasConstant.SMS_SEND_TO, number);
            startActivityForResult(intent, KhaasConstant.SMS_REQUEST_CODE);
        }
    }

    private void call(int position)
    {
        String number = getNumber(position);
        if (KhaasUtil.isNotEmpty(number))
        {
            Runnable callRunnable = new CallRunnable(number);
            handlerManager = new HandlerManager(callRunnable, getApplicationContext());
            handlerManager.run();

        }
    }


    private class CallRunnable implements Runnable
    {

        private String number;

        public CallRunnable(String number)
        {
            this.number = number;
        }

        @Override
        public void run()
        {
            String dialNumber = String.format(KhaasConstant.TEL, number);
            startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dialNumber)));
        }
    }

    public interface PhoneQuery
    {
        final static int QUERY_ID = 1;

        final Uri FILTER_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_FILTER_URI;

        final Uri URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;

        final static String[] PROJECTION = {ContactsContract.CommonDataKinds.Phone._ID, ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID, ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME};

        @SuppressLint("InlinedApi")
        final static String SORT_ORDER =
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME;

        @SuppressLint("InlinedApi")
        final static String SELECTION =
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME +
                        " like ? " + " AND " + ContactsContract.CommonDataKinds.Phone.IN_VISIBLE_GROUP + "=1";

    }


    private String getNumber(int pos)
    {
        final Cursor cursor = mAdapter.getCursor();
        if (cursor != null)
        {
            cursor.moveToPosition(pos);
            String number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            return number;
        }
        return null;
    }


    private class ExecuteHandler extends AsyncTask<String, Void, BotResponse>
    {
        @Override
        protected BotResponse doInBackground(String... params)
        {
            HttpEntity httpEntity;
            try
            {
                httpEntity = new com.omidbiz.khaas.manager.StringEntity(params[0], "UTF-8");
                CommandResponseHandler commandResponseHandler = new CommandResponseHandler(getApplicationContext());
                Map<String, Object> rp = new HashMap<String, Object>();
                rp.put("userId", KhaasUtil.getAndroidId(getApplicationContext()));
                restUtil.post("ai/response", commandResponseHandler, getApplicationContext(), httpEntity, rp);
                return commandResponseHandler.getResult();
            }
            catch (UnsupportedEncodingException e)
            {
                Log.d(KhaasConstant.LOG_TAG, e.getMessage());
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values)
        {

        }

        @Override
        protected void onPostExecute(BotResponse result)
        {
            speechToggleButton.setVisibility(View.VISIBLE);
            sendButton.setVisibility(View.GONE);
            commandText.setText("");
            //
            if (result != null)
            {
                BotResponse.ACTION resultAction = result.getAction();
                String msg = result.getMsg();
                processResult(result);
                if (resultAction.equals(BotResponse.ACTION.CALL))
                {
                    ContactsLoader cl = new ContactsLoader(msg, KhaasConstant.CALL);
                    getSupportLoaderManager().restartLoader(PhoneQuery.QUERY_ID, null, cl);
                    layoutListView.setAdapter(mAdapter);
                    layoutListView.setOnItemClickListener(cl);
                    KhaasUtil.createToast(getApplicationContext(), getString(R.string.wait), Toast.LENGTH_SHORT);
                }
                if (resultAction.equals(BotResponse.ACTION.IDENTIFY))
                {
                    KhaasUtil.createToast(getApplicationContext(), KhaasUtil.getAndroidId(getApplicationContext()), Toast.LENGTH_LONG);
                }
                if (resultAction.equals(BotResponse.ACTION.SMS))
                {
                    ContactsLoader cl = new ContactsLoader(msg, KhaasConstant.SEND_SMS);
                    getSupportLoaderManager().restartLoader(PhoneQuery.QUERY_ID, null, cl);
                    layoutListView.setAdapter(mAdapter);
                    layoutListView.setOnItemClickListener(cl);
                    KhaasUtil.createToast(getApplicationContext(), getString(R.string.wait), Toast.LENGTH_SHORT);
                }
                if (resultAction.equals(BotResponse.ACTION.TELL_JOKE))
                {
                    JokeAsyncLoader aj = new JokeAsyncLoader();
                    aj.execute();
                }
                if (resultAction.equals(BotResponse.ACTION.UPDATE_NAME))
                {
                    android_id = KhaasUtil.getAndroidId(getApplicationContext());
                    UpdateNameAsyncLoader updateName = new UpdateNameAsyncLoader(android_id, msg);
                    updateName.execute();
                }
                if (resultAction.equals(BotResponse.ACTION.ALARM))
                {
                    long interval = KhaasUtil.parseTime(result.getMsg());
                    if (interval > 0)
                    {
                        Intent alarmIntent = new Intent(KhaasActivity.this, AlarmReceiver.class);
                        pendingIntent = PendingIntent.getBroadcast(KhaasActivity.this, 0, alarmIntent, 0);
                        alarmManager.set(AlarmManager.RTC_WAKEUP, interval, pendingIntent);
                        KhaasUtil.createToast(getApplicationContext(), getString(R.string.alarm_set), Toast.LENGTH_SHORT);
                    }
                    else
                    {
                        KhaasUtil.createToast(getApplicationContext(), getString(R.string.invalid_time_format), Toast.LENGTH_SHORT);
                    }
                }
                if (resultAction.equals(BotResponse.ACTION.LOCATE))
                {
                }
                if (resultAction.equals(BotResponse.ACTION.SEARCH))
                {
                    typeForLocationUpdate = googlePlacesType.get(result.getMsg());
                    if (KhaasUtil.isNotEmpty(typeForLocationUpdate) && KhaasUtil.isNotEmpty(locationProvider))
                    {
                        Location location = locationManager.getLastKnownLocation(locationProvider);
                        LocationFinderTask lft = new LocationFinderTask(location.getLatitude(), location.getLongitude());
                        lft.execute();
                        KhaasUtil.createToast(getApplicationContext(), getString(R.string.wait), Toast.LENGTH_LONG);
                    }
                    else
                    {
                        KhaasUtil.createToast(getApplicationContext(), getString(R.string.unkonwn_provider), Toast.LENGTH_SHORT);
                    }
                }
                if (resultAction.equals(BotResponse.ACTION.PLAY_MUSIC))
                {
                    MusicRequest mr = new MusicRequest();
                    mr.execute();
                    KhaasUtil.createToast(getApplicationContext(), getString(R.string.wait), Toast.LENGTH_LONG);
                }
                if (resultAction.equals(BotResponse.ACTION.CLEAR))
                {
                    layoutListView.setAdapter(null);
                }
                if (resultAction.equals(BotResponse.ACTION.CANCEL))
                {
                    if (handlerManager != null)
                    {
                        handlerManager.cancel();
                    }
                }
            }
        }
    }


    private class MusicRequest extends AsyncTask<String, Void, List<MusicJam>>
    {

        @Override
        protected List<MusicJam> doInBackground(String... params)
        {

            MusicResponseHandler responseHandler = new MusicResponseHandler();
            String jamUrl = String.format("https://api.jamendo.com/v3.0/tracks/?client_id=99877d66&format=json&limit=15&fuzzytags=%s&include=musicinfo&groupby=artist_id", pickFuzzyTag());
            restUtil.getExternalService(jamUrl, responseHandler);
            return responseHandler.getMusics();
        }

        @Override
        protected void onPostExecute(List<MusicJam> musicJams)
        {
            if (musicJams != null && musicJams.size() > 0)
            {
                MusicAdapter musicAdapter = new MusicAdapter(musicJams, getApplicationContext());
                layoutListView.setAdapter(musicAdapter);
                layoutListView.setOnItemClickListener(new MusicClickListener(musicJams));
            }

        }

    }

    protected class LocationFinderTask extends AsyncTask<String, Void, List<GooglePlace>>
    {

        double lat;
        double lag;

        public LocationFinderTask(double lat, double lag)
        {
            this.lat = lat;
            this.lag = lag;
        }

        @Override
        protected List<GooglePlace> doInBackground(String... params)
        {
            RequestParams rp = new RequestParams();
            rp.put("lat", lat);
            rp.put("lng", lag);
            rp.put("type", typeForLocationUpdate);
            PlaceResponseHandler responseHandler = new PlaceResponseHandler();
            restUtil.get("ai/findPlaces", rp, responseHandler);
            return responseHandler.getResultList();
        }


        @Override
        protected void onPostExecute(List<GooglePlace> response)
        {
            if (response != null && response.size() > 0)
            {
                PlaceArrayAdapter pa = new PlaceArrayAdapter(response, getApplicationContext());
                layoutListView.setAdapter(pa);
                layoutListView.setOnItemClickListener(new PlaceItemClickListener(response));

            }
        }
    }


    private class PlaceItemClickListener implements AdapterView.OnItemClickListener
    {


        private List<GooglePlace> googlePlaces;

        private PlaceItemClickListener(List<GooglePlace> googlePlaces)
        {
            this.googlePlaces = googlePlaces;
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {
            if (googlePlaces != null && googlePlaces.size() > 0)
            {
                GooglePlace place = googlePlaces.get(position);
                //TODO: add google map activity
            }

        }
    }

    @Override
    protected void processResult(BotResponse result)
    {
        super.processResult(result);
        KhaasUtil.processResponse(getApplicationContext(), result, layoutListView);
    }

    private class UpdateNameAsyncLoader extends AsyncTask<Void, Void, BotResponse>
    {

        private String userId;
        private String userName;

        private UpdateNameAsyncLoader(String userId, String userName)
        {
            this.userId = userId;
            this.userName = userName;
        }

        @Override
        protected BotResponse doInBackground(Void... params)
        {
            HttpEntity httpEntity;
            try
            {
                JSONObject json = new JSONObject();
                json.put("userId", this.userId);
                json.put("userName", this.userName);
                httpEntity = new StringEntity(json.toString(), "UTF-8");
                CommandResponseHandler commandResponseHandler = new CommandResponseHandler(getApplicationContext());
                restUtil.put("ai/updateName", getApplicationContext(), httpEntity, commandResponseHandler);
                return commandResponseHandler.getResult();
            }
            catch (UnsupportedEncodingException e)
            {
                Log.d(KhaasConstant.LOG_TAG, e.getMessage());
            }
            catch (JSONException e)
            {
                Log.d(KhaasConstant.LOG_TAG, e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(BotResponse botResponse)
        {
            if (botResponse != null)
                processResult(botResponse);
        }
    }

    private class JokeAsyncLoader extends AsyncTask<Void, Void, BotResponse>
    {

        @Override
        protected BotResponse doInBackground(Void... params)
        {
            JokeResponseHandler handler = new JokeResponseHandler();
            restUtil.get("ai/pickJoke", new RequestParams(), handler);
            return handler.getResult();
        }

        @Override
        protected void onPostExecute(BotResponse botResponse)
        {
            processResult(botResponse);
        }
    }

    private void callCheckUserService()
    {
        UserAsyncHandler ua = new UserAsyncHandler();
        ua.execute(android_id);
    }

    private class UserAsyncHandler extends AsyncTask<String, Void, BotResponse>
    {
        @Override
        protected BotResponse doInBackground(String... params)
        {

            CommandResponseHandler h = new CommandResponseHandler(getApplicationContext());
            RequestParams rp = new RequestParams();
            rp.put("androidId", KhaasUtil.getAndroidId(getApplicationContext()));
            restUtil.get("ai/checkUser", rp, h);
            return h.getResult();
        }


        @Override
        protected void onPostExecute(BotResponse br)
        {
            if (br != null)
            {
                BotResponse.ACTION resultAction = br.getAction();
                String msg = br.getMsg();

                if (resultAction.equals(BotResponse.ACTION.NOTHING))
                {
                    Log.i(KhaasConstant.LOG_TAG, msg);
                    msg = getString(R.string.welcome) + " " + msg;
                    TextView textView = KhaasUtil.createTextView(getApplicationContext(), msg);
                    KhaasUtil.updateAdapter(getApplicationContext(), layoutListView, textView);
                }
            }
            else
            {
                TextView textView = KhaasUtil.createTextView(getApplicationContext(), getString(R.string.hello));
                KhaasUtil.updateAdapter(getApplicationContext(), layoutListView, textView);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_khaas, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
            startActivityForResult(intent, KhaasConstant.Setting_REQUEST_CODE);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == KhaasConstant.Setting_REQUEST_CODE)
        {
            Intent intent = getIntent();
            finish();
            startActivity(intent);
        }
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        if (mAdapter != null)
            mAdapter.swapCursor(null);

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState)
    {
        super.onPostCreate(savedInstanceState);
    }

    private class CommandTextWatcher implements TextWatcher
    {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after)
        {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count)
        {
            speechToggleButton.setVisibility(View.GONE);
            sendButton.setVisibility(View.VISIBLE);
        }

        @Override
        public void afterTextChanged(Editable s)
        {
            String textValue = s.toString();
            if (KhaasUtil.isEmpty(textValue))
            {
                speechToggleButton.setVisibility(View.VISIBLE);
                sendButton.setVisibility(View.GONE);
            }
        }
    }


}
