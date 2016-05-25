package cz.ccnull.cc2016project.ui.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import cz.ccnull.cc2016project.App;
import cz.ccnull.cc2016project.Config;
import cz.ccnull.cc2016project.R;
import cz.ccnull.cc2016project.gcm.RegistrationIntentService;
import cz.ccnull.cc2016project.model.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = LoginActivity.class.getName();
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private EditText mEditLogin;
    private EditText mEditPassword;
    private Button mButtonLogin;
    private CheckBox mCheckRemember;

    private ProgressDialog mDialogProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_login);

        if (checkPlayServices()) {
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }

        mEditLogin = (EditText) findViewById(R.id.edit_login);
        mEditPassword = (EditText) findViewById(R.id.edit_password);
        mButtonLogin = (Button) findViewById(R.id.button_login);
        mCheckRemember = (CheckBox) findViewById(R.id.check_remember);

        String savedLogin = App.getInstance().getPreferences().getString(Config.SP_LOGIN, "");
        if (!savedLogin.equals("")) {
            mEditLogin.setText(savedLogin);
            if (mEditPassword.requestFocus()) {
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            }
        }
        mCheckRemember.setChecked(!App.getInstance().getPreferences().getString(Config.SP_LOGIN, "").equals(""));

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
                        Log.d(TAG, "onResponseLogin");
                        showProgress(false);
                        User user = response.body();
                        if (user != null && user.getAuthToken() != null) {
                            App.getInstance().setCurrentUser(user);
                            Intent intent = new Intent(LoginActivity.this, ReceiverActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        } else {
                            Toast.makeText(LoginActivity.this, R.string.login_call_error, Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        Log.d(TAG, "onFailureLogin");
                        showProgress(false);
                        Toast.makeText(LoginActivity.this, R.string.login_call_error, Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        mCheckRemember.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String login = isChecked ? mEditLogin.getText().toString() : "";
                App.getInstance().getPreferences().edit().putString(Config.SP_LOGIN, login).commit();
            }
        });
    }

    private void showProgress(boolean show) {
        if (mDialogProgress == null) {
            mDialogProgress = new ProgressDialog(this);
            mDialogProgress.setMessage(getString(R.string.please_wait));
            mDialogProgress.setIndeterminate(true);
        }

        if (show) {
            mDialogProgress.show();
        } else {
            mDialogProgress.cancel();
        }
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
