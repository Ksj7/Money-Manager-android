package com.tonight.manage.organization.managingmoneyapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.github.clans.fab.FloatingActionButton;
import com.readystatesoftware.viewbadger.BadgeView;
import com.tonight.manage.organization.managingmoneyapp.Custom.CustomCreateGroupPopup;
import com.tonight.manage.organization.managingmoneyapp.Custom.CustomEntrancePopup;
import com.tonight.manage.organization.managingmoneyapp.Object.GroupListBundle;
import com.tonight.manage.organization.managingmoneyapp.Object.GroupListItem;
import com.tonight.manage.organization.managingmoneyapp.Server.GroupJSONParser;
import com.tonight.manage.organization.managingmoneyapp.Server.NetworkDefineConstant;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
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

    private final int PICK_IMAGE_REQUEST = 1;
    public static final String UPLOAD_KEY = "image";

    private GroupAdapter mGroupListAdapter;
    private SwipeRefreshLayout mGroupListSwipeRefreshLayout;

    CircleImageView profileImage;
    TextView userName;
    TextView userPhone;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent i = getIntent();
        if(i==null) return;
        userId = i.getStringExtra("userId");


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton mCreateGroupFab = (FloatingActionButton) findViewById(R.id.creatGroupFab);
        FloatingActionButton mEnterGrouopFab = (FloatingActionButton) findViewById(R.id.enterGroupFab);

        mCreateGroupFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomCreateGroupPopup createGroupPopup = CustomCreateGroupPopup.newInstance(userId);
                createGroupPopup.show(getSupportFragmentManager(), "create_group");

            }
        });
        mEnterGrouopFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomEntrancePopup entrancePopup = CustomEntrancePopup.newInstance(userId);
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

        View headerView = navigationView.getHeaderView(0);
        profileImage = (CircleImageView) headerView.findViewById(R.id.profile_imageView);//프로필 이미지뷰
        userName = (TextView) headerView.findViewById(R.id.userNameText) ;
        userPhone = (TextView) headerView.findViewById(R.id.userPhoneNumberText);
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);//앨범으로 이동
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
            }
        });

        RecyclerView mGroupListRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mGroupListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mGroupListRecyclerView.setHasFixedSize(true);
        mGroupListAdapter = new GroupAdapter(this);
        mGroupListRecyclerView.setAdapter(mGroupListAdapter);

        mGroupListSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        mGroupListSwipeRefreshLayout.setSize(SwipeRefreshLayout.DEFAULT);

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
    }


    @Override
    protected void onResume() {
        super.onResume();
        new LoadGroupListAsyncTask().execute();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    { //이미지를 받으면 서버에 보내줌
        if (requestCode == PICK_IMAGE_REQUEST &&
                resultCode == RESULT_OK && data != null && data.getData() != null) {

            //무사히 종료되었으면
            Uri selectedImageUri = data.getData();
            try {
                Bitmap receivedbitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                UploadImage uploadImage = new UploadImage();
                uploadImage.execute(receivedbitmap);

                profileImage.setImageBitmap(receivedbitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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

        } else if( id == R.id.nav_logout){
            SharedPreferences pref = getSharedPreferences("Login", MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putBoolean("loginok", false);
            editor.apply();
            Intent intent = new Intent(GroupListActivity.this,LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {

        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)
        {

            this.moveTaskToBack(true);
            return true;
        }

        return super.onKeyDown(keyCode, event);
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
        public void onBindViewHolder(ViewHolder holder, final int position) {

            holder.groupName.setText(groupDatas.get(position).getGroupname());
            holder.groupNumber.setText(groupDatas.get(position).getMembernum());
            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(GroupListActivity.this, EventListActivity.class);
                    i.putExtra("groupName",groupDatas.get(position).getGroupname());
                    i.putExtra("balance",groupDatas.get(position).getBalance());
                    i.putExtra("groupcode",groupDatas.get(position).getGroupcode());
                    startActivity(i);
                }
            });
            String bubble = groupDatas.get(position).getBubblecount();
            if(bubble.equals("0")) {
                holder.badge.hide();
                return;
            }
            holder.badge.setText(bubble);
            holder.badge.show();
        }

        @Override
        public int getItemCount() {
            return groupDatas.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView groupName;
            TextView groupNumber;
            View bubble;
            RecyclerView recyclerView;
            View view;
            BadgeView badge;

            public ViewHolder(View v) {
                super(v);
                view = v;
                groupName = (TextView) v.findViewById(R.id.groupname);
                groupNumber = (TextView) v.findViewById(R.id.groupNumber);
                bubble = v.findViewById(R.id.bubble);
                badge = new BadgeView(getApplicationContext(), bubble);
                recyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);
            }
        }
    }

    //group list 가져오기 위한 Thread
    public class LoadGroupListAsyncTask extends AsyncTask<Void, Void, GroupListBundle> {
        @Override
        protected GroupListBundle doInBackground(Void... voids) {
            String requestURL = "";
            Response response = null;
            try {

                requestURL = NetworkDefineConstant.SERVER_URL_GROUP_LIST;

                //연결
                OkHttpClient toServer = NetworkDefineConstant.getOkHttpClient();
                FormBody.Builder builder = new FormBody.Builder();
                builder.add("userid", userId).add("signal", "0");
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
                    if(resBody!=null) {
                        return GroupJSONParser.parseGroupListItems(resBody.string());
                    }
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
        protected void onPostExecute(GroupListBundle result) {

            // RecyclerView Adapter Item 값 추가
            if (result.getResult() != null && result.getUserinfo() != null && result.getResult().size() > 0 && result.getUserinfo().size()>0) {

                mGroupListAdapter.addAllItem(result.getResult());
                mGroupListAdapter.notifyDataSetChanged();
                userName.setText(result.getUserinfo().get(0).getUsername());
                userPhone.setText(result.getUserinfo().get(0).getPhone());
                Glide.with(getApplicationContext())
                        .load(result.getUserinfo().get(0).getProfileimg())
                        .override(150, 150)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .into(profileImage);
            }
        }
    }

    class UploadImage extends AsyncTask<Bitmap, Void, String> {

        ProgressDialog loading;
        RequestHandler handler = new RequestHandler();
        String userid;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = ProgressDialog.show(GroupListActivity.this, "Uploading...", null, true, true);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            new LoadGroupListAsyncTask().execute();
            loading.dismiss();
        }

        @Override
        protected String doInBackground(Bitmap... params) {
            Bitmap bitmap = params[0];//사용자가 업로드할 이미지 비트맵
            String uploadImage = getStringImage(bitmap);

            SharedPreferences pref = getSharedPreferences("Login", MODE_PRIVATE);
            userid = pref.getString("id","error");

            HashMap<String, String> data = new HashMap<>();

            data.put(UPLOAD_KEY, uploadImage);
            data.put("userid", userid);
            String result = handler.sendPostRequest(NetworkDefineConstant.SERVERP_URL_UPLOAD_PROFILE_IMAGE, data);

            return result;
        }

        public String getStringImage(Bitmap bmp) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] imageBytes = baos.toByteArray();
            String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
            return encodedImage;
        }
    }

}