package com.talent.taskmanager.update;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;

import com.coal.black.bc.socket.client.handlers.ApkQueryHandler;
import com.coal.black.bc.socket.client.returndto.ApkQueryResult;
import com.talent.taskmanager.R;
import com.talent.taskmanager.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by chris on 15-3-5.
 */
public class AppUpdateManager {
    private static final int MSG_SHOW_UPDATE = 0;
    private static final int MSG_DOWNLOAD_BEGIN = 1;
    private static final int MSG_DOWNLOADING = 2;
    private static final int MSG_DOWNLOAD_FINISHED = 3;

    private Context mActivity;
    private int mCurrentProgress;
    private ProgressBar mProgressBar;
    private Dialog mDownloadDialog;
    private boolean mCancelDownload = false;

    private static final String APK_DOWNLOAD_URL = "http://223.68.133.15:12346/app-update/appupdate.do";
    public static final String APK_DOWNLOAD_SAVE_DIR = Environment.getExternalStorageDirectory() + "/TaskFiles";
    private static final String APK_DOWNLOAD_SAVE_NAME = "TaskManager.apk";

    public AppUpdateManager(Context context) {
        mActivity = context;
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_SHOW_UPDATE:
                    showUpdateInfoDialog();
                    break;
                case MSG_DOWNLOAD_BEGIN:
                    showDownloadProgress();
                    break;
                case MSG_DOWNLOADING:
                    mProgressBar.setProgress(mCurrentProgress);
                    break;
                case MSG_DOWNLOAD_FINISHED:
                    if (mDownloadDialog.isShowing())
                        mDownloadDialog.dismiss();
                    installApkFile(new File(APK_DOWNLOAD_SAVE_DIR, APK_DOWNLOAD_SAVE_NAME));
                    break;
                default:
                    break;
            }
        };
    };

    /**
     * Check if need to update app
     */
    public void checkIfNeedUpdate() {
        new Thread().start();
        Thread thread = new Thread() {
            @Override
            public void run() {
//                super.run();
                ApkQueryHandler handler = new ApkQueryHandler();
                ApkQueryResult result = handler.queryLastedVersion();
                if (result.isSuccess()) {
                    Log.d("Chris", "Check app version, server: " + result.getVersionDto().getVersionNum()
                            + ", current: " + Utils.getVersionCode(mActivity));
                    if (result.getVersionDto().getVersionNum() > Utils.getVersionCode(mActivity)) {
                        mHandler.sendEmptyMessage(MSG_SHOW_UPDATE);
                    }
                } else {
                    if (result.isBusException()) {
                        Log.d("Chris", "isBusException: " + + result.getBusinessErrorCode());
                        System.out.println("Business Exception, exception code is " + result.getBusinessErrorCode());
                    } else {
                        Log.d("Chris", "Other");
                        System.out.println("Other Exception, exception type is " + result.getThrowable());
                    }
                }
            }
        };
        thread.start();
    }

    private void showUpdateInfoDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setTitle(mActivity.getString(R.string.app_update_title))
                .setMessage(mActivity.getString(R.string.app_update_message))
                .setCancelable(false)
                .setPositiveButton(mActivity.getString(R.string.update), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                startDownloadApk();
            }
        }).create().show();
    }

    private void showDownloadProgress() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setTitle(mActivity.getString(R.string.app_updating));
        final LayoutInflater inflater = LayoutInflater.from(mActivity);
        View v = inflater.inflate(R.layout.layout_update_progress, null);
        mProgressBar = (ProgressBar) v.findViewById(R.id.progressBar);
        builder.setView(v);
        // Cancel updating
        builder.setNegativeButton(R.string.app_cancel_update, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mCancelDownload = true;
                dialog.dismiss();
            }
        });
        mDownloadDialog = builder.create();
//        Window dialogWindow = mDownloadDialog.getWindow();
//        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
//        dialogWindow.setGravity(Gravity.CENTER);
//        dialogWindow.setAttributes(lp);
        mDownloadDialog.show();
    }

    /**
     * Install apk file
     * @param apkFile
     */
    private void installApkFile(File apkFile) {
        if (!apkFile.exists()) {
            return;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse("file://" + apkFile.toString()), "application/vnd.android.package-archive");
        mActivity.startActivity(intent);
    }

    public void startDownloadApk() {
        mHandler.sendEmptyMessage(MSG_DOWNLOAD_BEGIN);
        new DownloadApkThread().start();
    }

    private class DownloadApkThread extends Thread {
        @Override
        public void run() {
            super.run();
            try {
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    URL url = new URL(APK_DOWNLOAD_URL);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.connect();
                    int length = conn.getContentLength();
                    InputStream is = conn.getInputStream();
                    File file = new File(APK_DOWNLOAD_SAVE_DIR);
                    if (!file.exists()) {
                        file.mkdir();
                    }
                    File apkFile = new File(APK_DOWNLOAD_SAVE_DIR, APK_DOWNLOAD_SAVE_NAME);
                    FileOutputStream fos = new FileOutputStream(apkFile);
                    int count = 0;
                    byte buf[] = new byte[1024];
                    do {
                        int numRead = is.read(buf);
                        count += numRead;
                        mCurrentProgress = (int) (((float) count / length) * 100);
                        // update downloading progress
                        mHandler.sendEmptyMessage(MSG_DOWNLOADING);
                        if (numRead <= 0) {
                            // download finished
                            mHandler.sendEmptyMessage(MSG_DOWNLOAD_FINISHED);
                            break;
                        }
                        fos.write(buf, 0, numRead);
                    } while (!mCancelDownload);
                    fos.close();
                    is.close();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
