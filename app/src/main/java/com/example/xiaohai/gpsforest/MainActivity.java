package com.example.xiaohai.gpsforest;


        import android.app.Activity;
        import android.content.Context;
        import android.content.Intent;

        import android.location.LocationManager;
        import android.net.ConnectivityManager;
        import android.net.NetworkInfo;

        import android.preference.PreferenceManager;
        import android.provider.Settings;

        import android.os.Bundle;

        import android.view.View;
        import android.widget.TextView;
        import android.widget.Toast;


        import com.example.geoConverter;
        import com.example.myLatLng;
        import com.example.xiaohai.gpsforest.MyEvent.CusEvent;
        import com.example.xiaohai.gpsforest.MyEvent.CusEventListener;
        import com.example.xiaohai.gpsforest.MyEvent.EventSourceObject;


        import org.osmdroid.api.IMapController;
        import org.osmdroid.config.Configuration;
        import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
        import org.osmdroid.util.GeoPoint;
        import org.osmdroid.views.MapView;
        import org.osmdroid.views.overlay.ItemizedIconOverlay;
        import org.osmdroid.views.overlay.ItemizedOverlay;

        import org.osmdroid.views.overlay.OverlayItem;

        import java.util.ArrayList;

public class MainActivity extends Activity {

    MapView map;
    private IMapController mapController;
    GPSManager gps;
    GpsStatusManager gpsStatus;
    NetworkLbsManager NetLbs;
    ItemizedOverlay<OverlayItem> currentItemItemizedOverlay;
    TextView txtGPSState;
    MyRoute route;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context ctx = getApplicationContext();

        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        setContentView(R.layout.activity_main);

        initUI();


        map = (MapView) findViewById(R.id.map);
        route = new MyRoute(this.map);

        //定位
        gpsStatus = GpsStatusManager.instance(MainActivity.this);

        if(openGPSSettings()) {
            gps = GPSManager.instance(MainActivity.this);
            NetLbs = NetworkLbsManager.instance(MainActivity.this);
            NetLbs.GetResEventInit.addCusListener(cusEventLbs);
            NetLbs.startGpsLocate();
        }

        if(!isNetworkAvailable(MainActivity.this))
        {
           // Toast.makeText(MainActivity.this,"请打开网络，用于初始化地图位置!", Toast.LENGTH_LONG).show();
            GeoPoint gp5 = new GeoPoint(23.176874,113.41555);
            final ArrayList<OverlayItem> items = new ArrayList<>();
            items.add(new OverlayItem("Hannover", "SampleDescription",gp5));
            AddOverLay(items);
        }
    }

    void initUI()
    {
        txtGPSState = (TextView)this.findViewById(R.id.txtGPSState);
        txtGPSState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addLine2();
            }
        });
    }

    int n = 0;
    void  addLine2()
    {
        n++;
        GeoPoint gp5 = new GeoPoint(31.15235+0.001*n,121.48940+0.001*n);
        final ArrayList<OverlayItem> items = new ArrayList<>();
        items.add(new OverlayItem("Hannover", "SampleDescription",gp5));
        AddOverLay(items);
    }

    void initMap(GeoPoint center)
    {
        map.setUseDataConnection(false);
        map.setTileSource(TileSourceFactory.getTileSource("Mapnik"));

        mapController = map.getController();

        map.setMultiTouchControls(true);
        map.setClickable(true);
        map.setUseDataConnection(false);

        mapController.setZoom(17);
        mapController.setCenter(center);
    }

    CusEventListener cusEventLbs =   new CusEventListener() {
        @Override
        public void fireCusEvent(CusEvent e) {
            EventSourceObject eObject = (EventSourceObject) e.getSource();
            String res =eObject.getString();

            //Toast.makeText(MainActivity.this,"lbs"+  res, Toast.LENGTH_LONG).show();

            double lon = Double.parseDouble(res.split(",")[0]);
            double lat = Double.parseDouble(res.split(",")[1]);

            myLatLng latLng =  geoConverter.toGooglePoint(lat, lon);
            GeoPoint  center = new GeoPoint(latLng.getLatitude(),latLng.getLongitude());
            initMap(center);

            //marker
            final ArrayList<OverlayItem> overlayItems = new ArrayList<>();
            overlayItems.add(new OverlayItem("Hannover", "SampleDescription",center));
            AddOverLay(overlayItems);

            //获取初始化的位置后要停止监听
            NetLbs.GetResEvent.removeListener(cusEventLbs);
            NetLbs.closeGpsLocate();

            gps.GetResEvent.addCusListener(cusEventGPS);
            gps.startGpsLocate();

            gpsStatus.GetResEvent.addCusListener(cusEventGPSStatus);
            gpsStatus.start();
        }
    };

    CusEventListener cusEventGPSStatus =   new CusEventListener() {
        @Override
        public void fireCusEvent(CusEvent e) {
            EventSourceObject eObject = (EventSourceObject) e.getSource();
            String res = eObject.getString();
            txtGPSState.setText("接收到"+res+"个gps卫星！");
            //Toast.makeText(MainActivity.this,res, Toast.LENGTH_SHORT).show();
        }
    };

    CusEventListener cusEventGPS =   new CusEventListener() {
        @Override
        public void fireCusEvent(CusEvent e) {
            EventSourceObject eObject = (EventSourceObject) e.getSource();
            String res = eObject.getString();
            Toast.makeText(MainActivity.this,"gps  " +res, Toast.LENGTH_SHORT).show();
            double lon = Double.parseDouble(res.split(",")[0]);
            double lat = Double.parseDouble(res.split(",")[1]);

            myLatLng  latLng =  geoConverter.toGooglePoint(lat, lon);
            GeoPoint  center = new GeoPoint(latLng.getLatitude(),latLng.getLongitude());

            route.addPoint(center);

            final ArrayList<OverlayItem> overlayItems = new ArrayList<>();
            overlayItems.add(new OverlayItem("Hannover", "SampleDescription",center));
            AddOverLay(overlayItems);
        }
    };

    void AddOverLay(ArrayList<OverlayItem> items)
    {

			/* OnTapListener for the Markers, shows a simple Toast. */
        ItemizedOverlay<OverlayItem> itemItemizedOverlay = new ItemizedIconOverlay<>(items,
                new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
                    @Override
                    public boolean onItemSingleTapUp(final int index, final OverlayItem item) {
                        Toast.makeText(
                                MainActivity.this,
                                "Item '" + item.getTitle() + "' (index=" + index
                                        + ") got single tapped up", Toast.LENGTH_LONG).show();
                        return true; // We 'handled' this event.
                    }

                    @Override
                    public boolean onItemLongPress(final int index, final OverlayItem item) {
                        Toast.makeText(
                                MainActivity.this,
                                "Item '" + item.getTitle() + "' (index=" + index
                                        + ") got long pressed", Toast.LENGTH_LONG).show();
                        return false;
                    }
                }, MainActivity.this.getApplicationContext());

        //清楚当前的
        if(currentItemItemizedOverlay!=null)
        map.getOverlayManager().remove(currentItemItemizedOverlay);

        this.map.getOverlays().add(itemItemizedOverlay);
        currentItemItemizedOverlay = itemItemizedOverlay;
    }

    private boolean openGPSSettings() {
        LocationManager alm = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
        if (alm.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
            //Toast.makeText(this, "GPS模块正常" ,Toast.LENGTH_SHORT) .show();
            return true;
        }
        Toast.makeText(this, "请开启GPS！", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        this.startActivityForResult(intent, 0); //此为设置完成后返回到获取界面
        return  false;
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null && info.isConnected())
            {
                // 当前网络是连接的
                if (info.getState() == NetworkInfo.State.CONNECTED)
                {
                    // 当前所连接的网络可用
                    return true;
                }
        }
        }
        return false;
    }

    public void onResume() {
        super.onResume();

        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
    }

    public  void onStop()
    {
        super.onStop();
        this.gpsStatus.stop();
        this.gps.closeGpsLocate();
    }
}
