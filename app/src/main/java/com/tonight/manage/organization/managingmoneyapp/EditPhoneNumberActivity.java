package com.tonight.manage.organization.managingmoneyapp;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.tonight.manage.organization.managingmoneyapp.Server.NetworkDefineConstant;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by sujinKim on 2016-11-30.
 */

public class EditPhoneNumberActivity extends AppCompatActivity {

    private EditText changePhoneNumber;
    private Button confirmButton;
    private boolean isSuccess;

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
                new UpdatePhoneNumberDataAsyncTask().execute(phone);
            }
        });

    }

    //Update PhoneNumber Data
    class UpdatePhoneNumberDataAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... args) {

            String requestURL = "";
            Response response = null;
            try {

                requestURL = NetworkDefineConstant.SERVER_URL_CHANGES;

                //연결
                OkHttpClient toServer = NetworkDefineConstant.getOkHttpClient();
                FormBody.Builder builder = new FormBody.Builder();
                SharedPreferences pref = getSharedPreferences("Login", MODE_PRIVATE);
                builder.add("userid", pref.getString("id","error")).add("phone",args[0]).add("signal", "0");
                FormBody formBody = builder.build();
                //요청
                Request request = new Request.Builder()
                        .url(requestURL)
                        .post(formBody)
                        .build();
                //응답
                response = toServer.newCall(request).execute();
                boolean flag = response.isSuccessful();
                ResponseBody resBody = response.body();

                if (flag) { //http req/res 성공
                    String res = resBody.string();
                    if(res.contains("0"))
                        isSuccess = true;
                    else isSuccess = false;
                } else { //실패시 정의
                    Log.e("에러", "데이터를 로드하는데 실패하였습니다");
                }
            } catch (Exception e) {
                Log.e("요청중에러", "그룹 리스트", e);
            } finally {
                if (response != null) {
                    response.close();
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            if(isSuccess) {
                Toast.makeText(EditPhoneNumberActivity.this,"휴대폰 번호 변경 완료",Toast.LENGTH_SHORT).show();
                finish();
            }
            else{
                Toast.makeText(EditPhoneNumberActivity.this,"휴대폰 번호 변경 실패",Toast.LENGTH_SHORT).show();
            }
        }
    }
}
