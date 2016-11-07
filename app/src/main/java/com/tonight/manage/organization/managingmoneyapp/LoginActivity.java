package com.tonight.manage.organization.managingmoneyapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * Created by sujinKim on 2016-11-04.
 */

public class LoginActivity extends AppCompatActivity {

    Button mJoinBtn;
    Button mLoginBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
        mJoinBtn = (Button) findViewById(R.id.loginBtn);
        mLoginBtn = (Button) findViewById(R.id.joinBtn);
    }

    public void login(View v){
        startActivity(new Intent(this, InvitationActivity.class));
        finish();
    }

    public void join(View v){
        startActivity(new Intent(this, MemberListActivity.class));
    }


    //Invalid Login Data
    class CheckInvalidLoginDataAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... userID) {

            return null;
        }

        @Override
        protected void onPostExecute(String s) {

        }
    }


}