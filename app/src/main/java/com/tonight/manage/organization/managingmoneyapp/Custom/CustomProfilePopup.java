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

import com.tonight.manage.organization.managingmoneyapp.R;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by sujinKim on 2016-11-23.
 */

public class CustomProfilePopup extends DialogFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, R.style.CustomDialogTheme);
    }

    public static CustomProfilePopup newInstance() {
        CustomProfilePopup profilePopup = new CustomProfilePopup();
        return profilePopup;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.popup_profile,container,false);
        Button Ybtn = (Button) view.findViewById(R.id.confirmBtn);
        final Button phoneNumberButton = (Button) view.findViewById(R.id.phoneNumberBtn);
        CircleImageView profileImageView = (CircleImageView) view.findViewById(R.id.profileImage);
        TextView nameText = (TextView) view.findViewById(R.id.nameText);


        //profileImageView와 nameText는 asynctask로 받아와야함.

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
    public void onStop() {
        super.onStop();
        dismiss();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        int width = getResources().getDimensionPixelSize(R.dimen.popup_profile_width);
        int height = getResources().getDimensionPixelSize(R.dimen.popup_profile_height);
        getDialog().getWindow().setLayout(width,height);
    }
}