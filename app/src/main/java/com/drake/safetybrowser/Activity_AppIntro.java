package com.drake.safetybrowser;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.Toast;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;

public class Activity_AppIntro extends com.github.paolorotolo.appintro.AppIntro {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try{
            addSlide(AppIntroFragment.newInstance("導航菜單",
                    "單擊“菜單”圖標或輕鬆向右滑動到屏幕邊緣.",
                    R.drawable.ic_screenshot_1,
                    Color.parseColor("#EB6306")));

            addSlide(AppIntroFragment.newInstance("通知菜單",
                    "單擊通知圖標或只需輕鬆向左滑動到屏幕邊緣.",
                    R.drawable.ic_screenshot_2,
                    Color.parseColor("#EB6306")));

            addSlide(AppIntroFragment.newInstance("幫助和支持",
                    "單擊“問號”圖標以顯示“幫助和支持.",
                    R.drawable.ic_screenshot_3,
                    Color.parseColor("#EB6306")));

            showStatusBar(false);
            setBarColor(Color.parseColor("#D35600"));
            setSeparatorColor(Color.parseColor("#D35600"));
        } catch(Exception e){
            Log.d("deleted", e.getMessage());
        }
    }

    @Override
    public void onDonePressed(){
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    @Override
    public void onSkipPressed(Fragment currentFragment){
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

//    @Override
//    public void onSlideChanged(){
//        Toast.makeText(getApplicationContext(), "Slide", Toast.LENGTH_LONG).show();
//    }
}