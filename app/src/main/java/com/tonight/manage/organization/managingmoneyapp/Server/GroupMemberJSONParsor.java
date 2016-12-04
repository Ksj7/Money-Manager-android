package com.tonight.manage.organization.managingmoneyapp.Server;

import android.util.Log;

import com.tonight.manage.organization.managingmoneyapp.Object.MemberListItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by sujinKim on 2016-12-02.
 */
public class GroupMemberJSONParsor {

    /*
     GroupListItem 데이터 파싱
     */
    public static ArrayList<MemberListItem> parseMemberListItems(String responedJSONData) {
        ArrayList<MemberListItem> memberListItemArrayList = null;

        JSONObject jsonRoot = null;

        try {
            jsonRoot = new JSONObject(responedJSONData);
            JSONArray datas = jsonRoot.getJSONArray("result"); //이벤트 정보
            int  size = datas.length();
            if( size > 0) {
                memberListItemArrayList = new ArrayList<>();
                for (int i = 0; i < size; i++) {

                    JSONObject item = datas.getJSONObject(i);
                    MemberListItem valueObject = new MemberListItem()
                            .setProfileimg(item.getString("profileimg"))
                            .setPhone(item.getString("phone"))
                            .setUsername(item.getString("username"));

                    memberListItemArrayList.add(valueObject);
                }
            }

        } catch (JSONException e) {
            Log.e("parseMemberListItems", "Parsing error :", e);
        }
        return memberListItemArrayList;
    }
}
