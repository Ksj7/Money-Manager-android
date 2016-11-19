package com.tonight.manage.organization.managingmoneyapp;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tonight.manage.organization.managingmoneyapp.Object.EventInfoUsageListItem;

import java.util.ArrayList;

/**
 * Created by 3 on 2016-11-14.
 */

public class UserFragment extends Fragment{

    private RecyclerView mUsageListRecyclerView;
    private EventInfoUsageAdapter mUsageListAdapter;
    private SwipeRefreshLayout mUsageListSwipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        View v= inflater.inflate(R.layout.activity_event_info_user, container, false);

        mUsageListRecyclerView = (RecyclerView) v.findViewById(R.id.eventInfo_user_recyclerView);
        mUsageListRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mUsageListRecyclerView.setHasFixedSize(true);
        mUsageListAdapter = new EventInfoUsageAdapter(getActivity());
        mUsageListRecyclerView.setAdapter(mUsageListAdapter);


        mUsageListSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.eventInfo_user_swipeRefreshLayout);
        mUsageListSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                mUsageListSwipeRefreshLayout.setRefreshing(false);
            }
        });


        return v;

    }



    class EventInfoUsageAdapter extends RecyclerView.Adapter<EventInfoUsageAdapter.ViewHolder> {

        private LayoutInflater mLayoutInflater;
        private ArrayList<EventInfoUsageListItem> usageArrayList; // group list
        private Context mContext;

        public EventInfoUsageAdapter(Context context) {
            mContext = context;
            mLayoutInflater = LayoutInflater.from(context);
            usageArrayList = new ArrayList<EventInfoUsageListItem>();
        }

        public void addItem(ArrayList<EventInfoUsageListItem> datas) {
            this. usageArrayList = datas;
        }


        @Override
        public EventInfoUsageAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            return new EventInfoUsageAdapter.ViewHolder(mLayoutInflater.inflate(R.layout.activity_event_info_user_item, parent, false));
        }

        @Override
        public void onBindViewHolder(EventInfoUsageAdapter.ViewHolder holder, int position) {

            //test
            holder.date.setText("16.11.04");
            holder.location.setText("회식");
            holder.usedmoney.setText("20000원");
            holder.view.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    //Intent i = new Intent(EventInfoActivity.this,EventListActivity.class);
                    //startActivity(i);
                }
            });
            //이게 정상
            //holder.groupName.setText(groupDatas.get(position).groupName);
            //holder.groupNumber.setText(groupDatas.get(position).groupNumber+"명");
        }

        @Override
        public int getItemCount() {
            //test
            return 3;

            //이게 원래 정상
            // return usageArrayList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView date;
            TextView location ;
            TextView usedmoney;
            RecyclerView recyclerView;
            View view;
            public ViewHolder(View v) {
                super(v);
                view = v;
                date = (TextView) v.findViewById(R.id.eventInfo_usage_date);
                location = (TextView) v.findViewById(R.id.eventInfo_usage_Location);
                usedmoney = (TextView) v.findViewById(R.id.eventInfo_usage_usedMoney);
                recyclerView = (RecyclerView) v.findViewById(R.id.eventInfo_user_recyclerView);

            }
        }
    }


}
