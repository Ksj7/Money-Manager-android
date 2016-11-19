package com.tonight.manage.organization.managingmoneyapp;

import com.tonight.manage.organization.managingmoneyapp.Toss.TossActivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by 3 on 2016-11-14.
 */

public class PaymentFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        View v =  inflater.inflate(R.layout.activity_event_info_payment, container, false);
        v.findViewById(R.id.eventInfo_userPayState).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), TossActivity.class);
                startActivity(i);
            }
        });
        return v;
    }

}