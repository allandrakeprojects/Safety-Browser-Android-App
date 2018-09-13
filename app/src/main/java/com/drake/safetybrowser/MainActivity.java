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
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.error.ServerError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;
import com.android.volley.request.StringRequest;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.Volley;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.NetworkInterface;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ir.mahdi.mzip.zip.ZipArchive;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        Response.ErrorListener, Response.Listener<String>{

    String[] text_to_search_service = { "http://www.ssicortex.com/GetTxt2Search", "http://www.ssitectonic.com/GetTxt2Search", "http://www.ssihedonic.com/GetTxt2Search" };
    String[] domain_service = { "http://www.ssicortex.com/GetDomains", "http://www.ssitectonic.com/GetDomains", "http://www.ssihedonic.com/GetDomains" };
    String[] send_service = { "http://www.ssicortex.com/SendDetails", "http://www.ssitectonic.com/SendDetails", "http://www.ssihedonic.com/SendDetails" };
    String[] notifications_service = { "http://www.ssicortex.com/GetNotifications", "http://www.ssitectonic.com/GetNotifications", "http://www.ssihedonic.com/GetNotifications" };
    String[] notifications_delete_service = { "http://www.ssicortex.com/GetMessageX", "http://www.ssitectonic.com/GetMessageX", "http://www.ssihedonic.com/GetMessageX" };
    String API_KEY = "6b8c7e5617414bf2d4ace37600b6ab71";
    String BRAND_CODE = "YB";
    String domain = "";
    String text_search = "";
    String get_external_ip_address = "";
    String get_deleted_id = "";
    int detect_no_internet_connection = 0;
    int domain_count_max = -1;
    int domain_count_current = 0;
    int notifications_count = 0;
    int notification_count = 1;
    int notification_clear = 1;
    boolean loadingFinished = true;
    boolean redirect = false;
    boolean isConnected;
    boolean isHijacked;
    boolean isLoadingFinished = false;
    boolean isHelpAndSupportVisible = false;
    boolean isClearCache = false;
    boolean isUnread = false;
    boolean isHide = false;
    boolean isInsertMenu = false;
    boolean isRunning = false;
    boolean isOpened = false;
    boolean isFirstOpened = true;
    boolean new_entry = false;
    boolean detect_new_entry = false;
    private WebView webView;
    Integer parent = 0;
    Integer child = 0;
    Integer detect_deleted_notification = 0;
    Integer detect_added_notification = 0;
    Integer detect_updated_notification = 0;
    ArrayList<String> domain_list = new ArrayList<>();
    List<String> get_id = new ArrayList<>();
    List<String> get_id_delete = new ArrayList<>();
    final Context context = this;
    GifImageView gifImageView_loader;
    RelativeLayout relativeLayout_connection, relativeLayout_webview;
    GifImageView gifImageView_connection;
    TextView textView_textchanged, textView_chatus_2, textView_emailus_1, textView_clearcache, textView_getdiagnostics, textView_loader;
    RelativeLayout relativeLayout_helpandsupport, relativeLayout_loader;
    ImageView imageView_help_back;
    DrawerLayout drawer;
    NavigationView nav_view;
    NavigationView nav_view_notification;
    Menu menu_notification;
    private Context mContext;
    private ImageView mAvatarImage;
    private ImageView mCoverImage;
    Timer timer = new Timer();
//    SwipeRefreshLayout swipeContainer;
    Button button_test;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // remove title
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        this.registerReceiver(this.mConnReceiver,
                new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        Toast.makeText(getApplicationContext(), "Opened", Toast.LENGTH_LONG).show();

        // Find ID
        webView = findViewById(R.id.webView);
        drawer = findViewById(R.id.drawer_layout);
        nav_view = findViewById(R.id.nav_view);
        nav_view.setNavigationItemSelectedListener(this);
        nav_view_notification = findViewById(R.id.nav_view_notification);
        nav_view_notification.setNavigationItemSelectedListener(this);
        menu_notification = nav_view_notification.getMenu();
        gifImageView_loader = findViewById(R.id.gifImageView_loader);
        relativeLayout_loader = findViewById(R.id.relativeLayout_loader);
        gifImageView_loader.setGifImageResource(R.drawable.ic_loader);
        relativeLayout_connection = findViewById(R.id.relativeLayout_connection);
        gifImageView_connection = findViewById(R.id.gifImageView_connection);
        gifImageView_connection.setGifImageResource(R.drawable.ic_connection);
        textView_textchanged = findViewById(R.id.textView_textchanged);
        relativeLayout_helpandsupport = findViewById(R.id.relativeLayout_helpandsupport);
        relativeLayout_webview = findViewById(R.id.relativeLayout_webview);
        textView_chatus_2 = findViewById(R.id.textView_chatus_3);
        textView_emailus_1 = findViewById(R.id.textView_emailus_1);
        textView_clearcache = findViewById(R.id.textView_clearcache);
        textView_getdiagnostics = findViewById(R.id.textView_getdiagnostics);
        textView_loader = findViewById(R.id.textView_loader);
        imageView_help_back = findViewById(R.id.imageView_help_back);
//        swipeContainer = findViewById(R.id.swipeContainer);
        mContext = getApplicationContext();
        button_test = findViewById(R.id.button_test);
        // End of Find ID

        textView_chatus_2.setPaintFlags(textView_chatus_2.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
        textView_emailus_1.setPaintFlags(textView_emailus_1.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);

        textView_clearcache.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
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
                    StringBuilder output = new StringBuilder();
                    while ((i = reader.read(buffer)) > 0)
                        output.append(buffer, 0, i);
                    reader.close();
                    Toast.makeText(getApplicationContext(), output, Toast.LENGTH_LONG).show();
                     writeToFile(output + "", "sb_ping.txt");
                    String target_path = getFilesDir() + "/sb_ping.txt";
                    String destination_path = getFilesDir() + "/sb_diagnostic.zip";
                    ZipArchive zipArchive = new ZipArchive();
                    zipArchive.zip(target_path,destination_path,"");
                    uploadData();
                } catch (Exception e) {
                    Log.d("deleted", e.getMessage());
                }
            }
        });

        button_test.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                try {
                } catch(Exception e){
                    Log.d("deleted", e.getMessage());
                }
            }
        });

        imageView_help_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isHelpAndSupportVisible){
                    relativeLayout_helpandsupport.setVisibility(View.INVISIBLE);
                    relativeLayout_webview.setVisibility(View.VISIBLE);
                    isHelpAndSupportVisible = false;
                } else{
                    relativeLayout_webview.setVisibility(View.INVISIBLE);
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

        textView_textchanged.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                // Leave blank
            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // Leave blank
            }

            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
            @SuppressLint("SetJavaScriptEnabled")
            public void afterTextChanged(Editable s) {
                if(s.length() > 0) {
                    // Load URL
                    if(domain_count_max != domain_count_current){
                        WebSettings webSettings = webView.getSettings();
                        webSettings.setPluginState(WebSettings.PluginState.OFF);
                        webSettings.setMediaPlaybackRequiresUserGesture(true);
                        webSettings.setJavaScriptEnabled(true);
                        webSettings.setAllowFileAccess(true);
                        webSettings.setDefaultTextEncodingName("utf-8");
                        webSettings.setLoadWithOverviewMode(true);
                        webSettings.setUseWideViewPort(true);
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
                        webSettings.setLightTouchEnabled(true);
                        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
                        webSettings.supportMultipleWindows();
                        webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
                        Log.i("WebViewActivity", "UA: " + webView.getSettings().getUserAgentString());
                        webView.setScrollbarFadingEnabled(false);
                        webView.setInitialScale(1);
                        webView.loadUrl(domain_list.get(domain_count_current));
                        webView.requestFocus();
                        webView.setWebViewClient(new MyBrowser());
                    }
                }
            }
        });
    }

    private void uploadData() {
        try {
            String path = getFilesDir() + "/sb_diagnostic.zip";
            WebService.getInstance().updateProfile(MainActivity.this, API_KEY, BRAND_CODE, GETMACADDRESS(), path, this, this);
        } catch(Exception e){
            Log.d("deleted", e.getMessage());
        }
     }

    @Override
    public void onErrorResponse(VolleyError error) {
        Log.d("deleted", error.getMessage());
        NetworkResponse response = error.networkResponse;
        if (error instanceof ServerError && response != null) {
            try {
                String res = new String(response.data,
                        HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                // Now you can use any deserializer to make sense of data
                JSONObject obj = new JSONObject(res);
            } catch (UnsupportedEncodingException e1) {
                // Couldn't properly decode data to string
                Log.d("deleted", e1.getMessage());
            } catch (JSONException e2) {
                // returned data is not JSONObject?
                Log.d("deleted", e2.getMessage());
            }
        }
    }

    @Override
    public void onResponse(String response) {
        //Your response here
        Log.d("deleted", response);
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
                // Loading
//                swipeContainer.setRefreshing(false);
//                swipeContainer.setEnabled(false);
                textView_textchanged.setText("");
                relativeLayout_loader.setVisibility(View.VISIBLE);
                relativeLayout_webview.setVisibility(View.INVISIBLE);
            }
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onPageFinished(WebView view, String url) {
            if (!redirect) {
                loadingFinished = true;
            }

            if (loadingFinished && !redirect) {
                if (isConnected) {
                    // Loaded
//                    swipeContainer.setEnabled(false);
                    NavigationView navView = findViewById(R.id.nav_view);
                    Menu menu = navView.getMenu();
                    MenuItem nav_back = menu.findItem(R.id.nav_back);
                    MenuItem nav_forward = menu.findItem(R.id.nav_forward);

                    nav_back.setEnabled(webView.canGoBack());
                    nav_forward.setEnabled(webView.canGoForward());

                    if(!isLoadingFinished){
                        String webtitle = webView.getTitle();
                        String[] namesList = text_search.split(",");
                        for(String text_search : namesList){
                            boolean contains = webtitle.contains(text_search);

                            if(contains){
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
                            relativeLayout_loader.setVisibility(View.INVISIBLE);

                            if(!isHelpAndSupportVisible){
                                relativeLayout_webview.setVisibility(View.VISIBLE);
                            }

                            isLoadingFinished = true;

                            nav_view(true);
                        }
                    } else{
                        relativeLayout_loader.setVisibility(View.INVISIBLE);

                        if(!isHelpAndSupportVisible){
                            relativeLayout_webview.setVisibility(View.VISIBLE);
                        }

                        nav_view(true);

                        if(isClearCache){
                            isClearCache = false;
                            textView_clearcache.setText("CLEAR CACHE");
                            Toast.makeText(getApplicationContext(), "Cache has been cleared", Toast.LENGTH_LONG).show();
                        }
                    }

//                    if(!isLoadingFinished){
//
//                    }
//                    else{
//                        String webtitle = webView.getTitle();
//                        String[] namesList = text_search.split(",");
//
//                        for(String text_search : namesList){
//                            boolean contains = webtitle.contains(text_search);
//
//                            if(contains){
//                                isHijacked = false;
//                                break;
//                            } else {
//                                isHijacked = true;
//                            }
//                        }
//
//                        if(isHijacked){
//                            webView.goForward();
//                        } else {
//                            relativeLayout_loader.setVisibility(View.INVISIBLE);
//
//                            if(!isHelpAndSupportVisible){
//                                relativeLayout_webview.setVisibility(View.VISIBLE);
//                            }
//                        }
//
//                        if(isClearCache){
//                            isClearCache = false;
//                            textView_clearcache.setText("CLEAR CACHE");
//                            Toast.makeText(getApplicationContext(), "Cache has been cleared", Toast.LENGTH_LONG).show();
//                        }
//                    }
                }
            } else {
                redirect = false;
            }

        }
    }

    // nav_view Nagivation View
    private void nav_view(boolean type){
        NavigationView navView = findViewById(R.id.nav_view);
        Menu menu = navView.getMenu();
        MenuItem nav_home = menu.findItem(R.id.nav_home);
        MenuItem nav_reload = menu.findItem(R.id.nav_reload);
        MenuItem nav_hard_reload = menu.findItem(R.id.nav_hard_reload);
        nav_home.setEnabled(type);
        nav_reload.setEnabled(type);
        nav_hard_reload.setEnabled(type);
    }

    // Get MAC Address --------------
    @NonNull
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
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "There is a problem with the server!" + "\nError Code: 1000", Toast.LENGTH_LONG).show();
        }
        return "02:00:00:00:00:00";
    }

    // Get Public IP Address --------------
    public void GETPUBLICIP_V(final VolleyCallback callback) {
        RequestQueue MyRequestQueue = Volley.newRequestQueue(this);
        StringRequest MyStringRequest = new StringRequest(Request.Method.POST, "https://canihazip.com/s", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                callback.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(com.android.volley.error.VolleyError error) {
                Toast.makeText(getApplicationContext(), "There is a problem with the server!" + "\nError Code: 1001", Toast.LENGTH_LONG).show();
            }
        });

        MyRequestQueue.add(MyStringRequest);
    }

    public interface VolleyCallback{
        void onSuccess(String result);
    }

    public void onResume(){
        super.onResume();
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
                    Toast.makeText(getApplicationContext(), "There is a problem with the server!" + "\nError Code: 1002", Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(com.android.volley.error.VolleyError error) {
                Toast.makeText(getApplicationContext(), "There is a problem with the server!" + "\nError Code: 1003", Toast.LENGTH_LONG).show();
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
                Matcher m = Pattern.compile("\\[([^)]+)]").matcher(replace_responce);

                while(m.find()){
                    text_search = m.group(1).replace("\"", "");
                }

                if(response.contains("OK")){
                    GETDOMAIN(domain_service[0]);
                } else{
                    Toast.makeText(getApplicationContext(), "There is a problem with the server!" + "\nError Code: 1004", Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "There is a problem with the server!" + "\nError Code: 1005", Toast.LENGTH_LONG).show();
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
                Matcher m = Pattern.compile("\\[([^)]+)]").matcher(replace_responce);

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
                } else{
                    Toast.makeText(getApplicationContext(), "There is a problem with the server!" + "\nError Code: 1006", Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "There is a problem with the server!" + "\nError Code: 1007", Toast.LENGTH_LONG).show();
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

    // Send Device Information --------------
    private void SENDDEVICEINFO(String get, final String ip, final String city, final String province, final String country){
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest putRequest = new StringRequest(Request.Method.POST, get,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // Leave blank
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError response) {
                        Toast.makeText(getApplicationContext(), "There is a problem with the server!" + "\nError Code: 1008", Toast.LENGTH_LONG).show();
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

    // Notification
    // --------------

    // Get Notifications
    public void GETNOTIFICAITON_V(final VolleyCallback callback) {
        RequestQueue MyRequestQueue = Volley.newRequestQueue(this);
        StringRequest MyStringRequest = new StringRequest(Request.Method.POST, notifications_service[0], new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                callback.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "There is a problem with the server!" + "\nError Code: 1009", Toast.LENGTH_LONG).show();
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
    // Get Deleted Notification
    public void GETDELETEDNOTIFICATION_V(final VolleyCallback callback) {
        RequestQueue MyRequestQueue = Volley.newRequestQueue(this);

        StringRequest MyStringRequest = new StringRequest(Request.Method.POST, notifications_delete_service[0], new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                callback.onSuccess(response);
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
                MyData.put("macid", GETMACADDRESS());
                return MyData;
            }
        };

        MyRequestQueue.add(MyStringRequest);
    }
    // Write to File
    private void writeToFile(String data, String file_name){
        FileOutputStream fos = null;

        try {
            fos = openFileOutput(file_name, MODE_APPEND);
            fos.write(data.getBytes());
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "There is a problem with the server!" + "\nError Code: 1010", Toast.LENGTH_LONG).show();
        }
    }
    // Preview Notifications
    private void PreviewNotifications(){

        UpdateAvailable();

        try {
//            File fdelete = new File(getFilesDir() + "/sb_notifications.txt");
//            if (fdelete.exists()) {
//                fdelete.delete();
//            }

            final File file = new File(getFilesDir() + "/sb_notifications.txt");

            if (file.exists()) {
                String path = getFilesDir() + "/sb_notifications.txt";
                FileReader fr=new FileReader(path);
                BufferedReader br=new BufferedReader(fr);
                String s;

                int count_line = 0;
                List<String> tmp = new ArrayList<>();
                do{
                    s = br.readLine();
                    if(s != null){
                        if(s.length() > 0){
                            count_line++;
                            Log.d("asd", s);
                            tmp.add(s);
                        }
                    }
                }while(s!=null);

                int get_count_notification = 0;

                for(int i=count_line-1;i>=0;i--) {
                    String line = tmp.get(i);
                    get_count_notification++;

                    if(line.length() > 0){NavigationView navView = findViewById(R.id.nav_view_notification);
                        String message_date = "";
                        String message_title = "";
                        String message_content = "";
                        boolean isDisplay = true;

                        String[] values = line.split("\\*\\|\\*");

                        int i_inner = 1;
                        for(String str : values){
                            if(i_inner == 2){
                                message_date = str;
                            }else if(i_inner == 3){
                                message_title = str;
                            }else if(i_inner == 4){
                                String lineSep = System.getProperty("line.separator");
                                message_content = str;
                                message_content = message_content.replace("&lt;", "<");
                                message_content = message_content.replace("&gt;", ">");
                                message_content = message_content.replace("<br />", lineSep);
                            }else if(i_inner == 6){
                                if(str.equals("1")){
                                    isDisplay = false;
                                } else {
                                    isDisplay = true;
                                }
                            }else if(i_inner == 8){
                                if(str.contains("U")){
                                    isUnread = true;
                                } else {
                                    Menu menu = navView.getMenu();
                                    MenuItem notification_header = menu.findItem(99999);
                                    if(notifications_count == 0){
                                        notification_header.setTitle("Notifications");
                                    }
                                }
                            }

                            i_inner++;
                        }

                        if(isDisplay){
                            // Add Navigation View
                            Menu menu = navView.getMenu();
                            MenuItem notification_header = menu.findItem(99999);

                            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            String final_datetime = "";
                            Date past = format.parse(message_date);
                            Date now = new Date();
                            long seconds= TimeUnit.MILLISECONDS.toSeconds(now.getTime() - past.getTime());
                            long minutes=TimeUnit.MILLISECONDS.toMinutes(now.getTime() - past.getTime());
                            long hours=TimeUnit.MILLISECONDS.toHours(now.getTime() - past.getTime());
                            long days=TimeUnit.MILLISECONDS.toDays(now.getTime() - past.getTime());

                            if(seconds<60)
                            {

                                final_datetime = "just now";
                            }
                            else if(minutes<60)
                            {
                                if(minutes == 1){
                                    final_datetime = minutes+" min ago";
                                } else{
                                    final_datetime = minutes+" mins ago";
                                }
                            }
                            else if(hours<24)
                            {
                                if(hours == 1){
                                    final_datetime = hours+" hr ago";
                                } else{
                                    final_datetime = hours+" hrs ago";
                                }
                            }
                            else if(hours<48)
                            {
                                final_datetime = days+" yesterday";
                            }
                            else if(days<30)
                            {
                                if(days == 1){
                                    final_datetime = days+" day ago";
                                } else{
                                    final_datetime = days+" days ago";
                                }
                            }
                            else
                            {
                                final_datetime = "older message";
                            }

                            int final_count = Integer.valueOf(String.valueOf(get_count_notification) + "120012" +String.valueOf(notification_count));

                            // asd123
                            if(isUnread){
                                menu.add(final_count, 120, Menu.NONE,getSafeSubstring( "⍣ " + message_title, 18, "title") + " (" + final_datetime + ")");
                                menu.add(final_count, 120, Menu.NONE, getSafeSubstring(message_content, 20, "content"));

                                isUnread = false;
                                isInsertMenu = true;
                            } else {
                                menu.add(final_count, 120, Menu.NONE,getSafeSubstring( message_title, 18, "title") + " (" + final_datetime + ")");
                                menu.add(final_count, 120, Menu.NONE, getSafeSubstring(message_content, 20, "content"));

                                isUnread = false;
                                isInsertMenu = true;
                            }

                            if(!isInsertMenu){
                                notification_header.setTitle("There are currently no notifications.");
                                isInsertMenu = false;
                            }

                            ControlsInRightNavigation();
                            notification_count++;
                            notification_clear+=2;
                        }
                    }
                }
            } else {
                NavigationView navView = findViewById(R.id.nav_view_notification);
                Menu menu = navView.getMenu();
                MenuItem notification_header = menu.findItem(99999);
                notification_header.setTitle("There are currently no notifications.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // Get Update Available
    private void UpdateAvailable(){
        try {

            final File file = new File(getFilesDir() + "/sb_notifications.txt");

            if (file.exists()) {
                String path = getFilesDir() + "/sb_notifications.txt";
                FileReader fr=new FileReader(path);
                BufferedReader br=new BufferedReader(fr);
                String s;
                boolean isDisplay = true;

                int count_line = 0;
                List<String> tmp = new ArrayList<>();
                do{
                    s = br.readLine();
                    if(s != null){
                        if(s.length() > 0){
                            count_line++;
                            tmp.add(s);
                        }
                    }
                }while(s!=null);

                int get_count_notification = 0;

                for(int i=count_line-1;i>=0;i--) {
                    String line = tmp.get(i);
                    get_count_notification++;

                    if(line.length() > 0){NavigationView navView = findViewById(R.id.nav_view_notification);
                        String message_date = "";
                        String message_title = "";
                        String message_content = "";

                        String[] values = line.split("\\*\\|\\*");

                        int i_inner = 1;
                        for(String str : values){
                            if(i_inner == 2){
                                message_date = str;
                            }else if(i_inner == 3){
                                message_title = str;
                            }else if(i_inner == 4){
                                String lineSep = System.getProperty("line.separator");
                                message_content = str;
                                message_content = message_content.replace("&lt;", "<");
                                message_content = message_content.replace("&gt;", ">");
                                message_content = message_content.replace("<br />", lineSep);
                            }else if(i_inner == 6){
                                if(str.equals("1")){
                                    isDisplay = true;
                                } else {
                                    isDisplay = false;
                                }
                            }else if(i_inner == 8){
                                if(isDisplay){
                                    if(str.contains("U")){
                                        isUnread = true;
                                    } else {
                                        Menu menu = navView.getMenu();
                                        MenuItem notification_header = menu.findItem(99999);
                                        if(notifications_count == 0){
                                            notification_header.setTitle("Notifications");
                                        }
                                    }

                                }
                            }

                            i_inner++;
                        }

                        if(isDisplay){
                            MenuItem notification_header = menu_notification.findItem(99999);

                            // Add Navigation View
                            Menu menu = navView.getMenu();

                            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            String final_datetime = "";
                            Date past = format.parse(message_date);
                            Date now = new Date();
                            long seconds= TimeUnit.MILLISECONDS.toSeconds(now.getTime() - past.getTime());
                            long minutes=TimeUnit.MILLISECONDS.toMinutes(now.getTime() - past.getTime());
                            long hours=TimeUnit.MILLISECONDS.toHours(now.getTime() - past.getTime());
                            long days=TimeUnit.MILLISECONDS.toDays(now.getTime() - past.getTime());

                            if(seconds<60)
                            {
                                final_datetime = "just now";
                            }
                            else if(minutes<60)
                            {
                                if(minutes == 1){
                                    final_datetime = minutes+" min ago";
                                } else{
                                    final_datetime = minutes+" mins ago";
                                }
                            }
                            else if(hours<24)
                            {
                                if(hours == 1){
                                    final_datetime = hours+" hr ago";
                                } else{
                                    final_datetime = hours+" hrs ago";
                                }
                            }
                            else if(hours<48)
                            {
                                final_datetime = days+" yesterday";
                            }
                            else if(days<30)
                            {
                                if(days == 1){
                                    final_datetime = days+" day ago";
                                } else{
                                    final_datetime = days+" days ago";
                                }
                            }
                            else
                            {
                                final_datetime = "older message";
                            }

                            int final_count = Integer.valueOf(String.valueOf(get_count_notification) + "120012" +String.valueOf(notification_count));

                            // asd123
                            if(isUnread){
                                notifications_count++;
                                notification_header.setTitle("Notifications (" + notifications_count + ")");
                                menu.add(final_count, 120, Menu.NONE,"⍣ " + message_title + " (" + final_datetime + ")");
                                menu.add(final_count, 120, Menu.NONE, message_content);

                                isUnread = false;
                                isInsertMenu = true;
                            } else {
                                menu.add(final_count, 120, Menu.NONE,message_title + " (" + final_datetime + ")");
                                menu.add(final_count, 120, Menu.NONE, message_content);

                                isUnread = false;
                                isInsertMenu = true;
                            }

                            if(!isInsertMenu){
                                notification_header.setTitle("There are currently no notifications.");
                                isInsertMenu = false;
                            }

                            ControlsInRightNavigation();
                            notification_count++;
                            notification_clear+=2;
                        }
                    }
                }
            } else {
                NavigationView navView = findViewById(R.id.nav_view_notification);
                Menu menu = navView.getMenu();
                MenuItem notification_header = menu.findItem(99999);
                notification_header.setTitle("There are currently no notifications.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // Controls in Right Navigation
    private void ControlsInRightNavigation(){
        final NavigationView navigationViewRight = findViewById(R.id.nav_view_notification);
        navigationViewRight.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // Handle navigation view item clicks here.
                int id = item.getItemId();
                Integer get_group_id = item.getGroupId();
                String group_id = String.valueOf(item.getGroupId());

                // asd123
                if (id == 120) {
                    try {

                        String str_get = String.valueOf(get_group_id);
                        List<String> elephantList = Arrays.asList(str_get.split("120012"));
                        int inner_count = 0;
                        for(String str : elephantList){
                            inner_count++;
                            if(inner_count == 1){
                                group_id = str;
                            } else if(inner_count == 2){
                                get_group_id = Integer.parseInt(str);
                            }
                        }

                        String path = getFilesDir() + "/sb_notifications.txt";
                        FileReader fr=new FileReader(path);
                        BufferedReader br=new BufferedReader(fr);
                        String s;

                        int count_line = 0;
                        int count_notification = 0;
                        List<String> tmp = new ArrayList<>();
                        do{
                            s = br.readLine();
                            if(s != null){
                                if(s.length() > 0){
                                    count_line++;
                                    tmp.add(s);
                                }
                            }
                        }while(s!=null);

                        for(int i=count_line-1;i>=0;i--) {
                            if(tmp.get(i) != null) {
                                count_notification++;

                                if(Integer.parseInt(group_id) == count_notification){
                                    String line = tmp.get(i);
                                    String message_date = "";
                                    String message_title = "";
                                    String message_content = "";
                                    String message_status = "";
                                    String[] values = line.split("\\*\\|\\*");

                                    int i_inner = 1;
                                    for(String str : values){
                                        if(i_inner == 2){
                                            message_date = str;
                                        }else if(i_inner == 3){
                                            message_title = str;
                                        }else if(i_inner == 4){
                                            String lineSep = System.getProperty("line.separator");
                                            message_content = str;
                                            message_content = message_content.replace("&lt;", "<");
                                            message_content = message_content.replace("&gt;", ">");
                                            message_content = message_content.replace("<br />", lineSep);
                                        }else if(i_inner == 8){
                                            message_status = str;
                                        }

                                        i_inner++;
                                    }

                                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                    String final_datetime = "";
                                    Date past = format.parse(message_date);
                                    Date now = new Date();
                                    long seconds= TimeUnit.MILLISECONDS.toSeconds(now.getTime() - past.getTime());
                                    long minutes=TimeUnit.MILLISECONDS.toMinutes(now.getTime() - past.getTime());
                                    long hours=TimeUnit.MILLISECONDS.toHours(now.getTime() - past.getTime());
                                    long days=TimeUnit.MILLISECONDS.toDays(now.getTime() - past.getTime());

                                    if(seconds<60)
                                    {
                                        final_datetime = "just now";
                                    }
                                    else if(minutes<60)
                                    {
                                        if(minutes == 1){
                                            final_datetime = minutes+" min ago";
                                        } else{
                                            final_datetime = minutes+" mins ago";
                                        }
                                    }
                                    else if(hours<24)
                                    {
                                        if(hours == 1){
                                            final_datetime = hours+" hr ago";
                                        } else{
                                            final_datetime = hours+" hrs ago";
                                        }
                                    }
                                    else if(hours<48)
                                    {
                                        final_datetime = days+" yesterday";
                                    }
                                    else if(days<30)
                                    {
                                        if(days == 1){
                                            final_datetime = days+" day ago";
                                        } else{
                                            final_datetime = days+" days ago";
                                        }
                                    }
                                    else
                                    {
                                        final_datetime = "older message";
                                    }

                                    ReadNotification(message_title + " (" + final_datetime + ")", message_title, get_group_id, final_datetime);

                                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                                            context);
                                    alertDialogBuilder.setTitle(message_title);
                                    alertDialogBuilder.setMessage(message_content);
                                    alertDialogBuilder
                                            .setCancelable(false)
                                            .setPositiveButton("OK",new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog,int id) {
                                                    NavigationView navView_delete = findViewById(R.id.nav_view_notification);
                                                    Menu menu_delete = navView_delete.getMenu();
                                                    MenuItem pinMenuItem_parent = menu_delete.getItem(parent);
                                                    MenuItem pinMenuItem_child = menu_delete.getItem(child);
                                                    pinMenuItem_parent.setChecked(false);
                                                    pinMenuItem_child.setChecked(false);
                                                    dialog.cancel();
                                                }
                                            });
                                    AlertDialog alertDialog = alertDialogBuilder.create();
                                    alertDialog.show();
                                }
                            }
                        }
                    }
                    catch (Exception e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }

                return true;
            }
        });
    }
    // Read Notification
    private void ReadNotification(String title, String without_replace, Integer group_id, String date){
        try {
            Integer get_final_id = (group_id*2)-1;
            String path = getFilesDir() + "/sb_notifications.txt";
            FileReader fr = new FileReader(path);
            String s;
            String totalStr = "";
            try {
                BufferedReader br = new BufferedReader(fr);
                NavigationView navView_delete = findViewById(R.id.nav_view_notification);
                Menu menu_delete = navView_delete.getMenu();
                MenuItem pinMenuItem_parent = menu_delete.getItem(get_final_id);
                parent = get_final_id;
                child = get_final_id+1;
                MenuItem pinMenuItem_child = menu_delete.getItem(child);
                pinMenuItem_parent.setChecked(true);
                pinMenuItem_child.setChecked(true);

                if(pinMenuItem_parent.getTitle().toString().contains("⍣")){
                    while ((s = br.readLine()) != null) {
                        if(s.contains(without_replace)) {
                            Log.d("deleted", s);
                            s = s.substring(0, s.length() - 1) + "R";
                        }

                        totalStr += s + "\n";
                    }

                    FileWriter fw = new FileWriter(path);
                    fw.write(totalStr);
                    fw.close();

                    MenuItem pinMenuItem = menu_delete.getItem(get_final_id);
                    String final_replace = title.substring(1);
                    pinMenuItem.setTitle(getSafeSubstring(without_replace, 18, "title") + " (" + date + ")");

                    TextView textview_notification = findViewById(R.id.textview_notification);
                    notifications_count--;
                    if(0 < notifications_count){
                        NavigationView navView = findViewById(R.id.nav_view_notification);
                        Menu menu = navView.getMenu();
                        MenuItem notification_header = menu.findItem(99999);
                        notification_header.setTitle("Notifications (" + notifications_count + ")");
                        textview_notification.setText(notifications_count + "");
                        textview_notification.setVisibility(View.VISIBLE);
                    } else{
                        NavigationView navView = findViewById(R.id.nav_view_notification);
                        Menu menu = navView.getMenu();
                        MenuItem notification_header = menu.findItem(99999);
                        notification_header.setTitle("Notifications");
                        textview_notification.setVisibility(View.INVISIBLE);
                    }
                }


            } catch (Exception e) {
                System.out.println("Problem reading file.");
            }
        } catch (Exception e) {
            System.out.println("Problem reading file.");
        }
    }
    // Updated Notification
    private void UpdatedNotification(){
        try {
            final File file = new File(getFilesDir() + "/sb_notifications.txt");

            if (file.exists()) {
                String path = getFilesDir() + "/sb_notifications.txt";
                FileReader fr=new FileReader(path);
                BufferedReader br=new BufferedReader(fr);
                String s;

                do{
                    s = br.readLine();
                    if(s != null){
                        if(s.length() > 0){
                            String[] values = s.split("\\*\\|\\*");
                            int i_inner = 1;
                            for(String str : values){
                                if(i_inner == 7){
                                    if(!str.contains("null")){
                                        GetUpdateNotification(str);
                                    }
                                }

                                i_inner++;
                            }
                        }
                    }
                }while(s!=null);

                Log.d("123", "Preview notification");
                PreviewNotifications();

                TextView textview_notification = findViewById(R.id.textview_notification);
                if(0 < notifications_count){
                    textview_notification.setText(notifications_count + "");
                    textview_notification.setVisibility(View.VISIBLE);
                } else{
                    textview_notification.setVisibility(View.INVISIBLE);
                }
            } else {
                NavigationView navView = findViewById(R.id.nav_view_notification);
                Menu menu = navView.getMenu();
                MenuItem notification_header = menu.findItem(99999);
                notification_header.setTitle("There are currently no notifications.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // Delete Updated Notification
    private void GetUpdateNotification(String id_to_delete){
        try {
            String path = getFilesDir() + "/sb_notifications.txt";
            FileReader fr=new FileReader(path);
            BufferedReader br=new BufferedReader(fr);
            String s;
            List<String> updated_line = new ArrayList<>();

            do{
                s = br.readLine();
                if(s != null){
                    if(s.length() > 0){
                        String[] values = s.split("\\*\\|\\*");
                        int i_inner = 1;
                        for(String str : values){
                            if(i_inner == 1){
                                if(str.equals(id_to_delete)){

                                    updated_line.add(s);
                                }
                            }

                            i_inner++;
                        }
                    }
                }
            }while(s!=null);

            FileReader fr_delete = new FileReader(path);
            BufferedReader br_delete = new BufferedReader(fr_delete);
            String s_delete;
            String totalStr = "";

            for(String line : updated_line){
                while ((s_delete = br_delete.readLine()) != null) {
                    if(s_delete.contains(line)) {
                        s_delete = "";
                    }

                    totalStr += s_delete + "\n";
                }

                FileWriter fw = new FileWriter(path);
                fw.write(totalStr);
                fw.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // Ellipsis
    public String getSafeSubstring(String s, int maxLength, String type){
        if(type == "title"){
            if(!TextUtils.isEmpty(s)){
                if(s.length() >= maxLength){
                    return s.substring(0, maxLength) + "...";
                } else{

                }
            }
        } else {
            if(!TextUtils.isEmpty(s)){
                if(s.length() >= maxLength){
                    return s.substring(0, maxLength) + "... view more";
                } else{

                }
            }
        }
        return s;
    }
    // Add Menu Header
    private void AddMenuHeader(){
        menu_notification.add(0, 99999, Menu.NONE, "There are currently no notifications.");
    }
    // Timer Notification
    private void TimerNotification(){
        final Timer timer = new Timer();
        //Set the schedule function
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if(new_entry){
                    Log.d("Handlers", "loaded");

                    notification_count = 1;
                    notification_clear = 1;
                    notifications_count = 0;

                    // Get Deleted Notification
                    GETDELETEDNOTIFICATION_V(new VolleyCallback(){
                        @Override
                        public void onSuccess(String result){
                            String replace_responce = StringEscapeUtils.unescapeJava(result);
                            Matcher m = Pattern.compile("\\[([^)]+)\\]").matcher(replace_responce);

                            while(m.find()){
                                get_deleted_id = m.group(1);
                            }

                            if(result.contains("OK")){
                                get_deleted_id = get_deleted_id.replace("\"", "");
                                List<String> get_delete_id_lists = new ArrayList<>(Arrays.asList(get_deleted_id.split(",")));
                                for(String get_delete_id_list : get_delete_id_lists){
                                    GetUpdateNotification(get_delete_id_list);
                                }
                            } else{
                                Toast.makeText(getApplicationContext(), "There is a problem with the server!", Toast.LENGTH_LONG).show();
                            }
                        }
                    });

                    // Get Notification
                    GETNOTIFICAITON_V(new VolleyCallback(){
                        @Override
                        public void onSuccess(String result){
                            try {
                                JSONObject obj = new JSONObject(result);
                                JSONArray array = obj.getJSONArray("data");

                                for(int i=0;i<array.length();i++){
                                    JSONObject data = array.getJSONObject(i);

                                    String id = data.getString("id");
                                    String message_date = data.getString("message_date");
                                    String message_title = data.getString("message_title");
                                    String message_content = data.getString("message_content");
                                    String status = data.getString("status");
                                    String message_type = data.getString("message_type");
                                    String edited_id = data.getString("edited_id");

                                    String  notification = id + "*|*" + message_date + "*|*" + message_title + "*|*" + message_content + "*|*" + status + "*|*" + message_type + "*|*" + edited_id + "*|*U\n";
                                    writeToFile(notification, "sb_notifications.txt");
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            // Preview Notification
                            UpdatedNotification();
                        }
                    });

                    new_entry = false;
                }
            }
        },0, 60000);
    }
    // Timer Notification Clear
    private void TimerNotificationClear(){
        final Timer timer = new Timer();
        //Set the schedule function
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        if(new_entry){
                            for(int l=0; l<=notification_clear; l++){
                                menu_notification.removeItem(120);
                            }
                        }
                    }
                });
            }
        },0, 59000);
    }
    // Timer Notification New Entry
    private void TimerNotificationNewEntry(){
        final Timer timer = new Timer();
        //Set the schedule function
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        // Get Deleted Notification
                        GETDELETEDNOTIFICATION_V(new VolleyCallback(){
                            @Override
                            public void onSuccess(String result){
                                String replace_responce = StringEscapeUtils.unescapeJava(result);
                                Matcher m = Pattern.compile("\\[([^)]+)\\]").matcher(replace_responce);

                                while(m.find()){
                                    get_deleted_id = m.group(1);
                                }

                                if(result.contains("OK")){
                                    Integer count = 0;
                                    get_deleted_id = get_deleted_id.replace("\"", "");
                                    List<String> get_delete_id_lists = new ArrayList<>(Arrays.asList(get_deleted_id.split(",")));
                                    for(String get_delete_id_list : get_delete_id_lists){
                                        count++;
                                    }

                                    if(detect_deleted_notification != count){
                                        Log.d("deleted", "deleted new entry");
                                        new_entry = true;
                                        detect_deleted_notification = count;
                                    } else{
                                        Log.d("deleted", "deleted no entry");
                                    }
                                } else{
                                    Toast.makeText(getApplicationContext(), "There is a problem with the server!", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                        // Get Notification
                        GETNOTIFICAITON_V(new VolleyCallback(){
                            @Override
                            public void onSuccess(String result){
                                try {
                                    JSONObject obj = new JSONObject(result);
                                    JSONArray array = obj.getJSONArray("data");

                                    for(int i=0;i<array.length();i++){
                                        Log.d("deleted", "add new entry");
                                        new_entry = true;
                                        JSONObject data = array.getJSONObject(i);
                                        String id = data.getString("id");
                                        String message_date = data.getString("message_date");
                                        String message_title = data.getString("message_title");
                                        String message_content = data.getString("message_content");
                                        String status = data.getString("status");
                                        String message_type = data.getString("message_type");
                                        String edited_id = data.getString("edited_id");

                                        String  notification = id + "*|*" + message_date + "*|*" + message_title + "*|*" + message_content + "*|*" + status + "*|*" + message_type + "*|*" + edited_id + "*|*U\n";
                                        writeToFile(notification, "sb_notifications.txt");
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                });
            }
        },0, 10000);
    }

    // --------------
    // End of Notification

    // Send Diagnostics
    private void SENDDIAGNOSTICS(){
//        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, "", new Response.Listener<NetworkResponse>() {
//            @Override
//            public void onResponse(NetworkResponse response) {
//                String resultResponse = new String(response.data);
//                // parse success output
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                error.printStackTrace();
//            }
//        }) {
//            @Override
//            protected Map<String, String> getParams() {
//                Map<String, String> params = new HashMap<>();
//                params.put("api_token", "gh659gjhvdyudo973823tt9gvjf7i6ric75r76");
//                params.put("name", "Angga");
//                params.put("location", "Indonesia");
//                params.put("about", "UI/UX Designer");
//                params.put("contact", "angga@email.com");
//                return params;
//            }
//
//            @Override
//            protected Map<String, DataPart> getByteData() {
//                Map<String, DataPart> params = new HashMap<>();
//                // file name could found file base or direct access from real path
//                // for now just get bitmap data from ImageView
//                params.put("avatar", new DataPart("file_avatar.jpg", AppHelper.getFileDataFromDrawable(getBaseContext(), mAvatarImage.getDrawable()), "image/jpeg"));
//                params.put("cover", new DataPart("file_cover.jpg", AppHelper.getFileDataFromDrawable(getBaseContext(), mCoverImage.getDrawable()), "image/jpeg"));
//
//                return params;
//            }
//        };
//
//        VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(multipartRequest);
    }

    // Back for WebBrowser --------------
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

    // For Navigation View --------------
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

    // Create Navigation Menu --------------
    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        final Menu m = menu;
        final MenuItem item1 = menu.findItem(R.id.item_notification);
        MenuItemCompat.setActionView(item1, R.layout.notification_update_count_layout);

        MenuItemCompat.getActionView(item1).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                drawer.openDrawer(GravityCompat.END);
            }
        });
        return true;
    }

    // Navigation View Selected --------------
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
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
        } else if (id == R.id.nav_exit) {
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

    // Navigation View Selected --------------
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        int id = item.getItemId();

        if (id == R.id.item_help) {
            if(isHelpAndSupportVisible){
                relativeLayout_helpandsupport.setVisibility(View.INVISIBLE);
                if(isLoadingFinished){
                    relativeLayout_webview.setVisibility(View.VISIBLE);
                }
                isHelpAndSupportVisible = false;
            } else{
                relativeLayout_webview.setVisibility(View.INVISIBLE);
                relativeLayout_helpandsupport.setVisibility(View.VISIBLE);
                relativeLayout_helpandsupport.bringToFront();
                isHelpAndSupportVisible = true;
            }
        } else if (id == R.id.item_notification) {
            drawer.openDrawer(GravityCompat.END);
        }

        return super.onOptionsItemSelected(item);
    }

    // Network Handler --------------
    private BroadcastReceiver mConnReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            NetworkInfo currentNetworkInfo = intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);

            if(currentNetworkInfo.isConnected()){
                isConnected = true;
                relativeLayout_connection.setVisibility(View.INVISIBLE);

                if(detect_no_internet_connection == 1){
                    relativeLayout_loader.setVisibility(View.VISIBLE);
                    webView.reload();
                }

                if(isFirstOpened){
                    // Functions --------------

                    AddMenuHeader();
                    TimerNotificationClear();
                    TimerNotification();
                    TimerNotificationNewEntry();

                    // Get API
                    GETAPI(text_to_search_service[0]);
                    // Get Public IP
                    GETPUBLICIP_V(new VolleyCallback(){
                        @Override
                        public void onSuccess(String result){
                            if(get_external_ip_address.equals("")){
                                get_external_ip_address = result;
                            }

                            GETIPINFO();
                        }
                    });
                    // Get Deleted Notification
                    GETDELETEDNOTIFICATION_V(new VolleyCallback(){
                        @Override
                        public void onSuccess(String result){
                            String replace_responce = StringEscapeUtils.unescapeJava(result);
                            Matcher m = Pattern.compile("\\[([^)]+)\\]").matcher(replace_responce);

                            while(m.find()){
                                get_deleted_id = m.group(1);
                            }

                            if(result.contains("OK")){
                                get_deleted_id = get_deleted_id.replace("\"", "");
                                List<String> get_delete_id_lists = new ArrayList<>(Arrays.asList(get_deleted_id.split(",")));
                                for(String get_delete_id_list : get_delete_id_lists){
                                    detect_deleted_notification++;
                                    GetUpdateNotification(get_delete_id_list);
                                }
                            } else{
                                Toast.makeText(getApplicationContext(), "There is a problem with the server!", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                    // Get Notification
                    GETNOTIFICAITON_V(new VolleyCallback(){
                        @Override
                        public void onSuccess(String result){
                            try {
                                JSONObject obj = new JSONObject(result);
                                JSONArray array = obj.getJSONArray("data");

                                for(int i=0;i<array.length();i++){
                                    JSONObject data = array.getJSONObject(i);
                                    String id = data.getString("id");
                                    String message_date = data.getString("message_date");
                                    String message_title = data.getString("message_title");
                                    String message_content = data.getString("message_content");
                                    String status = data.getString("status");
                                    String message_type = data.getString("message_type");
                                    String edited_id = data.getString("edited_id");

                                    String  notification = id + "*|*" + message_date + "*|*" + message_title + "*|*" + message_content + "*|*" + status + "*|*" + message_type + "*|*" + edited_id + "*|*U\n";
                                    writeToFile(notification, "sb_notifications.txt");
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            // Preview Notification
                            UpdatedNotification();
                        }
                    });

                    isFirstOpened = false;

                    // End of Functions --------------
                }


            }else{
                isConnected = false;
                relativeLayout_webview.setVisibility(View.INVISIBLE);
                detect_no_internet_connection = 1;
                relativeLayout_loader.setVisibility(View.INVISIBLE);
                relativeLayout_connection.setVisibility(View.VISIBLE);
            }
        }
    };
}