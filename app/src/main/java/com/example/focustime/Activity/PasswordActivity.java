package com.example.focustime.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.focustime.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class PasswordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);

        final Button ButtonSubmit =findViewById(R.id.sendButton);
        final EditText cEmail = findViewById(R.id.emailEditText);

        ButtonSubmit.setOnClickListener(new View.OnClickListener() {


            public void onClick(View v) {


                String strEmail = cEmail.getText().toString();

                if (strEmail.length() > 0) {

                    final RelativeLayout loaderLayout = findViewById(R.id.loaderLayout);
                    loaderLayout.setVisibility(View.VISIBLE);
                    FirebaseAuth auth = FirebaseAuth.getInstance();

                    auth.sendPasswordResetEmail(strEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {


                            if (task.isSuccessful()) {
                                loaderLayout.setVisibility(View.GONE);
                                startToast("이메일을 보냈습니다");
                                finish();
                                startLoginActivity();
                            }
                        }
                    });

                } else {

                    startToast("정확한 이메일을 입력해주세요");
                }

            }
        });

    }

    private void startToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
    private void startLoginActivity() {
        Intent intent = new Intent(this,LoginActivity.class);
        startActivity(intent);
    }


}
