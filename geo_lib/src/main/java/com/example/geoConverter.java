package com.example;

public class geoConverter  {

    static  double LAT_OFFSET_0(double x, double y) {
        return -100.0 + 2.0 * x + 3.0 * y + 0.2 * y * y + 0.1 * x * y + 0.2 * Math.sqrt(Math.abs(x));
    }

    static  double LAT_OFFSET_1(double x, double y) {
        return (20.0 * Math.sin(6.0 * x * Math.PI) + 20.0 * Math.sin(2.0 * x * Math.PI)) * 2.0 / 3.0;
    }

    static  double LAT_OFFSET_2(double x, double y) {
        return (20.0 * Math.sin(y * Math.PI) + 40.0 * Math.sin(y / 3.0 * Math.PI)) * 2.0 / 3.0;
    }

    static  double LAT_OFFSET_3(double x, double y) {
        return (160.0 * Math.sin(y / 12.0 * Math.PI) + 320 * Math.sin(y * Math.PI / 30.0)) * 2.0 / 3.0;
    }

    static  double LON_OFFSET_0(double x, double y) {
        return 300.0 + x + 2.0 * y + 0.1 * x * x + 0.1 * x * y + 0.1 * Math.sqrt(Math.abs(x));
    }

    static  double LON_OFFSET_1(double x, double y) {
        return (20.0 * Math.sin(6.0 * x * Math.PI) + 20.0 * Math.sin(2.0 * x * Math.PI)) * 2.0 / 3.0;
    }

    static  double LON_OFFSET_2(double x, double y) {
        return (20.0 * Math.sin(x * Math.PI) + 40.0 * Math.sin(x / 3.0 * Math.PI)) * 2.0 / 3.0;
    }

    static  double LON_OFFSET_3(double x, double y) {
        return (150.0 * Math.sin(x / 12.0 * Math.PI) + 300.0 * Math.sin(x / 30.0 * Math.PI)) * 2.0 / 3.0;
    }

    static  double RANGE_LON_MAX = 137.8347;
    static  double RANGE_LON_MIN = 72.004;
    static  double RANGE_LAT_MAX = 55.8271;
    static  double RANGE_LAT_MIN = 0.8293;

    static  double jzA  = 6378245.0;
    static  double jzEE = 0.00669342162296594323;

    static  double transformLat(double x, double y) {
        double ret = LAT_OFFSET_0(x, y);
        ret += LAT_OFFSET_1(x, y);
        ret += LAT_OFFSET_2(x, y);
        ret += LAT_OFFSET_3(x, y);
        return ret;
    }

    static  double transformLon(double x, double y) {
        double ret = LON_OFFSET_0(x, y);
        ret += LON_OFFSET_1(x, y);
        ret += LON_OFFSET_2(x, y);
        ret += LON_OFFSET_3(x, y);
        return ret;
    }

    static  boolean outOfChina(double lat, double lon) {
        if (lon < RANGE_LON_MIN || lon > RANGE_LON_MAX)
            return true;
        if (lat < RANGE_LAT_MIN || lat > RANGE_LAT_MAX)
            return true;
        return false;
    }

    public static    myLatLng toGooglePoint(double ggLat, double ggLon) {
        return toGooglePoint2(ggLat,ggLon);
    }

   static   myLatLng toGooglePoint2(double ggLat, double ggLon)
    {
        myLatLng resPoint = new myLatLng();
        double mgLat;
        double mgLon;
        if (outOfChina(ggLat, ggLon)) {
            resPoint.latitude = ggLat;
            resPoint.longitude = ggLon;
            return resPoint;
        }
        double dLat = transformLat(ggLon - 105.0, ggLat - 35.0);
        double dLon = transformLon(ggLon - 105.0, ggLat - 35.0);
        double radLat = ggLat / 180.0 * Math.PI;
        double magic = Math.sin(radLat);
        magic = 1 - jzEE * magic * magic;
        double sqrtMagic = Math.sqrt(magic);
        dLat = (dLat * 180.0) / ((jzA * (1 - jzEE)) / (magic * sqrtMagic) * Math.PI);
        dLon = (dLon * 180.0) / (jzA / sqrtMagic * Math.cos(radLat) * Math.PI);
        mgLat = ggLat + dLat;
        mgLon = ggLon + dLon;

        resPoint.latitude = mgLat;
        resPoint.longitude = mgLon;
        return resPoint;
    }
}