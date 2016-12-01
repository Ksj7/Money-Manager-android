package com.tonight.manage.organization.managingmoneyapp.Object;

import android.net.Uri;

/**
 * Created by 3 on 2016-11-19.
 */

public class EventInfoUsageListItem {
    Uri receipturl;
    int usedMoney;
    String location;
    String date;

    public EventInfoUsageListItem(Uri receipturl, int usedMoney, String location, String date){
        this.receipturl = receipturl;
        this.usedMoney = usedMoney;
        this.location = location;
        this.date = date;
    }
}
