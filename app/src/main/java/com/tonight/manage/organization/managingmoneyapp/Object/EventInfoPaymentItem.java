package com.tonight.manage.organization.managingmoneyapp.Object;

/**
 * Created by 3 on 2016-12-03.
 */

public class EventInfoPaymentItem {
    String date;
    int targetMoney;
    int collectedMoney;


    public EventInfoPaymentItem setDate(String date){
        this.date = date;
        return this;
    }

    public EventInfoPaymentItem getTargetMoney(int targetMoney){
        this.targetMoney = targetMoney;
        return this;
    }
    public EventInfoPaymentItem getColectedMondey(int collectedMoney){
        this.collectedMoney = collectedMoney;
        return this;
    }


    public String getDate(){
        return this.date;
    }
    public int getTargetMoney(){
        return this.targetMoney;
    }
    public int getCollectedMoney(){
        return this.collectedMoney;
    }


}
