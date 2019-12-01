package com.example.killerapp;

import android.content.Context;
import android.location.Location;

import com.eis.smslibrary.SMSHandler;

/***
 * @author Turcato
 * Action to execute when receiving a Location request
 * Sends back current position
 */
public class SMSSendResponse implements Command<Location> {
    private String receivingAddress;
    private Constants constants;
    private SMSHandler handler;
    private Context applicationContext;
    private LocationManager locationManager;

    /**
     * @author Turcato
     * Sends an sms message to the defined sms number with a text specifically formatted to contain
     * the position in foundlocation
     * @param foundLocation location to forward to given phone number
     */
    public void execute(Location foundLocation) {
        String responseMessage = locationManager.getResponseStringMessage(foundLocation);
        handler.sendMessage(new Message(receivingAddress, responseMessage));
    }

    /***
     * @author Turcato
     * @param receiverAddress receiver's phone number
     * @param handler SMSHandler's class object to use to send sms
     * @param context android application Context
     */
    public SMSSendResponse(String receiverAddress, SMSHandler handler, Context context)
    {
        receivingAddress = receiverAddress;
        constants = new Constants();
        this.handler = handler;
        applicationContext = context;
        locationManager = new LocationManager(SMSHandler.WAKE_KEY);
    }
}
