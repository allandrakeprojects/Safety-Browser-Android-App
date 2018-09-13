package com.drake.safetybrowser;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.request.SimpleMultiPartRequest;

import java.io.File;

public class WebService {
    String[] diagnostics_service = { "http://www.ssicortex.com/SendDiagnostic", "http://www.ssitectonic.com/SendDiagnostic", "http://www.ssihedonic.com/SendDiagnostic" };

    private RequestQueue mRequestQueue;
    private static WebService apiRequests = null;

    public static WebService getInstance() {
        if (apiRequests == null) {
            apiRequests = new WebService();
            return apiRequests;
        }
        return apiRequests;
    }

    public void updateProfile(Context context, String API_KEY, String BRAND_CODE, String GETMACADDRESS, String file, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        try{
            SimpleMultiPartRequest request = new SimpleMultiPartRequest(Request.Method.POST, "http://www.ssicortex.com/SendDiagnostic", listener, errorListener);
//        request.setParams(data);
            mRequestQueue = RequestManager.getnstance(context);
            request.addMultipartParam("api_key", "text", API_KEY);
            request.addMultipartParam("brand_code", "text", BRAND_CODE);
            request.addMultipartParam("macid", "text", GETMACADDRESS);
            request.addFile("zipfile", file);

            request.setFixedStreamingMode(true);
            mRequestQueue.add(request);
        } catch(Exception e){
            Log.d("deleted", e.getMessage());
        }
    }
}
