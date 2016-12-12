package com.tonight.manage.organization.managingmoneyapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.tonight.manage.organization.managingmoneyapp.Server.NetworkDefineConstant;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by sujinKim on 2016-11-04.
 */

public class JoinActivity extends AppCompatActivity{

    private boolean joinSuccess;
    EditText idEdit;
    EditText pwdEdit;
    EditText pwdCheckEdit;
    EditText nameEdit;
    EditText phoneNumberEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.join_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        idEdit = (EditText) findViewById(R.id.idEdit);
        pwdEdit = (EditText) findViewById(R.id.passwordEdit);
        pwdCheckEdit = (EditText) findViewById(R.id.passwordCheckEdit);
        nameEdit = (EditText) findViewById(R.id.nameEdit);
        phoneNumberEdit = (EditText) findViewById(R.id.phoneNumberEdit);

        findViewById(R.id.joinBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = idEdit.getText().toString();
                String pwd = pwdEdit.getText().toString();
                String pwdCheck = pwdCheckEdit.getText().toString();
                String name = nameEdit.getText().toString();
                String phone = phoneNumberEdit.getText().toString();
                if (id.equals("") || pwd.equals("") || pwdCheck.equals("") || pwdCheck.equals("")
                        || name.equals("") || phone.equals("")) {
                    Toast.makeText(JoinActivity.this, "입력하지 않은 칸이 있습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    if (pwd.equals(pwdCheck)) {
                        new UpdateJoinDataAsyncTask().execute(id, pwd, name, phone);
                    } else {
                        Toast.makeText(JoinActivity.this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();

                        pwdEdit.setText("");
                        pwdCheckEdit.setText("");
                    }
                }
            }
        });
    }

    public void join(View v) {
        startActivity(new Intent(this, LoginActivity.class));
    }

    //Update Join Data
    class UpdateJoinDataAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... args) {
            String requestURL = "";
            Response response = null;
            try {
                requestURL = NetworkDefineConstant.SERVER_URL_JOIN;

                //연결
                OkHttpClient toServer = NetworkDefineConstant.getOkHttpClient();
                FormBody.Builder builder = new FormBody.Builder();
                builder.add("userid", args[0])
                        .add("userpw", args[1])
                        .add("username", args[2])
                        .add("phone", args[3]);
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
                    String result = (resBody.string());
                    if (result.contains("0")) {
                        Log.d("join", "아이디 중복");
                        joinSuccess = false;
                    } else if (result.contains("1")) {
                        Log.d("join", "회원가입 완료");
                        joinSuccess = true;
                    } else {
                        Log.d("join", "오류");
                    }

                } else { //실패시 정의
                    Log.e("에러", "데이터를 로드하는데 실패하였습니다");
                }
            } catch (Exception e) {
                Log.e("요청중에러", "가입", e);
            } finally {
                if (response != null) {
                    response.close();
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (joinSuccess) {//회원가입 성공시
                Toast.makeText(JoinActivity.this, "회원 가입 완료되었습니다.", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(JoinActivity.this, LoginActivity.class));
                finish();
            } else {
                Toast.makeText(JoinActivity.this, "아이디 중복", Toast.LENGTH_SHORT).show();
                idEdit.setText("");
            }
        }
    }
}
