package com.tonight.manage.organization.managingmoneyapp.Custom;

import android.app.ProgressDialog;
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

import com.tonight.manage.organization.managingmoneyapp.AddPayCheckByTossNotificationActivity;
import com.tonight.manage.organization.managingmoneyapp.EventInfoActivity;
import com.tonight.manage.organization.managingmoneyapp.Object.EventInfoMemberPaymentListItem;
import com.tonight.manage.organization.managingmoneyapp.Object.EventInfoPaymentItem;
import com.tonight.manage.organization.managingmoneyapp.Object.EventInfoPaymentTotalItem;
import com.tonight.manage.organization.managingmoneyapp.Object.EventListBundle;
import com.tonight.manage.organization.managingmoneyapp.Object.EventListItem;
import com.tonight.manage.organization.managingmoneyapp.Object.GroupListBundle;
import com.tonight.manage.organization.managingmoneyapp.Object.GroupListItem;
import com.tonight.manage.organization.managingmoneyapp.R;
import com.tonight.manage.organization.managingmoneyapp.AddUsageByPasteActivity;
import com.tonight.manage.organization.managingmoneyapp.RequestHandler;
import com.tonight.manage.organization.managingmoneyapp.Server.EventInfoJSONParser;
import com.tonight.manage.organization.managingmoneyapp.Server.EventJSONParser;
import com.tonight.manage.organization.managingmoneyapp.Server.GroupJSONParser;
import com.tonight.manage.organization.managingmoneyapp.Server.NetworkDefineConstant;

import java.io.IOException;
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

public class CustomSelectEventForReceivedTossSMSPopup extends DialogFragment {

    private int isSuccess;
    private String userid;
    String name;
    int money;
    int groupcount = 0;
    int currentindex = 0;
    //----
    ExpandableListAdapter expandableListAdapter;
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
        userid = pref.getString("id", "");
    }

    public static CustomSelectEventForReceivedTossSMSPopup newInstance(String name, int money) {
        CustomSelectEventForReceivedTossSMSPopup customSelectEventForReceivedTossSMSPopup = new CustomSelectEventForReceivedTossSMSPopup();
        Bundle b = new Bundle();
        b.putString("name", name);
        b.putInt("money", money);

        customSelectEventForReceivedTossSMSPopup.setArguments(b);
        return customSelectEventForReceivedTossSMSPopup;
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
        name = b.getString("name");
        money = b.getInt("money");
        ExpandableListView expListView = (ExpandableListView) view.findViewById(R.id.expandable_listview);
        //prepareListData();

        listDataHeader = new ArrayList<GroupListItem>();
        listDataChild = new HashMap<String, ArrayList<EventListItem>>();
        expandableListAdapter = new ExpandableListAdapter(getContext(), listDataHeader, listDataChild);
        expListView.setAdapter(expandableListAdapter);
        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                String groupname = listDataHeader.get(groupPosition).getGroupname();
                String eventname = listDataChild.get(listDataHeader.get(groupPosition).getGroupname()).get(childPosition).getEventname();
                String eventnum = listDataChild.get(listDataHeader.get(groupPosition).getGroupname()).get(childPosition).getEventnum();
                new LoadEventInfoAsyncTask().execute(eventname, groupname, eventnum);

                //Toast.makeText(getContext(), listDataChild.get(listDataHeader.get(groupPosition).getGroupname()).get(childPosition).getEventname(),Toast.LENGTH_SHORT).show();

                return true;
            }
        });
        new LoadGroupListAsyncTask().execute();


        return view;

    }

    @Override
    public void onStop() {
        AddPayCheckByTossNotificationActivity.activityInstance.finish();
        AddPayCheckByTossNotificationActivity.activityInstance.overridePendingTransition(0, 0);
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
            if (result.getResult() != null && result.getUserinfo() != null && result.getResult().size() > 0 && result.getUserinfo().size() > 0) {
                for (int i = 0; i < result.getResult().size(); i++) {
                    Log.e("결과", result.getResult().get(i).getGroupname() + " 결과입니다.");
                    listDataHeader.add(result.getResult().get(i));
                }
                expandableListAdapter.addAllItem(result.getResult());
                groupcount = result.getResult().size();
                for (int i = 0; i < result.getResult().size(); i++) {
                    new LoadEventListAsyncTask().execute(result.getResult().get(i).getGroupcode());
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
                        .add("userid", userid);
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
                //Log.e("result ",resBody.string());
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
            // RecyclerView Adapter Item 값 추가
            if (result.getResult() != null && result.getUserinfo() != null && result.getResult().size() > 0 && result.getUserinfo().size() > 0) {
                for (int i = 0; i < result.getResult().size(); i++) {
                    Log.e("eventlist불러오는중", result.getResult().get(i).getEventname() + " 입니다.");
                }
                listDataChild.put(listDataHeader.get(currentindex++).getGroupname(), result.getResult());
                expandableListAdapter.notifyDataSetChanged();
              /*  mEventListAdapter.addAllItem(result.getResult());
                mEventListAdapter.notifyDataSetChanged();
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

    public class LoadEventInfoAsyncTask extends AsyncTask<String, Void, EventInfoPaymentTotalItem> {

        ArrayList<EventInfoMemberPaymentListItem> eventInfoMemberItemArrayList = null;
        ArrayList<EventInfoPaymentItem> eventInfoPaymentItemArrayList = null;
        ArrayList<String> eventInfoMemberPositionArrayList = null;
        private int memberPosition = 2;
        String eventnum;
        String groupname;
        String eventname;

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
                String userid = pref.getString("id", "error");

                //연결
                OkHttpClient toServer = NetworkDefineConstant.getOkHttpClient();
                FormBody.Builder builder = new FormBody.Builder();
                //builder.add("eventnum", eventnum).add("userid", userid);
                builder.add("userid", userid).add("eventnum", eventnum).add("signal", "0");

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


            if (result != null) {
                Log.e("받아온 정보들", result.toString());
                eventInfoMemberItemArrayList = result.getEventInfoMemberItemArrayList();
                eventInfoMemberPositionArrayList = result.getEventInfoMemberPositionArrayList();
                eventInfoPaymentItemArrayList = result.getEventInfoPaymentItemArrayList();

                if (eventInfoMemberPositionArrayList.size() > 0) {
                    memberPosition = Integer.parseInt(eventInfoMemberPositionArrayList.get(0));
                    Log.e("멤버포지션", memberPosition + "?");
                    if (memberPosition == 0) {
                        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                        alert.setTitle("지불 여부 체크");
                        alert.setMessage("'" + name + "'님께서 보낸 " + money + "원을 '" + groupname + "' 그룹의 '" + eventname + "' 이벤트에 추가하시겠습니까?");
                        alert.setPositiveButton("예", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                boolean exist = false;
                                String userid = "";
                                if (eventInfoMemberItemArrayList.size() > 0) {
                                    for (int i = 0; i < eventInfoMemberItemArrayList.size(); i++) {
                                        if(name.equals(eventInfoMemberItemArrayList.get(i).getName())){
                                            exist = true;
                                            userid = eventInfoMemberItemArrayList.get(i).getUserId();
                                        }
                                    }
                                    if (exist == true) {
                                        if (money == Integer.parseInt(eventInfoPaymentItemArrayList.get(0).getPersonalMoney())) {
                                            uploadPaymentAsyncTask u = new uploadPaymentAsyncTask();
                                            u.execute(eventnum, userid);
                                        } else {
                                            Toast.makeText(getContext(), "지출해야 될 금액이 '" + eventInfoPaymentItemArrayList.get(0).getPersonalMoney() + "'원 인데, '" + money + "'원 을 추가하시려 하십니다..", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    else {
                                        Toast.makeText(getContext(),"'"+eventname+"'라는 이벤트에 '"+name+"' 라는 이름이 존재하지 않습니다.",Toast.LENGTH_SHORT).show();
                                    }
                                }

                            }
                        });
                        alert.setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                            }
                        });
                        alert.show();
                    } else {
                        Toast.makeText(getContext(), "당신은 '" + eventname + "' 이벤트의 총무가 아닙니다", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }

    public class uploadPaymentAsyncTask extends AsyncTask<String, Void, String> {

        ProgressDialog loading;
        RequestHandler handler = new RequestHandler();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = ProgressDialog.show(getActivity(), "Uploading...", null, true, true);
        }

        @Override
        protected String doInBackground(String... params) {
            String requestURL = "";
            Response response = null;
            try {
                requestURL = NetworkDefineConstant.SERVER_URL_EVENT_INFO;

                OkHttpClient toServer = NetworkDefineConstant.getOkHttpClient();
                FormBody.Builder builder = new FormBody.Builder();
                builder.add("signal", "4")
                        .add("eventnum", params[0])
                        .add("userid", params[1]);
                FormBody formBody = builder.build();

                Request request = new Request.Builder()
                        .url(requestURL)
                        .post(formBody)
                        .build();

                response = toServer.newCall(request).execute();
                boolean flag = response.isSuccessful();
                ResponseBody resBody = response.body();

                if (flag) {
                    String valid = resBody.string();
                    if (valid.contains("1"))
                        isSuccess = 1;
                    else if (valid.contains("2"))
                        isSuccess = 2;
                    else
                        isSuccess = 0;
                } else {
                    Log.e("에러", "현금 체크 에러");
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            } finally {
                if (response != null) {
                    response.close();
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(String aVoid) {
            super.onPostExecute(aVoid);
            loading.dismiss();//progressbar 종료

            if (isSuccess == 1) {
                Toast.makeText(getActivity(), "지출확인", Toast.LENGTH_SHORT).show();
                if(EventInfoActivity.pagerInstance != null) {
                    EventInfoActivity.pagerInstance.notifyDataSetChanged();
                }
                //dismiss();
            } else if (isSuccess == 2) {
                Toast.makeText(getActivity(), "목표금액이 0", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), "에러!", Toast.LENGTH_SHORT).show();
            }
            dismiss();
            AddPayCheckByTossNotificationActivity.activityInstance.finish();
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

            final String childText = getChild(groupPosition, childPosition).getEventname();

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
            if ((this._listDataChild.get(this._listDataHeader.get(groupPosition).getGroupname())) == null) {
                return 0;
            } else {
                return this._listDataChild.get(this._listDataHeader.get(groupPosition).getGroupname()).size();
            }
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
