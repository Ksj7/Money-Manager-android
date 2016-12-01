package com.tonight.manage.organization.managingmoneyapp.Custom;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.SwitchCompat;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;

import com.tonight.manage.organization.managingmoneyapp.R;

/**
 * Created by sujinKim on 2016-11-04.
 */

public class CustomAddMoneyPopup extends DialogFragment {

    private View view;
    private boolean isPortion;
    private boolean isSum;
    private int width,height;
    RelativeLayout wrapLayout;
    private int wrapHeight;
    private Button positiveButton, negativeButton;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, R.style.CustomDialogTheme);
        initWindow();
    }

    public static CustomAddMoneyPopup newInstance() {
        CustomAddMoneyPopup togglePopup = new CustomAddMoneyPopup();
        return togglePopup;
    }




    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.popup_add_money, container, false);


        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        final TextInputLayout portionLayout = (TextInputLayout) view.findViewById(R.id.portionText);
        final TextInputLayout sumLayout = (TextInputLayout) view.findViewById(R.id.sumText);

        wrapLayout = (RelativeLayout) view.findViewById(R.id.wrapLayout);

        positiveButton = (Button) view.findViewById(R.id.confirmBtn);
        negativeButton = (Button ) view.findViewById(R.id.negativeBtn);

        portionLayout.setVisibility(View.GONE);
        sumLayout.setVisibility(View.GONE);

        final SwitchCompat switchButton = (SwitchCompat) view.findViewById(R.id.portionSwitch);
        final SwitchCompat switchButton2 = (SwitchCompat) view.findViewById(R.id.sumSwitch);


        switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isPortion = isChecked;
                if (isChecked) {
                    if (isSum) {
                        switchButton2.setChecked(false);
                        sumLayout.setVisibility(View.GONE);
                    }
                    portionLayout.setVisibility(View.VISIBLE);
                    wrapLayout.setMinimumHeight(wrapHeight+100);
                    wrapLayout.invalidate();
                } else {
                    portionLayout.setVisibility(View.GONE);
                    wrapLayout.setMinimumHeight(wrapHeight-100);
                    wrapLayout.invalidate();

                }
            }
        });

        switchButton2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isSum = isChecked;
                if (isChecked) {
                    if (isPortion) {
                        switchButton.setChecked(false);
                        portionLayout.setVisibility(View.GONE);
                    }
                    sumLayout.setVisibility(View.VISIBLE);
                    wrapLayout.setMinimumHeight(wrapHeight+100);
                    wrapLayout.invalidate();
                } else {
                    sumLayout.setVisibility(View.GONE);
                    wrapLayout.setMinimumHeight(wrapHeight-100);
                    wrapLayout.invalidate();
                }
            }
        });

        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // UpdateAddMoneyDataAsyncTask();
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

    public void initWindow(){
        width = getResources().getDimensionPixelSize(R.dimen.popup_width);
        height = getResources().getDimensionPixelSize(R.dimen.popup_height);
    }



    @Override
    public void onStop() {
        super.onStop();
        dismiss();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getDialog().getWindow().setLayout(width, height);
        wrapHeight = wrapLayout.getMeasuredHeight();
    }

    //Update Add Money Data
    class UpdateAddMoneyDataAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... userID) {

            return null;
        }

        @Override
        protected void onPostExecute(String s) {

        }
    }
}
