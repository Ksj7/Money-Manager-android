package com.tonight.manage.organization.managingmoneyapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.tonight.manage.organization.managingmoneyapp.Custom.CustomUsagePopup;
import com.tonight.manage.organization.managingmoneyapp.Object.EventInfoUsageListItem;

import java.util.ArrayList;


/**
 * Created by 3 on 2016-11-14.
 */

public class UserFragment extends Fragment implements View.OnClickListener {

    private RecyclerView mUsageListRecyclerView;
    private EventInfoUsageAdapter mUsageListAdapter;
    private SwipeRefreshLayout mUsageListSwipeRefreshLayout;
    Button mUsageUploadButton;
    ArrayList<EventInfoUsageListItem> arrayList;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

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
                mUsageListSwipeRefreshLayout.setRefreshing(false);
            }
        });

        return v;

    }

    @Override
    public void onClick(View view) { //upload버튼 클릭 시
        Snackbar snackbar = Snackbar.make(view, "", Snackbar.LENGTH_SHORT);
        Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbar.getView();
        //layout.setPadding(0, 0, 0, 0);//set padding to 0

        TextView textView = (TextView) layout.findViewById(android.support.design.R.id.snackbar_text);
        textView.setVisibility(View.INVISIBLE);

        View snackView = getActivity().getLayoutInflater().inflate(R.layout.activity_event_info_usage_snack,null);
        ImageView cameraButton = (ImageView) snackView.findViewById(R.id.usage_snack_camera);
        ImageView galleryButton = (ImageView) snackView.findViewById(R.id.usage_snack_gallery);

        layout.addView(snackView, 0);
        snackbar.show();

        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {//카메라로부터 가져오기
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent,1);
            }
        });
        galleryButton.setOnClickListener(new View.OnClickListener() { //앨범으로부터 가져오기
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                startActivityForResult(intent, 1);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    { //이미지를 받으면 서버에 보내줌
         if(resultCode == getActivity().RESULT_OK) { //무사히 종료되었으면
             Uri selectedImageUri = data.getData();
             Toast.makeText(getActivity(), selectedImageUri + " ", Toast.LENGTH_LONG).show();

             CustomUsagePopup usagePopup = CustomUsagePopup.newInstance();
             usagePopup.show(getActivity().getSupportFragmentManager(),"usage_popup");

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
        public void onBindViewHolder(EventInfoUsageAdapter.ViewHolder holder, int position) {

            //test
            holder.date.setText("16.11.04");
            holder.location.setText("회식");
            holder.usedmoney.setText("20000원");
            holder.view.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    //Intent i = new Intent(EventInfoActivity.this,EventListActivity.class);
                    //startActivity(i);
                }
            });
            //이게 정상
            //holder.eventName.setText(groupDatas.get(position).eventName);
            //holder.groupNumber.setText(groupDatas.get(position).groupNumber+"명");
        }

        @Override
        public int getItemCount() {
            //test
            //return 3;

            //이게 원래 정상
             return usageArrayList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView date;
            TextView location ;
            TextView usedmoney;
            RecyclerView recyclerView;
            View view;
            public ViewHolder(View v) {
                super(v);
                view = v;
                date = (TextView) v.findViewById(R.id.eventInfo_usage_date);
                location = (TextView) v.findViewById(R.id.eventInfo_usage_Location);
                usedmoney = (TextView) v.findViewById(R.id.eventInfo_usage_usedMoney);
                recyclerView = (RecyclerView) v.findViewById(R.id.eventInfo_user_recyclerView);

            }
        }
    }


}

/* public  void SaveBitmapToFileCache(Bitmap bitmap, String strFilePath,
                                       String filename) {

        File file = new File(strFilePath);

        // If no folders
        if (!file.exists()) {
            file.mkdirs();
            // Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();
        }

        File fileCacheItem = new File(strFilePath + filename);
        OutputStream out = null;

        try {
            fileCacheItem.createNewFile();
            out = new FileOutputStream(fileCacheItem);

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    } //파일 생성하는 메소드*/

