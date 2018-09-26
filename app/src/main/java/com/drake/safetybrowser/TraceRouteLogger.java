package com.drake.safetybrowser;

import android.content.Context;
import android.util.Log;

import com.qiniu.android.netdiag.Output;

import java.io.FileOutputStream;

import ir.mahdi.mzip.zip.ZipArchive;

class TraceRouteLogger implements Output {
    Context context;

    @Override
    public void write(String result) {
//        writeToFile("\r\n" + result + "\r\n", "traceroute.txt");
    }

//    private void writeToFile(String data, String file_name){
//        FileOutputStream fos = null;
//
//        try {
//            fos = context.openFileOutput(file_name, Context.MODE_APPEND);
//            fos.write(data.getBytes());
//        } catch (Exception e) {
//            Log.d("deleted", e.getMessage());
//        }
//    }
}
