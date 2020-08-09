package com.example.focustime.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.focustime.Adapter.MySayingAdapter;
import com.example.focustime.Adapter.StorageAdapter;
import com.example.focustime.MySayingItem;
import com.example.focustime.R;
import com.example.focustime.StorageItem;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

public class StorageActivity extends AppCompatActivity {

    Toolbar toolbar;

    SharedPreferences sortPreferences;
    FirebaseUser user;
    private RecyclerView recyclerView;
    private StorageAdapter storageAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<StorageItem> models = new ArrayList<>();  // 모든 데이터를 담을 리스트
    public String[] formattedDate;
    private ImageView imageView;

    ImageView previousDay;
    ImageView nextDay;
    Calendar calendar;
    SimpleDateFormat df;
    TextView DateTextView;

    JSONArray jArray,jarray;
    JSONObject jsonObject;

    private String newDate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storage);


        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.main:
                        startMainActivity();
                        // Toast.makeText(SettingActivity.this, "Recents", Toast.LENGTH_SHORT).show();
                        break;
                        /*
                    case R.id.storage:
                        startStorageActivity();
                        //  Toast.makeText(SettingActivity.this, "Favorites", Toast.LENGTH_SHORT).show();
                        break;

                         */

                    case R.id.infor:
                       startSettingActivity();
                        // Toast.makeText(SettingActivity.this, "Nearby", Toast.LENGTH_SHORT).show();
                        break;


                }
                return true;
            }
        });


        toolbar = findViewById(R.id.storage_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("   내 기록");
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setIcon(R.drawable.ic_action_sort);

        sortPreferences = this.getSharedPreferences("StorageSortPref", MODE_PRIVATE);
        user =  FirebaseAuth.getInstance().getCurrentUser();

        imageView = (ImageView) findViewById(R.id.tryTitleRevise);

        calendar = Calendar.getInstance();
        df = new SimpleDateFormat("yyyy.MM.dd. EE");

        formattedDate = new String[]{df.format(calendar.getTime())};

        DateTextView = (TextView)findViewById(R.id.Date);
        DateTextView.setText(formattedDate[0]);


        recyclerView = findViewById(R.id.RecordRecyclerView);
      //  findViewById(R.id.mainButton).setOnClickListener(onClickListener);   // 메인으로 이동하는 버튼
      //  findViewById(R.id.settingButton).setOnClickListener(onClickListener);   // setting으로 이동하는 버튼

        previousDay = (ImageView) findViewById(R.id.BeforeImageView);  // 왼쪽 화살표키를 눌렀을 때 이전 날짜로 이동하는 버튼
        nextDay = (ImageView) findViewById(R.id.AfterImageView);  // 오른쪽 화살표키를 눌렀을 때 다음 날짜로 이동하는 버튼


        /*

        previousDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // models.clear(); // recyclerView 아이템들 삭제

                calendar.add(Calendar.DATE, -1);
                formattedDate[0] = df.format(calendar.getTime());
                DateTextView.setText(formattedDate[0]);

                models = new ArrayList<>();
                loadData();

                Log.d("model 길이",""+models.size());

                storageAdapter.notifyDataSetChanged();
                recyclerView.setAdapter(storageAdapter);


            }
        });

        nextDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendar.add(Calendar.DATE, 1);
                formattedDate[0] = df.format(calendar.getTime());

                DateTextView.setText(formattedDate[0]);

                models = new ArrayList<>();
                loadData();

                Log.d("model 길이",""+models.size());

                storageAdapter.notifyDataSetChanged();
                recyclerView.setAdapter(storageAdapter);

            }
        });

         */

        // newDate = formattedDate[0];
        storageAdapter = new StorageAdapter(models,this);
        loadData();
        // load();
        recyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);

        recyclerView.setLayoutManager(mLayoutManager);

        recyclerView.setAdapter(storageAdapter);

       // loadData();

        previousDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // models.clear(); // recyclerView 아이템들 삭제


                calendar.add(Calendar.DATE, -1);
                formattedDate[0] = df.format(calendar.getTime());
                DateTextView.setText(formattedDate[0]);

                models = new ArrayList<>();
              //  storageAdapter = new StorageAdapter(models,StorageActivity.this);

                // DateTextView = formattedDate[0];

                loadData();
               // load();
                Log.d("model 길이",""+models.size());

                storageAdapter.notifyDataSetChanged();

                storageAdapter = new StorageAdapter(models,StorageActivity.this);

                recyclerView.setAdapter(storageAdapter);
               // save();
                saveData();
            }
        });

        nextDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                calendar.add(Calendar.DATE, 1);
                formattedDate[0] = df.format(calendar.getTime());

                DateTextView.setText(formattedDate[0]);

                models = new ArrayList<>();
              // storageAdapter = new StorageAdapter(models,StorageActivity.this);
                 loadData();
               // load();
                Log.d("model 길이",""+models.size());

                storageAdapter.notifyDataSetChanged();

                storageAdapter = new StorageAdapter(models,StorageActivity.this);

                recyclerView.setAdapter(storageAdapter);
               // save();
                saveData();
            }
        });


        /*

        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat weekdayFormat = new SimpleDateFormat("EE", Locale.getDefault());
        SimpleDateFormat dayFormat = new SimpleDateFormat("dd", Locale.getDefault());
        SimpleDateFormat monthFormat = new SimpleDateFormat("MM", Locale.getDefault());
        SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());

        String weekDay = weekdayFormat.format(currentTime);
        String year = yearFormat.format(currentTime);
        String month = monthFormat.format(currentTime);
        String day = dayFormat.format(currentTime);

         */


        //  Date currentTime = Calendar.getInstance().getTime();
        //  String date_text = new SimpleDateFormat("yyyy. MM. dd. EE요일", Locale.getDefault()).format(currentTime);



       // loadData();

        /*
        recyclerView = findViewById(R.id.sayingRecyclerView);
        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mySayingAdapter = new MySayingAdapter(this, models);

        recyclerView.setAdapter(mySayingAdapter);

         */


        /*

        storageAdapter.setOnItemClickListener(new StorageAdapter.OnItemClickListener() {
            @Override
            public void reviseBtnClick(int positon) {

                models.remove(positon);
                storageAdapter.notifyItemRemoved(positon);

            }

        });

         */



    }


    /*

    @Override
    protected void onResume() {
        super.onResume();


        previousDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // models.clear(); // recyclerView 아이템들 삭제

                calendar.add(Calendar.DATE, -1);
                formattedDate[0] = df.format(calendar.getTime());
                DateTextView.setText(formattedDate[0]);

                models = new ArrayList<>();
                storageAdapter = new StorageAdapter(models,StorageActivity.this);
                newDate = formattedDate[0];
                loadData();

                Log.d("model 길이",""+models.size());

                storageAdapter.notifyDataSetChanged();
                recyclerView.setAdapter(storageAdapter);


            }
        });


        nextDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendar.add(Calendar.DATE, 1);
                formattedDate[0] = df.format(calendar.getTime());

                DateTextView.setText(formattedDate[0]);

                models = new ArrayList<>();
                storageAdapter = new StorageAdapter(models,StorageActivity.this);

                loadData();
                Log.d("model 길이",""+models.size());

                storageAdapter.notifyDataSetChanged();
                recyclerView.setAdapter(storageAdapter);

            }
        });
    }

     */

    @Override
    protected void onStop() {
        super.onStop();

       // save();
        saveData();
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

                    // 생성자에 구조를 짜놨으므로 밑의 코드는 안써도 됨
                    // 생성자를 작성하지 않았다면 밑의 코드를 작성해서 값을 넣어줘야됨
                   // storageItem.setStartTime(startTime);
                   // storageItem.setTryTitle(tryTitle);
                   // storageItem.setSaveTime(saveTime);
                   // storageItem.setCreateAt(createAt);

                    models.add(storageItem);

                    storageAdapter.notifyItemInserted(i); // 몇번째 추가될 아이템인지 알려줌

                }
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

    public void save() {
        SharedPreferences sharedPreferences = getSharedPreferences("mFile",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(models);
        editor.putString(user.getEmail()+formattedDate[0],json);
        editor.apply();
    }

    private void load() {
        SharedPreferences sharedPreferences = getSharedPreferences("mFile", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString(user.getEmail()+formattedDate[0], null);
        Type type = new TypeToken<ArrayList<StorageItem>>() {}.getType();
        models = gson.fromJson(json, type);

        if (models == null) {
            models = new ArrayList<>();
        }
    }




    private void startMainActivity() {          // 메인으로 이동하는 메소드
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void startSettingActivity() {          // setting으로 이동하는 메소드
        Intent intent = new Intent(this, SettingActivity.class);
        startActivity(intent);
    }

    private void getSayingItemList() {

        // models = new ArrayList<>();


        String mSortSetting = sortPreferences.getString("정렬","등록시간 순");  // 처음 정렬하는 방식을 지정

        if(mSortSetting.equals("가나다 순")) {
            Collections.sort(models,StorageItem.By_TITLE_ASCENDING);
        }
        else if (mSortSetting.equals("가나다 역순")) {

            Collections.sort(models,StorageItem.By_TITLE_DESCENDING);
        }
        /*
        else if (mSortSetting.equals("등록시간 순")) {

            Collections.sort(models,StorageItem.By_DATE_ASCENDING);
        }
        else if (mSortSetting.equals("등록시간 역순")) {
            Collections.sort(models,StorageItem.By_DATE_DESCENDING);
        }

         */



        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        storageAdapter = new StorageAdapter(models,this);
        recyclerView.setAdapter(storageAdapter);


    }



    // first function onOptionMenu which inflate our menu
    // now create menu function

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu,menu);
        return true;
    }


    // now create option selected function
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.sorting) {    // res 안에 menu 안에 있는 menu 의 id 를 가져옴

            sortDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void sortDialog() {
        String[] options = {"가나다순 정렬","가나다역순 정렬"};
      //  String[] options = {"가나다순 정렬","가나다역순 정렬","등록시간순 정렬","등록시간역순 정렬"};
        AlertDialog.Builder sortBuilder = new AlertDialog.Builder(this);

        sortBuilder.setTitle("정렬방식");
        sortBuilder.setIcon(R.drawable.ic_action_sort);  // add one icon

        sortBuilder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {

                if(which == 0) {
                    SharedPreferences.Editor editor = sortPreferences.edit();
                    editor.putString("정렬","가나다 순");
                    editor.apply();
                    getSayingItemList();
                }

                if (which == 1) {
                    SharedPreferences.Editor editor = sortPreferences.edit();
                    editor.putString("정렬","가나다 역순");
                    editor.apply();
                    getSayingItemList();
                }

                /*
                if (which == 2) {
                    SharedPreferences.Editor editor = sortPreferences.edit();
                    editor.putString("정렬","등록시간 순");
                    editor.apply();
                    getSayingItemList();
                }

                if (which == 3) {
                    SharedPreferences.Editor editor = sortPreferences.edit();
                    editor.putString("정렬","등록시간 역순");
                    editor.apply();
                    getSayingItemList();
                }

                 */
            }
        });

        sortBuilder.create().show();
    }



}
