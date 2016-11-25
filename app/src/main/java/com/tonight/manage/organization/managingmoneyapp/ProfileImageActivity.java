package com.tonight.manage.organization.managingmoneyapp;

/**
 * Created by 10 on 2016-11-25.
 */

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.goka.flickableview.FlickableImageView;

public class ProfileImageActivity extends AppCompatActivity {

    private static final String TAG = ProfileImageActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_profile_image);
        final FlickableImageView flickableImageView = (FlickableImageView) findViewById(R.id.profileImage);

        // Resource
        flickableImageView.setImageResource(R.mipmap.ic_launcher);

        // Http Request
        // String url = "http://www.frenchrevolutionfood.com/wp-content/uploads/2009/04/Twitter-Bird.png";
        // Picasso.with(this).load(url).tag(TAG).into(flickableImageView);
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
       // Picasso.with(this).cancelTag(TAG);
        super.onDestroy();
    }
}
