package com.tonight.manage.organization.managingmoneyapp.Object;

/**
 * Created by 10 on 2016-12-03.
 */

public class InvitationUpdateItem {

    String eventnum;
    String userid;

    public InvitationUpdateItem setUserid(String userid) {
        this.userid = userid;
        return this;
    }
    public InvitationUpdateItem setEventnum(String eventnum) {
        this.eventnum = eventnum;
        return this;
    }

    public String getUserid() {
        return userid;
    }
    public String getEventnum() {
        return eventnum;
    }


}
