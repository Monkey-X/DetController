package com.etek.controller.activity;

import android.graphics.Color;
import android.os.Bundle;

import com.elvishew.xlog.XLog;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
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


public class ProMapActivity extends BaseActivity {

    /**
     * MapView 是地图主控件
     */
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private Marker mMarkerB;

    LatLng point;
//    private InfoWindow mInfoWindow;


    BitmapDescriptor bd = BitmapDescriptorFactory
            .fromResource(R.drawable.explore);

    BitmapDescriptor fd = BitmapDescriptorFactory
            .fromResource(R.drawable.forbidden);

    BitmapDescriptor my = BitmapDescriptorFactory
            .fromResource(R.drawable.icon_gcoding);
    private LocationClient mLocClient;


    private ProjectInfoEntity getProjectInfo() {
        long proId = getIntent().getLongExtra("projectId", 0);
        ProjectInfoEntity projectInfo = null;
        if (proId > 0) {
            projectInfo = DBManager.getInstance().getProjectInfoEntityDao().
                    queryBuilder()
                    .where(ProjectInfoEntityDao.Properties.Id.eq(proId)).uniqueOrThrow();
//            XLog.v(projectInfo.toString());

        } else {
            ToastUtils.showCustom(mContext, "没有此项目!");

            return projectInfo;
        }
        return projectInfo;
    }

    private void setMarkPoint(double jingdu, double weidu) {
        //定义Maker坐标点
//        mBaiduMap.clear();
        point = new LatLng(jingdu, weidu);
//构建Marker图标
        BitmapDescriptor bitmap = BitmapDescriptorFactory
                .fromResource(R.drawable.icon_gcoding);
//构建MarkerOption，用于在地图上添加Marker
        OverlayOptions option = new MarkerOptions()
                .position(point)
                .icon(bitmap);
//在地图上添加Marker，并显示
//        mBaiduMap.addOverlay(option);
        TextView showInfo = new TextView(getApplicationContext());
        showInfo.setBackgroundResource(R.drawable.popup);

        showInfo.setText(String.format("%.6f", jingdu)+" , "+String.format("%.6f", weidu));
        InfoWindow mInfoWindow = new InfoWindow(showInfo, point, -1);
        mBaiduMap.showInfoWindow(mInfoWindow);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pro_map);


        mMapView = findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();

        ProjectInfoEntity projectInfo = getProjectInfo();

        if (projectInfo == null) {
            ToastUtils.showCustom(mContext, "项目为空！");
            finish();
            return;
        } else {

            for (PermissibleZoneEntity zoneBean : projectInfo.getPermissibleZoneList()) {
                XLog.v("zoneBean: , ",zoneBean.getLatitude(),zoneBean.getLongitude());
                LatLng llB = new LatLng(zoneBean.getLatitude(), zoneBean.getLongitude());

                CoordinateConverter converter = new CoordinateConverter()
                        .from(CoordinateConverter.CoordType.GPS)
                        .coord(llB);

                //desLatLng 转换后的坐标
                LatLng desLatLng = converter.convert();
                MapStatus mMapStatus = new MapStatus.Builder()
                        .target(desLatLng)
                        .zoom(15)
                        .build();
                MapStatusUpdate msu = MapStatusUpdateFactory.newMapStatus(mMapStatus);
                mBaiduMap.setMapStatus(msu);

                initOverlay(desLatLng);
//                mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
//                    public boolean onMarkerClick(final Marker marker) {
//                        Button button = new Button(getApplicationContext());
//                        button.setBackgroundResource(R.drawable.popup);
//                        InfoWindow.OnInfoWindowClickListener listener = null;
//                        if (marker == mMarkerB) {
//                            if(zoneBean!=null){
//                                button.setText("爆破区域:"+zoneBean.getName()+"\n"
//                                        +zoneBean.getLongitude()+","+zoneBean.getLatitude());
//                                button.setTextColor(Color.BLACK);
//                            }
//
//                            button.setOnClickListener(new View.OnClickListener() {
//                                public void onClick(View v) {
//
//                                    mBaiduMap.hideInfoWindow();
//                                }
//                            });
//                            LatLng ll = marker.getPosition();
//                            InfoWindow mInfoWindow = new InfoWindow(button, ll, -1);
//                            mBaiduMap.showInfoWindow(mInfoWindow);
//                        }
//                        return true;
//                    }
//                });

                OverlayOptions overlayOptions = drawCircle(desLatLng.latitude, desLatLng.longitude, zoneBean.getRadius());
                mBaiduMap.addOverlay(overlayOptions);
            }
            for (ForbiddenZoneEntity zoneBean : projectInfo.getForbiddenZoneList()) {
                LatLng llB = new LatLng(zoneBean.getLatitude(), zoneBean.getLongitude());

                CoordinateConverter converter = new CoordinateConverter()
                        .from(CoordinateConverter.CoordType.GPS)
                        .coord(llB);

                //desLatLng 转换后的坐标
                LatLng desLatLng = converter.convert();
                MapStatus mMapStatus = new MapStatus.Builder()
                        .target(desLatLng)
                        .zoom(15)
                        .build();
                MapStatusUpdate msu = MapStatusUpdateFactory.newMapStatus(mMapStatus);
                mBaiduMap.setMapStatus(msu);

                initForbiddenOverlay(desLatLng);


                OverlayOptions overlayOptions = drawForbiddenCircle(desLatLng.latitude, desLatLng.longitude, zoneBean.getRadius());
                mBaiduMap.addOverlay(overlayOptions);
            }
        }

        initSupportActionBar(R.string.title_activity_baidu_map);
//        getBaiduLocation();
        BaiduMap.OnMapClickListener listener = new BaiduMap.OnMapClickListener() {
            /**
             * 地图单击事件回调函数
             *
             * @param point 点击的地理坐标
             */
            @Override
            public void onMapClick(LatLng point) {
                XLog.v( "onMapClick latitude:   longitude: ", point.latitude,point.longitude  );//经度
//                XLog.v(  "onMapClick longitude: "+);//纬度
                setMarkPoint(point.latitude,point.longitude);
            }

            /**
             * 地图内 Poi 单击事件回调函数
             *
             * @param mapPoi 点击的 poi 信息
             * @return
             */
            @Override
            public boolean onMapPoiClick(MapPoi mapPoi) {
                XLog.v(  "onMapPoiClick getName: "+mapPoi.getName() );
                XLog.v(  "onMapPoiClick getUid: "+mapPoi.getUid() );
                XLog.v(  "onMapPoiClick getPosition: "+mapPoi.getPosition() );
                return false;
            }
        };//点击获取经纬度；
        mBaiduMap.setOnMapClickListener(listener);
        mBaiduMap.setMyLocationEnabled(true);


        

    }

    /**
     * 绘制圆，该圆随地图状态变化
     *
     * @return 圆对象
     */
    public OverlayOptions drawCircle(double lat, double lot, int radius) {
        //设置圆心的左边
        LatLng pt1 = new LatLng(lat, lot);
        OverlayOptions overlayOptions = new CircleOptions()
                .center(pt1)
                //设置圆的颜色
                .fillColor(Color.parseColor("#201c3d6f"))
                //设置边缘线的颜色
                .stroke(new Stroke(0, Color.parseColor("#ffffff")))
                //设置半径
                .radius(radius);
        return overlayOptions;
//        baiduMap.addOverlay(overlayOptions);
    }

    /**
     * 绘制圆，该圆随地图状态变化
     *
     * @return 圆对象
     */
    public OverlayOptions drawForbiddenCircle(double lat, double lot, int radius) {
        //设置圆心的左边
        LatLng pt1 = new LatLng(lat, lot);
        OverlayOptions overlayOptions = new CircleOptions()
                .center(pt1)
                //设置圆的颜色
                .fillColor(getResources().getColor(R.color.trn_red))
                //设置边缘线的颜色
                .stroke(new Stroke(0, Color.parseColor("#ffffff")))
                //设置半径
                .radius(radius);
        return overlayOptions;
//        baiduMap.addOverlay(overlayOptions);
    }

    public void initForbiddenOverlay(LatLng desLatLng) {
        MarkerOptions ooB = new MarkerOptions().position(desLatLng).icon(fd)
                .zIndex(5);

        mMarkerB = (Marker) (mBaiduMap.addOverlay(ooB));


    }

    public void initOverlay(LatLng desLatLng) {
        // add marker overlay


        MarkerOptions ooB = new MarkerOptions().position(desLatLng).icon(bd)
                .zIndex(5);
        mMarkerB = (Marker) (mBaiduMap.addOverlay(ooB));
//        CircleOptions circle = new CircleOptions().center(desLatLng).fillColor(R.color.bubble_chatto_unread_stroke_bg).radius(1000);
//        mBaiduMap.addOverlay(circle);


//        // add ground overlay
//        LatLng southwest = new LatLng(39.92235, 116.380338);
//        LatLng northeast = new LatLng(39.947246, 116.414977);
//        LatLngBounds bounds = new LatLngBounds.Builder().include(northeast)
//                .include(southwest).build();
//
//        OverlayOptions ooGround = new GroundOverlayOptions()
//                .positionFromBounds(bounds).image(bdGround).transparency(0.8f);
//        mBaiduMap.addOverlay(ooGround);

//        MapStatusUpdate u = MapStatusUpdateFactory
//                .newLatLng(bounds.getCenter());
//        mBaiduMap.setMapStatus(u);

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
        mLocClient = new LocationClient(mContext);
        mLocClient.registerNotifyLocationListener(
                location -> {
                    if (location == null)
                        return;


                    mLocClient.stop();
                    MarkerOptions ooB = new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude())).icon(my)
                            .zIndex(5);
                    mMarkerB = (Marker) (mBaiduMap.addOverlay(ooB));

                });
        LocationClientOption option = new LocationClientOption();
//        option.setOpenGps(false);
        option.setOpenGps(true);// 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000);
        //这个要写
//        option.setAddrType("all");
        option.setIsNeedAddress(true);

//        mToolbar.setTitle(contractId);
        mLocClient.setLocOption(option);
        mLocClient.start();

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
        bd.recycle();
        super.onDestroy();


    }

}
