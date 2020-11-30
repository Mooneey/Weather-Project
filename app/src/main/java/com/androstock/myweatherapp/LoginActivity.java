package com.androstock.myweatherapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button loginbtn = (Button) findViewById(R.id.btnlog);
        loginbtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                EditText i_id;
                EditText i_pw;

                i_id = (EditText) findViewById(R.id.id);
                i_pw = (EditText) findViewById(R.id.pw);

                String cmpid = i_id.getText().toString();
                String cmppw = i_pw.getText().toString();
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);

                if(cmpid.equals("howow")&&cmppw.equals("1234")){
                    builder.setTitle("알림").setMessage("로그인 되었습니다.").setCancelable(false).setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }

                else {
                    builder.setTitle("알림").setMessage("해당 정보의 회원이 없습니다.").setCancelable(false).setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            }
        });

        Button signbtn = (Button) findViewById(R.id.btnsign);
        signbtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SignActivity.class);
                startActivity(intent);
            }
        });
    }
}
