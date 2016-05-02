package cz.ccnull.cc2016project.ui.activity;

import android.Manifest;
import android.content.Intent;
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

    private Button mButtonSend;
    private TextView mTextStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
    }

    @Override
    protected void onResume() {
        super.onResume();
        mChirpSDK.startListening();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mChirpSDK.stopListening();
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
                    shortCode.getShortCode());

            call.enqueue(new Callback<Payment>() {
                @Override
                public void onResponse(Call<Payment> call, Response<Payment> response) {
                    Payment payment = response.body();
                    if (payment != null) {
                        mTextStatus.setText("Payment from: " + payment.getSenderName());
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
