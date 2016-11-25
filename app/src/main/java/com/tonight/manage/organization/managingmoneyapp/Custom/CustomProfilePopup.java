package com.tonight.manage.organization.managingmoneyapp.Custom;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.tonight.manage.organization.managingmoneyapp.ProfileImageActivity;
import com.tonight.manage.organization.managingmoneyapp.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by sujinKim on 2016-11-23.
 */

public class CustomProfilePopup extends DialogFragment {


    private static final String ARG_PARAM1 = "profile" ;
    private String name;
    private String imgUrl;
    private ArrayList profileDatas;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, R.style.CustomDialogTheme);
        if (getArguments() != null) {
            profileDatas = getArguments().getStringArrayList(ARG_PARAM1);
        }
    }

    public static CustomProfilePopup newInstance(ArrayList datas) {
        CustomProfilePopup profilePopup = new CustomProfilePopup();
        Bundle args = new Bundle();
        args.putStringArrayList(ARG_PARAM1, datas);
        profilePopup.setArguments(args);
        return profilePopup;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.popup_profile,container,false);
        final Button Ybtn = (Button) view.findViewById(R.id.confirmBtn);
        TextView nameText = (TextView) view.findViewById(R.id.nameText);
        final Button phoneNumberButton = (Button) view.findViewById(R.id.phoneNumberBtn);
        final CircleImageView profileImageView = (CircleImageView) view.findViewById(R.id.profileImage);

        //nameText.setText(profileDatas.get(0).toString());
        //phoneNumberButton.setText(profileDatas.get(1).toString());
        //IMPORTANT! profileDatas.get(2)는 imgURl에 대한 정보여야 함.
        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), ProfileImageActivity.class));
            }
        });

        //TODO profileImageView와 nameText는 asynctask로 받아와야함.


        Ybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }

        });
        phoneNumberButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+phoneNumberButton.getText()));
                startActivity(intent);
            }
        });

        return view;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        int width = getResources().getDimensionPixelSize(R.dimen.popup_profile_width);
        int height = getResources().getDimensionPixelSize(R.dimen.popup_profile_height);
        getDialog().getWindow().setLayout(width,height);
    }
}