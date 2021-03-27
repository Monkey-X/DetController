package com.etek.controller.activity.project;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
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
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Map;

/**
 * 百度地图查看位置信息
 */
public class MapActivity extends BaseActivity implements View.OnClickListener {

    private TextView longitude;
    private TextView latitude;
    private MapView mMapView;
    private BaiduMap map;
    private LocationClient mLocationClient;
    private MyLocationListener myLocationListener;

    private final String TAG ="MapActivity";

    private LocationManager locationManager;

    private boolean m_bBaiduLocationValid = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        initSupportActionBar(R.string.activity_map);
        initView();
        initMap();

        initGPSLocation();
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
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);   // 高精度
        option.setOpenGps(true);                                                    // 打开gps
        option.setCoorType("bd09ll");                                               // 设置坐标类型
        option.setIsNeedAddress(true);// 位置，一定要设置，否则后面得不到地址
        option.setScanSpan(0);
        option.setNeedDeviceDirect(true);        // 返回的定位结果包含手机机头的方向
        mLocationClient.setLocOption(option);
        myLocationListener = new MyLocationListener();
        mLocationClient.registerLocationListener(myLocationListener);
        mLocationClient.start();


        longitude.setText("0.0000");
        latitude.setText("0.0000");
    }

    public class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            //mapView 销毁后不在处理新接收的位置
            if (location == null || mMapView == null) {
                return;
            }
            longitude.setText(String.format("%.4f",location.getLongitude()));
            latitude.setText(String.format("%.4f",location.getLatitude()));
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

            //  百度没定位到，返回经纬度都是0
            if((Math.abs(location.getLongitude())<0.00001)&&(Math.abs(location.getLatitude())<0.00001)){
                DetLog.writeLog(TAG,String.format("百度未定位到：%.5f,%.5f",longitude,latitude));
                return;
            }

            m_bBaiduLocationValid =true;

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

            // 经纬度缓存时间
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            java.util.Date date=new java.util.Date();
            String str=sdf.format(date);
            setStringInfo("LocationCacheTime",str);

            ToastUtils.showCustom(MapActivity.this,"已获取经纬度！");
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

        double dlongitude = Double.valueOf(longitude.getText().toString());
        double dlatitude = Double.valueOf(latitude.getText().toString());
        if((Math.abs(dlongitude)<0.00001)&&(Math.abs(dlatitude)<0.00001)){
            return;
        }

        Intent intent=new Intent();
        intent.putExtra("Longitude",longitude.getText().toString());
        intent.putExtra("Latitude",latitude.getText().toString());
        //设置结果码标识当前Activity，回传数据。不管多早调用这句代码，
        // 这句代码在当前Activity销毁时才会执行，即此Activity销毁时才会回传数据。请求码和结果码不必相同。
        setResult(1,intent);
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

        closeBaiduLocation();

        closeGPSLocation();

        mMapView.onDestroy();
        mMapView = null;
    }

    private void closeBaiduLocation(){
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        if(null!=mLocationClient){
            mLocationClient.stop();

            mLocationClient.unRegisterLocationListener(myLocationListener);
            mLocationClient = null;
        }
        myLocationListener = null;
        map.setMyLocationEnabled(false);
    }

    private void closeGPSLocation(){
        if(null!=locationManager){
            locationManager.removeUpdates(locationListener);
            locationManager = null;
        }
        locationListener = null;
    }

    private void initGPSLocation(){
        Log.d(TAG,"进入initGPSLocation");

        locationManager=(LocationManager)this.getSystemService(this.LOCATION_SERVICE);
        // 判断GPS是否正常启动
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this, "请开启GPS定位...", Toast.LENGTH_SHORT).show();
            // 返回开启GPS导航设置界面
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivityForResult(intent, 0);
            return;
        }
        Log.d(TAG,"GPS定位已开启");

        if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED){
            Log.d(TAG,"GPS定位已授权");

            // 为获取地理位置信息时设置查询条件
            String bestProvider = locationManager.getBestProvider(getCriteria(), true);
            // 获取位置信息
            // 如果不设置查询要求，getLastKnownLocation方法传人的参数为LocationManager.GPS_PROVIDER
            Location location = locationManager.getLastKnownLocation(bestProvider);

            // 监听状态
            //locationManager.addGpsStatusListener(listener);
            // 绑定监听，有4个参数
            // 参数1，设备：有GPS_PROVIDER和NETWORK_PROVIDER两种
            // 参数2，位置信息更新周期，单位毫秒
            // 参数3，位置变化最小距离：当位置距离变化超过此值时，将更新位置信息
            // 参数4，监听
            // 备注：参数2和3，如果参数3不为0，则以参数3为准；参数3为0，则通过时间来定时更新；两者为0，则随时刷新

            // 1秒更新一次，或最小位移变化超过1米更新一次；
            // 注意：此处更新准确度非常低，推荐在service里面启动一个Thread，在run中sleep(10000);然后执行handler.sendMessage(),更新位置
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, locationListener);
        }else{
            ToastUtils.show(MapActivity.this,"请在设置里授权");
        }
    }

    private Criteria getCriteria(){
        Criteria criteria = new Criteria();
        // 设置定位精确度 Criteria.ACCURACY_COARSE比较粗略，Criteria.ACCURACY_FINE则比较精细  
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        // 设置是否要求速度  
        criteria.setSpeedRequired(true);
        // 设置是否允许运营商收费  
        criteria.setCostAllowed(true);
        // 设置是否需要方位信息  
        criteria.setBearingRequired(true);
        // 设置是否需要海拔信息  
        criteria.setAltitudeRequired(true);
        // 设置对电源的需求  
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        return criteria;
    }

    private LocationListener locationListener=new LocationListener() {
        /**
         * 位置信息变化时触发
         */
        public void onLocationChanged(Location location) {
            Log.d(TAG,"GPS定位：");
            Log.i(TAG, "时间："+location.getTime());
            Log.i(TAG, "经度："+location.getLongitude());
            Log.i(TAG, "纬度："+location.getLatitude());
            Log.i(TAG, "海拔："+location.getAltitude());

            double longitude = location.getLongitude();
            double latitude = location.getLatitude();

            // 如果百度网络定位有效，不需要GPS定位
            if(m_bBaiduLocationValid){
                return;
            }

            //  百度没定位到，返回经纬度都是0
            if((Math.abs(longitude)<0.00001)&&(Math.abs(latitude)<0.00001)){
                DetLog.writeLog(TAG,String.format("GPS未定位到：%.5f,%.5f",longitude,latitude));
                return;
            }

            DetLog.writeLog(TAG,"GPS刷新本地经纬度："+longitude+","+latitude);

            //  对于离线作业，需要先缓存经纬度，然后在【离线检查】中使用
            CachePositon(location.getLongitude(),location.getLatitude());
        }

        /**
         * GPS状态变化时触发
         */
        public void onStatusChanged(String provider, int status, Bundle extras) {
            switch (status) {
                //GPS状态为可见时
                case LocationProvider.AVAILABLE:
                    Log.i(TAG, "当前GPS状态为可见状态");
                    break;
                //GPS状态为服务区外时
                case LocationProvider.OUT_OF_SERVICE:
                    Log.i(TAG, "当前GPS状态为服务区外状态");
                    break;
                //GPS状态为暂停服务时
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    Log.i(TAG, "当前GPS状态为暂停服务状态");
                    break;
            }
        }

        /**
         * GPS开启时触发
         */
        public void onProviderEnabled(String provider) {
            if(ContextCompat.checkSelfPermission(MapActivity.this,android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                Location location = locationManager.getLastKnownLocation(provider);
            }
        }

        /**
         * GPS禁用时触发
         */
        public void onProviderDisabled(String provider) {

        }
    };
}