package com.example.killerapp;

import android.app.AlarmManager;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.eis.smslibrary.SMSHandler;

public class AlarmAndLocateResponseActivity extends AppCompatActivity {
    private final String AlarmAndLocateActivityTAG = "Alarm&LocateActivityTAG";
    private String receivedTextMessage;
    private String receivedMessageAddress;
    private Constants constants;
    private SMSHandler handler;
    private com.example.killerapp.SMSSendResponse SMSSendResponse;
    private MediaPlayer mediaPlayer;
    private LocationManager locationManager;
    private AlarmManager alarmManager;


    /**
     * This activity is created in all situations, for each request, so it needs to be executed also when screen is shut
     *
     * @param savedInstanceState system parameter
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Needed to open Activity if screen is shut
        final Window win= getWindow();
        win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        win.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        setContentView(R.layout.activity_alarm_and_locate);
        //
        constants = new Constants();
        locationManager = new LocationManager(SMSHandler.WAKE_KEY);
        alarmManager = new AlarmManager(SMSHandler.WAKE_KEY);

        //Params passed by methods tha called this activity
        receivedTextMessage = getIntent().getStringExtra(constants.receivedStringMessage);
        receivedMessageAddress = getIntent().getStringExtra(constants.receivedStringAddress);
        handler = new SMSHandler();


        if (locationManager.containsLocationRequest(receivedTextMessage)) {
            //Action to execute when device receives a Location request
            SMSSendResponse = new SMSSendResponse(receivedMessageAddress, handler, getApplicationContext());
            locationManager.getLastLocation(this, SMSSendResponse);
        }

        if (alarmManager.containsAlarmRequest(receivedTextMessage))
            startAlarm(); //User has to close app manually to stop

    }



    /**
     * Starts and alarm with the default ringtone of the device, stops when activity is closed by user
     */
    public void startAlarm()
    {
        mediaPlayer =MediaPlayer.create(this, Settings.System.DEFAULT_RINGTONE_URI);
        AudioManager audioManager= (AudioManager) getSystemService((Context.AUDIO_SERVICE));
        try{
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC),0);
        }
        catch (Exception e) {
            Log.e(AlarmAndLocateActivityTAG, "Error in setStreamVolume: " + e.getMessage());
        }
        mediaPlayer.start();
    }

    @Override
    protected void onDestroy() {

        handler.clearListener();
        handler.unregisterReceiver(getApplicationContext());
        if(mediaPlayer != null && mediaPlayer.isPlaying())
            mediaPlayer.stop();
        super.onDestroy();
    }

}
