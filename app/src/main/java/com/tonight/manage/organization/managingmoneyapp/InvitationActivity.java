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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tonight.manage.organization.managingmoneyapp.Object.InvitationList;
import com.tonight.manage.organization.managingmoneyapp.Object.InvitationListItem;

import java.util.ArrayList;

/**
 * Created by hooo5 on 2016-11-06.
 */

public class InvitationActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView mInvitationListRecyclerView;
    private InvitationAdapter mInvitationAdapter;
    private HorizontalScrollView invitationPersonScroll;
    private LinearLayout invitationPerSonLinear;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.invitation_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mInvitationListRecyclerView = (RecyclerView) findViewById(R.id.invitationRecyclerView);
        mInvitationListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mInvitationListRecyclerView.setHasFixedSize(true);
        mInvitationAdapter = new InvitationAdapter(this);
        mInvitationListRecyclerView.setAdapter(mInvitationAdapter);
        invitationPersonScroll = (HorizontalScrollView) findViewById(R.id.invitationPersonScroll);
        invitationPerSonLinear = (LinearLayout) findViewById(R.id.invitationPersonList);
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
        if (id == R.id.action_settings) {
            return true;
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

    class InvitationAdapter extends RecyclerView.Adapter<InvitationActivity.InvitationAdapter.ViewHolder> {


        private LayoutInflater mLayoutInflater;
        private ArrayList<InvitationListItem> invitationDatas;
        private Context mContext;

        public InvitationAdapter(Context context) {
            mContext = context;
            mLayoutInflater = LayoutInflater.from(context);
            invitationDatas = new ArrayList<>();
        }

        public void addItem(ArrayList<InvitationListItem> datas) {
            this.invitationDatas = datas;
        }


        @Override
        public InvitationActivity.InvitationAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            return new InvitationActivity.InvitationAdapter.ViewHolder(mLayoutInflater.inflate(R.layout.invitation_list_item, parent, false));
        }

        @Override
        public void onBindViewHolder(final InvitationActivity.InvitationAdapter.ViewHolder holder, int position) {
            //test
            holder.personName.setText("test");
            holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Button personBtn = new ProductButton.ProductBuilder(holder.personName.getText().toString()).build();
                    LinearLayout.LayoutParams plControl = new LinearLayout.LayoutParams(150,100);
                    plControl.setMargins(8,5,8,5);
                    invitationPerSonLinear.addView(personBtn,plControl);
                    invitationPersonScroll.computeScroll();
                } else { }
                }
            });
        }

        @Override
        public int getItemCount() {
            //test
            return 3;

        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView personName;
            RecyclerView recyclerView;
            CheckBox checkBox;
            ImageView profileImageView;

            public ViewHolder(View v) {
                super(v);
                checkBox = (CheckBox) v.findViewById(R.id.invitationCheckBox);
                personName = (TextView) v.findViewById(R.id.invitationListPersonName);
                recyclerView = (RecyclerView) v.findViewById(R.id.invitationRecyclerView);
                profileImageView = (ImageView) v.findViewById(R.id.invitationListProfileImageView);
            }
        }
    }

    public class LoadInvitationListAsyncTask extends AsyncTask<String, Integer, InvitationList> {
        @Override
        protected InvitationList doInBackground(String... args) {
            return null;
        }

        @Override
        protected void onPostExecute(InvitationList result) {

            if (result != null && result.data.size() > 0) {

                mInvitationAdapter.addItem(result.data);
                mInvitationAdapter.notifyDataSetChanged();
            }
        }
    }
}

