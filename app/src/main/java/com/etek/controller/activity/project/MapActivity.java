package com.etek.controller.activity.project;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.etek.controller.R;
import com.etek.controller.hardware.util.DetLog;
import com.etek.sommerlibrary.activity.BaseActivity;
import com.etek.sommerlibrary.utils.ToastUtils;

import android.util.Log;

/**
 * 百度地图查看位置信息
 */
public class MapActivity extends BaseActivity implements View.OnClickListener {

    private TextView longitude;
    private TextView latitude;
    private MapView mMapView;
    private BaiduMap map;
    private LocationClient mLocationClient;

    private final String TAG ="MapActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        initSupportActionBar(R.string.activity_map);
        initView();
        initMap();
    }

    private void initView() {
        longitude = findViewById(R.id.longitude);
        latitude = findViewById(R.id.latitude);
        mMapView = (MapView) findViewById(R.id.bmapView);
        map = mMapView.getMap();
        View refresh = findViewById(R.id.refresh);
        refresh.setOnClickListener(this);
    }

    private void initMap() {
        map.setMyLocationEnabled(true);
        map.setCompassEnable(true);
        MapStatus.Builder builder = new MapStatus.Builder();
        builder.zoom(16.0f);
        map.setMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
        //定位初始化
        //  mLocationClient = new LocationClient(this);
        //  必须用getApplicationContext()，否则其他地方使用MapActivity会报错
        mLocationClient = new LocationClient(getApplicationContext());
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(0);
        mLocationClient.setLocOption(option);
        MyLocationListener myLocationListener = new MyLocationListener();
        mLocationClient.registerLocationListener(myLocationListener);
        mLocationClient.start();
    }

    public class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            //mapView 销毁后不在处理新接收的位置
            if (location == null || mMapView == null) {
                return;
            }
            longitude.setText(location.getLongitude()+"");
            latitude.setText(location.getLatitude()+"");
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(location.getDirection()).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            map.setMyLocationData(locData);
            // 显示定位到的位置
            LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
            MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(latLng);
            map.animateMapStatus(update);

            //  对于离线作业，需要先缓存经纬度，然后在【离线检查】中使用
            CachePositon(location.getLongitude(),location.getLatitude());
        }
    }

    private void CachePositon(double longitude,double latitude){
        Intent intent = getIntent();
        String  strCacheLocation =intent.getStringExtra("cachePositon");
        if(TextUtils.isEmpty(strCacheLocation)){
            Log.d(TAG,"不缓存经纬度");
            return;
        }

        if(strCacheLocation.toUpperCase().equals("CACHE")){
            DetLog.writeLog(TAG, String.format("缓存经纬度：%.4f,%.4f", longitude, latitude));
            setStringInfo("Longitude", longitude + "");
            setStringInfo("Latitude", latitude + "");
            ToastUtils.showCustom(MapActivity.this,"经纬度缓存成功！");
        }else{
            Log.d(TAG,"不缓存经纬度");
        }

    }
    @Override
    public void onClick(View v) {
        // 刷新地图
        if (mLocationClient!=null) {
            mLocationClient.start();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mLocationClient.stop();
        map.setMyLocationEnabled(false);
        mMapView.onDestroy();
        mMapView = null;
    }

}