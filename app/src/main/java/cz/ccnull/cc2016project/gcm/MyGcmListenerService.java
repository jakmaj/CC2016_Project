package cz.ccnull.cc2016project.gcm;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;

import cz.ccnull.cc2016project.App;
import cz.ccnull.cc2016project.Config;
import cz.ccnull.cc2016project.model.Receiver;
import cz.ccnull.cc2016project.provider.DataProvider;

public class MyGcmListenerService extends GcmListenerService {
    private static final String TAG = MyGcmListenerService.class.getName();

    private static final String NOTIFICATION_TYPE = "notification_type";
    private static final String NOTIFICATION_HEARD = "heard";
    private static final String NOTIFICATION_CONFIRM = "confirm";

    private static final String HEARD_PAYMENT_CODE = "payment_code";
    private static final String HEARD_USER_ID = "user_id";
    private static final String HEARD_NAME = "user_name";

    private static final String CONFIRM_PAYMENT_CODE = "payment_code";

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
        String code = data.getString(CONFIRM_PAYMENT_CODE);
        App.getInstance().getPreferences().edit().putString(Config.KEY_CONFIRM, code).commit();

        Intent intent = new Intent(Config.BROADCAST_CONFIRM);
        intent.putExtra(Config.KEY_PAYMENT_CODE, code);
        sendBroadcast(intent);
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
