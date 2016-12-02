package com.tonight.manage.organization.managingmoneyapp.Server;

import android.util.Log;

import com.tonight.manage.organization.managingmoneyapp.Object.EventListItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by sujinKim on 2016-12-02.
 */
public class EventJSONParsor {

    /*
     GroupListItem 데이터 파싱
     */
    public static ArrayList<EventListItem> parseEventListItems(String responedJSONData) {
        ArrayList<EventListItem> eventListItemArrayList = null;

        JSONObject jsonRoot = null;

        try {
            jsonRoot = new JSONObject(responedJSONData);
            JSONArray datas = jsonRoot.getJSONArray("result");
            int  size = datas.length();
            if( size > 0) {
                eventListItemArrayList = new ArrayList<>();
                for (int i = 0; i < size; i++) {

                    JSONObject item = datas.getJSONObject(i);
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
            Log.e("parseEventListItem", "Parsing error :", e);
        }
        return eventListItemArrayList;
    }
}
