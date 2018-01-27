package com.example.localreceivertest;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by weiguanghua on 18-1-25.
 */

public class RegisterActivity extends BaseActivity implements View.OnClickListener{
    private EditText userEdit;
    private EditText passwEdit;
    private EditText passwAgEdit;
    private Button registerButton;
    private Button backButton;
    private MyOpenHelper myOpenHelper;
    private SQLiteDatabase database;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_layout);
        initView();
    }

    private void initView() {
        userEdit =findViewById(R.id.userameText);
        passwEdit =findViewById(R.id.passwordText1);
        passwAgEdit =findViewById(R.id.passwordText2);
        registerButton = findViewById(R.id.register);
        backButton = findViewById(R.id.back);
        registerButton.setOnClickListener(this);
        registerButton.setTag(1);
        backButton.setOnClickListener(this);
        backButton.setTag(2);
    }

    @Override
    public void onClick(View v) {
        switch ((int)v.getTag()){
            case 1:
                createDatabase();
                insertData();
                break;
            case 2:
                backLogin();
                break;
            default:
                break;
        }
    }

    private void createDatabase() {
        myOpenHelper = new MyOpenHelper(RegisterActivity.this,"myUser.db",null,1);
        database = myOpenHelper.getWritableDatabase();
    }

    private void insertData() {
        String username = userEdit.getText().toString();
        String password1 = passwEdit.getText().toString();
        String password2 = passwAgEdit.getText().toString();
        if(username!=null && username.length()!=0
                && password1!=null && password1.length()!=0
                && password2!=null && password2.length()!=0){
            createDatabase();
            if(pwdCorrect()){
                Log.d("wgh","userEdit = "+userEdit.getText().toString()+"\n"+"passwEdit="+passwEdit.getText().toString()+"\n"+
                        "passwAgEdit="+passwAgEdit.getText().toString());
                ContentValues contentValues = new ContentValues();
                contentValues.put("userId",userEdit.getText().toString());
                contentValues.put("password",passwEdit.getText().toString());
                database.insert("userInfo",null,contentValues);
                backLogin();
                Toast.makeText(RegisterActivity.this,"注册成功",Toast.LENGTH_LONG).show();
            }else {
                Toast.makeText(RegisterActivity.this,"两次密码输入不一致，请重新输入",Toast.LENGTH_LONG).show();
                passwAgEdit.setText("");
            }
        }else {
            Toast.makeText(RegisterActivity.this,"帐号或密码不能为空",Toast.LENGTH_LONG).show();
        }

    }

    private void backLogin() {
        Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
        startActivity(intent);
    }

    private boolean pwdCorrect() {
        String password = passwEdit.getText().toString();
        String passwordAgain = passwAgEdit.getText().toString();
        if(password!=null && passwordAgain!=null && password.length()!=0 && passwordAgain.length()!=0){
            if(password.equals(passwordAgain)){
                return true;
            }
          return false;
        }
        return false;
    }

}
