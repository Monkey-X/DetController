package com.etek.controller.activity;

import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.etek.controller.R;
import com.etek.controller.hardware.command.DetApp;
import com.etek.controller.utils.location.DLocationTools;
import com.etek.controller.utils.location.DLocationUtils;
import com.etek.controller.utils.location.OnLocationChangeListener;
import com.etek.sommerlibrary.activity.BaseActivity;
import com.etek.sommerlibrary.utils.ToastUtils;

import static com.etek.controller.utils.location.DLocationWhat.NO_LOCATIONMANAGER;
import static com.etek.controller.utils.location.DLocationWhat.NO_PROVIDER;
import static com.etek.controller.utils.location.DLocationWhat.ONLY_GPS_WORK;

/**
 * 充电起爆
 */
public class PowerBombActivity extends BaseActivity implements View.OnClickListener {

    private Context mContext;
    private int GO_TO_GPS = 150;
    private TextView toastText;
    private TextView powerBank;
    private String TAG = "PowerBombActivity";
    private PowerAsyncTask powerAsyncTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_power_bomb);
        initSupportActionBar(R.string.title_power_bomb);
        init();
        initView();
        getLocation();
    }

    private void initView() {
        toastText = findViewById(R.id.toast_text);
        powerBank = findViewById(R.id.power_bank);
        powerBank.setOnClickListener(this);
    }

    /**
     * 初始化
     */
    private void init() {
        this.mContext = this;
        DLocationUtils.init(this);
    }


    int mBackKeyAction;
    long mActionTime;
    int mOkKeyAction;

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {

        int keyCode = event.getKeyCode();
        int action = event.getAction();

        if (keyCode == 189 && action == KeyEvent.ACTION_DOWN) {
            mBackKeyAction = KeyEvent.ACTION_DOWN;  //记录按下状态
            if (mActionTime == 0) {
                mActionTime = System.currentTimeMillis();
            }
        }

        if (keyCode == 189 && action == KeyEvent.ACTION_UP) {
            mBackKeyAction = KeyEvent.ACTION_UP;  //记录松下状态
            mActionTime = 0;
        }

        if (keyCode == 190 && event.getAction() == KeyEvent.ACTION_DOWN) {
            mOkKeyAction = KeyEvent.ACTION_DOWN;   //记录按下状态
            if (mActionTime == 0) {
                mActionTime = System.currentTimeMillis();
            }
        }

        if (keyCode == 190 && event.getAction() == KeyEvent.ACTION_UP) {
            mOkKeyAction = KeyEvent.ACTION_UP;    //记录松下状态
            mActionTime = 0;
        }

        //长按，左右侧键  todo
        if (isLongPress() && mBackKeyAction == KeyEvent.ACTION_DOWN && mOkKeyAction == KeyEvent.ACTION_DOWN) {
            //  长按左右键之后进行起爆操作 todo

        }

        return super.dispatchKeyEvent(event);

    }

    private boolean isLongPress() {
        if (System.currentTimeMillis() - mActionTime > 1000) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * 定位
     */
    private void getLocation() {
        int status = DLocationUtils.getInstance().register(locationChangeListener);
        switch (status) {
            case NO_LOCATIONMANAGER:
                //请求权限
                ToastUtils.show(this, "没有定位权限");
                DLocationTools.openAppSetting(mContext);
                break;
            case NO_PROVIDER:
                //打开定位
                ToastUtils.show(this, "尚未打开定位");
                DLocationTools.openGpsSettings(mContext, GO_TO_GPS);
                break;
            case ONLY_GPS_WORK:
                //切换定位模式到【高精确度】或【节电】
                ToastUtils.show(this, "切换定位模式到【高精确度】或【节电】");
                DLocationTools.openGpsSettings(mContext, GO_TO_GPS);
                break;
        }
    }

    /**
     * 更新经纬度信息
     */
    public void updateGPSInfo(Location location) {
        if (location != null) {
            // TODO: 2020/11/20   获取到经纬度
        }
    }

    /**
     * 定位监听器
     */
    private OnLocationChangeListener locationChangeListener = new OnLocationChangeListener() {
        @Override
        public void getLastKnownLocation(Location location) {
            updateGPSInfo(location);
        }

        @Override
        public void onLocationChanged(Location location) {
            updateGPSInfo(location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            //TODO
        }
    };

    /**
     * 注销
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        DLocationUtils.getInstance().unregister();
        if (powerAsyncTask!=null) {
            powerAsyncTask.cancel(true);
        }
    }

    @Override
    public void onClick(View v) {
        toastText.setText("");
        powerAsyncTask = new PowerAsyncTask();
        powerAsyncTask.execute();
    }

    public class PowerAsyncTask extends AsyncTask<String, Integer, Integer> {
        @Override
        protected Integer doInBackground(String... strings) {
            int i = DetApp.getInstance().ModuleSetDormantStatus(0);
            Log.d(TAG, "doInBackground: ModuleSetDormantStatus = " + i);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            int result = DetApp.getInstance().ModuleCapacitorCharge(0, true);
            Log.d(TAG, "doInBackground: ModuleCapacitorCharge = " + result);
            return result;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProDialog("充电中...");
        }


        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            toastText.setText("请同时按下左右侧黄色按钮进行起爆操作！");
            missProDialog();
        }
    }
}