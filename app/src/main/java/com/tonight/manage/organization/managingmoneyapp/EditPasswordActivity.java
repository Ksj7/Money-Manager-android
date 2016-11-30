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

public class EditPasswordActivity extends AppCompatActivity {

    private EditText originalPassword;
    private EditText changePassword;
    private Button confirmButton;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_edit_password);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("비밀번호 변경");
        setSupportActionBar(toolbar);

        originalPassword = (EditText) findViewById(R.id.originalPassword);
        changePassword = (EditText) findViewById(R.id.changePassword);
        confirmButton = (Button) findViewById(R.id.confirmBtn);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String origin = originalPassword.getText().toString();
                String change = changePassword.getText().toString();
                //UpdatePasswordDataAsyncTask(origin, change);
            }
        });

    }

    //Update Password Data
    class UpdatePasswordDataAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... userID) {

            return null;
        }

        @Override
        protected void onPostExecute(String s) {

        }
    }
}

