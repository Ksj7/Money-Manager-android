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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.tonight.manage.organization.managingmoneyapp.Custom.CustomProfilePopup;
import com.tonight.manage.organization.managingmoneyapp.Object.MemberListItem;
import com.tonight.manage.organization.managingmoneyapp.Server.GroupMemberJSONParsor;
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
 * Created by hooo5 on 2016-11-07.
 */

public class MemberListActivity extends AppCompatActivity {
    private RecyclerView mMemeberListRecyclerView;
    private MemberAdapter mMemberAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.member_main);

        Intent i = getIntent();
        if(i==null) {
            Toast.makeText(this, "오류가 발생했습니다. 다시 실행해 주세요", Toast.LENGTH_SHORT).show();
            return;
        }

        String groupcode = i.getStringExtra("groupcode");
        String groupName = i.getStringExtra("groupName");

        TextView groupNameText = (TextView) findViewById(R.id.groupNameText);
        groupNameText.setText(groupName);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mMemeberListRecyclerView = (RecyclerView) findViewById(R.id.memberListRecyclerView);
        mMemeberListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mMemeberListRecyclerView.setHasFixedSize(true);
        mMemberAdapter = new MemberAdapter(this);
        mMemeberListRecyclerView.setAdapter(mMemberAdapter);

        new LoadMemberListAsyncTask().execute(groupcode);
    }

    @Override
    public void onBackPressed() {
            super.onBackPressed();
    }


    class MemberAdapter extends RecyclerView.Adapter<MemberListActivity.MemberAdapter.ViewHolder> {


        private LayoutInflater mLayoutInflater;
        private ArrayList<MemberListItem> memberDatas;
        private Context mContext;

        public MemberAdapter(Context context) {
            mContext = context;
            mLayoutInflater = LayoutInflater.from(context);
            memberDatas = new ArrayList<>();
        }

        public void addAllItem(ArrayList<MemberListItem> datas) {
            this.memberDatas = datas;
        }


        @Override
        public MemberListActivity.MemberAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MemberListActivity.MemberAdapter.ViewHolder(mLayoutInflater.inflate(R.layout.member_list_item, parent, false));
        }

        @Override
        public void onBindViewHolder(final MemberListActivity.MemberAdapter.ViewHolder holder, int position) {
            //test
            holder.personName.setText(memberDatas.get(position).getUsername());

            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CustomProfilePopup customProfilePopup = CustomProfilePopup.newInstance(memberDatas);
                    customProfilePopup.show(getSupportFragmentManager(), "profile");
                }
            });
            if(memberDatas.get(position).getProfileimg()==null) return;
            Glide.with(getApplicationContext())
                    .load(memberDatas.get(position).getProfileimg())
                    .override(150, 150)
                    .into(holder.profileImageView);
        }

        @Override
        public int getItemCount() {
            return memberDatas.size();
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

    public class LoadMemberListAsyncTask extends AsyncTask<String, Integer, ArrayList<MemberListItem>> {
        @Override
        protected ArrayList<MemberListItem> doInBackground(String... args) {
            String requestURL = "";
            Response response = null;
            try {
                requestURL = NetworkDefineConstant.SERVER_URL_EVENT_LIST;

                OkHttpClient toServer = NetworkDefineConstant.getOkHttpClient();
                FormBody.Builder builder = new FormBody.Builder();
                builder.add("signal", "2")
                        .add("groupcode", args[0]);

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
                    return GroupMemberJSONParsor.parseMemberListItems((responseBody.string()));
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
        protected void onPostExecute(ArrayList<MemberListItem> result) {

            if (result != null && result.size() > 0) {
                mMemberAdapter.addAllItem(result);
                mMemberAdapter.notifyDataSetChanged();
            }
        }
    }
}
