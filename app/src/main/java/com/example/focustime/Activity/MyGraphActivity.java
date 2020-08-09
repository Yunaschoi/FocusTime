package com.example.focustime.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.focustime.Adapter.StorageAdapter;
import com.example.focustime.R;
import com.example.focustime.StorageItem;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class MyGraphActivity extends AppCompatActivity {

    TextView dateView;
    ImageView afterView;
    ImageView beforeView;
    private String[] formattedDate;
    Calendar calendar;
    SimpleDateFormat df;
    Toolbar toolbar;
    private ArrayList<StorageItem> models = new ArrayList<>();  // 모든 데이터를 담을 리스트

    private ArrayList<String> successModels = new ArrayList<>();
    private ArrayList<String> failModels = new ArrayList<>();

    private RecyclerView recyclerView;
    private StorageAdapter storageAdapter;
    JSONArray jArray,jarray;
    BarChart barChart;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_graph);

        barChart = (BarChart) findViewById(R.id.barchart);

        toolbar = findViewById(R.id.myGraph_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("   내 기록 그래프");
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);


        user =  FirebaseAuth.getInstance().getCurrentUser();

        dateView = (TextView) findViewById(R.id.DateView);
        afterView = (ImageView) findViewById(R.id.AfterView);
        beforeView = (ImageView) findViewById(R.id.BeforeView);


        calendar = Calendar.getInstance();
        df = new SimpleDateFormat("yyyy.MM.dd. EE");
        formattedDate = new String[]{df.format(calendar.getTime())};

        dateView.setText(formattedDate[0]);


        loadData();


        beforeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                barChart = (BarChart) findViewById(R.id.barchart);

                calendar.add(Calendar.DATE, -1);
                formattedDate[0] = df.format(calendar.getTime());
                dateView.setText(formattedDate[0]);

                successModels = new ArrayList<>();
                failModels = new ArrayList<>();

                loadData();
                // load();
                Log.d("model 길이",""+models.size());

                ArrayList<BarEntry> entries = new ArrayList<>();
                entries.add(new BarEntry((float)successModels.size(), 0));
                entries.add(new BarEntry((float)failModels.size(), 1));

                BarDataSet bardataset = new BarDataSet(entries,"기록");

                ArrayList<String> labels = new ArrayList<String>();
                labels.add("성공");
                labels.add("실패");


                BarData data = new BarData(labels, bardataset);
                barChart.setData(data); // set the data and list of labels into chart
                bardataset.setColors(ColorTemplate.COLORFUL_COLORS);
                barChart.setDescription("");
                barChart.animateY(2000);

                XAxis xAxis = barChart.getXAxis();
                xAxis.setDrawGridLines(true);
                xAxis.setPosition(XAxis.XAxisPosition.TOP);
                xAxis.setTextSize(15f);

                barChart.getAxisRight().setEnabled(false);
                YAxis leftAxix = barChart.getAxisLeft();
                leftAxix.setValueFormatter(new LargeValueFormatter());
                leftAxix.setDrawGridLines(true);
                leftAxix.setSpaceTop(35f);
                leftAxix.setAxisMinValue(0f);
                leftAxix.setAxisMaxValue(30f);

                Legend legend = barChart.getLegend();
                legend.setEnabled(false);



            }
        });

        afterView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                barChart = (BarChart) findViewById(R.id.barchart);

                calendar.add(Calendar.DATE, +1);
                formattedDate[0] = df.format(calendar.getTime());
                dateView.setText(formattedDate[0]);

                successModels = new ArrayList<>();
                failModels = new ArrayList<>();

                loadData();

                Log.d("model 길이",""+models.size());
                ArrayList<BarEntry> entries = new ArrayList<>();
                entries.add(new BarEntry((float)successModels.size(), 0));
                entries.add(new BarEntry((float)failModels.size(), 1));


                BarDataSet bardataset = new BarDataSet(entries,"기록");

                ArrayList<String> labels = new ArrayList<String>();
                labels.add("성공");
                labels.add("실패");


                BarData data = new BarData(labels, bardataset);
                barChart.setData(data); // set the data and list of labels into chart
                bardataset.setColors(ColorTemplate.COLORFUL_COLORS);
                barChart.setDescription("");
                barChart.animateY(2000);

                XAxis xAxis = barChart.getXAxis();
                xAxis.setDrawGridLines(true);
                xAxis.setPosition(XAxis.XAxisPosition.TOP);
                xAxis.setTextSize(15f);


                barChart.getAxisRight().setEnabled(false);
                YAxis leftAxix = barChart.getAxisLeft();
                leftAxix.setValueFormatter(new LargeValueFormatter());
                leftAxix.setDrawGridLines(true);
                leftAxix.setSpaceTop(35f);
                leftAxix.setAxisMinValue(0f);
                leftAxix.setAxisMaxValue(30f);

                Legend legend = barChart.getLegend();
                legend.setEnabled(false);



            }
        });




        ArrayList<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry((float)successModels.size(), 0));
        entries.add(new BarEntry((float)failModels.size(), 1));


        BarDataSet bardataset = new BarDataSet(entries,"기록");

      //  ArrayList<String> labels = new ArrayList<String>();
      //  labels.add("성공");
      //  labels.add("실패");

        String [] labels = new String[] {"성공","실패"};


        BarData data = new BarData(labels, bardataset);
        barChart.setData(data); // set the data and list of labels into chart
        bardataset.setColors(ColorTemplate.COLORFUL_COLORS);
        barChart.setDescription("");
        barChart.animateY(2000);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setDrawGridLines(true);
        xAxis.setPosition(XAxis.XAxisPosition.TOP);
        xAxis.setTextSize(15f);


        barChart.getAxisRight().setEnabled(false);
        YAxis leftAxix = barChart.getAxisLeft();
        leftAxix.setValueFormatter(new LargeValueFormatter());
        leftAxix.setDrawGridLines(true);
        leftAxix.setSpaceTop(35f);
        leftAxix.setAxisMinValue(0f);
        leftAxix.setAxisMaxValue(30f);

        Legend legend = barChart.getLegend();
        legend.setEnabled(false);


    }



    public void loadData() {

        if(models == null) {
            models = new ArrayList<>();
        } else {

            SharedPreferences preferences = getApplicationContext().getSharedPreferences("mFile", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();

            // String result = preferences.getString(user.getEmail(),null);  //첫번째 인자는 데이터의 키, 두번째 인자는 해당값이 없을경우 반환할 값을 넣어준다.
            // 카운트다운타이머 구현 후 값을 넣어 줄 때 이 코드 복원

            // String result = preferences.getString(user.getEmail() + newDate, "");
            String result = preferences.getString(user.getEmail() + formattedDate[0], "");

            Log.d("Key", user.getEmail() + formattedDate[0]);

            StringBuffer sb = new StringBuffer();
            Log.d("어레이 길이",""+result);
            try {
                jarray = new JSONArray(result);  // 진짜 값을 넣어 줄때는 String str 을 String result 로 변경
                Log.d("어레이 길이",""+jarray.length());

                for (int i = 0; i < jarray.length(); i++) {
                    JSONObject jObject = jarray.getJSONObject(i);

                    String startTime = jObject.getString("startTime");
                    String tryTitle = jObject.getString("tryTitle");
                    String saveTime = jObject.getString("saveTime");
                    String createAt = jObject.getString("createAt");


                    sb.append("startTime:" + startTime + ", tryTitle:" + tryTitle + ", saveTime:" + saveTime + ", createAt:" + createAt + "\n");

                    StorageItem storageItem = new StorageItem(startTime,saveTime,tryTitle,createAt); // 오늘 날짜(월/일)를 저장시켜줘야됨

                    if(storageItem.getTryTitle().equals("실패")) {

                            failModels.add(storageItem.getTryTitle());


                    } else {

                        successModels.add(storageItem.getTryTitle());
                    }

                    models.add(storageItem);

                 //   storageAdapter.notifyItemInserted(i); // 몇번째 추가될 아이템인지 알려줌

                }

                Log.d("실패모델",""+failModels.size());
                Log.d("성공모델",""+successModels.size());

                Log.d("JSON", sb.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            // editor.commit();

        }
    }



    public void saveData() {



        SharedPreferences sharedPreferences = getSharedPreferences("mFile",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // JSON 으로 변환
        try {
            jArray = new JSONArray();//배열
            for (int i = 0; i < models.size(); i++) {
                JSONObject sObject = new JSONObject();//배열 내에 들어갈 json
                sObject.put("startTime", models.get(i).getStartTime());
                sObject.put("tryTitle", models.get(i).getTryTitle());
                sObject.put("saveTime", models.get(i).getSaveTime());
                sObject.put("createAt",models.get(i).getCreateAt());
                jArray.put(sObject);
            }

            Log.e("JSON Test", jArray.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        editor.putString(user.getEmail()+formattedDate[0],jArray.toString());
        editor.apply();

    }


}
