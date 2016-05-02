package cz.ccnull.cc2016project.ui.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import cz.ccnull.cc2016project.App;
import cz.ccnull.cc2016project.R;
import cz.ccnull.cc2016project.model.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText mEditLogin;
    private EditText mEditPassword;
    private Button mButtonLogin;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

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
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
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
}
