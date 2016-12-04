package com.tonight.manage.organization.managingmoneyapp.Server;

import android.util.Log;

import com.tonight.manage.organization.managingmoneyapp.Object.EventListBundle;
import com.tonight.manage.organization.managingmoneyapp.Object.EventListItem;
import com.tonight.manage.organization.managingmoneyapp.Object.UserItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by sujinKim on 2016-12-02.
 */
public class EventJSONParser {

    /*
     GroupListItem 데이터 파싱
     */
    public static EventListBundle parseEventListItems(String responedJSONData) {
        ArrayList<EventListItem> eventListItemArrayList = null;
        ArrayList<UserItem> userItemArrayList = null;
        EventListBundle eventListBundle;

        JSONObject jsonRoot = null;
        JSONArray[] datas = null;

        try {
            jsonRoot = new JSONObject(responedJSONData);
            datas = new JSONArray[2];

            datas[0] = jsonRoot.getJSONArray("userinfo");
            int userInfoSize = datas[0].length();

            if(userInfoSize > 0){
                userItemArrayList = new ArrayList<>();
                for(int i = 0 ; i < userInfoSize; i++){

                    JSONObject item = datas[0].getJSONObject(i);
                    UserItem valueObject = new UserItem()
                            .setUsername(item.getString("username"))
                            .setPhone(item.getString("phone"))
                            .setProfileimg(item.getString("profileimg"));

                    userItemArrayList.add(valueObject);
                }

            }

            datas[1] = jsonRoot.getJSONArray("result");
            int  size = datas[1].length();
            if( size > 0) {
                eventListItemArrayList = new ArrayList<>();
                for (int i = 0; i < size; i++) {

                    JSONObject item = datas[1].getJSONObject(i);
                    EventListItem valueObject = new EventListItem()
                            .setMembernum(item.getString("membernum"))
                            .setEventnum(item.getString("eventnum"))
                            .setEventname(item.getString("eventname"))
                            .setTargetm(item.getString("targetm"))
                            .setSumm(item.getString("summ"));

                    eventListItemArrayList.add(valueObject);
                }
            }
        } catch (JSONException e) {
            Log.e("parseEventListItems", "Parsing error :", e);
        }
        eventListBundle = new EventListBundle(eventListItemArrayList,userItemArrayList);
        return eventListBundle;
    }
}
