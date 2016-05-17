package cz.ccnull.cc2016project.ui.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import cz.ccnull.cc2016project.Config;
import cz.ccnull.cc2016project.R;
import cz.ccnull.cc2016project.model.Payment;

public class ResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_result);

        Intent intent = getIntent();

        String role = intent.getStringExtra(Config.KEY_ROLE);
        Payment payment = intent.getParcelableExtra(Config.KEY_PAYMENT);

        TextView text = (TextView) findViewById(R.id.text);

        if (role.equals(Config.ROLE_SENDER)) {
            text.setText("payment send to " + payment.getReceiverName() + " / amount: " + payment.getAmount());
        } else {
            text.setText("payment received from " + payment.getSenderName() + " / amount: " + payment.getAmount());
        }
    }
}
