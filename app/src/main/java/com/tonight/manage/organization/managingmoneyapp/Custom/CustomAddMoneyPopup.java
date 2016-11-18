package com.tonight.manage.organization.managingmoneyapp.Custom;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.SwitchCompat;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.tonight.manage.organization.managingmoneyapp.R;

/**
 * Created by sujinKim on 2016-11-04.
 */

public class CustomAddMoneyPopup extends DialogFragment {

    private View view;
    private boolean isPortion;
    private boolean isSum;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, R.style.CustomDialogTheme);
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
        final ToggleExpandLayout portionToggle = (ToggleExpandLayout) view.findViewById(R.id.portionToggle);
        final ToggleExpandLayout sumToggle = (ToggleExpandLayout) view.findViewById(R.id.sumToggle);

        final SwitchCompat switchButton = (SwitchCompat) view.findViewById(R.id.portionSwitch);
        final SwitchCompat switchButton2 = (SwitchCompat) view.findViewById(R.id.sumSwitch);


        portionToggle.setOnToggleTouchListener(new ToggleExpandLayout.OnToggleTouchListener() {
            @Override
            public void onStartOpen(int height, int originalHeight) {
            }

            @Override
            public void onOpen() {
                int childCount = portionToggle.getChildCount();
                for (int i = 0; i < childCount; i++) {
                    View view = portionToggle.getChildAt(i);
                }
            }

            @Override
            public void onStartClose(int height, int originalHeight) {

            }

            @Override
            public void onClosed() {

            }
        });

        switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isPortion = isChecked;
                if (isChecked) {
                    if (isSum) {
                        switchButton2.setChecked(false);
                        sumToggle.close();
                    }
                    portionToggle.open();
                } else {
                    portionToggle.close();
                }
            }
        });

        sumToggle.setOnToggleTouchListener(new ToggleExpandLayout.OnToggleTouchListener() {
            public void onStartOpen(int height, int originalHeight) {
            }

            @Override
            public void onOpen() {

            }

            @Override
            public void onStartClose(int height, int originalHeight) {

            }

            @Override
            public void onClosed() {

            }
        });

        switchButton2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isSum = isChecked;
                if (isChecked) {
                    if (isPortion) {
                        switchButton.setChecked(false);
                        portionToggle.close();
                    }
                    sumToggle.open();
                } else {
                    sumToggle.close();
                }
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
        int width = getResources().getDimensionPixelSize(R.dimen.popup_width);
        int height = getResources().getDimensionPixelSize(R.dimen.popup_height);
        getDialog().getWindow().setLayout(width, height);
    }
}
