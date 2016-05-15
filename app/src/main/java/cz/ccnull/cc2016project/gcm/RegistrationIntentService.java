package cz.ccnull.cc2016project.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;

import cz.ccnull.cc2016project.App;
import cz.ccnull.cc2016project.R;

public class RegistrationIntentService extends IntentService {
    public static final String TAG = RegistrationIntentService.class.getName();

    public RegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "onHandleIntent() called with: " + "intent = [" + intent + "]");
        InstanceID instanceID = InstanceID.getInstance(this);
        try {
            String token = instanceID.getToken(getString(R.string.google_app_id),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);

            Log.i(TAG, "GCM Registration Token: " + token);

            App.getInstance().getPreferences().edit().putString(App.SP_GCM_TOKEN_KEY, token).commit();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
