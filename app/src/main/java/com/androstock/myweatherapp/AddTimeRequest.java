package com.androstock.myweatherapp;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class AddTimeRequest extends StringRequest {
    private static final String URL = "http://hjy7547.cafe24.com/wg_update.php";
    private Map<String, String> param;
    public AddTimeRequest(String aTime, Response.Listener<String>listener){
        super(Method.POST,URL, listener, null);
        param = new HashMap<>();
        param.put("name", aTime);
        //Log.d("time : ",aTime);
    }

    @Override
    public Map<String, String> getParams() { return param;}
}
