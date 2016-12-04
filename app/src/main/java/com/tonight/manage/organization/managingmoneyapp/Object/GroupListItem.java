package com.tonight.manage.organization.managingmoneyapp.Object;

/**
 * Created by sujinKim on 2016-11-04.
 */

public class GroupListItem {
    String membernum;
    String groupcode;
    String groupname;
    String balance;


    public GroupListItem setGroupname(String groupname) {
        this.groupname = groupname;
        return this;
    }

    public GroupListItem setMembernum(String membernum) {
        this.membernum = membernum;
        return this;
    }
    public GroupListItem setGroupcode(String groupcode) {
        this.groupcode = groupcode;
        return this;
    }


    public GroupListItem setBalance(String balance) {
        this.balance = balance;
        return this;
    }

    public String getBalance() {
        return balance;
    }
    public String getGroupname() {
        return groupname;
    }
    public String getGroupcode() {
        return groupcode;
    }
    public String getMembernum() {
        return membernum;
    }

}
