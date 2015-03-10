package com.talent.taskmanager.file;

import android.util.Log;

import com.talent.taskmanager.data.UploadFileDao;

/**
 * Created by chris on 15-2-10.
 */
public class UploadFileCallback implements UploadFileSingleton.UploadResultListener {

    private UploadFileDao mUploadFileDao = null;

    public UploadFileCallback(UploadFileDao mUploadFileDao) {
        this.mUploadFileDao = mUploadFileDao;
    }

    @Override
    public void onUploadSucceed(FileInfo fileInfo) {
        if (mUploadFileDao != null) {
            fileInfo.setUploadResult(1);
            mUploadFileDao.updateUploadStatus(fileInfo.getFilePath());
            Log.d("Chris", "onUploadSucceed, change status, " + fileInfo);
        }
    }

    @Override
    public void onUploadFailed(FileInfo fileInfo) {
        // do nothing
    }
}
