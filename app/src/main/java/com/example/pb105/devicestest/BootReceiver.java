package com.example.pb105.devicestest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by weiguanghua on 18-1-24.
 */

public class BootReceiver extends BroadcastReceiver {
    private static final String action = "android.intent.action.BOOT_COMPLETED";
    private SharedPreferences pref;
    private Process process = null;
    private DataOutputStream outputStream = null;
    @Override
    public void onReceive(Context context, Intent intent) {
        pref = context.getSharedPreferences("Myshare", Context.MODE_PRIVATE);
        boolean flag = pref.getBoolean("flag",false);
        String cmd = pref.getString("cmd","");
        if(intent.getAction().equals(action)){
            Toast.makeText(context,"接收到开机广播"+flag,Toast.LENGTH_SHORT).show();
            if(flag){
                do_exec(cmd);
                Toast.makeText(context,"修改命令成功",Toast.LENGTH_SHORT).show();
            }
        }
    }

    public String do_exec(String cmd){
        try {
            process = Runtime.getRuntime().exec("sh");
            outputStream = new DataOutputStream(process.getOutputStream());
            outputStream.writeBytes("su\n");
            outputStream.writeBytes(cmd+"\n");
            outputStream.writeBytes("exit\n");
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cmd;
    }
}
