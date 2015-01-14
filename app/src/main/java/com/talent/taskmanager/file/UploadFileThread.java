package com.talent.taskmanager.file;

import android.content.Context;
import android.os.Message;
import android.util.Log;

import com.coal.black.bc.socket.client.handlers.UploadFileHandler;
import com.coal.black.bc.socket.client.returndto.UploadFileResult;
import com.coal.black.bc.socket.dto.UploadFileDto;
import com.talent.taskmanager.dada.UploadFileDao;

import java.io.File;

/**
 * Created by chris on 14-12-30.
 */
public class UploadFileThread extends Thread {
    Context mContext = null;
    FileInfo mFileInfo = null;
    UploadResultListener mListener = null;
    private UploadFileDao mUploadFileDao = null;

    public interface UploadResultListener {
        void onUploadSucceed(FileInfo fileInfo);
        void onUploadFailed(FileInfo fileInfo);
    }

    public UploadFileThread(FileInfo mFileInfo, Context context) {
        this.mFileInfo = mFileInfo;
        this.mContext = context;
        mUploadFileDao = new UploadFileDao(context);
    }

    public FileInfo getFileInfo() {
        return mFileInfo;
    }

    public void setFileInfo(FileInfo mFileInfo) {
        this.mFileInfo = mFileInfo;
    }

    public UploadResultListener getListener() {
        return mListener;
    }

    public void setListener(UploadResultListener mListener) {
        this.mListener = mListener;
    }

    @Override
    public void run() {
        if (mFileInfo != null)
            upLoadFile(mFileInfo);
    }

    public void upLoadFile(FileInfo fileInfo) {
        File f = new File(fileInfo.getFilePath());
//        UploadFileDto fileDto = new UploadFileDto();
//        fileDto.setClientFile(f);
//        fileDto.setTaskId(fileInfo.getTaskId());
//        fileDto.setPicture(fileInfo.isPicture());
        UploadFileHandler uh = new UploadFileHandler();
        UploadFileResult result = uh.upload(f, fileInfo.getTaskId(), fileInfo.getTaskFlowTimes(), fileInfo.isPicture());
        Message msg = new Message();
        if (result.isSuccess()) {
            if (mListener != null) {
                mListener.onUploadSucceed(fileInfo);
            }
            Log.d("Chris", "upLoadFile, succeed: " + fileInfo);
        } else {
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
}
