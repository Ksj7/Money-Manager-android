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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Taek on 2016. 12. 2..
 */

public class PasteService extends Service {

    public static String nong = "15881600";//농협
    public static String kuck = "15881688";//국민
    public static String sin = "15447000";//신한

    static boolean semaphore = false;//실행중일때 중복으로 스낵바 띄우는거 방지.
    ClipboardManager clipBoard;
    ClipboardListener clipboardListener;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        clipboardListener = new ClipboardListener();
        clipBoard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        clipBoard.addPrimaryClipChangedListener(clipboardListener);
        emptyClipboard(clipBoard);
    }
    @Override
    public void onDestroy() {
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

        String smsDate = "";
        String smsMoney = "";
        String smsContent = "";

        public void onPrimaryClipChanged() {
            if(semaphore == false) {
                semaphore = true;
                ClipData.Item item = clipBoard.getPrimaryClip().getItemAt(0);
                pasteData = item.getText();
                Log.e("data ", pasteData.toString());


                if(SMSListCheck(pasteData.toString()) == true) {//그 문자가 은행문자라면.
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
                }else{

                }
            }

        }//클립보드 내용이 바뀌면

        public Boolean SMSListCheck(String content) {

            Uri allMessage = Uri.parse("content://sms/");
            Cursor cur = getContentResolver().query(allMessage, null, null, null, null);
            String row = "";
            String msg = "";

            while (cur.moveToNext()) {
                row = cur.getString(cur.getColumnIndex("address"));
                msg = cur.getString(cur.getColumnIndex("body"));
                if(content.contains(msg)){
                    if(row.contains(nong) || row.contains(kuck) || row.contains(sin)) {//만약 은행 번호랑 일치한다면, 공기계라 주석.
                        DateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
                        Calendar calendar = Calendar.getInstance();
                        smsContent = msg;
                        smsDate = cur.getString(cur.getColumnIndex("date"));
                        //smsMoney = "25000";
                        String[] splitString = msg.split("\\s+"); // 돈을 파싱해오는 것인데, 역시 공기계라 주석.
                        for(int i = 0 ; i <splitString.length;i++){
                            if(splitString[i].contains("원")){
                                String temp = splitString[i].replaceAll(",","");
                                temp = temp.replace("원","");
                                smsMoney = temp;
                            }
                        }
                        calendar.setTimeInMillis(Long.parseLong(smsDate));
                        smsDate = formatter.format(calendar.getTime());
                        smsDate = smsDate.substring(2);//
                    Log.e("결과", "SMS Phone: " + row + " / Mesg: " + msg + " / Date: " + smsDate);
                        return true;
                    }
                }
               /* date = cur.getString(cur.getColumnIndex("date"));
                DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss.SSS");
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(Long.parseLong(date));
                String finalDateString = formatter.format(calendar.getTime());
                Log.e("결과" , "SMS Phone: " + row + " / Mesg: " + msg + " / Date: " + finalDateString);*/
            }
            return false;
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getApplicationContext(),AddUsageByPasteActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.putExtra("smsDate",smsDate);
            intent.putExtra("smsMoney",smsMoney);
            intent.putExtra("smsContent",smsContent);
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


