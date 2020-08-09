package com.example.focustime.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.focustime.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class EmailVerificationActivity extends AppCompatActivity {

    private static final String TAG = "EmailVerifiActivity";

    Button verifiEmail, nextPage, refresh;
    FirebaseUser user;
    FirebaseAuth auth;
    boolean emailVerified;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_verification);


        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        if (user != null) {
            emailVerified = user.isEmailVerified();
        }

        verifiEmail = (Button) findViewById(R.id.email_send); // 인증 이메일 보내는 버튼
        nextPage = (Button) findViewById(R.id.email_pass); // 인증 후 다음 화면으로 가는 버튼

        /*

       refresh = (Button) findViewById(R.id.refresh);

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = getIntent();
                finish();
                startActivity(intent);
            }
        });


         */

        
        verifiEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sendVerificationEmail();
                /*
                user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {                         //해당 이메일에 확인메일을 보냄
                            Log.d(TAG, "Email sent.");
                            startToast("이메일을 보냈습니다");

                        } else {
                            //메일 보내기 실패
                            Log.e(TAG, "sendEmailVerification", task.getException());
                            startToast("이메일을 보내는데 실패했습니다");
                        }
                    }
                });

                 */
            }
        });


        nextPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startMemberInfomationActivity();

            }
        });

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

                            startToast("이메일인증 후 가입을 계속 진행해주세요");
                            //startActivity(new Intent(EmailVerificationActivity.this, MemberInformationActivity.class));
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




    private void startMemberInfomationActivity() {
        Intent intent = new Intent(this, MemberInformationActivity.class);
        startActivity(intent);
    }


    private void startToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();

    }

    /*

    private void checkIfEmailVerified()  // 다음버튼을 눌렀을 때 사용자가 이메일을 인증했는지 확인
    {

        if (user.isEmailVerified())
        {
            // user is verified, so you can finish this activity or send user to activity which you want.
            startToast("인증되었습니다");
            startMemberInfomationActivity();     // 로그인후 회원정보 등록 화면으로 이동
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
