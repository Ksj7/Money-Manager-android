package com.tonight.manage.organization.managingmoneyapp;

/**
 * Created by 10 on 2016-11-25.
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;

import com.bumptech.glide.Glide;
import com.goka.flickableview.FlickableImageView;

public class ProfileImageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_profile_image);
        Intent i = getIntent();
        String url = i.getStringExtra("profile");
        final FlickableImageView flickableImageView = (FlickableImageView) findViewById(R.id.profileImage);
        DisplayMetrics dm = getApplicationContext().getResources().getDisplayMetrics();
        int width = dm.widthPixels;
        int height = dm.heightPixels;

        Glide.with(getApplicationContext())
                .load(url)
                .override(width, height)
                .into(flickableImageView);
        flickableImageView.setOnFlickListener(new FlickableImageView.OnFlickableImageViewFlickListener() {
            @Override
            public void onStartFlick() {

            }

            @Override
            public void onFinishFlick() {
                flickableImageView.setVisibility(View.GONE);
                finish();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
