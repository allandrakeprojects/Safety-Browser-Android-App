package com.drake.safetybrowser;

import android.util.Log;
import android.widget.Toast;

import com.qiniu.android.netdiag.Output;

import java.io.FileOutputStream;

public class PingLogger extends MainActivity implements Output {
    @Override
    public void write(String result) {
        Log.d("deleted", "Ping: " + result);
        writeToFile(result, "ping.txt");
    }

    private void writeToFile(String data, String file_name){
        FileOutputStream fos = null;

        try {
            fos = openFileOutput(file_name, MODE_APPEND);
            fos.write(data.getBytes());
        } catch (Exception e) {
            Log.d("deleted", e.getMessage());
        }
    }
}
