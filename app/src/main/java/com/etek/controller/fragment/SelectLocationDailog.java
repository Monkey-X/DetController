package com.etek.controller.fragment;


import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import android.util.DisplayMetrics;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import android.widget.TextView;


import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.etek.controller.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class SelectLocationDailog extends DialogFragment {


    @BindView(R.id.det_location)
    TextView tvLocation;

    @BindView(R.id.det_address)
    TextView tvAddress;

    @OnClick(R.id.dialog_return)
    public void OnClick() {
        myDialogFragment_Listener.setLocation(mLocation);
        dismiss();
    }


    private SelectLocationListener myDialogFragment_Listener;

    // 回调接口，用于传递数据给Activity -------
    public interface SelectLocationListener {
        void setLocation(BDLocation location);
    }

    BDLocation mLocation;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        try {
            myDialogFragment_Listener = (SelectLocationListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implementon EditPODataDailog_Listener");
        }
    }

    private Context mContext;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        View rootView = inflater.inflate(R.layout.dialog_fragment_select_location, null);
        ButterKnife.bind(this, rootView);

        getDialog().setCanceledOnTouchOutside(true);

        return rootView;

    }


    @Override
    public void onStart() {
        super.onStart();
        Window win = getDialog().getWindow();
        // 一定要设置Background，如果不设置，window属性设置无效
        win.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);

        WindowManager.LayoutParams params = win.getAttributes();
        params.gravity = Gravity.CENTER;
        // 使用ViewGroup.LayoutParams，以便Dialog 宽度充满整个屏幕
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        win.setAttributes(params);
        getBaiduLocation();
    }

    @Override
    public void onDestroy() {
        // 通过接口回传数据给activity
        if (myDialogFragment_Listener != null) {
            myDialogFragment_Listener.setLocation(mLocation);
        }
        if(mLocClient!=null){
            mLocClient.stop();
            mLocClient = null;
        }

        super.onDestroy();
    }
    LocationClient mLocClient;
    void getBaiduLocation() {
//         MyLocationListenner   myListener = new MyLocationListenner();

        // 定位初始化
        mLocClient = new LocationClient(mContext);
        mLocClient.registerNotifyLocationListener(
                location -> {
                    if (location == null)
                        return;

                    tvLocation.setText(location.getLongitude() + " , " + location.getLatitude());
//                        XLog.d(LOG_TAG,location.getCity() + "location" + location.getStreet() + "--" + location.getAddrStr() + "---" + location.getStreetNumber());
                    tvAddress.setText(location.getAddrStr() + "-" + location.getStreetNumber());
                    mLocClient.stop();
                    mLocation = location;

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


}
