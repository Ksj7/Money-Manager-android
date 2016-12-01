package com.tonight.manage.organization.managingmoneyapp.Custom;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.tonight.manage.organization.managingmoneyapp.R;

/**
 * Created by sujinKim on 2016-11-04.
 */

public class CustomCreateGroupPopup extends DialogFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE,R.style.CustomDialogTheme);
    }

    public static CustomCreateGroupPopup newInstance() {
        CustomCreateGroupPopup EntrancePopup = new CustomCreateGroupPopup();
        return EntrancePopup;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.popup_create_group,container,false);
        Button Ybtn = (Button) view.findViewById(R.id.confirmBtn);
        Button NBtn = (Button) view.findViewById(R.id.negativeBtn);
        Ybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "그룹에 개설되었습니다.", Toast.LENGTH_SHORT).show();
                //여기서 다시 GroupList 로드해야됨!
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
        int width = getResources().getDimensionPixelSize(R.dimen.popup_create_group_width);
        int height = getResources().getDimensionPixelSize(R.dimen.popup_create_group_height);
        getDialog().getWindow().setLayout(width,height);
    }
}
