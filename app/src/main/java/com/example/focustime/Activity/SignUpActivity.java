package com.example.focustime.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.focustime.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;
import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity {
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^[a-zA-Z0-9!@.#$%^&*?_~]{4,16}$");
    final String TAG = "SignUpActivity";
  //  EditText signUpEmail, signUpPassword, signUpName;
    Button signUp, returnLogin,checkButton;

   // Button verifyEmail;

    private FirebaseUser user;

    private FirebaseAuth mAuth;

    private String email = "";
    private String password = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();
      //  verifyEmail = (Button) findViewById(R.id.email_send); // 인증 이메일 보내는 버튼


        signUp = (Button) findViewById(R.id.SignUpBtn);   // 회원가입 버튼
        returnLogin = (Button) findViewById(R.id.returnLoginBtn);  // Login 으로 돌아가는 버튼
       // checkButton = (Button) findViewById(R.id.email_check);

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               // signUp();     // 유효성 검사하지 않는 회원가입 버튼

                checkSignUp();  // 유효성검사도 하는 회원 가입 버튼
            }
        });

        returnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onBackPressed();  // 뒤로가기 버튼
            }
        });


        /*
        verifyEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sendVerificationEmail();

            }
        });

         */



    }

    private void sendVerificationEmail()

    {

        user.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // email sent

                            // after email is sent just logout the user and finish this activity
                            // FirebaseAuth.getInstance().signOut();  // 나중에 회원정보 등록하고 다시 로그아웃할 예정

                            startToast("이메일을 보냈습니다. 이메일인증 후 가입을 계속 진행해주세요");

                        }
                        else
                        {
                            // email not sent, so display message and restart the activity or do whatever you wish to do

                            //restart this activity
                            overridePendingTransition(0, 0);

                            overridePendingTransition(0, 0);
                            // startActivity(getIntent());
                            startToast("이메일 보내는데 실패하였습니다");

                        }
                    }
                });
    }




    /*

    private void signUp() {

        email = ((EditText) findViewById(R.id.signUpEmailEdit)).getText().toString();
        password = ((EditText) findViewById(R.id.signUpPasswordEdit)).getText().toString();
//      String name = ((EditText) findViewById(R.id.signUpNameEdit)).getText().toString();

        if (email.length() > 0 && password.length() > 0) {

            final RelativeLayout loaderLayout = findViewById(R.id.loaderLayout);
            loaderLayout.setVisibility(View.VISIBLE);
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                loaderLayout.setVisibility(View.GONE);
                                FirebaseUser user = mAuth.getCurrentUser();  // 회원 가입 성공시 user 정보를 저장함
                                startToast("회원가입에 성공했습니다");

                                startMemberInformationActivity();
                               // startLoginActivity(); // 로그인 화면으로 이동
                                finish(); // 현재화면은 종료

                                // 회원가입 성공했을 때
                            } else {
                                if (task.getException() != null) {  // 이미 있는 계정일 때
                                    startToast("이미 가입된 이메일이거나 형식이 맞지않습니다");
                                }
                                else {
                                    startToast("회원가입에 실패하였습니다");
                                }

                                // 회원가입 실패했을 때
                            }

                        }
                    });
    } else {
            // email , password , name 중 하나라도 입력하지 않았을 때 실행
            startToast("모든 정보를 입력해주세요");
        }

    }

     */


    @Override
    public void onBackPressed() {  // 뒤로가기 했을 때의 코드

        AlertDialog.Builder builder = new AlertDialog.Builder( SignUpActivity.this);
        builder.setMessage("회원가입을 취소하시겠습니까?");
        builder.setTitle("알림")
                .setCancelable(false)
                .setPositiveButton("네", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();  // 다이얼로그를 종료 시켜준다
                        startLoginActivity();    //로그인 화면으로 이동
                        finish();   // SignUp Activity 는 onDestroy()
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

    private  void startMemberInformationActivity() {
        Intent MemberInformationIntent = new Intent(this, MemberInformationActivity.class);
        startActivity(MemberInformationIntent);
    }

    private  void startLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    private void startToast(String msg) {

        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }


    public void checkSignUp() {
        email = ((EditText) findViewById(R.id.signUpEmailEdit)).getText().toString();
        password = ((EditText) findViewById(R.id.signUpPasswordEdit)).getText().toString();

        if(isValidEmail() && isValidPassword()) {
            final RelativeLayout loaderLayout = findViewById(R.id.loaderLayout);
            loaderLayout.setVisibility(View.VISIBLE);
            createUser(email, password);
        }
    }



    // 이메일 유효성 검사
    private boolean isValidEmail() {
        if (email.isEmpty()) {
            // 이메일 공백
            startToast("이메일을 입력해주세요");


            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            // 이메일 형식 불일치
            startToast("이메일 형식에 맞지 않습니다");
            return false;
        } else {
            return true;
        }
    }

    // 비밀번호 유효성 검사
    private boolean isValidPassword() {
        if (password.isEmpty()) {
            // 비밀번호 공백
            startToast("비밀번호를 입력해주세요");
            return false;
        } else if (!PASSWORD_PATTERN.matcher(password).matches()) {
            // 비밀번호 형식 불일치
            startToast("비밀번호는 6자리이상으로 해주세요");
            return false;
        } else {
            return true;
        }
    }

    // 회원가입
    private void createUser(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // 회원가입 성공

                            final RelativeLayout loaderLayout = findViewById(R.id.loaderLayout);
                            loaderLayout.setVisibility(View.GONE);
                            FirebaseUser user = mAuth.getCurrentUser();  // 회원 가입 성공시 user 정보를 저장함
                            startToast("회원가입에 성공했습니다");


                            startEmailVerificationActivity(); // 이메일 인증하기
                           // startMemberInformationActivity();

                            finish(); // 현재화면은 종료

                        } else {
                            final RelativeLayout loaderLayout = findViewById(R.id.loaderLayout);
                            loaderLayout.setVisibility(View.GONE);
                            startToast("이미 가입된 이메일 입니다");
                            // 회원가입 실패

                        }
                    }
                });
    }

    private void startEmailVerificationActivity() {
        Intent intent = new Intent(this,EmailVerificationActivity.class);
        startActivity(intent);
    }





}
