package com.tonight.manage.organization.managingmoneyapp.Custom;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.tonight.manage.organization.managingmoneyapp.AddUsageByPasteActivity;
import com.tonight.manage.organization.managingmoneyapp.Object.EventInfoMemberPaymentListItem;
import com.tonight.manage.organization.managingmoneyapp.Object.EventInfoPaymentItem;
import com.tonight.manage.organization.managingmoneyapp.Object.EventInfoPaymentTotalItem;
import com.tonight.manage.organization.managingmoneyapp.Object.EventListBundle;
import com.tonight.manage.organization.managingmoneyapp.Object.EventListItem;
import com.tonight.manage.organization.managingmoneyapp.Object.GroupListBundle;
import com.tonight.manage.organization.managingmoneyapp.Object.GroupListItem;
import com.tonight.manage.organization.managingmoneyapp.R;
import com.tonight.manage.organization.managingmoneyapp.Server.EventInfoJSONParser;
import com.tonight.manage.organization.managingmoneyapp.Server.EventJSONParser;
import com.tonight.manage.organization.managingmoneyapp.Server.GroupJSONParser;
import com.tonight.manage.organization.managingmoneyapp.Server.NetworkDefineConstant;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Taek on 2016. 12. 5..
 */

public class CustomSelectEventForUsageSMSPupup extends DialogFragment{
    private boolean isSuccess;
    private String id;
    private String userid;
    int groupcount = 0;
    int currentindex = 0;
    //----
    CustomSelectEventForUsageSMSPupup.ExpandableListAdapter expandableListAdapter;
    ExpandableListView expListView;
    ArrayList<GroupListItem> listDataHeader;
    HashMap<String, ArrayList<EventListItem>> listDataChild;
    ArrayList<ArrayList<EventListItem>> childarr;
    //------

    int width;
    int height;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, R.style.CustomDialogTheme);

        SharedPreferences pref = getActivity().getSharedPreferences("Login", MODE_PRIVATE);
        userid = pref.getString("id","");
    }

    public static CustomSelectEventForUsageSMSPupup newInstance(String id) {
        CustomSelectEventForUsageSMSPupup customSelectEventForUsageSMSPupup = new CustomSelectEventForUsageSMSPupup();
        Bundle b = new Bundle();
        b.putString("id", id);
        customSelectEventForUsageSMSPupup.setArguments(b);
        return customSelectEventForUsageSMSPupup;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.popup_selectevent_sms, container, false);
        Bundle b = getArguments();
        if (b == null) {
            Toast.makeText(getActivity(), "오류가 발생하였습니다.", Toast.LENGTH_SHORT).show();
            return view;
        }

        id = b.getString("id");

        ExpandableListView expListView = (ExpandableListView) view.findViewById(R.id.expandable_listview);
        //prepareListData();

        listDataHeader = new ArrayList<GroupListItem>();
        listDataChild = new HashMap<String, ArrayList<EventListItem>>();
        expandableListAdapter = new CustomSelectEventForUsageSMSPupup.ExpandableListAdapter(getContext(),listDataHeader,listDataChild);
        expListView.setAdapter(expandableListAdapter);
        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener(){
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                String groupname = listDataHeader.get(groupPosition).getGroupname();
                String eventname = listDataChild.get(listDataHeader.get(groupPosition).getGroupname()).get(childPosition).getEventname();
                String eventnum = listDataChild.get(listDataHeader.get(groupPosition).getGroupname()).get(childPosition).getEventnum();
                new LoadEventInfoAsyncTask().execute(eventname,groupname,eventnum);
                //Toast.makeText(getContext(), listDataChild.get(listDataHeader.get(groupPosition).getGroupname()).get(childPosition).getEventname(),Toast.LENGTH_SHORT).show();

                return true;
            }
        });
        new LoadGroupListAsyncTask().execute();


        return view;

    }

    @Override
    public void onStop() {
        AddUsageByPasteActivity.activityInstance.finish();
        AddUsageByPasteActivity.activityInstance.overridePendingTransition(0, 0);
        super.onStop();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        width = getResources().getDimensionPixelSize(R.dimen.popup_entrance_group_width);
        height = getResources().getDimensionPixelSize(R.dimen.popup_entrance_group_height);
        getDialog().getWindow().setLayout(width, 1200);
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
                builder.add("userid", userid).add("signal", "0");
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
                    return GroupJSONParser.parseGroupListItems(resBody.string());
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
                for(int i = 0 ; i < result.getResult().size() ; i ++){
                    Log.e("결과",result.getResult().get(i).getGroupname()+" 결과입니다.");
                    listDataHeader.add(result.getResult().get(i));
                }
                expandableListAdapter.addAllItem(result.getResult());
                groupcount = result.getResult().size();
                for(int i = 0 ; i < result.getResult().size() ; i++){
                    new CustomSelectEventForUsageSMSPupup.LoadEventListAsyncTask().execute(result.getResult().get(i).getGroupcode());
                }
                //expandableListAdapter.notifyDataSetChanged();
                /*mGroupListAdapter.addAllItem(result.getResult());
                mGroupListAdapter.notifyDataSetChanged();
                userName.setText(result.getUserinfo().get(0).getUsername());
                userPhone.setText(result.getUserinfo().get(0).getPhone());
                Glide.with(getApplicationContext())
                        .load(result.getUserinfo().get(0).getProfileimg())
                        .override(150, 150)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .into(profileImage);*/

            }
        }
    }
    public class LoadEventListAsyncTask extends AsyncTask<String, Void, EventListBundle> {
        @Override
        protected EventListBundle doInBackground(String... arg) {
            String requestURL = "";
            Response response = null;
            try {

                requestURL = NetworkDefineConstant.SERVER_URL_EVENT_LIST;

                //연결
                OkHttpClient toServer = NetworkDefineConstant.getOkHttpClient();
                FormBody.Builder builder = new FormBody.Builder();
                builder.add("groupcode", arg[0])
                        .add("signal", "0")
                        .add("userid",userid);
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
                    return EventJSONParser.parseEventListItems(resBody.string());
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
        protected void onPostExecute(EventListBundle result) {
            if (result.getResult() != null && result.getUserinfo() != null && result.getResult().size() > 0 && result.getUserinfo().size()>0) {
                /*for(int i = 0 ; i < result.getResult().size() ; i++){
                    Log.e("eventlist불러오는중",result.getResult().get(i).getEventname()+" 입니다.");
                }*/
                listDataChild.put(listDataHeader.get(currentindex++).getGroupname(), result.getResult());
                expandableListAdapter.notifyDataSetChanged();
            }
        }
    }
    public class LoadEventInfoAsyncTask extends AsyncTask<String, Void, EventInfoPaymentTotalItem> {
        private int memberPosition=2;
        String groupname;
        String eventname;
        String eventnum;
        @Override
        protected EventInfoPaymentTotalItem doInBackground(String... arg) {
            String requestURL = "";
            Response response = null;
            eventname = arg[0];
            groupname = arg[1];
            eventnum = arg[2];
            try {

                requestURL = NetworkDefineConstant.SERVER_URL_EVENT_INFO;

                SharedPreferences pref = getActivity().getSharedPreferences("Login", MODE_PRIVATE);
                String userid = pref.getString("id","error");

                //연결
                OkHttpClient toServer = NetworkDefineConstant.getOkHttpClient();
                FormBody.Builder builder = new FormBody.Builder();
                //builder.add("eventnum", eventnum).add("userid", userid);
                builder.add("userid", userid).add("eventnum", eventnum).add("signal","0");

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
                    return EventInfoJSONParser.parseEventInfoMemberItems(new StringBuilder(resBody.string()));
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
        protected void onPostExecute(EventInfoPaymentTotalItem result) {
            //ArrayList<EventInfoMemberPaymentListItem> eventInfoMemberItemArrayList = null ;
            //ArrayList<EventInfoPaymentItem> eventInfoPaymentItemArrayList = null;
            ArrayList<String> eventInfoMemberPositionArrayList=null;


            if (result != null) {
                Log.e("받아온 정보들", result.toString());
                //eventInfoMemberItemArrayList = result.getEventInfoMemberItemArrayList();
                eventInfoMemberPositionArrayList = result.getEventInfoMemberPositionArrayList();
                //eventInfoPaymentItemArrayList = result.getEventInfoPaymentItemArrayList();

                if (eventInfoMemberPositionArrayList.size() > 0) {
                    memberPosition = Integer.parseInt(eventInfoMemberPositionArrayList.get(0));
                    Log.e("멤버포지션", memberPosition + "?");
                    if(memberPosition == 0){
                        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                        alert.setTitle("사용내역 추가");
                        alert.setMessage("조선주막에서 사용한 4000원을 '"+groupname+"' 그룹의 '"+eventname+"' 이벤트의 사용내역에 추가하시겠습니까?");
                        alert.setPositiveButton("예",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //서버전송.
                                dismiss();
                                //AddUsageByPasteActivity.AddUsageByPasteActivity.finish();
                            }
                        });
                        alert.setNegativeButton("아니요",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                            }
                        });


                        alert.show();
                    }
                    else{
                        Toast.makeText(getContext(),"당신은 '"+eventname+"' 이벤트의 총무가 아닙니다",Toast.LENGTH_SHORT).show();
                    }
                }
/*
                if (eventInfoPaymentItemArrayList.size() > 0) {//이벤트 정보
                    mTargetMoney.setText(eventInfoPaymentItemArrayList.get(0).getTargetMoney());//목표액
                    mCollectedMoney.setText(eventInfoPaymentItemArrayList.get(0).getCollectedMoney());//모인 금액
                    mEventDate.setText(eventInfoPaymentItemArrayList.get(0).getEventDate());//기한
                    mManagerName.setText(eventInfoPaymentItemArrayList.get(0).getManagerId());//총무 이름
                    //총무 프로필
                    myUserName.setText(eventInfoPaymentItemArrayList.get(0).getUserName());//유저 이름
                    myMoney.setText(eventInfoPaymentItemArrayList.get(0).getPersonalMoney()); //유저 금액

                    if (Integer.parseInt(eventInfoPaymentItemArrayList.get(0).getUserIspay()) == 1) {
                        myStatus.setText("지출완료");//유저 상태
                    }
                    Glide.with(getActivity().getApplicationContext())
                            .load(eventInfoPaymentItemArrayList.get(0).getUserprofileURL())
                            .override(150, 150)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .into(myProfileImage); // 유저 프로필

                }


                if (eventInfoMemberItemArrayList.size() > 0) {//멤버 리스트
                    SharedPreferences pref = getActivity().getSharedPreferences("Login", MODE_PRIVATE);
                    String userid = pref.getString("id","error");

                    if(memberPosition!=2){
                        for(int i=0; i<eventInfoMemberItemArrayList.size(); i++){
                            if(eventInfoMemberItemArrayList.get(i).getUserId().equals(userid)){//내 아이디 있으면
                                eventInfoMemberItemArrayList.remove(i);
                                break;
                            }
                        }
                    }

                    mPaymentListAdapter.addItem(eventInfoMemberItemArrayList);
                    mPaymentListAdapter.notifyDataSetChanged();
                }*/

            }
        }
    }

    class ExpandableListAdapter extends BaseExpandableListAdapter {

        private Context _context;
        private ArrayList<GroupListItem> _listDataHeader; // header titles
        // child data in format of header title, child title
        private HashMap<String, ArrayList<EventListItem>> _listDataChild;

        public ExpandableListAdapter(Context context, ArrayList<GroupListItem> listDataHeader,
                                     HashMap<String, ArrayList<EventListItem>> listChildData) {
            this._context = context;
            this._listDataHeader = listDataHeader;
            this._listDataChild = listChildData;
        }

        @Override
        public EventListItem getChild(int groupPosition, int childPosititon) {
            return this._listDataChild.get(this._listDataHeader.get(groupPosition).getGroupname())
                    .get(childPosititon);
        }

        public void addAllItem(ArrayList<GroupListItem> datas) {
            this._listDataHeader = datas;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public View getChildView(int groupPosition, final int childPosition,
                                 boolean isLastChild, View convertView, ViewGroup parent) {

            final String childText =  getChild(groupPosition, childPosition).getEventname();

            if (convertView == null) {
                LayoutInflater infalInflater = (LayoutInflater) this._context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = infalInflater.inflate(R.layout.popup_selectevent_sms_list_item, null);
            }

            TextView txtListChild = (TextView) convertView
                    .findViewById(R.id.lblListItem);

            txtListChild.setText(childText);
            return convertView;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return this._listDataChild.get(this._listDataHeader.get(groupPosition).getGroupname()).size();
        }

        @Override
        public GroupListItem getGroup(int groupPosition) {
            return this._listDataHeader.get(groupPosition);
        }

        @Override
        public int getGroupCount() {
            return this._listDataHeader.size();
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded,
                                 View convertView, ViewGroup parent) {
            String headerTitle = getGroup(groupPosition).getGroupname();
            if (convertView == null) {
                LayoutInflater infalInflater = (LayoutInflater) this._context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = infalInflater.inflate(R.layout.popup_selectevent_sms_list_group, null);
            }

            TextView lblListHeader = (TextView) convertView
                    .findViewById(R.id.lblListHeader);
            lblListHeader.setTypeface(null, Typeface.BOLD);
            lblListHeader.setText(headerTitle);

            return convertView;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }
}
