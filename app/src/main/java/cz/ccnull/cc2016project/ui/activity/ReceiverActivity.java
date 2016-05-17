package cz.ccnull.cc2016project.ui.activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import cz.ccnull.cc2016project.App;
import cz.ccnull.cc2016project.Config;
import cz.ccnull.cc2016project.R;
import cz.ccnull.cc2016project.model.Payment;
import io.chirp.sdk.ChirpSDK;
import io.chirp.sdk.ChirpSDKListener;
import io.chirp.sdk.model.ChirpError;
import io.chirp.sdk.model.ShortCode;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReceiverActivity extends AppCompatActivity {

    private static final int RESULT_REQUEST_RECORD_AUDIO = 0;

    private ChirpSDK mChirpSDK;

    private Payment mPayment;

    private Button mButtonSend;
    private TextView mTextStatus;

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String code = intent.getStringExtra(Config.KEY_PAYMENT_CODE);
            paymentConfirm(code);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_receiver);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    RESULT_REQUEST_RECORD_AUDIO);
        }

        mChirpSDK = new ChirpSDK(this, "", "");
        mChirpSDK.setListener(chirpSDKListener);

        mTextStatus = (TextView) findViewById(R.id.text_status);
        mButtonSend = (Button) findViewById(R.id.button_send_payment);
        mButtonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReceiverActivity.this, SenderActivity.class);
                startActivity(intent);
            }
        });

        if (savedInstanceState != null) {
            mPayment = savedInstanceState.getParcelable(Config.KEY_PAYMENT);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        paymentConfirm(null);
        mChirpSDK.startListening();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mChirpSDK.stopListening();
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter(Config.BROADCAST_CONFIRM);
        registerReceiver(mBroadcastReceiver, filter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(mBroadcastReceiver);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(Config.KEY_PAYMENT, mPayment);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case RESULT_REQUEST_RECORD_AUDIO: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("permission", "Granted");
                } else {
                    Log.d("permission", "Denied");
                }
            }
        }
    }

    private void paymentConfirm(String code) {
        if (code == null) {
            code = App.getInstance().getPreferences().getString(Config.KEY_CONFIRM, "");
            if (code.equals("")) return;
        }

        App.getInstance().getPreferences().edit().putString(Config.KEY_CONFIRM, "").commit();

        if (mPayment != null && mPayment.getCode().equals(code)) {
            Intent intent = new Intent(ReceiverActivity.this, ResultActivity.class);
            intent.putExtra(Config.KEY_PAYMENT, mPayment);
            intent.putExtra(Config.KEY_ROLE, Config.ROLE_RECEIVER);
            startActivity(intent);
        }
    }

    private ChirpSDKListener chirpSDKListener = new ChirpSDKListener() {
        @Override
        public void onChirpHeard(final ShortCode shortCode) {
            Log.d("listener", "ShortCode received: " + shortCode.getShortCode());
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mTextStatus.setText("Payment code received - sending to server");
                }
            });

            Call<Payment> call = App.getInstance().getApiDescription().paymentHeard(
                    App.getInstance().getCurrentUser().getAuthToken(),
                    shortCode.getShortCode(),
                    App.getInstance().getPreferences().getString(Config.SP_GCM_TOKEN_KEY, ""));

            call.enqueue(new Callback<Payment>() {
                @Override
                public void onResponse(Call<Payment> call, Response<Payment> response) {
                    mPayment = response.body();
                    if (mPayment != null) {
                        mPayment.setCode(shortCode.getShortCode());
                        mTextStatus.setText("Payment from: " + mPayment.getSenderName());
                    } else {
                        mTextStatus.setText("Sending to server failed...");
                    }
                }

                @Override
                public void onFailure(Call<Payment> call, Throwable t) {
                    mTextStatus.setText("Sending to server failed...");
                }
            });

        }

        @Override
        public void onChirpError(ChirpError error) {
            Log.d("listener", "ShortCode received error: " + error.getMessage());
        }
    };
}
