package com.androstock.myweatherapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.media.ExifInterface;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.app.Service;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.IBinder;
import android.util.Log;

import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;


import static android.os.Environment.DIRECTORY_PICTURES;

public class MainActivity extends AppCompatActivity {

    // Project Created by Ferdousur Rahman Shajib
    // www.androstock.com
    private GpsTracker gpsTracker;

    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    String[] REQUIRED_PERMISSIONS  = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

    TextView selectCity, cityField, currentTemperatureField, weatherIcon, updatedField; //detailsField, humidity_field, pressure_field
    ProgressBar loader;
    Typeface weatherFont;

    String insert_city = "Seoul";//"Seoul";
    String base_country = ", KR";
    String city = insert_city + base_country;
    String OPEN_WEATHER_MAP_API = "6d4593e61f42df835facb4e23e804515";
    /* Please Put your API KEY here */
    Button btnSendData;
    Button timeSetting;
    Button cancel;
    Button P_advice;
    EditText SendData;
    private AlertDialog dialog;
    private String Hour="25";
    private String Minute="00";
    TimePickerFragment newFragment;
    Button addBtn;
    ImageView settingBtn;
    TextView advice;

    boolean alarmOn;
    ImageView alarmImage;
    TextView ampm;
    TextView alarmTime;
    String ampmS;
    String alarmTimeS;

    String w_state;
    String fakeCity;


    SharedPreferences sf;               // 로컬 DB
    SharedPreferences.Editor editor;    // DB 편집 객체

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getSupportActionBar().hide();
        setContentView(R.layout.activity_main);

        newFragment = new TimePickerFragment();
        setButton();

        loader = (ProgressBar) findViewById(R.id.loader);
        selectCity = (TextView) findViewById(R.id.selectCity);
        cityField = (TextView) findViewById(R.id.city_field);
        updatedField = (TextView) findViewById(R.id.updated_field);
        currentTemperatureField = (TextView) findViewById(R.id.current_temperature_field);
//        humidity_field = (TextView) findViewById(R.id.humidity_field2);
//        pressure_field = (TextView) findViewById(R.id.pressure_field2);
//        detailsField = (TextView) findViewById(R.id.details_field2);
        weatherIcon = (TextView) findViewById(R.id.weather_icon);
        weatherFont = Typeface.createFromAsset(getAssets(), "fonts/weathericons_regular_webfont.ttf");
        btnSendData = (Button) findViewById(R.id.btnSendData);
        timeSetting = (Button) findViewById(R.id.timeSetting);
        cancel = (Button) findViewById(R.id.cancel);
        SendData = (EditText) findViewById(R.id.sendData);
        weatherIcon.setTypeface(weatherFont);
        settingBtn = findViewById(R.id.settingButton);

        advice = findViewById(R.id.advice);
        int color_White = ContextCompat.getColor(getApplicationContext(), R.color.white);
        int color_Gray = ContextCompat.getColor(getApplicationContext(), R.color.gray);
        alarmImage = findViewById(R.id.alarmImage);
        ampm = findViewById(R.id.ampm);
        alarmTime = findViewById(R.id.alarmTime);

        sf = getSharedPreferences("settingFile", MODE_PRIVATE); // 로컬 DB 객체
        editor = sf.edit(); // DB 편집 객체

        editor.putString("fakecity", "Cheongju");
        editor.commit();

        alarmOn = sf.getBoolean("alarm", false);
        if (alarmOn) {
            alarmImage.setImageResource(R.drawable.ic_baseline_alarm_50);
            ampm.setTextColor(color_White);
            alarmTime.setTextColor(color_White);

        } else {
            alarmImage.setImageResource(R.drawable.ic_baseline_alarm_50_gray);
            ampm.setTextColor(color_Gray);
            alarmTime.setTextColor(color_Gray);
        }

        taskLoadUp(city);
        setTime();

//        selectCity.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(MainActivity.this, "위치를 파악 중입니다...", Toast.LENGTH_SHORT).show();
//
//                Handler handler = new Handler();
//                handler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        fakeCity = sf.getString("fakecity", "Cheongju");
//                        city = fakeCity + base_country;
//                        taskLoadUp(city);
//                        advice.setText("큰 일교차 조심하세요!!");
//                    }
//                }, 2000); //딜레이 타임 조절
//
////                AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
////                alertDialog.setTitle("도시 변경 (영어로 입력해주세요.)");
////                final EditText input = new EditText(MainActivity.this);
////                input.setText(insert_city);
////                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
////                        LinearLayout.LayoutParams.MATCH_PARENT,
////                        LinearLayout.LayoutParams.MATCH_PARENT);
////                input.setLayoutParams(lp);
////                alertDialog.setView(input);
////
////                alertDialog.setPositiveButton("변경",
////                        new DialogInterface.OnClickListener() {
////                            public void onClick(DialogInterface dialog, int which) {
////                                city = input.getText().toString();
////                                taskLoadUp(city);
////                            }
////                        });
////                alertDialog.setNegativeButton("취소",
////                        new DialogInterface.OnClickListener() {
////                            public void onClick(DialogInterface dialog, int which) {
////                                dialog.cancel();
////                            }
////                        });
////                alertDialog.show();
//            }
//        });


        settingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(MainActivity.this, "설정창 누름", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                startActivity(intent);
            }
        });

        advice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                city = "Seoul" + base_country;
                taskLoadUp(city);
                advice.setText("우산을 챙겨볼까요?");
            }
        });

        Button cambtn = (Button) findViewById(R.id.camera);
        cambtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CameraActivity.class);
                startActivity(intent);
            }
        });

        if (!checkLocationServicesStatus()) {

            showDialogForLocationServiceSetting();
        }else {

            checkRunTimePermission();
        }

       final TextView textview_address = (TextView)findViewById(R.id.advice);

        Button ShowLocationButton = (Button) findViewById(R.id.selectCity);
        ShowLocationButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View arg0)
            {

                gpsTracker = new GpsTracker(MainActivity.this);

                double latitude = gpsTracker.getLatitude();
                double longitude = gpsTracker.getLongitude();

                String address = getCurrentAddress(latitude, longitude);
                //textview_address.setText(address);
                StringTokenizer tokens = new StringTokenizer(address);

                String t_country    = tokens.nextToken(" ") ;
                String t_area    = tokens.nextToken(" ") ;
                String t_city    = tokens.nextToken(" ") ;
                //textview_address.setText(t_city);

                Toast.makeText(MainActivity.this, "현재위치 \n위도 " + latitude + "\n경도 " + longitude, Toast.LENGTH_LONG).show();

                final EditText input = new EditText(MainActivity.this);

                input.setText("Cheongju");
                city = input.getText().toString();
                taskLoadUp(city);

            }
        });

        P_advice = (Button)findViewById(R.id.advice2);

        P_advice.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                AlertDialog.Builder ad = new AlertDialog.Builder(MainActivity.this, R.style.MyDialogTheme);
                ad.setTitle("화분 조언");
                ad.setMessage(Html.fromHtml("<font color='#00469B'><br>물 좀 줘봐<br></font>"));
                ad.show();
            }
        });
    }

    public void setTime()
    {

        ampmS = sf.getString("ampm", "");
        alarmTimeS = sf.getString("time", "");

        //Toast.makeText(MainActivity.this, ampmS + "  " + alarmTimeS, Toast.LENGTH_SHORT).show();

        ampm.setText(ampmS);
        alarmTime.setText(alarmTimeS);

    }

    public void taskLoadUp(String query) {
        if (Function.isNetworkAvailable(getApplicationContext())) {
            DownloadWeather task = new DownloadWeather();
            task.execute(query);
        } else {
            Toast.makeText(getApplicationContext(), "인터넷 연결이 끊겨있습니다.", Toast.LENGTH_LONG).show();
        }
    }

    void getaTimeData(int getHour, int getMinute){
        if(getHour<10){
            Hour = "0" + String.valueOf(getHour);
        }
        else
            Hour = String.valueOf(getHour);
        if(getMinute<10){
            Minute = "0" + String.valueOf(getMinute);
        }
        else
            Minute = String.valueOf(getMinute);
    }
    public void setButton(){
        addBtn = (Button)findViewById(R.id.btnSendData);
        addBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(newFragment.val)
                    getaTimeData(newFragment.DvHour,newFragment.DvMinute);
                String tmpTime;
                tmpTime = Hour + Minute;
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");
                            if (success) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                dialog = builder.setMessage("등록 성공")
                                        .setPositiveButton("확인", null)
                                        .create();
                                dialog.show();
                            }
                            else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
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
                RequestQueue qu = Volley.newRequestQueue(MainActivity.this);
                qu.add(addGrandRequest);
            }
        });
    }
//    public void onBtnClicked(View v){
//        newFragment.show(getSupportFragmentManager(),"TimePicker");
//    }

    public void onClClicked(View v){
        TextView tv = (TextView) findViewById(R.id.sendData);
        tv.setText("설정된 시간이 없습니다.\n");
        Hour = "25";
        Minute = "00";
        newFragment.val = false;
    }

    class DownloadWeather extends AsyncTask < String, Void, String > {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loader.setVisibility(View.VISIBLE);

        }
        protected String doInBackground(String...args) {
            String xml = Function.excuteGet("http://api.openweathermap.org/data/2.5/weather?q=" + args[0] +
                    "&units=metric&appid=" + OPEN_WEATHER_MAP_API);
            return xml;
        }
        @Override
        protected void onPostExecute(String xml) {

            try {
                JSONObject json = new JSONObject(xml);
                if (json != null) {
                    JSONObject details = json.getJSONArray("weather").getJSONObject(0);
                    JSONObject main = json.getJSONObject("main");
                    DateFormat df = DateFormat.getDateTimeInstance();

                    cityField.setText(json.getString("name").toUpperCase(Locale.KOREA)); // + ", " + json.getJSONObject("sys").getString("country"));
                    currentTemperatureField.setText(String.format("%.1f", main.getDouble("temp")) + "°");
                    //detailsField.setText(details.getString("description").toUpperCase(Locale.US));
                    //humidity_field.setText("Humidity: " + main.getString("humidity") + "%");
                    //pressure_field.setText("Pressure: " + main.getString("pressure") + " hPa");
                    updatedField.setText(df.format(new Date(json.getLong("dt") * 1000)));
                    weatherIcon.setText(Html.fromHtml(Function.setWeatherIcon(details.getInt("id"),
                            json.getJSONObject("sys").getLong("sunrise") * 1000,
                            json.getJSONObject("sys").getLong("sunset") * 1000)));
                    w_state = Function.setWeatherIcon(details.getInt("id"),
                            json.getJSONObject("sys").getLong("sunrise") * 1000,
                            json.getJSONObject("sys").getLong("sunset") * 1000);
                    getAdvice(w_state);
                    btnSendData.setVisibility(View.VISIBLE);
                    timeSetting.setVisibility(View.VISIBLE);
                    cancel.setVisibility(View.VISIBLE);
                    SendData.setVisibility(View.VISIBLE);
                    loader.setVisibility(View.GONE);

                }
            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), "도시 이름을 다시 확인 해주세요.", Toast.LENGTH_SHORT).show();
            }
        }


    }

    public void getAdvice(String stateW){

        switch(stateW) {
            case "&#xf01e;" : //비, 번개
                advice.setText("번개를 동반한 비가 내립니다.");
                break;
            case "&#xf01c;" : //굵은 비
                advice.setText("빗방울이 굵은 비가 많이 내려요.");
                break;
            case "&#xf014;": //흐림
                advice.setText("날씨가 흐려요!");
                break;
            case "&#xf013;" : //구름 많음
                advice.setText("구름이 많이 껴있어요.");
                break;
            case "&#xf01b;" : //눈
                advice.setText("눈이 와요,\n 눈사람을 만들어 봐요");
                break;
            case "&#xf019;" : //비
                advice.setText("비가 와요,\n 우산을 챙겨요!");
                break;
            case "&#xf00d;" : //맑음
                advice.setText("날씨가 맑네요,\n 외출을 준비해봐요!");
                break;
            case "&#xf02e;" : //밤
                advice.setText("벌써 밤이에요,\n 내일을 준비해요!");
                break;
        }

    }

    private void showDialogForLocationServiceSetting() {

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n"
                + "위치 설정을 수정하실래요?");
        builder.setCancelable(true);
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent
                        = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }

    /*
     * ActivityCompat.requestPermissions를 사용한 퍼미션 요청의 결과를 리턴받는 메소드입니다.
     */
    @Override
    public void onRequestPermissionsResult(int permsRequestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grandResults) {

        if ( permsRequestCode == PERMISSIONS_REQUEST_CODE && grandResults.length == REQUIRED_PERMISSIONS.length) {

            // 요청 코드가 PERMISSIONS_REQUEST_CODE 이고, 요청한 퍼미션 개수만큼 수신되었다면

            boolean check_result = true;


            // 모든 퍼미션을 허용했는지 체크합니다.

            for (int result : grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }


            if ( check_result ) {

                //위치 값을 가져올 수 있음
                ;
            }
            else {
                // 거부한 퍼미션이 있다면 앱을 사용할 수 없는 이유를 설명해주고 앱을 종료합니다.2 가지 경우가 있습니다.

                if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[1])) {

                    Toast.makeText(MainActivity.this, "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요.", Toast.LENGTH_LONG).show();
                    finish();


                }else {

                    Toast.makeText(MainActivity.this, "퍼미션이 거부되었습니다. 설정(앱 정보)에서 퍼미션을 허용해야 합니다. ", Toast.LENGTH_LONG).show();

                }
            }

        }
    }

    void checkRunTimePermission(){

        //런타임 퍼미션 처리
        // 1. 위치 퍼미션을 가지고 있는지 체크합니다.
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION);


        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) {

            // 2. 이미 퍼미션을 가지고 있다면
            // ( 안드로이드 6.0 이하 버전은 런타임 퍼미션이 필요없기 때문에 이미 허용된 걸로 인식합니다.)


            // 3.  위치 값을 가져올 수 있음



        } else {  //2. 퍼미션 요청을 허용한 적이 없다면 퍼미션 요청이 필요합니다. 2가지 경우(3-1, 4-1)가 있습니다.

            // 3-1. 사용자가 퍼미션 거부를 한 적이 있는 경우에는
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, REQUIRED_PERMISSIONS[0])) {

                // 3-2. 요청을 진행하기 전에 사용자가에게 퍼미션이 필요한 이유를 설명해줄 필요가 있습니다.
                Toast.makeText(MainActivity.this, "이 앱을 실행하려면 위치 접근 권한이 필요합니다.", Toast.LENGTH_LONG).show();
                // 3-3. 사용자게에 퍼미션 요청을 합니다. 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(MainActivity.this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);


            } else {
                // 4-1. 사용자가 퍼미션 거부를 한 적이 없는 경우에는 퍼미션 요청을 바로 합니다.
                // 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(MainActivity.this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);
            }

        }

    }


    public String getCurrentAddress( double latitude, double longitude) {

        //지오코더... GPS를 주소로 변환
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        List<Address> addresses;

        try {

            addresses = geocoder.getFromLocation(
                    latitude,
                    longitude,
                    7);
        } catch (IOException ioException) {
            //네트워크 문제
            Toast.makeText(this, "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show();
            return "지오코더 서비스 사용불가";
        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(this, "잘못된 GPS 좌표", Toast.LENGTH_LONG).show();
            return "잘못된 GPS 좌표";

        }



//        if (addresses == null || addresses.size() == 0) {
//            Toast.makeText(this, "주소 미발견", Toast.LENGTH_LONG).show();
//            return "주소 미발견";
//
//        }

        Address address = addresses.get(0);
        return address.getAddressLine(0).toString()+"\n";

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case GPS_ENABLE_REQUEST_CODE:

                //사용자가 GPS 활성 시켰는지 검사
                if (checkLocationServicesStatus()) {
                    if (checkLocationServicesStatus()) {

                        Log.d("@@@", "onActivityResult : GPS 활성화 되있음");
                        checkRunTimePermission();
                        return;
                    }
                }

                break;
        }
    }

    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }
}