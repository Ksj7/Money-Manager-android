package com.tonight.manage.organization.managingmoneyapp.Object;

import java.util.ArrayList;

/**
 * Created by 3 on 2016-12-05.
 */

public class EventInfoPaymentTotalItem {
    ArrayList<EventInfoMemberPaymentListItem> eventInfoMemberItemArrayList ;
    ArrayList<EventInfoPaymentItem> eventInfoPaymentItemArrayList ;
    ArrayList<String> eventInfoMemberPositionArrayList;

    public EventInfoPaymentTotalItem(ArrayList<EventInfoMemberPaymentListItem> eventInfoMemberItemArrayList,
                                     ArrayList<EventInfoPaymentItem> eventInfoPaymentItemArrayList,
                                     ArrayList<String> eventInfoMemberPositionArrayList){
        this.eventInfoMemberItemArrayList = eventInfoMemberItemArrayList;
        this.eventInfoMemberPositionArrayList = eventInfoMemberPositionArrayList;
        this.eventInfoPaymentItemArrayList = eventInfoPaymentItemArrayList;
    }

    public ArrayList<EventInfoMemberPaymentListItem> getEventInfoMemberItemArrayList() {
        return eventInfoMemberItemArrayList;
    }

    public ArrayList<EventInfoPaymentItem> getEventInfoPaymentItemArrayList() {
        return eventInfoPaymentItemArrayList;
    }

    public ArrayList<String> getEventInfoMemberPositionArrayList() {
        return eventInfoMemberPositionArrayList;
    }
}
