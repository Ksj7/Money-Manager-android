package com.tonight.manage.organization.managingmoneyapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.goka.flickableview.FlickableImageView;
import com.tonight.manage.organization.managingmoneyapp.Custom.CustomUsagePopup;
import com.tonight.manage.organization.managingmoneyapp.Object.EventInfoUsageListItem;
import com.tonight.manage.organization.managingmoneyapp.Server.EventInfoJSONParser;
import com.tonight.manage.organization.managingmoneyapp.Server.NetworkDefineConstant;

import java.util.ArrayList;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;


/**
 * Created by 3 on 2016-11-14.
 */

public class UsageFragment extends Fragment implements View.OnClickListener {
    private int PICK_IMAGE_REQUEST = 1;

    private RecyclerView mUsageListRecyclerView;
    private EventInfoUsageAdapter mUsageListAdapter;
    private SwipeRefreshLayout mUsageListSwipeRefreshLayout;
    Button mUsageUploadButton;
    String eventnum;
    ArrayList<EventInfoUsageListItem> arrayList;
    private  WindowManager.LayoutParams mParams;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        Bundle b = getArguments();
        eventnum = b.getString("eventnum");
        View v= inflater.inflate(R.layout.activity_event_info_user, container, false);

        mUsageUploadButton = (Button) v.findViewById(R.id.eventinfo_user_btn_upload);
        mUsageUploadButton.setOnClickListener(this);

        mUsageListRecyclerView = (RecyclerView) v.findViewById(R.id.eventInfo_user_recyclerView);
        mUsageListRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mUsageListRecyclerView.setHasFixedSize(true);
        mUsageListAdapter = new EventInfoUsageAdapter(getActivity());
        mUsageListRecyclerView.setAdapter(mUsageListAdapter);

        mUsageListSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.eventInfo_user_swipeRefreshLayout);
        mUsageListSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                new UsageListLoadAsyncTask().execute();
                mUsageListSwipeRefreshLayout.setRefreshing(false);
            }
        });

        new UsageListLoadAsyncTask().execute();
        return v;

    }

    @Override
    public void onClick(View view) { //upload버튼 클릭 시
        Snackbar snackbar = Snackbar.make(view, null, Snackbar.LENGTH_SHORT);
        Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbar.getView();

        View snackView = getActivity().getLayoutInflater().inflate(R.layout.activity_event_info_usage_snack,null);
        ImageView cameraButton = (ImageView) snackView.findViewById(R.id.usage_snack_camera);
        ImageView galleryButton = (ImageView) snackView.findViewById(R.id.usage_snack_gallery);
        mParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        mParams.gravity = Gravity.BOTTOM;
        layout.addView(snackView,mParams);

        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {//카메라로부터 가져오기
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent,PICK_IMAGE_REQUEST);
            }
        });
        galleryButton.setOnClickListener(new View.OnClickListener() { //앨범으로부터 가져오기
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
            }
        });
        snackbar.show();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    { //이미지를 받으면 서버에 보내줌
        if (requestCode == PICK_IMAGE_REQUEST &&
                resultCode == getActivity().RESULT_OK && data != null && data.getData() != null) {

            //무사히 종료되었으면
             Uri selectedImageUri = data.getData();
             Toast.makeText(getActivity(), selectedImageUri + " ", Toast.LENGTH_LONG).show();

             CustomUsagePopup usagePopup = CustomUsagePopup.newInstance(selectedImageUri,eventnum);
             usagePopup.show(getActivity().getSupportFragmentManager(),"usage_popup"); //popup창 띄우고

        }
    }


    class EventInfoUsageAdapter extends RecyclerView.Adapter<EventInfoUsageAdapter.ViewHolder> {

        private LayoutInflater mLayoutInflater;
        private ArrayList<EventInfoUsageListItem> usageArrayList; // group list
        private Context mContext;

        public EventInfoUsageAdapter(Context context) {
            mContext = context;
            mLayoutInflater = LayoutInflater.from(context);
            usageArrayList = new ArrayList<EventInfoUsageListItem>();
        }

        public void addItem(ArrayList<EventInfoUsageListItem> datas) {
            this. usageArrayList = datas;
        }


        @Override
        public EventInfoUsageAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            return new EventInfoUsageAdapter.ViewHolder(mLayoutInflater.inflate(R.layout.activity_event_info_user_item, parent, false));
        }

        @Override
        public void onBindViewHolder(EventInfoUsageAdapter.ViewHolder holder, final int position) {

            //test
            holder.date.setText(usageArrayList.get(position).getDate());
            holder.location.setText(usageArrayList.get(position).getLocation());
            holder.usedmoney.setText(usageArrayList.get(position).getUsedMoney());

            final String img = usageArrayList.get(position).getReceipturl();

            if(usageArrayList.get(position).getReceipturl() !=null &&
                    !usageArrayList.get(position).getReceipturl().equals("0")){
                Log.e("여기이미지가있니?","여기여기"+img);
                holder.existimg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Toast.makeText(getActivity(), "여기 이미지가 있어요", Toast.LENGTH_LONG).show();
                        Intent i = new Intent(getActivity(), ProfileImageActivity.class);
                        i.putExtra("profile",img);
                        startActivity(i);
                    }
                });

            }//영수증이 존재하면
            else{
                holder.existimg.setVisibility(getView().GONE);
            }
        }

        @Override
        public int getItemCount() {
             return usageArrayList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView date;
            TextView location ;
            TextView usedmoney;
            RecyclerView recyclerView;
            ImageView existimg;
            View view;
            public ViewHolder(View v) {
                super(v);
                view = v;
                date = (TextView) v.findViewById(R.id.eventInfo_usage_date);
                location = (TextView) v.findViewById(R.id.eventInfo_usage_Location);
                usedmoney = (TextView) v.findViewById(R.id.eventInfo_usage_usedMoney);
                recyclerView = (RecyclerView) v.findViewById(R.id.eventInfo_user_recyclerView);
                existimg = (ImageView) v.findViewById(R.id.usage_existImg);
            }
        }
    }


    //Usage list 가져오기 위한 Thread
    public class UsageListLoadAsyncTask extends AsyncTask<String, Void, ArrayList<EventInfoUsageListItem>> {
        @Override
        protected ArrayList<EventInfoUsageListItem> doInBackground(String... arg) {
            String requestURL = "";
            Response response = null;
            try {

                requestURL = NetworkDefineConstant.SERVER_URL_EVENT_INFO;

                //연결
                OkHttpClient toServer = NetworkDefineConstant.getOkHttpClient();
                FormBody.Builder builder = new FormBody.Builder();
                builder.add("eventnum", eventnum).add("signal","5");
                Log.e("???????????? ",eventnum+",");
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
                    //Log.e("--------------- ",resBody.string());
                    return EventInfoJSONParser.parseEventInfoUsageItems(new StringBuilder(resBody.string()));
                } else { //실패시 정의
                    Log.e("에러", "데이터를 로드하는데 실패하였습니다");
                }
            } catch (Exception e) {
                Log.e("요청중에러", "payment프레그먼트", e);
            } finally {
                if (response != null) {
                    response.close();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<EventInfoUsageListItem> result) {

            if (result != null && result.size() > 0) {
                Log.e("받아온 정보들", result.toString());

                mUsageListAdapter.addItem(result);
                mUsageListAdapter.notifyDataSetChanged();
            }
        }
    }

}

