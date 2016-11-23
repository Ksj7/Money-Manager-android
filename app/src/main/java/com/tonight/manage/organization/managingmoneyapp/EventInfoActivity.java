package com.tonight.manage.organization.managingmoneyapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;

/**
 * Created by 3 on 2016-11-14.
 */

public class EventInfoActivity extends AppCompatActivity implements TabLayout.OnTabSelectedListener, ViewPager.OnPageChangeListener {

    private TabLayout tabLayout;
    private ViewPager mViewPager;
    private ImageButton mInvitationButton;

    private RecyclerView mPaymentListRecyclerView;
    //    private EventInfoPaymentAdapter mPaymentListAdapter;
    private SwipeRefreshLayout mPaymentListSwipeRefreshLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_info);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tabLayout = (TabLayout) findViewById(R.id.tabLayout);

        tabLayout.addTab(tabLayout.newTab().setText("납부내역"));
        tabLayout.addTab(tabLayout.newTab().setText("사용내역"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        mViewPager = (ViewPager) findViewById(R.id.pager);
        Pager adapter = new Pager(getSupportFragmentManager(), tabLayout.getTabCount());
        mViewPager.setAdapter(adapter);
        tabLayout.setOnTabSelectedListener(this);//Tab 선택 시 화면 이동하는 리스너
        mViewPager.addOnPageChangeListener(this);//Tab swiping시 화면 이동하는 리스너

        mInvitationButton = (ImageButton) findViewById(R.id.invitationBtn);
        mInvitationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(EventInfoActivity.this,InvitationActivity.class);
                startActivity(i);
            }
        });
    }


    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
//actionBar.setSelectedNavigationItem(postion);
        tabLayout.setScrollPosition(position,0,true);
        tabLayout.setSelected(true);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

}
