package com.tonight.manage.organization.managingmoneyapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.tonight.manage.organization.managingmoneyapp.Custom.CustomSelectEventForReceivedTossSMSPopup;
import com.tonight.manage.organization.managingmoneyapp.Custom.CustomSelectEventForUsageSMSPupup;

/**
 * Created by Taek on 2016. 12. 5..
 */

public class AddUsageByPasteActivity extends AppCompatActivity {
    public static Activity activityInstance;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms);
        Intent intent = getIntent();
        String smsMoney = intent.getStringExtra("smsMoney");
        String smsDate = intent.getStringExtra("smsDate");
        String smsContent = intent.getStringExtra("smsContent");
        CustomSelectEventForUsageSMSPupup customSelectEventForUsageSMSPupup = CustomSelectEventForUsageSMSPupup.newInstance(smsMoney,smsDate,smsContent);
        customSelectEventForUsageSMSPupup.show(getSupportFragmentManager(),"add usage");
        activityInstance = this;
    }
}
