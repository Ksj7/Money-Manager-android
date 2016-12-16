package com.tonight.manage.organization.managingmoneyapp;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.tonight.manage.organization.managingmoneyapp.Server.NetworkDefineConstant;
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
    boolean permissionOk = false;
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
        new CheckInvalidLoginDataAsyncTask().execute(id, pwd);
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
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                //단말기 OS버전이 마쉬멜로 버전 보다 작을때.....처리 코드
                loginstart();
            }
            else {
                //마쉬멜로 버전 이상일때.....처리 코드
                checkPermission();
                if(permissionOk == true){
                    loginstart();
                }
            }
        }
    }
    public void checkPermission(){
        if (ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            Log.e("문자 권한 없습니다", "ㅠㅠㅠㅠ");
            // 이 권한을 필요한 이유를 설명해야하는가?
            if (ActivityCompat.shouldShowRequestPermissionRationale(LoginActivity.this, Manifest.permission.READ_SMS)) {

                // 다이어로그같은것을 띄워서 사용자에게 해당 권한이 필요한 이유에 대해 설명합니다
                // 해당 설명이 끝난뒤 requestPermissions()함수를 호출하여 권한허가를 요청해야 합니다

                ActivityCompat.requestPermissions(LoginActivity.this,
                        new String[]{Manifest.permission.READ_SMS},
                        1);
            } else {

                ActivityCompat.requestPermissions(LoginActivity.this,
                        new String[]{Manifest.permission.READ_SMS},
                        1);

                // 필요한 권한과 요청 코드를 넣어서 권한허가요청에 대한 결과를 받아야 합니다

            }
        } else {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.canDrawOverlays(LoginActivity.this)) {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                            Uri.parse("package:" + getPackageName()));
                    startActivityForResult(intent, 1234);
                } else {
                    Log.e("권한전체다잇어야되","진짜?");
                    permissionOk = true;
                }
            }
        }
    }
        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
                Log.e("문자 권한 없습니다", "ㅠㅠㅠㅠ");
                // 이 권한을 필요한 이유를 설명해야하는가?
                if (ActivityCompat.shouldShowRequestPermissionRationale(LoginActivity.this, Manifest.permission.READ_SMS)) {

                    // 다이어로그같은것을 띄워서 사용자에게 해당 권한이 필요한 이유에 대해 설명합니다
                    // 해당 설명이 끝난뒤 requestPermissions()함수를 호출하여 권한허가를 요청해야 합니다

                } else {

                    ActivityCompat.requestPermissions(LoginActivity.this,
                            new String[]{Manifest.permission.READ_SMS},
                            1);

                    // 필요한 권한과 요청 코드를 넣어서 권한허가요청에 대한 결과를 받아야 합니다

                }
            } else {

                        if (!Settings.canDrawOverlays(LoginActivity.this)) {
                            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                    Uri.parse("package:" + getPackageName()));
                            startActivityForResult(intent, 1234);
                        }else{

                            loginstart();
                        }

                    // 이 권한을 필요한 이유를 설명해야하는가?
                    /*if (ActivityCompat.shouldShowRequestPermissionRationale(LoginActivity.this, Manifest.permission.SYSTEM_ALERT_WINDOW)) {

                        // 다이어로그같은것을 띄워서 사용자에게 해당 권한이 필요한 이유에 대해 설명합니다
                        // 해당 설명이 끝난뒤 requestPermissions()함수를 호출하여 권한허가를 요청해야 합니다

                    } else {

                        ActivityCompat.requestPermissions(LoginActivity.this,
                                new String[]{Manifest.permission.SYSTEM_ALERT_WINDOW},
                                2);

                        // 필요한 권한과 요청 코드를 넣어서 권한허가요청에 대한 결과를 받아야 합니다

                    }*/
            }
        }
    }

    public void loginstart() {
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
            if (enabledNotificationListeners == null || !enabledNotificationListeners.contains(packageName)) {
                // in this situation we know that the user has not granted the app the Notification access permission
                Log.e("체크되어있지않음.", "야호");
                Toast.makeText(getApplicationContext(), "AMA앱의 알림접근을 허용해주시기 바랍니다.", Toast.LENGTH_LONG).show();
                Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
                startActivityForResult(intent, 0);
            } else {
                Log.e("체크되어있음.", "ㅎㅎㅎㅎ");

                Intent serviceIntent = new Intent(LoginActivity.this, PasteService.class);//문자로 사용내역 추가 서비스
                startService(serviceIntent);

                Intent serviceIntent = new Intent(LoginActivity.this, PasteService.class);//문자로 사용내역 추가 서비스
                startService(serviceIntent);

                Intent i = new Intent(LoginActivity.this, GroupListActivity.class);
                i.putExtra("userId", idEditText.getText().toString());
                startActivity(i);
                finish();
            }

        } else {
            if (loginSuccess == 1) {
                Toast.makeText(LoginActivity.this, "비밀번호 오류입니다", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(LoginActivity.this, "아이디가 존재하지 않습니다", Toast.LENGTH_SHORT).show();
            }
            if (!autoLoginCheckbox.isChecked()) {
                idEditText.setText("");
            }
            pwdEditText.setText("");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.SYSTEM_ALERT_WINDOW) != PackageManager.PERMISSION_GRANTED) {
                        Log.e("문자 권한 있지만 윈도우 권한 없습니다", "ㅠㅠㅠㅠ");
                        // 이 권한을 필요한 이유를 설명해야하는가?
                        if (ActivityCompat.shouldShowRequestPermissionRationale(LoginActivity.this, Manifest.permission.SYSTEM_ALERT_WINDOW)) {

                            // 다이어로그같은것을 띄워서 사용자에게 해당 권한이 필요한 이유에 대해 설명합니다
                            // 해당 설명이 끝난뒤 requestPermissions()함수를 호출하여 권한허가를 요청해야 합니다

                        } else {

                            ActivityCompat.requestPermissions(LoginActivity.this,
                                    new String[]{Manifest.permission.SYSTEM_ALERT_WINDOW},
                                    2);

                            // 필요한 권한과 요청 코드를 넣어서 권한허가요청에 대한 결과를 받아야 합니다

                        }
                    } else {
                        Log.e("둘다 권한 있습니다!", "ㅎㅎ");
                        loginstart();
                    }
                    // 권한 허가
                    // 해당 권한을 사용해서 작업을 진행할 수 있습니다
                } else {
                    Log.e("권한 거부..", "하");
                    // 권한 거부
                    // 사용자가 해당권한을 거부했을때 해주어야 할 동작을 수행합니다
                }
                break;
            }
            case 2: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("이제 윈도우 권한 있습니다.", "ㅎㅎ");
                    loginstart();
                }else{

                    Log.e("윈도우 권한 거절..", "ㅎㅎ");
                }
            }
            return;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) { //이미지를 받으면 서버에 보내줌
        if (requestCode == 0) {
            loginstart();
        }
        if (requestCode == 1234) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.canDrawOverlays(this)) {
                    // SYSTEM_ALERT_WINDOW permission not granted...
                    if (ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
                        Log.e("문자 권한 없습니다", "ㅠㅠㅠㅠ");
                        // 이 권한을 필요한 이유를 설명해야하는가?

                            ActivityCompat.requestPermissions(LoginActivity.this,
                                    new String[]{Manifest.permission.READ_SMS},
                                    1);
                    }
                }else{
                    Log.e("권한전체다잇어야되","진짜?");
                    loginstart();
                }
            }
            Intent serviceIntent = new Intent(LoginActivity.this, PasteService.class);//문자로 사용내역 추가 서비스
            startService(serviceIntent);
            Intent i = new Intent(LoginActivity.this, GroupListActivity.class);
            i.putExtra("userId", idEditText.getText().toString());
            startActivity(i);
            finish();
        }
        if (requestCode == 1234) {
            if (!Settings.canDrawOverlays(this)) {
                // SYSTEM_ALERT_WINDOW permission not granted...
                loginstart();
            }
        }
    }


}