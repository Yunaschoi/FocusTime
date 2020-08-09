package com.example.focustime;

import android.app.Activity;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

public class BackPressCloseHandler {

    private long backKeyPressedTime = 0;
    private Toast toast;

    private Activity activity;

    public BackPressCloseHandler(Activity context) {

        this.activity = context;
    }

    public void onBackPressed() {
        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis();
            showGuide();
            return;
        }
        if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
            //activity.finish();
            //android.os.Process.killProcess(android.os.Process.myPid());
            //System.exit(1);
            ActivityCompat.finishAffinity(activity);
            toast.cancel();
        }
    }

    public void showGuide() {
        toast = Toast.makeText(activity,"\'뒤로가기\' 한번 더 누르면 앱 종료.", Toast.LENGTH_SHORT);
        toast.show();
    }
}