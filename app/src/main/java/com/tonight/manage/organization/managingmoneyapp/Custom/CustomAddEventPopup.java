package com.tonight.manage.organization.managingmoneyapp.Custom;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.tonight.manage.organization.managingmoneyapp.EventListActivity;
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

public class CustomAddEventPopup extends DialogFragment {

    private View view;
    private Button positiveButton, negativeButton;
    private boolean isSuccess;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, R.style.CustomDialogTheme);
    }

    public static CustomAddEventPopup newInstance(String id, String groupcode) {
        CustomAddEventPopup eventPopup = new CustomAddEventPopup();
        Bundle b = new Bundle();
        b.putString("id",id);
        b.putString("groupcode",groupcode);
        eventPopup.setArguments(b);
        return eventPopup;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.popup_add_event, container, false);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        Bundle b = getArguments();
        if(b==null){
            Toast.makeText(getActivity(), "에러가 발생했습니다", Toast.LENGTH_SHORT).show();
            return view;
        }
        final String groupcode = b.getString("groupcode");
        final String id = b.getString("id");
        positiveButton = (Button) view.findViewById(R.id.confirmBtn);
        negativeButton = (Button ) view.findViewById(R.id.negativeBtn);
        final EditText eventNameEdit = (EditText) view.findViewById(R.id.eventNameEdit);
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String eventName = eventNameEdit.getText().toString();
                new UpdateAddEventAsyncTask().execute(id,groupcode,eventName);
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

    //Update Add event Data
    class UpdateAddEventAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String requestURL = "";
            Response response = null;
            try {
                requestURL = NetworkDefineConstant.SERVER_URL_EVENT_LIST;

                OkHttpClient toServer = NetworkDefineConstant.getOkHttpClient();
                FormBody.Builder builder = new FormBody.Builder();
                builder.add("signal", "1")
                        .add("userid", params[0])
                        .add("groupcode", params[1])
                        .add("eventname",params[2]);

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
                    Log.e("에러", "이벤트 생성 실패!");
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
            if (isSuccess) {
                Toast.makeText(getActivity(), "이벤트가 추가 되었습니다.", Toast.LENGTH_SHORT).show();
                EventListActivity activity = (EventListActivity) getActivity();
                activity.isSuccessCreateEvent(true);
            } else {
                Toast.makeText(getActivity(), "오류가 발생했습니다. 잠시 후 다시 시도해 주세요", Toast.LENGTH_SHORT).show();
            }
            dismiss();
        }

    }

}
