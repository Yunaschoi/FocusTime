package com.example.focustime.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.focustime.R;
import com.example.focustime.UserInformation;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class MemberInformationActivity extends AppCompatActivity {

    private static final String TAG = "MemberInformation";
    private ImageView profileImageView;
    private String profilePath;
    FirebaseUser user;
    private RelativeLayout loaderLayout;
    private String userPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_information);

        profileImageView = findViewById(R.id.profileImageView);
        profileImageView.setOnClickListener(onClickListener);

       // findViewById(R.id.certify_button).setOnClickListener(onClickListener);
        findViewById(R.id.complete_btn).setOnClickListener(onClickListener);
        findViewById(R.id.gallery).setOnClickListener(onClickListener);
        findViewById(R.id.picture).setOnClickListener(onClickListener);
        loaderLayout = findViewById(R.id.loaderLayout);


    }

    @Override

    public void onActivityResult(int requestCode, int resultCode, Intent data) {   // 이미지 선택 값을 받음
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0: {
                if (resultCode == Activity.RESULT_OK) {
                    //T0D0 Extract the data returned from the child Activity.
                    profilePath = data.getStringExtra("profilePath");
                    Log.e("로그", "profilePath " + profilePath);

                    //Bitmap bmp = BitmapFactory.decodeFile(profilePath);
                    //profileImageView.setImageBitmap(bmp);
                    Glide.with(this).load(profilePath).centerCrop().override(500).into(profileImageView);
                }
                break;
            }
        }
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {

                case R.id.complete_btn:
                    if (profilePath == null) { // 사진을 등록하지 않았을 때 처리하는 코드

                        startToast("이미지를 등록해주세요");

                        // UserInformation userInformation = new UserInformation(name, phoneNumber, address);
                        // upLoader(userInformation);

                    } else {
                        profileUpdate();      // 완료 버튼 클릭
                    }


                    break;
                case R.id.profileImageView:                                       // 프로필 이미지를 눌렀을 때 선택 버튼이 뜬다
                    CardView cardView = findViewById(R.id.CardViewButton);
                    if (cardView.getVisibility() == View.VISIBLE) {
                        cardView.setVisibility(View.GONE);               // 카드뷰가 보이면 안보이게
                    } else {
                        cardView.setVisibility(View.VISIBLE);          // 카드뷰가 안보이면 보이게
                    }
                    break;
                case R.id.picture:
                    startCameraActivity();  // 사진촬영 클릭
                    break;
                case R.id.gallery:
                    // Here, thisActivity is the current activity
                    if (ContextCompat.checkSelfPermission(MemberInformationActivity.this,
                            Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {

                        ActivityCompat.requestPermissions(MemberInformationActivity.this,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                1);

                        // Permission is not granted
                        // Should we show an explanation?
                        if (ActivityCompat.shouldShowRequestPermissionRationale(MemberInformationActivity.this,
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
                    break;

            }
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    startProfileGalleryActivity(); // 프로필 갤러리 화면으로 이동

                    //startGalleryActivity();  // 갤러리 화면으로 이동
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    startToast("권한을 허용해 주세요");
                }
            }
            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    private void profileUpdate() {
        final String name = ((EditText) findViewById(R.id.nameEditText)).getText().toString();  // 이름 설정
        final String phoneNumber = ((EditText) findViewById(R.id.phoneNumberEditText)).getText().toString();  // 휴대폰 번호 설정
        final String address = ((EditText) findViewById(R.id.addressEditText)).getText().toString();  // 주소 설정

        if (name.length() > 0 && phoneNumber.length() > 9 && address.length() > 0) {  //  이름의 길이, 주소의길이가 0 보다 클 때, 휴대폰번호가 9자리보다 클 때

            loaderLayout.setVisibility(View.VISIBLE);
            FirebaseStorage storage = FirebaseStorage.getInstance();
            // Create a storage reference from our app
            StorageReference storageRef = storage.getReference();
            // Create a reference to 'images/mountains.jpg'


            userPhone = phoneNumber;

            user = FirebaseAuth.getInstance().getCurrentUser(); // 사용자 Uid
            final StorageReference mountainImagesRef = storageRef.child("user/" + user.getUid() + "/mountains.jpg");

            if (profilePath == null) { // 사진을 등록하지 않았을 때 처리하는 코드

                // startToast("이미지를 등록해주세요");

                // UserInformation userInformation = new UserInformation(name, phoneNumber, address);
                // upLoader(userInformation);

            } else {   // 프로필 사진을 등록했을 때 처리하는 코드
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
                                Uri downloadUri = task.getResult();
                                Log.e("성공2", "성공 : " + downloadUri);

                                SharedPreferences sharedPreferences = getSharedPreferences("ImageChange", MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString(user.getEmail(), downloadUri.toString());
                                editor.commit();

                                // 쉐어드에 저장

                                UserInformation userInformation = new UserInformation(name, phoneNumber, address, downloadUri.toString());
                                upLoader(userInformation);

                                // 인텐트로 CertityPhoneActivity 로 휴대폰 번호 전달
                                //Intent intent = new Intent(MemberInformationActivity.this,CertifyPhoneActivity.class);
                                //intent.putExtra("userPhoneNumber",userPhone);
                                //startActivity(intent);   // 회원정보 저장 후 폰 인증 화면으로 이동


                            } else {
                                // Handle failures
                                startToast("회원정보를 보내는데 실패하였습니다.");
                            }
                        }
                    });
                } catch (FileNotFoundException e) {
                    Log.e("로그", "에러: " + e.toString());

                }

            }
            //FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        } else {
            startToast("내 정보를 입력해주세요.");
        }
    }

    private void upLoader(UserInformation userInformation) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        //UserInformation userInformation = new UserInformation(name, phoneNumber, address, downloadUri.toString());

        db.collection("users").document(user.getUid()).set(userInformation)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override

                    public void onSuccess(Void aVoid) {
                        startToast("저장완료");
                        loaderLayout.setVisibility(View.GONE);
                        startLoginActivity();   // 회원정보 저장 후 로그인 화면으로 이동
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        startToast("회원정보 등록에 실패하였습니다.");
                        loaderLayout.setVisibility(View.GONE);
                        Log.w(TAG, "Error writing document", e);
                    }
                });
    }

    private void startToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private void startCertifyPhoneActivity() {
        Intent intent = new Intent(this, CertifyPhoneActivity.class);
        startActivity(intent);
    }

    private void startLoginActivity() {
        Intent loginIntent = new Intent(this, LoginActivity.class);
        startActivity(loginIntent);
    }

    private void startCameraActivity() {          // 카메라 화면으로 이동하는 메소드
        Intent cameraActivityIntent = new Intent(this, CameraActivity.class);
        startActivityForResult(cameraActivityIntent, 0);
    }

    private void startProfileGalleryActivity() {   // 갤러리 화면으로 이동하는 메소드 - 테스트 중
        Intent ProfileGalleryIntent = new Intent(this, ProfileGalleryActivity.class);
        startActivityForResult(ProfileGalleryIntent, 0);
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        startToast("회원정보 등록을 진행해주세요");
    }

    /*

    private void certifyDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MemberInformationActivity.this);

        // 다이얼로그를 보여주기 위해 edit_box.xml 파일을 사용합니다.

        View view = LayoutInflater.from(MemberInformationActivity.this)
                .inflate(R.layout.activity_certify_phone, null, false);
        builder.setView(view);

        final Button continueButton = (Button) view.findViewById(R.id.buttonContinue);  // continue 버튼 눌렀을 때
        final EditText phoneNumber = (EditText) view.findViewById(R.id.editTextPhone); // 휴대폰 번호 입력

        final String inputPhone = phoneNumber.getText().toString();
        // 6. 해당 줄에 입력되어 있던 데이터를 불러와서 다이얼로그에 보여줍니다.

        final AlertDialog dialog = builder.create();


        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (inputPhone.length() > 10) {  // 입력한 전화번호가 11자리 이상일 때 실행

                    // VerifyPhone 액티비티를 다이얼로그로 띄워줌

                    dialog.dismiss();
                } else {

                    startToast("정확한 번호를 입력해주세요");
                }

            }
        });

        dialog.show();
    }

    private void verifyDialog() {

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MemberInformationActivity.this);

        // 다이얼로그를 보여주기 위해 edit_box.xml 파일을 사용합니다.

        View view = LayoutInflater.from(MemberInformationActivity.this)
                .inflate(R.layout.activity_verify_phone, null, false);
        builder.setView(view);

        final Button signInButton = (Button) view.findViewById(R.id.buttonSignIn);  // SignIn 버튼 눌렀을 때
        final EditText phoneNumber = (EditText) view.findViewById(R.id.editTextPhone); // 휴대폰 번호 입력

        final String inputPhone = phoneNumber.getText().toString();
        // 6. 해당 줄에 입력되어 있던 데이터를 불러와서 다이얼로그에 보여줍니다.

        final AlertDialog dialog = builder.create();


        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (inputPhone.length() > 10) {  // 입력한 전화번호가 11자리 이상일 때 실행

                    // VerifyPhone 액티비티를 다이얼로그로 띄워줌

                    dialog.dismiss();
                } else {

                    startToast("정확한 번호를 입력해주세요");
                }

            }
        });

        dialog.show();
    }

     */


}
