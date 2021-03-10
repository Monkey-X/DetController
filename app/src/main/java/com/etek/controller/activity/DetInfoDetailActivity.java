package com.etek.controller.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.DisplayMetrics;

import com.elvishew.xlog.XLog;

import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.baidu.mapapi.model.LatLng;
import com.etek.controller.R;
import com.etek.controller.common.AppConstants;
import com.etek.controller.entity.DetReportDetail;
import com.etek.controller.entity.DetReportInfo;
import com.etek.controller.entity.ServerRespDetDetail;
import com.etek.controller.utils.AsyncHttpCilentUtil;
import com.etek.controller.utils.SommerUtils;
import com.etek.sommerlibrary.utils.DateUtil;
import com.etek.sommerlibrary.utils.StringTool;
import com.etek.sommerlibrary.utils.ToastUtils;
import com.etek.sommerlibrary.widget.TableView;
import com.etek.sommerlibrary.activity.BaseActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class DetInfoDetailActivity extends BaseActivity {

    String token;
    String timeStr;

    String location;

    private ArrayList<DetReportDetail> mlist;

    TableView tv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_det_detail);

        initTableView();

        Intent intent = getIntent();
        DetReportInfo detInf = (DetReportInfo) getIntent().getSerializableExtra("detReport");

        token = detInf.getMsg();
        location = intent.getStringExtra("location");
        TextView tvDevice = findViewById(R.id.det_device_id);
        tvDevice.setText(detInf.getDevice());
        TextView tvLocation = findViewById(R.id.det_location);
        tvLocation.setText(detInf.getLongitude() + "," + detInf.getLatitude());
        TextView tvAddress = findViewById(R.id.det_address);
        tvAddress.setText(detInf.getAddress());

        TextView tvTime = findViewById(R.id.det_time);
        timeStr = DateUtil.getDateStr(detInf.getDetTime());
        tvTime.setText(timeStr);

        Button selectMap = findViewById(R.id.det_address_sel);
        selectMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(mContext, DetInfoDetailMapActivity.class);
//                intent.putExtra("detReport", detInf);
//                startActivity(intent);
            }
        });
        initToolBar(R.string.title_activity_det_detail);
    }


    private void initTableView() {
        tv = findViewById(R.id.det_table);

        WindowManager manager = this.getWindowManager();
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);
        int width2 = outMetrics.widthPixels;
        int height2 = outMetrics.heightPixels;
        XLog.v("width2:" + width2
                + "  height2:" + height2);
        tv.setHeaderNames("序号", "雷管编码", "状态");
        width2 = width2 - 20;
        tv.setColumnWidth(0, width2 / 4);
        tv.setColumnWidth(1, width2 / 2);
        tv.setColumnWidth(2, width2 / 4);
//        tv.setTableData(getTestData());

        //注册点击item事件
        //需要 tv.setEventMode(TableView.MODE_ITEM_EVENT);
        tv.setOnItemClickListener(new TableView.OnTableItemClickListener() {
            @Override
            public void onItemClick(int position, String[] rowData) {
                XLog.d("click item,pos=" + position + "  data=" + rowData[1]);
            }
        });
        //注册长按item事件
        //需要 tv.setEventMode(TableView.MODE_ITEM_EVENT);
        tv.setOnItemLongClickListener(new TableView.OnTableItemLongClickListener() {
            @Override
            public void onItemLongClick(int position, String[] rowData) {
                XLog.d("long click item,pos=" + position + "  data[1]=" + rowData[1]);
            }
        });
        //注册点击单元格事件
        //需要tv.setEventMode(TableView.MODE_ALL_UNIT_EVENT);
        //或者tv.setEventMode(TableView.MODE_EITHER_UNIT_EVENT); 并且 tv.setColumnEventIndex(1,7,9,18);
        tv.setOnUnitClickListener(new TableView.OnUnitClickListener() {
            @Override
            public void onUnitClick(int row, int column, String unitText) {
                XLog.d("onUnitClick: row=" + row + "  column=" + column + "  text=" + unitText);
            }
        });

        //***********************************************************************
        //上面注册的事件需要设置相应的事件模式，以下选其一，默认不处理任何事件
        //tv.setEventMode(TableView.MODE_NONE_EVENT);//不处理任何事件

        //tv.setEventMode(TableView.MODE_ITEM_EVENT);//item处理点击和长按事件

        tv.setEventMode(TableView.MODE_ALL_UNIT_EVENT);//所有单元格处理事件

        //tv.setEventMode(TableView.MODE_EITHER_UNIT_EVENT);//自定义某些列的单元格处理事件
        //tv.setColumnEventIndex(1,7,9,18);//设置哪些列的单元格处理事件
        //***********************************************************************

        //其他可选设置项
        tv.setUnitSelectable(true);//单元格处理事件的时候是否可以选中
        //tv.setUnitDownColor(R.color.blue_color);//单元格处理事件的时候，按下态的颜色
        //tv.setUnitSelectedColor(R.color.cyan_color);//单元格被选中的颜色

        //其他相关的方法
        //Map<Point,String> selectedData = tv.getSelectedUnits();//获取所有选中的单元格数据
        //tv.clearSelectedUnits();//清除所有选中的单元格
        //tv.setUnitSelected(1,2);//在单元格可以被选中的时候，设置第1行第2列的单元格被选中
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        }
//        SharedPreferences preferences = getSharedPreferences("DetInfo",0);
//        String token =   preferences.getString(ActivityConstants.TOKEN, "");
//        XLog.v(token);
//        mToolbar.setSubtitle(token);
//        tokenInfoView.setText(token);
        getData(token);
//        getData();
    }

    private void getData(String token) {
//        OkHttpClient okHttpClient = new OkHttpClient();
        LinkedHashMap params = new LinkedHashMap();

        params.put("strRecv", token);
        XLog.v("token:" + token);
        if (!StringTool.isBlank(token)) {
            String newUrl = SommerUtils.attachHttpGetParams(AppConstants.DET_DETAIL, params);
            XLog.v("newUrl:" + newUrl);
            AsyncHttpCilentUtil.getOkHttpClient(newUrl, new Callback() {

                @Override
                public void onFailure(Call call, IOException e) {
                    XLog.e("onFailure:" + call);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    XLog.d("onSuccess:" + response.toString());
//                if (Looper.getMainLooper().getThread() == Thread.currentThread()) {
//                    XLog.d( "Main Thread");
//                } else {
//                    XLog.d( "Not Main Thread");
//                }


//                XLog.d("onSuccess:"+response.body().string());
                    try {
                        final ServerRespDetDetail detServerResp = JSON.parseObject(response.body().string(), ServerRespDetDetail.class);
                        XLog.d(detServerResp.toString());
                        if (detServerResp.getTotal() > 0) {
                            runOnUiThread(() -> {

                                mlist = (ArrayList<DetReportDetail>) detServerResp.getRows();
//                                List<String[]> datas = new ArrayList<>();
                                int size = mlist.size();
                                String[][] detData = new String[size][3];
                                int i;
                                for (i = 0; i < size; i++) {
                                    detData[i][0] = "" + (i + 1);
                                    detData[i][1] = mlist.get(i).getDetonatorid();
                                    detData[i][2] = "正常";
//                                    XLog.v( Arrays.toString(detData[i]));
                                }
//                                tv = (TableView) findViewById(R.id.event_table);
//                                tv.setHeaderNames("序号", "雷管编码", "状态");
//                                tv.setColumnWidth(0,200);
//                                tv.setColumnWidth(1,800);
//                                tv.setColumnWidth(2,120);

                                tv.setTableData(detData);
                                tv.notifyAttributesChanged();

//                        System.out.println(tv.getAllData());
                                //设置适配器
//                                md = new DetReportDetailAdapter(logList, mContext);
//                                bleDrv.setLayoutManager(new LinearLayoutManager(mContext));
//                                bleDrv.setAdapter(md);
//                                md.notifyDataSetChanged();
                            });


                        } else {


                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    tv.setVisibility(View.INVISIBLE);
                                    ToastUtils.show(mContext, "没有详细数据");
//                                bleDrv.setVisibility(View.INVISIBLE);
                                }
                            });


                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        } else {
            showToast("没有有效的token值");
        }

    }


}
