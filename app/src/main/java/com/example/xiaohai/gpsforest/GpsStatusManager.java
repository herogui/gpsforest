package com.example.xiaohai.gpsforest;

import android.content.Context;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.LocationManager;
import android.util.Log;
import android.widget.Toast;


import com.example.xiaohai.gpsforest.MyEvent.EventSourceObject;

import java.util.Iterator;

/**
 * Created by giser on 2015/6/9.
 */
public class GpsStatusManager{
    private static Context context;
    private static GpsStatusManager gpsStatusManager = null;
    private static LocationManager mLocationManager;
    public EventSourceObject GetResEvent = new EventSourceObject();
    private  GpsStatusManager(Context context) {
        GpsStatusManager.context = context;
    }

    public static GpsStatusManager instance(Context context) {
        GpsStatusManager.context = context;
        if (gpsStatusManager == null) {
            gpsStatusManager = new GpsStatusManager(context);
        }
        return gpsStatusManager;
    }

    public void start() {
        mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        mLocationManager.addGpsStatusListener(statusListener);//����GPS״̬
    }

    public void stop()
    {
        mLocationManager.removeGpsStatusListener(statusListener);
    }

    private GpsStatus.Listener statusListener = new GpsStatus.Listener()
    {
        public void onGpsStatusChanged(int event)
        {
            // TODO Auto-generated method stub
            GpsStatus gpsStatus= mLocationManager.getGpsStatus(null);
            switch(event)
            {
                case GpsStatus.GPS_EVENT_FIRST_FIX:{

                    int i=gpsStatus.getTimeToFirstFix();

                    //GetResEvent.setString("GPS first run");
                    break;
                }

                case GpsStatus.GPS_EVENT_SATELLITE_STATUS:{

                    Iterable<GpsSatellite> allSatellites = gpsStatus.getSatellites();
                    Iterator<GpsSatellite> iterator = allSatellites.iterator();
                    int numOfSatellites = 0;
                    int maxSatellites=gpsStatus.getMaxSatellites();

                    while(iterator.hasNext() && numOfSatellites<maxSatellites){
                        numOfSatellites++;
                        GpsSatellite gps = iterator.next();
                        float ft=  gps.getElevation();
                    }

                    GetResEvent.setString(String.valueOf(numOfSatellites));
                    break;
                }
                case GpsStatus.GPS_EVENT_STARTED:{

                    Log.d("gpsstate", "GPS run");
                    //GetResEvent.setString("gps run");
                    break;
                }
                case GpsStatus.GPS_EVENT_STOPPED:{

                    Log.d("gpsstate", "GPS stop");
                   // GetResEvent.setString("gps stop");
                    break;
                }
                default :
                    break;
            }
        }
    };
}
