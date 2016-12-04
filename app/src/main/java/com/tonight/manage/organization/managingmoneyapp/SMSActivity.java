package com.tonight.manage.organization.managingmoneyapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.tonight.manage.organization.managingmoneyapp.Custom.CustomSelectEventForReceivedTossSMSPopup;

/**
 * Created by Taek on 2016. 12. 5..
 */

public class SMSActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms);
        CustomSelectEventForReceivedTossSMSPopup customSelectEventForReceivedTossSMSPopup = CustomSelectEventForReceivedTossSMSPopup.newInstance("");
        customSelectEventForReceivedTossSMSPopup.show(getSupportFragmentManager(), "create_group");
    }
}
