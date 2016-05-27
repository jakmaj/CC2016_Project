package cz.ccnull.cc2016project.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import cz.ccnull.cc2016project.App;
import cz.ccnull.cc2016project.Config;
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

    private TextView mTextAmount;
    private EditText mEditAmount;
    private Button mButtonCreate;
    private Button mButtonPlay;
    private ProgressBar mProgressBar;
    private RecyclerView mRecyclerView;
    private View mSearchingView;

    private ReceiverAdapter mAdapter;

    private Payment mPayment;

    private ChirpSDK mChirpSDK;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_sender);

        mTextAmount = (TextView) findViewById(R.id.text_amount);
        mEditAmount = (EditText) findViewById(R.id.edit_amount);
        mButtonCreate = (Button) findViewById(R.id.button_create_payment);
        mButtonPlay = (Button) findViewById(R.id.button_play_sound);
        mProgressBar = (ProgressBar) findViewById(android.R.id.progress);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler);
        mSearchingView = findViewById(R.id.layout_searching);
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
                        amountNumber,
                        App.getInstance().getPreferences().getString(Config.SP_GCM_TOKEN_KEY, ""));

                showProgressCreate(true);

                call.enqueue(new Callback<Payment>() {
                    @Override
                    public void onResponse(Call<Payment> call, Response<Payment> response) {
                        showProgressCreate(false);
                        Payment payment = response.body();
                        if (payment != null && payment.getCode() != null) {
                            mPayment = payment;
                            mPayment.setAmount(Integer.parseInt(mEditAmount.getText().toString()));
                            paymentCodeReady(true);
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

        if (savedInstanceState != null) {
            mPayment = savedInstanceState.getParcelable(Config.KEY_PAYMENT);
            if (mPayment != null) {
                paymentCodeReady(false);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mChirpSDK.setListener(chirpSDKListener);
        mChirpSDK.startListening();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mChirpSDK.stopListening();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(Config.KEY_PAYMENT, mPayment);
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
            if (data.getCount() > 0) {
                mSearchingView.setVisibility(View.GONE);
            }
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
        mAdapter = new ReceiverAdapter(this, new OnItemClickListener<Receiver>() {
            @Override
            public void onItemClick(final Receiver receiver) {
                Call<Payment> call = App.getInstance().getApiDescription().paymentConfirm(
                        App.getInstance().getCurrentUser().getAuthToken(),
                        receiver.getPaymentCode(),
                        receiver.getUserId());

                call.enqueue(new Callback<Payment>() {
                    @Override
                    public void onResponse(Call<Payment> call, Response<Payment> response) {
                        if (response.body() != null) {
                            mPayment.setReceiverName(receiver.getName());
                            Intent intent = new Intent(SenderActivity.this, ResultActivity.class);
                            intent.putExtra(Config.KEY_PAYMENT, mPayment);
                            intent.putExtra(Config.KEY_ROLE, Config.ROLE_SENDER);
                            startActivity(intent);
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

    private void paymentCodeReady(boolean play) {
        mButtonCreate.setVisibility(View.GONE);
        mTextAmount.setVisibility(View.GONE);
        mEditAmount.setVisibility(View.GONE);
        mButtonPlay.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
        mSearchingView.setVisibility(View.VISIBLE);

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mEditAmount.getWindowToken(), 0);

        if (play) playSound();

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
