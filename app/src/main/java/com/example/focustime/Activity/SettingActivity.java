package com.example.focustime.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
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

import com.bumptech.glide.Glide;
import com.example.focustime.MySayingItem;
import com.example.focustime.R;
import com.example.focustime.Service.MyService;
import com.example.focustime.UserInformation;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.auth.UserProfileChangeRequest.Builder;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class SettingActivity<profileRequest> extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle drawerToggle;
    androidx.appcompat.widget.Toolbar toolbar;
    TextView hUserName;
    TextView hUserEmail;
    ImageView hUserImage;
    View header;




    private static final String TAG = "SettingActivity";

    TextView userEmail;
    TextView userName;
    TextView userPhoneNumber;
    TextView userAddress;
    ImageView userImage;
    FirebaseUser user;
   // DocumentSnapshot document;
    private RelativeLayout loaderLayout;

    private String profilePath;
    private String saveUri;

    private Calendar mCalendar;
    Calendar minDate = Calendar.getInstance();
    private final int ONE_DAY = 24 * 60 * 60 * 1000;
    protected String strCountResult;
    protected String titleResult;

    // DatePicker 에서 날짜 선택 시 호출
    private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
        // 다이얼로그를 띄워서 제목을 설정할 수 있게 해주기


        @Override
        public void onDateSet(DatePicker a_view, int a_year, int a_monthOfYear, int a_dayOfMonth) {
            // D-day 계산 결과 출력

            String mResult = getDday(a_year, a_monthOfYear, a_dayOfMonth); // mResult 는 mTvResult 에 들어간 결과 값을 받음 => 알람설정을 위해 변수 선언


            // 다이얼로그를 띄워서 제목을 설정할 수 있게 해줌
            DdayTitleDialog();



            if (mResult.equals("D-Day")) {
                // 만약 mResult 의 값이 D-Day 와 같다면 알람 호출
                Intent intent = new Intent(SettingActivity.this, MyService.class);
                startService(intent);

            } else {

                // 아니라면 Service 멈춤
                // Toast.makeText(getApplicationContext(), "Service 끝", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SettingActivity.this, MyService.class);
                stopService(intent);

            }

        }
    };




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.main:
                        startMainActivity();
                       // Toast.makeText(SettingActivity.this, "Recents", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.storage:
                        startStorageActivity();
                      //  Toast.makeText(SettingActivity.this, "Favorites", Toast.LENGTH_SHORT).show();
                        break;
                        /*
                    case R.id.infor:
                        Toast.makeText(SettingActivity.this, "Nearby", Toast.LENGTH_SHORT).show();
                        break;

                         */
                }
                return true;
            }
        });




        mCalendar = new GregorianCalendar();

        initLayout();

        hUserName = (TextView) header.findViewById(R.id.header_user_name);
        hUserEmail = (TextView) header.findViewById(R.id.header_user_email);
        hUserImage = (ImageView) header.findViewById(R.id.header_user_Image);


        user = FirebaseAuth.getInstance().getCurrentUser();



        hUserEmail.setText(user.getEmail());


        // 네비게이션 드로어에 대한 이미지를 봐꿔줌
        SharedPreferences sf = getSharedPreferences("ImageChange",MODE_PRIVATE);
        String changeUri = sf.getString(user.getEmail(),"");
        Log.d("변경",""+changeUri);
        Glide.with(SettingActivity.this).load(changeUri).centerCrop().override(500).into(hUserImage);



        userEmail = (TextView) findViewById(R.id.userEmail);    // 유저 이메일 값 받아올 변수
        userName = (TextView) findViewById(R.id.user_name);      // 유저 이름 값 받아올 변수
        userPhoneNumber = (TextView) findViewById(R.id.user_phoneNumber);   // 유저 폰번호 받아올 변수
        userAddress = (TextView) findViewById(R.id.user_address);     // 유저 주소 받아올 변수

        loaderLayout = findViewById(R.id.loaderLayout);

        userImage = (ImageView) findViewById(R.id.user_picture);    // 유저 이미지 받아올 변수

        userEmail.setText(user.getEmail());  // 유저이메일 값은 따로 받아옴

     //   findViewById(R.id.mainButton).setOnClickListener(onClickListener);
     //   findViewById(R.id.storageButton).setOnClickListener(onClickListener);

      //  findViewById(R.id.MysayingBtn).setOnClickListener(onClickListener);
      //  findViewById(R.id.LogoutBtn).setOnClickListener(onClickListener);
      //  findViewById(R.id.changePasswordBtn).setOnClickListener(onClickListener);

        userImage.setOnClickListener(new View.OnClickListener() {     // 이미지 클릭시 갤러리로 이동  현재 수정하는 기능은 아직 대기
            @Override
            public void onClick(View view) {
                // Here, thisActivity is the current activity
                if (ContextCompat.checkSelfPermission(SettingActivity.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(SettingActivity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            1);

                    // Permission is not granted
                    // Should we show an explanation?
                    if (ActivityCompat.shouldShowRequestPermissionRationale(SettingActivity.this,
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
                    startProfileGalleryActivity(); // 프로필 갤러리 화면으로 이동
                    //startGalleryActivity();  // 갤러리 화면으로 이동
                }
            }
        });


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
                            userPhoneNumber.setText(document.getData().get("phoneNumber").toString());
                            userAddress.setText(document.getData().get("address").toString());

                            hUserName.setText(document.getData().get("name").toString());

                           // if (document.getData().get("photoUrl") != null)
                            {

                           //     Glide.with(getBaseContext()).load(document.getData().get("photoUrl")).override(200, 200).centerCrop().into(userImage);

                           //     Log.d("개인화면 이미지변경", "" + document.getData().get("photoUrl"));

                           //     saveUri = String.valueOf(document.getData().get("photoUrl"));

                                loadImage();

                            }

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


    public void onActivityResult(int requestCode, int resultCode, Intent data) {   // 이미지 선택 값을 받음
        super.onActivityResult(requestCode,resultCode,data);
        switch (requestCode) {
            case 0: {
                if(resultCode == Activity.RESULT_OK) {
                    //T0D0 Extract the data returned from the child Activity.
                    profilePath = data.getStringExtra("profilePath");
                    Log.e("로그","profilePath "+profilePath);

                    //Bitmap bmp = BitmapFactory.decodeFile(profilePath);
                    //profileImageView.setImageBitmap(bmp);

                    ImageUpdate();
                   // Glide.with(this).load(profilePath).centerCrop().override(500).into(userImage);
                }
                break;
            }
        }
    }

    private void ImageUpdate() {

     //   FirebaseFirestore db = FirebaseFirestore.getInstance();
     //   DocumentReference documentReference = FirebaseFirestore.getInstance().collection("users").document(user.getUid());
     //   user = FirebaseAuth.getInstance().getCurrentUser(); // 현재유저

        loaderLayout.setVisibility(View.VISIBLE);

        FirebaseStorage storage = FirebaseStorage.getInstance();
        // Create a storage reference from our app
        StorageReference storageRef = storage.getReference();
        // Create a reference to 'images/mountains.jpg'

      // user = FirebaseAuth.getInstance().getCurrentUser(); // 사용자 Uid
        final StorageReference mountainImagesRef = storageRef.child("user/" + user.getUid() + "/mountains.jpg");

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
                        saveImage();


                       // FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser(); // 현재유저
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                //.setDisplayName("Jane Q. User")
                                //.setPhotoUri(Uri.parse(profilePath))
                                .setPhotoUri(downloadUri)
                                .build();

                        user.updateProfile(profileUpdates)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Log.d(TAG, "User profile updated.");


                                            Glide.with(SettingActivity.this).load(downloadUri).centerCrop().override(500).into(userImage);
                                            //  Glide.with(SettingActivity.this).load(downloadUri).centerCrop().override(500).into(userImage);
                                            loaderLayout.setVisibility(View.GONE);
                                        }
                                    }
                                });
                    } else {
                        // Handle failures
                        startToast("이미지 등록에 실패하였습니다.");
                        loaderLayout.setVisibility(View.GONE);
                    }
                }
            });
        } catch (FileNotFoundException e) {
            Log.e("로그", "에러: " + e.toString());
        }

    }


    @Override
    protected void onStop() {
        super.onStop();

       // saveImage(); // 사용자의 모든 이미지 주소 저장
    }




    private void saveImage() {

        SharedPreferences sharedPreferences = getSharedPreferences("ImageChange",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String ImageUri = saveUri; // 사용자가 저장할 데이터
        editor.putString(user.getEmail(),ImageUri); // key, value를 이용하여 저장하는 형태

        editor.commit();
    }




    public void loadImage() {

        SharedPreferences sf = getSharedPreferences("ImageChange",MODE_PRIVATE);
        String changeUri = sf.getString(user.getEmail(),"");
        Log.d("변경",""+changeUri);
        Glide.with(SettingActivity.this).load(changeUri).centerCrop().override(500).into(userImage);

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

                if(strEmail.length()>0) {

                    final RelativeLayout loaderLayout = findViewById(R.id.loaderLayout);
                    loaderLayout.setVisibility(View.VISIBLE);
                    FirebaseAuth auth = FirebaseAuth.getInstance();

                    auth.sendPasswordResetEmail(strEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {


                            if(task.isSuccessful()) {
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

    private void logOut() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);
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

    private void startToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private void startStorageActivity() {          // storage로 이동하는 메소드
        Intent intent = new Intent(this, StorageActivity.class);
        startActivity(intent);
    }
    private void startMainActivity() {          // 메인으로 이동하는 메소드
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
    private void startMysayingActivity() {          // 나만의 명언으로 이동하는 메소드
        Intent intent = new Intent(this, MySayingActivity.class);
        startActivity(intent);
    }
    private void startLoginActivity() {          // 로그인으로 이동하는 메소드
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
    private  void startProfileGalleryActivity() {   // 갤러리 화면으로 이동하는 메소드 - 테스트 중
        Intent ProfileGalleryIntent = new Intent(this, ProfileGalleryActivity.class);
        startActivityForResult(ProfileGalleryIntent,0);
    }




    @Override
    public boolean onNavigationItemSelected (@NonNull MenuItem item){    // 아이템 눌렀을 때 어떻게 할지
        switch (item.getItemId()) {
            case R.id.item1:  // 메모장으로 이동
                startMysayingActivity();
                // Toast.makeText(this, "item1 clicked..", Toast.LENGTH_SHORT).show();
                break;
            case R.id.item2:    // 비밀번호 변경
                AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);
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
                Toast.makeText(this, "item4 clicked..", Toast.LENGTH_SHORT).show();
                break;
            case R.id.select_dday:    // D-day 설정할 수 있는 버튼

                final int year = mCalendar.get(Calendar.YEAR);
                final int month = mCalendar.get(Calendar.MONTH);
                final int day = mCalendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(SettingActivity.this, mDateSetListener, year, month, day);

                minDate.set(year,month,day);  // 오늘 날짜는 선택 가능하도록 함

                // minDate.set(year, month, day + 1);   // 이전 날짜, 오늘 날짜로는 선택 못하게 막음
                dialog.getDatePicker().setMinDate(minDate.getTime().getTime());
                dialog.show();

                break;

            case R.id.delete_dday:    // D-day 삭제할 수 있는 버튼

                // 간단한 다이얼로그를 띄워서 D-day 날짜와 제목을 삭제시켜 준다
                ddayDelete();

                break;

            case R.id.weekData:

                Intent intent = new Intent(this,GraphActivity.class);
                startActivity(intent);

                break;


        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return false;
    }

    @Override
    protected void onPostCreate (Bundle savedInstanceState){
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged (Configuration newConfig){
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item){
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle your other action bar items...

        return super.onOptionsItemSelected(item);
    }

    private void initLayout () {

        toolbar = (androidx.appcompat.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("내 정보");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);

        drawerLayout = (DrawerLayout) findViewById(R.id.dl_main_drawer_root);
        navigationView = (NavigationView) findViewById(R.id.nv_main_navigation_root);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(drawerToggle);
        navigationView.setNavigationItemSelectedListener(this);

        header = navigationView.getHeaderView(0);

    }

    private void userDelete() {

        final FirebaseUser use = FirebaseAuth.getInstance().getCurrentUser();
        AlertDialog.Builder alert_confirm = new AlertDialog.Builder(SettingActivity.this);
        alert_confirm.setMessage("정말 탈퇴하시겠습니까?").setCancelable(false).setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        use.delete()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(SettingActivity.this, "계정이 삭제 되었습니다.", Toast.LENGTH_LONG).show();
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
                Toast.makeText(SettingActivity.this, "취소", Toast.LENGTH_LONG).show();
            }
        });
        alert_confirm.show();
    }



    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }

    }





    private String getDday(int a_year, int a_monthOfYear, int a_dayOfMonth) {
        // D-day 설정
        final Calendar ddayCalendar = Calendar.getInstance();
        ddayCalendar.set(a_year, a_monthOfYear, a_dayOfMonth);

        // D-day 를 구하기 위해 millisecond 으로 환산하여 d-day 에서 today 의 차를 구한다.
        final long dday = ddayCalendar.getTimeInMillis() / ONE_DAY;
        final long today = Calendar.getInstance().getTimeInMillis() / ONE_DAY;
        long result = dday - today;


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


    public void DdayTitleSave() {
        SharedPreferences sharedPreferences = getSharedPreferences("DdayTitle", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String DdayTitleResult = titleResult; // 사용자가 저장할 데이터
        editor.putString(user.getEmail(),DdayTitleResult); // key, value를 이용하여 저장하는 형태

        editor.commit();

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

                    titleResult = strTitle;  // Shared 에 저장시킬 디데이 제목
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

        AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);
        builder.setMessage("삭제 하시겠습니까?");
        builder.setTitle("알림")
                .setCancelable(false)
                .setPositiveButton("네", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();  // 다이얼로그를 종료 시켜준다

                        deleteDdayDate();
                        deleteDdayTitle();

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
