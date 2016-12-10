package com.tonight.manage.organization.managingmoneyapp.Custom;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.tonight.manage.organization.managingmoneyapp.GroupListActivity;
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

public class CustomCreateGroupPopup extends DialogFragment {
    String groupname;
    String account;
    String bank;
    String balance;
    String id;

    boolean isSuccess;
    EditText roomCodeEditText;
    EditText accountEditText;
    Spinner bankSpinner;
    EditText balanceEditText;
    private ArrayAdapter<String> mSpinnerAdapter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, R.style.CustomDialogTheme);
    }

    public static CustomCreateGroupPopup newInstance(String id) {
        CustomCreateGroupPopup groupPopup = new CustomCreateGroupPopup();
        Bundle b = new Bundle();
        b.putString("id", id);
        groupPopup.setArguments(b);
        return groupPopup;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.popup_create_group, container, false);
        Bundle b = getArguments();
        if (b == null) {
            Toast.makeText(getActivity(), "오류가 발생하였습니다.", Toast.LENGTH_SHORT).show();
            return view;
        }

        id = b.getString("id");

        Button Ybtn = (Button) view.findViewById(R.id.confirmBtn);
        Button NBtn = (Button) view.findViewById(R.id.negativeBtn);
        roomCodeEditText = (EditText) view.findViewById(R.id.roomCodeEdit);
        accountEditText = (EditText) view.findViewById(R.id.accountEdit);
        bankSpinner = (Spinner) view.findViewById(R.id.bankSpinner);
        balanceEditText = (EditText) view.findViewById(R.id.balanceEdit);

        mSpinnerAdapter= new ArrayAdapter<>(getContext(), R.layout.spinner_text, getResources().getStringArray(R.array.bank) );
        mSpinnerAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown);
        bankSpinner.setAdapter(mSpinnerAdapter);
        bankSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                bank =  (String) bankSpinner.getSelectedItem();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });


        Ybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                groupname = roomCodeEditText.getText().toString();
                account = accountEditText.getText().toString();
                balance = balanceEditText.getText().toString();
                new CreateGroupAsyncTask().execute(id, groupname, bank, account, balance);

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

    private void clearSetting() {
        roomCodeEditText.setText("");
        accountEditText.setText("");
        balanceEditText.setText("");
    }

    @Override
    public void onStop() {
        super.onStop();
        dismiss();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        int width = getResources().getDimensionPixelSize(R.dimen.popup_create_group_width);
        int height = getResources().getDimensionPixelSize(R.dimen.popup_create_group_height);
        getDialog().getWindow().setLayout(width, height);
    }

    public class CreateGroupAsyncTask extends AsyncTask<String, Void, Void> {


        @Override
        protected Void doInBackground(String... params) {
            String requestURL = "";
            Response response = null;
            try {
                requestURL = NetworkDefineConstant.SERVER_URL_GROUP_LIST;

                OkHttpClient toServer = NetworkDefineConstant.getOkHttpClient();
                FormBody.Builder builder = new FormBody.Builder();
                builder.add("signal", "2")
                        .add("userid", params[0])
                        .add("groupname", params[1])
                        .add("bank", params[2])
                        .add("account", params[3])
                        .add("balance", params[4]);

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
                    if (valid.contains("0"))
                        isSuccess = true;
                    else
                        isSuccess = false;

                } else {
                    Log.e("에러", "그룹 개설 실패");
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
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (isSuccess) {
                Toast.makeText(getActivity(), "그룹이 개설되었습니다.", Toast.LENGTH_SHORT).show();
                GroupListActivity activity = (GroupListActivity) getActivity();
                activity.isSuccessCreateGroup(true);
                dismiss();
            } else {
                Toast.makeText(getActivity(), "그룹 생성 실패", Toast.LENGTH_SHORT).show();
                clearSetting();
                return;
            }
        }

    }
}
