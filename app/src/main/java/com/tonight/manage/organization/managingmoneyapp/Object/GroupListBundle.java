package com.tonight.manage.organization.managingmoneyapp.Object;

import java.util.ArrayList;

/**
 * Created by sujinKim on 2016-12-05.
 */

public class GroupListBundle {
    ArrayList<GroupListItem> result;
    ArrayList<UserItem> userinfo;

    public GroupListBundle(ArrayList<GroupListItem> result, ArrayList<UserItem> userinfo){
        this.result = result;
        this.userinfo = userinfo;
    }


    public GroupListBundle setUserinfo(ArrayList<UserItem> userinfo) {
        this.userinfo = userinfo;
        return this;
    }
    public GroupListBundle setResult(ArrayList<GroupListItem> result) {
        this.result = result;
        return this;
    }

    public ArrayList<UserItem> getUserinfo() {
        return userinfo;
    }

    public ArrayList<GroupListItem> getResult() {
        return result;
    }





}
