package com.talent.taskmanager.location;

import android.content.Context;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;

/**
 * Created by chris on 15-1-4.
 */
public class BaiduLocationManager {
    private static final String TAG = "BaiduLocationManager";
    private static final LocationMode LOCATION_MODE = LocationMode.Hight_Accuracy;
    // "gcj02": 国测局加密经纬度坐标
    // "bd09ll": 百度加密经纬度坐标
    // "bd09": 百度加密墨卡托坐标
    private static final String COORDINATE_TYPE = "bd09ll";
    private static final int UPDATE_FREQUENCY = 1000;

    private Context mContext;
    private boolean mRecordLocation;
    private LocationClient mLocationClient;
    private LocationListener mLocationListener;
    private BDLocation mBDLocation;

    public BaiduLocationManager(Context mContext) {
        this.mContext = mContext;
        initLocation();
    }

    public void recordLocation(boolean recordLocation) {
        if (mRecordLocation != recordLocation) {
            mRecordLocation = recordLocation;
            if (recordLocation) {
                startReceivingLocationUpdates();
            } else {
                stopReceivingLocationUpdates();
            }
        }
    }

    private void startReceivingLocationUpdates() {
        if (mLocationClient != null) {
            mLocationClient.registerLocationListener(mLocationListener);
            mLocationClient.start();
            Log.d(TAG, "startReceivingLocationUpdates()");
        }
    }

    private void stopReceivingLocationUpdates() {
        if (mLocationClient != null) {
            mLocationClient.stop();
            mLocationClient.unRegisterLocationListener(mLocationListener);
            Log.d(TAG, "stopReceivingLocationUpdates()");
        }
    }

    private void initLocation() {
        mLocationClient = new LocationClient(mContext);
        mLocationListener = new LocationListener();
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LOCATION_MODE);
        option.setCoorType(COORDINATE_TYPE);
        int span = UPDATE_FREQUENCY;
        option.setScanSpan(span);
        option.setIsNeedAddress(true);
        mLocationClient.setLocOption(option);
    }

    public class LocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            mBDLocation = location;
//            Log.d(TAG, "onReceiveLocation, " + "Location: (" + location.getLatitude() + ", " + location.getLongitude() + ")" + ", Time: " + location.getTime() + ", code = " + location.getLocType());
        }
    }

    public BDLocation getCurrentLocation() {
        return mBDLocation;
    }
}
