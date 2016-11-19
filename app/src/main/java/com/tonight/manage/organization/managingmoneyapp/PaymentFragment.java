package com.tonight.manage.organization.managingmoneyapp;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.tonight.manage.organization.managingmoneyapp.Object.EventInfoMemberPaymentListItem;

import java.util.ArrayList;

/**
 * Created by 3 on 2016-11-14.
 */

public class PaymentFragment extends Fragment {

    private RecyclerView mPaymentListRecyclerView;
    private EventInfoPaymentAdapter mPaymentListAdapter;
    private SwipeRefreshLayout mPaymentListSwipeRefreshLayout;
    // private ScrollView mPaymentListScrollView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.activity_event_info_payment, container, false);


        mPaymentListRecyclerView = (RecyclerView) v.findViewById(R.id.eventInfo_recyclerView);
        mPaymentListRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mPaymentListRecyclerView.setNestedScrollingEnabled(true);//scrollview를 위해
        // mPaymentListRecyclerView.setLayoutManager(new WrappingLinearLayoutManager(getContext()));//
        //mPaymentListRecyclerView.setHasFixedSize(false);

        mPaymentListRecyclerView.setHasFixedSize(true);
        mPaymentListAdapter = new EventInfoPaymentAdapter(getActivity());
        mPaymentListRecyclerView.setAdapter(mPaymentListAdapter);

      /*  mPaymentListRecyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                mPaymentListScrollView.requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });*/

        mPaymentListSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.eventInfo_payment_swipeRefreshLayout);
        mPaymentListSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                mPaymentListSwipeRefreshLayout.setRefreshing(false);
            }
        });


        return v;
    }



    class EventInfoPaymentAdapter extends RecyclerView.Adapter<EventInfoPaymentAdapter.ViewHolder> {

        private LayoutInflater mLayoutInflater;
        private ArrayList<EventInfoMemberPaymentListItem> paymentArrayList; // group list
        private Context mContext;

        public EventInfoPaymentAdapter(Context context) {
            mContext = context;
            mLayoutInflater = LayoutInflater.from(context);
            paymentArrayList = new ArrayList<EventInfoMemberPaymentListItem>();
        }

        public void addItem(ArrayList<EventInfoMemberPaymentListItem> datas) {
            this. paymentArrayList = datas;
        }


        @Override
        public EventInfoPaymentAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            return new EventInfoPaymentAdapter.ViewHolder(mLayoutInflater.inflate(R.layout.activity_event_info_payment_listitem, parent, false));
        }

        @Override
        public void onBindViewHolder(EventInfoPaymentAdapter.ViewHolder holder, int position) {

            //test
            holder.useName.setText("test text");
            holder.paymentMoney.setText("0");
            holder.payStatus.setText("미지출");
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
            return 5;

            //이게 원래 정상
            // return paymentArrayList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView useName;
            TextView paymentMoney ;
            TextView payStatus;
            RecyclerView recyclerView;
            View view;
            public ViewHolder(View v) {
                super(v);
                view = v;
                useName = (TextView) v.findViewById(R.id.eventInfo_payment_username);
                paymentMoney = (TextView) v.findViewById(R.id.eventInfo_payment_money);
                payStatus = (TextView) v.findViewById(R.id.eventInfo_payment_payState);
                recyclerView = (RecyclerView) v.findViewById(R.id.eventInfo_recyclerView);

            }
        }
    }


    public class WrappingLinearLayoutManager extends LinearLayoutManager
    {

        public WrappingLinearLayoutManager(Context context) {
            super(context);
        }

        private int[] mMeasuredDimension = new int[2];

        @Override
        public boolean canScrollVertically() {
            return false;
        }

        @Override
        public void onMeasure(RecyclerView.Recycler recycler, RecyclerView.State state,
                              int widthSpec, int heightSpec) {
            final int widthMode = View.MeasureSpec.getMode(widthSpec);
            final int heightMode = View.MeasureSpec.getMode(heightSpec);

            final int widthSize = View.MeasureSpec.getSize(widthSpec);
            final int heightSize = View.MeasureSpec.getSize(heightSpec);

            int width = 0;
            int height = 0;
            for (int i = 0; i < getItemCount(); i++) {
                if (getOrientation() == HORIZONTAL) {
                    measureScrapChild(recycler, i,
                            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                            heightSpec,
                            mMeasuredDimension);

                    width = width + mMeasuredDimension[0];
                    if (i == 0) {
                        height = mMeasuredDimension[1];
                    }
                } else {
                    measureScrapChild(recycler, i,
                            widthSpec,
                            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                            mMeasuredDimension);

                    height = height + mMeasuredDimension[1];
                    if (i == 0) {
                        width = mMeasuredDimension[0];
                    }
                }
            }

            switch (widthMode) {
                case View.MeasureSpec.EXACTLY:
                    width = widthSize;
                case View.MeasureSpec.AT_MOST:
                case View.MeasureSpec.UNSPECIFIED:
            }

            switch (heightMode) {
                case View.MeasureSpec.EXACTLY:
                    height = heightSize;
                case View.MeasureSpec.AT_MOST:
                case View.MeasureSpec.UNSPECIFIED:
            }

            setMeasuredDimension(width, height);
        }

        private void measureScrapChild(RecyclerView.Recycler recycler, int position, int widthSpec,
                                       int heightSpec, int[] measuredDimension) {

            View view = recycler.getViewForPosition(position);
            if (view.getVisibility() == View.GONE) {
                measuredDimension[0] = 0;
                measuredDimension[1] = 0;
                return;
            }
            // For adding Item Decor Insets to view
            super.measureChildWithMargins(view, 0, 0);
            RecyclerView.LayoutParams p = (RecyclerView.LayoutParams) view.getLayoutParams();
            int childWidthSpec = ViewGroup.getChildMeasureSpec(
                    widthSpec,
                    getPaddingLeft() + getPaddingRight() + getDecoratedLeft(view) + getDecoratedRight(view),
                    p.width);
            int childHeightSpec = ViewGroup.getChildMeasureSpec(
                    heightSpec,
                    getPaddingTop() + getPaddingBottom() + getDecoratedTop(view) + getDecoratedBottom(view),
                    p.height);
            view.measure(childWidthSpec, childHeightSpec);

            // Get decorated measurements
            measuredDimension[0] = getDecoratedMeasuredWidth(view) + p.leftMargin + p.rightMargin;
            measuredDimension[1] = getDecoratedMeasuredHeight(view) + p.bottomMargin + p.topMargin;
            recycler.recycleView(view);
        }
    }


}