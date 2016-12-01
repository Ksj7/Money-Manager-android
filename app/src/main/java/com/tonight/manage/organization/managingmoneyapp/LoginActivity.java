package com.tonight.manage.organization.managingmoneyapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

/**
 * Created by sujinKim on 2016-11-04.
 */

public class LoginActivity extends AppCompatActivity {
    EditText idEditText;
    Button mJoinBtn;
    Button mLoginBtn;
    CheckBox autoLoginCheckbox;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
        mJoinBtn = (Button) findViewById(R.id.loginBtn);
        mLoginBtn = (Button) findViewById(R.id.joinBtn);
        autoLoginCheckbox = (CheckBox) findViewById(R.id.idSavedCheckbox);
        idEditText = (EditText) findViewById(R.id.idEdit);

        SharedPreferences pref = getSharedPreferences("Login", MODE_PRIVATE);
        if(pref.getBoolean("autoLogin",false) == true){//자동로그인 체크 해놨었다면,
            autoLoginCheckbox.setChecked(true);
            idEditText.setText(pref.getString("id",""));
        }
    }

    public void login(View v){
        boolean loginSuccess = true;//로그인 성공여부

        if(loginSuccess == true){//로그인 성공시
            if(autoLoginCheckbox.isChecked() == true){
                SharedPreferences pref = getSharedPreferences("Login", MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.putBoolean("autoLogin", true);
                editor.putString("id", idEditText.getText().toString());
                editor.commit();
            }else{
                SharedPreferences pref = getSharedPreferences("Login", MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.putBoolean("autoLogin", false);
                editor.putString("id", "");
                editor.commit();
            }
            startActivity(new Intent(this, GroupListActivity.class));
            finish();
        }else{//실패시 조치

        }
    }

    public void join(View v){
        startActivity(new Intent(this, JoinActivity.class));
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