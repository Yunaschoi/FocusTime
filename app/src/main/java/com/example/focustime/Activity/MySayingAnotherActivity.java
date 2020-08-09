package com.example.focustime.Activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.focustime.Adapter.MySayingAdapter;
import com.example.focustime.Adapter.StorageAdapter;
import com.example.focustime.MySayingItem;
import com.example.focustime.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MySayingAnotherActivity extends AppCompatActivity  {
    androidx.appcompat.widget.Toolbar toolbar;
    TextView mTitleTv;
    TextView mContextTv;
    TextView mDateTv;

    MySayingAdapter mySayingAdapter;
    Button reviseBtn;
    ImageView mImageViewTv;

    ArrayList<MySayingItem> models = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_saying_another);

        Intent intent = getIntent();

       final String mTitle = intent.getStringExtra("iTitle");
       final String mContext = intent.getStringExtra("iMemo");
       final String mDate = intent.getStringExtra("iDate");
       final String mImage = intent.getStringExtra("iImage");


        toolbar = findViewById(R.id.another_toolbar);


        ////////////////////////////////////////////////
        // 데이터(item) 추가하는 코드
        // in this activity we will use a back button

       // ActionBar actionBar = getSupportActionBar();

        mTitleTv = findViewById(R.id.memoTitle);
        mContextTv = findViewById(R.id.MemoTextView);
        mDateTv = findViewById(R.id.anotherDate);
        mImageViewTv = findViewById(R.id.imageMemo);


        // 보내준 데이터를 받는 곳


         // byte [] mBytes = getIntent().getByteArrayExtra("iImage");

        // now decode image because from previous activity we get our image in bytes
        // 이전 활동에서 이미지를 바이트 단위로 가져오기 때문에 이미지를 디코딩합니다.

       // Bitmap bitmap = BitmapFactory.decodeByteArray(mBytes,0,mBytes.length);

      //  actionBar.setTitle(mTitle);   // 액션 바에서 얻을 수있는 이전 activity 에서 얻은 제목

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(mTitle);  // 툴바에서 얻을 수 있는 이전 activity 에서 얻은 제목


        // now set our data in our view, which we get in our previous activity
        //  이전 액티비티에서 얻은 것을 뷰안의 데이터에 세팅 해줌
        mTitleTv.setText(mTitle);
        mContextTv.setText(mContext);
        mDateTv.setText(mDate);
       // SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd-EE",Locale.getDefault());
       // String formattedDate = df.format(mDate);

        Glide.with(this).load(mImage).centerCrop().override(800).into(mImageViewTv);

      //  mImageViewTv.setImageBitmap(bitmap);
//////////////////////////////////////////////////////
    }


}
