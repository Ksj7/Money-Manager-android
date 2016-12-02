package com.tonight.manage.organization.managingmoneyapp.Server;

import android.util.Log;

import com.tonight.manage.organization.managingmoneyapp.Object.GroupListItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by sujinKim on 2016-12-02.
 */
public class GroupJSONParsor {

    /*
     GroupListItem 데이터 파싱
     */
    public static ArrayList<GroupListItem> parseGroupListItems(String responedJSONData) {
        ArrayList<GroupListItem> groupListItemArrayList = null;

        JSONObject jsonRoot = null;

        try {
            jsonRoot = new JSONObject(responedJSONData);
            JSONArray datas = jsonRoot.getJSONArray("result");
            int  size = datas.length();
            if( size > 0) {
                groupListItemArrayList = new ArrayList<>();
                for (int i = 0; i < size; i++) {

                    JSONObject item = datas.getJSONObject(i);
                    GroupListItem valueObject = new GroupListItem()
                            .setMembernum(item.getString("membernum"))
                            .setGroupcode(item.getString("groupcode"))
                            .setGroupname(item.getString("groupname"))
                            .setBalance(item.getString("balance"));

                    groupListItemArrayList.add(valueObject);
                }
            }

        } catch (JSONException e) {
            Log.e("parseGroupListItems", "Parsing error :", e);
        }
        return groupListItemArrayList;
    }
}
