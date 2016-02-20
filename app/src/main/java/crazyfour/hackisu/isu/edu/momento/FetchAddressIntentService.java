package crazyfour.hackisu.isu.edu.momento;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.text.TextUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import crazyfour.hackisu.isu.edu.momento.constants.LocationConstants;

/**
 * Created by Sriram on 20-02-2016.
 */
public class FetchAddressIntentService extends IntentService {

    protected ResultReceiver mReceiver;

    public FetchAddressIntentService() {
        super("AddressService");
    }

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public FetchAddressIntentService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        Location location = intent.getParcelableExtra(
                LocationConstants.LOCATION_DATA_EXTRA);
        mReceiver = intent.getParcelableExtra(LocationConstants.RECEIVER);
        String errorMessage = null;
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(
                    location.getLatitude(),
                    location.getLongitude(),
                    // In this sample, get just a single address.
                    1);
        } catch (IOException ioException) {
            // Catch network or other I/O problems.
            System.out.println("Service Not available");
        } catch (IllegalArgumentException illegalArgumentException) {
            // Catch invalid latitude or longitude values.
            System.out.println("Invalid lat/long");
        }

        // Handle case where no address was found.
        if (addresses == null || addresses.size()  == 0) {
            if (errorMessage == null) {
                System.out.println("No address found");
            }
            deliverResultToReceiver(LocationConstants.FAILURE_RESULT, errorMessage);
        } else {
            Address address = addresses.get(0);
            deliverResultToReceiver(LocationConstants.SUCCESS_RESULT,
                    address.getFeatureName());
        }
    }

    private void deliverResultToReceiver(int resultCode, String message) {
        Bundle bundle = new Bundle();
        bundle.putString(LocationConstants.RESULT_DATA_KEY, message);
        mReceiver.send(resultCode, bundle);
    }
}
