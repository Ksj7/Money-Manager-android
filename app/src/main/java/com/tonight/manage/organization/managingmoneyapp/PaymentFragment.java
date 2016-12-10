package com.tonight.manage.organization.managingmoneyapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.tonight.manage.organization.managingmoneyapp.Builder.ProductExcel;
import com.tonight.manage.organization.managingmoneyapp.Custom.CustomAddMoneyPopup;
import com.tonight.manage.organization.managingmoneyapp.Custom.CustomCheckCashPopup;
import com.tonight.manage.organization.managingmoneyapp.Custom.CustomProfilePopup;
import com.tonight.manage.organization.managingmoneyapp.Custom.CustomSetDatePopup;
import com.tonight.manage.organization.managingmoneyapp.Object.EventInfoMemberPaymentListItem;
import com.tonight.manage.organization.managingmoneyapp.Object.EventInfoPaymentItem;
import com.tonight.manage.organization.managingmoneyapp.Object.EventInfoPaymentTotalItem;
import com.tonight.manage.organization.managingmoneyapp.Object.MemberListItem;
import com.tonight.manage.organization.managingmoneyapp.Server.EventInfoJSONParser;
import com.tonight.manage.organization.managingmoneyapp.Server.NetworkDefineConstant;
import com.tonight.manage.organization.managingmoneyapp.Toss.TossConstants;
import com.tonight.manage.organization.managingmoneyapp.Toss.TossUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import jxl.write.WriteException;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by 3 on 2016-11-14.
 */

public class PaymentFragment extends Fragment {

    private RecyclerView mPaymentListRecyclerView;
    private EventInfoPaymentAdapter mPaymentListAdapter;
    private SwipeRefreshLayout mPaymentListSwipeRefreshLayout;
    // private ScrollView mPaymentListScrollView;
    PopupMenu popup;
    private ImageButton mAddButton;

    //Toss TEST API Key
    private static final String API_KEY = "sk_test_apikey1234567890a";

    private ProgressDialog mProgressDialog;
    String eventnum;
    TextView mEventDate;
    TextView mTargetMoney;
    TextView mCollectedMoney;
    TextView mManagerName;

    private int memberPosition = 2; //초기값은 초대되어 있지 않은 회원
    LinearLayout eventInfo_payment_userInfo;
    ImageView myProfileImage;
    TextView myUserName;
    TextView myMoney;
    TextView myStatus;
    ImageView mManagerProfile;

    private ArrayList<EventInfoMemberPaymentListItem> eventInfoMemberItemArrayList;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_event_info_payment, container, false);
        Bundle b = getArguments();
        if (b == null) {
            Toast.makeText(getActivity(), "데이터를 가져오는 중에 오류가 발생했습니다. 다시 실행해 주세요 ◕ˇoˇ◕", Toast.LENGTH_SHORT).show();
            return v;
        }
        final String fileName = b.getString("eventName");
        eventnum = b.getString("eventnum");

        mPaymentListRecyclerView = (RecyclerView) v.findViewById(R.id.eventInfo_recyclerView);
        mPaymentListRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mPaymentListRecyclerView.setNestedScrollingEnabled(true);//scrollview를 위해
        // mPaymentListRecyclerView.setLayoutManager(new WrappingLinearLayoutManager(getContext()));//
        //mPaymentListRecyclerView.setHasFixedSize(false);

        mPaymentListRecyclerView.setHasFixedSize(true);
        mPaymentListAdapter = new EventInfoPaymentAdapter(getActivity());
        mPaymentListRecyclerView.setAdapter(mPaymentListAdapter);

      /*  mPaymentListRecyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                mPaymentListScrollView.requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });*/

        mPaymentListSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.eventInfo_payment_swipeRefreshLayout);
        mPaymentListSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                new loadPaymentAsyncTask().execute();
                mPaymentListSwipeRefreshLayout.setRefreshing(false);
            }
        });

        mAddButton = (ImageButton) v.findViewById(R.id.eventInfo_addmoney_btn);
        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(getActivity(), mAddButton);
                popup.getMenuInflater().inflate(R.menu.activity_eventinfo_popup, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int id = item.getItemId();
                        if (id == R.id.setdate) {
                            CustomSetDatePopup setDatePopup = CustomSetDatePopup.newInstance(eventnum);
                            setDatePopup.show(getFragmentManager(), "setDate");
                        } else {
                            CustomAddMoneyPopup addMoneyPopup = CustomAddMoneyPopup.newInstance(eventnum);
                            addMoneyPopup.show(getFragmentManager(), "addMoney");
                        }
                        return false;
                    }
                });
                popup.show();
            }
        });

        v.findViewById(R.id.eventInfo_userPayState).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (API_KEY.contains("API KEY")) {
                    throw new IllegalStateException("API KEY를 올바르게 입력해주세요");
                }
                new TossRequestAsyncTask(generatePaymentParams()).execute(TossConstants.PAYMENT_API_URL);
            }
        });

        Button mExportButton = (Button) v.findViewById(R.id.exportBtn);
        mExportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/AMM";
                try {
                    //path 부분엔 파일 경로를 지정해주세요.
                    File excel = new File(filePath + "/" + fileName + ".xls");
                    //파일 유무를 확인합니다.
                    new ProductExcel.ExcelBuilder(fileName, eventInfoMemberItemArrayList).build();
                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("message/rfc822");
                    i.putExtra(Intent.EXTRA_SUBJECT, "AMM_EXPORT_EXCEL_FILE");
                    i.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(excel));

                    startActivity(Intent.createChooser(i, "Send mail..."));

                } catch (IOException | WriteException e) {
                    e.printStackTrace();
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(getActivity(), "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mEventDate = (TextView) v.findViewById(R.id.eventInfo_date);
        mTargetMoney = (TextView) v.findViewById(R.id.eventInfo_targetmoney);
        mCollectedMoney = (TextView) v.findViewById(R.id.eventInfo_collectedmoney);
        mManagerName = (TextView) v.findViewById(R.id.eventInfo_managerName);
        mManagerProfile = (ImageView) v.findViewById(R.id.eventInfo_myManagerImageView);
        eventInfo_payment_userInfo = (LinearLayout) v.findViewById(R.id.eventInfo_payment_userInfo);
        myProfileImage = (ImageView) v.findViewById(R.id.eventInfo_myImageView);
        myUserName = (TextView) v.findViewById(R.id.eventInfo_myuserName);
        myMoney = (TextView) v.findViewById(R.id.eventInfo_userMoney);
        myStatus = (TextView) v.findViewById(R.id.eventInfo_userPayState);


        new loadPaymentAsyncTask().execute();
        return v;
    }


    class EventInfoPaymentAdapter extends RecyclerView.Adapter<EventInfoPaymentAdapter.ViewHolder> {

        private LayoutInflater mLayoutInflater;
        private ArrayList<EventInfoMemberPaymentListItem> paymentArrayList; // member list
        private Context mContext;

        public EventInfoPaymentAdapter(Context context) {
            mContext = context;
            mLayoutInflater = LayoutInflater.from(context);
            paymentArrayList = new ArrayList<>();
        }

        public void addItem(ArrayList<EventInfoMemberPaymentListItem> datas) {
            this.paymentArrayList = datas;
        }


        @Override
        public EventInfoPaymentAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            return new EventInfoPaymentAdapter.ViewHolder(mLayoutInflater.inflate(R.layout.activity_event_info_payment_listitem, parent, false));
        }

        @Override
        public void onBindViewHolder(final EventInfoPaymentAdapter.ViewHolder holder, final int position) {

            //test
       /*     holder.useName.setText("test text");
            holder.paymentMoney.setText("0");
            holder.payStatus.setText("미지출");
            holder.view.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    // TODO 지불 내역 변경 팝업창 생성 코드 추가
                }
            });*/
            //이게 정상
            holder.useName.setText(paymentArrayList.get(position).getName());
            holder.paymentMoney.setText(paymentArrayList.get(position).getPersonalMoney());
            Log.e("여기 리스트", paymentArrayList.get(position).getSpendingstatus() + "?");
            if (paymentArrayList.get(position).getSpendingstatus().equals("1")) {
                holder.payStatus.setText("지출완료");
            }
            Glide.with(getActivity().getApplicationContext())
                    .load(paymentArrayList.get(position).getImgurl())
                    .override(150, 150)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(holder.profileimg);

            if (memberPosition == 0) {//총무이면 지불내역 변경 팝업창 생성 리스너
                Log.e("총무니?", memberPosition + "ehlsehlsehlshelslhel");
                holder.payStatus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (holder.payStatus.getText().toString().equals("미지출")) {
                            CustomCheckCashPopup checkcashpopup = CustomCheckCashPopup.newInstance(
                                    paymentArrayList.get(position).getName().toString(),
                                    paymentArrayList.get(position).getUserId().toString(),
                                    eventnum);
                            checkcashpopup.show(getActivity().getSupportFragmentManager(), "check_cash_popup");
                        } else {
                            Toast.makeText(getActivity(), "이미 지출하셨습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } else {
                Log.e("여기도니?", memberPosition + "ehlsehlsehlshelslhel");
            }

            holder.profileimg.setOnClickListener(new View.OnClickListener() {//프로필 확인 팝업창
                @Override
                public void onClick(View view) {
                    CustomProfilePopup customProfilePopup = CustomProfilePopup.newInstance(
                            new MemberListItem().setUsername(holder.useName.getText().toString())
                                    .setPhone(paymentArrayList.get(position).getUserphone())
                                    .setProfileimg(paymentArrayList.get(position).getImgurl()));
                    customProfilePopup.show(getActivity().getSupportFragmentManager(), "profile");
                }
            });
        }

        @Override
        public int getItemCount() {
            //test
            //return 7;

            //이게 원래 정상
            return paymentArrayList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView useName;
            TextView paymentMoney;
            TextView payStatus;
            RecyclerView recyclerView;
            View view;
            ImageView profileimg;

            public ViewHolder(View v) {
                super(v);
                view = v;
                useName = (TextView) v.findViewById(R.id.eventInfo_payment_username);
                paymentMoney = (TextView) v.findViewById(R.id.eventInfo_payment_money);
                payStatus = (TextView) v.findViewById(R.id.eventInfo_payment_payState);
                recyclerView = (RecyclerView) v.findViewById(R.id.eventInfo_recyclerView);
                profileimg = (ImageView) v.findViewById(R.id.eventInfo_payment_ImgView);
            }
        }
    }

    /**
     * 결제 생성 API 요청에 필요한 파라미터들을 설정합니다.
     * 파라미터에 대한 자세한 정보는 https://toss.im/tosspay/developer/apidoc#charge 에서 확인 할 수 있습니다..
     *
     * @return 테스트 결제용으로 10,000원짜리 상품에 대한 파라미터 {@link JSONObject}
     */
    private JSONObject generatePaymentParams() {
        JSONObject params = new JSONObject();
        try {
            //필수 항목
            params.put(TossConstants.PARAM_API_KEY, API_KEY);
            //필수 항목, 테스트 용도로 매번 다른 주문번호를 생성하도록 함
            params.put(TossConstants.PARAM_ORDER_NO, System.currentTimeMillis());
            //필수 항목
            params.put(TossConstants.PARAM_AMOUNT, 10000);
            //필수 항목
            params.put(TossConstants.PARAM_PRODUCT_DESC, "테스트 결제");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return params;
    }

    /**
     * 결제 건이 성공적으로 생성되면 토스 앱을 실행하거나, 사용자 정보를 입력받는 페이지를 통해 주문을 완료합니다.
     *
     * @param payToken 결제 시 사용되는 결제 Token
     */
    private void payWithToss(String payToken) {
        if (TossUtils.isTossInstalled(getActivity())) {
            TossUtils.launchForPayment(getActivity(), payToken);
        } else {
            TossUtils.goToPlayStore(getActivity());
        }
    }

    /**
     * 결제 API 서버에 결제 생성을 요청하고, 성공 시 결제를 시도합니다.
     */
    private class TossRequestAsyncTask extends AsyncTask<String, Void, String> {

        private JSONObject params;

        public TossRequestAsyncTask(JSONObject params) {
            this.params = params;
        }

        public String readSomething(InputStream in) throws IOException {
            StringBuilder content = new StringBuilder();
            String line;
            BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            while ((line = br.readLine()) != null) {
                content.append(line);
            }
            return content.toString();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (mProgressDialog == null) {
                mProgressDialog = new ProgressDialog(getActivity());
                mProgressDialog.setCancelable(false);
                mProgressDialog.setMessage("서버와 통신중입니다.");
            }
            mProgressDialog.show();
        }

        @Override
        protected String doInBackground(String... urls) {
            InputStream is = null;
            OutputStream os = null;

            try {
                URL url = new URL(urls[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.addRequestProperty("Content-Type", "application/json");
                conn.setReadTimeout(10000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setDoInput(true);

                os = conn.getOutputStream();
                System.out.print(params.toString().getBytes() + "***\n");
                os.write(params.toString().getBytes());
                os.flush();
                int response = conn.getResponseCode();
                is = conn.getInputStream();

                String contentAsString = readSomething(is);
                return contentAsString;
            } catch (IOException e) {
                e.printStackTrace();
                return "Error : Unable to retrieve web page. URL may be invalid.";
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                if (os != null) {
                    try {
                        os.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }

            //서버에 요청이 실패하였을 때
            if (result.contains("Error")) {
                Toast.makeText(getActivity(), result, Toast.LENGTH_SHORT).show();
                return;
            }

            //서버에서 응답이 온 경우
            try {
                JSONObject response = new JSONObject(result);
                int resultCode = response.optInt(TossConstants.PARAM_RESULT_CODE, TossConstants.RESULT_FAILED);
                switch (resultCode) {
                    case TossConstants.RESULT_SUCCEED:
                        //결제 건이 성공적으로 생성된 경우, 결제 요청을 합니다.
                        payWithToss(response.optString(TossConstants.PARAM_PAY_TOKEN));
                        break;
                    case TossConstants.RESULT_FAILED:
                    case TossConstants.RESULT_FAILED_ORDER_DUPLICATED:
                    case TossConstants.RESULT_FAILED_EXCEED_LIMIT:
                        //실패 사유를 Toast로 보여줍니다.
                        String msg = response.optString(TossConstants.PARAM_MSG);
                        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
                        break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    //evnetinfo list 가져오기 위한 Thread
    public class loadPaymentAsyncTask extends AsyncTask<String, Void, EventInfoPaymentTotalItem> {
        @Override
        protected EventInfoPaymentTotalItem doInBackground(String... arg) {
            String requestURL = "";
            Response response = null;
            try {

                requestURL = NetworkDefineConstant.SERVER_URL_EVENT_INFO;

                SharedPreferences pref = getActivity().getSharedPreferences("Login", MODE_PRIVATE);
                String userid = pref.getString("id", "error");

                //연결
                OkHttpClient toServer = NetworkDefineConstant.getOkHttpClient();
                FormBody.Builder builder = new FormBody.Builder();
                //builder.add("eventnum", eventnum).add("userid", userid);
                builder.add("userid", userid).add("eventnum", eventnum).add("signal", "0");
                //Log.e("???????????? ",eventnum+","+userid);
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
            eventInfoMemberItemArrayList = null;
            ArrayList<EventInfoPaymentItem> eventInfoPaymentItemArrayList = null;
            ArrayList<String> eventInfoMemberPositionArrayList = null;


            if (result != null) {
                //Log.e("받아온 정보들", result.toString());
                eventInfoMemberItemArrayList = result.getEventInfoMemberItemArrayList();
                eventInfoMemberPositionArrayList = result.getEventInfoMemberPositionArrayList();
                eventInfoPaymentItemArrayList = result.getEventInfoPaymentItemArrayList();

                if (eventInfoMemberPositionArrayList.size() > 0) {
                    memberPosition = Integer.parseInt(eventInfoMemberPositionArrayList.get(0));
                    //Log.e("멤버포지션", memberPosition + "?");
                    if (memberPosition == 2) {//가입되어 있지 않은 회원이라면 자신의 정보 안보임
                        eventInfo_payment_userInfo.setVisibility(View.GONE);
                    }
                }

                if (eventInfoPaymentItemArrayList.size() > 0) {//이벤트 정보
                    mTargetMoney.setText(eventInfoPaymentItemArrayList.get(0).getTargetMoney());//목표액
                    mCollectedMoney.setText(eventInfoPaymentItemArrayList.get(0).getCollectedMoney());//모인 금액
                    mEventDate.setText(eventInfoPaymentItemArrayList.get(0).getEventDate());//기한
                    mManagerName.setText(eventInfoPaymentItemArrayList.get(0).getManagerId());//총무 이름
                    //총무 프로필
                    myUserName.setText(eventInfoPaymentItemArrayList.get(0).getUserName());//유저 이름
                    myMoney.setText(eventInfoPaymentItemArrayList.get(0).getPersonalMoney()); //유저 금액
                    mManagerName.setText((eventInfoPaymentItemArrayList.get(0).getManagerName()));
                    if (eventInfoPaymentItemArrayList.get(0).getUserIspay().equals("1")) {
                        myStatus.setText("지출완료");//유저 상태
                    }
                    Glide.with(getActivity().getApplicationContext())
                            .load(eventInfoPaymentItemArrayList.get(0).getUserprofileURL())
                            .override(150, 150)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .into(myProfileImage); // 유저 프로필
                    Glide.with(getActivity().getApplicationContext())
                            .load(eventInfoPaymentItemArrayList.get(0).getManagerProfileimg())
                            .override(150, 150)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .into(mManagerProfile); // 메니저 프로필

                }


                if (eventInfoMemberItemArrayList.size() > 0) {//멤버 리스트
                    SharedPreferences pref = getActivity().getSharedPreferences("Login", MODE_PRIVATE);
                    String userid = pref.getString("id", "error");

                    if (memberPosition != 2) {
                        for (int i = 0; i < eventInfoMemberItemArrayList.size(); i++) {
                            if (eventInfoMemberItemArrayList.get(i).getUserId().equals(userid)) {//내 아이디 있으면
                                eventInfoMemberItemArrayList.remove(i);
                                break;
                            }
                        }
                    }

                    mPaymentListAdapter.addItem(eventInfoMemberItemArrayList);
                    mPaymentListAdapter.notifyDataSetChanged();
                }

            }
        }
    }


}