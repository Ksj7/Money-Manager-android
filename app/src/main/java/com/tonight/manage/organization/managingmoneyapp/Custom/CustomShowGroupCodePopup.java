package com.tonight.manage.organization.managingmoneyapp.Custom;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.tonight.manage.organization.managingmoneyapp.R;

/**
 * Created by sujinKim on 2016-11-04.
 */

public class CustomShowGroupCodePopup extends DialogFragment {

    private View view;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, R.style.CustomDialogTheme);
    }

    public static CustomShowGroupCodePopup newInstance(String groupcode) {
        CustomShowGroupCodePopup showGroupCodePopup = new CustomShowGroupCodePopup();
        Bundle b = new Bundle();
        b.putString("groupcode",groupcode);
        showGroupCodePopup.setArguments(b);
        return showGroupCodePopup;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.popup_show_group_code, container, false);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        Bundle b = getArguments();
        if(b==null){
            Toast.makeText(getActivity(), "에러가 발생했습니다", Toast.LENGTH_SHORT).show();
            return view;
        }
        final String groupcode = b.getString("groupcode");
        TextView groupText = (TextView) view.findViewById(R.id.groupText);
        groupText.setText(groupcode);
        groupText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // MyApplication.setClipBoardLink(getContext(),groupcode);
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
        int width = getResources().getDimensionPixelSize(R.dimen.popup_group_width);
        int height = getResources().getDimensionPixelSize(R.dimen.popup_group_height);
        getDialog().getWindow().setLayout(width, height);
    }

}