package com.tonight.manage.organization.managingmoneyapp;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.tonight.manage.organization.managingmoneyapp.Builder.ProductButton;
import com.tonight.manage.organization.managingmoneyapp.Object.InvitationListItem;
import com.tonight.manage.organization.managingmoneyapp.Server.InvitationJSONParsor;
import com.tonight.manage.organization.managingmoneyapp.Server.NetworkDefineConstant;

import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by hooo5 on 2016-11-06.
 */

public class InvitationActivity extends AppCompatActivity {

    private RecyclerView mInvitationListRecyclerView;
    private InvitationAdapter mInvitationAdapter;
    private HorizontalScrollView invitationPersonScroll;
    private LinearLayout invitationPerSonLinear;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.invitation_main);

        Intent i = getIntent();
        if(i == null) return ;

        //String eventName = i.getStringExtra("eventName");
        String eventnum = i.getStringExtra("eventnum");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mInvitationListRecyclerView = (RecyclerView) findViewById(R.id.invitationRecyclerView);
        mInvitationListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mInvitationListRecyclerView.setHasFixedSize(true);
        mInvitationAdapter = new InvitationAdapter(this);
        mInvitationListRecyclerView.setAdapter(mInvitationAdapter);
        invitationPersonScroll = (HorizontalScrollView) findViewById(R.id.invitationPersonScroll);
        invitationPerSonLinear = (LinearLayout) findViewById(R.id.invitationPersonList);

        new LoadInvitationListAsyncTask().execute(eventnum);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    class InvitationAdapter extends RecyclerView.Adapter<InvitationActivity.InvitationAdapter.ViewHolder> {


        private LayoutInflater mLayoutInflater;
        private ArrayList<InvitationListItem> invitationListArrayList;
        private Context mContext;

        InvitationAdapter(Context context) {
            mContext = context;
            mLayoutInflater = LayoutInflater.from(context);
            invitationListArrayList = new ArrayList<>();
        }

        void addItem(ArrayList<InvitationListItem> datas) {
            this.invitationListArrayList = datas;
        }


        @Override
        public InvitationActivity.InvitationAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            return new InvitationActivity.InvitationAdapter.ViewHolder(mLayoutInflater.inflate(R.layout.invitation_list_item, parent, false));
        }

        @Override
        public void onBindViewHolder(final InvitationActivity.InvitationAdapter.ViewHolder holder, int position) {
            //test
            InvitationListItem listItem = invitationListArrayList.get(position);
            holder.personName.setText(listItem.getUsername());
            holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        Button personBtn = new ProductButton.ProductBuilder(holder.personName.getText().toString()).build();
                        LinearLayout.LayoutParams plControl = new LinearLayout.LayoutParams(150, 100);
                        plControl.setMargins(8, 5, 8, 5);
                        invitationPerSonLinear.addView(personBtn, plControl);
                        invitationPersonScroll.computeScroll();
                    } else {
                    }
                }
            });
            Glide.with(getApplicationContext()).load(listItem.getProfileimg()).into(holder.profileImageView);
        }

        @Override
        public int getItemCount() {
            return invitationListArrayList.size();
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

    public class LoadInvitationListAsyncTask extends AsyncTask<String, Integer, ArrayList<InvitationListItem>> {
        @Override
        protected ArrayList<InvitationListItem> doInBackground(String... args) {
            String requestURL = "";
            Response response = null;
            try {
                requestURL = NetworkDefineConstant.SERVER_URL_INVITATION;

                OkHttpClient toServer = NetworkDefineConstant.getOkHttpClient();
                FormBody.Builder builder = new FormBody.Builder();
                builder.add("signal", "1")
                        .add("eventnum", args[0]);

                FormBody formBody = builder.build();

                Request request = new Request.Builder()
                        .url(requestURL)
                        .post(formBody)
                        .build();


                response = toServer.newCall(request).execute();
                ResponseBody responseBody = response.body();
                boolean flag = response.isSuccessful();

                int responseCode = response.code();
                if (responseCode >= 400) return null;
                if (flag) {
                    return InvitationJSONParsor.parseInvitationListItems((responseBody.string()));
                }
            } catch (UnknownHostException une) {
                Log.e("connectionFail", une.toString());
            } catch (UnsupportedEncodingException uee) {
                Log.e("connectionFail", uee.toString());
            } catch (Exception e) {
                Log.e("connectionFail", e.toString());
            } finally {
                if (response != null) {
                    response.close();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<InvitationListItem> result) {

            if (result != null && result.size() > 0) {
                mInvitationAdapter.addItem(result);
                mInvitationAdapter.notifyDataSetChanged();
            }
        }
    }
}

