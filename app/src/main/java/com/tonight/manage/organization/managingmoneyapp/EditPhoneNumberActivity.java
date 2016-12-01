package com.tonight.manage.organization.managingmoneyapp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by sujinKim on 2016-11-30.
 */

public class EditPhoneNumberActivity extends AppCompatActivity {

    private EditText changePhoneNumber;
    private Button confirmButton;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_edit_phonenumber);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("휴대폰 번호 변경");
        setSupportActionBar(toolbar);

        changePhoneNumber = (EditText) findViewById(R.id.changePhoneNumber);
        confirmButton = (Button) findViewById(R.id.confirmBtn);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = changePhoneNumber.getText().toString();
                //UpdatePhoneNumberDataAsyncTask(phone);
            }
        });

    }

    //Update PhoneNumber Data
    class UpdatePhoneNumberDataAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... userID) {

            return null;
        }

        @Override
        protected void onPostExecute(String s) {

        }
    }
}
