package com.example.xiaohai.gpsforest;

import android.graphics.Color;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.OverlayManager;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.MapView;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiaohai on 2017/3/11.
 */
public class MyRoute {
    OverlayManager overlayManager;
    List<GeoPoint> listPoint;
    public Polyline polyline;
    MapView map;

    public MyRoute(MapView map) {
        this.map = map;
        this.overlayManager = map.getOverlayManager();
        this.polyline = new Polyline();
        this.listPoint = new ArrayList<GeoPoint>();
        this.polyline.setColor(Color.BLUE);
        this.polyline.setWidth(11);
    }

    public void addPoint(GeoPoint pnt) {
        this.listPoint.add(pnt);
        if (this.listPoint.size() > 1) {
            this.polyline.setPoints(listPoint);
            this.overlayManager.remove(this.polyline);
            this.overlayManager.add(this.polyline);
            this.map.getController().setCenter(pnt);
        }
    }
}
