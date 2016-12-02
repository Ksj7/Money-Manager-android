package com.tonight.manage.organization.managingmoneyapp.Object;

/**
 * Created by hooo5 on 2016-11-07.
 */

public class MemberListItem {

    String username;
    String phone;
    String profileimg;

    public MemberListItem setUsername(String username) {
        this.username = username;
        return this;
    }
    public MemberListItem setPhone(String phone) {
        this.phone = phone;
        return this;
    }
    public MemberListItem setProfileimg(String profileimg) {
        this.profileimg = profileimg;
        return this;
    }
    public String getPhone() {
        return phone;
    }

    public String getProfileimg() {
        return profileimg;
    }

    public String getUsername() {
        return username;
    }
}
