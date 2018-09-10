package com.drake.safetybrowser;

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
import android.support.v4.widget.DrawerLayout;
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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;
import com.android.volley.request.StringRequest;
import com.android.volley.toolbox.Volley;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
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
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

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
    private WebView webView;
    ArrayList<String> domain_list = new ArrayList<>();
    List<String> get_id = new ArrayList<>();
    List<String> get_id_delete = new ArrayList<>();
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
                readToFile("sb_notifications.txt");
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
//                    Log.d("*************", "" + output);
                    Toast.makeText(getApplicationContext(), output, Toast.LENGTH_SHORT).show();
//                    writeToFile(output + "", "sb_ping.txt");

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
//        GETNOTIFICATIONS(notifications_service[0]);
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

//        NotificationTimer();
        isRunning = true;
        isOpened = true;
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

        getString_deletedid(new VolleyCallback(){
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
//                                String[] get_delete_id_lists = get_deleted_id.split(",");
                    for(String get_delete_id_list : get_delete_id_lists){
                        if(get_delete_id_list != ""){
//                            get_id.add(get_delete_id_list);
                            try {
                                final File file = new File(getFilesDir() + "/sb_notifications.txt");

                                if (file.exists()) {
                                    String path = getFilesDir() + "/sb_notifications.txt";
                                    FileReader fr=new FileReader(path);
                                    BufferedReader br=new BufferedReader(fr);
                                    String s;

                                    int count_line = 0;
                                    List<String> tmp_asd = new ArrayList<>();
                                    do{
                                        count_line++;
                                        s = br.readLine();
                                        tmp_asd.add(s);
                                    }while(s!=null);

                                    for(int i=count_line-1;i>=0;i--) {
                                        if(tmp_asd.get(i) != null){
                                            String line = tmp_asd.get(i);
                                            String id = "";

                                            String[] values = line.split("\\*\\|\\*");

                                            int i_inner = 1;
                                            for(String str : values){
                                                if(i_inner == 1){
                                                    id = str;
                                                    if(id.contains(get_delete_id_list)){

                                                        FileReader fr_delete = new FileReader(path);
                                                        String s_delete;
                                                        String totalStr = "";
                                                        BufferedReader br_delete = new BufferedReader(fr_delete);


                                                        while ((s_delete = br_delete.readLine()) != null) {
                                                            if(s_delete.contains(line)) {
                                                                Log.d("Testshow", s_delete);
                                                                s_delete = s_delete.substring(0, s_delete.length() - 1) + "X";
                                                            }

                                                            totalStr += s_delete + "\n";
                                                        }

                                                        FileWriter fw = new FileWriter(path);
                                                        fw.write(totalStr);
                                                        fw.close();

                                                    }
                                                }

                                                i_inner++;
                                            }
                                        }
                                    }

                                } else {
                                    NavigationView navView = findViewById(R.id.nav_view_notification);
                                    Menu menu = navView.getMenu();
                                    MenuItem notification_header = menu.findItem(R.id.notification_header);
                                    notification_header.setTitle("There are currently no notifications.");
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }


                            // Delete
                            String path = getFilesDir() + "/sb_notifications.txt";
                            try {
                                try {


                                } catch (Exception e) {
                                    Log.d("Testshow", e.getMessage());
                                }
                            } catch (Exception e) {
                                Log.d("Testshow", e.getMessage());
                            }
                        }
                    }

                } else{
                    Toast.makeText(getApplicationContext(), "There is a problem with the server!", Toast.LENGTH_LONG).show();
                }
            }
        });

        getString_notification(new VolleyCallback(){
            @Override
            public void onSuccess(String result){
                try {
                    JSONObject obj = new JSONObject(result);
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

                        String  notification = id + "*|*" + message_date + "*|*" + message_title + "*|*" + message_content + "*|*" + status + "*|*" + message_type + "*|*" + edited_id + "*|*U\n";
                        writeToFile(notification, "sb_notifications.txt");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // Preview Notification
                UpdateNotifications_EditedId();
                PreviewNotifications();
            }
        });

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

    private void PreviewNotifications(){
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
                    count_line++;
                    s = br.readLine();
                    tmp.add(s);
                }while(s!=null);

                int notification_count = 0;
                for(int i=count_line-1;i>=0;i--) {
                    if(tmp.get(i) != null){
                        String line = tmp.get(i);
                        NavigationView navView = findViewById(R.id.nav_view_notification);
                        String id = "";
                        String message_date = "";
                        String message_title = "";
                        String edited_id = "";
                        String message_content = "";

                        String[] values = line.split("\\*\\|\\*");

                        int i_inner = 1;
                        for(String str : values){
                            if(i_inner == 1){
                                id = str;
                            } else if(i_inner == 2){
                                message_date = str;
                            }else if(i_inner == 3){
                                message_title = str;
                            }else if(i_inner == 4){
                                String lineSep = System.getProperty("line.separator");
                                message_content = str;
                                message_content = message_content.replace("&lt;", "<");
                                message_content = message_content.replace("&gt;", ">");
                                message_content = message_content.replace("<br />", lineSep);
                            }else if(i_inner == 7){
                                if(!str.contains("null")){
                                    edited_id = str;
//                                    Log.d("Test", edited_id);
                                }
                            }else if(i_inner == 8){
                                if(str.contains("U")){
                                    isUnread = true;
                                    notifications_count++;
                                    Menu menu = navView.getMenu();
                                    MenuItem notification_header = menu.findItem(R.id.notification_header);
                                    notification_header.setTitle("Notifications (" + notifications_count + ")");
                                } else if(str.contains("X")){
                                    isHide = true;
                                }
                                else {
                                    Menu menu = navView.getMenu();
                                    MenuItem notification_header = menu.findItem(R.id.notification_header);
                                    if(notifications_count == 0){
                                        notification_header.setTitle("Notifications");
                                    }
                                }
                            }

                            i_inner++;
                        }

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
                        else if(days>30)
                        {
                            long months = days / 30;
                            if(months == 1){
                                final_datetime = months+" month ago";
                            } else{
                                final_datetime = months+" months ago";
                            }
                        }
                        else
                        {
                            long years = days / 365;
                            if(years == 1){
                                final_datetime = years+" year ago";
                            } else{
                                final_datetime = years+" years ago";
                            }
                        }

                        // asd123
                        if(!isHide){
                            if(isUnread){
                                menu.add(notification_count, 120, Menu.NONE, getSafeSubstring( "⍣ " + message_title, 18, "title") + " (" + final_datetime + ")");
                                menu.add(notification_count, 120, Menu.NONE, getSafeSubstring(message_content, 20, "content"));
                                isUnread = false;
                                isInsertMenu = true;
                            } else {
                                menu.add(notification_count, 120, Menu.NONE, getSafeSubstring(message_title, 18, "title") + " (" + final_datetime + ")");
                                menu.add(notification_count, 120, Menu.NONE, getSafeSubstring(message_content, 20, "content"));
                                isUnread = false;
                                isInsertMenu = true;
                            }
                        } else {
                            isHide = false;
                        }

                        if(!isInsertMenu){
                            MenuItem notification_header = menu.findItem(R.id.notification_header);
                            notification_header.setTitle("There are currently no notifications.");
                            isInsertMenu = false;
                        }

                        displayRightNavigation();
                    }

                    notification_count++;
                }
            } else {
                NavigationView navView = findViewById(R.id.nav_view_notification);
                Menu menu = navView.getMenu();
                MenuItem notification_header = menu.findItem(R.id.notification_header);
                notification_header.setTitle("There are currently no notifications.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void UpdateID(String edited_id){
        try {
            final File file = new File(getFilesDir() + "/sb_notifications.txt");

            if (file.exists()) {
                String path = getFilesDir() + "/sb_notifications.txt";
                FileReader fr=new FileReader(path);
                BufferedReader br=new BufferedReader(fr);
                String s;

                int count_line = 0;
                List<String> tmp = new ArrayList<>();
                do{
                    count_line++;
                    s = br.readLine();
                    tmp.add(s);
                }while(s!=null);

                for(int i=count_line-1;i>=0;i--) {
                    if(tmp.get(i) != null){
                        String line = tmp.get(i);
                        String id = "";

                        String[] values = line.split("\\*\\|\\*");

                        int i_inner = 1;
                        for(String str : values){
                            if(i_inner == 1){
                                id = str;
                                if(id.contains(edited_id)){
                                    get_id.add(line);
                                }
                            }

                            i_inner++;
                        }
                    }
                }
            } else {
                NavigationView navView = findViewById(R.id.nav_view_notification);
                Menu menu = navView.getMenu();
                MenuItem notification_header = menu.findItem(R.id.notification_header);
                notification_header.setTitle("There are currently no notifications.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void UpdateNotifications_EditedId(){
        try {
            final File file = new File(getFilesDir() + "/sb_notifications.txt");

            if (file.exists()) {
                String path = getFilesDir() + "/sb_notifications.txt";
                FileReader fr=new FileReader(path);
                BufferedReader br=new BufferedReader(fr);
                String s;

                int count_line = 0;
                List<String> tmp = new ArrayList<>();
                do{
                    count_line++;
                    s = br.readLine();
                    tmp.add(s);
                }while(s!=null);

                for(int i=count_line-1;i>=0;i--) {
                    if(tmp.get(i) != null){
                        String line = tmp.get(i);
                        String edited_id = "";

                        String[] values = line.split("\\*\\|\\*");

                        int i_inner = 1;
                        for(String str : values){
                            if(i_inner == 7){
                                if(!str.contains("null")){
                                    List<String> get_delete_id_lists = new ArrayList<>(Arrays.asList(get_deleted_id.split(",")));
//                                String[] get_delete_id_lists = get_deleted_id.split(",");
                                    for(String get_delete_id_list : get_delete_id_lists){
                                        if(get_delete_id_list != ""){
                                            if(!edited_id.contains(get_delete_id_list)){
                                                edited_id = str;
                                                UpdateID(edited_id);
                                            }
                                        }
                                    }
                                }
                            }

                            i_inner++;
                        }
                    }
                }

            } else {
                NavigationView navView = findViewById(R.id.nav_view_notification);
                Menu menu = navView.getMenu();
                MenuItem notification_header = menu.findItem(R.id.notification_header);
                notification_header.setTitle("There are currently no notifications.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Delete
        String path = getFilesDir() + "/sb_notifications.txt";
        try {
            FileReader fr = new FileReader(path);
            String s;
            String totalStr = "";
            try {
                BufferedReader br = new BufferedReader(fr);

                for(String ids : get_id){
                    while ((s = br.readLine()) != null) {
                        if(s.contains(ids)) {
                            s = s.substring(0, s.length() - 1) + "X";
                        }


                        totalStr += s + "\n";
                    }

                    FileWriter fw = new FileWriter(path);
                    fw.write(totalStr);
                    fw.close();
                }

            } catch (Exception e) {
                System.out.println("Problem reading file.");
            }
        } catch (Exception e) {
            System.out.println("Problem reading file.");
        }

    }
//
//    private void NotificationTimer() {
//        Runnable helloRunnable = new Runnable() {
//            public void run() {
//
//                if(!isOpened){
//                    if(isRunning){
//                        Log.d("Timer", "gotyaaaaaaaaaaa");
//
//                        getString_deletedid(new VolleyCallback(){
//                            @Override
//                            public void onSuccess(String result){
//
//                                String replace_responce = StringEscapeUtils.unescapeJava(result);
//                                Matcher m = Pattern.compile("\\[([^)]+)\\]").matcher(replace_responce);
//
//                                while(m.find()){
//                                    get_deleted_id = m.group(1);
//                                }
//
//                                if(result.contains("OK")){
//                                    get_deleted_id = get_deleted_id.replace("\"", "");
//                                    List<String> get_delete_id_lists = new ArrayList<>(Arrays.asList(get_deleted_id.split(",")));
//    //                                String[] get_delete_id_lists = get_deleted_id.split(",");
//                                    for(String get_delete_id_list : get_delete_id_lists){
//                                        if(get_delete_id_list != ""){
//    //                            get_id.add(get_delete_id_list);
//                                            try {
//                                                final File file = new File(getFilesDir() + "/sb_notifications.txt");
//
//                                                if (file.exists()) {
//                                                    String path = getFilesDir() + "/sb_notifications.txt";
//                                                    FileReader fr=new FileReader(path);
//                                                    BufferedReader br=new BufferedReader(fr);
//                                                    String s;
//
//                                                    int count_line = 0;
//                                                    List<String> tmp_asd = new ArrayList<>();
//                                                    do{
//                                                        count_line++;
//                                                        s = br.readLine();
//                                                        tmp_asd.add(s);
//                                                    }while(s!=null);
//
//                                                    for(int i=count_line-1;i>=0;i--) {
//                                                        if(tmp_asd.get(i) != null){
//                                                            String line = tmp_asd.get(i);
//                                                            String id = "";
//
//                                                            String[] values = line.split("\\*\\|\\*");
//
//                                                            int i_inner = 1;
//                                                            for(String str : values){
//                                                                if(i_inner == 1){
//                                                                    id = str;
//                                                                    if(id.contains(get_delete_id_list)){
//
//                                                                        FileReader fr_delete = new FileReader(path);
//                                                                        String s_delete;
//                                                                        String totalStr = "";
//                                                                        BufferedReader br_delete = new BufferedReader(fr_delete);
//
//
//                                                                        while ((s_delete = br_delete.readLine()) != null) {
//                                                                            if(s_delete.contains(line)) {
//                                                                                Log.d("Testshow", s_delete);
//                                                                                s_delete = s_delete.substring(0, s_delete.length() - 1) + "X";
//                                                                            }
//
//                                                                            totalStr += s_delete + "\n";
//                                                                        }
//
//                                                                        FileWriter fw = new FileWriter(path);
//                                                                        fw.write(totalStr);
//                                                                        fw.close();
//
//                                                                    }
//                                                                }
//
//                                                                i_inner++;
//                                                            }
//                                                        }
//                                                    }
//
//                                                } else {
//                                                    NavigationView navView = findViewById(R.id.nav_view_notification);
//                                                    Menu menu = navView.getMenu();
//                                                    MenuItem notification_header = menu.findItem(R.id.notification_header);
//                                                    notification_header.setTitle("There are currently no notifications.");
//                                                }
//                                            } catch (Exception e) {
//                                                e.printStackTrace();
//                                            }
//
//
//                                            // Delete
//                                            String path = getFilesDir() + "/sb_notifications.txt";
//                                            try {
//                                                try {
//
//
//                                                } catch (Exception e) {
//                                                    Log.d("Testshow", e.getMessage());
//                                                }
//                                            } catch (Exception e) {
//                                                Log.d("Testshow", e.getMessage());
//                                            }
//                                        }
//                                    }
//
//                                } else{
//                                    Toast.makeText(getApplicationContext(), "There is a problem with the server!", Toast.LENGTH_LONG).show();
//                                }
//                            }
//                        });
//
//                        getString_notification(new VolleyCallback(){
//                            @Override
//                            public void onSuccess(String result){
//                                try {
//                                    JSONObject obj = new JSONObject(result);
//                                    JSONArray array = obj.getJSONArray("data");
//
//                                    for(int i=0;i<array.length();i++){
//                                        JSONObject student = array.getJSONObject(i);
//
//                                        String id = student.getString("id");
//                                        String message_date = student.getString("message_date");
//                                        String message_title = student.getString("message_title");
//                                        String message_content = student.getString("message_content");
//                                        String status = student.getString("status");
//                                        String message_type = student.getString("message_type");
//                                        String edited_id = student.getString("edited_id");
//
//                                        String  notification = id + "*|*" + message_date + "*|*" + message_title + "*|*" + message_content + "*|*" + status + "*|*" + message_type + "*|*" + edited_id + "*|*U\n";
//                                        writeToFile(notification, "sb_notifications.txt");
//                                    }
//                                } catch (JSONException e) {
//                                    e.printStackTrace();
//                                }
//
//                                // Preview Notification
//                                UpdateNotifications_EditedId();
//                                PreviewNotifications();
//                            }
//                        });
//                    }
//                } else {
//                    isOpened = false;
//                }
//            }
//        };
//
//        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
//        executor.scheduleAtFixedRate(helloRunnable, 0, 15, TimeUnit.SECONDS);
//
//
//
//
//
//
//
//
//
//
//
//
////        int delay = 1000; // delay for 1 sec.
////        int period = 15000; // repeat every 10 sec.
////        final Timer timer = new Timer();
////        timer.scheduleAtFixedRate(new TimerTask()
////        {
////            public void run()
////            {
////                if(isRunning){
////                    Log.d("Timer", "gotyaaaa");
////
////                }
////            }
////        }, delay, period);
//    }


























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

    // Get Notifications
    public void getString_notification(final VolleyCallback callback) {
        RequestQueue MyRequestQueue = Volley.newRequestQueue(this);
        StringRequest MyStringRequest = new StringRequest(Request.Method.POST, notifications_service[0], new Response.Listener<String>() {
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
                MyData.put("macid", GETMACADDRESS());
                MyData.put("brand_code", BRAND_CODE);
                MyData.put("api_key", API_KEY);
                return MyData;
            }
        };

        MyRequestQueue.add(MyStringRequest);
    }

    public void getString_deletedid(final VolleyCallback callback) {
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

    private void writeToFile(String data, String file_name){
        FileOutputStream fos = null;

        try {
            fos = openFileOutput(file_name, MODE_APPEND);
            fos.write(data.getBytes());

//            Toast.makeText(getApplicationContext(), "Saved to " + getFilesDir() + "/" + file_name, Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readToFile(String file_name) {
        FileInputStream fis = null;

        try {
            fis = openFileInput(file_name);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String text;

            while ((text = br.readLine()) != null) {
                sb.append(text).append("\n");
            }

            Toast.makeText(getApplicationContext(), sb.toString(), Toast.LENGTH_SHORT).show();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
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
        }

        return super.onOptionsItemSelected(item);
    }

    private void displayRightNavigation(){
        final NavigationView navigationViewRight = findViewById(R.id.nav_view_notification);
        navigationViewRight.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // Handle navigation view item clicks here.
                int id = item.getItemId();
                Integer get_group_id = item.getGroupId();

                // asd123
                if (id == 120) {

                    try {
                        String group_id = String.valueOf(item.getGroupId());
                        String id_get = String.valueOf(item.getItemId());


                        String path = getFilesDir() + "/sb_notifications.txt";
                        FileReader fr=new FileReader(path);
                        BufferedReader br=new BufferedReader(fr);
                        String s;

                        int count_line = 0;
                        int count_notification = 0;
                        List<String> tmp = new ArrayList<>();
                        do{
                            count_line++;
                            s = br.readLine();
                            tmp.add(s);
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
                                    else if(days>30)
                                    {
                                        long months = days / 30;
                                        if(months == 1){
                                            final_datetime = months+" month ago";
                                        } else{
                                            final_datetime = months+" months ago";
                                        }
                                    }
                                    else
                                    {
                                        long years = days / 365;
                                        if(years == 1){
                                            final_datetime = years+" year ago";
                                        } else{
                                            final_datetime = years+" years ago";
                                        }
                                    }


                                    UpdateNotifications("⍣ " + message_title + " (" + final_datetime + ")", message_title, get_group_id, final_datetime);


                                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                                            context);
                                    alertDialogBuilder.setMessage(message_content);
                                    alertDialogBuilder.setTitle(message_title + " (" + final_datetime + ")");
                                    alertDialogBuilder
                                            .setCancelable(false)
                                            .setPositiveButton("OK",new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog,int id) {
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

    private void UpdateNotifications(String title, String without_replace, Integer group_id, String date){
        //asd123
        Integer get_final_id = (group_id*2)-1;
        String path = getFilesDir() + "/sb_notifications.txt";
        try {
            FileReader fr = new FileReader(path);
            String s;
            String totalStr = "";
            try {
                BufferedReader br = new BufferedReader(fr);
                NavigationView navView_delete = findViewById(R.id.nav_view_notification);
                Menu menu_delete = navView_delete.getMenu();

                if(title.contains("⍣")){
                    while ((s = br.readLine()) != null) {
                        if(s.contains(without_replace)) {
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

                    notifications_count--;
                    if(0 < notifications_count){
                        NavigationView navView = findViewById(R.id.nav_view_notification);
                        Menu menu = navView.getMenu();
                        MenuItem notification_header = menu.findItem(R.id.notification_header);
                        notification_header.setTitle("Notifications (" + notifications_count + ")");
                    } else{
                        NavigationView navView = findViewById(R.id.nav_view_notification);
                        Menu menu = navView.getMenu();
                        MenuItem notification_header = menu.findItem(R.id.notification_header);
                        notification_header.setTitle("Notifications");
                    }
                }
            } catch (Exception e) {
                System.out.println("Problem reading file.");
            }
        } catch (Exception e) {
            System.out.println("Problem reading file.");
        }
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
