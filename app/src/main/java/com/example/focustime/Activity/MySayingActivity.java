package com.example.focustime.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.ColorSpace;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.focustime.Adapter.MySayingAdapter;
import com.example.focustime.Adapter.StorageAdapter;
import com.example.focustime.MySayingItem;
import com.example.focustime.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Dictionary;
import java.util.LinkedList;
import java.util.List;

public class MySayingActivity extends AppCompatActivity {

    private ImageView selectedImageView;


    public static Context CONTEXT;

    private MySayingAdapter sayingAdapter;
    androidx.appcompat.widget.Toolbar toolbar;
    private String imagePath; // 이미지 경로 주소
    private FirebaseUser user; // 현재 로그인한 유저를 확인하는 용도

    private Button ButtonSubmit;   // 등록 버튼
    private EditText titleEditText;   // 제목 입력란
    private EditText contentEditText;  // 내용 입력란
    private CardView memoCardView; // 사진 등록란

    private ImageView memoImageView; // 메모 등록란


    String reviseImagePath;
   // ImageView cProfileImage;

    private String profilePath;

  //  private String memoPath;
    private String saveUri;
    private RelativeLayout loaderLayout;


    private String usersTitle;  // 쉐어드의 key 로 사용할 메모 제목
    private String usersContent;  // 쉐어드의 key 로 사용할 메모 내용

    RecyclerView recyclerView;
    MySayingAdapter mySayingAdapter;

    ArrayList<MySayingItem> models = new ArrayList<>();

    SharedPreferences sortPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_saying);

        CONTEXT = this;

        loaderLayout = findViewById(R.id.loaderLayout);

        toolbar = findViewById(R.id.mysaying_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("   메모장");
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setIcon(R.drawable.ic_action_memo);

        user = FirebaseAuth.getInstance().getCurrentUser();
        //  recyclerView = findViewById(R.id.sayingRecyclerView);

        sortPreferences = this.getSharedPreferences("My_Pref", MODE_PRIVATE);


        loadData();
        builderRecyclerView(); // 리사이클러뷰 생성
        getSayingItemList();  // 정렬시켜주는 메서드


        /////////////////////////////////////////////////////////
        // now i will use shared preference for our app because whenever we open our app it remember our state
        // 앱을 열때마다 상태를 기억해주기 위해서 sharedPreference 를 사용 할 것이다
        // 다른 액티비티에 다녀와도 사용자가 설정한 오름차순, 내림차순의 데이터 정렬한 모습이 그대로 저장되어 있다


        //////////////////////////////////////////////////////////////
        // 이미지 파일 추가하는 법 추가 해야됨


        mySayingAdapter.setOnItemClickListener(new MySayingAdapter.clickListener() {

            @Override
            public void reviseBtnClick(final int positon, View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(MySayingActivity.this);

                // 다이얼로그를 보여주기 위해 activity_floating_action.xml 파일을 사용합니다.

                view = LayoutInflater.from(MySayingActivity.this)
                        .inflate(R.layout.activity_floating_action, null, false);
                builder.setView(view);

                final Button ButtonSubmit = view.findViewById(R.id.saveButton);
                final EditText cTitle = view.findViewById(R.id.titleEditText);
                final EditText cMemo = view.findViewById(R.id.contentEditText);
                ButtonSubmit.setText("저 장");
                final ImageView cProfileImage = (ImageView) view.findViewById(R.id.memo_imageView);


                // 6. 해당 줄에 입력되어 있던 데이터를 불러와서 다이얼로그에 보여줍니다.
                cTitle.setText(models.get(positon).getTitle());
                cMemo.setText(models.get(positon).getMemo());

                // cProfileImage.setImageResource(R.drawable.memo); // 기본이미지로 지정
               // reviseImagePath = models.get(positon).getMemoUri();

                // 이전 이미지를 불러옴
                 Glide.with(MySayingActivity.this).load(models.get(positon).getMemoUri()).centerCrop().override(800).into(cProfileImage);

                // 다이얼로그 안에 있는 이미지를 눌렀을 때 갤러리 액티비티로 이동...
                cProfileImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        startActivity(ProfileGalleryActivity.class,10);

                    }
                });


                // 저장 버튼 눌렀을 때

                final AlertDialog dialog = builder.create();
                ButtonSubmit.setOnClickListener(new View.OnClickListener() {


                    public void onClick(View v) {

                        String reImagePath = reviseImagePath;

                        if (cTitle.length() > 0 && cMemo.length() > 0) {  // 2개다 0보다 크

                            String strTitle = cTitle.getText().toString();
                            String strContent = cMemo.getText().toString();

                            usersTitle = strTitle;   // 쉐어드의 키 값으로 사용
                            usersContent = strContent;  // 쉐어드의 키 값으로 사용

                            //  ImageUpdate();

                            MySayingItem mySayingItem = new MySayingItem(strTitle, strContent, user.getEmail(), new Date(), reImagePath);
                            //models.add(mySayingItem); // 마지막 줄에 삽입할 때 사용


                            models.set(positon, mySayingItem);

                            mySayingAdapter.notifyItemChanged(positon);
                            //  loadImage();

                            // ImageUpdate();

                            dialog.dismiss();

                        } else {
                            startToast("내용을 입력해주세요");
                        }

                    }


                });
                dialog.show();

            }

        });

        mySayingAdapter.setOnItemClickListener(new MySayingAdapter.clickListener2() {
            @Override
            public void garbageClick(int positon, View view) {
                models.remove(positon);
                mySayingAdapter.notifyItemRemoved(positon);
            }
        });




        FloatingActionButton buttonInsert = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        buttonInsert.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                user =  FirebaseAuth.getInstance().getCurrentUser();
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReference();
                FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
                final DocumentReference documentReference = firebaseFirestore.collection("memo").document();


                // 레이아웃 파일 floating_action 파일을 불러와 화면에 다이얼로그를 보여줌
                AlertDialog.Builder builder = new AlertDialog.Builder(MySayingActivity.this);
                View v = LayoutInflater.from(MySayingActivity.this).inflate(R.layout.activity_floating_action, null, false);
                builder.setView(v);

                ButtonSubmit = (Button) v.findViewById(R.id.saveButton);   // 등록 버튼
                titleEditText = (EditText) v.findViewById(R.id.titleEditText);  // 제목 입력란
                contentEditText = (EditText) v.findViewById(R.id.contentEditText); // 내용 입력란
                memoImageView = (ImageView) v.findViewById(R.id.memo_imageView); // 사진이 들어갈 공간
                ButtonSubmit.setText("저 장");

                memoCardView = (CardView) v.findViewById(R.id.memo_cardView); // 사진 등록하는 카드뷰

                memoCardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (ContextCompat.checkSelfPermission(MySayingActivity.this,
                                Manifest.permission.READ_EXTERNAL_STORAGE)
                                != PackageManager.PERMISSION_GRANTED) {

                            ActivityCompat.requestPermissions(MySayingActivity.this,
                                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                    1);

                            // Permission is not granted
                            // Should we show an explanation?
                            if (ActivityCompat.shouldShowRequestPermissionRationale(MySayingActivity.this,
                                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                                // Show an explanation to the user *asynchronously* -- don't block
                                // this thread waiting for the user's response! After the user
                                // sees the explanation, try again to request the permission.

                            } else {
                                // No explanation needed; request the permission

                                // startToast("권한을 허용해 주세요");
                                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                                // app-defined int constant. The callback method gets the
                                // result of the request.
                            }
                        } else {
                            // Permission has already been granted 사용자가 권한을 허락했을 때
                            startActivity(ProfileGalleryActivity.class,0); // 프로필 갤러리 화면으로 이동
                            //startGalleryActivity();  // 갤러리 화면으로 이동
                        }


                        // 갤러리로 이동
                        // Here, thisActivity is the current activity
                       // startActivity(ProfileGalleryActivity.class,0);
                    }
                });


                final AlertDialog dialog = builder.create();

                // 다이얼로그의 저장 버튼을 누르면 실행할 코드
                ButtonSubmit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (titleEditText.length() > 0 && contentEditText.length() > 0) {  // 2개다 0보다 크

                            String strTitle = titleEditText.getText().toString();
                            String strContent = contentEditText.getText().toString();


                            usersTitle = strTitle;   // 쉐어드의 키 값으로 사용
                            usersContent = strContent;  // 쉐어드의 키 값으로 사용


                            if( profilePath == null ) {  // 이미지를 저장하지 않았다면 제목과 내용만 데이터베이스에 저장
                                MySayingItem mySayingItem = new MySayingItem(strTitle, strContent, user.getEmail(), new Date());

                                models.add(0, mySayingItem);  // 첫번째 줄에 삽입

                                mySayingAdapter.notifyItemInserted(0);

                                dialog.dismiss();


                            } else {
                                // 이미지도 저장했을 때

                              //  ImageUpdate();

                                MySayingItem mySayingItem = new MySayingItem(strTitle, strContent, user.getEmail(), new Date(),profilePath);
                                //models.add(mySayingItem); // 마지막 줄에 삽입할 때 사용
                                models.add(0, mySayingItem);  // 첫번째 줄에 삽입

                                mySayingAdapter.notifyItemInserted(0);
                                // mySayingAdapter.notifyDataSetChanged(); // 마지막 줄에 삽입할 때 사용

                              //  loadImage();

                               // ImageUpdate();



                                dialog.dismiss();

                            }
                        } else {
                            startToast("내용을 입력해주세요");
                        }
                    }
                });
                dialog.show();
            }
        });

    }

    @Override public void onResume() {
        super.onResume();
        mySayingAdapter.notifyDataSetChanged();
    }



    public void onActivityResult(int requestCode, int resultCode, Intent data) {   // 이미지 선택 값을 받음
        super.onActivityResult(requestCode,resultCode,data);
        switch (requestCode) {
            case 0: {
                if (resultCode == Activity.RESULT_OK) {
                    //T0D0 Extract the data returned from the child Activity.
                    profilePath = data.getStringExtra("profilePath");
                    Log.e("로그", "profilePath " + profilePath);



                    //Bitmap bmp = BitmapFactory.decodeFile(profilePath);
                    //profileImageView.setImageBitmap(bmp);

                    Glide.with(this).load(profilePath).centerCrop().override(500).into(memoImageView);
                    ImageUpdate();

                }
            }
                break;

                case 10: {
                    if(resultCode == Activity.RESULT_OK) {
                        //T0D0 Extract the data returned from the child Activity.
                        reviseImagePath = data.getStringExtra("profilePath");
                        Log.e("로그", "profilePath " + reviseImagePath);

                        ImageView imageView = new ImageView(MySayingActivity.this);

                        imageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                selectedImageView = (ImageView) view;
                            }
                        });
                        //Bitmap bmp = BitmapFactory.decodeFile(profilePath);
                        //profileImageView.setImageBitmap(bmp);

                        Glide.with(MySayingActivity.this).load(reviseImagePath).centerCrop().override(500).into(memoImageView);
                        ImageUpdate();
                    }
                    break;
                }
            }
        }



    private void ImageUpdate() {

        //   FirebaseFirestore db = FirebaseFirestore.getInstance();
        //   DocumentReference documentReference = FirebaseFirestore.getInstance().collection("users").document(user.getUid());
        user = FirebaseAuth.getInstance().getCurrentUser(); // 현재유저

        loaderLayout.setVisibility(View.VISIBLE);

        FirebaseStorage storage = FirebaseStorage.getInstance();
        // Create a storage reference from our app
        StorageReference storageRef = storage.getReference();
        // Create a reference to 'images/mountains.jpg'

        // user = FirebaseAuth.getInstance().getCurrentUser(); // 사용자 Uid
        final StorageReference mountainImagesRef = storageRef.child("memo/" + user.getUid() + "/mountains.jpg");

        try {
            InputStream stream = new FileInputStream(new File(profilePath));

            UploadTask uploadTask = mountainImagesRef.putStream(stream);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {

                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    // Continue with the task to get the download URL
                    return mountainImagesRef.getDownloadUrl();

                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {

                    if (task.isSuccessful()) {
                        final Uri downloadUri = task.getResult();
                        Log.e("성공2", "성공 : " + downloadUri);
                        saveUri = String.valueOf(downloadUri);
                        saveData();
                      //  Glide.with(MySayingActivity.this).load(profilePath).centerCrop().override(500).into(memoImageView);

                       // Glide.with(MySayingActivity.this).load(downloadUri).centerCrop().override(500).into(memoImageView);

                        loaderLayout.setVisibility(View.GONE);


                    } else {
                        // Handle failures
                        startToast("메모 등록에 실패하였습니다.");
                        loaderLayout.setVisibility(View.GONE);
                    }
                }
            });
        } catch (FileNotFoundException e) {
            Log.e("로그", "에러: " + e.toString());
        }

    }

    /*
    private void saveImage() {

        SharedPreferences sharedPreferences = getSharedPreferences("memoImage",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String ImageUri = saveUri; // 사용자가 저장할 데이터
        editor.putString(user.getEmail()+"/"+usersTitle+"/"+usersContent,ImageUri); // key, value를 이용하여 저장하는 형태

        editor.commit();
    }

    public void loadImage() {

        SharedPreferences sf = getSharedPreferences("memoImage",MODE_PRIVATE);
        String changeUri = sf.getString(user.getEmail()+"/"+usersTitle+"/"+usersContent ,"");
        Log.d("변경",""+changeUri);
        Glide.with(MySayingActivity.this).load(changeUri).centerCrop().override(500).into(memoImageView);

    }

     */




    /*

    private void storeUpLoader(DocumentReference documentReference, MySayingItem mySayingItem) {
        documentReference.set(mySayingItem)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("이미지 store 성공", "DocumentSnapshot successfully written!");
                       // finish();  // 보내는 것을 성공하면 현재 activity 종료 해줌
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("이미지 store 실패", "Error writing document", e);
                        startToast("글을 작성해주세요");
                    }
                });

    }

     */



    private void builderRecyclerView() {

        recyclerView = findViewById(R.id.sayingRecyclerView);
        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mySayingAdapter = new MySayingAdapter(this, models);

        recyclerView.setAdapter(mySayingAdapter);

    }


    @Override
    protected void onStop() {
        super.onStop();

        saveData();

    }

    public void saveData() {
        SharedPreferences sharedPreferences = getSharedPreferences("memo",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(models);
        editor.putString(user.getEmail(),json);
        editor.apply();
    }

    private void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences("memo",MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString(user.getEmail(),null);
        Type type = new TypeToken<ArrayList<MySayingItem>>() {}.getType();
        models = gson.fromJson(json,type);

        if(models == null) {
            models = new ArrayList<>();
        }
    }


    private void getSayingItemList() {

       // models = new ArrayList<>();


        String mSortSetting = sortPreferences.getString("정렬","등록시간 순");  // 처음 정렬하는 방식을 지정

        if(mSortSetting.equals("가나다 순")) {
            Collections.sort(models,MySayingItem.By_TITLE_ASCENDING);
        }
        else if (mSortSetting.equals("가나다 역순")) {

            Collections.sort(models,MySayingItem.By_TITLE_DESCENDING);
        }
        else if (mSortSetting.equals("등록시간 순")) {

            Collections.sort(models,MySayingItem.By_DATE_ASCENDING);
        }
        else if (mSortSetting.equals("등록시간 역순")) {
            Collections.sort(models,MySayingItem.By_DATE_DESCENDING);
        }



        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        mySayingAdapter = new MySayingAdapter(this, models);
        recyclerView.setAdapter(mySayingAdapter);


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

        String[] options = {"가나다순 정렬","가나다역순 정렬","등록시간순 정렬","등록시간역순 정렬"};
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
            }
        });

        sortBuilder.create().show();
    }

    private void startToast(String msg) {

        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private  void startProfileGalleryActivity() {   // 갤러리 화면으로 이동하는 메소드 - 테스트 중
        Intent ProfileGalleryIntent = new Intent(this, ProfileGalleryActivity.class);
        startActivityForResult(ProfileGalleryIntent,0);
    }

    private void startActivity(Class c, int requestCode) {    // 동영상 파일인지 이미지파일인지 구분해주는 인텐트
        Intent intent = new Intent(this,c);
        startActivityForResult(intent,requestCode);
    }


    /*

    public void dataSave() {
        SharedPreferences sf = getSharedPreferences("file",MODE_PRIVATE);
        // SharedPreferences 를 'file' 이름 ,  기본 모드로 설정
        SharedPreferences.Editor editor = sf.edit();
        editor.putString("mFile", titleEditText.toString());
        editor.putString("mFile", contentEditText.toString());
        editor.putString("mFile",user.getEmail());
        editor.putString("mFile",new Date().toString());

    }

    public void dataLoad() {
        SharedPreferences sharedPreferences = getSharedPreferences("file",MODE_PRIVATE);
         // 저장된 값을 불러오기 위해 같은 네임파일을 찾음

        String titleText = sharedPreferences.getString("mFile","");
        String contentText = sharedPreferences.getString("mFile","");
        String date = sharedPreferences.getString("mFile","");
        String publisher = sharedPreferences.getString("mFile","");

        titleEditText.setText(titleText);
        contentEditText.setText(contentText);

    }

     */


}
