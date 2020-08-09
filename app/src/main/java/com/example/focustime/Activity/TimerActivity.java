package com.example.focustime.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.focustime.Adapter.StorageAdapter;
import com.example.focustime.MySayingItem;
import com.example.focustime.R;
import com.example.focustime.Service.MyService;
import com.example.focustime.StorageItem;
import com.example.focustime.UserInformation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.widget.NumberPicker.*;

public class TimerActivity extends AppCompatActivity implements NumberPicker.OnValueChangeListener {


    private static final String CHANNEL_ID = "channel1";
    private static final String CHANNEL_NAME = "Channel1";
    NotificationManager manager;

    protected Calendar timeCalendar;  // 오늘 날짜를 뽑아내 줄 켈린더 변수
    protected SimpleDateFormat df;
    protected String[] formattedDate;
    protected String timerClassMondayDate; // 타이머 클래스에서 월요일 날짜로 변환된 String 값을 받을 변수 그래프 shared Key 값으로 사용

    protected String resultForGraph; // 그래프에 쓰일 결과 값을 받을 String 값 ex) 성공인지 실패인지

    FirebaseUser user;
    // private static final long mStartTimerInMillis = 10000; // 10초
    private long mStartTimerInMillis = 300000;  // 처음 시간 값  이 경우 현재 5분으로 셋팅해둠
    // 1000 = 1초

    private long successtime;

    // private static final long mStartTimerInMillis = 1000;

    private static final String TAG = "TimerActivity";

  //  private StorageAdapter storageAdapter;
    private ArrayList<StorageItem> models;


    JSONArray jArray, jarray,ja,jA;

    private TextView mTextViewCountDown;
    private ImageButton mButtonStartPause;  // 화살표 모양의 start 이미지 버튼
    private Button mButtonReset;   // reset 버튼
    private CardView userNameFamousCardView; // 명언/ 사용자이름 카드뷰
    private CardView giveUpCardView;  //  나가기 카드뷰 (giveUp 카드뷰)

    private CountDownTimer mCountDownTimer;  // 카운트다운 타이머 클래스
    private boolean mTimerRunning;  // 타이머가 실행중인지 확인하는 boolean

    private TextView famousAdvice; // 명언 텍스트뷰
    private long mTimeLeftInMillis;

    private long mEndTime;

    private String aStartTime;  // 시작한 시간
    private String aSaveTime;   // 집중한 시간
    private String aTryTitle;   // 제목

    String focusTime;

    int countSum=0; // 성공 횟수를 쉐어드에 저장시켜줄 변수

    protected TextView DdayDate;
    protected TextView DdayTitle;


    protected int count;   // 성공 횟수 증가를 담당
    private int saveCount; // 성공 증가하는 수를 저장

    protected int transferDay; // 성공했을 때의 요일을 숫자로 변환시켜 저장 시켜 줄 변수
    private ArrayList<SuccessItem> successItems;

    long mNow;  // 현재시간
    SimpleDateFormat mFormat; // date 클래스를 변환해서 나타내줄 변수


    TextView userName;  // 사용자 이름을 담아 줄 변수이름
    ImageView userPicture; // 사용자의 사진을 담아 줄 변수 이름

    ImageButton setTime;  // 시간설정하는 카드뷰 안의 이미지
    CardView clockCardView; // 타이머가 실행이 된다면 카드 뷰 안보이게 만들기
    CardView startCardView;
    long castValue; // 사용자가 설정한 값


    String advice;   // 명언을 하나씩 담아 줄 String 변수
    boolean isRunning = false;  // 스레드 멈춤, 시작할 때 사용
    DisplayHandler handler; // DisplayHandler 참조변수 선언


   // SuccessItem item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);



        count = saveCount;  // 증가한

        successItems = new ArrayList<>();

        models = new ArrayList<>();

        handler = new DisplayHandler();

        user = FirebaseAuth.getInstance().getCurrentUser(); // 현재 로그인된 유저

        loadData();
       // graphDataLoad();

        userName = (TextView) findViewById(R.id.currentUserName);   // 파이어베이스의 정보를 불러옴 - 이름
        userPicture = (ImageView) findViewById(R.id.userPicture);   // 파이어베이스의 정보를 불러옴 - 사진

        DdayDate = (TextView) findViewById(R.id.textView_date);   // 쉐어드에서 불러와서 입력시킬 D-day
        DdayTitle = (TextView) findViewById(R.id.textView_title);  // 쉐어드에서 불러와서 입력시킬 제목


        userNameFamousCardView = findViewById(R.id.userNameCardView);  // 명언/ 유저이름 보여 줄 cardView
        giveUpCardView = findViewById(R.id.give_up_cardview);    // 타이머 종료 하는 cardView

        mTextViewCountDown = findViewById(R.id.text_view_countdown);

        mButtonStartPause = findViewById(R.id.button_start_pause);
        famousAdvice = findViewById(R.id.famousAdvice);
        startCardView = (CardView) findViewById(R.id.startCardView);


        setTime = (ImageButton) findViewById(R.id.imageTimeSet);
        clockCardView = (CardView) findViewById(R.id.clockCardView);


        //////////////////////////////////////////////////////////////////////
        // 타이머 클래스에서 그래프 Shared Key 로 사용할 월요일 날짜를 구하기 위한 코드 작성 중

        timeCalendar = Calendar.getInstance(); // 오늘 날짜
        df = new SimpleDateFormat("yyyy.MM.dd", Locale.getDefault());
        formattedDate = new String[]{df.format(timeCalendar.getTime())};

        // 월요일을 뽑아 내주는 코드 작성 중
        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat weekdayFormat = new SimpleDateFormat("EE", Locale.getDefault());

        String weekDay = weekdayFormat.format(currentTime);  // 오늘 요일

        if (weekDay.equals("월")) {  // 오늘 날짜가 월요일이라면

            timeCalendar.add(Calendar.DATE, +0);
            formattedDate[0] = df.format(timeCalendar.getTime());

            timerClassMondayDate = formattedDate[0];  // onCreate 때 쉐어드 키로 사용할 월요일 날짜 값

            transferDay = 1;

            Log.d("formattedDate[0]", formattedDate[0]);


        } else if (weekDay.equals("화")) {

            timeCalendar.add(Calendar.DATE, -1);
            formattedDate[0] = df.format(timeCalendar.getTime());

            timerClassMondayDate = formattedDate[0];  // onCreate 때 쉐어드 키로 사용할 월요일 날짜 값

            transferDay = 2;

            Log.d("formattedDate[0]", formattedDate[0]);


        } else if (weekDay.equals("수")) {

            timeCalendar.add(Calendar.DATE, -2);
            formattedDate[0] = df.format(timeCalendar.getTime());

            timerClassMondayDate = formattedDate[0];  // onCreate 때 쉐어드 키로 사용할 월요일 날짜 값

            transferDay = 3;

            Log.d("formattedDate[0]", formattedDate[0]);


        } else if (weekDay.equals("목")) {

            timeCalendar.add(Calendar.DATE, -3);
            formattedDate[0] = df.format(timeCalendar.getTime());

            timerClassMondayDate = formattedDate[0];  //onCreate 때 쉐어드 키로 사용할 월요일 날짜 값

            transferDay = 4;

            Log.d("formattedDate[0]", formattedDate[0]);


        } else if (weekDay.equals("금")) {

            timeCalendar.add(Calendar.DATE, -4);
            formattedDate[0] = df.format(timeCalendar.getTime());

            timerClassMondayDate = formattedDate[0];  // onCreate 때 쉐어드 키로 사용할 월요일 날짜 값

            transferDay = 5;

            Log.d("formattedDate[0]", formattedDate[0]);

        } else if (weekDay.equals("토")) {

            timeCalendar.add(Calendar.DATE, -5);
            formattedDate[0] = df.format(timeCalendar.getTime());

            timerClassMondayDate = formattedDate[0];  // onCreate 때 쉐어드 키로 사용할 월요일 날짜 값

            transferDay = 6;

            Log.d("formattedDate[0]", formattedDate[0]);

        } else if (weekDay.equals("일")) {

            timeCalendar.add(Calendar.DATE, -6);
            formattedDate[0] = df.format(timeCalendar.getTime());

            timerClassMondayDate = formattedDate[0];  // onCreate 때 쉐어드 키로 사용할 월요일 날짜 값

            transferDay = 7;

            Log.d("formattedDate[0]", formattedDate[0]);

        }



        /////////////////////////////////////////////////////////////
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("users").document(user.getUid());
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {
                        if (document.exists()) {
                            Log.d(TAG, "DocumentSnapshot data: " + document.getData()); // 회원정보가 등록된 사용자라면 그 데이터를 가져온다
                            userName.setText(document.getData().get("name").toString());


                            //  if(document.getData().get("photoUrl") != null) {
                            //      Glide.with(getBaseContext()).load(document.getData().get("photoUrl")).override(200,200).centerCrop().into(userPicture);

                            Log.d("타이머화면 변경이미지", "" + document.getData().get("photoUrl"));
                            SharedPreferences sf = getSharedPreferences("ImageChange", MODE_PRIVATE);
                            String changeUri = sf.getString(user.getEmail(), "");
                            Log.d("변경", "" + changeUri);
                            Glide.with(TimerActivity.this).load(changeUri).centerCrop().override(500).into(userPicture);
                            // Shared 로 이미지 받기
                            //  }

                        } else {
                            Log.d(TAG, "No such document");
                            // startToast("회원정보를 등록해주세요");
                        }


                    }


                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });


        setTime.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                timeSettingDialog(); // 타임 세팅 해주는 다이얼로그
            }
        }); // timeSet 눌렀을 때 반응하는

////////////////////////////////////////////////////////

        mButtonStartPause.setOnClickListener(new View.OnClickListener() {    // 시작이미지버튼 클릭했을 때
            @Override
            public void onClick(View view) {

                if (mTimerRunning) {

                    //   pauseTimer();

                } else {  // start 버튼 눌렀을 때
                    isRunning = true;
                    cThread thread = new cThread();  // 스레드 객체 생성
                    thread.start();   // start 이미지버튼 눌렀을 때 명언 스레드 시작


                    getStartTime();  // 현재 시간을 저장 -> StorageItem 값으로 사용
                    getDay(); // 오늘 날짜를 저장 -> Key 로 사용
                    aStartTime = getStartTime();  // 현재 저장된 시간 값을 저장

                    //  storageItem.setStartTime(aStartTime);
                    //  storageItem.setCreateAt(getDay());

                    Log.d("startTime : ", aStartTime);
                    Log.d("today :", getDay());

                    startTimer();  // 타이머 시작하는 메서드

                }

            }
        });

        /*

        mButtonReset.setOnClickListener(new View.OnClickListener() {   // 리셋버튼 클릭했을 때
            @Override
            public void onClick(View view) {

                resetTimer();
            }
        });

        */

        // updateCountDownText();

        giveUpCardView.setOnClickListener(new View.OnClickListener() {   // giveUp 카드뷰 눌렀을 때
            @Override
            public void onClick(View view) {

                if (mTimerRunning == false) {  // 타이머가 실행 중이 아닐 때, 시간 값을 저장시키지 않음

                    //   onBackPressed(); // 메인화면으로 돌아감
                    AlertDialog.Builder builder = new AlertDialog.Builder(TimerActivity.this);
                    builder.setMessage("메인화면으로 돌아가시겠습니까?");
                    builder.setTitle("알림")
                            .setCancelable(false)
                            .setPositiveButton("네", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    dialogInterface.dismiss();  // 다이얼로그를 종료 시켜준다
                                    resetTimer();  // 메인화면으로 갈 때 시간 값 다시 원상 복귀
                                    startMainActivity();    // 메인 화면으로 이동
                                    finish();
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


                } else {       // 타이머가 실행 중인데 giveUp 버튼 눌렀을 때 , 그만둔다고 했을 시 현재 시간을 저장 시켜줌

                    pauseTimer();

                    AlertDialog.Builder builder = new AlertDialog.Builder(TimerActivity.this);
                    builder.setMessage(" 이대로 그만둘꺼야?");
                    builder.setTitle("알림")
                            .setCancelable(false)
                            .setPositiveButton("그만", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    // thread.stopThread();  // 스레드 종료

                                    SharedPreferences sharedPreferences = getSharedPreferences("timer", MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();

                                    mStartTimerInMillis = sharedPreferences.getLong("millisLeft", castValue); // 사용자가 선택한 값으로 다시 세팅 해줌

                                    editor.apply();

                                    getSaveTime();  // saveTime 얻는 메소드

                                  //  resultForGraph = "fail"; // 중간에 타이머를 멈췄을 때 저장시킬 값
                                  //  graphDataSave(); // 그래프 쉐어드에 fail 저장 시켜줌

                                    dialogInterface.dismiss();  // 다이얼로그를 종료 시켜준다
                                    startMainActivity();
                                    finish();   // 현재화면은 꺼짐

                                    aTryTitle = "실패";    // 타이머 실행시키고 중간에 나갈시 실패로 저장
                                    Log.d("tryTitle :", aTryTitle);
                                    // storageItem.setTryTitle(aTryTitle);

                                    StorageItem storageItem = new StorageItem(aStartTime, aSaveTime, aTryTitle, getDay());
                                    // storageItem.setStartTime(aStartTime);
                                    // storageItem.setTryTitle(aTryTitle);
                                    // storageItem.setSaveTime(aSaveTime);
                                    // storageItem.setCreateAt(getDay());
                                    models.add(storageItem);


                                    saveData();  // 사용자가 임의로 포기했을 때 JSON 배열 저장

                                    ///////////////////////////////////////////////

                                    resetTimer(); // resetTimer 실행
                                }
                            })
                            .setNegativeButton("계속 진행", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.cancel();
                                    // thread.start(); // 스레드 시작
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

    /*
    이미지 절대경로 주소를 불러오는 코드
    private String getRealPathFromURI(Uri contentURI) {



        String result;

        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);



        if (cursor == null) { // Source is Dropbox or other similar local file path

            result = contentURI.getPath();



        } else {

            cursor.moveToFirst();

            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);

            result = cursor.getString(idx);

            cursor.close();

        }



        return result;

    }

     */


    public String getSuccessSaveTime() {

        Date startDate = new Date(successtime);

        Log.d("나와라", "" + successtime);

        long diff = startDate.getTime();

        Log.d("diff", "" + diff);

        //  DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        //  String str = dateFormat.format(diff);


        //  long sec = diff/1000;
        long minutes = diff / 60000;
        long hours = diff / 3600000;

        // String strSec = String.format("%02d",sec);
        String strMinutes = String.format("%02d", minutes);
        String strHours = String.format("%02d", hours);

        String a = String.valueOf(startDate.getTime());
        Log.d("startDate", a);

        //  Log.e("saveTime ", strHours+":"+strMinutes+":"+strSec);


        // aSaveTime = strHours+":"+strMinutes+":"+strSec;

        aSaveTime = strHours + " : " + strMinutes + " : 00";
        Log.e("세이브", aSaveTime);
        return aSaveTime;

    }


    public void getSaveTime() {    // saveTime 값 가져오는 메소드

        Date startDate = new Date(mStartTimerInMillis);  // 시작 분:초
        Date stopDate = new Date(mTimeLeftInMillis);    // 현재 남아 있는 분:초

        long diff = startDate.getTime() - stopDate.getTime();

        // 이렇게 동작하면 diff 가 1분이라는 시간을 가졌을 때 sec 에 60초가 대입 된다
       // long sec = diff / 1000;
       // long minutes = diff / 60000;
       // long hours = diff / 3600000;


        if(diff >= 3600000) {     // 1시간이상 공부했을 때 출력해줄 String 값
            SimpleDateFormat dateFormat = new SimpleDateFormat("hh : mm : ss");
            String time = dateFormat.format(diff);
            Log.d("시간",""+time);
            focusTime = time;

        } else {     // 1시간 이하로 공부했을 때 출력해줄 String 값

            SimpleDateFormat dateFormat = new SimpleDateFormat("00 : mm : ss");
            String time = dateFormat.format(diff);
            Log.d("시간", "" + time);
            focusTime = time;
        }

       // String strSec = String.format("%02d", sec);
       // String strMinutes = String.format("%02d", minutes);
       // String strHours = String.format("%02d", hours);


        String a = String.valueOf(startDate.getTime());
        String b = String.valueOf(stopDate.getTime());
        Log.d("startDate", a);
        Log.d("stopDate", b);


        Log.e("saveTime ", focusTime);

     //   Log.e("saveTime ", strHours + ":" + strMinutes + ":" + strSec);
     //   aSaveTime = strHours + ":" + strMinutes + ":" + strSec;

        aSaveTime = focusTime;

    }

    public void timeSettingDialog() {

        final AlertDialog.Builder d = new AlertDialog.Builder(TimerActivity.this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.time_setting, null);
        d.setTitle("FocusTime");
        d.setMessage("시간 설정");
        d.setView(dialogView);
        final NumberPicker numberPicker = (NumberPicker) dialogView.findViewById(R.id.numberPicker);

        numberPicker.setMaxValue(36);
        numberPicker.setMinValue(1);

        List<String> displayedValues = new ArrayList<>();  // 5 분씩 출력되게 하기 위해 담을 List
        for (int i = 5; i <= 180; ) {

            displayedValues.add(String.format("%02d", i));
            i = i + 5;
        }

        numberPicker.setDisplayedValues(displayedValues.toArray(new String[displayedValues.size()]));

        numberPicker.setWrapSelectorWheel(false);
        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                Log.d(TAG, "onValueChange: ");
            }
        });
        d.setPositiveButton("Done", new DialogInterface.OnClickListener() {   // Done 눌렀을 때
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.d(TAG, "onClick: " + numberPicker.getValue());

                int selectedNum = numberPicker.getValue() * 5;

                //  int selectedNum = numberPicker.getValue(); // 시연을 위해 1분으로 설정

                castValue = (int) selectedNum * 60000;  // long castValue; 전역으로 변수 설정
                mStartTimerInMillis = castValue;

                successtime = mStartTimerInMillis;   // 성공했을 때 받을 saveTime 값

                int minute = (int) (mStartTimerInMillis / 1000) / 60;   // 분을 계산
                int second = (int) (mStartTimerInMillis / 1000) % 60;   // 초를 계산


                String changeValue = String.format(Locale.getDefault(), "%02d:%02d", minute, second);
                mTextViewCountDown.setText(changeValue);

                SharedPreferences prefs = getSharedPreferences("timer", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();

                editor.clear(); // 데이터 삭제
                editor.commit();

                editor.putLong("millisLeft", castValue);
                editor.putBoolean("timerRunning", mTimerRunning);
                editor.putLong("endTime", mEndTime);

                editor.apply();

                mStartTimerInMillis = prefs.getLong("millisLeft", castValue);
                mTimeLeftInMillis = prefs.getLong("millisLeft", mStartTimerInMillis);


            }
        });
        d.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {   // cancel 눌렀을 때
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        AlertDialog alertDialog = d.create();
        alertDialog.show();

    }


    private void startTimer() {   // 시작하는 메소드
        mEndTime = System.currentTimeMillis() + mTimeLeftInMillis;

        clockCardView.setVisibility(View.INVISIBLE); // 카드뷰 감춤
        mCountDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {  // 시작하는 메소드 , 1000 으로 설정 1초씩 감소
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
                updateCountDownText();

            }

            @Override
            public void onFinish() {   // 카운트 타이머 시간이 끝났을 때

                // 시간이 다되었을 때는 집중한 시간 값으로 초기값을 줌
                // aSaveTime = String.valueOf(mStartTimerInMillis);
                // Log.d("SaveTime :",aSaveTime);

                //  storageItem.setSaveTime(aSaveTime);
                resetTimer(); //  시간을 리셋시켜준다
                mStartTimerInMillis = castValue; // 타이머 시간이 다되면 사용자가 설정한 시간으로 초기화

                mTimerRunning = false;
                isRunning = false;

                updateButtons();
                //     mButtonStartPause.setImageResource(R.drawable.ic_play_arrow_black_24dp);  // 다시 원래 이미지로 변경 해줌
                //     mButtonStartPause.setVisibility(View.INVISIBLE);  //  start 이미지버튼 감춤
                //    userNameFamousCardView.setVisibility(View.INVISIBLE);  // 카드뷰 감춤
                //    mButtonReset.setVisibility(View.VISIBLE); // RESET 버튼 보여줌

                successFocus(); // 카운트 타이머 시간이 끝났을 때 실행하는 메소드 ( success 다이얼로그를 띄워줌 )


                //mTimeLeftInMillis = 0;

                // getSaveTime();


                // 새로운 액티비티(goodJob activity) 만들어서 화면이동 및 값 넘겨주기

            }
        }.start();

        mTimerRunning = true; // 타이머 시작을 알리는 변수
        isRunning = true;  // 명언스레드 시작을 알리는 변수

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
        //   thread.stopThread(); // 일시정지 상태일때 스레드 멈춤

    }


    private void resetTimer() {    // 처음 시간으로 돌리는 메소드

        mTimeLeftInMillis = mStartTimerInMillis;  // 다시 원래 시간으로 만들어 줌
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
        mTextViewCountDown.setText(timeLeftFormatted);

    }

    private void updateButtons() {
        if (mTimerRunning) {
            //  mButtonReset.setVisibility(View.INVISIBLE);
            //  mButtonStartPause.setImageResource(R.drawable.ic_pause_orange_24dp);

            userNameFamousCardView.setVisibility(View.VISIBLE);   // 카드뷰 보여줌
            startCardView.setVisibility(View.INVISIBLE);

        } else {
            mButtonStartPause.setImageResource(R.drawable.ic_play_arrow_black_24dp);
            clockCardView.setVisibility(View.VISIBLE); // 시계 카드뷰 보임
            startCardView.setVisibility(View.VISIBLE); // start 카드뷰 보임
            userNameFamousCardView.setVisibility(View.INVISIBLE); // 카드뷰 감춤

            if (mTimeLeftInMillis < 1000) {   // 셋팅했던 시간이 1초보다 작을 때
                mButtonStartPause.setVisibility(View.INVISIBLE);
                startCardView.setVisibility(View.INVISIBLE);  // startCardView 안보임
                clockCardView.setVisibility(View.INVISIBLE); // 시계 카드뷰 안보임
            } else {
                mButtonStartPause.setVisibility(View.VISIBLE);
                startCardView.setVisibility(View.VISIBLE);
                clockCardView.setVisibility(View.VISIBLE);
            }

            if (mTimeLeftInMillis < mStartTimerInMillis) {
                // mButtonReset.setVisibility(View.VISIBLE);
            } else {
                // mButtonReset.setVisibility(View.INVISIBLE);
            }
        }
    }

    // 백그라운드로 나갔을 때 타이머 동작하게 하기 위해 생명주기 사용

    @Override
    protected void onStop() {   // onStop 일 때 현재 진행 중인 타이머 시간 값 저장
        super.onStop();

      //  Toast.makeText(this,"onStop",Toast.LENGTH_LONG).show();

        SharedPreferences prefs = getSharedPreferences("timer", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putLong("millisLeft", mTimeLeftInMillis);
        editor.putBoolean("timerRunning", mTimerRunning);
        editor.putLong("endTime", mEndTime);

        editor.apply();

        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }

       // saveData();

       // graphDataSave();

        if (mTimerRunning == true) {
            // 타이머가 동작 중이라면

            showNoti();  // 알림을 띄워 줄 메서드


            new CountDownTimer(10000, 1000) { // 10초 동안 1초의 간격으로 onTick 메소드를 호출합니다.

                public void onTick(long millisUntilFinished) {

                }

                public void onFinish() {

                    SharedPreferences prefs = getSharedPreferences("timer", MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();

                    editor.putLong("millisLeft", mStartTimerInMillis);
                    editor.putBoolean("timerRunning", false);
                    editor.putLong("endTime", 0);

                    editor.apply();

                    manager.cancel(1); // 알림 없애줌

                    mCountDownTimer.cancel(); // 카운트다운타이머 취소
                    mTimerRunning = false;  // 동작하지 않는 다고 false 값을 줌
                    mTimeLeftInMillis = castValue;

                    Log.d("시간끝", "알림");

                    updateButtons();

                    // 알람을 종료 시켜줌

                    finish();  // 타이머 화면 종료
                    // android.os.Process.killProcess(android.os.Process.myPid());
                    // System.exit(1);
                } //종료 되었을 때,

            }.start(); //카운트 시작

        }

    }

    public void showNoti() {     // 알림 띄우는 메서드


        Intent fullScreenIntent = new Intent(this, TimerActivity.class);
        PendingIntent fullScreenPendingIntent = PendingIntent.getActivity(this, 0,
                fullScreenIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        /*
        Intent intent = new Intent(this,TimerActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent fullScreenPendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);


         */


        Bitmap mLageIconForNoti = BitmapFactory.decodeResource(getResources(), R.drawable.calendar);

        PendingIntent mPendingIntent = PendingIntent.getActivity(
                TimerActivity.this,
                0,
                new Intent(getApplicationContext(), TimerActivity.class),
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        /*
        PendingIntent mCancelPendingIntent = PendingIntent.getActivity(
                TimerActivity.this,
                0,
                new Intent(getApplicationContext(),TimerActivity.class),
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        PendingIntent mConfirmPendingIntent = PendingIntent.getActivity(
                TimerActivity.this,
                0,
                new Intent(getApplicationContext(),TimerActivity.class),
                PendingIntent.FLAG_UPDATE_CURRENT
        );

         */

        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {   // 내 버전이 오레오 (26) 버전 보다 높다는 것을 의미

            //채널이 없는 경우 채널을 생성
            if (manager.getNotificationChannel(CHANNEL_ID) == null) {

                manager.createNotificationChannel(new NotificationChannel
                        (CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH));
            }

            builder = new NotificationCompat.Builder(this, CHANNEL_ID);
            builder.setContentTitle("FocusTime");
            builder.setContentText("지금 당장 돌아오지 않으면 타이머가 종료됩니다!");
            builder.setSmallIcon(R.drawable.ic_history_black_24dp);
            builder.setWhen(System.currentTimeMillis());

            builder.setLargeIcon(mLageIconForNoti);  // 비트맵으로 바꿔서 아이콘을 집어 넣음
            builder.setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});  // 진동 패턴
            builder.setDefaults(Notification.DEFAULT_VIBRATE);  // 진동
            builder.setAutoCancel(true);  // 터치시 자동으로 삭제할 것인지
            builder.setPriority(NotificationCompat.PRIORITY_MAX);  // 노티의 중요도 높게 설정
            builder.setContentIntent(mPendingIntent);

            builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
            builder.setFullScreenIntent(fullScreenPendingIntent, true);
            // builder.setOngoing(true);  // 알림을 지속적으로 띄울 것인지 설정

            //  builder.addAction(R.drawable.ic_history_black_24dp,"지금종료",mCancelPendingIntent);
            //  builder.addAction(R.drawable.ic_android_black_24dp,"돌아가기",mConfirmPendingIntent);

            final Notification noti = builder.build();

            manager.notify(1, noti);

        } else {  // 내 버전이 오레오 버전보다 낮을 때 사용할 코드 => 현재 사용 안하고 있음

            builder = new NotificationCompat.Builder(this, CHANNEL_ID);
        }

    }



    @Override
    protected void onStart() {
        super.onStart();      // onStart 때 Load 해줌

        SharedPreferences prefs = getSharedPreferences("timer", MODE_PRIVATE);

        mTimeLeftInMillis = prefs.getLong("millisLeft", mStartTimerInMillis);
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
        // super.onBackPressed();
        // 뒤로가기 버튼 막음
    }

    private void successFocus() {    // 집중에 성공했을 때 사용될 메서드

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(TimerActivity.this);

        // 다이얼로그를 보여주기 위해 success_focus.xml 파일을 사용합니다.

        View view = LayoutInflater.from(TimerActivity.this)
                .inflate(R.layout.success_focus, null, false);
        builder.setView(view);

        final Button ButtonSubmit = (Button) view.findViewById(R.id.submit_btn);
        final EditText cTitle = (EditText) view.findViewById(R.id.fill_in_title);

        final android.app.AlertDialog dialog = builder.create();

        ButtonSubmit.setOnClickListener(new View.OnClickListener() {    // SUBMIT 버튼 눌렀을 때

            public void onClick(View v) {
                String strTitle = cTitle.getText().toString();  // 제목 입력

                if (strTitle.length() > 0) {   // 제목이 입력이 되어있을 때


                    clockCardView.setVisibility(View.VISIBLE); // 시계 카드뷰 보임
                    startCardView.setVisibility(View.VISIBLE); // start 카드뷰 보임
                    mButtonStartPause.setVisibility(View.VISIBLE); // start 버튼 보임

                    // aSaveTime = String.valueOf(mStartTimerInMillis);
                    Log.d("SaveTime :", getSuccessSaveTime());

                    aTryTitle = strTitle;     // 타이머 시간이 완료되고 success 다이얼로그에서 입력한 제목을 받음
                    Log.d("tryTitle :", aTryTitle);
                    //   storageItem.setTryTitle(aTryTitle);


                   // resultForGraph = "success"; // 타이머가 끝나고 제목을 입력했을 때 저장시킬 값


                   SuccessItem item = new SuccessItem();


                    countSum = count+1;

                    item.setCountItem(countSum);
                    item.setTodayItem(transferDay);

                    count++;
                    saveCount = count;

                    successItems.add(item);

                    //graphDataSave(); // 그래프 쉐어드에 저장 시켜줌

                    StorageItem storageItem = new StorageItem(aStartTime, getSuccessSaveTime(), aTryTitle, getDay());

                    models.add(storageItem);

                    dialog.dismiss();

                    //제목값을 입력시켜주고 다이얼로그 종료

                    saveData();

                    startMainActivity();
                    TimerActivity.this.finish(); // 타이머 액티비티화면 종료

                    // 메인화면으로 이동
                } else {  // 글자를 입력하지 않았을 때
                    Toast.makeText(TimerActivity.this, "제목을 입력해주세요", Toast.LENGTH_SHORT).show();
                }
            }
        });

        dialog.show();
    }

    private String getStartTime() {   //  start 시간 값을 반환 해줄 String 값
        mNow = System.currentTimeMillis();
        mFormat = new SimpleDateFormat("kk:mm");
        return mFormat.format(mNow);
    }

    @SuppressLint("SimpleDateFormat")

    private String getDay() {  // 현재 월/일 을 반환 해줄 String 값

        Date dateDay = new Date();
        mFormat = new SimpleDateFormat("yyyy.MM.dd. EE");
        return mFormat.format(dateDay);
    }

    public void loadData() {

        if (models == null) {
            models = new ArrayList<>();
        } else {

            SharedPreferences preferences = getSharedPreferences("mFile", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();

            // String result = preferences.getString(user.getEmail(),null);  //첫번째 인자는 데이터의 키, 두번째 인자는 해당값이 없을경우 반환할 값을 넣어준다.
            // 카운트다운타이머 구현 후 값을 넣어 줄 때 이 코드 복원

            String result = preferences.getString(user.getEmail() + getDay(), "");

            StringBuffer sb = new StringBuffer();
            Log.d("result", "" + result);
            try {
                jarray = new JSONArray(result);  // 진짜 값을 넣어 줄때는 String str 을 String result 로 변경
                Log.d("array 길이", "" + jarray.length());
                for (int i = 0; i < jarray.length(); i++) {
                    JSONObject jObject = jarray.getJSONObject(i);
                    String startTime = jObject.getString("startTime");
                    String tryTitle = jObject.getString("tryTitle");
                    String saveTime = jObject.getString("saveTime");
                    String createAt = jObject.getString("createAt");


                    sb.append("startTime:" + startTime + ", tryTitle:" + tryTitle + ", saveTime:" + saveTime + ", createAt:" + createAt + "\n");


                    StorageItem storageItem = new StorageItem(startTime, saveTime, tryTitle, getDay());


                    models.add(storageItem);

                }
                Log.d("JSON", sb.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            //editor.commit();

        }
    }

    public void saveData() {

        // 타이머 시간이 다 되었다면 tryTitle 은 successDialog 에서 tryTitle 값을 받아옴
        // 사용자가 임의로 타이머를 종료했다면 ( GiveUp 버튼 클릭 ) tryTitle 은 '실패' 로 저장
        // startTime = 시작이미지버튼을 눌렀을 때의 시간을 저장
        // saveTime ( 집중한 시간을 저장하는 변수이름 )
        //  -> 설정 시간이 아직 남아 있는데 GiveUp 버튼을 눌렀을 때 ( 종료했을때 남은 시간 ) 저장
        //  -> 타이머 시간이 다 되었을 때는 처음 설정한 시간 값을 저장

        SharedPreferences sharedPreferences = getSharedPreferences("mFile", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // JSON 으로 변환
        try {
            jArray = new JSONArray();//배열
            for (int i = 0; i < models.size(); i++) {
                JSONObject sObject = new JSONObject();//배열 내에 들어갈 json

                sObject.put("startTime", models.get(i).getStartTime());
                sObject.put("tryTitle", models.get(i).getTryTitle());
                sObject.put("saveTime", models.get(i).getSaveTime());
                sObject.put("createAt", models.get(i).getCreateAt());
                jArray.put(sObject);

                //  Log.e("JSON Test", models.get(i).getStartTime());
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

        editor.putString(user.getEmail() + getDay(), jArray.toString());  // 현재 유저의 이메일과 날짜를 Key 로 설정
        editor.apply();

    }


    @Override
    public void onValueChange(NumberPicker numberPicker, int i, int i1) {
        int selectedNum = i1;
        long castValue = (int) selectedNum;
        mStartTimerInMillis = castValue * 60000; // 분으로 입력하는 것이기 때문에 60000 을 곱해줌

    }


    class cThread extends java.lang.Thread {

        @Override
        public void run() {      // 화면에 관련된 작업을 할 때 필요한 데이터를 여기서 넘겨줌

            while (isRunning) {   // 이 작업을 오래걸리는 작업으로 가정한다

                // SystemClock.sleep(1000);
                //  Log.d("오래걸리는 작업 :","오래걸리는 작업");

                //   String advice[] = {"명언1","명언2","명언3"};

                ArrayList<String> arrayList = new ArrayList<>();
                arrayList.add("아는 것이 힘이다");
                arrayList.add("고통 없이는 얻는 것도 없다");
                arrayList.add("일찍 일어나는 새가 벌레를 잡는다");
                arrayList.add("게으름은 성공의 적이다");
                arrayList.add("성공은 나의 노력 여하에 달려있다");
                arrayList.add("멈추기엔 너무 이르다");
                arrayList.add("미래는 현재의 노력으로 얻어진다");
                arrayList.add("진정으로 중요한 것은 배우는 것이 아니라 실천하는 것이다");


                while (isRunning == true) {

                    int t;
                    t = (int) (0 + arrayList.size() * Math.random());

                    advice = arrayList.get(t);
                    Message msg = new Message(); // 랜덤 값을 받은 advice 메시지를 계속 만들어 주기 위해

                    //메시지 객체 만들기
                    // 여기서 만들어진 메시지 객체가 DisplayHandler 안의 handlerMessage 에 들어감
                    msg.what = 2;
                    //    msg.arg1 = ++a1;   // 정수값
                    //    msg.arg2 = ++a2;   // 정수값

                    // 객체를 세팅할 때 배열이나, 어레이리스트같은 컬렉션 , 문자열등 다 가능
                    msg.obj = advice; // 객체
                    // 현재시간 값을 세팅

                    handler.sendMessage(msg);
                    SystemClock.sleep(3000);

                }

                //핸들러 요청을 할 때 화면 처리를 위한 데이터를 전달

            }
        }
    }

    class DisplayHandler extends Handler {   // 화면에 관련된 작업만 실시

        // 개발자가 발생시킨 스레드에서 화면에 관련된 처리를 하기위해 작업을 요청하면
        // 자동으로 호출되는 메서드  -> 메인스레드가 처리할 것임
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);


            //what 값으로 분기한다

            switch (msg.what) {

                case 2:
                    famousAdvice.setText(msg.obj.toString());
                    break;
            }

        }
    }

    @Override
    protected void onPause() {  // onPause 때 스레드 종료 시킴, 다시 start 누르면 다시 진행
        super.onPause();
        isRunning = false;

    }


    private void startMainActivity() {
        Intent intent = new Intent(TimerActivity.this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();

        DdayDateLoad();
        DdayTitleLoad();
        cThread thread = new cThread();
        thread.start();

      //  Toast.makeText(this,"onResume",Toast.LENGTH_LONG).show();

    }

    public void DdayDateLoad() {

        SharedPreferences sf = getSharedPreferences("Dday", MODE_PRIVATE);
        //text라는 key에 저장된 값이 있는지 확인. 아무값도 들어있지 않으면 ""를 반환
        String DdayResult = sf.getString(user.getEmail(), "");
        DdayDate.setText(DdayResult);

    }


    public void DdayTitleLoad() {

        SharedPreferences sf = getSharedPreferences("DdayTitle", MODE_PRIVATE);
        //text라는 key에 저장된 값이 있는지 확인. 아무값도 들어있지 않으면 ""를 반환
        String Title = sf.getString(user.getEmail(),"");
        DdayTitle.setText(Title);

    }

    /*
    public void graphDataSave() {  // 그래프 데이터 저장해줄 코드

        SharedPreferences sharedPreferences = getSharedPreferences("graphData", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        try {
            jA = new JSONArray();//배열
            for (int i = 0; i < successItems.size(); i++) {

                JSONObject sObject = new JSONObject();//배열 내에 들어갈 json

                sObject.put("todayItem", successItems.get(i).getTodayItem());
                sObject.put("countItem", successItems.get(i).getCountItem());

                jA.put(sObject);

                //  Log.e("JSON Test", models.get(i).getStartTime());
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        editor.putString(user.getEmail() + timerClassMondayDate + transferDay ,jA.toString());   // 유저 이메일주소와 월요일 날짜를 뽑아낸 것 으로 유저 키를 생성
        editor.apply();


    }

    // Load 해주지 않으면 같은 key 값에 계속 중첩이 될 수 있음

    private void graphDataLoad() {  // 그래프 데이터 로드해줄 코드

        if (successItems == null) {
            successItems = new ArrayList<>();

            Toast.makeText(this,"성공아이템 NUll",Toast.LENGTH_LONG).show();

        } else {

            SharedPreferences preferences = getSharedPreferences("graphData", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();

            int Success = preferences.getInt(user.getEmail() + timerClassMondayDate + transferDay, 0);

            // String result = preferences.getString(user.getEmail(),null);  //첫번째 인자는 데이터의 키, 두번째 인자는 해당값이 없을경우 반환할 값을 넣어준다.
            // 카운트다운타이머 구현 후 값을 넣어 줄 때 이 코드 복원

            StringBuffer sb = new StringBuffer();
            Log.d("array 길이", "" + Success);

            try {
                ja = new JSONArray(Success);  // 진짜 값을 넣어 줄때는 String str 을 String result 로 변경
                Log.d("array 길이", "" + ja.length());
                for (int i = 0; i < ja.length(); i++) {
                    JSONObject jObject = ja.getJSONObject(i);
                    int todayItem = jObject.getInt("todayItem");
                    int countItem = jObject.getInt("countItem");

                    sb.append("todayItem:" + todayItem + ", countItem:" + countItem);

                SuccessItem successItem = new SuccessItem();
                successItem.setTodayItem(todayItem);
                successItem.setCountItem(countItem);


                successItems.add(successItem);

                }

                Log.d("JSON", sb.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }



        }


     */


}

