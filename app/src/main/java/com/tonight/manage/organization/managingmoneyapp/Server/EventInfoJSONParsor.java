package com.tonight.manage.organization.managingmoneyapp.Server;

import android.util.Log;

import com.tonight.manage.organization.managingmoneyapp.Object.EventInfoMemberPaymentListItem;
import com.tonight.manage.organization.managingmoneyapp.Object.EventInfoPaymentItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by 3 on 2016-12-03.
 */

public class EventInfoJSONParsor {

    /*
     EventInfoMemberlistItem 데이터 파싱
     */
    public static ArrayList<EventInfoMemberPaymentListItem> parseEventInfoMemberItems(StringBuilder responedJSONData) {
        ArrayList<EventInfoMemberPaymentListItem> eventInfoMemberItemArrayList = null;

        JSONObject jsonRoot ;

        try {
            jsonRoot = new JSONObject(responedJSONData.toString());
           // Log.e("잘라봐 ",responedJSONData.toString()+"?????");
            JSONArray member = jsonRoot.getJSONArray("member");
            int  size = member.length();
            if( size > 0) {
                eventInfoMemberItemArrayList = new ArrayList<>();
                for (int i = 0; i < size; i++) {

                    JSONObject item = member.getJSONObject(i);
                    EventInfoMemberPaymentListItem valueObject = new EventInfoMemberPaymentListItem()
                            .setImgurl(item.getString("profileimg"))
                            .setName(item.getString("userid"))
                            .setSpendingstatus(item.getString("ispay"))
                            .setUserId(item.getString("userid"));

                    eventInfoMemberItemArrayList.add(valueObject);
                }
            }

        } catch (JSONException e) {
            Log.e("parseEventListItem", "Parsing error :", e);
        }
        return eventInfoMemberItemArrayList;
    }

    /*
    EventInfoInfoItem 데이터 파싱
    */
    public static ArrayList<EventInfoPaymentItem> parseEventInfoPaymentItems(String responedJSONData) {
        ArrayList<EventInfoPaymentItem> eventInfoPaymentItemArrayList = null;

        JSONObject jsonRoot = null;

        try {
            jsonRoot = new JSONObject(responedJSONData);
            JSONArray member = jsonRoot.getJSONArray("result");
            int  size = member.length();
            if( size > 0) {
                eventInfoPaymentItemArrayList = new ArrayList<>();
                for (int i = 0; i < size; i++) {

                    JSONObject item = member.getJSONObject(i);
                    EventInfoPaymentItem valueObject = new EventInfoPaymentItem()
                            .setDate(item.getString("eventdate"))
                            .getTargetMoney(Integer.parseInt(item.getString("targettm")))
                            .getColectedMondey(Integer.parseInt(item.getString("summ")))
                           ;

                    eventInfoPaymentItemArrayList.add(valueObject);
                }
            }

        } catch (JSONException e) {
            Log.e("parseEventInfoItem", "Parsing error :", e);
        }
        return eventInfoPaymentItemArrayList;
    }
}
