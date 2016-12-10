package com.tonight.manage.organization.managingmoneyapp.Service;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import com.tonight.manage.organization.managingmoneyapp.AddPayCheckByTossNotificationActivity;

/**
 * Created by Taek on 2016. 12. 10..
 */

public class NotificationService extends NotificationListenerService {
    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //좀더 보안을 강화하기 위해서 패키지 명을 추가시켜야됨.
            String notificationTitle = sbn.getNotification().extras.getCharSequence(Notification.EXTRA_TITLE).toString();
            String notificationContent = sbn.getNotification().extras.getCharSequence(Notification.EXTRA_TEXT).toString();
            SharedPreferences pref = getSharedPreferences("Login", MODE_PRIVATE);
            boolean loginok = pref.getBoolean("loginok",false);
            if(loginok) {
                if (notificationTitle.contains("TOSS")) {
                    if (notificationContent.contains("Toss 잔고로") && notificationContent.contains("보낸 사람")) {
                        String[] splitString = notificationContent.split("\\s+");//2번째에 돈 포함, 7번째에 이름포함.
                        int receiveMoney = Integer.parseInt(splitString[2].split("원")[0]);
                        String receiveName = splitString[7].substring(0, splitString[7].length() - 1);
                        Log.e("돈", receiveMoney + " 내용");
                        Log.e("이름", receiveName + " 내용");
                        Intent intent = new Intent(getApplicationContext(), AddPayCheckByTossNotificationActivity.class);
                        intent.putExtra("money", receiveMoney);
                        intent.putExtra("name", receiveName);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
                    }
                }
            }
            /*Log.i("패키지","ID :" + sbn.getId() + "t" + sbn.getNotification().tickerText + "t" + sbn.getPackageName());
            Log.e("제목", sbn.getNotification().extras.getCharSequence(Notification.EXTRA_TITLE).toString());
            Log.e("내용", sbn.getNotification().extras.getCharSequence(Notification.EXTRA_TEXT).toString());*/
        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Log.e("제목", sbn.getNotification().extras.getCharSequence(Notification.EXTRA_TITLE).toString());
            Log.e("내용", sbn.getNotification().extras.getCharSequence(Notification.EXTRA_TEXT).toString());
        }
    }
}
