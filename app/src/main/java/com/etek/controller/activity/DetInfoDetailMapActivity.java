package com.etek.controller.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.InfoWindow.OnInfoWindowClickListener;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.etek.controller.R;
import com.etek.controller.entity.DetReportInfo;
import com.etek.sommerlibrary.activity.BaseActivity;


/**
 * 演示覆盖物的用法
 */
public class DetInfoDetailMapActivity extends BaseActivity {

    /**
     * MapView 是地图主控件
     */
    private MapView mMapView;
    private BaiduMap mBaiduMap;

    private Marker mMarkerB;

    private InfoWindow mInfoWindow;

    DetReportInfo detReportInfo;

    BitmapDescriptor bd = BitmapDescriptorFactory
            .fromResource(R.drawable.icon_gcoding);


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pro_map);
        initToolBar(R.string.title_activity_baidu_map);

        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
         detReportInfo = (DetReportInfo) getIntent().getSerializableExtra("detReport");
        LatLng llB = new LatLng(detReportInfo.getLatitude(), detReportInfo.getLongitude());

        CoordinateConverter converter = new CoordinateConverter();
        converter.from(CoordinateConverter.CoordType.GPS);


        converter.coord(llB);
        LatLng desLatLng = converter.convert();
        MapStatus mMapStatus = new MapStatus.Builder()
                .target(desLatLng)
                .zoom(15)
                .build();
        MapStatusUpdate msu =  MapStatusUpdateFactory.newMapStatus(mMapStatus);

        mBaiduMap.setMapStatus(msu);


        initOverlay(desLatLng);
        mBaiduMap.setOnMarkerClickListener(new OnMarkerClickListener() {
            public boolean onMarkerClick(final Marker marker) {
                Button button = new Button(getApplicationContext());
                button.setBackgroundResource(R.drawable.popup);
                OnInfoWindowClickListener listener = null;
               if (marker == mMarkerB) {
                   if(detReportInfo!=null){
                       button.setText("设备号:"+detReportInfo.getDevice());
                       button.setTextColor(Color.BLACK);
                   }

                    button.setOnClickListener(new OnClickListener() {
                        public void onClick(View v) {

                            mBaiduMap.hideInfoWindow();
                        }
                    });
                    LatLng ll = marker.getPosition();
                    mInfoWindow = new InfoWindow(button, ll, -1);
                    mBaiduMap.showInfoWindow(mInfoWindow);
                }
                return true;
            }
        });

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
        super.onDestroy();

        bd.recycle();

    }

}
