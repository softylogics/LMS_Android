package com.dusre.lms.Util;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class DownloadCompleteReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(intent.getAction())) {
            long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            // Handle the completed download (e.g., perform post-download tasks)
            handleDownloadCompletion(context, downloadId);
        }

    }
    private void handleDownloadCompletion(Context context, long downloadID) {
        Log.d("post Download" , " in receiver handle download");
        // Perform actions after download completes (e.g., process the downloaded file)
        // You can start a service or perform any necessary background processing here
        // For example, launch a service to handle post-download tasks
        Intent serviceIntent = new Intent(context, PostDownloadService.class);
        serviceIntent.putExtra("downloadId", downloadID);

        context.startService(serviceIntent);
    }
}
