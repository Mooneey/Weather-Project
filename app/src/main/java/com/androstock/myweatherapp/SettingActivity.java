package com.androstock.myweatherapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import android.widget.TimePicker;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.Set;


public class SettingActivity extends AppCompatActivity {



    SharedPreferences sf;               // 로컬 DB
    SharedPreferences.Editor editor;    // DB 편집 객체
    TextView setNumber;
    TextView version;


    LinearLayout voiceSettingLayout;
    LinearLayout alarmLayout;
    LinearLayout moodLayout;
    Switch moodPower;
    Switch alarmPower;
    Switch gpsPower;


    ImageButton closeBtn;
    Button btnColor;

    TimePickerFragment newFragment;

    LinearLayout protectLayout;

    LinearLayout etcLayout;
    TextView setButton;
    TextView timeSelect;

    TimePicker timePicker;
    TextView alarmText;
    Calendar calendar;
    String format = "";
    String settingTime;
    String time;
    String ampm;

    private AlertDialog dialog;

    final String[] items = new String[]{"하얀색", "빨간색", "초록색", "파란색", "노란색", "하늘색", "보라색"};
    final int[] selectedindex = {0};

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        timeSelect = findViewById(R.id.timeSelect);
        closeBtn = findViewById(R.id.closeBtn);

        version = findViewById(R.id.version_Info);
        //osLicense = findViewById(R.id.OS_License);

        //voiceSettingLayout = findViewById(R.id.voice_Setting_Layout);
        alarmLayout = findViewById(R.id.alarmLayout);
        moodLayout = findViewById(R.id.moodLayout);

        moodPower = findViewById(R.id.mood_Power);
        alarmPower = findViewById(R.id.alarm_Power);
        gpsPower = findViewById(R.id.GPS_Power);

        etcLayout = findViewById(R.id.etc_layout);
        setButton = findViewById(R.id.btn_set);
        newFragment = new TimePickerFragment();

        sf = getSharedPreferences("settingFile", MODE_PRIVATE); // 로컬 DB 객체
        editor = sf.edit(); // DB 편집 객체


        timePicker = (TimePicker) findViewById(R.id.timePicker);
        //alarmText = findViewById(R.id.alarmText);
        calendar = Calendar.getInstance();

        //int hour = calendar.get(Calendar.HOUR_OF_DAY);
        //int min = calendar.get(Calendar.MINUTE);
        int hour = sf.getInt("hour", calendar.get(Calendar.HOUR_OF_DAY));
        int min = sf.getInt("min", calendar.get(Calendar.MINUTE));
        timePicker.setHour(hour);
        timePicker.setMinute(min);
        showTime(hour, min);

        gpsPower.setChecked(false);


        btnColor = (Button) findViewById(R.id.colorSelect);


        /*******************************************
         * 알람 파워 확인부분
         ******************************************/
        boolean alarm = sf.getBoolean("alarm", false);
        if (alarm) {
            alarmPower.setChecked(true);
            alarmLayout.setVisibility(View.VISIBLE);

        } else {
            alarmPower.setChecked(false);
            alarmLayout.setVisibility(View.GONE);

        }

        /*******************************************
         * 무드 파워 확인부분
         ******************************************/
        boolean mood = sf.getBoolean("mood", false);
        if (mood) {
            moodPower.setChecked(true);
            moodLayout.setVisibility(View.VISIBLE);

        } else {
            moodPower.setChecked(false);
            moodLayout.setVisibility(View.GONE);

        }



        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SettingActivity.this, MainActivity.class));
                finish();
            }
        });

        version.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(SettingActivity.this, "Version 3.1.31", Toast.LENGTH_SHORT).show();
            }
        });



        alarmPower.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    editor.putBoolean("alarm", true);
                    editor.commit();
                    alarmLayout.setVisibility(View.VISIBLE);
                   // Toast.makeText(SettingActivity.this, "Alarm On", Toast.LENGTH_SHORT).show();
                } else {
                    editor.putBoolean("alarm", false);
                    editor.commit();
                    alarmLayout.setVisibility(View.GONE);

                    //Toast.makeText(SettingActivity.this, "Alarm Off", Toast.LENGTH_SHORT).show();
                }
            }
        });

        moodPower.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    editor.putBoolean("mood", true);
                    editor.commit();
                    moodLayout.setVisibility(View.VISIBLE);
                    // Toast.makeText(SettingActivity.this, "Alarm On", Toast.LENGTH_SHORT).show();
                } else {
                    editor.putBoolean("mood", false);
                    editor.commit();
                    moodLayout.setVisibility(View.GONE);

                    //Toast.makeText(SettingActivity.this, "Alarm Off", Toast.LENGTH_SHORT).show();
                }
            }
        });


        btnColor.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {


                AlertDialog.Builder dialog = new AlertDialog.Builder(SettingActivity.this);
                dialog.setTitle("무드등 색상을 선택하세요.")
                        .setSingleChoiceItems(items, 0,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        selectedindex[0] = which;
                                    }
                                })
                        .setPositiveButton("확인", new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which){
                                Toast.makeText(SettingActivity.this, items[selectedindex[0]], Toast.LENGTH_SHORT).show();
                                btnColor.setText(items[selectedindex[0]]);

                            }
                        }).create().show();
            }
        });

        SharedPreferences sf = getSharedPreferences("sFile",MODE_PRIVATE);
        String text = sf.getString("text","색상 선택");
        btnColor.setText(text);

    }

    protected void onStop(){
        super.onStop();
        SharedPreferences sharedPreferences = getSharedPreferences("sFile",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String text = items[selectedindex[0]]; // 사용자가 입력한 저장할 데이터
        editor.putString("text",text);
        editor.commit();

    }


//    public void SwitchCheckedListener() {
//        // 보이스피싱 파워 체크
//        CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                switch (buttonView.getId()) {
//
//                    // 보이스피싱 파워 체크
//                    case R.id.voice_Power:
//                        if (isChecked) {
//                            editor.putBoolean("voice_fishing", true);
//                            editor.commit();
//
//                            //alarmLayout.setVisibility(View.VISIBLE);
//
//                            //Toast.makeText(SettingActivity.this, "VoiceFishing On", Toast.LENGTH_SHORT).show();
//                        } else {
//                            editor.putBoolean("voice_fishing", false);
//                            editor.commit();
//
//
//                            voiceSettingLayout.setVisibility(View.GONE);
//                        }
//                        break;
//
//                    case R.id.alarm_Power:
//                        // default 값으로 우선 전부 기능 꺼져있게 만듬
//                        if (isChecked) {
//                            editor.putBoolean("alarm", true);
//                            editor.commit();
//                            alarmLayout.setVisibility(View.VISIBLE);
//                            Toast.makeText(SettingActivity.this, "Alarm On", Toast.LENGTH_SHORT).show();
//                        } else {
//                            editor.putBoolean("alarm", false);
//                            editor.commit();
//                            alarmLayout.setVisibility(View.GONE);
//
//                            Toast.makeText(SettingActivity.this, "Alarm Off", Toast.LENGTH_SHORT).show();
//                        }
//                        break;
//                }
//            }
//        };
//        voicePower.setOnCheckedChangeListener(onCheckedChangeListener);
//        alarmPower.setOnCheckedChangeListener(onCheckedChangeListener);
//    }
//
//
//
//
//
//
//    /*******************************************
//     * 설정창 종료, 텍스트 클릭 이벤트
//     *******************************************/
//    public void TextClickListener() {
//        View.OnClickListener Listener = new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                switch (view.getId()) {
//                    case R.id.closeBtn:
//                        finish();
//                        Toast.makeText(SettingActivity.this, "Version 3.1.31", Toast.LENGTH_SHORT).show();
//                        break;
//
////                        break;
//
//                    // 버전 정보
//                    case R.id.version_Info:
//                        Toast.makeText(SettingActivity.this, "Version 3.1.31", Toast.LENGTH_SHORT).show();
//                        break;
//
///*                    // OS 라이센스 정보
//                    case R.id.OS_License:
//                        Toast.makeText(SettingActivity.this, "OS 클릭", Toast.LENGTH_SHORT).show();
//                        break;*/
//                }
//            }
//        };
//
//        closeBtn.setOnClickListener(Listener);
//        setNumber.setOnClickListener(Listener);
//        version.setOnClickListener(Listener);
//        //osLicense.setOnClickListener(Listener);
//    }
//

    /****************************************
     * 알람 설정
     *****************************************/
    public void setTime(View view) {
        int hour = timePicker.getCurrentHour();
        int min = timePicker.getCurrentMinute();
        showTime(hour, min);
        Toast.makeText(SettingActivity.this, "알람이 설정되었습니다.", Toast.LENGTH_SHORT).show();
        String tmpTime, tmpHour, tmpMin;
        if(hour<10)
            tmpHour = "0" + Integer.toString(hour);
        else
            tmpHour = Integer.toString(hour);

        if(min<10)
            tmpMin = "0" + Integer.toString(min);
        else
            tmpMin = Integer.toString(min);

        tmpTime = tmpHour + tmpMin;

        //Toast.makeText(SettingActivity.this, tmpTime, Toast.LENGTH_SHORT).show();

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");
                    if (success) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);
                        dialog = builder.setMessage("등록 성공")
                                .setPositiveButton("확인", null)
                                .create();
                        dialog.show();
                    }
                    else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);
                        dialog = builder.setMessage("등록 실패")
                                .setNegativeButton("확인", null)
                                .create();
                        dialog.show();
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        AddTimeRequest addGrandRequest = new AddTimeRequest(tmpTime, responseListener);
        RequestQueue qu = Volley.newRequestQueue(SettingActivity.this);
        qu.add(addGrandRequest);
    }

    public void showTime(int hour, int min) {
        if (hour == 0) {
            hour += 12;
            format = "AM";
        } else if (hour == 12) {
            format = "PM";
        } else if (hour > 12) {
            hour -= 12;
            format = "PM";
        } else {
            format = "AM";
        }

        //alarmText.setText(new StringBuilder().append(format).append(" ")
             //           .append(hour).append(" : ").append(min));
        ampm = new StringBuilder().append(format).toString();
        settingTime = new StringBuilder().append(hour).append(" : ").append(min).toString();
        //Toast.makeText(SettingActivity.this, ampm  + "  " + settingTime, Toast.LENGTH_SHORT).show();
        editor.putInt("hour", hour);
        editor.putInt("min", min);
        editor.putString("ampm", ampm);
        editor.putString("time", settingTime);
        editor.commit();

    }

}
