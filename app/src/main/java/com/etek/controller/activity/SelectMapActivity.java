package com.etek.controller.activity;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.Poi;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.CircleOptions;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.etek.controller.R;
import com.etek.controller.persistence.DBManager;
import com.etek.controller.persistence.entity.ForbiddenZoneEntity;
import com.etek.controller.persistence.entity.PermissibleZoneEntity;
import com.etek.controller.persistence.entity.ProjectInfoEntity;
import com.etek.controller.persistence.gen.ProjectInfoEntityDao;
import com.etek.sommerlibrary.activity.BaseActivity;
import com.etek.sommerlibrary.utils.ToastUtils;

import com.elvishew.xlog.XLog;

import java.util.List;


public class SelectMapActivity extends BaseActivity {

    /**
     * MapView 是地图主控件
     */
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private Marker mMarkerB;

    LatLng point;
    boolean isPositioning = false;
//    private InfoWindow mInfoWindow;



    BitmapDescriptor my = BitmapDescriptorFactory
            .fromResource(R.drawable.icon_gcoding);
    private LocationClient mLocClient;




    private void setMarkPoint(double latitude, double longitude) {
        //定义Maker坐标点
        mBaiduMap.clear();
        point = new LatLng(latitude, longitude);
//构建Marker图标

        Bundle mBundle = new Bundle(); //用来传值 也可以识别点击的是哪一个marker
        mBundle.putString("title", "第"  + "个marker");
        mBundle.putDouble("lat", latitude);
        mBundle.putDouble("lng", longitude);


//构建MarkerOption，用于在地图上添加Marker
        OverlayOptions option = new MarkerOptions()
                .extraInfo(mBundle) //这里bundle 跟maker关联上
                .position(point)
                .icon(my);
//在地图上添加Marker，并显示
        mBaiduMap.addOverlay(option);
        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @SuppressLint({"DefaultLocale", "SetTextI18n"})
            @Override
            public boolean onMarkerClick(Marker marker) {
                Bundle extraInfo = marker.getExtraInfo(); //通过这个方法获取到，你前面塞的值
                String title = extraInfo.getString("title");
                double lat = extraInfo.getDouble("lat");
                double lng = extraInfo.getDouble("lng");
                TextView showInfo = new TextView(getApplicationContext());
        showInfo.setBackgroundResource(R.drawable.popup);
        showInfo.setText(String.format("%.6f", lng)+" , "+String.format("%.6f", lat));
        InfoWindow mInfoWindow = new InfoWindow(showInfo, point, -1);
        mBaiduMap.showInfoWindow(mInfoWindow);
//                showToast(title + " ---- " + lat + "----" + lng);
//                Toast.makeText(MainActivity.this, title + " ---- " + lat + "----" + lng, Toast.LENGTH_SHORT).show();
                return false;
            }
        });

//        TextView showInfo = new TextView(getApplicationContext());
//        showInfo.setBackgroundResource(R.drawable.popup);
//
//        showInfo.setText(String.format("%.6f", latitude)+" , "+String.format("%.6f", longitude));
//        InfoWindow mInfoWindow = new InfoWindow(showInfo, point, -1);
//        mBaiduMap.showInfoWindow(mInfoWindow);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pro_map);
        initSupportActionBar(R.string.title_activity_selete_map);

        mMapView = findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();


        getBaiduLocation();

//        getBaiduLocation();
        BaiduMap.OnMapClickListener listener = new BaiduMap.OnMapClickListener() {
            /**
             * 地图单击事件回调函数
             *
             * @param point 点击的地理坐标
             */
            @Override
            public void onMapClick(LatLng point) {
//                clearOverlay(mMapView);
                XLog.v( "onMapClick latitude:  "+point.latitude+" longitude: " +point.longitude  );//经度
//                XLog.v(  "onMapClick longitude: "+);//纬度
                setMarkPoint(point.latitude,point.longitude);
//                MarkerOptions ooB = new MarkerOptions().position(point).icon(my)
//                        .zIndex(5);
//                mMarkerB = (Marker) (mBaiduMap.addOverlay(ooB));
            }

            /**
             * 地图内 Poi 单击事件回调函数
             *
             * @param mapPoi 点击的 poi 信息
             * @return
             */
            @Override
            public boolean onMapPoiClick(MapPoi mapPoi) {
                XLog.v(  " getName: "+ mapPoi.getName() );
                XLog.v(  " getUid: "+ mapPoi.getUid() );
                XLog.v(  " getPosition: "+ mapPoi.getPosition() );
                return false;
            }
        };//点击获取经纬度；
        mBaiduMap.setOnMapClickListener(listener);
        mBaiduMap.setMyLocationEnabled(true);


        

    }






    /**
     * 清除所有Overlay
     *
     * @param view
     */
    public void clearOverlay(View view) {
        mBaiduMap.clear();

        mMarkerB = null;

    }

    void getBaiduLocation() {
//         MyLocationListenner   myListener = new MyLocationListenner();
        // 定位初始化
//        mLocClient = new LocationClient(mContext);
//        mLocClient.registerNotifyLocationListener(
//                location -> {
//                    XLog.d(getLocation(location));
////                    XLog.d("location:",JSON.toJSONString(location));
//                    if (location.getLocType() == BDLocation.TypeServerError
//                            || location.getLocType() == BDLocation.TypeOffLineLocationFail) {
//                        showStatusDialog("定位失败：" + location.getLocType());
//                        return;
//                    }
//
//
////                    isLocation = true;
////                        XLog.d(LOG_TAG,location.getCity() + "location" + location.getStreet() + "--" + location.getAddrStr() + "---" + location.getStreetNumber());
//                    mLocClient.stop();
////                    mLocation = location;
////                    tvLocation.setText(location.getLongitude() + " , " + location.getLatitude());
////                    tvLocation.setTextColor(getMyColor(R.color.colorPrimary));
////                    detController.setLatitude(mLocation.getLatitude());
////                    detController.setLongitude(mLocation.getLongitude());
//
//                    setStringInfo("Longitude", location.getLongitude() + "");
//                    setStringInfo("Latitude", location.getLatitude() + "");
//
//                });
//

        // 定位初始化
        mLocClient = new LocationClient(mContext);
        mLocClient.registerNotifyLocationListener(
                location -> {
                    isPositioning = false;
                    mLocClient.stop();
                    if (location == null){
                        XLog.w("返回值为空！");
                        return;
                    }
                    if (location.getLocType() == BDLocation.TypeServerError
                            || location.getLocType() == BDLocation.TypeOffLineLocationFail) {
                        showStatusDialog("定位失败：" + location.getLocType());
                        return;
                    }

                    XLog.d(getLocation(location));



                    LatLng llB = new LatLng(location.getLatitude(), location.getLongitude());
                    CoordinateConverter converter = new CoordinateConverter()
                            .from(CoordinateConverter.CoordType.COMMON)
                            .coord(llB);
                    LatLng desLatLng = converter.convert();
                    MarkerOptions ooB = new MarkerOptions().position(desLatLng).icon(my)
                            .zIndex(5);
                    mMarkerB = (Marker) (mBaiduMap.addOverlay(ooB));

                    MapStatus mMapStatus = new MapStatus.Builder()
                            .target(desLatLng)
                            .zoom(15)
                            .build();
                    MapStatusUpdate msu = MapStatusUpdateFactory.newMapStatus(mMapStatus);
                    mBaiduMap.setMapStatus(msu);
                });
        LocationClientOption option = new LocationClientOption();
        option.setCoorType("bd09ll");// 坐标类型
        option.setIsNeedAddress(true);// 可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);// 打开Gps
        option.setScanSpan(1000);// 1000毫秒定位一次
        option.setIsNeedLocationPoiList(false);// 可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        mLocClient.setLocOption(option);
        mLocClient.start();
        isPositioning = true;

    }

    private String getLocation(BDLocation location) {
        StringBuffer sb = new StringBuffer(256);
        sb.append("time : ");
        sb.append(location.getTime());
        sb.append("\nerror code : ");
        sb.append(location.getLocType());
        sb.append("\nlatitude : ");
        sb.append(location.getLatitude());
        sb.append("\nlontitude : ");
        sb.append(location.getLongitude());
        sb.append("\nradius : ");
        sb.append(location.getRadius());
        if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
            sb.append("\nspeed : ");
            sb.append(location.getSpeed());// 单位：公里每小时
            sb.append("\nsatellite : ");
            sb.append(location.getSatelliteNumber());
            sb.append("\nheight : ");
            sb.append(location.getAltitude());// 单位：米
            sb.append("\ndirection : ");
            sb.append(location.getDirection());// 单位度
            sb.append("\naddr : ");
            sb.append(location.getAddrStr());
            sb.append("\ndescribe : ");
            sb.append("gps定位成功");

        } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
            sb.append("\naddr : ");
            sb.append(location.getAddrStr());
            //运营商信息
            sb.append("\noperationers : ");
            sb.append(location.getOperators());
            sb.append("\ndescribe : ");
            sb.append("网络定位成功");
        } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
            sb.append("\ndescribe : ");
            sb.append("离线定位成功，离线定位结果也是有效的");
        } else if (location.getLocType() == BDLocation.TypeServerError) {
            sb.append("\ndescribe : ");
            sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
        } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
            sb.append("\ndescribe : ");
            sb.append("网络不同导致定位失败，请检查网络是否通畅");
        } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
            sb.append("\ndescribe : ");
            sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
        }
        sb.append("\nlocationdescribe : ");
        sb.append(location.getLocationDescribe());// 位置语义化信息
        List<Poi> list = location.getPoiList();// POI数据
        if (list != null) {
            sb.append("\npoilist size = : ");
            sb.append(list.size());
            for (Poi p : list) {
                sb.append("\npoi= : ");
                sb.append(p.getId() + " " + p.getName() + " " + p.getRank());
            }
        }
        XLog.i( sb.toString());
        return sb.toString();
    }


    @Override
    protected void onPause() {
        // MapView的生命周期与Activity同步，当activity挂起时需调用MapView.onPause()
        mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        // MapView的生命周期与Activity同步，当activity恢复时需调用MapView.onResume()
        mMapView.onResume();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        // MapView的生命周期与Activity同步，当activity销毁时需调用MapView.destroy()
        mMapView.onDestroy();
        my = null;
        super.onDestroy();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_fresh, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar det_rpt_item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {

            if (!isPositioning) {
                getBaiduLocation();
            }

        }

        return super.onOptionsItemSelected(item);
    }

}
