package com.tonight.manage.organization.managingmoneyapp.Server;

import android.util.Log;

import com.tonight.manage.organization.managingmoneyapp.Object.EventInfoMemberPaymentListItem;
import com.tonight.manage.organization.managingmoneyapp.Object.EventInfoPaymentItem;
import com.tonight.manage.organization.managingmoneyapp.Object.EventInfoPaymentTotalItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by 3 on 2016-12-03.
 */

public class EventInfoJSONParser {

    /*
     EventInfoMemberlistItem 데이터 파싱
     */
    public static EventInfoPaymentTotalItem parseEventInfoMemberItems(StringBuilder responedJSONData) {
        ArrayList<EventInfoMemberPaymentListItem> eventInfoMemberItemArrayList = null;
        ArrayList<EventInfoPaymentItem> eventInfoPaymentItemArrayList = null;
        ArrayList<String> eventInfoMemberPositionArrayList = null;
        JSONObject jsonRoot ;
        JSONArray member;
        JSONArray result;
        JSONArray memberposition;

        try {
            Log.e("받아온 정보들 : ",responedJSONData.toString()+"여기");
            jsonRoot = new JSONObject(responedJSONData.toString());
            member = jsonRoot.getJSONArray("member");
            result = jsonRoot.getJSONArray("result");
            memberposition = jsonRoot.getJSONArray("memberposition");

            int  membersize = member.length();
            int  resultsize = result.length();
            int  memberpositionsize = memberposition.length();

            if( membersize > 0) {
                eventInfoMemberItemArrayList = new ArrayList<>();
                for (int i = 0; i < membersize; i++) {

                    JSONObject item = member.getJSONObject(i);
                    EventInfoMemberPaymentListItem memberObject = new EventInfoMemberPaymentListItem()
                            .setImgurl(item.getString("profileimg"))
                            .setName(item.getString("userid"))
                            .setSpendingstatus(item.getString("ispay"))
                            .setUserId(item.getString("userid"))
                            .setPersonalMoney(item.getString("personalm"))
                            .setUserphone(item.getString("phone"));

                    eventInfoMemberItemArrayList.add(memberObject);
                }
            }

            if( resultsize > 0) {
                eventInfoPaymentItemArrayList = new ArrayList<>();
                for (int i = 0; i < resultsize; i++) {

                    JSONObject item = result.getJSONObject(i);
                    EventInfoPaymentItem resultObject = new EventInfoPaymentItem()
                            .setAccount(item.getString("account"))
                            .setBank(item.getString("bank"))
                            .setBalance(item.getString("balance"))
                            .setColectedMondey(item.getString("summ"))
                            .setDate(item.getString("eventdate"))
                            .setManagerId(item.getString("managerid"))
                            .setPersonalMoney(item.getString("personalm"))
                            .setName(item.getString("eventname"))
                            .setTargetMoney(item.getString("targetm"))
                            .setUserName(item.getString("username"))
                            .setUserIspay(item.getString("ispay"))
                            .setUserprofileURL(item.getString("profileimg"));

                    eventInfoPaymentItemArrayList.add(resultObject);
                }
            }

            if( memberpositionsize > 0) {
                eventInfoMemberPositionArrayList = new ArrayList<>();
                for (int i = 0; i < memberpositionsize; i++) {
                    JSONObject item = memberposition.getJSONObject(i);
                    eventInfoMemberPositionArrayList.add(item.getString("position"));
                }
            }

        } catch (JSONException e) {
            Log.e("parsePaymentItem", "Parsing error :", e);
        }

        EventInfoPaymentTotalItem totalItem = new EventInfoPaymentTotalItem(eventInfoMemberItemArrayList,eventInfoPaymentItemArrayList,eventInfoMemberPositionArrayList);
        return totalItem;
    }

}