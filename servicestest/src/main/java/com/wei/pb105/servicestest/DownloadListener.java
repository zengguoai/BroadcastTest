package com.wei.pb105.servicestest;

/**
 * Created by weiguanghua on 18-3-14.
 */

public interface DownloadListener {
    void onProgress(int progress);
    void onSuccess();
    void onFailed();
    void onPaused();
    void onCanceled();
}
