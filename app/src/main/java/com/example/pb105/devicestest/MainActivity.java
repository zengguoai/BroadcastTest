package com.example.pb105.devicestest;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {
    private EditText prev_edit;
    private EditText final_edit;
    private Button send_btn;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private TextView cmdTest;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pref = this.getSharedPreferences("Myshare", Context.MODE_PRIVATE);
        prev_edit = (EditText) findViewById(R.id.prev_name);
        final_edit = (EditText) findViewById(R.id.final_name);
        send_btn =(Button) findViewById(R.id.send_cmd);
        cmdTest = (TextView)findViewById(R.id.cmd_text);
        boolean flag = pref.getBoolean("flag",false);

        if(flag){
            String previous = pref.getString("previous","");
            String after = pref.getString("after","");
            String cmd = pref.getString("cmd","");
            prev_edit.setText(previous);
            final_edit.setText(after);
            cmdTest.setText(cmd);
        }

        send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String previous_name = prev_edit.getText().toString();
                String after_name = final_edit.getText().toString();
                String cmdTestview = "mv "+previous_name+" "+after_name;
                editor =pref.edit();
                if(previous_name!=null && after_name!=null){
                    editor.putString("previous",previous_name);
                    editor.putString("after",after_name);
                    editor.putBoolean("flag",true);
                    editor.putString("cmd",cmdTestview);
                    cmdTest.setText(cmdTestview);
                    Toast.makeText(MainActivity.this,"发送命令成功，请重启设备",Toast.LENGTH_SHORT).show();
                }else {
                    editor.clear();
                }
                editor.apply();

            }
        });

    }



}
