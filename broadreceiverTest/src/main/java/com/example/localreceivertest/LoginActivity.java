package com.example.localreceivertest;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends BaseActivity {
    private EditText accountEdit;
    private EditText passwordEdit;
    private Button loginButton;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private CheckBox rememberCheckBox;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        accountEdit = findViewById(R.id.acount);
        passwordEdit = findViewById(R.id.password);
        loginButton = findViewById(R.id.login);
        rememberCheckBox = findViewById(R.id.remember_pass);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);//获取sharedPreferences的实例
        boolean isremember = sharedPreferences.getBoolean("remember_pass",false);//检测是否钩选记住密码
        if(isremember){//如果检测到记住密码，则从sharedPreferences获得帐号密码，并填入，钩选记住密码选项
            String account = sharedPreferences.getString("account","");
            String password = sharedPreferences.getString("password","");
            accountEdit.setText(account);
            passwordEdit.setText(password);
            rememberCheckBox.setChecked(true);
        }
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String account = accountEdit.getText().toString();
                String password = passwordEdit.getText().toString();
                if(account.equals("admin")&&password.equals("123456")){
                    editor = sharedPreferences.edit(); //获取editor对象
                    if(rememberCheckBox.isChecked()){//判断是否钩选记住密码，如果是就将相关信息保存起来，如果不是，就清除editor对象
                        editor.putBoolean("remember_pass",true);
                        editor.putString("account",account);
                        editor.putString("password",password);
                    }else {
                        editor.clear();
                    }
                    editor.apply();//提交修改
                    Intent intent = new Intent(LoginActivity.this,DisplayActivity.class);
                    startActivity(intent);
                    finish();
                }else {
                    Toast.makeText(LoginActivity.this,"account or password is invalid",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
