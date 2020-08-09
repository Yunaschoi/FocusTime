package com.example.focustime.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.focustime.R;
import com.example.focustime.StorageItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.w3c.dom.Text;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class GraphActivity extends AppCompatActivity {

    protected TextView dateTextView1;
    protected TextView dateTextView2;
    protected ImageView beforeImageView;
    protected ImageView afterImageView;
    protected Calendar calendar, calendar2;
    protected SimpleDateFormat df;
    protected String[] formattedDate, formattedDate2;
    protected FirebaseUser user;
    protected ArrayList<StorageItem> models = new ArrayList<>();  // 모든 데이터를 담을 리스트
    protected Toolbar toolbar;

    protected String mondayDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        user = FirebaseAuth.getInstance().getCurrentUser();

        toolbar = findViewById(R.id.graph_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("   주간 기록");
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);


        dateTextView1 = (TextView) findViewById(R.id.newDate1);   // 주간 기록 시작날짜
        dateTextView2 = (TextView) findViewById(R.id.newDate2);   // 주간 기록 끝나는 날짜
        beforeImageView = (ImageView) findViewById(R.id.BeforeView);
        afterImageView = (ImageView) findViewById(R.id.AfterView);


        calendar = Calendar.getInstance(); // 오늘 날짜

        calendar2 = Calendar.getInstance(); // 오늘 날짜


        df = new SimpleDateFormat("yyyy.MM.dd", Locale.getDefault());

        formattedDate = new String[]{df.format(calendar.getTime())};

        formattedDate2 = new String[]{df.format(calendar.getTime())};


        // 오늘 요일을 뽑아내주는 코드

        Date currentTime = Calendar.getInstance().getTime();  // 오늘 날짜
        SimpleDateFormat weekdayFormat = new SimpleDateFormat("EE", Locale.getDefault());
        //  SimpleDateFormat dayFormat = new SimpleDateFormat("dd", Locale.getDefault());
        //  SimpleDateFormat monthFormat = new SimpleDateFormat("MM", Locale.getDefault());
        //  SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());

        String weekDay = weekdayFormat.format(currentTime);  // 오늘 요일
        //  String year = yearFormat.format(currentTime);
        //  String month = monthFormat.format(currentTime);
        //  String day = dayFormat.format(currentTime);

        Log.d("weekDay", weekDay);
        //   Log.d("year",year);
        //   Log.d("month",month);
        //   Log.d("day",day);

        //  String outputMon = year+"."+month+"."+day;  // 오늘 날짜가 월요일일에 dateTextView1 에 넣어줄 데이터


        if (weekDay.equals("월")) {  // 오늘 날짜가 월요일이라면

            calendar.add(Calendar.DATE, +0);
            formattedDate[0] = df.format(calendar.getTime());

            mondayDate = formattedDate[0];  // onCreate 때 쉐어드 키로 사용할 월요일 날짜 값

            calendar2.add(Calendar.DATE, +6);
            formattedDate2[0] = df.format(calendar2.getTime());

            Log.d("formattedDate[0]", formattedDate[0]);
            Log.d("formattedDate2[0]", formattedDate2[0]);

            dateTextView1.setText(formattedDate[0]);    // calendar 와 formattedDate 값을 사용
            dateTextView2.setText(formattedDate2[0]);   // calendar2 와 formattedDate2 값을 사용

        } else if (weekDay.equals("화")) {

            calendar.add(Calendar.DATE, -1);
            formattedDate[0] = df.format(calendar.getTime());

            mondayDate = formattedDate[0];  // onCreate 때 쉐어드 키로 사용할 월요일 날짜 값

            calendar2.add(Calendar.DATE, +5);
            formattedDate2[0] = df.format(calendar2.getTime());

            Log.d("formattedDate[0]", formattedDate[0]);
            Log.d("formattedDate2[0]", formattedDate2[0]);

            dateTextView1.setText(formattedDate[0]);    // calendar 와 formattedDate 값을 사용
            dateTextView2.setText(formattedDate2[0]);   // calendar2 와 formattedDate2 값을 사용

        } else if (weekDay.equals("수")) {

            calendar.add(Calendar.DATE, -2);
            formattedDate[0] = df.format(calendar.getTime());

            mondayDate = formattedDate[0];  // onCreate 때 쉐어드 키로 사용할 월요일 날짜 값

            calendar2.add(Calendar.DATE, +4);
            formattedDate2[0] = df.format(calendar2.getTime());

            Log.d("formattedDate[0]", formattedDate[0]);
            Log.d("formattedDate2[0]", formattedDate2[0]);

            dateTextView1.setText(formattedDate[0]);    // calendar 와 formattedDate 값을 사용
            dateTextView2.setText(formattedDate2[0]);   // calendar2 와 formattedDate2 값을 사용

        } else if (weekDay.equals("목")) {

            calendar.add(Calendar.DATE, -3);
            formattedDate[0] = df.format(calendar.getTime());

            mondayDate = formattedDate[0];  //onCreate 때 쉐어드 키로 사용할 월요일 날짜 값

            calendar2.add(Calendar.DATE, +3);
            formattedDate2[0] = df.format(calendar2.getTime());

            Log.d("formattedDate[0]", formattedDate[0]);
            Log.d("formattedDate2[0]", formattedDate2[0]);

            dateTextView1.setText(formattedDate[0]);    // calendar 와 formattedDate 값을 사용
            dateTextView2.setText(formattedDate2[0]);   // calendar2 와 formattedDate2 값을 사용

        } else if (weekDay.equals("금")) {

            calendar.add(Calendar.DATE, -4);
            formattedDate[0] = df.format(calendar.getTime());

            mondayDate = formattedDate[0];  // onCreate 때 쉐어드 키로 사용할 월요일 날짜 값

            calendar2.add(Calendar.DATE, +2);
            formattedDate2[0] = df.format(calendar2.getTime());

            Log.d("formattedDate[0]", formattedDate[0]);
            Log.d("formattedDate2[0]", formattedDate2[0]);

            dateTextView1.setText(formattedDate[0]);    // calendar 와 formattedDate 값을 사용
            dateTextView2.setText(formattedDate2[0]);   // calendar2 와 formattedDate2 값을 사용

        } else if (weekDay.equals("토")) {

            calendar.add(Calendar.DATE, -5);
            formattedDate[0] = df.format(calendar.getTime());

            mondayDate = formattedDate[0];  // onCreate 때 쉐어드 키로 사용할 월요일 날짜 값

            calendar2.add(Calendar.DATE, +1);
            formattedDate2[0] = df.format(calendar2.getTime());

            Log.d("formattedDate[0]", formattedDate[0]);
            Log.d("formattedDate2[0]", formattedDate2[0]);

            dateTextView1.setText(formattedDate[0]);    // calendar 와 formattedDate 값을 사용
            dateTextView2.setText(formattedDate2[0]);   // calendar2 와 formattedDate2 값을 사용

        } else if (weekDay.equals("일")) {

            calendar.add(Calendar.DATE, -6);
            formattedDate[0] = df.format(calendar.getTime());

            mondayDate = formattedDate[0];  // onCreate 때 쉐어드 키로 사용할 월요일 날짜 값

            calendar2.add(Calendar.DATE, +0);
            formattedDate2[0] = df.format(calendar2.getTime());

            Log.d("formattedDate[0]", formattedDate[0]);
            Log.d("formattedDate2[0]", formattedDate2[0]);

            dateTextView1.setText(formattedDate[0]);    // calendar 와 formattedDate 값을 사용
            dateTextView2.setText(formattedDate2[0]);   // calendar2 와 formattedDate2 값을 사용

        }

        /////////////////////////////////////////////////////////////////////////////////


        beforeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                calendar.add(Calendar.DATE, -7);
                formattedDate[0] = df.format(calendar.getTime());

                mondayDate = formattedDate[0]; // 이전 버튼을 눌렀을 때 쉐어드 키로 사용할 월요일 날짜 값

                calendar2.add(Calendar.DATE, -7);
                formattedDate2[0] = df.format(calendar2.getTime());

                Log.d("formattedDate[0]", formattedDate[0]);
                Log.d("formattedDate2[0]", formattedDate2[0]);

                dateTextView1.setText(formattedDate[0]);    // calendar 와 formattedDate 값을 사용
                dateTextView2.setText(formattedDate2[0]);   // calendar2 와 formattedDate2 값을 사용

            }
        });

        afterImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendar.add(Calendar.DATE, +7);
                formattedDate[0] = df.format(calendar.getTime());

                mondayDate = formattedDate[0]; // 다음 버튼을 눌렀을 때 쉐어드 키로 사용할 월요일 날짜 값

                calendar2.add(Calendar.DATE, +7);
                formattedDate2[0] = df.format(calendar2.getTime());

                Log.d("formattedDate[0]", formattedDate[0]);
                Log.d("formattedDate2[0]", formattedDate2[0]);

                dateTextView1.setText(formattedDate[0]);    // calendar 와 formattedDate 값을 사용
                dateTextView2.setText(formattedDate2[0]);   // calendar2 와 formattedDate2 값을 사용

            }
        });


    }


    /*
    public void graphDataSave() {  // 그래프 데이터 저장해줄 코드
        SharedPreferences sharedPreferences = getSharedPreferences("graphData", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(user.getEmail() + mondayDate, json);   // 유저 이메일주소와 월요일 날짜를 뽑아낸 것 으로 유저 키를 생성
        editor.apply();
    }

     */

    private void graphDataLoad() {  // 그래프 데이터 로드해줄 코드
        SharedPreferences sharedPreferences = getSharedPreferences("graphData", MODE_PRIVATE);
        String graphResult = sharedPreferences.getString(user.getEmail() + mondayDate, "");  // 유저 이메일주소와 월요일 날짜를 뽑아낸 것 으로 유저 키를 생성


    }

}
