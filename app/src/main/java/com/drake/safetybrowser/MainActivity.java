package com.drake.safetybrowser;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;
import com.android.volley.request.SimpleMultiPartRequest;
import com.android.volley.request.StringRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.Volley;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.NetworkInterface;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    String[] text_to_search_service = { "http://www.ssicortex.com/GetTxt2Search", "http://www.ssitectonic.com/GetTxt2Search", "http://www.ssihedonic.com/GetTxt2Search" };
    String[] domain_service = { "http://www.ssicortex.com/GetDomains", "http://www.ssitectonic.com/GetDomains", "http://www.ssihedonic.com/GetDomains" };
    String[] send_service = { "http://www.ssicortex.com/SendDetails", "http://www.ssitectonic.com/SendDetails", "http://www.ssihedonic.com/SendDetails" };
    String[] notifications_service = { "http://www.ssicortex.com/GetNotifications", "http://www.ssitectonic.com/GetNotifications", "http://www.ssihedonic.com/GetNotifications" };
    String API_KEY = "6b8c7e5617414bf2d4ace37600b6ab71";
    String BRAND_CODE = "YB";
    String domain = "";
    String text_search = "";
    String get_external_ip_address = "";
    int detect_no_internet_connection = 0;
    int domain_count_max = -1;
    int domain_count_current = 0;
    boolean loadingFinished = true;
    boolean redirect = false;
    boolean isConnected;
    boolean isHijacked;
    boolean isLoadingFinished = false;
    boolean isHelpAndSupportVisible = false;
    boolean isClearCache = false;
    private WebView webView;
    ArrayList<String> domain_list = new ArrayList<>();
    final Context context = this;
    GifImageView gifImageView_loader;
    RelativeLayout relativeLayout_connection;
    GifImageView gifImageView_connection;
    TextView textView_textchanged, textView_chatus_2, textView_emailus_1, textView_clearcache, textView_getdiagnostics;
    RelativeLayout relativeLayout_helpandsupport;
    ImageView imageView_help_back;
    DrawerLayout drawer;
    NavigationView nav_view;
    private Context mContext;
    private ImageView mAvatarImage;
    private ImageView mCoverImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        this.registerReceiver(this.mConnReceiver,
                new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        // Find ID
        drawer = findViewById(R.id.drawer_layout);
        nav_view = findViewById(R.id.nav_view);
        nav_view.setNavigationItemSelectedListener(this);
        gifImageView_loader = findViewById(R.id.gifImageView_loader);
        gifImageView_loader.setGifImageResource(R.drawable.ic_loader);
        relativeLayout_connection = findViewById(R.id.relativeLayout_connection);
        gifImageView_connection = findViewById(R.id.gifImageView_connection);
        gifImageView_connection.setGifImageResource(R.drawable.ic_connection);
        textView_textchanged = findViewById(R.id.textView_textchanged);
        relativeLayout_helpandsupport = findViewById(R.id.relativeLayout_helpandsupport);
        textView_chatus_2 = findViewById(R.id.textView_chatus_3);
        textView_emailus_1 = findViewById(R.id.textView_emailus_1);
        textView_clearcache = findViewById(R.id.textView_clearcache);
        textView_getdiagnostics = findViewById(R.id.textView_getdiagnostics);
        imageView_help_back = findViewById(R.id.imageView_help_back);
        mContext = getApplicationContext();
        // End of Find ID

        textView_chatus_2.setPaintFlags(textView_chatus_2.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
        textView_emailus_1.setPaintFlags(textView_emailus_1.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);

        textView_clearcache.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textView_clearcache.setText("CLEARING...");
                isClearCache = true;
                webView.getSettings().setAppCacheEnabled(false);
                webView.clearCache(true);
                webView.loadUrl("about:blank");
                webView.reload();
            }
        });

        textView_getdiagnostics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Process process = Runtime.getRuntime().exec("/system/bin/ping -t 1 -c 1 yb188.com");

                    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    int i;
                    char[] buffer = new char[4096];
                    StringBuffer output = new StringBuffer();
                    while ((i = reader.read(buffer)) > 0)
                        output.append(buffer, 0, i);
                    reader.close();
                    Log.d("*************", "" + output);
                    Toast.makeText(getApplicationContext(), output, Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }

//                Toast.makeText(getApplicationContext(), "Get Diagnostics", Toast.LENGTH_SHORT).show();
            }
        });

        imageView_help_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isHelpAndSupportVisible){
                    relativeLayout_helpandsupport.setVisibility(View.INVISIBLE);
                    webView.setVisibility(View.VISIBLE);
                    isHelpAndSupportVisible = false;
                } else{
                    webView.setVisibility(View.INVISIBLE);
                    relativeLayout_helpandsupport.setVisibility(View.VISIBLE);
                    relativeLayout_helpandsupport.bringToFront();
                    isHelpAndSupportVisible = true;
                }
            }
        });

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        toggle.setDrawerIndicatorEnabled(false);

        toggle.setHomeAsUpIndicator(R.drawable.ic_hamburger);
        toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawer.isDrawerVisible(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                } else {
                    drawer.openDrawer(GravityCompat.START);
                }
            }
        });

        // Functions
        GETAPI(text_to_search_service[0]);
        GETNOTIFICATIONS(notifications_service[0]);
//        GETPUBLICIPADDRESS();
//        GETIPINFO();

        textView_textchanged.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
//                if(s.length() > 0) {
//                    Toast.makeText(getApplicationContext(), "dasdasdasdsa1", Toast.LENGTH_LONG).show();
//                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
//                if(s.length() > 0) {
//                    Toast.makeText(getApplicationContext(), "dasdasdasdsa2", Toast.LENGTH_LONG).show();
//                }
            }

            public void afterTextChanged(Editable s) {
                if(s.length() > 0) {
                    // Load URL
                    if(domain_count_max != domain_count_current){
                        webView = findViewById(R.id.webView);
                        WebSettings webSettings = webView.getSettings();
                        webSettings.setJavaScriptEnabled(true);
                        webSettings.setDefaultTextEncodingName("utf-8");
                        webSettings.setLoadWithOverviewMode(true);
                        webSettings.setBuiltInZoomControls(false);
                        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
                        webSettings.setSavePassword(true);
                        webSettings.setSaveFormData(true);
                        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
                        webSettings.setAppCacheMaxSize(1024 * 1024 * 10);
                        webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
                        webSettings.setLoadsImagesAutomatically(true);
                        webSettings.setDomStorageEnabled(true);
                        webSettings.setBuiltInZoomControls(true);
                        webSettings.setSupportZoom(true);
                        webSettings.setDisplayZoomControls(true);
                        webView.loadUrl(domain_list.get(domain_count_current));
                        webView.requestFocus();
                        webView.setWebViewClient(new MyBrowser());
                    }
                }
            }
        });
    }




















    // WebView --------------
    private class MyBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (!loadingFinished) {
                redirect = true;
            }

            loadingFinished = false;
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageStarted(
                WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            loadingFinished = false;
            if (isConnected) {
                textView_textchanged.setText("");
                gifImageView_loader.setVisibility(View.VISIBLE);
                webView.setVisibility(View.INVISIBLE);
            }
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            if (!redirect) {
                loadingFinished = true;
            }

            if (loadingFinished && !redirect) {
                if (isConnected) {
                    if(!isLoadingFinished){
                        String webtitle = webView.getTitle();
                        String[] namesList = text_search.split(",");
                        for(String text_search : namesList){
                            boolean contains = webtitle.contains(text_search);

                            if(contains == true){
                                isHijacked = false;
                                break;
                            } else {
                                isHijacked = true;
                            }
                        }

                        if(isHijacked){
                            domain_count_current++;
                            textView_textchanged.setText("0");
                        } else{
                            gifImageView_loader.setVisibility(View.INVISIBLE);

                            if(!isHelpAndSupportVisible){
                                webView.setVisibility(View.VISIBLE);
                            }

                            isLoadingFinished = true;
                        }
                    } else{
                        String webtitle = webView.getTitle();
                        String[] namesList = text_search.split(",");

                        for(String text_search : namesList){
                            boolean contains = webtitle.contains(text_search);

                            if(contains == true){
                                isHijacked = false;
                                break;
                            } else {
                                isHijacked = true;
                            }
                        }

                        if(isHijacked){
//                            Toast.makeText(getApplicationContext(), "Hijacked", Toast.LENGTH_LONG).show();
                            webView.goForward();
                        } else {
//                            Toast.makeText(getApplicationContext(), "Not Hijacked", Toast.LENGTH_LONG).show();
                            gifImageView_loader.setVisibility(View.INVISIBLE);

                            if(!isHelpAndSupportVisible){
                                webView.setVisibility(View.VISIBLE);
                            }
                        }

                        if(isClearCache){
                            isClearCache = false;
                            textView_clearcache.setText("CLEAR CACHE");
                            Toast.makeText(getApplicationContext(), "Cache has been cleared", Toast.LENGTH_LONG).show();
                        }
                    }
                }
            } else {
                redirect = false;
            }

        }
    }

    // Get MAC Address --------------
    private String GETMACADDRESS(){
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(String.format("%02X",b));
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception ex) {
        }
        return "02:00:00:00:00:00";
    }

    // Get Public IP Address
    public void getString(final VolleyCallback callback) {
        RequestQueue MyRequestQueue = Volley.newRequestQueue(this);
        StringRequest MyStringRequest = new StringRequest(Request.Method.POST, "https://canihazip.com/s", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                callback.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(com.android.volley.error.VolleyError error) {
                Toast.makeText(getApplicationContext(), "There is a problem with the server!", Toast.LENGTH_LONG).show();
            }
        });

        MyRequestQueue.add(MyStringRequest);
    }

    public interface VolleyCallback{
        void onSuccess(String result);
    }

    public void onResume(){
        super.onResume();

        getString(new VolleyCallback(){
            @Override
            public void onSuccess(String result){
                if(get_external_ip_address == ""){
                    get_external_ip_address = result;
                }

                GETIPINFO();
            }
        });
    }

    // Get Notifications
    private void GETNOTIFICATIONS(String get){
        RequestQueue MyRequestQueue = Volley.newRequestQueue(this);
        StringRequest MyStringRequest = new StringRequest(Request.Method.POST, get, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject obj = new JSONObject(response);
                    JSONArray array = obj.getJSONArray("data");

                    for(int i=0;i<array.length();i++){
                        JSONObject student = array.getJSONObject(i);

                        String id = student.getString("id");
                        String message_date = student.getString("message_date");
                        String message_title = student.getString("message_title");
                        String message_content = student.getString("message_content");
                        String status = student.getString("status");
                        String message_type = student.getString("message_type");
                        String edited_id = student.getString("edited_id");

                        String  notification = id + "*|*" + message_date + "*|*" + message_title + "*|*" + message_content + "*|*" + status + "*|*" + message_type + "*|*" + edited_id;
                        writeToFile(notification);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //This code is executed if there is an error.
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> MyData = new HashMap<>();
                MyData.put("macid", GETMACADDRESS());
                MyData.put("brand_code", BRAND_CODE);
                MyData.put("api_key", API_KEY);
                return MyData;
            }
        };

        MyRequestQueue.add(MyStringRequest);
    }

    public void writeToFile(String data)
    {
        @SuppressLint("SdCardPath") String path =
                Environment.getExternalStorageDirectory() + File.separator  + "/data/data/com.drake.safetybrowser/";
        // Create the folder.
        File folder = new File(path);
        folder.mkdirs();

        // Create the file.
        File file = new File(folder, "config.txt");

        // Save your stream, don't forget to flush() it before closing it.

        try
        {
            file.createNewFile();
            FileOutputStream fOut = new FileOutputStream(file);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
            myOutWriter.append(data);

            myOutWriter.close();

            fOut.flush();
            fOut.close();
        }
        catch (IOException e)
        {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    // Get IP Info --------------
    private void GETIPINFO(){
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://ip-api.com/json/";
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    String city = (String) response.get("city");
                    String country = (String) response.get("country");
                    String province = (String) response.get("regionName");
                    SENDDEVICEINFO(send_service[0], get_external_ip_address, city, province, country);
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "There is a problem with the server!", Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(com.android.volley.error.VolleyError error) {
                Toast.makeText(getApplicationContext(), "There is a problem with the server!", Toast.LENGTH_LONG).show();
            }
        });

        queue.add(jsObjRequest);
    }

    // Get API --------------
    private void GETAPI(String get) {
        RequestQueue MyRequestQueue = Volley.newRequestQueue(this);
        StringRequest MyStringRequest = new StringRequest(Request.Method.POST, get, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String replace_responce = StringEscapeUtils.unescapeJava(response);
                Matcher m = Pattern.compile("\\[([^)]+)\\]").matcher(replace_responce);

                while(m.find()){
                    text_search = m.group(1).replace("\"", "");
                }

                if(response.contains("OK")){
                    GETDOMAIN(domain_service[0]);
                } else{
                    Toast.makeText(getApplicationContext(), "There is a problem with the server!", Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //This code is executed if there is an error.
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> MyData = new HashMap<>();
                MyData.put("api_key", API_KEY);
                MyData.put("brand_code", BRAND_CODE);
                return MyData;
            }
        };

        MyRequestQueue.add(MyStringRequest);
    }

    // Get Domain --------------
    private void GETDOMAIN(String get) {
        RequestQueue MyRequestQueue = Volley.newRequestQueue(this);

        StringRequest MyStringRequest = new StringRequest(Request.Method.POST, get, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String replace_responce = StringEscapeUtils.unescapeJava(response);
                Matcher m = Pattern.compile("\\[([^)]+)\\]").matcher(replace_responce);

                while(m.find()){
                    domain = m.group(1);
                }

                domain = domain.replace("domain_ur", "");
                domain = domain.replace("\"", "");
                domain = domain.replace("l:", "");
                domain = domain.replace("{", "");
                domain = domain.replace("}", "");

                if(response.contains("OK")){
                    String[] namesList = domain.split(",");
                    for(String domain : namesList){
                        domain_list.add(domain);
                        domain_count_max++;
                    }

                    textView_textchanged.setText("0");

//                    Toast.makeText(getApplicationContext(), domain_list.get(0), Toast.LENGTH_LONG).show();
//                    Toast.makeText(getApplicationContext(), domain_count_max + "", Toast.LENGTH_LONG).show();
                } else{
                    Toast.makeText(getApplicationContext(), "There is a problem with the server!", Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //This code is executed if there is an error.
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> MyData = new HashMap<>();
                MyData.put("api_key", API_KEY);
                MyData.put("brand_code", BRAND_CODE);
                return MyData;
            }
        };

        MyRequestQueue.add(MyStringRequest);
    }

    // Send Device Information
    private void SENDDEVICEINFO(String get, final String ip, final String city, final String province, final String country){
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest putRequest = new StringRequest(Request.Method.POST, get,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
//                        Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError response) {
                        Toast.makeText(getApplicationContext(), "There is a problem with the server!", Toast.LENGTH_LONG).show();
                    }
                }
        ) {

            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<>();
                params.put("api_key", API_KEY);
                params.put("brand_code", BRAND_CODE);
                params.put("ip", ip);
                params.put("macid", GETMACADDRESS());
                params.put("city", city);
                params.put("province", province);
                params.put("country", country);
                return params;
            }

        };

        queue.add(putRequest);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    if (webView.canGoBack()) {
                        webView.goBack();
                    } else {
                        finish();
                    }
                    return true;
            }

        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if(drawer.isDrawerOpen(GravityCompat.END)){
            drawer.closeDrawer(GravityCompat.END);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_back) {
            webView.goBack();
        } else if (id == R.id.nav_forward) {
            webView.goForward();
        } else if (id == R.id.nav_home) {
            webView.loadUrl(domain_list.get(domain_count_current));
        } else if (id == R.id.nav_reload) {
            webView.reload();
        } else if (id == R.id.nav_hard_reload) {
            webView.getSettings().setAppCacheEnabled(false);
            webView.clearCache(true);
            webView.loadUrl("about:blank");
            webView.reload();
        } else if (id == R.id.item_help) {
            Toast.makeText(getApplicationContext(), "Help and Support", Toast.LENGTH_LONG).show();
        } else if (id == R.id.item_notification) {
            Toast.makeText(getApplicationContext(), "Notification", Toast.LENGTH_LONG).show();
        }
//        else if (id == R.id.nav_zoom_reset) {
//
//        } else if (id == R.id.nav_zoom_in) {
//
//        } else if (id == R.id.nav_zoom_out) {
//
//        }
        else if (id == R.id.nav_exit) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    context);
            alertDialogBuilder.setTitle("\n" + "Exit the program?");
            alertDialogBuilder
                    .setCancelable(false)
                    .setNegativeButton("Yes",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int id) {
                            MainActivity.this.finish();
                        }
                    })
                    .setPositiveButton("No",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        int id = item.getItemId();

        if (id == R.id.item_help) {
            if(isHelpAndSupportVisible){
                relativeLayout_helpandsupport.setVisibility(View.INVISIBLE);
                if(isLoadingFinished){
                    webView.setVisibility(View.VISIBLE);
                }
                isHelpAndSupportVisible = false;
            } else{
                webView.setVisibility(View.INVISIBLE);
                relativeLayout_helpandsupport.setVisibility(View.VISIBLE);
                relativeLayout_helpandsupport.bringToFront();
                isHelpAndSupportVisible = true;
            }
        } else if (id == R.id.item_notification) {
            drawer.openDrawer(GravityCompat.END);
//            nav_view.getMenu().clear();
//            nav_view.inflateMenu(R.menu.activity_notification_drawer);
//            invalidateOptionsMenu();
//            Toast.makeText(getApplicationContext(), "Notification", Toast.LENGTH_LONG).show();
//            if (drawer.isDrawerVisible(GravityCompat.START)) {
//                drawer.closeDrawer(GravityCompat.START);
//            } else {
//                drawer.openDrawer(GravityCompat.START);
//            }
        }

//        return true;
        return super.onOptionsItemSelected(item);
    }

    private void displayRightNavigation(){
        final NavigationView navigationViewRight = (NavigationView) findViewById(R.id.nav_view_notification);
        navigationViewRight.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // Handle navigation view item clicks here.
                int id = item.getItemId();

//                if (id == R.id.nav_camera_right) {
//                    // Handle the camera action
//                } else if (id == R.id.nav_gallery_right) {
//
//                } else if (id == R.id.nav_slideshow_right) {
//
//                } else if (id == R.id.nav_manage_right) {
//
//                } else if (id == R.id.nav_share_right) {
//
//                } else if (id == R.id.nav_send_right) {
//
//                }

                Toast.makeText(MainActivity.this, "Handle from navigation right", Toast.LENGTH_SHORT).show();
                DrawerLayout drawer = findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.END);
                return true;

            }
        });
    }

    private BroadcastReceiver mConnReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            boolean noConnectivity = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
            String reason = intent.getStringExtra(ConnectivityManager.EXTRA_REASON);
            boolean isFailover = intent.getBooleanExtra(ConnectivityManager.EXTRA_IS_FAILOVER, false);
            NetworkInfo currentNetworkInfo = intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
            NetworkInfo otherNetworkInfo = intent.getParcelableExtra(ConnectivityManager.EXTRA_OTHER_NETWORK_INFO);

            if(currentNetworkInfo.isConnected()){
                isConnected = true;
//                Toast.makeText(getApplicationContext(), "Connected", Toast.LENGTH_LONG).show();
                relativeLayout_connection.setVisibility(View.INVISIBLE);
//                gifImageView_connection.setVisibility(View.INVISIBLE);

                if(detect_no_internet_connection == 1){
                    webView.reload();
                }
            }else{
                isConnected = false;
                detect_no_internet_connection = 1;
//                Toast.makeText(getApplicationContext(), "Check your Internet Connection", Toast.LENGTH_LONG).show();
                webView.setVisibility(View.INVISIBLE);
                gifImageView_loader.setVisibility(View.INVISIBLE);
                relativeLayout_connection.setVisibility(View.VISIBLE);
//                gifImageView_connection.setVisibility(View.VISIBLE);
            }
        }
    };
}
