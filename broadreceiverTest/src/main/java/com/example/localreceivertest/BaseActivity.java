package com.example.localreceivertest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by weiguanghua on 18-1-18.
 */

public class BaseActivity extends AppCompatActivity {
    private ForceOfflineReceiver receiver;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCollector.addActivity(this);
    }

    @Override
    protected void onResume() {//需要保证只有处于栈顶的活动才能接收到强制下线的广播，非栈顶的活动不应该也没有必要去接受这条广播
        super.onResume();
        IntentFilter intentFilter= new IntentFilter();
        intentFilter.addAction("com.weibu.FORCE_OFFLINE");
        receiver = new ForceOfflineReceiver();
        registerReceiver(receiver,intentFilter);//注册广播
    }

    class ForceOfflineReceiver extends BroadcastReceiver{//广播接受放在baseActivity，是因为所有的活动都是继承它。
        @Override
        public void onReceive(final Context context, final Intent intent) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Warning");
            builder.setMessage("You are forced to be offline.Please try to login again.");
            builder.setCancelable(false);//设置不能取消，只能按确定
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                     ActivityCollector.finishAll();//销毁所有的活动
                    Intent intent = new Intent(context,LoginActivity.class);
                    context.startActivity(intent);//进入登录界面
                }
            });
            builder.show();
        }
    }

    @Override
    protected void onPause() {//需要保证只有处于栈顶的活动才能接收到强制下线的广播，非栈顶的活动不应该也没有必要去接受这条广播
        super.onPause();
        if(receiver!=null){//判断广播非空，取消注册广播
            unregisterReceiver(receiver);
            receiver = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);

    }
}
