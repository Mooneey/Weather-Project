package com.androstock.myweatherapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {
    public int DvHour, DvMinute;
    public boolean val = false;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){

        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        TimePickerDialog tpd = new TimePickerDialog(getActivity(),
                AlertDialog.THEME_HOLO_LIGHT,this,
                hour, minute, DateFormat.is24HourFormat(getActivity()));
        TextView tvTitle = new TextView(getActivity());
        tvTitle.setText("시간 설정");
        tvTitle.setBackgroundColor(Color.parseColor("#ffEEE8AA"));
        tvTitle.setPadding(5,3,5,3);
        tvTitle.setGravity(Gravity.CENTER_HORIZONTAL);
        tpd.setCustomTitle(tvTitle);
        return tpd;
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute){

        TextView tv = (TextView)getActivity().findViewById(R.id.sendData);
        String aMpM = "AM";
        if(hourOfDay > 11){
            aMpM = "PM";
        }
        int currentHour;
        if(hourOfDay > 11){
            currentHour = hourOfDay-12;
        }
        else{
            currentHour = hourOfDay;
        }
        tv.setText(String.valueOf(hourOfDay) + "시" + String.valueOf(minute) + "분\n");
        DvHour = hourOfDay;
        DvMinute = minute;
        val = true;
    }
}