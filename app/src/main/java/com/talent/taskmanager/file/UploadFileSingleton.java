package com.talent.taskmanager.file;

import android.util.Log;

import com.coal.black.bc.socket.client.handlers.UploadFileHandler;
import com.coal.black.bc.socket.client.returndto.UploadFileResult;

import java.io.File;
import java.util.HashSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by chris on 15-2-10.
 */
public class UploadFileSingleton {
    private static UploadFileSingleton mInstance = null;
    private ExecutorService mExecutorService = Executors.newSingleThreadExecutor();
    private UploadResultListener mListener = null;
    private HashSet<FileInfo> mFileInfoHashSet = new HashSet<FileInfo>();

    public interface UploadResultListener {
        void onUploadSucceed(FileInfo fileInfo);
        void onUploadFailed(FileInfo fileInfo);
    }

    public void setListener(UploadResultListener mListener) {
        this.mListener = mListener;
    }

    public static synchronized UploadFileSingleton getInstance() {
        if (mInstance == null) {
            mInstance = new UploadFileSingleton();
        }
        return mInstance;
    }

    public void upLoadFile(final FileInfo fileInfo) {
        if (mExecutorService == null)
            return;
        if (addFileInfoIntoQueue(fileInfo)) {
            mExecutorService.submit(new Runnable() {
                @Override
                public void run() {
                    uploadFileToServer(fileInfo);
                }
            });
        } else {
            Log.d("Chris", "Omit, has exist: " + fileInfo);
        }
    }

    private void uploadFileToServer(FileInfo fileInfo) {
        File f = new File(fileInfo.getFilePath());
        UploadFileHandler uh = new UploadFileHandler();
        UploadFileResult result = uh.upload(f, fileInfo.getTaskId(), fileInfo.getTaskFlowTimes(), fileInfo.isPicture());
        Log.d("Chris", "Queue count " + mFileInfoHashSet.size());
        if (result.isSuccess()) {
            Log.d("Chris", "upLoadFile, succeed: " + fileInfo);
            if (mListener != null) {
                mListener.onUploadSucceed(fileInfo);
            }
            removeFileInfoFromQueue(fileInfo);
        } else {
            removeFileInfoFromQueue(fileInfo);
            if (mListener != null) {
                mListener.onUploadFailed(fileInfo);
            }
            if (result.isBusException()) {
                Log.d("Chris", "upLoadFile, Business Exception: " + result.getBusinessErrorCode());
            } else {
                Log.d("Chris", "upLoadFile, Other Exception: " + result.getThrowable());
            }
        }
    }

    private synchronized boolean addFileInfoIntoQueue(FileInfo fileInfo) {
        if (!mFileInfoHashSet.contains(fileInfo)) {
            mFileInfoHashSet.add(fileInfo);
            Log.d("Chris", "Add to queue: " + fileInfo);
            return true;
        } else {
            return false;
        }
    }

    private synchronized void removeFileInfoFromQueue(FileInfo fileInfo) {
        if (mFileInfoHashSet.contains(fileInfo)) {
            mFileInfoHashSet.remove(fileInfo);
            Log.d("Chris", "Remove from queue: " + fileInfo);
        }
    }

}