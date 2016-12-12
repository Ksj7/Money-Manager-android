package com.tonight.manage.organization.managingmoneyapp.Custom;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.tonight.manage.organization.managingmoneyapp.R;

/**
 * Created by sujinKim on 2016-11-04.
 */

public class CustomInfoGroupPopup extends DialogFragment {
    String groupname;
    String account;
    String bank;

    TextView accountText;
    TextView bankText;
    TextView groupNameText;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, R.style.CustomDialogTheme);
    }

    public static CustomInfoGroupPopup newInstance(String groupname, String account, String bank) {
        CustomInfoGroupPopup infoGroupPopup = new CustomInfoGroupPopup();
        Bundle b = new Bundle();
        b.putString("groupname",groupname);
        b.putString("account",account);
        b.putString("bank",bank);
        infoGroupPopup.setArguments(b);
        return infoGroupPopup;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.popup_info_group, container, false);
        Bundle b = getArguments();
        if (b == null) {
            Toast.makeText(getActivity(), "오류가 발생하였습니다.", Toast.LENGTH_SHORT).show();
            return view;
        }

        groupname = b.getString("groupname");
        account = b.getString("account");
        bank = b.getString("bank");

        Button Ybtn = (Button) view.findViewById(R.id.confirmBtn);
        accountText = (TextView) view.findViewById(R.id.accountText);
        bankText = (TextView) view.findViewById(R.id.bankNameText);
        groupNameText = (TextView) view.findViewById(R.id.groupNameText);

        accountText.setText(account);
        bankText.setText(bank);
        groupNameText.setText(groupname);
        Ybtn.setOnClickListener(new View.OnClickListener() {
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
        int height = getResources().getDimensionPixelSize(R.dimen.popup_info_group_height);
        getDialog().getWindow().setLayout(width, height);
    }
}
