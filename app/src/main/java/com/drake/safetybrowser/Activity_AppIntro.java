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
            addSlide(AppIntroFragment.newInstance("Navigation Menu",
                    "Click the Menu icon or just easily swipe right to the edge of screen.",
                    R.drawable.ic_screenshot_1,
                    Color.parseColor("#EB6306")));

            addSlide(AppIntroFragment.newInstance("Notification Menu",
                    "Click the Notification icon or just easily swipe left to the edge of screen.",
                    R.drawable.ic_screenshot_2,
                    Color.parseColor("#EB6306")));

            addSlide(AppIntroFragment.newInstance("Help and Support",
                    "Click the Question Mark icon to show Help and Support.",
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