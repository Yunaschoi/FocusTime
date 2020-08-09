package com.example.focustime;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.focustime.Activity.MainActivity;
import com.example.focustime.Activity.TimerActivity;

import java.util.Locale;

public class exerciseTimerActivity extends AppCompatActivity {

    private static final long START_TIME_IN_MILLIS = 600000;  // 처음 시간 값  이 경우 현재 10분으로 셋팅해둠

    private static final String TAG = "TimerActivity";


    private TextView mTextViewCountDown;
    private ImageButton mButtonStartPause;  // 화살표 모양의 start 이미지 버튼
    private Button mButtonReset;   // reset 버튼
    private CardView userNameFamousCardView; // 명언/ 사용자이름 카드뷰
    private CardView giveUpCardView;  //  나가기 카드뷰 (giveUp 카드뷰)

    private CountDownTimer mCountDownTimer;  // 카운트다운 타이머 클래스
    private boolean mTimerRunning;  // 타이머가 실행중인지 확인하는 boolean

    private long mTimeLeftInMillis;

    private long mEndTime;

    TextView userName;  // 사용자 이름을 담아 줄 변수이름


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        userName = (TextView) findViewById(R.id.currentUserName);   // 회원정보 화면 구현 후에 파이어베이스의 회원정보의 이름과 연동

        userNameFamousCardView = findViewById(R.id.userNameCardView);  // 명언/ 유저이름 보여 줄 cardView
        giveUpCardView = findViewById(R.id.give_up_cardview);    // 타이머 종료 하는 cardView

        mTextViewCountDown = findViewById(R.id.text_view_countdown);

        mButtonStartPause = findViewById(R.id.button_start_pause);
        mButtonReset = findViewById(R.id.button_reset);



        mTextViewCountDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


        mButtonStartPause.setOnClickListener(new View.OnClickListener() {    // 시작이미지버튼 클릭했을 때
            @Override
            public void onClick(View view) {

                if (mTimerRunning) {

                    pauseTimer();

                } else {

                    startTimer();

                }

            }
        });


        mButtonReset.setOnClickListener(new View.OnClickListener() {   // 리셋버튼 클릭했을 때
            @Override
            public void onClick(View view) {

                resetTimer();
            }
        });

        // updateCountDownText();

        giveUpCardView.setOnClickListener(new View.OnClickListener() {   // giveUp 카드뷰 눌렀을 때
            @Override
            public void onClick(View view) {

                if (mTimerRunning == false) {  // 타이머가 실행 중이 아닐 때

                    onBackPressed(); // 메인화면으로 돌아감

                } else {

                    pauseTimer(); // giveUp 버튼 눌렀을 때

                    AlertDialog.Builder builder = new AlertDialog.Builder(exerciseTimerActivity.this);
                    builder.setMessage(" Focus 를 끝내시겠습니까?");
                    builder.setTitle("알림")
                            .setCancelable(false)
                            .setPositiveButton("네", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {


                                    dialogInterface.dismiss();  // 다이얼로그를 종료 시켜준다
                                    startMainActivity();
                                    finish();   // 현재화면은 꺼짐
                                    resetTimer(); // resetTimer 실행
                                }
                            })
                            .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.cancel();
                                    startTimer(); // 타이머를 계속 진행 시켜줌
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.setTitle("알림");
                    alert.show();


                }
            }
        });


    }


    private void startTimer() {   // 시작하는 메소드
        mEndTime = System.currentTimeMillis() + mTimeLeftInMillis;

        mCountDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {  // 시작하는 메소드 , 1000 으로 설정 1초씩 감소
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {   // 카운트 타이머 시간이 끝났을 때

                mTimerRunning = false;

                updateButtons();
                //     mButtonStartPause.setImageResource(R.drawable.ic_play_arrow_black_24dp);  // 다시 원래 이미지로 변경 해줌
                //     mButtonStartPause.setVisibility(View.INVISIBLE);  //  start 이미지버튼 감춤
                //    userNameFamousCardView.setVisibility(View.INVISIBLE);  // 카드뷰 감춤
                //    mButtonReset.setVisibility(View.VISIBLE); // RESET 버튼 보여줌

            }
        }.start();

        mTimerRunning = true;

        updateButtons();

        // mButtonStartPause.setImageResource(R.drawable.ic_pause_orange_24dp);  //  눌렀을 때 이 모양으로 변경
        // mButtonReset.setVisibility(View.INVISIBLE); // RESET 버튼 감춤
        // userNameFamousCardView.setVisibility(View.VISIBLE);   // 카드뷰 보여줌
    }


    private void pauseTimer() {   // 일시정지 하는 메소드

        mCountDownTimer.cancel(); // 카운트다운타이머 취소
        mTimerRunning = false;  // 동작하지 않는 다고 false 값을 줌

        updateButtons();

        //   mButtonStartPause.setImageResource(R.drawable.ic_play_arrow_black_24dp);  // 다시 원래 이미지로 변경 해줌
        //   mButtonReset.setVisibility(View.VISIBLE);  //  숨겨 놓은 버튼을 보여줌
        //   userNameFamousCardView.setVisibility(View.INVISIBLE);  // 카드뷰 숨김

    }


    private void resetTimer() {    // 처음 시간으로 돌리는 메소드

        mTimeLeftInMillis = START_TIME_IN_MILLIS;  // 다시 원래 시간으로 만들어 줌
        updateCountDownText();
        updateButtons(); // 버튼 뷰를 변경

        //   mButtonReset.setVisibility(View.INVISIBLE);  // RESET 버튼 감춤
        //   userNameFamousCardView.setVisibility(View.INVISIBLE);  // 카드뷰 감춤
        //   mButtonStartPause.setVisibility(View.VISIBLE); // start 이미지 버튼 보여줌
    }


    private void updateCountDownText() {     // 시간이 감소(변경) 되는 것을 나타내어 주기 위한 메소드
        int minutes = (int) (mTimeLeftInMillis / 1000) / 60;   // 분을 계산
        int seconds = (int) (mTimeLeftInMillis / 1000) % 60;   // 초를 계산

        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        mTextViewCountDown.setText(timeLeftFormatted); //
    }

    private void updateButtons() {
        if (mTimerRunning) {
            mButtonReset.setVisibility(View.INVISIBLE);
            mButtonStartPause.setImageResource(R.drawable.ic_pause_orange_24dp);

            userNameFamousCardView.setVisibility(View.VISIBLE);   // 카드뷰 보여줌


        } else {
            mButtonStartPause.setImageResource(R.drawable.ic_play_arrow_black_24dp);
            userNameFamousCardView.setVisibility(View.INVISIBLE); // 카드뷰 감춤

            if (mTimeLeftInMillis < 1000) {   // 셋팅했던 시간이 1초보다 작을 때
                mButtonStartPause.setVisibility(View.INVISIBLE);
            } else {
                mButtonStartPause.setVisibility(View.VISIBLE);
            }

            if (mTimeLeftInMillis < START_TIME_IN_MILLIS) {
                mButtonReset.setVisibility(View.VISIBLE);
            } else {
                mButtonReset.setVisibility(View.INVISIBLE);
            }
        }
    }

    // 백그라운드로 나갔을 때 타이머 동작하게 하기 위해 생명주기 사용

    @Override
    protected void onStop() {   // onStop 일 때 현재 진행 중인 타이머 시간 값 저장
        super.onStop();
        SharedPreferences prefs = getSharedPreferences("timer", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putLong("millisLeft", mTimeLeftInMillis);
        editor.putBoolean("timerRunning", mTimerRunning);
        editor.putLong("endTime", mEndTime);

        editor.apply();

        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }


    }

    @Override
    protected void onStart() {
        super.onStart();      // onStart 때 Load 해줌

        SharedPreferences prefs = getSharedPreferences("timer", MODE_PRIVATE);

        mTimeLeftInMillis = prefs.getLong("millisLeft", START_TIME_IN_MILLIS);
        mTimerRunning = prefs.getBoolean("timerRunning", false);

        updateCountDownText();
        updateButtons();

        if (mTimerRunning) {
            mEndTime = prefs.getLong("endTime", 0);
            mTimeLeftInMillis = mEndTime - System.currentTimeMillis();

            if (mTimeLeftInMillis < 0) {    // 시간이 0 보다 작을 때
                mTimeLeftInMillis = 0;
                mTimerRunning = false;
                updateCountDownText();
                updateButtons();
            } else {
                startTimer();               // 아닐 경우 startTimer 메소드 실행
            }
        }
    }

    @Override
    public void onBackPressed() {

        AlertDialog.Builder builder = new AlertDialog.Builder( exerciseTimerActivity.this);
        builder.setMessage("메인화면으로 돌아가시겠습니까?");
        builder.setTitle("알림")
                .setCancelable(false)
                .setPositiveButton("네", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {


                        dialogInterface.dismiss();  // 다이얼로그를 종료 시켜준다
                        startMainActivity();    // 메인 화면으로 이동
                        finish();
                        resetTimer();  // 메인화면으로 갈 때 시간 값 다시 원상 복귀
                    }
                })
                .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.setTitle("알림");
        alert.show();
    }




    private void startMainActivity() {
        Intent intent = new Intent(exerciseTimerActivity.this, MainActivity.class);
        startActivity(intent);
    }

}
