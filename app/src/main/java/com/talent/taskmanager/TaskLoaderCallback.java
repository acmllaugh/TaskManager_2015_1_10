package com.talent.taskmanager;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.talent.taskmanager.task.TaskLoader;
import com.coal.black.bc.socket.dto.TaskDto;
import com.github.androidprogresslayout.ProgressLayout;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by acmllaugh on 14-11-20.
 */
public class TaskLoaderCallback implements LoaderManager.LoaderCallbacks<ArrayList<TaskDto>> {

    private TextView mEmptyView;
    private Activity mActivity;
    private ListView mTaskList;
    private ArrayAdapter mAdapter;
    private ProgressLayout mProgressLayout;
    private SharedPreferences mPrefs;
    private View mListHeader;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            Log.d("acmllaugh1", "set task count to screen.");
            int totalCount = (Integer)msg.obj;
            int unreadCount = msg.arg1;
            int readCount = msg.arg2;
            int processingCount = totalCount - unreadCount - readCount;
            if (mListHeader != null) {
                ((TextView) (mListHeader.findViewById(R.id.header_count_num))).setText(Integer.toString(totalCount));
                ((TextView) mListHeader.findViewById(R.id.list_header_unread_count)).setText(Integer.toString(unreadCount));
                ((TextView) mListHeader.findViewById(R.id.list_header_read_count)).setText(Integer.toString(readCount));
                ((TextView) mListHeader.findViewById(R.id.list_header_processing_count)).setText(Integer.toString(processingCount));
            }
            mAdapter.notifyDataSetChanged();//For update urgent tasks.
        }
    };

    public TaskLoaderCallback(Activity activity, ListView taskListView,
                              ArrayAdapter adapter, View headerView) {
        mActivity = activity;
        mTaskList = taskListView;
        mAdapter = adapter;
        if (mActivity instanceof TaskListActivity) {
            mProgressLayout = ((TaskListActivity) mActivity).getProgressLayout();
            mEmptyView = (TextView) mActivity.findViewById(R.id.txt_empty);
        }
        mListHeader = headerView;
    }

    @Override
    public Loader<ArrayList<TaskDto>> onCreateLoader(int i, Bundle bundle) {
//        Utils.log("TaskLoaderCallback:", "create progress and loader.");
        displayProgress(true);
        return new TaskLoader(mActivity.getApplicationContext());
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<TaskDto>> arrayListLoader, ArrayList<TaskDto> tasks) {
        if (tasks != null) {
            mAdapter.clear();
            mAdapter.addAll(tasks);
            mAdapter.notifyDataSetChanged();
            showEmptyView(tasks.size() < 1);
            showListHeader(tasks);
            saveLoadTime();
        }
        displayProgress(false);
    }

    private void showListHeader(final ArrayList<TaskDto> tasks) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                int unreadCount = 0;
                int readCount = 0;
                int taskCount = tasks.size();
                for (int i = 0; i < taskCount; i++) {
                    TaskDto task = tasks.get(i);
                    if (mActivity == null || mActivity.isFinishing()) {
                        return;
                    }
                    switch (task.getUserTaskStatus()) {
                        case 1:
                            unreadCount++;
                            break;
                        case 2:
                            readCount++;
                            break;
                    }
                    if (task.isUrgent()) {
                        tasks.remove(task);
                        tasks.add(0, task);
                    }
                }
                Message msg = new Message();
                msg.obj = tasks.size();
                msg.arg1 = unreadCount;
                msg.arg2 = readCount;
                mHandler.sendMessage(msg);
            }
        });
        thread.start();
    }

    private void saveLoadTime() {
        mPrefs = mActivity.getSharedPreferences(Constants.TASK_MANAGER, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putLong(Constants.LAST_REFRESH_TIME, System.currentTimeMillis());
        editor.apply();
    }

    private void showEmptyView(boolean visiable) {
        Utils.log("acmllaugh1:", "set empty view : " + visiable);
        if (mEmptyView == null) {
            Utils.log("acmllaugh1:", "Empty view is null.");
            return;
        }
        if (visiable) {
            mEmptyView.setVisibility(View.VISIBLE);
        } else {
            mEmptyView.setVisibility(View.INVISIBLE);
        }
    }


    @Override
    public void onLoaderReset(Loader<ArrayList<TaskDto>> arrayListLoader) {

    }

    private void displayProgress(boolean showProgress) {
        if (mProgressLayout != null) {
//            Utils.log("TaskLoaderCallback:", "show progress : " + showProgress);
            ArrayList<Integer> skipIDs = new ArrayList<Integer>();
            skipIDs.add(R.id.txt_empty);
            mProgressLayout.setProgress(showProgress, skipIDs);
        }
    }
}
