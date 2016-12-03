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
    public static final String SERVER_URL_EVENT_LIST=
            "http://"+HOST_URL + "eventlistActivity.php";
    public static final String SERVER_URL_LOGIN=
            "http://"+HOST_URL + "loginActivity.php";
    public static final String SERVER_URL_JOIN=
            "http://"+HOST_URL + "joinActivity.php";
    public static final String SERVER_URL_CHANGES=
            "http://"+HOST_URL + "changeActivity.php";
    public static final String SERVER_URL_INVITATION =
            "http://"+HOST_URL + "invitationActivity.php";
    public static final String SERVER_URL_EVENT_INFO=
            "http://"+HOST_URL + "eventInfoActivity.php";
    public static final String SERVER_URL_UPDATE_INVITATION=
            "http://"+HOST_URL + "invitationActivityExtra.php";

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
