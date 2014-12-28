package com.talent.taskmanager;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.coal.black.bc.socket.client.ClientGlobal;
import com.coal.black.bc.socket.client.handlers.UserSignHandler;
import com.coal.black.bc.socket.client.returndto.LoginResult;
import com.coal.black.bc.socket.client.returndto.SignInResult;
import com.coal.black.bc.socket.dto.SignInDto;
import com.coal.black.bc.socket.dto.TaskDto;
import com.coal.black.bc.socket.enums.SignInType;
import com.github.androidprogresslayout.ProgressLayout;
import com.talent.taskmanager.location.LocationManager;
import com.talent.taskmanager.network.NetworkState;
import com.talent.taskmanager.notification.TaskManagerService;
import com.talent.taskmanager.task.TaskListAdapter;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;


public class TaskListActivity extends Activity {

    private static final int SIGN_IN_RESULT = 1;
    LoaderManager.LoaderCallbacks mTaskLoaderCallback;
    private ListView mTaskListView;
    private TaskListAdapter mTaskListAdapter;
    private ProgressLayout mProgressLayout;
    private EventBus mEventBus = EventBus.getDefault();
    private UserSignHandler mSignInHandler = new UserSignHandler();
    private int mUserID;
    private ProgressDialog mProcessingDialog;
    private LocationManager mLocationManager;
    private Toast mToast = null;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SIGN_IN_RESULT:
                    if (msg.obj instanceof SignInResult) {
                        Utils.dissmissProgressDialog(mProcessingDialog);
                        SignInResult result = (SignInResult) msg.obj;
                        Log.d("acmllaugh1", "handleMessage (line 63): sign in result : " + result.isSuccess());
                        if (result.isSuccess()) {
                            Utils.showToast(mToast, getString(R.string.sign_in_success), TaskListActivity.this.getApplicationContext());
                        }
                    } else {
                        Log.d("acmllaugh1", "handleMessage (line 65): not available sign in result object.");
                    }
                    break;
            }
        }
    };
    private LocationUI mLocationUI;
    private SharedPreferences mPrefs;
    private NetworkState mNetWorkState;
    private View mListHeader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_list);
        checkNetWorkConnected();
        Utils.registerToEventBus(this);
        initVariables();
        startNotificationService();
        loadTasks();
    }

    private void startNotificationService() {
        if (Utils.isServiceRunning(this.getApplicationContext(), Constants.SERVICE_NAME)) {
            Log.d("acmllaugh1", "startNotificationService (line 40): Service is running now.");
            return;
        }
        // Log.d("acmllaugh1", "startNotificationService (line 44): Service is not running, we start one.");
        Intent serviceIntent = new Intent(this, TaskManagerService.class);
        ComponentName name = startService(serviceIntent);
        //Log.d("acmllaugh1", "startNotificationService (line 49): name : " + name.getClassName());
    }


    private void loadTasks() {
        if (getLoaderManager().getLoader(0) != null) {
            getLoaderManager().getLoader(0).cancelLoad();
        }
        getLoaderManager().restartLoader(0, null, mTaskLoaderCallback);
    }

    private void initVariables() {
        initUserIDFromIntent();
        mLocationUI = new LocationUI();
        mLocationManager = new LocationManager(this.getApplicationContext(), mLocationUI);
        mTaskListView = (ListView) findViewById(R.id.listview_tasks);
        mProgressLayout = (ProgressLayout) findViewById(R.id.progress_layout);
        mTaskListAdapter = new TaskListAdapter(this.getApplicationContext(),
                android.R.layout.simple_list_item_1, new ArrayList<TaskDto>());
        mListHeader = getLayoutInflater().inflate(R.layout.list_header, null);
        mTaskListView.addHeaderView(mListHeader);
        mTaskListView.setAdapter(mTaskListAdapter);
        mTaskLoaderCallback = new TaskLoaderCallback(this, mTaskListView, mTaskListAdapter, mListHeader);
        mTaskListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TaskDto task = (TaskDto) adapterView.getAdapter().getItem(i);
                mEventBus.postSticky(task);
                Intent intent = new Intent(TaskListActivity.this,
                        SingleTaskActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initUserIDFromIntent() {
        Intent intent = getIntent();
        mUserID = intent.getIntExtra(Constants.USER_ID, -1);
        if (mUserID == -1) {
            Log.d("acmllaugh1", "initVariables (line 76): userid is -1, quit activity.");
            this.finish();
        } else {
            ClientGlobal.userId = mUserID;
            Log.d("acmllaugh1", "initUserIDFromIntent (line 99): login successful, welcome");
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.task_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (!checkNetWorkConnected() && id != R.id.action_log_out) {
            return super.onOptionsItemSelected(item);
        }
        switch (id) {
            case R.id.action_sign_in:
                mProcessingDialog = Utils.showProgressDialog(mProcessingDialog, this);
                if (mUserID != -1) {
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            doSignIn();
                        }
                    });
                    thread.start();
                }
                break;
            case R.id.action_log_out:
                confirmLogout();
                break;
            case R.id.action_refresh:
                loadTasks();
                break;
        }
        if (id == R.id.action_sign_in) {

        }
        return super.onOptionsItemSelected(item);
    }

    private void confirmLogout() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.logout_alert_title))
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                doLogout();
            }
        }).create().show();
    }

    private void doLogout() {
        mProcessingDialog = Utils.showProgressDialog(mProcessingDialog, this);
        clearSavedUserID();
        tryStopService();
        Utils.dissmissProgressDialog(mProcessingDialog);
        this.finish();
    }

    private void clearSavedUserID() {
        mPrefs = getSharedPreferences(Constants.TASK_MANAGER, MODE_PRIVATE);
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putInt(Constants.SAVED_USER_ID, Constants.INVALIDATE_USER_ID);
        editor.apply();
    }

    private void tryStopService() {
        Intent serviceIntent = new Intent(this, TaskManagerService.class);
        try {
            stopService(serviceIntent);
        } catch (Exception e) {
            Log.d("acmllaugh1", "tryStopService (line 177): error stop service : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void doSignIn() {
        SignInDto dto = new SignInDto();
        Location location = mLocationManager.getCurrentLocation();
        if (location == null) {
            Log.d("acmllaugh1", "doSignIn (line 156): location is null.");
            Utils.dissmissProgressDialog(mProcessingDialog);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Utils.showToast(mToast, getString(R.string.sign_in_fail),
                            TaskListActivity.this.getApplicationContext());
                }
            });
            return;
        }
        dto.setLatitude(location.getLatitude());
        dto.setLongitude(location.getLongitude());
        dto.setTime(System.currentTimeMillis());
        dto.setType(SignInType.SignIn);
        ArrayList<SignInDto> signInActionList = new ArrayList<SignInDto>();
        signInActionList.add(dto);
        SignInResult result = mSignInHandler.signIn(signInActionList);
        Message msg = new Message();
        msg.what = SIGN_IN_RESULT;
        msg.obj = result;
        mHandler.sendMessage(msg);
    }

    public ProgressLayout getProgressLayout() {
        return mProgressLayout;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Utils.unRegisterEventBus(this);
    }

    private class LocationUI implements LocationManager.Listener {
        @Override
        public void showGpsOnScreenIndicator(boolean hasSignal) {
        }

        @Override
        public void hideGpsOnScreenIndicator() {
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mLocationManager != null)
            mLocationManager.recordLocation(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mLocationManager != null)
            mLocationManager.recordLocation(false);
    }


    public void onEvent(NetworkState state) {
        mNetWorkState = state;
        if (!state.isConnected()) {
            Utils.showToast(mToast, getString(R.string.net_work_unavailable), this);
        } else {
            loadTasks();
        }
    }

    private boolean checkNetWorkConnected() {
        //Check if there is an available network.
        mNetWorkState = Utils.getCurrentNetworkState(getApplicationContext());
        if (!mNetWorkState.isConnected()) {
            Utils.showToast(mToast, getString(R.string.net_work_unavailable), this);
            return false;
        }
        return true;
    }
}
