package com.tonight.manage.organization.managingmoneyapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.tonight.manage.organization.managingmoneyapp.Custom.CustomSelectEventForReceivedTossSMSPopup;
/**
 * Created by Taek on 2016. 12. 5..
 */

public class AddPayCheckByTossNotificationActivity extends AppCompatActivity {
    public static Activity activityInstance;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms);
        Intent intent = getIntent();
        int money = intent.getIntExtra("money",0);
        String name = intent.getStringExtra("name");
        CustomSelectEventForReceivedTossSMSPopup customSelectEventForReceivedTossSMSPopup = CustomSelectEventForReceivedTossSMSPopup.newInstance(name,money);
        customSelectEventForReceivedTossSMSPopup.show(getSupportFragmentManager(), "add paycheck");
        activityInstance = this;
    }
}
