package com.example.localreceivertest.cameratest;

import android.Manifest;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
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
    private static final int OPEN_ALBUM=2;
    private Button takePhoto;
    private Button choosePhoto;
    private ImageView picture;
    private Uri imagerUri;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cameratest_layout);
        takePhoto = (Button) findViewById(R.id.take_photo);
        picture =(ImageView) findViewById(R.id.picture_view);
        choosePhoto = (Button) findViewById(R.id.choose_photo);
        choosePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    openAlbum();
            }
        });
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
                takephoto();
            }
        });
    }
    //启动相机程序
    private void takephoto(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,imagerUri);
        startActivityForResult(intent,TAKE_PHOTO);
    }
    //访问相册
    private void openAlbum(){
        //启动相机程序
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent,OPEN_ALBUM);
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

            case OPEN_ALBUM:
                if(resultCode == RESULT_OK){
                    String imagePath = null ;
                    Uri uri = data.getData();
                    if(DocumentsContract.isDocumentUri(this,uri)){
                        String docId = DocumentsContract.getDocumentId(uri);
                        if("com.android.providers.media.documents".equals(uri.getAuthority())){
                            String id = docId.split(":")[1];
                            String selection = MediaStore.Images.Media._ID+"="+id;
                            imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,selection);
                        }else if("com.android.providers.downloads.documents".equals(uri.getAuthority())){
                            Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),Long.valueOf(docId));
                            imagePath = getImagePath(contentUri,null);
                        }
                    }else if("content".equalsIgnoreCase(uri.getScheme())){
                        imagePath = getImagePath(uri,null);
                    }else if("file".equalsIgnoreCase(uri.getScheme())){
                        imagePath = uri.getPath();
                    }
                    if(imagePath != null){
                        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
                        picture.setImageBitmap(bitmap);
                    }else {
                        Toast.makeText(this,"获取照片失败",Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            default:
                break;
        }
    }

    private String getImagePath(Uri uri,String selection){
        String path = null;
        Cursor cursor = getContentResolver().query(uri,null,selection,null,null);
        if(cursor!=null){
            if(cursor.moveToNext()){
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }
}
