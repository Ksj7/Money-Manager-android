package com.tonight.manage.organization.managingmoneyapp.Custom;

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
import android.widget.Toast;

import android.app.ProgressDialog;

import com.tonight.manage.organization.managingmoneyapp.R;
import com.tonight.manage.organization.managingmoneyapp.RequestHandler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;

/**
 * Created by 3 on 2016-12-02.
 */

public class CustomUsagePopup extends DialogFragment {
    private Bitmap loadedbitmap;
    private static Uri imageUri;

    public static final String UPLOAD_URL = "http://52.79.174.172/MAM/eventInfoActivity.php";
    public static final String UPLOAD_KEY = "image";


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
        Button Ybtn = (Button) view.findViewById(R.id.confirmBtn);
        Button NBtn = (Button) view.findViewById(R.id.cancelBtn);
        Ybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //서버에 사용내역 리스트 보내줌
                try {
                    loadedbitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);

                    UploadImage uploadImage = new UploadImage();
                    uploadImage.execute(loadedbitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                dismiss();
            }

        });
        NBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }

        });

        return view;

    }

    @Override
    public void onStop() {
        super.onStop();
        dismiss();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        int width = getResources().getDimensionPixelSize(R.dimen.popup_usage_width);
        int height = getResources().getDimensionPixelSize(R.dimen.popup_usage_height);
        getDialog().getWindow().setLayout(width,height);
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
            Toast.makeText(getContext(), s, Toast.LENGTH_LONG).show();
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
            data.put("image",uploadImage);
            //data.put("userid", "jun");
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