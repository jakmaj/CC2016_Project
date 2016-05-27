package cz.ccnull.cc2016project.ui.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
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

        TextView textName = (TextView) findViewById(R.id.text_name);
        TextView textStatus = (TextView) findViewById(R.id.text_status);
        ImageView imagePhoto = (ImageView) findViewById(R.id.image_photo);

        if (role.equals(Config.ROLE_SENDER)) {
            imagePhoto.setImageDrawable(getUserPhoto(payment.getReceiverName()));
            textName.setText(payment.getReceiverName());
            textStatus.setText(getString(R.string.payment_successful_send, payment.getAmount()));
        } else {
            imagePhoto.setImageDrawable(getUserPhoto(payment.getSenderName()));
            textName.setText(payment.getSenderName());
            textStatus.setText(getString(R.string.payment_successful_receive, payment.getAmount()));
        }
    }

    private Drawable getUserPhoto(String name) {
        switch (name) {
            case "První uživatel":
                return ResourcesCompat.getDrawable(getResources(), R.drawable.user_pic1, null);
            case "Druhý uživatel":
                return ResourcesCompat.getDrawable(getResources(), R.drawable.user_pic2, null);
            default:
                return ResourcesCompat.getDrawable(getResources(), R.drawable.user_pic3, null);
        }
    }
}
