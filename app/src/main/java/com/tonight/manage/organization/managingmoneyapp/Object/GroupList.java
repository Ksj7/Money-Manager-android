package com.tonight.manage.organization.managingmoneyapp.Object;

import java.util.ArrayList;

/**
 * Created by sujinKim on 2016-11-04.
 */

public class GroupList {
    int total;
    public ArrayList<GroupListItem> data;


    GroupList(ArrayList<GroupListItem> data) {
        this.data = data;
    }

}
