package com.example.xiaohai.gpsforest;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;


import com.example.xiaohai.gpsforest.MyEvent.EventSourceObject;


public class NetworkLbsManager implements LocationListener {
    /**
     * define a Context object
     */
    private static Context context;
    /**
     * define a GPSManager object to manage  GPS location
     */
    private static NetworkLbsManager gpsManager = null;
    /**
     * define a LoationManager object to manage GPS function
     */
    private static LocationManager mLocationManager;

    public EventSourceObject GetResEvent = new EventSourceObject();
    public EventSourceObject GetResEventInit = new EventSourceObject();

    /**
     */
    private NetworkLbsManager(Context context) {
        NetworkLbsManager.context = context;
    }

    public static NetworkLbsManager instance(Context context) {
        NetworkLbsManager.context = context;
        if (gpsManager == null) {
            gpsManager = new NetworkLbsManager(context);
        }
        return gpsManager;
    }

    public void startGpsLocate() {
//        mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 3 * 1000, 2, this);
    }

    @Override
    public void onLocationChanged(Location location) {
        double x = location.getLongitude();
        double y = location.getLatitude();
        GetResEvent.setString(x + "," +y);
        GetResEventInit.setString(x + "," +y);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub
        Log.d("jfttcjl", "gps is enabled");
        this.startGpsLocate();
    }

    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub
    }

    public boolean isEnabled() {
        if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Log.d("jfttcjl", "gps is on");
            return true;
        } else {
            return false;
        }
    }

    public void closeGpsLocate() {
        if (mLocationManager != null) {
            mLocationManager.removeUpdates(this);
            mLocationManager = null;
        }
    }
}