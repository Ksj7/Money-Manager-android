package com.tonight.manage.organization.managingmoneyapp.Service;

import android.app.Service;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.tonight.manage.organization.managingmoneyapp.AddUsageByPasteActivity;
import com.tonight.manage.organization.managingmoneyapp.R;

import java.io.IOException;
import java.text.SimpleDateFormat;

/**
 * Created by Taek on 2016. 12. 2..
 */

public class PasteService extends Service {

    public static final String MESSAGE_TYPE_INBOX = "1";
    public static final String MESSAGE_TYPE_SENT = "2";
    public static final String MESSAGE_TYPE_CONVERSATIONS = "3";
    public static final String MESSAGE_TYPE_NEW = "new";

    static boolean semaphore = false;//실행중일때 중복으로 스낵바 띄우는거 방지.
    ClipboardManager clipBoard;
    ClipboardListener clipboardListener;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        /*new Thread() {
            public void run() {
                while (true) {
                    try {
                        Log.e("test", "testing..");
                        sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();*/

        clipboardListener = new ClipboardListener();
        clipBoard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        clipBoard.addPrimaryClipChangedListener(clipboardListener);
        emptyClipboard(clipBoard);
        //IntentFilter filter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        //registerReceiver(mBroadcastReceiver,filter);
    }
    @Override
    public void onDestroy() {
        //unregisterReceiver(mBroadcastReceiver);
        super.onDestroy();
    }

    class ClipboardListener implements ClipboardManager.OnPrimaryClipChangedListener, View.OnClickListener {
        View mView;
        WindowManager mManager;
        Button btn;
        TextView text;
        CharSequence pasteData = "";
        WindowManager.LayoutParams mParams;
        LayoutInflater mInflater;
        int i=5;
        public void onPrimaryClipChanged() {
            if(semaphore == false) {
                semaphore = true;
                ClipData.Item item = clipBoard.getPrimaryClip().getItemAt(0);
                pasteData = item.getText();
                Log.e("data ", pasteData.toString());

                mInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                mView = mInflater.inflate(R.layout.snackbar_activity, null);
                btn = (Button) mView.findViewById(R.id.btn);
                text = (TextView) mView.findViewById(R.id.timetext);
                text.setText(i + "");
                btn.setOnClickListener(this);
                mParams = new WindowManager.LayoutParams(
                        WindowManager.LayoutParams.MATCH_PARENT,
                        WindowManager.LayoutParams.WRAP_CONTENT,
                        WindowManager.LayoutParams.TYPE_PHONE,
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                        PixelFormat.TRANSLUCENT);

                mManager = (WindowManager) getApplicationContext().getSystemService(WINDOW_SERVICE);
                mParams.gravity = Gravity.BOTTOM;
                mManager.addView(mView, mParams);

                new TimerThread().start();
            }

            SMSList();
        }//클립보드 내용이 바뀌면

        public void SMSList() {
            // Retrieve All SMS
            /*
                Inbox = "content://sms/inbox"
                Failed = "content://sms/failed"
                Queued = "content://sms/queued"
                Sent = "content://sms/sent"
                Draft = "content://sms/draft"
                Outbox = "content://sms/outbox"
                Undelivered = "content://sms/undelivered"
                All = "content://sms/all"
                Conversations = "content://sms/conversations"

                addressCol= mCurSms.getColumnIndex("address");
                personCol= mCurSms.getColumnIndex("person");
                dateCol = mCurSms.getColumnIndex("date");
                protocolCol= mCurSms.getColumnIndex("protocol");
                readCol = mCurSms.getColumnIndex("read");
                statusCol = mCurSms.getColumnIndex("status");
                typeCol = mCurSms.getColumnIndex("type");
                subjectCol = mCurSms.getColumnIndex("subject");
                bodyCol = mCurSms.getColumnIndex("body");
             */
            Uri allMessage = Uri.parse("content://sms/");
            Cursor cur = getContentResolver().query(allMessage, null, null, null, null);
            int count = cur.getCount();
            Log.e("값" , "SMS count = " + count);
            String row = "";
            String msg = "";
            String date = "";
            String protocol = "";
            while (cur.moveToNext()) {
                row = cur.getString(cur.getColumnIndex("address"));
                msg = cur.getString(cur.getColumnIndex("body"));
                date = cur.getString(cur.getColumnIndex("date"));
                protocol = cur.getString(cur.getColumnIndex("protocol"));
                // Logger.d( TAG , "SMS PROTOCOL = " + protocol);
                String FormattedDate;
  //              SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss dd-MM-yyyy");
//                FormattedDate = sdf.format(date).toString();
                String type = "";
                if (protocol == MESSAGE_TYPE_SENT) type = "sent";
                else if (protocol == MESSAGE_TYPE_INBOX) type = "receive";
                else if (protocol == MESSAGE_TYPE_CONVERSATIONS) type = "conversations";
                else if (protocol == null) type = "send";

                Log.e("결과" , "SMS Phone: " + row + " / Mesg: " + msg + " / Type: " + type + " / Date: " + date);
            }
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getApplicationContext(),AddUsageByPasteActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            if(mView != null) {
                mManager.removeView(mView);
                mView = null;
                i=5;
                semaphore = false;//클릭해서 들어왔을 때, 스레드는 종료가 되어야 함.
                emptyClipboard(clipBoard);
                //clipBoard.removePrimaryClipChangedListener(clipboardListener);
            }
        }

        class TimerThread extends Thread{
            public void run() {
                while (i > 0 && semaphore == true) {
                    try {
                        this.sleep(1000);
                        //Message m = new Message();
                        i--;
                        //m.what = i;
                        new Handler(Looper.getMainLooper()).post(new Runnable() { // new Handler and Runnable
                            @Override
                            public void run() {
                                text.setText(i + "");
                            }
                        });
                        //handler.sendMessage(m);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if (mView != null) {
                    mManager.removeView(mView);
                    mView = null;
                    i = 5;
                    if(semaphore == true){
                        Log.e("here","여기돔");
                        semaphore = false;
                        emptyClipboard(clipBoard);
                    }
                }
            }
        }
    }

    public void emptyClipboard(ClipboardManager clipBoard)//클립보드 비우는 작업.
    {
        ClipData.Item item = new ClipData.Item("");
        String[] mimeType = new String[1];
        mimeType[0] = ClipDescription.MIMETYPE_TEXT_URILIST;
        ClipData cd = new ClipData(new ClipDescription("text_data", mimeType), item);
        clipBoard.removePrimaryClipChangedListener(clipboardListener);
        clipBoard.setPrimaryClip(cd);
        clipBoard.addPrimaryClipChangedListener(clipboardListener);
    }

    public void stopservice(){
        this.stopSelf();
    }
}


