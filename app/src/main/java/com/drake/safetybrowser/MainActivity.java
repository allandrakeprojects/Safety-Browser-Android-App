package com.drake.safetybrowser;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.apache.commons.lang3.StringEscapeUtils;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private WebView webView;
    boolean loadingFinished = true;
    boolean redirect = false;
    final Context context = this;
    boolean isConnected;
    GifImageView gifImageView_loader;
    RelativeLayout relativeLayout_connection;
    GifImageView gifImageView_connection;
    int detect_no_internet_connection = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        this.registerReceiver(this.mConnReceiver,
                new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        final DrawerLayout drawer = findViewById(R.id.drawer_layout);
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

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        gifImageView_loader = findViewById(R.id.gifImageView_loader);
        gifImageView_loader.setGifImageResource(R.drawable.ic_loader);
        relativeLayout_connection = findViewById(R.id.relativeLayout_connection);
        gifImageView_connection = findViewById(R.id.gifImageView_connection);
        gifImageView_connection.setGifImageResource(R.drawable.ic_connection);

        // Load url
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
        webView.loadUrl("http://yb188.com");
        webView.requestFocus();
        webView.setWebViewClient(new MyBrowser());

        GETAPI();
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
                gifImageView_loader.setVisibility(View.INVISIBLE);
                if (isConnected) {
                    webView.setVisibility(View.VISIBLE);
                }
            } else {
                redirect = false;
            }

        }
    }

    // Get API --------------
    private void GETAPI() {
        RequestQueue MyRequestQueue = Volley.newRequestQueue(this);

        String url = "http://ssicortex.com/getTxt2Search";
        StringRequest MyStringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //This code is executed if the server responds, whether or not the response contains data.
                //The String 'response' contains the server's response.
                Toast.makeText(getApplicationContext(), StringEscapeUtils.unescapeJava(response), Toast.LENGTH_LONG).show();
            }
        }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
            @Override
            public void onErrorResponse(VolleyError error) {
                //This code is executed if there is an error.
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> MyData = new HashMap<>();
                MyData.put("api_key", "6b8c7e5617414bf2d4ace37600b6ab71"); //Add the data you'd like to send to the server.
                MyData.put("brand_code", "YB");
                return MyData;
            }
        };

        MyRequestQueue.add(MyStringRequest);
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
            webView.loadUrl("http://yb188.com");
        } else if (id == R.id.nav_reload) {
            webView.reload();
        } else if (id == R.id.nav_hard_reload) {
            webView.getSettings().setAppCacheEnabled(false);
            webView.clearCache(true);
            webView.loadUrl("about:blank");
            webView.reload();
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
