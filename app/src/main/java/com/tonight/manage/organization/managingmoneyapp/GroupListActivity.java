package com.tonight.manage.organization.managingmoneyapp;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;
import com.tonight.manage.organization.managingmoneyapp.Custom.CustomCreateGroupPopup;
import com.tonight.manage.organization.managingmoneyapp.Custom.CustomEntrancePopup;
import com.tonight.manage.organization.managingmoneyapp.Object.GroupListItem;
import com.tonight.manage.organization.managingmoneyapp.Server.GroupJSONParsor;
import com.tonight.manage.organization.managingmoneyapp.Server.NetworkDefineConstant;

import java.util.ArrayList;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

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
    private boolean isRefresh;

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
                createGroupPopup.show(getSupportFragmentManager(), "create_group");

            }
        });
        mEnterGrouopFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomEntrancePopup entrancePopup = CustomEntrancePopup.newInstance();
                entrancePopup.show(getSupportFragmentManager(), "entrance_group");
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemBackgroundResource(R.color.white);


        mGroupListRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mGroupListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mGroupListRecyclerView.setHasFixedSize(true);
        mGroupListAdapter = new GroupAdapter(this);
        mGroupListRecyclerView.setAdapter(mGroupListAdapter);

        mGroupListSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        mGroupListSwipeRefreshLayout.setSize(SwipeRefreshLayout.LARGE);
        mGroupListSwipeRefreshLayout.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_orange_light,
                android.R.color.holo_green_light,
                android.R.color.holo_red_light
        );
        mGroupListSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        new LoadGroupListAsyncTask().execute();
                        mGroupListSwipeRefreshLayout.setRefreshing(false);
                    }
                }, 2500);

            }
        });
        new LoadGroupListAsyncTask().execute();
    }

    public void isSuccessCreateGroup(boolean isRefresh) {
        if (isRefresh) new LoadGroupListAsyncTask().execute();
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

        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_edit_password) {
            startActivity(new Intent(this, EditPasswordActivity.class));
        } else if (id == R.id.nav_edit_phoneNumber) {
            startActivity(new Intent(this, EditPhoneNumberActivity.class));
        } else if (id == R.id.nav_alarm_list) {

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
            groupDatas = new ArrayList<>();
        }

        public void addAllItem(ArrayList<GroupListItem> datas) {
            this.groupDatas = datas;
        }


        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            return new ViewHolder(mLayoutInflater.inflate(R.layout.group_list_item, parent, false));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {

            holder.groupName.setText(groupDatas.get(position).getGroupname());
            holder.groupNumber.setText(groupDatas.get(position).getMembernum());
            holder.view.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent i = new Intent(GroupListActivity.this, EventListActivity.class);
                    startActivity(i);
                }
            });
        }

        @Override
        public int getItemCount() {
            return groupDatas.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView groupName;
            TextView groupNumber;
            RecyclerView recyclerView;
            View view;

            public ViewHolder(View v) {
                super(v);
                view = v;
                groupName = (TextView) v.findViewById(R.id.groupname);
                groupNumber = (TextView) v.findViewById(R.id.groupNumber);
                recyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);
            }
        }
    }

    //group list 가져오기 위한 Thread
    public class LoadGroupListAsyncTask extends AsyncTask<Void, Void, ArrayList<GroupListItem>> {
        @Override
        protected ArrayList<GroupListItem> doInBackground(Void... voids) {
            String requestURL = "";
            Response response = null;
            try {

                requestURL = NetworkDefineConstant.SERVER_URL_GROUP_LIST;

                //연결
                OkHttpClient toServer = NetworkDefineConstant.getOkHttpClient();
                FormBody.Builder builder = new FormBody.Builder();
                builder.add("userid", "jun1").add("signal", "0");
                FormBody formBody = builder.build();
                //요청
                Request request = new Request.Builder()
                        .url(requestURL)
                        .post(formBody)
                        .build();
                //응답
                response = toServer.newCall(request).execute();
                boolean flag = response.isSuccessful();
                ResponseBody resBody = response.body();

                if (flag) { //http req/res 성공
                    return GroupJSONParsor.parseGroupListItems(resBody.string());
                } else { //실패시 정의
                    Log.e("에러", "데이터를 로드하는데 실패하였습니다");
                }
            } catch (Exception e) {
                Log.e("요청중에러", "그룹 리스트", e);
            } finally {
                if (response != null) {
                    response.close();
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<GroupListItem> result) {

            // RecyclerView Adapter Item 값 추가
            if (result != null && result.size() > 0) {

                mGroupListAdapter.addAllItem(result);
                mGroupListAdapter.notifyDataSetChanged();
            }
        }
    }
}
