package com.talent.taskmanager;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.talent.taskmanager.task.TaskLoader;
import com.coal.black.bc.socket.dto.TaskDto;
import com.github.androidprogresslayout.ProgressLayout;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private ArrayList<TaskDto> mAllTasks;
    private Spinner mSpinStatus;
    private Spinner mSpinProvince;
    private Spinner mSpinCity;
    private Spinner mSpinRegion;
    private List<String> mTaskStatusList = new ArrayList<String>();
    private List<String> mProviceList = new ArrayList<String>();
    private List<String> mCityList = new ArrayList<String>();
    private List<String> mRegionList = new ArrayList<String>();
    private List<Map<String, String>> mProvinceCityList = new ArrayList<Map<String, String>>();
    private List<Map<String, String>> mCityRegionList = new ArrayList<Map<String, String>>();
    private ArrayAdapter<String> mCityArrayAdapter;
    private ArrayAdapter<String> mRegionArrayAdapter;
    private String mSelectedAll;        // String of "All"
    private SpinnerItemSelectedListener mSpinnerItemSelectedListener;
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

                initSpinners();
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

        mSelectedAll = mActivity.getResources().getString(R.string.all);
        mSpinnerItemSelectedListener = new SpinnerItemSelectedListener();
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
            if (tasks.size() > 0 && tasks != mAllTasks) {
                getSelections(tasks);
                mAllTasks = tasks;
            }

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

    /**
     * ["未读", "已读", "进行中"] => [1, 2, 3]
     * @param statusValue
     * @return
     */
    private int getTaskStatusKey(String statusValue) {
        Resources resources = mActivity.getResources();
        int statusKey;
        if (resources.getString(R.string.un_read).equals(statusValue)) {
            statusKey = 1;
        } else if (resources.getString(R.string.has_been_read).equals(statusValue)) {
            statusKey = 2;
        } else if (resources.getString(R.string.task_processing).equals(statusValue)) {
            statusKey = 3;
        } else {
            statusKey = 0;
        }
        return statusKey;
    }

    /**
     * [1, 2, 3] => ["未读", "已读", "进行中"]
     * @param statusKey
     * @return
     */
    private String getTaskStatusValue(int statusKey) {
        Resources resources = mActivity.getResources();
        String statusValue;
        switch (statusKey) {
            case 1:
                statusValue = resources.getString(R.string.un_read);
                break;
            case 2:
                statusValue = resources.getString(R.string.has_been_read);
                break;
            case 3:
                statusValue = resources.getString(R.string.task_processing);
                break;
            default:
                statusValue = resources.getString(R.string.all);
                break;
        }
        return statusValue;
    }

    /**
     * Get all selections of those spinners
     * @param tasks
     */
    private void getSelections(ArrayList<TaskDto> tasks) {
        mTaskStatusList.clear();
        mTaskStatusList.add(mActivity.getResources().getString(R.string.all));
        mProviceList.clear();
        mProviceList.add(mActivity.getResources().getString(R.string.all));
        mProvinceCityList.clear();
        mCityRegionList.clear();
        for (int i = 0; i < tasks.size(); i++) {
            TaskDto task = tasks.get(i);
//            Log.d("Chris", "Task status: " + task.getTaskStatus() + ", Province = " + task.getProvince()
//                    + ", City = " + task.getCity() + ", Region = " + task.getRegion());
            String taskStatus = getTaskStatusValue(task.getTaskStatus());

            String taskProvince = task.getProvince();
            String taskCity = task.getCity();
            String taskRegion = task.getRegion();
            Map<String, String> provinceCity = new HashMap<String, String>();
            Map<String, String> cityRegion = new HashMap<String, String>();
            if (!mTaskStatusList.contains(taskStatus)) {
                mTaskStatusList.add(taskStatus);
            }
            if (!mProviceList.contains(taskProvince)) {
                mProviceList.add(taskProvince);
            }
            provinceCity.put(taskProvince, taskCity);
            if (!mProvinceCityList.contains(provinceCity)) {
                mProvinceCityList.add(provinceCity);
            }
            cityRegion.put(taskCity, taskRegion);
            if (!mCityRegionList.contains(cityRegion)) {
                mCityRegionList.add(cityRegion);
            }
        }
    }

    /**
     * Get all of cities of selected province
     * @param province
     * @return
     */
    private List<String> getCities(String province) {
        List<String> cityList = new ArrayList<String>();
        cityList.add(mActivity.getResources().getString(R.string.all));
        String city;
        if (province != null && mProvinceCityList != null && mProvinceCityList.size() > 0) {
            for(int i = 0; i < mProvinceCityList.size(); i++) {
//                Log.d("Chris", "" + mProvinceCityList.get(i) + ", " + mProvinceCityList.get(i).get(province));
                city = mProvinceCityList.get(i).get(province);
                if (city != null && !cityList.contains(city)) {
                     cityList.add(city);
                }
            }
        }
        return cityList;
    }

    /**
     * Get all of regions of selected city
     * @param city
     * @return
     */
    private List<String> getRegions(String city) {
        List<String> regionList = new ArrayList<String>();
        regionList.add(mActivity.getResources().getString(R.string.all));
        String region;
        if (city != null && mCityRegionList != null && mCityRegionList.size() > 0) {
            for(int i = 0; i < mCityRegionList.size(); i++) {
//                Log.d("Chris", "" + mCityRegionList.get(i) + ", " + mCityRegionList.get(i).get(city));
                region = mCityRegionList.get(i).get(city);
                if (region != null && !regionList.contains(region)) {
                    regionList.add(region);
                }
            }
        }
        return regionList;
    }

    private void initSpinners() {
        // Spinner: Task status
        mSpinStatus = (Spinner)mListHeader.findViewById(R.id.spinner_status);
        ArrayAdapter<String> statusArrayAdapter = new ArrayAdapter<String>(mActivity.getApplicationContext(), R.layout.spinner_item, mTaskStatusList);
        statusArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinStatus.setAdapter(statusArrayAdapter);
        mSpinStatus.setOnItemSelectedListener(mSpinnerItemSelectedListener);

        // Spinner: Province
        mSpinProvince = (Spinner)mListHeader.findViewById(R.id.spinner_province);
        ArrayAdapter<String> provinceArrayAdapter = new ArrayAdapter<String>(mActivity.getApplicationContext(), R.layout.spinner_item, mProviceList);
        provinceArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinProvince.setAdapter(provinceArrayAdapter);
        mSpinProvince.setOnItemSelectedListener(mSpinnerItemSelectedListener);

        // Spinner: City
        mSpinCity = (Spinner)mListHeader.findViewById(R.id.spinner_city);
        mSpinCity.setOnItemSelectedListener(mSpinnerItemSelectedListener);

        // Spinner: Region
        mSpinRegion = (Spinner)mListHeader.findViewById(R.id.spinner_region);
        mSpinRegion.setOnItemSelectedListener(mSpinnerItemSelectedListener);
    }

    private void initSpinCity(String province) {
        mCityList = getCities(province);
        mCityArrayAdapter = new ArrayAdapter<String>(mActivity.getApplicationContext(), R.layout.spinner_item, mCityList);
        mCityArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinCity.setAdapter(mCityArrayAdapter);
    }
    private void initSpinRegion(String city) {
        mRegionList = getRegions(city);
        mRegionArrayAdapter = new ArrayAdapter<String>(mActivity.getApplicationContext(), R.layout.spinner_item, mRegionList);
        mRegionArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinRegion.setAdapter(mRegionArrayAdapter);
    }

    private class SpinnerItemSelectedListener implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            if (adapterView.getId() == R.id.spinner_province) {
                Log.d("Chris", "Province selected: " + mSpinProvince.getSelectedItem().toString());
                initSpinCity(mSpinProvince.getSelectedItem().toString());
            } else if (adapterView.getId() == R.id.spinner_city) {
                Log.d("Chris", "City selected: " + mSpinCity.getSelectedItem().toString());
                initSpinRegion(mSpinCity.getSelectedItem().toString());
            } else if (adapterView.getId() == R.id.spinner_region) {
                Log.d("Chris", "Region selected: " + mSpinRegion.getSelectedItem().toString());
            } else if (adapterView.getId() == R.id.spinner_status) {
                Log.d("Chris", "Status selected: " + mSpinStatus.getSelectedItem().toString());
            }
            filterTasks();
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {
        }
    }

    private boolean ifNeedStatusFilter() {
        return (mSpinStatus.getSelectedItem() != null
                && !mSelectedAll.equals(mSpinStatus.getSelectedItem().toString()) ? true : false);
    }
    private boolean ifNeedLocationFilter() {
        return (mSpinProvince.getSelectedItem() != null
                && !mSelectedAll.equals(mSpinProvince.getSelectedItem().toString()) ? true : false);
    }
    private boolean ifNeedCityFilter() {
        return (mSpinCity.getSelectedItem() != null
                && !mSelectedAll.equals(mSpinCity.getSelectedItem().toString()) ? true : false);
    }
    private boolean ifNeedRegionFilter() {
        return (mSpinRegion.getSelectedItem() != null
                && !mSelectedAll.equals(mSpinRegion.getSelectedItem().toString()) ? true : false);
    }

    /**
     * Judge this task is right for the filters.
     * @param task
     * @return
     */
    private boolean isFilterTask(TaskDto task) {
        if (ifNeedStatusFilter() && task.getTaskStatus() != getTaskStatusKey(mSpinStatus.getSelectedItem().toString())) {
            return false;
        }
        if (ifNeedLocationFilter()) {
            if (!task.getProvince().equals(mSpinProvince.getSelectedItem().toString())) {
                return false;
            }
            if (ifNeedCityFilter()) {
                if (!task.getCity().equals(mSpinCity.getSelectedItem().toString())) {
                    return false;
                }
                if (ifNeedRegionFilter()) {
                    if (!task.getRegion().equals(mSpinRegion.getSelectedItem().toString())) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Filter those tasks
     */
    private void filterTasks() {
        mAdapter.clear();
        for (int i = 0; i < mAllTasks.size(); i++) {
            TaskDto task = mAllTasks.get(i);
            if (isFilterTask(task))
                mAdapter.add(task);
        }
        mAdapter.notifyDataSetChanged();
    }

}
