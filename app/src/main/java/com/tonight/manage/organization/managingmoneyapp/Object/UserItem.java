package com.tonight.manage.organization.managingmoneyapp.Object;

/**
 * Created by sujinKim on 2016-12-05.
 */

public class UserItem {

    String username;
    String phone;
    String profileimg;
    String manager;
    String balance;


    public UserItem setUsername(String username) {
        this.username = username;
        return this;
    }
    public UserItem setProfileimg(String profileimg) {
        this.profileimg = profileimg;
        return this;
    }

    public UserItem setPhone(String phone) {
        this.phone = phone;
        return this;
    }
    public UserItem setManager(String manager) {
        this.manager = manager;
        return this;
    }
    public UserItem setBalance(String balance) {
        this.balance = balance;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public String getProfileimg() {
        return profileimg;
    }

    public String getPhone() {
        return phone;
    }

    public String getManager() {
        return manager;
    }

    public String getBalance() {
        return balance;
    }


}
