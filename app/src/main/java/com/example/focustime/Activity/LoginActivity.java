package com.example.focustime.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.focustime.R;
import com.example.focustime.StorageItem;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private DatabaseReference databaseReference;
    FirebaseUser user;
    boolean emailVerified;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        user = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        if (user != null) {
            emailVerified = user.isEmailVerified();
        }

          mAuth = FirebaseAuth.getInstance();
          //다음과 같이 로그인 작업의 onCreate 메소드에서 FirebaseAuth 객체의 공유 인스턴스를 가져옴


        findViewById(R.id.loginBtn).setOnClickListener(onClickListener);
        findViewById(R.id.createBtn).setOnClickListener(onClickListener);
        findViewById(R.id.password_find_textView).setOnClickListener(onClickListener);
       // findViewById(R.id.move_Verify).setOnClickListener(onClickListener);
    }


    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.loginBtn:
                    Login();
                    break;
                case R.id.createBtn:

                   // choiceSignUp();
                    startSignUpActivity();  // 이메일 가입화면으로 보내질 코드
                   // startEmailVerificationActivity();

                    break;
                case R.id.password_find_textView:
                    startPasswordActivity();
                    break;


            }
        }
    };

/*

    private void choiceSignUp() {

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(LoginActivity.this);

        // 다이얼로그를 보여주기 위해 edit_box.xml 파일을 사용합니다.

        View view = LayoutInflater.from(LoginActivity.this)
                .inflate(R.layout.choice_signup, null, false);
        builder.setView(view);

        final Button emailSignUp = (Button) view.findViewById(R.id.email_signup);  // 이메일 가입을 눌렀을 때
        final Button phoneSignUp = (Button) view.findViewById(R.id.phone_signup);  // 폰 가입을 눌렀을 때

        // 6. 해당 줄에 입력되어 있던 데이터를 불러와서 다이얼로그에 보여줍니다.

        final AlertDialog dialog = builder.create();

        emailSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.dismiss();
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);  // 이메일 회원가입으로 이동
            }
        });

        phoneSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.dismiss();
                Intent intent = new Intent(LoginActivity.this, CertifyPhoneActivity.class);
                startActivity(intent);  // 휴대폰 회원가입으로 이동
            }
        });

        dialog.show();


        }

 */




    private void Login() {
        String email = ((EditText)findViewById(R.id.EmailEditText)).getText().toString();
        String password = ((EditText)findViewById(R.id.PasswordEditText)).getText().toString();

        if(email.length()>0 && password.length()>0 ) {  // 이메일의 자리가 0 보다 작거나 비밀번호자리가 0 보다 작을 때
           final RelativeLayout loaderLayout = findViewById(R.id.loaderLayout);
           loaderLayout.setVisibility(View.VISIBLE);

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                          loaderLayout.setVisibility(View.GONE);
                            if (task.isSuccessful()) {

                                FirebaseUser user = mAuth.getCurrentUser();

                                startToast("로그인");
                                startMainActivity();
                                finish();

                                // 이때는 뒤로가기 눌렀을 때 이전화면인 로그인으로 가지 않고 액티비티가 꺼진다
                            } else {
                                if(task.getException() != null) {
                                    startToast("이메일과 비밀번호를 확인해주세요.");
                                    //startToast(task.getException().toString());
                                }
                            }
                        }
                    });
        } else {

            startToast("정확한 이메일과 비밀번호를 입력해주세요");

        }

    }

    private void startEmailVerificationActivity() {
        Intent intent = new Intent(this,EmailVerificationActivity.class);
        startActivity(intent);
    }

    private void startCertifyActivity() {
        Intent intent = new Intent(this,CertifyPhoneActivity.class);
        startActivity(intent);
    }


    private void startPasswordActivity() {
        Intent intent = new Intent(this,PasswordActivity.class);
        startActivity(intent);
    }


    private void startMainActivity() {          //   메인으로 이동하는 메소드
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void startSignUpActivity() {          // SignUp 로 이동하는 메소드
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }

    private void startToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onBackPressed() {    //로그인하고 내정보액티비티에서 로그아웃 했을때 로그인화면에서 뒤로가기하면 앱이 꺼짐
        super.onBackPressed();           // 뒤로가기했을때 다시 메인으로 이동하는 것을 방지
        moveTaskToBack(true);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }
    /*

    private void checkIfEmailVerified()  // 다음버튼을 눌렀을 때 사용자가 이메일을 인증했는지 확인
    {

        if (emailVerified==true)
        {
            // user is verified, so you can finish this activity or send user to activity which you want.
            startToast("로그인");
            startMainActivity();     // 로그인후 회원정보 등록 화면으로 이동
            finish();

            //  Toast.makeText(this, "Successfully logged in", Toast.LENGTH_SHORT).show();
        }
        else
        {
            // email is not verified, so just prompt the message to the user and restart this activity.
            // NOTE: don't forget to log out the user.
            // FirebaseAuth.getInstance().signOut();
            startToast("이메일을 인증해주세요");

            //restart this activity

        }
    }

     */



}
