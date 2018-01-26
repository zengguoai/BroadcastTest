package com.example.localreceivertest;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
    private Button registerButton;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private CheckBox rememberCheckBox;
    private MyOpenHelper myOpenHelper;
    private SQLiteDatabase database;
    private String userName1;
    private String userPassword1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        accountEdit = findViewById(R.id.acount);
        passwordEdit = findViewById(R.id.password);
        loginButton = findViewById(R.id.login);
        registerButton =findViewById(R.id.register_btn);
        rememberCheckBox = findViewById(R.id.remember_pass);
        myOpenHelper = new MyOpenHelper(this,"myUser.db",null,1);
        database = myOpenHelper.getWritableDatabase();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);//获取sharedPreferences的实例
        boolean isremember = sharedPreferences.getBoolean("remember_pass",false);//检测是否钩选记住密码
        if(isremember){//如果检测到记住密码，则从数据库中获得帐号密码，并填入，钩选记住密码选项
            accountEdit.setText(readDatabseName());
            passwordEdit.setText(readDatabsePassword());
            rememberCheckBox.setChecked(true);
        }

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerIntent = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(registerIntent);
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String account = accountEdit.getText().toString();
                String password = passwordEdit.getText().toString();
                editor = sharedPreferences.edit();
                if(rememberCheckBox.isChecked()){
                    editor.putBoolean("remember_pass",true);
                }else {
                    editor.putBoolean("remember_pass",false);
                    editor.clear();
                }
                editor.apply();
                if(idExists() && account!=null && password!=null){
                    Intent intent = new Intent(LoginActivity.this,DisplayActivity.class);
                    startActivity(intent);
                    finish();
                }else {
                    Toast.makeText(LoginActivity.this,"帐号或者密码错误，请重新输入",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public String readDatabseName(){
        Cursor cursor = database.rawQuery("select userId from userInfo",null);
        while (cursor.moveToNext()){
            userName1 = cursor.getString(cursor.getColumnIndex("userId"));
        }
        return userName1;
    }

    public String readDatabsePassword(){
        Cursor cursor = database.rawQuery("select password from userInfo",null);
        while (cursor.moveToNext()){
            userPassword1 = cursor.getString(cursor.getColumnIndex("password"));
        }
        return userPassword1;
    }

    public boolean idExists(){
        if(readDatabseName().equals(accountEdit.getText().toString())
                && readDatabsePassword().equals(passwordEdit.getText().toString())){
            return true;
        }
        return false;
    }
}
