package com.wei.pb105.servicestest;

import android.os.AsyncTask;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by weiguanghua on 18-3-14.
 */

public class DownloadTask extends AsyncTask<String, Integer, Integer> {
    public static  final int TYPE_SUCCESS = 0;
    public static  final int TYPE_FAILED = 1;
    public static  final int TYPE_PAUSED = 2;
    public static  final int TYPE_CANCELED = 3;
    private DownloadListener listener;
    private boolean isCanceled =false;
    private boolean isPaused = false;
    private int  lastProgress;

    public DownloadTask(DownloadListener listener){
        this.listener = listener;
    }

    @Override
    protected void onPreExecute() {
    //显示UI 界面
    }

    @Override
    protected Integer doInBackground(String... params) {
        //任务执行，publishProgress方法用来进行UI操作
        InputStream is =null;
        RandomAccessFile saveFile = null;
        File file = null;
        try {
			 //1、从param【0】获取下载地址URL，然后解析URL获得文件名，并指定将文件下载到download目录下。
            long downloadLength = 0;
            String downloadUrl = params[0];
            String fileName = downloadUrl.substring(downloadUrl.lastIndexOf("/"));
            String directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
            file = new File(directory + fileName);
            logUtil("fileName = "+ fileName +"\n" +"file = "+file);
			//2、判断download目录是否已存在该下载文件，如果存在，就读取该文件的字节长度并保存，英语后的断点续传的功能
            if (file.exists()) {
                downloadLength = file.length();
            }
			//3、然后先调用getContentLength(String url)方法获取网上待下载的文件总长度;
			//先判断这个总长度是否为0，若是则说明文件有问题，返回TYPE_FAILED；
			//如果跟已存在的文件长度相等，说明已下载成功，返回TYPE_SUCCESS；
            long contentLength = getContentLength(downloadUrl);
            if (contentLength == 0) {
                return TYPE_FAILED;
            } else if (contentLength == downloadLength) {
                return TYPE_SUCCESS;
            }
			//4、使用okhttp发送网络请求，添加addHeader告诉服务器我们要从哪个字节开始下载，因为一下子的部门不需要重新下载；
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .addHeader("RANGE", "bytes=" + downloadLength + "-")
                    .url(downloadUrl)
                    .build();
            Response response = client.newCall(request).execute();
			
			//5、通过response读取服务器响应的数据，并且使用Java的文件流方式，不断从网络上读取数据，不断写入本地，直到文件全部下载完成为止；
			//6、在这个过程，需要判断用户有没有触发暂停和取消的操作，如果有就返回TYPE_PAUSED和TYPE_CANCELED来中断下载；
			//如果没有，就实时计算当前的下载进度，然后通过publishProgress（integer progress）进行通知。
			
            if(response.isSuccessful()){
                is = response.body().byteStream();
                saveFile = new RandomAccessFile(file,"rw");
                saveFile.seek(downloadLength);
                byte[] b = new byte[1024];
                int total =0;
                int len;
                while((len=is.read(b)) != -1){
                    if(isCanceled){
                        return TYPE_CANCELED;
                    }else if(isPaused){
                        return TYPE_PAUSED;
                    }else {
                        total = total+len;
                        saveFile.write(b,0,len);
                        //计算百分比
                        int progress = (int) ((total + downloadLength)*100/contentLength);
                        publishProgress(progress);
                    }
                }

                response.body().close();
                return TYPE_SUCCESS;
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                if(is!=null){
                    is.close();
                }
                if(saveFile!=null){
                    saveFile.close();
                }
                if(isCanceled&& file!=null){
                    file.delete();
                }
               } catch (IOException e) {
                    e.printStackTrace();
            }
        }
        return TYPE_FAILED;
    }
	
	/*
	1、获取publishProgress（integer progress）传递的当前进度值，
		然后跟上一次的下载进度lastProgress进行对比，
		如果有变化就调用DownloadListener的onProgress方法来通知下载进度更新。
	*/
    @Override
    protected void onProgressUpdate(Integer... values) {
        //在这里更新任务执行进度
        int progress = values[0];
        if(progress > lastProgress){
            listener.onProgress(progress);
            lastProgress = progress;
        }
    }


    @Override
    protected void onPostExecute(Integer status) {
        //任务执行完毕返回的staus的值进行回调。
        switch (status){
            case TYPE_SUCCESS:
                listener.onSuccess();
                break;
            case TYPE_PAUSED:
                listener.onPaused();
                break;
            case TYPE_CANCELED:
                listener.onCanceled();
                break;
            case TYPE_FAILED:
                listener.onFailed();
                break;
            default:
                break;
        }
    }
	
	//暂停和取消操作都是使用一个Boolean值来控制，然后调用pauseDownload（）和cancelDownload（）方法来改变改变变量的值。
    public void pauseDownload(){
        isPaused = true;
    }

    public void cancelDownload(){
        isCanceled = true;
    }

    private long getContentLength(String downloadUrl) throws IOException{
		//需添加这句UI线程策略，否则没有权限访问网络
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskWrites().detectNetwork().penaltyLog().build());
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(downloadUrl)
                .build();
        Response response = client.newCall(request).execute();
        if(response != null && response.isSuccessful()){
            long contentLength = response.body().contentLength();
            response.close();
            logUtil("contentLength == " +contentLength);
            return contentLength;
        }
        return 0;

    }

    public void logUtil(String message){
        Log.d("wgh",message);
    }


}
