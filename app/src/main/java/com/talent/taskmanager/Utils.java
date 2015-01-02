package com.talent.taskmanager;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.coal.black.bc.socket.client.ClientGlobal;
import com.coal.black.bc.socket.utils.CommonUtils;
import com.talent.taskmanager.network.NetworkState;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by acmllaugh on 14-11-21.
 */
public class Utils {

    private static EventBus mEventBus = EventBus.getDefault();

    private Utils() {
        // This class should not be initialize or have sub classes.
        throw new AssertionError();
    }

    /**
     *  Check if service is running.
     * */
    public static boolean isServiceRunning(Context context, String serviceClassName){
        final ActivityManager activityManager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        final List<ActivityManager.RunningServiceInfo> services = activityManager.getRunningServices(Integer.MAX_VALUE);
        for (ActivityManager.RunningServiceInfo runningServiceInfo : services) {
            if (runningServiceInfo.service.getClassName().equals(serviceClassName)){
                return true;
            }
        }
        return false;
    }

    public static void log(String className, String logText) {
        if (Constants.IN_DEBUG_MODE) {
            Log.d("acmllaugh1", className + " : " + logText);
        }
    }

    public static Toast showToast(Toast toast, String message, Context context) {
        if (toast == null) {
            toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        }else {
            toast.setText(message);
        }
        toast.show();
        return toast;
    }

    public static void registerToEventBus(Activity activity) {
        if (!mEventBus.isRegistered(activity)) {
            try {
                mEventBus.register(activity);
            } catch (Exception e) {
                Log.d("acmllaugh1", "registerToEventBus (line 60): register to event bus error.");
                e.printStackTrace();
            }
        }
    }

    public static void unRegisterEventBus(Activity activity) {
        if (mEventBus.isRegistered(activity)) {
            try {
                mEventBus.unregister(activity);
            } catch (Exception e) {
                Log.d("acmllaugh1", "registerToEventBus (line 60): unregister to event bus error.");
                e.printStackTrace();
            }
        }
    }


    public static NetworkState getCurrentNetworkState(Context context) {
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mMobile = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        boolean wifiConnected = false;
        boolean mobileConnected = false;
        boolean networkConnected;
        if (mWifi.isConnected()) {
            wifiConnected = true;
        }
        if (mMobile.isConnected()) {
            mobileConnected = true;
        }
        networkConnected = wifiConnected || mobileConnected;
        return new NetworkState(networkConnected, wifiConnected, mobileConnected);
    }


    public static ProgressDialog showProgressDialog(ProgressDialog dialog, Activity activity) {
        if (dialog == null) {
            dialog = ProgressDialog.show(activity, null, activity.getApplicationContext().getString(R.string.please_wait));
            dialog.setCancelable(false);
        } else if (!dialog.isShowing()) {
            dialog.show();
        }
        return dialog;
    }

    public static void dissmissProgressDialog(ProgressDialog dialog) {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    public static void getMACAddress(SharedPreferences preferences, Context context) {
        //We get mac address if there is not one saved in preference.
        if (preferences == null) {
            preferences = context.getSharedPreferences(Constants.TASK_MANAGER, Context.MODE_PRIVATE);
        }
        String macAddress = preferences.getString(Constants.MAC_ADDRESS, null);
        if (macAddress == null) {
            //Get mac address from system and save it to preference.
            WifiManager manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo info = manager.getConnectionInfo();
            macAddress = info.getMacAddress();
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(Constants.MAC_ADDRESS, macAddress);
            editor.apply();
        }
        ClientGlobal.setMacAddress(macAddress);
    }

    /**
     * Check if sd card is available
     * @return
     */
    public static boolean isSDCardAvailable() {
        String sdStatus = Environment.getExternalStorageState();
        if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) {
            return false;
        }
        return true;
    }

    public static String getImageName(long time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String currentTime = format.format(new Date(time));
        return "IMG_" + currentTime + ".jpg";
    }

    public static String getAudioName(long time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String currentTime = format.format(new Date(time));
        return "Audio_" + currentTime + ".amr";
    }
}
