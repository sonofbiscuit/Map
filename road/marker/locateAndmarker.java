package com.example.zq.gd1;


import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMap.OnMapClickListener;
import com.amap.api.maps.AMap.OnMarkerClickListener;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements OnMapClickListener,
        OnMarkerClickListener {

    MapView mMapView = null;
    public PolylineOptions polyline;
    public Marker curShowWindowMarker;
    private MapView mapView;
    private AMap aMap;
    private Context mContext;
    private RouteSearch mRouteSearch;
    private RideRouteResult mRideRouteResult;
    private LatLonPoint mStartPoint = new LatLonPoint(39.942295, 116.335891);//起点，116.335891,39.942295
    private LatLonPoint mEndPoint = new LatLonPoint(39.995576, 116.481288);//终点，116.481288,39.995576
    private final int ROUTE_TYPE_RIDE = 4;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //获取地图控件引用
        mMapView = (MapView) findViewById(R.id.map);
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        mMapView.onCreate(savedInstanceState);
        init();


        MyLocationStyle myLocationStyle;
        myLocationStyle = new MyLocationStyle();
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER);//连续定位、蓝点不会移动到地图中心点，定位点依照设备方向旋转，并且蓝点会跟随设备移动
        myLocationStyle.interval(2000); //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        aMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
        aMap.getUiSettings().setMyLocationButtonEnabled(true);//设置默认定位按钮是否显示，非必需设置。
        aMap.setMyLocationEnabled(true);// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false


//标记显示
        LatLng xiAn = new LatLng(34.341568, 104.064855);//第一个参数是：latitude，第二个参数是longitude
        LatLng xiAn1 = new LatLng(39.999391, 114.135972);//第一个参数是：latitude，第二个参数是longitude
        LatLng xiAn2 = new LatLng(46.999391, 124.135972);//第一个参数是：latitude，第二个参数是longitude
        LatLng xiAn3 = new LatLng(50.999391, 127.135972);//第一个参数是：latitude，第二个参数是longitude
        //添加标记

        final MarkerOptions markerOption = new MarkerOptions();
        markerOption.position(xiAn)
                .title("position1").snippet("position：34.341568, 108.940174")
                .draggable(false)//设置Marker可拖动
                .icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.b)))
                // 将Marker设置为贴地显示，可以双指下拉地图查看效果
                .setFlat(true);//设置marker平贴地图效果
        final Marker marker_xian = aMap.addMarker(markerOption);

        //添加标记
        final MarkerOptions markerOption1 = new MarkerOptions();
        markerOption1.position(xiAn1)
                .title("position2").snippet("2")
                .draggable(false)//设置Marker可拖动
                .icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.b)))
                .setFlat(true);
        final Marker marker_xian1 = aMap.addMarker(markerOption1);

        final MarkerOptions markerOption2 = new MarkerOptions();
        markerOption2.position(xiAn2)
                .title("position3").snippet("3")
                .draggable(false)//设置Marker可拖动
                .icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.b)))
                .setFlat(true);
        final Marker marker_xian2 = aMap.addMarker(markerOption2);

        final MarkerOptions markerOption3 = new MarkerOptions();
        markerOption3.position(xiAn3)
                .title("position4").snippet("4")
                .draggable(false)//设置Marker可拖动
                .icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.b)))
                .setFlat(true);
        final Marker marker_xian3 = aMap.addMarker(markerOption3);


        //设置线颜色，宽度
        //起点位置和  地图界面大小控制
        List<LatLng> latLngs = new ArrayList<LatLng>();
        latLngs.add(xiAn);
        latLngs.add(xiAn1);
        latLngs.add(xiAn2);
        latLngs.add(xiAn3);
        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngs.get(0), 4));
        aMap.setMapTextZIndex(2);
        aMap.addPolyline((new PolylineOptions())
                //集合数据
                .addAll(latLngs)
                //线的宽度
                .width(10).setDottedLine(true).geodesic(true)
                //颜色
                .color(Color.argb(255, 255, 20, 147)));

        //aMap.setOnMarkerClickListener(this);
        aMap.setOnMapClickListener(this);//监听事件

    }

    private void init() {
        if (aMap == null) {
            aMap = mapView.getMap();
        }
    }





    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mMapView.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mMapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        mMapView.onSaveInstanceState(outState);
    }

    public boolean onMarkerClick(Marker marker) {
        curShowWindowMarker = marker;
        return true;
    }

    public void onMapClick(LatLng latLng) {
        //点击其它地方隐藏infoWindow
        if (curShowWindowMarker != null) {
            curShowWindowMarker.hideInfoWindow();
        }
    }

}