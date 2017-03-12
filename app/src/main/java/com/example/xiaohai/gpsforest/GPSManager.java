package com.example.xiaohai.gpsforest;

        import java.util.Set;

        import android.content.Context;
        import android.location.Location;
        import android.location.LocationListener;
        import android.location.LocationManager;
        import android.location.LocationProvider;
        import android.os.Bundle;
        import android.util.Log;

        import com.example.xiaohai.gpsforest.MyEvent.EventSourceObject;


public class GPSManager implements LocationListener {
    /**
     * define a Context object
     */
    private static Context context;
    /**
     * define a GPSManager object to manage  GPS location
     */
    private static GPSManager gpsManager = null;
    /**
     * define a LoationManager object to manage GPS function
     */
    private static LocationManager mLocationManager;

    public EventSourceObject GetResEvent = new EventSourceObject();

    /**

     */
    private  GPSManager(Context context) {
        GPSManager.context = context;
    }

    public static GPSManager instance(Context context) {
        GPSManager.context = context;
        if (gpsManager == null) {
            gpsManager = new GPSManager(context);
        }
        return gpsManager;
    }

    public void startGpsLocate() {
        mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3 * 1000, 2, this);
    }

    @Override
    public void onLocationChanged(Location location) {
        double x = location.getLongitude();
        double y = location.getLatitude();
        GetResEvent.setString(x + "," + y);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        switch (status) {
            //GPS״̬Ϊ�ɼ�ʱ
            case LocationProvider.AVAILABLE:
                Log.d("gpsstate", "gps is ok");
                break;
            //GPS״̬Ϊ��������ʱ
            case LocationProvider.OUT_OF_SERVICE:
                Log.d("gpsstate", "gps is out service");
                break;
            //GPS״̬Ϊ��ͣ����ʱ
            case LocationProvider.TEMPORARILY_UNAVAILABLE:
                Log.d("gpsstate", "gps is stop");
                break;
        }
    }

    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub
        Log.d("gpsstate", "gps is enabled");
    }

    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub
        Log.d("gpsstate", "gps is onProviderDisabled");
    }

    public boolean isEnabled() {
        if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
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