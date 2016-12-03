package com.tonight.manage.organization.managingmoneyapp;

import android.annotation.SuppressLint;
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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.tonight.manage.organization.managingmoneyapp.Builder.ProductButton;
import com.tonight.manage.organization.managingmoneyapp.Object.InvitationListItem;
import com.tonight.manage.organization.managingmoneyapp.Object.InvitationUpdateItem;
import com.tonight.manage.organization.managingmoneyapp.Server.InvitationJSONParsor;
import com.tonight.manage.organization.managingmoneyapp.Server.NetworkDefineConstant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
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
    private ArrayList<InvitationUpdateItem> newInvitationMemberList;
    private HashMap<Integer, Button> buttonBundle;
    private String eventnum;
    private int isSuccess;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.invitation_main);

        Intent i = getIntent();
        if (i == null) return;

        isSuccess = -1;
        //String eventName = i.getStringExtra("eventName");
        eventnum = i.getStringExtra("eventnum");

        newInvitationMemberList = new ArrayList<>();
        buttonBundle = new HashMap<>();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mInvitationListRecyclerView = (RecyclerView) findViewById(R.id.invitationRecyclerView);
        mInvitationListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mInvitationListRecyclerView.setHasFixedSize(true);
        mInvitationAdapter = new InvitationAdapter(this);
        mInvitationListRecyclerView.setAdapter(mInvitationAdapter);
        invitationPersonScroll = (HorizontalScrollView) findViewById(R.id.invitationPersonScroll);
        invitationPerSonLinear = (LinearLayout) findViewById(R.id.invitationPersonList);


        Button invitationButton = (Button) findViewById(R.id.confirmBtn);
        invitationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new UpdateInvitationListAsyncTask().execute(eventnum);
            }
        });
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
        public void onBindViewHolder(final InvitationActivity.InvitationAdapter.ViewHolder holder,
                                     @SuppressLint("RecyclerView") final int position) {
            //test
            final InvitationListItem listItem = invitationListArrayList.get(position);
            holder.personName.setText(listItem.getUsername());
            holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    if (isChecked) {
                        InvitationUpdateItem member = new InvitationUpdateItem();
                        member.setUserid(listItem.getUserid());
                        member.setEventnum(eventnum);
                        newInvitationMemberList.add(position, member);
                        Button personBtn = new ProductButton.ProductBuilder(holder.personName.getText().toString()).build();
                        buttonBundle.put(position, personBtn);
                        LinearLayout.LayoutParams plControl = new LinearLayout.LayoutParams(150, 100);
                        plControl.setMargins(8, 5, 8, 5);
                        invitationPerSonLinear.addView(personBtn, plControl);
                        invitationPersonScroll.computeScroll();
                    } else {
                        Button removeButton = buttonBundle.get(position);
                        invitationPerSonLinear.removeView(removeButton);
                        buttonBundle.remove(position);
                        newInvitationMemberList.remove(position);
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

    public class UpdateInvitationListAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... args) {
            String requestURL = "";
            Response response = null;
            try {
                requestURL = NetworkDefineConstant.SERVER_URL_UPDATE_INVITATION;

                OkHttpClient toServer = NetworkDefineConstant.getOkHttpClient();

                JSONObject newmember = new JSONObject();
                JSONArray newMemberList = new JSONArray();//배열이 필요할때
                for (int i = 0; i < newInvitationMemberList.size(); i++)//배열
                {
                    JSONObject memberInfo = new JSONObject();//배열 내에 들어갈 json
                    memberInfo.put("eventnum", newInvitationMemberList.get(i).getEventnum());
                    memberInfo.put("userid", newInvitationMemberList.get(i).getUserid());
                    newMemberList.put(memberInfo);
                }
                newmember.put("invitejson", newMemberList);//배열을 넣음

                final MediaType JSON
                        = MediaType.parse("application/json; charset=utf-8");
                RequestBody body = RequestBody.create(JSON, newmember.toString());


                Request request = new Request.Builder()
                        .url(requestURL)
                        .post(body)
                        .build();


                response = toServer.newCall(request).execute();
                ResponseBody resBody = response.body();
                boolean flag = response.isSuccessful();

                int responseCode = response.code();
                if (responseCode >= 400) return null;
                if (flag) {
                    String res = resBody.string();
                    if (res.contains("0"))
                        isSuccess = 0;
                    else if (res.contains("1")) {
                        isSuccess = 1;
                    } else if (res.contains("2")) {
                        isSuccess = 2;
                    } else {
                        isSuccess = -1;
                    }
                    return res;

                } else { //실패시 정의
                    Log.e("에러", "데이터를 로드하는데 실패하였습니다");
                }

            } catch (JSONException | IOException e) {
                e.printStackTrace();
            } finally {
                if (response != null) {
                    response.close();
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {

            if (result != null) {

                switch (isSuccess) {
                    case 0:
                        Toast.makeText(InvitationActivity.this, "결제 진행중이여서 초대가 불가능 합니다.", Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        Toast.makeText(InvitationActivity.this, "초대 성공!", Toast.LENGTH_SHORT).show();
                        finish();
                        break;
                    case 2:
                        Toast.makeText(InvitationActivity.this, "초대 실패!", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        Toast.makeText(InvitationActivity.this, "에러!", Toast.LENGTH_SHORT).show();
                        break;
                }
                Log.d("invite result : ",result);
            }
        }
    }
}

