package cz.ccnull.cc2016project.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import cz.ccnull.cc2016project.App;
import cz.ccnull.cc2016project.R;
import cz.ccnull.cc2016project.gcm.RegistrationIntentService;
import cz.ccnull.cc2016project.model.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    public static final String TAG = LoginActivity.class.getName();
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private EditText mEditLogin;
    private EditText mEditPassword;
    private Button mButtonLogin;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (checkPlayServices()) {
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }

        mEditLogin = (EditText) findViewById(R.id.edit_login);
        mEditPassword = (EditText) findViewById(R.id.edit_password);
        mButtonLogin = (Button) findViewById(R.id.button_login);
        mProgressBar = (ProgressBar) findViewById(android.R.id.progress);

        mButtonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String login = mEditLogin.getText().toString();
                if (login.equals("")) {
                    mEditLogin.setError(getString(R.string.login_empty_error));
                    return;
                }
                String password = mEditPassword.getText().toString();
                if (password.equals("")) {
                    mEditPassword.setError(getString(R.string.password_empty_error));
                    return;
                }

                Call<User> call = App.getInstance().getApiDescription().login(login, password);

                showProgress(true);

                call.enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        showProgress(false);
                        User user = response.body();
                        if (user != null && user.getAuthToken() != null) {
                            App.getInstance().setCurrentUser(user);
                            Intent intent = new Intent(LoginActivity.this, ReceiverActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(LoginActivity.this, R.string.login_call_error, Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        showProgress(false);
                        Toast.makeText(LoginActivity.this, R.string.login_call_error, Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    public void showProgress(boolean show) {
        mProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        mButtonLogin.setEnabled(!show);
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }
}
