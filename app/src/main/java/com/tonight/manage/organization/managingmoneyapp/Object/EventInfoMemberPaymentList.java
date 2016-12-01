package com.tonight.manage.organization.managingmoneyapp.Object;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by 3 on 2016-11-18.
 */

public class EventInfoMemberPaymentList implements Serializable {
    public ArrayList<EventInfoMemberPaymentListItem> data;

    public EventInfoMemberPaymentList(ArrayList<EventInfoMemberPaymentListItem> data){
        this.data = data;
    }

}
