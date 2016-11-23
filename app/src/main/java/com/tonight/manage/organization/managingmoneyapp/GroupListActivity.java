package com.tonight.manage.organization.managingmoneyapp;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;
import com.tonight.manage.organization.managingmoneyapp.Custom.CustomAddMoneyPopup;
import com.tonight.manage.organization.managingmoneyapp.Custom.CustomCreateGroupPopup;
import com.tonight.manage.organization.managingmoneyapp.Custom.CustomEntrancePopup;
import com.tonight.manage.organization.managingmoneyapp.Object.GroupList;
import com.tonight.manage.organization.managingmoneyapp.Object.GroupListItem;

import java.util.ArrayList;

/**
 * Created by sujinKim on 2016-11-04.
 */
public class GroupListActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView mGroupListRecyclerView;
    private GroupAdapter mGroupListAdapter;
    private SwipeRefreshLayout mGroupListSwipeRefreshLayout;
    private FloatingActionButton mCreateGroupFab;
    private FloatingActionButton mEnterGrouopFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mCreateGroupFab = (FloatingActionButton) findViewById(R.id.creatGroupFab);
        mEnterGrouopFab = (FloatingActionButton) findViewById(R.id.enterGroupFab);

        mCreateGroupFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomCreateGroupPopup createGroupPopup = CustomCreateGroupPopup.newInstance();
                createGroupPopup.show(getSupportFragmentManager(),"create_group");

            }
        });
        mEnterGrouopFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomEntrancePopup entrancePopup = CustomEntrancePopup.newInstance();
                entrancePopup.show(getSupportFragmentManager(),"entrance_group");
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);



        mGroupListRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mGroupListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mGroupListRecyclerView.setHasFixedSize(true);
        mGroupListAdapter = new GroupAdapter(this);
        mGroupListRecyclerView.setAdapter(mGroupListAdapter);


        mGroupListSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        mGroupListSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                mGroupListSwipeRefreshLayout.setRefreshing(false);
            }
        });

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_member_list) {
            /*TEST*/
            CustomAddMoneyPopup customAddMoneyPopup = CustomAddMoneyPopup.newInstance();
            customAddMoneyPopup.show(getSupportFragmentManager(), "add_money");
        }

        return super.onOptionsItemSelected(item);
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

    class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.ViewHolder> {


        private LayoutInflater mLayoutInflater;
        private ArrayList<GroupListItem> groupDatas; // group list
        private Context mContext;

        public GroupAdapter(Context context) {
            mContext = context;
            mLayoutInflater = LayoutInflater.from(context);
            groupDatas = new ArrayList<GroupListItem>();
        }

        public void addItem(ArrayList<GroupListItem> datas) {
            this.groupDatas = datas;
        }


        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            return new ViewHolder(mLayoutInflater.inflate(R.layout.group_list_item, parent, false));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {

            //test
            holder.groupName.setText("test text");
            holder.groupNumber.setText("88K");
            holder.view.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    Intent i = new Intent(GroupListActivity.this,EventListActivity.class);
                    startActivity(i);
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
            // return groupDatas.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView groupName;
            TextView groupNumber ;
            RecyclerView recyclerView;
            View view;
            public ViewHolder(View v) {
                super(v);
                view = v;
                groupName = (TextView) v.findViewById(R.id.groupName);
                groupNumber = (TextView) v.findViewById(R.id.groupNumber);
                recyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);
            }
        }
    }

    //group list 가져오기 위한 Thread
    public class LoadGroupListAsyncTask extends AsyncTask<String, Integer, GroupList> {
        @Override
        protected GroupList doInBackground(String... args) {
            return null;
        }

        @Override
        protected void onPostExecute(GroupList result) {

            // RecyclerView Adapter Item 값 추가
            if (result != null && result.data.size() > 0) {

                mGroupListAdapter.addItem(result.data);
                mGroupListAdapter.notifyDataSetChanged();
            }
        }
    }
}
