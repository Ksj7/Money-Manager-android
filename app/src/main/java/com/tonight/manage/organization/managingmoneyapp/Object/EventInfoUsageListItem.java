package com.tonight.manage.organization.managingmoneyapp.Object;

import android.net.Uri;

/**
 * Created by 3 on 2016-11-19.
 */

public class EventInfoUsageListItem {

    String receipturl;
    String usedMoney;
    String location; //titie 내역제목
    String date;

  /*  public EventInfoUsageListItem(Uri receipturl, int usedMoney, String location, String date){
        this.receipturl = receipturl;
        this.usedMoney = usedMoney;
        this.location = location;
        this.date = date;
    }
*/
    public String getReceipturl() {
        return receipturl;
    }

    public EventInfoUsageListItem setReceipturl(String receipturl) {
        this.receipturl = receipturl;
        return this;
    }

    public String getUsedMoney() {
        return usedMoney;
    }

    public EventInfoUsageListItem setUsedMoney(String usedMoney) {
        this.usedMoney = usedMoney;
        return this;
    }

    public String getLocation() {
        return location;
    }

    public EventInfoUsageListItem setLocation(String location) {
        this.location = location;
        return this;
    }

    public String getDate() {
        return date;
    }

    public EventInfoUsageListItem setDate(String date) {
        this.date = date;
        return this;
    }


}

