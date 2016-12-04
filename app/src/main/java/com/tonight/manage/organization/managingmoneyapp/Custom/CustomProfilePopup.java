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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.tonight.manage.organization.managingmoneyapp.Object.MemberListItem;
import com.tonight.manage.organization.managingmoneyapp.ProfileImageActivity;
import com.tonight.manage.organization.managingmoneyapp.R;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by sujinKim on 2016-11-23.
 */

public class CustomProfilePopup extends DialogFragment {


    private static final String ARG_PARAM1 = "profile" ;
    private MemberListItem profileDatas;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, R.style.CustomDialogTheme);
    }

    public static CustomProfilePopup newInstance(MemberListItem datas) {
        CustomProfilePopup profilePopup = new CustomProfilePopup();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, datas);
        profilePopup.setArguments(args);
        return profilePopup;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.popup_profile,container,false);

        if (getArguments() != null) {
            profileDatas = (MemberListItem) getArguments().getSerializable(ARG_PARAM1);
        }

        final Button Ybtn = (Button) view.findViewById(R.id.confirmBtn);
        TextView nameText = (TextView) view.findViewById(R.id.nameText);
        final Button phoneNumberButton = (Button) view.findViewById(R.id.phoneNumberBtn);
        final CircleImageView profileImageView = (CircleImageView) view.findViewById(R.id.profileImage);

        nameText.setText(profileDatas.getUsername());
        phoneNumberButton.setText(profileDatas.getPhone());

        Glide.with(getContext())
                .load(profileDatas.getProfileimg())
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .override(150, 150)
                .into(profileImageView);

        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), ProfileImageActivity.class);
                i.putExtra("profile",profileDatas.getProfileimg());
                startActivity(i);
            }
        });

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