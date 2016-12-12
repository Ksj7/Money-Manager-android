package com.tonight.manage.organization.managingmoneyapp.Object;

import java.util.ArrayList;

/**
 * Created by sujinKim on 2016-12-05.
 */

public class EventListBundle {

    ArrayList<UserItem> userinfo;
    ArrayList<EventListItem> result;

    public EventListBundle(ArrayList<EventListItem> result, ArrayList<UserItem> userinfo){
        this.result = result;
        this.userinfo = userinfo;
    }

    public EventListBundle setUserinfo(ArrayList<UserItem> userinfo) {
        this.userinfo = userinfo;
        return this;
    }
    public EventListBundle setResult(ArrayList<EventListItem> result) {
        this.result = result;
        return this;
    }

    public ArrayList<UserItem> getUserinfo() {
        return userinfo;
    }
    public ArrayList<EventListItem> getResult() {
        return result;
    }

}
