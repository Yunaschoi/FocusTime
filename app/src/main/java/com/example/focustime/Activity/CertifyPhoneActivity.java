package com.example.focustime.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.focustime.R;
import com.google.firebase.auth.FirebaseAuth;

public class CertifyPhoneActivity extends AppCompatActivity {

    private TextView phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_certify_phone);

        final EditText editText= (EditText)findViewById(R.id.editTextPhone);

        phoneNumber = findViewById(R.id.textView_Phone); // 회원 정보에서 입력한 값을 받음

        Intent intent = getIntent();
        String userPhoneNumber = intent.getStringExtra("userPhoneNumber");

        phoneNumber.setText(userPhoneNumber);  // 회원정보에서 인텐트로 넘어온 값을 받음

        findViewById(R.id.buttonContinue).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //   String code = CountryData.countryAreaCode[spinner.getSelectedItemPosition()];


                String number = editText.getText().toString().trim();

               // String number = phoneNumber.getText().toString().trim();

                if(number.isEmpty() || number.length() < 10 ) {  // 숫자칸이 빈칸이거나 10자리보다 작을 때
                    editText.setError("Valid number is required");
                    editText.requestFocus();
                    return;
                }

                //   String phoneNumber = "+"+ code + number;  // 국가 코드 + 휴대폰 번호를 받음

                String phoneNumber = number;  // 한국 휴대폰 번호를 받음

                Intent intent = new Intent(CertifyPhoneActivity.this, VerifyPhoneActivity.class);
                intent.putExtra("phoneNumber",phoneNumber);  // 인텐트로 여기서 입력한 번호를 인증하는 화면으로 보내줌
                startActivity(intent);
            }
        });


    }

    /*

    @Override
    protected void onStart() {
        super.onStart();

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {  // 현재로그인한 유저가 있다면

            Intent intent = new Intent(this, ProfileActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);


        }

    }

     */

}
