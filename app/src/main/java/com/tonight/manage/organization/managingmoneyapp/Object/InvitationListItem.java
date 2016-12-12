package com.tonight.manage.organization.managingmoneyapp.Object;

/**
 * Created by hooo5 on 2016-11-06.
 */

public class InvitationListItem {
    String userid;
    String username;
    String profileimg;

    public InvitationListItem setProfileimg(String profileimg) {
        this.profileimg = profileimg;
        return this;
    }
    public InvitationListItem setUserid(String userid) {
        this.userid = userid;
        return this;
    }
    public InvitationListItem setUsername(String username) {
        this.username = username;
        return this;
    }
    public String getUsername() {
        return username;
    }
    public String getProfileimg() {
        return profileimg;
    }
    public String getUserid() {
        return userid;
    }


}
