package com.tonight.manage.organization.managingmoneyapp.Custom;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tonight.manage.organization.managingmoneyapp.R;
import com.tonight.manage.organization.managingmoneyapp.RequestHandler;
import com.tonight.manage.organization.managingmoneyapp.Server.NetworkDefineConstant;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by 3 on 2016-12-02.
 */

public class CustomUsagePopup extends DialogFragment {
    private Bitmap receivedbitmap;
    private static Uri imageUri;
    private static String eventnum;
    public static final String UPLOAD_KEY = "image";

    LinearLayout contentLinearLayout1;
    LinearLayout contentLinearLayout2;
    LinearLayout contentLinearLayout3;

    Button confirmButton;

    int count = 1;
    private int year, month, date;
    private String locate , money;
    TextView locateText , moneyText;
    private boolean isSuccess;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, R.style.CustomDialogTheme);
    }

    public static CustomUsagePopup newInstance(Uri uri, String num) {
        imageUri = uri;
        eventnum = num;
        CustomUsagePopup UsagePopup = new CustomUsagePopup();
        return UsagePopup;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.popup_usage_group,container,false);
        confirmButton = (Button) view.findViewById(R.id.confirmBtn);
        contentLinearLayout1 = (LinearLayout) view.findViewById(R.id.contentLinearLayout1);
        contentLinearLayout2 = (LinearLayout) view.findViewById(R.id.contentLinearLayout2);
        contentLinearLayout3 = (LinearLayout) view.findViewById(R.id.contentLinearLayout3);
        contentLinearLayout2.setVisibility(View.GONE);
        contentLinearLayout3.setVisibility(View.GONE);

        CalendarView calendarView = (CalendarView) view.findViewById(R.id.calendar);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView calendarView, int i, int i1, int i2) {
                java.util.Calendar curDate = java.util.Calendar.getInstance();
                curDate.setTimeInMillis(calendarView.getDate());

                year = curDate.get(java.util.Calendar.YEAR);
                month = 1 + curDate.get(java.util.Calendar.MONTH);
                date = curDate.get(java.util.Calendar.DATE);

                Toast.makeText(getActivity(), "선택한 날짜 : " + year + "/" + month + "/" + date, Toast.LENGTH_SHORT).show();
            }
        });

        locateText = (TextView) view.findViewById(R.id.usageLocate_edit);
        moneyText = (TextView) view.findViewById(R.id.usageMoney_edit);
        //서버에 사용내역 리스트 보내줌
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(count == 1){
                    contentLinearLayout1.setVisibility(View.GONE);
                    contentLinearLayout2.setVisibility(View.VISIBLE);
                    contentLinearLayout3.setVisibility(View.GONE);
                }else if(count == 2){
                    locate = locateText.getText().toString();
                    contentLinearLayout1.setVisibility(View.GONE);
                    contentLinearLayout2.setVisibility(View.GONE);
                    contentLinearLayout3.setVisibility(View.VISIBLE);
                    confirmButton.setText("OK");
                }else if(count == 3){
                    money = moneyText.getText().toString();

                    try {
                        receivedbitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
                        receivedbitmap = Bitmap.createScaledBitmap(receivedbitmap, 200, 200, false);

                        UploadImage uploadImage = new UploadImage();
                        uploadImage.execute(receivedbitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    dismiss();
                }
                count++;
            }

        });

        return view;

    }

    @Override
    public void onStop() {
        super.onStop();
        dismiss();
    }

    class UploadImage extends AsyncTask<Bitmap, Void, String> {

        ProgressDialog loading;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = ProgressDialog.show(getActivity(), "Uploading...", null, true, true);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (isSuccess) {
               // Toast.makeText(getActivity(), "사용내역이 업로드되었습니다.", Toast.LENGTH_SHORT).show();
                Log.e("사용내역이 업로드되었습니다.","isSuccess is true"+isSuccess);
            } else {
               // Toast.makeText(getActivity(), "에러!", Toast.LENGTH_SHORT).show();
                Log.e("에러","isSuccess is false"+isSuccess);

            }
            loading.dismiss();
        }

        @Override
        protected String doInBackground(Bitmap... params) {
            String requestURL = "";
            Response response = null;
            try {
                requestURL = NetworkDefineConstant.SERVER_URL_EVENT_INFO;

                OkHttpClient toServer = NetworkDefineConstant.getOkHttpClient();
                FormBody.Builder builder = new FormBody.Builder();

                Bitmap bitmap = params[0];//사용자가 업로드할 이미지 비트맵
                String uploadImage = getStringImage(bitmap);
                Log.e("업로드한값","타이틀 "+locate+",이벤트넘"+eventnum+",돈이랑 날짜"+money+","+year + "/" + month + "/" + date);
                builder.add("signal", "6")
                        .add("eventnum", eventnum)
                        .add("title", locate)
                        .add("usagemoney", money)
                        .add("usagedate", year + "/" + month + "/" + date)
                        .add(UPLOAD_KEY, uploadImage);

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
                        isSuccess = true;
                    else
                        isSuccess = false;

                } else {
                    Log.e("에러", "업로드 에러");
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

        public String getStringImage(Bitmap bmp) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] imageBytes = baos.toByteArray();
            String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
            return encodedImage;
        }
    }

}