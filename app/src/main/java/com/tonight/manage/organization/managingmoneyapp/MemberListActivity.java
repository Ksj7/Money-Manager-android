package com.tonight.manage.organization.managingmoneyapp;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.tonight.manage.organization.managingmoneyapp.Custom.CustomProfilePopup;
import com.tonight.manage.organization.managingmoneyapp.Object.MemberList;
import com.tonight.manage.organization.managingmoneyapp.Object.MemberListItem;

import java.util.ArrayList;

/**
 * Created by hooo5 on 2016-11-07.
 */

public class MemberListActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private RecyclerView mMemeberListRecyclerView;
    private MemberAdapter mMemberAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.member_main);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout)findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView)findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mMemeberListRecyclerView = (RecyclerView) findViewById(R.id.memberListRecyclerView);
        mMemeberListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mMemeberListRecyclerView.setHasFixedSize(true);
        mMemberAdapter = new MemberAdapter(this);
        mMemeberListRecyclerView.setAdapter(mMemberAdapter);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_setting) {
            // Handle the camera action
        } else if (id == R.id.nav_alarm_list) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    class MemberAdapter extends RecyclerView.Adapter<MemberListActivity.MemberAdapter.ViewHolder> {


        private LayoutInflater mLayoutInflater;
        private ArrayList<MemberListItem> memberDatas;
        private Context mContext;

        public MemberAdapter(Context context) {
            mContext = context;
            mLayoutInflater = LayoutInflater.from(context);
            memberDatas = new ArrayList<MemberListItem>();
        }

        public void addItem(ArrayList<MemberListItem> datas) {
            this.memberDatas = datas;
        }


        @Override
        public MemberListActivity.MemberAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MemberListActivity.MemberAdapter.ViewHolder(mLayoutInflater.inflate(R.layout.member_list_item, parent, false));
        }

        @Override
        public void onBindViewHolder(final MemberListActivity.MemberAdapter.ViewHolder holder, int position) {
            //test
            holder.personName.setText("test text");
            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CustomProfilePopup customProfilePopup = CustomProfilePopup.newInstance(memberDatas);
                    customProfilePopup.show(getSupportFragmentManager(), "profile");
                }
            });

        }

        @Override
        public int getItemCount() {
            //test
            return 20;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            View view;
            TextView personName;
            RecyclerView recyclerView;
            ImageView profileImageView;

            public ViewHolder(View v) {
                super(v);
                view = v;
                personName = (TextView) v.findViewById(R.id.memberListPersonName);
                recyclerView = (RecyclerView) v.findViewById(R.id.memberListRecyclerView);
                profileImageView = (ImageView) v.findViewById(R.id.memberListProfileImageView);
            }
        }
    }

    public class LoadMemberListAsyncTask extends AsyncTask<String, Integer, MemberList> {
        @Override
        protected MemberList doInBackground(String... args) {
            return null;
        }

        @Override
        protected void onPostExecute(MemberList result) {

            if (result != null && result.data.size() > 0) {

                mMemberAdapter.addItem(result.data);
                mMemberAdapter.notifyDataSetChanged();
            }
        }
    }
}
