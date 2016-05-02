package cz.ccnull.cc2016project.ui.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

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

public class SenderActivity extends AppCompatActivity {

    private EditText mEditAmount;
    private Button mButtonCreate;
    private Button mButtonPlay;
    private ProgressBar mProgressBar;

    private Payment mPayment;

    private ChirpSDK mChirpSDK;
    private ChirpSDKListener chirpSDKListener = new ChirpSDKListener() {
        @Override
        public void onChirpHeard(final ShortCode shortCode) {
        }

        @Override
        public void onChirpError(ChirpError error) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sender);

        mEditAmount = (EditText) findViewById(R.id.edit_amount);
        mButtonCreate = (Button) findViewById(R.id.button_create_payment);
        mButtonPlay = (Button) findViewById(R.id.button_play_sound);
        mProgressBar = (ProgressBar) findViewById(android.R.id.progress);

        mButtonPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playSound();
            }
        });

        mButtonCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String amount = mEditAmount.getText().toString();
                if (amount.equals("")) {
                    mEditAmount.setError(getString(R.string.amount_empty_error));
                    return;
                }

                Call<Payment> call = App.getInstance().getApiDescription().createPayment(
                        App.getInstance().getCurrentUser().getAuthToken(),
                        amount
                );

                showProgressCreate(true);

                call.enqueue(new Callback<Payment>() {
                    @Override
                    public void onResponse(Call<Payment> call, Response<Payment> response) {
                        showProgressCreate(false);
                        Payment payment = response.body();
                        if (payment != null && payment.getCode() != null) {
                            mPayment = payment;
                            paymentCodeReady();
                        } else {
                            Toast.makeText(SenderActivity.this, R.string.create_call_error, Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Payment> call, Throwable t) {
                        showProgressCreate(false);
                        Toast.makeText(SenderActivity.this, R.string.create_call_error, Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        mChirpSDK = new ChirpSDK(this, "", "");
        mChirpSDK.setListener(chirpSDKListener);
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

    public void showProgressCreate(boolean show) {
        mProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        mButtonCreate.setEnabled(!show);
    }

    private void paymentCodeReady() {
        mButtonCreate.setVisibility(View.GONE);
        mButtonPlay.setVisibility(View.VISIBLE);
        mEditAmount.setEnabled(false);

        playSound();
    }

    private void playSound() {
        ShortCode code = new ShortCode(mPayment.getCode());
        mChirpSDK.play(code);
    }
}
