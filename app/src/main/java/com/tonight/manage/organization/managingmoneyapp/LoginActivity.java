package com.tonight.manage.organization.managingmoneyapp;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.tonight.manage.organization.managingmoneyapp.Server.NetworkDefineConstant;
import com.tonight.manage.organization.managingmoneyapp.Service.NotificationService;
import com.tonight.manage.organization.managingmoneyapp.Service.PasteService;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by sujinKim on 2016-11-04.
 */

public class LoginActivity extends AppCompatActivity {
    EditText idEditText;
    EditText pwdEditText;
    Button mJoinBtn;
    Button mLoginBtn;
    CheckBox autoLoginCheckbox;
    int loginSuccess;//로그인 성공여부
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
        mJoinBtn = (Button) findViewById(R.id.loginBtn);
        mLoginBtn = (Button) findViewById(R.id.joinBtn);
        autoLoginCheckbox = (CheckBox) findViewById(R.id.idSavedCheckbox);
        idEditText = (EditText) findViewById(R.id.idEdit);
        pwdEditText = (EditText) findViewById(R.id.passwordEdit);
        SharedPreferences pref = getSharedPreferences("Login", MODE_PRIVATE);
        if (pref.getBoolean("autoLogin", false)) {//자동로그인 체크 해놨었다면,
            autoLoginCheckbox.setChecked(true);
            idEditText.setText(pref.getString("id", ""));
        }
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("loginok", false);
        editor.apply();
    }

    public void login(View v) {
        String id = idEditText.getText().toString();
        String pwd = pwdEditText.getText().toString();
        new CheckInvalidLoginDataAsyncTask().execute(id,pwd);
    }

    public void join(View v) {
        startActivity(new Intent(this, JoinActivity.class));
    }


    //Invalid Login Data
    class CheckInvalidLoginDataAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... args) {
            String requestURL = "";
            Response response = null;
            try {

                requestURL = NetworkDefineConstant.SERVER_URL_LOGIN;

                //연결
                OkHttpClient toServer = NetworkDefineConstant.getOkHttpClient();
                FormBody.Builder builder = new FormBody.Builder();
                builder.add("userid", args[0]).add("userpw", args[1]);
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
                        Log.d("login", "로그인 성공");
                        loginSuccess = 0;
                    } else if (result.contains("1")) {
                        Log.d("login", "비밀번호 일치 x");
                        loginSuccess = 1;
                    } else {
                        Log.d("login", "아이디 존재 x");
                        loginSuccess = 2;
                    }

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
            super.onPostExecute(s);
            if (loginSuccess == 0) {//로그인 성공시
                if (autoLoginCheckbox.isChecked()) {
                    SharedPreferences pref = getSharedPreferences("Login", MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putBoolean("autoLogin", true);
                    editor.putString("id", idEditText.getText().toString());
                    editor.putBoolean("loginok", true);
                    editor.apply();
                } else {
                    SharedPreferences pref = getSharedPreferences("Login", MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putBoolean("autoLogin", false);
                    editor.putString("id", idEditText.getText().toString());
                    editor.putBoolean("loginok", true);
                    editor.apply();
                }
                ContentResolver contentResolver = getContentResolver();
                String enabledNotificationListeners = Settings.Secure.getString(contentResolver, "enabled_notification_listeners");
                String packageName = getPackageName();

// check to see if the enabledNotificationListeners String contains our package name
                if (enabledNotificationListeners == null || !enabledNotificationListeners.contains(packageName))
                {
                    // in this situation we know that the user has not granted the app the Notification access permission
                    Log.e("체크되어있지않음.","야호");
                    Toast.makeText(getApplicationContext(),"AMA앱의 알림접근을 허용해주시기 바랍니다.",Toast.LENGTH_LONG).show();
                    Intent intent=new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
                    startActivity(intent);
                }
                else
                {
                    Log.e("체크되어있음.","ㅎㅎㅎㅎ");
                }
                Intent serviceIntent = new Intent(LoginActivity.this, PasteService.class);//문자로 사용내역 추가 서비스
                startService(serviceIntent);

                Intent i = new Intent(LoginActivity.this, GroupListActivity.class);
                i.putExtra("userId",idEditText.getText().toString());
                startActivity(i);
                finish();
            }
            else {
                if (loginSuccess == 1) {
                    Toast.makeText(LoginActivity.this, "비밀번호 오류입니다", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(LoginActivity.this, "아이디가 존재하지 않습니다", Toast.LENGTH_SHORT).show();
                }
                if(!autoLoginCheckbox.isChecked()) {
                    idEditText.setText("");
                }
                pwdEditText.setText("");
            }
        }
    }
}