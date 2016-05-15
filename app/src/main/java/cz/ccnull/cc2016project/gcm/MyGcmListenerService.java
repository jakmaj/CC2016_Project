package cz.ccnull.cc2016project.gcm;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;

import cz.ccnull.cc2016project.model.Receiver;
import cz.ccnull.cc2016project.provider.DataProvider;

public class MyGcmListenerService extends GcmListenerService {
    public static final String TAG = MyGcmListenerService.class.getName();

    public static final String NOTIFICATION_TYPE = "notification_type";
    public static final String NOTIFICATION_HEARD = "heard";
    public static final String NOTIFICATION_CONFIRM = "confirm";

    public static final String HEARD_PAYMENT_CODE = "paymentCode";
    public static final String HEARD_USER_ID = "_idReceiver";
    public static final String HEARD_NAME = "nameReceiver";

    @Override
    public void onMessageReceived(String from, Bundle data) {
        String type = data.getString(NOTIFICATION_TYPE, "");

        Log.i(TAG, "Received push notification of type " + type);

        switch (type) {
            case NOTIFICATION_HEARD:
                createReceiver(data);
                break;
            case NOTIFICATION_CONFIRM:
                sendConfirmation(data);
                break;
        }
    }

    private void sendConfirmation(Bundle data) {

    }

    private void createReceiver(Bundle data) {
        String paymentCode = data.getString(HEARD_PAYMENT_CODE);
        String userId = data.getString(HEARD_USER_ID);
        String name = data.getString(HEARD_NAME);

        Receiver receiver = new Receiver(paymentCode, userId, name);

        getContentResolver().insert(
                DataProvider.RECEIVERS_URI,
                receiver.getContentValues());
    }
}
