package cz.ccnull.cc2016project.ui.activity;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import cz.ccnull.cc2016project.App;
import cz.ccnull.cc2016project.R;
import cz.ccnull.cc2016project.listener.OnItemClickListener;
import cz.ccnull.cc2016project.model.Payment;
import cz.ccnull.cc2016project.model.Receiver;
import cz.ccnull.cc2016project.provider.DataProvider;
import cz.ccnull.cc2016project.ui.adapter.ReceiverAdapter;
import io.chirp.sdk.ChirpSDK;
import io.chirp.sdk.ChirpSDKListener;
import io.chirp.sdk.model.ChirpError;
import io.chirp.sdk.model.ShortCode;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SenderActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int LOADER_RECEIVERS = 1;

    private EditText mEditAmount;
    private Button mButtonCreate;
    private Button mButtonPlay;
    private ProgressBar mProgressBar;
    private RecyclerView mRecyclerView;

    private ReceiverAdapter mAdapter;

    private Payment mPayment;

    private ChirpSDK mChirpSDK;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sender);

        mEditAmount = (EditText) findViewById(R.id.edit_amount);
        mButtonCreate = (Button) findViewById(R.id.button_create_payment);
        mButtonPlay = (Button) findViewById(R.id.button_play_sound);
        mProgressBar = (ProgressBar) findViewById(android.R.id.progress);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler);
        initRecyclerView();

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
                int amountNumber = Integer.parseInt(amount);

                Call<Payment> call = App.getInstance().getApiDescription().createPayment(
                        App.getInstance().getCurrentUser().getAuthToken(),
                        amountNumber);

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

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (mPayment == null) return null;
        Uri uri = DataProvider.RECEIVERS_URI.buildUpon().appendPath(mPayment.getCode()).build();
        return new CursorLoader(this, uri, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null) {
            mAdapter.setData(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.setData(null);
    }

    public void showProgressCreate(boolean show) {
        mProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        mButtonCreate.setEnabled(!show);
    }

    private void initRecyclerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mAdapter = new ReceiverAdapter(new OnItemClickListener<Receiver>() {
            @Override
            public void onItemClick(Receiver receiver) {
                Call<Payment> call = App.getInstance().getApiDescription().paymentConfirm(
                        App.getInstance().getCurrentUser().getAuthToken(),
                        receiver.getPaymentCode(),
                        receiver.getUserId());

                call.enqueue(new Callback<Payment>() {
                    @Override
                    public void onResponse(Call<Payment> call, Response<Payment> response) {
                        if (response.body() != null) {
                            Toast.makeText(SenderActivity.this, R.string.payment_successful, Toast.LENGTH_LONG).show();
                            finish();
                        } else {
                            Toast.makeText(SenderActivity.this, R.string.confirm_call_error, Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Payment> call, Throwable t) {
                        Toast.makeText(SenderActivity.this, R.string.confirm_call_error, Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
        mRecyclerView.setAdapter(mAdapter);
    }

    private void paymentCodeReady() {
        mButtonCreate.setVisibility(View.GONE);
        mEditAmount.setEnabled(false);
        mButtonPlay.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);

        playSound();

        getSupportLoaderManager().initLoader(LOADER_RECEIVERS, null, this);
    }

    private void playSound() {
        ShortCode code = new ShortCode(mPayment.getCode());
        mChirpSDK.play(code);
    }

    private ChirpSDKListener chirpSDKListener = new ChirpSDKListener() {
        @Override
        public void onChirpHeard(final ShortCode shortCode) {
        }

        @Override
        public void onChirpError(ChirpError error) {
        }
    };
}
