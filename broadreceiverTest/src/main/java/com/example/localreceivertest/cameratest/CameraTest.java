package com.example.localreceivertest.cameratest;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.localreceivertest.BaseActivity;
import com.example.localreceivertest.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by weiguanghua on 18-1-26.
 */

public class CameraTest extends BaseActivity {
    private static final int TAKE_PHOTO=1;
    private Button takePhoto;
    private ImageView picture;
    private Uri imagerUri;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cameratest_layout);
        takePhoto = (Button) findViewById(R.id.take_photo);
        picture =(ImageView) findViewById(R.id.picture_view);
        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pictureName = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss",Locale.getDefault())
                                            .format(new Date()) + "-" + System.currentTimeMillis()+".jpg";
                File outputImage = new File(getFilesDir(),pictureName );
                try {
                    if(outputImage.exists()){
                        outputImage.delete();
                    }
                    outputImage.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(Build.VERSION.SDK_INT>=24){
                    imagerUri = FileProvider.getUriForFile(CameraTest.this,"com.example.fileprovider",outputImage);
                }else {
                    imagerUri = Uri.fromFile(outputImage);
                }
                Log.d("wgh","imagerUri="+imagerUri);
                if(ContextCompat.checkSelfPermission(CameraTest.this, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED
                        || ContextCompat.checkSelfPermission(CameraTest.this,Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                    if(ActivityCompat.shouldShowRequestPermissionRationale(CameraTest.this,Manifest.permission.CAMERA)){
                        Toast.makeText(CameraTest.this,"你已经拒绝过一次了",Toast.LENGTH_SHORT).show();
                    }
                    ActivityCompat.requestPermissions(CameraTest.this, new String[]{Manifest.permission.CAMERA,Manifest.permission.READ_EXTERNAL_STORAGE},2);
                }else{
                    takephoto();
                }
            }
        });
    }

    private void takephoto(){
        //启动相机程序
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,imagerUri);
        startActivityForResult(intent,TAKE_PHOTO);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case 2:
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    takephoto();
                }else{
                    Toast.makeText(this,"你没有权限打开相机",Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case TAKE_PHOTO:
                if(resultCode == RESULT_OK){
                    try {
                        //将拍摄的照片显示出来
                        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imagerUri));
                        picture.setImageBitmap(bitmap);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
            default:
                break;
        }
    }
}
