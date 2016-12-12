package com.tonight.manage.organization.managingmoneyapp.Server;

import android.util.Log;

import com.tonight.manage.organization.managingmoneyapp.Object.InvitationListItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by sujinKim on 2016-12-02.
 */
public class InvitationJSONParser {

    /*
        Invitation 데이터 파싱
     */
    public static ArrayList<InvitationListItem> parseInvitationListItems(String responedJSONData) {
        ArrayList<InvitationListItem> invitationListItems = null;

        JSONObject jsonRoot = null;

        try {
            jsonRoot = new JSONObject(responedJSONData);
            JSONArray datas = jsonRoot.getJSONArray("result");
            int  size = datas.length();
            if( size > 0) {
                invitationListItems = new ArrayList<>();
                for (int i = 0; i < size; i++) {

                    JSONObject item = datas.getJSONObject(i);
                    InvitationListItem valueObject = new InvitationListItem()
                            .setProfileimg(item.getString("profileimg"))
                            .setUserid(item.getString("userid"))
                            .setUsername(item.getString("username"));

                    invitationListItems.add(valueObject);
                }
            }

        } catch (JSONException e) {
            Log.e("parse invitation", "Parsing error :", e);
        }
        return invitationListItems;
    }
}
