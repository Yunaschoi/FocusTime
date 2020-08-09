package com.example.focustime.Service;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.example.focustime.Activity.MainActivity;
import com.example.focustime.Activity.TimerActivity;
import com.example.focustime.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MyService extends Service {
    NotificationManager Notifi_M;
    ServiceThread thread;
    FirebaseUser user;

    private static final String CHANNEL_ID = "D-Day_Channel";
    private static final String CHANNEL_NAME = "D-day_Channel";
    NotificationManager manager;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    public MyService() {
        //메모리에 만들어지는 시점
    }

    @Override
    public void onCreate() {              // 서비스가 만들어짐
        super.onCreate();
        user = FirebaseAuth.getInstance().getCurrentUser();  // 현재 로그인한 유저
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Notifi_M = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        myServiceHandler handler = new myServiceHandler();
        thread = new ServiceThread(handler);
        thread.start();
        return START_STICKY;

    }


    @Override
    public void onDestroy() {         // 서비스가 없어짐
        super.onDestroy();
        thread.stopForever();
        thread = null;//쓰레기 값을 만들어서 빠르게 회수하라고 null을 넣어줌.

       // Toast.makeText(MyService.this, "Service 끝", Toast.LENGTH_LONG).show();

    }


    class myServiceHandler extends Handler {
        @Override
        public void handleMessage(android.os.Message msg) {

            //  Intent intent = new Intent(MyService.this, MainActivity.class);
            //  PendingIntent pendingIntent = PendingIntent.getActivity(MyService.this, 0, intent,PendingIntent.FLAG_UPDATE_CURRENT);

            Bitmap mLageIconForNoti = BitmapFactory.decodeResource(getResources(), R.drawable.calendar);

            PendingIntent mPendingIntent = PendingIntent.getActivity(
                    MyService.this,
                    0,
                    new Intent(getApplicationContext(), MyService.class),
                    PendingIntent.FLAG_UPDATE_CURRENT
            );


            /*
            // 서비스 중지하라고 인텐트 보냄

            Intent stopIntent = new Intent(getApplicationContext(),MainActivity.class);
            stopService(stopIntent);
            stopIntent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

            PendingIntent mCancelPendingIntent = PendingIntent.getActivity(
                    MyService.this,
                    0,
                    stopIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT
            );

             */


            Intent fullScreenIntent = new Intent(MyService.this, MainActivity.class);
            PendingIntent fullScreenPendingIntent = PendingIntent.getActivity(MyService.this, 0,
                    fullScreenIntent, PendingIntent.FLAG_UPDATE_CURRENT);


            manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            NotificationCompat.Builder builder = null;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {   // 내 버전이 오레오 (26) 버전 보다 높다는 것을 의미

                //채널이 없는 경우 채널을 생성
                if (manager.getNotificationChannel(CHANNEL_ID) == null) {

                    manager.createNotificationChannel(new NotificationChannel
                            (CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH));
                }

                SharedPreferences sf = getSharedPreferences("DdayTitle", MODE_PRIVATE);
                //text라는 key에 저장된 값이 있는지 확인. 아무값도 들어있지 않으면 "D-day"를 반환
                String Title = sf.getString(user.getEmail(), "D-day");


                builder = new NotificationCompat.Builder(MyService.this, CHANNEL_ID);
                builder.setContentTitle("FocusTime");
                builder.setContentText("오늘은 "+Title+" 입니다!");
                builder.setSmallIcon(R.drawable.ic_history_black_24dp);
                builder.setWhen(System.currentTimeMillis());

                builder.setLargeIcon(mLageIconForNoti);  // 비트맵으로 바꿔서 아이콘을 집어 넣음
                builder.setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});  // 진동 패턴
                builder.setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND);  // 진동, 소리
                builder.setAutoCancel(true);  // 터치시 자동으로 삭제할 것인지
                builder.setDefaults(Notification.FLAG_ONLY_ALERT_ONCE);  // 소리 한번만 내도록 설정
                builder.setPriority(NotificationCompat.PRIORITY_MAX);  // 노티의 중요도 높게 설정
                builder.setContentIntent(mPendingIntent);
                builder.setNumber(1);

               // builder.setTimeoutAfter(8000);  // 사용자가 알림을 취소하지 않은 경우 8초뒤에 알림 종료....?

                builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
                builder.setFullScreenIntent(fullScreenPendingIntent, true);
                // builder.setOngoing(true);  // 알림을 지속적으로 띄울 것인지 설정

                // builder.addAction(R.drawable.ic_history_black_24dp, "알림 종료", mCancelPendingIntent);
                //  builder.addAction(R.drawable.ic_android_black_24dp,"돌아가기",mConfirmPendingIntent);

                final Notification noti = builder.build();

                manager.notify(1, noti);


            } else {  // 내 버전이 오레오 버전보다 낮을 때 사용할 코드 => 현재 사용 안하고 있음

                builder = new NotificationCompat.Builder(MyService.this, CHANNEL_ID);
            }


            /*
            Notifi = new Notification.Builder(getApplicationContext())
                    .setContentTitle("Content Title")
                    .setContentText("Content Text")
                    .setSmallIcon(R.drawable.ic_launcher_background)
                    .setTicker("알림!!!")
                    .setContentIntent(pendingIntent)
                    .build();

            //소리추가
            Notifi.defaults = Notification.DEFAULT_SOUND;

            //알림 소리를 한번만 내도록
            Notifi.flags = Notification.FLAG_ONLY_ALERT_ONCE;

            //확인하면 자동으로 알림이 제거 되도록
            Notifi.flags = Notification.FLAG_AUTO_CANCEL;


            Notifi_M.notify( 777 , Notifi);

             */

            //토스트 띄우기
          //  Toast.makeText(MyService.this, "Service 시작", Toast.LENGTH_LONG).show();
        }
    }

}