package com.tonight.manage.organization.managingmoneyapp.Custom;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.SwitchCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.tonight.manage.organization.managingmoneyapp.R;
import com.tonight.manage.organization.managingmoneyapp.Server.NetworkDefineConstant;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by sujinKim on 2016-11-04.
 */

public class CustomAddMoneyPopup extends DialogFragment {

    private boolean isPortion;
    private boolean isSum;
    RelativeLayout wrapLayout;
    private boolean isSuccess;
    Bundle b;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, R.style.CustomDialogTheme);
    }

    public static CustomAddMoneyPopup newInstance(String eventnum) {
        CustomAddMoneyPopup togglePopup = new CustomAddMoneyPopup();
        Bundle b = new Bundle();
        b.putString("eventnum",eventnum);
        togglePopup.setArguments(b);
        return togglePopup;
    }




    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.popup_add_money, container, false);

        b = getArguments();
        if(b== null){
            Toast.makeText(getActivity(), "에러발생!", Toast.LENGTH_SHORT).show();
            dismiss();
        }
        final String eventnum = b.getString("eventnum");

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        final TextInputLayout portionLayout = (TextInputLayout) view.findViewById(R.id.portionText);
        final TextInputLayout sumLayout = (TextInputLayout) view.findViewById(R.id.sumText);

        wrapLayout = (RelativeLayout) view.findViewById(R.id.wrapLayout);

        Button positiveButton = (Button) view.findViewById(R.id.confirmBtn);
        Button negativeButton = (Button) view.findViewById(R.id.negativeBtn);

        final AppCompatEditText portionEdit = (AppCompatEditText)view.findViewById(R.id.portionEdit);
        final AppCompatEditText sumEdit = (AppCompatEditText) view.findViewById(R.id.sumEdit);

        portionLayout.setVisibility(View.GONE);
        sumLayout.setVisibility(View.GONE);

        final SwitchCompat portionSwitch = (SwitchCompat) view.findViewById(R.id.portionSwitch);
        final SwitchCompat sumSwitch = (SwitchCompat) view.findViewById(R.id.sumSwitch);


        portionSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isPortion = isChecked;
                if (isChecked) {
                    if (isSum) {
                        sumSwitch.setChecked(false);
                        sumLayout.setVisibility(View.GONE);
                        isSum = false;
                    }
                    portionLayout.setVisibility(View.VISIBLE);
                } else {
                    portionLayout.setVisibility(View.GONE);

                }
            }
        });

        sumSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isSum = isChecked;
                if (isChecked) {
                    if (isPortion) {
                        portionSwitch.setChecked(false);
                        portionLayout.setVisibility(View.GONE);
                        isPortion = false;
                    }
                    sumLayout.setVisibility(View.VISIBLE);
                } else {
                    sumLayout.setVisibility(View.GONE);
                }
            }
        });

        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isPortion){
                    String portion = portionEdit.getText().toString();
                    new UpdateAddMoneyDataAsyncTask().execute("1",eventnum,portion);
                }
                else if(isSum){
                    String sum = sumEdit.getText().toString();
                    new UpdateAddMoneyDataAsyncTask().execute("2",eventnum,sum);
                }
            }
        });

        negativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

    //Update Add Money Data
    class UpdateAddMoneyDataAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String requestURL = "";
            Response response = null;
            try {
                requestURL = NetworkDefineConstant.SERVER_URL_EVENT_INFO;

                OkHttpClient toServer = NetworkDefineConstant.getOkHttpClient();
                FormBody.Builder builder = new FormBody.Builder();
                if(params[0].equals("1")) {
                    builder.add("signal", params[0])
                            .add("eventnum",params[1])
                            .add("personalm", params[2]);
                }
                else if(params[0].equals("2")){
                    builder.add("signal", params[0])
                            .add("eventnum",params[1])
                            .add("targetm", params[2]);

                }

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
                        isSuccess = true;
                    else
                        isSuccess = false;

                } else {
                    Log.e("에러", "금액 추가 실패");
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            } finally
            {
                if (response != null) {
                    response.close();
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(String aVoid) {
            super.onPostExecute(aVoid);
            if (isSuccess) {
                Toast.makeText(getActivity(), "목표액이 설정되었습니다.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), "금액 추가 실패!", Toast.LENGTH_SHORT).show();
            }
            dismiss();
        }

    }
}
