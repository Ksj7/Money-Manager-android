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
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tonight.manage.organization.managingmoneyapp.Custom.CustomRateTextCircularProgressBar;
import com.tonight.manage.organization.managingmoneyapp.Object.EventListItem;
import com.tonight.manage.organization.managingmoneyapp.Server.EventJSONParsor;
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
public class EventListActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private RecyclerView mEventListRecyclerView;
    private EventListActivity.EventListAdapter mEventListAdapter;
    private SwipeRefreshLayout mEventListSwipeRefreshLayout;
    private String mGroupCode;
    TextView eventname;
    TextView balance;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eventlist);
        Intent i = getIntent();
        if (i == null) return;
        mGroupCode = i.getStringExtra("groupcode");

        eventname = (TextView) findViewById(R.id.eventlist_eventname);
        balance = (TextView) findViewById(R.id.eventlist_balance);

        mEventListRecyclerView = (RecyclerView) findViewById(R.id.eventlist_recyclerView);
        mEventListRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        mEventListRecyclerView.setHasFixedSize(true);
        mEventListAdapter = new EventListActivity.EventListAdapter(this);
        mEventListRecyclerView.setAdapter(mEventListAdapter);


        mEventListSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.eventlist_swipeRefreshLayout);
        mEventListSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                mEventListSwipeRefreshLayout.setRefreshing(false);
            }
        });


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        new LoadEventListAsyncTask().execute(mGroupCode);
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
            Intent i = new Intent(this, MemberListActivity.class);
            startActivity(i);
            return true;
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
        drawer.setBackgroundResource(R.color.white);
        return true;
    }


    class EventListAdapter extends RecyclerView.Adapter<EventListActivity.EventListAdapter.ViewHolder> {


        private LayoutInflater mLayoutInflater;
        private ArrayList<EventListItem> eventListItems; // group list
        private Context mContext;

        public EventListAdapter(Context context) {
            mContext = context;
            mLayoutInflater = LayoutInflater.from(context);
            eventListItems = new ArrayList<>();
        }

        public void addAllItem(ArrayList<EventListItem> datas) {
            this.eventListItems = datas;
        }


        @Override
        public EventListActivity.EventListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            return new EventListActivity.EventListAdapter.ViewHolder(mLayoutInflater.inflate(R.layout.event_list_item, parent, false));
        }

        @Override
        public void onBindViewHolder(EventListActivity.EventListAdapter.ViewHolder holder, final int position) {

            //test
            holder.eventName.setText(eventListItems.get(position).getEventname());
            holder.eventNumber.setText(eventListItems.get(position).getMembernum());

            // 목표액 / 모인금액으로 퍼센트 계산해서 뿌려주는 부분
            holder.mRateTextCircularProgressBar.setMax(100);
            holder.mRateTextCircularProgressBar.clearAnimation();
            holder.mRateTextCircularProgressBar.getCircularProgressBar().setCircleWidth(20);
            int summ = Integer.parseInt(eventListItems.get(position).getSumm());//모인금액 받아와야함.
            int targetm = Integer.parseInt(eventListItems.get(position).getTargetm());//목표액 받아와야함.
            if(summ == 0) holder.mRateTextCircularProgressBar.setProgress(0);
            else {
                int percent = targetm / summ;
                holder.mRateTextCircularProgressBar.setProgress(percent);//이 percent => 모인금액 / 목표액
            }

            holder.eventpercent.setText(holder.mRateTextCircularProgressBar.getCircularProgressBar().getProgress() + "%");
            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(EventListActivity.this, EventInfoActivity.class);
                    intent.putExtra("eventName", eventListItems.get(position).getEventname());
                    startActivity(intent);
                }
            });

        }

        @Override
        public int getItemCount() {

            return eventListItems.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView eventName;
            TextView eventNumber;
            TextView eventpercent;
            CustomRateTextCircularProgressBar mRateTextCircularProgressBar;
            View view;

            public ViewHolder(View v) {
                super(v);
                view = v;
                eventName = (TextView) v.findViewById(R.id.eventlist_title_textview);
                eventNumber = (TextView) v.findViewById(R.id.eventlist_number_textview);
                eventpercent = (TextView) v.findViewById(R.id.eventlist_percent_textview);
                mRateTextCircularProgressBar = (CustomRateTextCircularProgressBar) v.findViewById(R.id.rate_progress_bar);
            }
        }
    }


    //group list 가져오기 위한 Thread
    public class LoadEventListAsyncTask extends AsyncTask<String, Void, ArrayList<EventListItem>> {
        @Override
        protected ArrayList<EventListItem> doInBackground(String... arg) {
            String requestURL = "";
            Response response = null;
            try {

                requestURL = NetworkDefineConstant.SERVER_URL_EVENT_LIST;

                //연결
                OkHttpClient toServer = NetworkDefineConstant.getOkHttpClient();
                FormBody.Builder builder = new FormBody.Builder();
                builder.add("groupcode", arg[0]).add("signal", "0");
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
                    return EventJSONParsor.parseEventListItems(resBody.string());
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
        protected void onPostExecute(ArrayList<EventListItem> result) {

            // RecyclerView Adapter Item 값 추가
            if (result != null && result.size() > 0) {

                mEventListAdapter.addAllItem(result);
                mEventListAdapter.notifyDataSetChanged();
            }
        }
    }
}