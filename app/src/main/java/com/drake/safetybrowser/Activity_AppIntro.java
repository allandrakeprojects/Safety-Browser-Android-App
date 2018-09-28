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
            addSlide(AppIntroFragment.newInstance("导航目录",
                    "点击目录图标 或 银幕上往右滑.",
                    R.drawable.ic_screenshot_1,
                    Color.parseColor("#EB6306")));

            addSlide(AppIntroFragment.newInstance("通知目录",
                    "点击通知图标 或 银幕上往左滑.",
                    R.drawable.ic_screenshot_2,
                    Color.parseColor("#EB6306")));

            addSlide(AppIntroFragment.newInstance("帮助",
                    "点击问号图标显示帮助.",
                    R.drawable.ic_screenshot_3,
                    Color.parseColor("#EB6306")));

            showStatusBar(false);
            setBarColor(Color.parseColor("#D35600"));
            setSeparatorColor(Color.parseColor("#D35600"));
            setDoneText("完成");
            setSkipText("跳过");
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