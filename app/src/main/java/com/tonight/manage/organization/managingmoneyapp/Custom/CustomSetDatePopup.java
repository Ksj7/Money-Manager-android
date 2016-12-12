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
import android.widget.CalendarView;
import android.widget.Toast;

import com.tonight.manage.organization.managingmoneyapp.EventInfoActivity;
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

public class CustomSetDatePopup extends DialogFragment {

    int year, month, date;
    private boolean isSuccess;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, R.style.CustomDialogTheme);
    }

    public static CustomSetDatePopup newInstance(String eventnum) {
        CustomSetDatePopup setDatePopup = new CustomSetDatePopup();
        Bundle b = new Bundle();
        b.putString("eventnum", eventnum);
        setDatePopup.setArguments(b);
        return setDatePopup;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.popup_set_date, container, false);

        Bundle b = getArguments();
        if (b == null) {
            Toast.makeText(getActivity(), "에러발생", Toast.LENGTH_SHORT).show();
            dismiss();
        }

        final String eventnum = b.getString("eventnum");
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        CalendarView calendarView = (CalendarView) view.findViewById(R.id.calendar);
        Button positiveButton = (Button) view.findViewById(R.id.confirmBtn);
        Button negativeButton = (Button) view.findViewById(R.id.negativeBtn);
        java.util.Calendar curDate = java.util.Calendar.getInstance();
        curDate.setTimeInMillis(calendarView.getDate());
        year = curDate.get(java.util.Calendar.YEAR);
        month = 1 + curDate.get(java.util.Calendar.MONTH);
        date = curDate.get(java.util.Calendar.DATE);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView calendarView, int i, int i1, int i2) {
                java.util.Calendar curDate = java.util.Calendar.getInstance();
                curDate.setTimeInMillis(calendarView.getDate());

                year = curDate.get(java.util.Calendar.YEAR);
                month = 1 + curDate.get(java.util.Calendar.MONTH);
                date = curDate.get(java.util.Calendar.DATE);

                Toast.makeText(getActivity(), "선택한 날짜 : " + year + "/" + month + "/" + date, Toast.LENGTH_SHORT).show();
            }
        });

        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String dateFormat = Integer.toString(year) + Integer.toString(month) +Integer.toString(date);
                new UpdateSetDateAsyncTask().execute(eventnum, dateFormat);
                EventInfoActivity.pagerInstance.notifyDataSetChanged();
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

    //Update SetDate Data
    class UpdateSetDateAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String requestURL = "";
            Response response = null;
            try {
                requestURL = NetworkDefineConstant.SERVER_URL_EVENT_INFO;

                OkHttpClient toServer = NetworkDefineConstant.getOkHttpClient();
                FormBody.Builder builder = new FormBody.Builder();
                builder.add("signal", "3")
                        .add("eventnum", params[0])
                        .add("eventdate", params[1]);
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
                    Log.e("에러", "기한 설정 에러");
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
                Toast.makeText(getActivity(), "기한이 설정되었습니다.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), "에러!", Toast.LENGTH_SHORT).show();
            }
            dismiss();
        }

    }
}
