package com.example.focustime.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.example.focustime.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {
    ProgressBar progressBar;
    LottieAnimationView lottie;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        lottie = (LottieAnimationView) findViewById(R.id.lottie);
        lottie.setAnimation("stopwatch.json");
        lottie.playAnimation();
        new MyTask().execute();

        progressBar = (ProgressBar) findViewById(R.id.progress_circular);

        /*
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();  // 현재 로그인한 유저

        if(user==null) {   // 만약 현재 로그인 된 유저가 null 이라면 스플래시화면이 보임


        } else {   // 현재 유저가 로그인되어 있는 상태라면 바로 메인화면으로 이동

            lottie.cancelAnimation();
            Intent intent = new Intent(this,MainActivity.class);
            startActivity(intent);   // 메인액티비티로 이동
        }

         */


    }

    private class MyTask extends AsyncTask<Void,Integer,Void> {

        int progress_status;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress_status = 0;

        }

        @Override
        protected Void doInBackground(Void[] params) {

            while (progress_status<100)
            {
                progress_status +=1;
                publishProgress(progress_status);    // 현재 3초동안 splash Activity 를 보여줌
                SystemClock.sleep(40);
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Integer[] values) {
            super.onProgressUpdate(values);
            progressBar.setProgress(values[0]);

        }
        @Override
        protected void onPostExecute(Void o) {
            super.onPostExecute(o);

            lottie.cancelAnimation();
            Intent intent = new Intent(SplashActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
        }

    }

}
