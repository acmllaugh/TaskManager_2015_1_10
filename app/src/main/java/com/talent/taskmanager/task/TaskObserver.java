package com.talent.taskmanager.task;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.coal.black.bc.socket.client.handlers.TaskQryUserNewTaskHandler;
import com.coal.black.bc.socket.client.returndto.TaskQryUserNewTaskCountResult;
import com.talent.taskmanager.Constants;
import com.talent.taskmanager.Utils;


/**
 * Created by acmllaugh on 14-11-20.
 */
public class TaskObserver {
    public static final int ONE_MINUTE = 60 * 1000;
    private TaskLoader loader;
    private SharedPreferences mPrefs;
    private Context mContext;

    public TaskObserver(TaskLoader loader, Context context) {
        mContext = context;
        this.loader = loader;
        RefreshThread thread = new RefreshThread();
        thread.start();
    }

    private long getLastRefreshTime() {
        mPrefs = mContext.getSharedPreferences(Constants.TASK_MANAGER, Context.MODE_PRIVATE);
        return mPrefs.getLong(Constants.LAST_REFRESH_TIME, 0);
    }

    private class RefreshThread extends Thread {
        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(ONE_MINUTE);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                long lastRefreshTime = getLastRefreshTime();
                TaskQryUserNewTaskCountResult countResult = null;
                TaskQryUserNewTaskHandler handler = new TaskQryUserNewTaskHandler();
                countResult = handler.qryNewTaskCount(lastRefreshTime);
                if (countResult.isSuccess()) {
                    if(countResult.getCount() > 0) {
                        loader.onContentChanged();
                    }
                } else {
                    if (countResult.getThrowable() != null) {
                        Log.d("acmllaugh1", "refresh task failed." + countResult.getThrowable().getMessage());
                    }else{
                        Log.d("acmllaugh1", "refresh task failed without throwable.");
                    }

                }
            }
        }
    }
}
