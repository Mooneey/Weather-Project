package com.androstock.myweatherapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SignActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign);
        Button cmpbtn = (Button) findViewById(R.id.btncmp);
        cmpbtn.setOnClickListener(new View.OnClickListener() {
            EditText i_id;
            EditText i_pw;
            EditText i_pwi;
            EditText i_name;
            EditText i_tel;
            @Override
            public void onClick(View view) {

                i_id = (EditText) findViewById(R.id.input_id);
                i_pw = (EditText) findViewById(R.id.input_pw);
                i_pwi = (EditText) findViewById(R.id.input_pwi);
                i_name = (EditText) findViewById(R.id.input_name);
                i_tel = (EditText) findViewById(R.id.input_tel);

                String cmppw1 = i_pw.getText().toString();
                String cmppw2 = i_pwi.getText().toString();

                AlertDialog.Builder builder = new AlertDialog.Builder(SignActivity.this);

                if(cmppw1.equals(cmppw2)){
                    builder.setTitle("알림").setMessage("가입되었습니다.").setCancelable(false).setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                            startActivity(intent);
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }

                else {
                    builder.setTitle("알림").setMessage("비밀번호가 다릅니다.");
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }

            }
        });
    }
}
