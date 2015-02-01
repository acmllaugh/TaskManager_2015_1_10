package com.talent.taskmanager.notification;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;

import com.talent.taskmanager.Constants;
import com.talent.taskmanager.Utils;

/**
 * Created by xiaoyue on 2015/2/1.
 */
public class RestartGuardService extends Service{

    public static final int ONE_MINUTE = 1000 * 60;
    private Context mContext;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mContext = getApplicationContext();
        GuardServiceTask task = new GuardServiceTask();
        task.execute();
        return START_STICKY;
    }

    private class GuardServiceTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            while (true) {
                try {
                    if (!Utils.isServiceRunning(getApplicationContext(), Constants.SERVICE_NAME)) {
                        Intent serviceIntent = new Intent(mContext, TaskManagerService.class);
                        startService(serviceIntent);
                    }
                    Thread.sleep(ONE_MINUTE);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
