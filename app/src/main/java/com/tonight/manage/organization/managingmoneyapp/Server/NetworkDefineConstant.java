package com.tonight.manage.organization.managingmoneyapp.Server;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

/**
 * Created by sujin on 2016-11-04.
 */

public class NetworkDefineConstant {
    public static final String HOST_URL = "52.79.174.172/MAM/";
    public static final String SERVER_URL_GROUP_LIST=
            "http://"+HOST_URL + "grouplistActivity.php";

    private static OkHttpClient okHttpClient;

    private NetworkDefineConstant(){
        okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .build();
    }
    public static OkHttpClient getOkHttpClient(){
        if(okHttpClient != null){
            return okHttpClient;
        }else{
            new NetworkDefineConstant();
        }
        return okHttpClient;
    }
}
