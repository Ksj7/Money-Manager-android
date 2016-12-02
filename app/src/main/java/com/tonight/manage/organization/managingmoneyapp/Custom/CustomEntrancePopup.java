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
import android.widget.EditText;
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

public class CustomEntrancePopup extends DialogFragment {

    private boolean isSuccess;
    private String id;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, R.style.CustomDialogTheme);
    }

    public static CustomEntrancePopup newInstance(String id) {
        CustomEntrancePopup EntrancePopup = new CustomEntrancePopup();
        Bundle b = new Bundle();
        b.putString("id", id);
        EntrancePopup.setArguments(b);
        return EntrancePopup;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.popup_entrance_group, container, false);
        Bundle b = getArguments();
        if (b == null) {
            Toast.makeText(getActivity(), "오류가 발생하였습니다.", Toast.LENGTH_SHORT).show();
            return view;
        }

        id = b.getString("id");

        final EditText roomCodeEdit = (EditText) view.findViewById(R.id.roomCodeEdit);

        Button YBtn = (Button) view.findViewById(R.id.confirmBtn);
        Button NBtn = (Button) view.findViewById(R.id.negativeBtn);
        YBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String roomcode  = roomCodeEdit.getText().toString();
                new CustomEntranceAsyncTask().execute(id, roomcode);

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
        int width = getResources().getDimensionPixelSize(R.dimen.popup_entrance_group_width);
        int height = getResources().getDimensionPixelSize(R.dimen.popup_entrance_group_height);
        getDialog().getWindow().setLayout(width, height);
    }


    public class CustomEntranceAsyncTask extends AsyncTask<String, Void, Void> {


        @Override
        protected Void doInBackground(String... params) {
            String requestURL = "";
            Response response = null;
            try {
                requestURL = NetworkDefineConstant.SERVER_URL_GROUP_LIST;

                OkHttpClient toServer = NetworkDefineConstant.getOkHttpClient();
                FormBody.Builder builder = new FormBody.Builder();
                builder.add("signal", "1")
                        .add("userid", params[0])
                        .add("groupcode", params[1]);

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
                        isSuccess = false;
                    else
                        isSuccess = true;

                } else {
                    Log.e("에러", "그룹 입장 실패");
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
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (isSuccess) {
                Toast.makeText(getActivity(), "그룹이 추가 되었습니다.", Toast.LENGTH_SHORT).show();
                GroupListActivity activity = (GroupListActivity) getActivity();
                activity.isSuccessCreateGroup(true);
            } else {
                Toast.makeText(getActivity(), "이미 속해 있는 그룹입니다", Toast.LENGTH_SHORT).show();
            }

            dismiss();
        }

    }


}
