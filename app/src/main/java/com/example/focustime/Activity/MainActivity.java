package com.example.focustime.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.example.focustime.BackPressCloseHandler;
import com.example.focustime.MySayingItem;
import com.example.focustime.R;
import com.example.focustime.Service.MyService;
import com.example.focustime.Service.ServiceThread;
import com.example.focustime.StorageItem;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    LottieAnimationView lottie;

    private TextView ddayTitle;

    protected String titleResult;

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle drawerToggle;
    androidx.appcompat.widget.Toolbar toolbar;
    TextView hUserName;
    TextView hUserEmail;
    ImageView hUserImage;
    View header;


    JSONArray jArray, jarray;
    FirebaseUser user;
    String strCountResult;

    long DdaySave;

    // Millisecond 형태의 하루(24 시간)
    private final int ONE_DAY = 24 * 60 * 60 * 1000;

    // 현재 날짜를 알기 위해 사용
    private Calendar mCalendar;
    Calendar minDate = Calendar.getInstance();

    // D-day result
    private TextView mTvResult;

    // DatePicker 에서 날짜 선택 시 호출
    private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
        // 다이얼로그를 띄워서 제목을 설정할 수 있게 해주기


        @Override
        public void onDateSet(DatePicker a_view, int a_year, int a_monthOfYear, int a_dayOfMonth) {
            // D-day 계산 결과 출력
            mTvResult.setText(getDday(a_year, a_monthOfYear, a_dayOfMonth));

            String mResult = mTvResult.getText().toString(); // mResult 는 mTvResult 에 들어간 결과 값을 받음 => 알람설정을 위해 변수 선언


            // 다이얼로그를 띄워서 제목을 설정할 수 있게 해줌
            DdayTitleDialog();



            if (mResult.equals("D-day")) {
                // 만약 mResult 의 값이 D-Day 와 같다면 알람 호출
                Intent intent = new Intent(MainActivity.this, MyService.class);
                startService(intent);

            } else {

                // 아니라면 Service 멈춤
                // Toast.makeText(getApplicationContext(), "Service 끝", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, MyService.class);
                stopService(intent);

            }



        }
    };


    private static final String TAG = "MainActivity";
    private BackPressCloseHandler backPressCloseHandler;  // 뒤로가기 두번을 눌렀을 때 사용할 클래스 변수 선언

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {

                    case R.id.storage:
                        startStorageActivity();
                        //  Toast.makeText(SettingActivity.this, "Favorites", Toast.LENGTH_SHORT).show();
                        break;

                    case R.id.infor:
                        startSettingActivity();
                       // Toast.makeText(SettingActivity.this, "Nearby", Toast.LENGTH_SHORT).show();
                        break;


                }
                return true;
            }
        });










        lottie = (LottieAnimationView) findViewById(R.id.lottie);
        lottie.setAnimation("calender.json");
        lottie.playAnimation();

        initLayout();

        hUserName = (TextView) header.findViewById(R.id.header_user_name);
        hUserEmail = (TextView) header.findViewById(R.id.header_user_email);
        hUserImage = (ImageView) header.findViewById(R.id.header_user_Image);

        ddayTitle = (TextView) findViewById(R.id.dday_title_textView);

        backPressCloseHandler = new BackPressCloseHandler(this);

        user = FirebaseAuth.getInstance().getCurrentUser();  // 현재 로그인한 유저




      //  findViewById(R.id.storageButton).setOnClickListener(onClickListener);   // storage로 이동하는 버튼
      //  findViewById(R.id.settingButton).setOnClickListener(onClickListener);   // setting으로 이동하는 버튼
        findViewById(R.id.startCardView).setOnClickListener(onClickListener);   // start카드뷰 클릭시 실행할 버튼


        ///////////////////////////////////////////////////////////////////

        Locale.setDefault(Locale.KOREAN);

        // 현재 날짜를 알기 위해 사용
        mCalendar = new GregorianCalendar();

        // Today 보여주기
        TextView tvDate = findViewById(R.id.tv_date);
        tvDate.setText(getToday());

        // D-day 보여주기
        mTvResult = findViewById(R.id.tv_result);

        /*
        // Input date click 시 date picker 호출
        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View a_view) {
                final int year = mCalendar.get(Calendar.YEAR);
                final int month = mCalendar.get(Calendar.MONTH);
                final int day = mCalendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(MainActivity.this, mDateSetListener, year, month, day);

                 minDate.set(year,month,day);  // 오늘 날짜는 선택 가능하도록 함

               // minDate.set(year, month, day + 1);   // 이전 날짜, 오늘 날짜로는 선택 못하게 막음
                dialog.getDatePicker().setMinDate(minDate.getTime().getTime());
                dialog.show();
            }
        };
        findViewById(R.id.btn_input_date).setOnClickListener(clickListener);


         */



        if (FirebaseAuth.getInstance().getCurrentUser() == null) {   // 만약 현재 로그인 된 유저가 null 이라면 로그인화면으로 이동
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);   // 로그인 액티비티로 이동
        } else {

            DdayDateLoad();  // 디데이 날짜를 쉐어드에서 업로드
            DdayTitleLoad();  // 디데이 제목을 쉐어드에서 업로드

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

                                hUserName.setText(document.getData().get("name").toString());
                                hUserEmail.setText(user.getEmail());

                                // 회원정보때 Shared에 등록된 사진경로를 받아옴
                                SharedPreferences sf = getSharedPreferences("ImageChange", MODE_PRIVATE);
                                String changeUri = sf.getString(user.getEmail(), "");
                                Log.d("변경", "" + changeUri);
                                Glide.with(MainActivity.this).load(changeUri).centerCrop().override(500).into(hUserImage);


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

        }

    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {

                case R.id.startCardView:
                    startTimerActivity();
                    // 타이머 화면으로 이동
                    break;
            }
        }
    };


    private void startStorageActivity() {          // storage로 이동하는 메소드
        Intent intent = new Intent(this, StorageActivity.class);
        startActivity(intent);
    }

    private void startSettingActivity() {          // setting으로 이동하는 메소드
        Intent intent = new Intent(this, SettingActivity.class);
        startActivity(intent);
    }


    private void startTimerActivity() {          // setting으로 이동하는 메소드
        Intent intent = new Intent(this, TimerActivity.class);
        startActivity(intent);
    }

    private void startMainActivity() {          // setting으로 이동하는 메소드
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }


    private void startToast(String msg) {

        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * Today 반환
     */
    private String getToday() {   // 오늘 날짜를 표시해줌 ex) 2019년 10월 29일 화요일
        // 지정된 format 으로 string 표시
        final String strFormat = getString(R.string.format_today);
        SimpleDateFormat CurDateFormat = new SimpleDateFormat(strFormat);


        return CurDateFormat.format(mCalendar.getTime());
    }

    /**
     * D-day 반환
     */
    private String getDday(int a_year, int a_monthOfYear, int a_dayOfMonth) {
        // D-day 설정
        final Calendar ddayCalendar = Calendar.getInstance();
        ddayCalendar.set(a_year, a_monthOfYear, a_dayOfMonth);

        // D-day 를 구하기 위해 millisecond 으로 환산하여 d-day 에서 today 의 차를 구한다.
        final long dday = ddayCalendar.getTimeInMillis() / ONE_DAY;
        final long today = Calendar.getInstance().getTimeInMillis() / ONE_DAY;
        long result = dday - today;

        DdaySave = result;

       // save();

        // 출력 시 d-day 에 맞게 표시
        final String strFormat;

        if (result > 0) {                  // 디데이 - 오늘날짜가 0보다 크다면  = 디데이가 오늘날짜보다 클 때 => D-%d 로 출력
            strFormat = "D-%d";
        } else if (result == 0) {          // 디데이 - 오늘날짜가 = 0 이라면  =  디데이와 오늘날짜가 같다면 => D-Day 로 출력
            strFormat = "D-day";

            // 이때 알람울리도록 설정

        } else {
            result *= -1;                   // 디데이 - 오늘날짜가 0보다 작다면  = 디데이가 오늘날짜보다 작을 때 => D+%d 로 출력
            strFormat = "D+%d";
        }

        final String strCount = (String.format(strFormat, result));
        strCountResult = strCount;
        DdayDateSave();
        return strCount;
    }


    public void DdayDateSave() {
        SharedPreferences sharedPreferences = getSharedPreferences("Dday", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String DdayResult = strCountResult; // 사용자가 저장할 데이터
        editor.putString(user.getEmail(), DdayResult); // key, value를 이용하여 저장하는 형태

        editor.commit();


    }


    public void DdayDateLoad() {

        SharedPreferences sf = getSharedPreferences("Dday", MODE_PRIVATE);
        //text라는 key에 저장된 값이 있는지 확인. 아무값도 들어있지 않으면 ""를 반환
        String DdayResult = sf.getString(user.getEmail(), "");
        mTvResult.setText(DdayResult);


        if(DdayResult.length() > 0 ) {  // Shared 에 저장된 DdayResult 가 있다면

            if (DdayResult.equals("D-day")) {
                // 만약 mResult 의 값이 D-Day 와 같다면 알람 호출
                Intent intent = new Intent(MainActivity.this, MyService.class);
                startService(intent);

            } else {

                // 아니라면 Service 멈춤
                // Toast.makeText(getApplicationContext(), "Service 끝", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, MyService.class);
                stopService(intent);

            }

        }


    }



    public void DdayTitleSave() {
        SharedPreferences sharedPreferences = getSharedPreferences("DdayTitle", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String DdayTitleResult = titleResult; // 사용자가 저장할 데이터
        editor.putString(user.getEmail(),DdayTitleResult); // key, value를 이용하여 저장하는 형태

        editor.commit();

    }


    public void DdayTitleLoad() {

        SharedPreferences sf = getSharedPreferences("DdayTitle", MODE_PRIVATE);
        //text라는 key에 저장된 값이 있는지 확인. 아무값도 들어있지 않으면 ""를 반환
        String DdayTitle = sf.getString(user.getEmail(), "");

        ddayTitle.setText(DdayTitle);

    }



    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {    // 눌렀을 때 어떻게 할지
        switch (item.getItemId()) {
            case R.id.item1:  // 메모장으로 이동
                startMysayingActivity();
                // Toast.makeText(this, "item1 clicked..", Toast.LENGTH_SHORT).show();
                break;
            case R.id.item2:    // 비밀번호 변경
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage("비밀번호를 변경하시겠습니까?");
                builder.setTitle("알림")
                        .setCancelable(false)
                        .setPositiveButton("네", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();  // 다이얼로그를 종료 시켜준다
                                // 비밀번호를 변경 한다고 했을 때의 로직 작성
                                passwordChange();

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

                // Toast.makeText(this, "item2 clicked..", Toast.LENGTH_SHORT).show();
                break;
            case R.id.item3:    // 로그아웃
                logOut();
                // Toast.makeText(this, "로그아웃", Toast.LENGTH_SHORT).show();
                break;
            case R.id.item4:    // 회원탈퇴
                userDelete();

                //  Toast.makeText(this, "item4 clicked..", Toast.LENGTH_SHORT).show();
                break;
            case R.id.select_dday:    // D-day 설정할 수 있는 버튼

                final int year = mCalendar.get(Calendar.YEAR);
                final int month = mCalendar.get(Calendar.MONTH);
                final int day = mCalendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(MainActivity.this, mDateSetListener, year, month, day);

                // minDate.set(year,month,day);  // 오늘 날짜는 선택 가능하도록 함

                 minDate.set(year, month, day + 1);   // 이전 날짜, 오늘 날짜로는 선택 못하게 막음
                dialog.getDatePicker().setMinDate(minDate.getTime().getTime());
                dialog.show();

                break;
            case R.id.delete_dday:    // D-day 삭제할 수 있는 버튼

                // 간단한 다이얼로그를 띄워서 D-day 날짜와 제목을 삭제시켜 준다
                ddayDelete();

                break;

            case R.id.weekData:
               // Intent intent = new Intent(this,GraphActivity.class);
                Intent intent = new Intent(this,MyGraphActivity.class);
                startActivity(intent);

                break;



        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return false;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle your other action bar items...

        return super.onOptionsItemSelected(item);
    }

    private void initLayout() {

        toolbar = (androidx.appcompat.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("FocusTime");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);

        drawerLayout = (DrawerLayout) findViewById(R.id.dl_main_drawer_root);
        navigationView = (NavigationView) findViewById(R.id.nv_main_navigation_root);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(drawerToggle);
        navigationView.setNavigationItemSelectedListener(this);

        header = navigationView.getHeaderView(0);

    }


    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            backPressCloseHandler.onBackPressed();    // 뒤로가기 두번 클릭시 어플 꺼짐
        }

    }

    private void startLoginActivity() {          // 나만의 명언으로 이동하는 메소드
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    private void startMysayingActivity() {          // 나만의 명언으로 이동하는 메소드
        Intent intent = new Intent(this, MySayingActivity.class);
        startActivity(intent);
    }

    private void logOut() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("로그아웃 하시겠습니까?");
        builder.setTitle("알림")
                .setCancelable(false)
                .setPositiveButton("네", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();  // 다이얼로그를 종료 시켜준다
                        FirebaseAuth.getInstance().signOut();  // 파이어베이스 사용자 로그아웃이 됨.
                        startLoginActivity();    //로그인 화면으로 이동
                        finish();   // 현재화면은 꺼짐
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

    private void passwordChange() {

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);

        // 다이얼로그를 보여주기 위해 activity_floating_action.xml 파일을 사용합니다.

        View view = LayoutInflater.from(this)
                .inflate(R.layout.activity_password_reset, null, false);
        builder.setView(view);

        final Button ButtonSubmit = view.findViewById(R.id.sendButton);
        final EditText cEmail = view.findViewById(R.id.emailEditText);

        final android.app.AlertDialog dialog = builder.create();
        ButtonSubmit.setOnClickListener(new View.OnClickListener() {


            public void onClick(View v) {


                String strEmail = cEmail.getText().toString();

                if (strEmail.length() > 0) {

                    final RelativeLayout loaderLayout = findViewById(R.id.loaderLayout);
                    loaderLayout.setVisibility(View.VISIBLE);

                    FirebaseAuth auth = FirebaseAuth.getInstance();

                    auth.sendPasswordResetEmail(strEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {
                                loaderLayout.setVisibility(View.GONE);
                                startToast("이메일을 보냈습니다");
                            }

                        }
                    });

                } else {

                    startToast("정확한 이메일을 입력해주세요");
                }


                dialog.dismiss();
            }
        });

        dialog.show();
    }


    private void userDelete() {

        final RelativeLayout loaderLayout = findViewById(R.id.loaderLayout);

        final FirebaseUser use = FirebaseAuth.getInstance().getCurrentUser();
        AlertDialog.Builder alert_confirm = new AlertDialog.Builder(MainActivity.this);
        alert_confirm.setMessage("정말 탈퇴하시겠습니까?").setCancelable(false).setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {


                        loaderLayout.setVisibility(View.VISIBLE);

                        use.delete()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        loaderLayout.setVisibility(View.INVISIBLE);
                                        Toast.makeText(MainActivity.this, "계정이 삭제 되었습니다.", Toast.LENGTH_LONG).show();
                                        finish();
                                        startLoginActivity();
                                    }
                                });
                    }
                }
        );
        alert_confirm.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                loaderLayout.setVisibility(View.INVISIBLE);
                Toast.makeText(MainActivity.this, "취소", Toast.LENGTH_LONG).show();
            }
        });
        alert_confirm.show();
    }


    private void DdayTitleDialog() {   // 디데이 제목 입력해줄 다이얼로그 메서드

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);

        // 다이얼로그를 보여주기 위해 activity_floating_action.xml 파일을 사용합니다.

        View view = LayoutInflater.from(this)
                .inflate(R.layout.dday_title_screen, null, false);
        builder.setView(view);

        final Button titleSaveBtn = view.findViewById(R.id.title_saveBtn);
        final EditText cTitle = view.findViewById(R.id.DdayTitleEditText);


        //  final ImageView cProfileImage = (ImageView)view.findViewById(R.id.imageMemo);


        final android.app.AlertDialog dialog = builder.create();
        titleSaveBtn.setOnClickListener(new View.OnClickListener() {


            public void onClick(View v) {

                String strTitle = cTitle.getText().toString();

                if(strTitle.length() > 0) {
                    ddayTitle.setText(strTitle);

                    titleResult = ddayTitle.getText().toString();  // Shared 에 저장시킬 디데이 제목
                    DdayTitleSave(); // d-day 쉐어드에 저장

                    dialog.dismiss();

                } else {
                    startToast("D-day 제목을 입력해주세요");
                }

            }
        });

        dialog.show();

    }


    private void ddayDelete() {  // 디데이 정보 삭제

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("삭제하시겠습니까?");
        builder.setTitle("알림")
                .setCancelable(false)
                .setPositiveButton("네", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();  // 다이얼로그를 종료 시켜준다


                        deleteDdayDate();
                        deleteDdayTitle();

                        Intent intent = new Intent(MainActivity.this, MyService.class);
                        stopService(intent);

                        mTvResult.setText("");
                        ddayTitle.setText("");


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

    private void deleteDdayDate() {

        SharedPreferences preferences = getSharedPreferences("Dday", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(user.getEmail());
        editor.commit();

    }

    private void deleteDdayTitle() {

        SharedPreferences sharedPreferences = getSharedPreferences("DdayTitle", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(user.getEmail());
        editor.commit();
    }


}




