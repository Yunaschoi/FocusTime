package com.example.focustime.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.focustime.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class VerifyPhoneActivity extends AppCompatActivity {

    private String verificationId;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    private EditText editText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_phone);

        mAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progressbar);
        editText = findViewById(R.id.editTextCode);

        String phonenumber = getIntent().getStringExtra("phoneNumber");

        sendVerificationCode(phonenumber);

        findViewById(R.id.buttonSignIn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String code = editText.getText().toString().trim();

                if (code.isEmpty() || code.length() < 6) {

                    editText.setError("Enter code...");
                    editText.requestFocus();
                    return;

                }
                // progressBar.setVisibility(View.VISIBLE);  // 폰이 유효하다면 프로그래스바를 나타낸다
                verifyCode(code);

            }
        });

    }

    private void verifyCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithCredential(credential);
    }

    private void signInWithCredential(PhoneAuthCredential credential) {

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {   // 사용자가 인증 성공했을 때

                            Intent intent = new Intent(VerifyPhoneActivity.this, MemberInformationActivity.class);

                            // 인증 성공 후 회원정보 입력 칸으로 이동

                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);

                            finish();


                        } else {   // 사용자가 인증에 실패했을 때

                           // Intent intent = new Intent(VerifyPhoneActivity.this, MainActivity.class);
                           // startActivity(intent);

                            // 다시 인증화면으로 보내줌
                            Toast.makeText(VerifyPhoneActivity.this, "인증에 실패했습니다", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(VerifyPhoneActivity.this, CertifyPhoneActivity.class);
                             startActivity(intent);

                           // Toast.makeText(VerifyPhoneActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }

                    }
                });
    }


    private void sendVerificationCode(String number) {
        progressBar.setVisibility(View.VISIBLE);
        PhoneAuthProvider.getInstance().verifyPhoneNumber(

                //  number,  CountryData 와 Spinner 를 사용해서 많은 국가의 국가코드를 불러올 때 사용

                "+82" + number,  // 한국 국가코드 = +82 / 한국만 사용하도록 설정
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallBack
        );
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);

            verificationId = s;
        }

        @Override  // 휴대폰 번호가 유효한지...?
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

            String code = phoneAuthCredential.getSmsCode();

            if (code != null) {
                //  progressBar.setVisibility(View.VISIBLE);  // 폰이 유효하다면 프로그래스바를 나타낸다
                editText.setText(code);
                verifyCode(code);
            }

        }

        @Override  //
        public void onVerificationFailed(FirebaseException e) {

            Toast.makeText(VerifyPhoneActivity.this, "유효하지않는 휴대폰번호 입니다", Toast.LENGTH_LONG).show();

          // Toast.makeText(VerifyPhoneActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();

        }
    };

}
