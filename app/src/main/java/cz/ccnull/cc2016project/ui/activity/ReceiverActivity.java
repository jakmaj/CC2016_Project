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

import cz.ccnull.cc2016project.R;
import io.chirp.sdk.ChirpSDK;
import io.chirp.sdk.ChirpSDKListener;
import io.chirp.sdk.model.ChirpError;
import io.chirp.sdk.model.ShortCode;

public class ReceiverActivity extends AppCompatActivity {

    private static final int RESULT_REQUEST_RECORD_AUDIO = 0;

    private ChirpSDK mChirpSDK;
    private Button mButtonSend;
    private ChirpSDKListener chirpSDKListener = new ChirpSDKListener() {
        @Override
        public void onChirpHeard(final ShortCode shortCode) {
            Log.d("listener", "ShortCode received: " + shortCode.getShortCode());
            runOnUiThread(new Runnable() { // Chirp is listening on background thread, so need this to manipulate with UI
                @Override
                public void run() {
                    //TODO
                }
            });
        }

        @Override
        public void onChirpError(ChirpError error) {
            Log.d("listener", "ShortCode received error: " + error.getMessage());
        }
    };

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
}
