package com.tonight.manage.organization.managingmoneyapp.Custom;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.Toast;

import com.tonight.manage.organization.managingmoneyapp.R;

/**
 * Created by sujinKim on 2016-11-04.
 */

public class CustomSetDatePopup extends DialogFragment {

    private View view;
    private CalendarView calendarView;
    int year, month, date;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, R.style.CustomDialogTheme);
    }

    public static CustomSetDatePopup newInstance() {
        CustomSetDatePopup setDatePopup = new CustomSetDatePopup();
        return setDatePopup;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.popup_set_date, container, false);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        calendarView = (CalendarView) view.findViewById(R.id.calendar);
        Button confirmButton = (Button) view.findViewById(R.id.confirmBtn);
        Button negativeButton = (Button) view.findViewById(R.id.negativeBtn);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView calendarView, int i, int i1, int i2) {
                java.util.Calendar curDate = java.util.Calendar.getInstance();
                curDate.setTimeInMillis(calendarView.getDate());

                year = curDate.get(java.util.Calendar.YEAR);
                month = 1 + curDate.get(java.util.Calendar.MONTH);
                date = curDate.get(java.util.Calendar.DATE);

                Toast.makeText(getActivity(),"선택한 날짜 : "+year + "/" + month +"/" + date,Toast.LENGTH_SHORT).show();
            }
        });

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        int width = getResources().getDimensionPixelSize(R.dimen.popup_calendar_width);
        int height = getResources().getDimensionPixelSize(R.dimen.popup_calendar_height);
        getDialog().getWindow().setLayout(width,height);
    }



    @Override
    public void onStop() {
        super.onStop();
        dismiss();
    }

    //Update SetDate Data
    class UpdateSetDateAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... userID) {

            return null;
        }

        @Override
        protected void onPostExecute(String s) {

        }
    }
}
