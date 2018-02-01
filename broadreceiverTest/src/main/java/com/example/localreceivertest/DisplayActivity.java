package com.example.localreceivertest;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.localreceivertest.cameratest.CameraTest;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by weiguanghua on 18-1-18.
 */

public class DisplayActivity extends BaseActivity {
    private RecyclerView recyclerView;
    private List<Contacts> mContactslist = new ArrayList<>();
    private ContactsAdapter adapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout);

        recyclerView = findViewById(R.id.recycler_view);
        LinearLayoutManager linearLayoutManager =new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new ContactsAdapter(mContactslist);
        recyclerView.setAdapter(adapter);
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_CONTACTS},1);
        }else {
            readContacts();
        }
        Button camerabutton = findViewById(R.id.camera_test);
        Button forceOffline = (Button)findViewById(R.id.force_offline);
        forceOffline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("com.weibu.FORCE_OFFLINE");
                sendBroadcast(intent);
            }
        });
        camerabutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(DisplayActivity.this, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED
                        || ContextCompat.checkSelfPermission(DisplayActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                        || ContextCompat.checkSelfPermission(DisplayActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ){
                    if(ActivityCompat.shouldShowRequestPermissionRationale(DisplayActivity.this,Manifest.permission.CAMERA)){
                        Toast.makeText(DisplayActivity.this,"你已经拒绝过一次了",Toast.LENGTH_SHORT).show();
                    }
                    ActivityCompat.requestPermissions(DisplayActivity.this, new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE},2);
                }else {
                   openCamera();
                }
            }
        });
    }

    private void openCamera(){
        Intent intent = new Intent(DisplayActivity.this, CameraTest.class);
        startActivity(intent);
    }
    private void readContacts() {
        Cursor cursor =null;
        try {
            cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,null,null,null);
            if(cursor!=null){
                while (cursor.moveToNext()){
                    String displayname = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    String phonenumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    Contacts contacts = new Contacts(displayname,phonenumber);
                    mContactslist.add(contacts);
                }
                adapter.notifyDataSetChanged();
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(cursor!=null){
                cursor.close();
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case 1:
                if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    readContacts();
                }else {
                    Toast.makeText(this,"You denied thi permission",Toast.LENGTH_SHORT).show();
                }
                break;
            case 2:
                if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    openCamera();
                }else {
                    Toast.makeText(this,"You denied thi permission",Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
