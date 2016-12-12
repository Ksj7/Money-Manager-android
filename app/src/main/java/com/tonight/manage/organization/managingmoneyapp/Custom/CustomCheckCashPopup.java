package com.tonight.manage.organization.managingmoneyapp.Custom;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.tonight.manage.organization.managingmoneyapp.EventInfoActivity;
import com.tonight.manage.organization.managingmoneyapp.R;
import com.tonight.manage.organization.managingmoneyapp.RequestHandler;
import com.tonight.manage.organization.managingmoneyapp.Server.NetworkDefineConstant;

import java.io.IOException;
import java.util.HashMap;
import android.app.ProgressDialog;
import android.widget.Toast;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class CustomCheckCashPopup extends DialogFragment {
    static String username;
    static String userid;
    static String eventnum;
    private int isSuccess;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, R.style.CustomDialogTheme);
    }

    public static CustomCheckCashPopup newInstance(String name,String id,String num) {
        username = name;
        userid = id;
        eventnum = num;
        CustomCheckCashPopup CheckCashPopup = new CustomCheckCashPopup();
        return CheckCashPopup;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.popup_check_cash,container,false);
        TextView usernameText = (TextView) view.findViewById(R.id.usernameText);
        Button Ybtn = (Button) view.findViewById(R.id.confirmBtn);
        Button NBtn = (Button) view.findViewById(R.id.negativeBtn);

        usernameText.setText(username);
        Ybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {//현금체크 ok
                    uploadPaymentAsyncTask uploadImage = new uploadPaymentAsyncTask();
                    uploadImage.execute(eventnum,userid);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                EventInfoActivity.pagerInstance.notifyDataSetChanged();
                dismiss();
            }

        });
        NBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        return view;

    }

    @Override
    public void onStop() {
        super.onStop();
        dismiss();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        int width = getResources().getDimensionPixelSize(R.dimen.popup_cash_check_width);
        int height = getResources().getDimensionPixelSize(R.dimen.popup_cash_check_height);
        getDialog().getWindow().setLayout(width,height);
    }


    class uploadPaymentAsyncTask extends AsyncTask<String, Void, String> {

        ProgressDialog loading;
        RequestHandler handler = new RequestHandler();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = ProgressDialog.show(getActivity(), "Uploading...", null, true, true);
        }

        @Override
        protected String doInBackground(String... params) {
            String requestURL = "";
            Response response = null;
            try {
                requestURL = NetworkDefineConstant.SERVER_URL_EVENT_INFO;

                OkHttpClient toServer = NetworkDefineConstant.getOkHttpClient();
                FormBody.Builder builder = new FormBody.Builder();
                builder.add("signal", "4")
                        .add("eventnum", params[0])
                        .add("userid", params[1]);
                FormBody formBody = builder.build();

                Request request = new Request.Builder()
                        .url(requestURL)
                        .post(formBody)
                        .build();

                response = toServer.newCall(request).execute();
                boolean flag = response.isSuccessful();
                ResponseBody resBody = response.body();

                if (flag) {
                    String valid = resBody.string();
                    if (valid.contains("1"))
                        isSuccess = 1;
                    else if(valid.contains("2"))
                        isSuccess = 2;
                    else
                        isSuccess = 0;
                } else {
                    Log.e("에러", "현금 체크 에러");
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            } finally {
                if (response != null) {
                    response.close();
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(String aVoid) {
            super.onPostExecute(aVoid);
            loading.dismiss();//progressbar 종료

            if (isSuccess==1) {
               // Toast.makeText(getActivity(), "지출확인", Toast.LENGTH_SHORT).show();
            } else if(isSuccess==2){
                //Toast.makeText(getActivity(), "목표금액이 0", Toast.LENGTH_SHORT).show();
            } else{
                //Toast.makeText(getActivity(), "에러!", Toast.LENGTH_SHORT).show();
            }
            dismiss();
        }

    }

}
