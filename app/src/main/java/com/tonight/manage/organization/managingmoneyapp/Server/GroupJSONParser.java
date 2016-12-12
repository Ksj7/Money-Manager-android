package com.tonight.manage.organization.managingmoneyapp.Server;

import android.util.Log;

import com.tonight.manage.organization.managingmoneyapp.Object.GroupListBundle;
import com.tonight.manage.organization.managingmoneyapp.Object.GroupListItem;
import com.tonight.manage.organization.managingmoneyapp.Object.UserItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by sujinKim on 2016-12-02.
 */
public class GroupJSONParser {

    /*
     GroupListItem 데이터 파싱
     */
    public static GroupListBundle parseGroupListItems(String responedJSONData) {
        ArrayList<GroupListItem> groupListItemArrayList = null;
        ArrayList<UserItem> userItemArrayList = null;
        GroupListBundle groupListBundle;

        JSONObject jsonRoot = null;
        JSONArray[] datas = null;

        try {
            String errorCheck = responedJSONData;
            if(errorCheck.length()<=2) return null;
            jsonRoot = new JSONObject(errorCheck);
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
                groupListItemArrayList = new ArrayList<>();
                for (int i = 0; i < size; i++) {

                    JSONObject item = datas[1].getJSONObject(i);
                    GroupListItem valueObject = new GroupListItem()
                            .setMembernum(item.getString("membernum"))
                            .setGroupcode(item.getString("groupcode"))
                            .setGroupname(item.getString("groupname"))
                            .setBubblecount(item.getString("bubblecount"))
                            .setBalance(item.getString("balance"))
                            .setBank(item.getString("bank"))
                            .setAccount(item.getString("account"));

                    groupListItemArrayList.add(valueObject);
                }
            }
        } catch (JSONException e) {
            Log.e("parseGroupListItems", "Parsing error :", e);
        }
        groupListBundle = new GroupListBundle(groupListItemArrayList,userItemArrayList);
        return groupListBundle;
    }
}