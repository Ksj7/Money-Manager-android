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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;

/**
 * Created by 3 on 2016-12-02.
 */

public class CustomUsagePopup extends DialogFragment {
    private Bitmap receivedbitmap;
    private static Uri imageUri;

    public static final String UPLOAD_URL = "http://52.79.174.172/MAM/eventInfoActivity.php";
    public static final String UPLOAD_KEY = "image";

    LinearLayout contentLinearLayout1;
    LinearLayout contentLinearLayout2;
    LinearLayout contentLinearLayout3;

    Button confirmButton;

    int count = 1;
    private int year, month, date;
    private String locate , money;
    TextView locateText , moneyText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, R.style.CustomDialogTheme);
    }

    public static CustomUsagePopup newInstance(Uri uri) {
        imageUri = uri;
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
        moneyText = (TextView) view.findViewById(R.id.usageMoney);
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
        RequestHandler handler = new RequestHandler();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = ProgressDialog.show(getActivity(), "Uploading...", null, true, true);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            loading.dismiss();
        }

        @Override
        protected String doInBackground(Bitmap... params) {
            Bitmap bitmap = params[0];//사용자가 업로드할 이미지 비트맵
            String uploadImage = getStringImage(bitmap);

            HashMap<String, String> data = new HashMap<>();

            data.put(UPLOAD_KEY, uploadImage);
            data.put("eventnum", "4");
            data.put("title", "나도테스트할래");
            data.put("usagemoney", "20000");
            data.put("usagedate", "123");
            data.put("signal", "6");
            String result = handler.sendPostRequest(UPLOAD_URL, data);

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