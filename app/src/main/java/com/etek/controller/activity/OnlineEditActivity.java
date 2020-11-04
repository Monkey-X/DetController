package com.etek.controller.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;

import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.elvishew.xlog.XLog;
import com.etek.controller.R;

import com.etek.controller.common.AppConstants;

import com.etek.controller.common.Globals;
import com.etek.controller.dto.Jbqy;
import com.etek.controller.dto.Jbqys;
import com.etek.controller.dto.Lg;
import com.etek.controller.dto.Lgs;
import com.etek.controller.dto.OnlineCheckDto;
import com.etek.controller.dto.OnlineCheckResp;
import com.etek.controller.dto.OnlineCheckStatusResp;

import com.etek.controller.dto.ProjectFileDto;
import com.etek.controller.dto.Sbbhs;
import com.etek.controller.dto.Zbqy;
import com.etek.controller.dto.Zbqys;
import com.etek.controller.entity.DetController;
import com.etek.controller.entity.Detonator;

import com.etek.controller.persistence.DBManager;
import com.etek.controller.persistence.entity.ControllerEntity;

import com.etek.controller.persistence.entity.DetonatorEntity;
import com.etek.controller.persistence.entity.ForbiddenZoneEntity;
import com.etek.controller.persistence.entity.PermissibleZoneEntity;
import com.etek.controller.persistence.entity.ProjectInfoEntity;
import com.etek.controller.utils.AsyncHttpCilentUtil;
import com.etek.controller.utils.DetUtil;
import com.etek.controller.utils.RptUtil;
import com.etek.controller.utils.SommerUtils;
import com.etek.sommerlibrary.activity.BaseActivity;
import com.etek.sommerlibrary.dto.Result;
import com.etek.sommerlibrary.widget.TableView;


import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.etek.sommerlibrary.widget.TableView.MODE_ITEM_EVENT;


public class OnlineEditActivity extends BaseActivity {


    public static final int RESQUESTCODE = 101;
    DetController detController;

    @BindView(R.id.contract_code)
    EditText contractCode;
    @BindView(R.id.pro_code)
    EditText projectCode;
    @BindView(R.id.pro_code_title)
    TextView proCodeTitle;
    @BindView(R.id.pro_name_title)
    TextView proNameTitle;
    @BindView(R.id.company_code)
    TextView companyCode;
    //    @BindView(R.id.company_name)
//    TextView companyName;
    @BindView(R.id.permision_location)
    TextView permisionLocation;

    @BindView(R.id.controller_sn)
    TextView controllerSn;
//    @BindView(R.id.det_reg)
//    TextView detReg ;

//
//    @BindView(R.id.rv_detonator_reg)
//    RecyclerView rvDetonatorReg ;
//
//    @BindView(R.id.rv_detonator_unused)
//    RecyclerView rvDetonatorUnused ;

    @BindView(R.id.det_reg_table)
    TableView detRegTable;


    long proId = 0;

    ProjectFileDto projectFile;

    //location
    LocationClient mLocClient;

    BDLocation mLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_edit);
        ButterKnife.bind(this);
        initSupportActionBar(R.string.title_activity_edit_pro);

//        inputSnDialog();
        showDialog();
//        getBaiduLocation();
    }

    @OnClick(R.id.refresh_location)
    public void refresh_location(){
        getBaiduLocation();;
    }

    void getBaiduLocation() {

        // 定位初始化
        mLocClient = new LocationClient(mContext);
        mLocClient.registerNotifyLocationListener(
                location -> {
//                    XLog.d(BDUtils.getLocation(location));
                    mLocClient.stop();
                    if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果

                        mLocation = location;
                        permisionLocation.setText(location.getLongitude() + " , " + location.getLatitude());
                        permisionLocation.setTextColor(getMyColor(R.color.colorPrimary));
                        detController.setLatitude(mLocation.getLatitude());
                        detController.setLongitude(mLocation.getLongitude());

                        setStringInfo("Longitude", location.getLongitude() + "");
                        setStringInfo("Latitude", location.getLatitude() + "");

                    } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
                        mLocation = location;
                        permisionLocation.setText(location.getLongitude() + " , " + location.getLatitude());
                        permisionLocation.setTextColor(getMyColor(R.color.colorPrimary));
                        detController.setLatitude(mLocation.getLatitude());
                        detController.setLongitude(mLocation.getLongitude());

                        setStringInfo("Longitude", location.getLongitude() + "");
                        setStringInfo("Latitude", location.getLatitude() + "");
                    } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
                        mLocation = location;
                        permisionLocation.setText(location.getLongitude() + " , " + location.getLatitude());
                        permisionLocation.setTextColor(getMyColor(R.color.colorPrimary));
                        detController.setLatitude(mLocation.getLatitude());
                        detController.setLongitude(mLocation.getLongitude());

                        setStringInfo("Longitude", location.getLongitude() + "");
                        setStringInfo("Latitude", location.getLatitude() + "");
                    } else if (location.getLocType() == BDLocation.TypeServerError) {
                        showStatusDialog("定位失败：" + location.getLocType());
                    } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                        showStatusDialog("定位失败：" + location.getLocType());
                    } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                        showStatusDialog("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
                    } else {
                        showStatusDialog("定位失败：" + location.getLocType());
                    }


//                    isLocation = true;
//                        XLog.d(LOG_TAG,location.getCity() + "location" + location.getStreet() + "--" + location.getAddrStr() + "---" + location.getStreetNumber());


                });

        LocationClientOption option = new LocationClientOption();
        option.setCoorType("bd09ll");// 坐标类型
        option.setIsNeedAddress(true);// 可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);// 打开Gps
        option.setScanSpan(1000);// 1000毫秒定位一次
        option.setIsNeedLocationPoiList(true);// 可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        mLocClient.setLocOption(option);

        mLocClient.start();

    }

    @OnClick(R.id.add_det)
    public void addDet() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this.getBaseContext()).inflate(R.layout.dialog_input_det, null, false);
        EditText etDetCode = view.findViewById(R.id.et_det_code);
//        etDetCode.setText("6100416D53834");

        EditText etDetNum = view.findViewById(R.id.et_det_num);
        Spinner spDetSpinner = view.findViewById(R.id.sp_det_type);
        dialog.setCancelable(false);
        dialog.setView(view);
        dialog.setTitle("请输入雷管信息");
        //设置对话框标题
        dialog.setPositiveButton("确认", (dialog1, which) -> {
            String fbh = etDetCode.getText().toString();
            if (!DetUtil.isValidFbh(fbh)) {
                showToast("请输入有效的雷管发编号！");
                return;
            }

            int num = Integer.parseInt(etDetNum.getText().toString());
            if (num <= 0) {
                showToast("请输入有效的雷管数！");
                return;
            }

            int tube = Integer.parseInt(fbh.substring(11, 13));
            int selectedItemPosition = spDetSpinner.getSelectedItemPosition();
            XLog.d("sp:" + selectedItemPosition);
            boolean isOdd = false;
            if (tube % 2 != 0) {
                isOdd = true;
            }

            for (int i = 0; i < num; i++) {
                if (tube > 100) {
                    break;
                }
                String newFbh = fbh.substring(0, 11) + String.format(Locale.CHINA, "%02d", tube);
                Detonator detonator = new Detonator(newFbh);
                detonator.getDetonatorByFbh(newFbh);
                detonator.setStatus(4);
                if (selectedItemPosition == 0) {
                    tube++;
                } else if (selectedItemPosition == 1) {
                    if (isOdd) {
                        tube += 2;
                    } else {
                        tube += 1;
                    }

                } else if (selectedItemPosition == 2) {
                    if (isOdd) {
                        tube += 1;
                    } else {
                        tube += 2;
                    }
                }
                detController.addDetonator(detonator);

            }

            updateDet(detController.getDetList());

        });
        dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public void showDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setTitle("是否开始新的工程？");
        dialog.setCancelable(false);
        //设置对话框标题
        dialog.setIcon(R.mipmap.ic_launcher);
        dialog.setPositiveButton("是", (dialog1, which) -> {

            detController = new DetController();

            DetController cController = (DetController) getIntent().getSerializableExtra("Controller");
            if (cController != null) {
                XLog.i("ctroller:" + cController.toString());
                if (!StringUtils.isEmpty(cController.getProjectId())) {
                    detController.setProjectId(cController.getProjectId());
                }

                if (!StringUtils.isEmpty(cController.getCompanyCode())) {
                    detController.setCompanyCode(cController.getCompanyCode());
                } else {
                    detController.setCompanyCode(Globals.user.getCompanyCode());
                }

                if (!StringUtils.isEmpty(cController.getContractId())) {
                    detController.setContractId(cController.getContractId());
                }

                if (cController.getLongitude()>0 && cController.getLatitude()>0) {
                    detController.setLongitude(cController.getLongitude());
                    detController.setLatitude(cController.getLatitude());
                }
            }
            initView(detController);
            inputSnDialog();

        });
        dialog.setNegativeButton("否", (dialog12, which) -> {
            String detStr = getStringInfo("OnlineController");
            if (!StringUtils.isEmpty(detStr)) {
                XLog.d("read OnlineController" + detStr);
                detController = JSON.parseObject(detStr, DetController.class);

            } else {
                detController = new DetController();
            }

            DetController cController = (DetController) getIntent().getSerializableExtra("Controller");
            if (cController != null) {
                XLog.i("ctroller:" + cController.toString());
                if (!StringUtils.isEmpty(cController.getProjectId())) {
                    detController.setProjectId(cController.getProjectId());
                }

                if (!StringUtils.isEmpty(cController.getCompanyCode())) {
                    detController.setCompanyCode(cController.getCompanyCode());
                } else {
                    detController.setCompanyCode(Globals.user.getCompanyCode());
                }

                if (!StringUtils.isEmpty(cController.getContractId())) {
                    detController.setContractId(cController.getContractId());
                }
            }
            initView(detController);
            inputSnDialog();
        });

        dialog.show();
    }

    @OnClick(R.id.controller_sn)
    public void inputSnDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this.getBaseContext()).inflate(R.layout.dialog_input_controller_sn, null, false);
        EditText etController = view.findViewById(R.id.et_controller);
        if (!StringUtils.isEmpty(detController.getSn()))
            etController.setText(detController.getSn());
        dialog.setView(view);
        dialog.setTitle("请输入起爆器编号");
        dialog.setCancelable(false);
        //设置对话框标题
        dialog.setIcon(R.mipmap.ic_launcher);
        dialog.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                showToast("你输入的是: " + edit.getText().toString());
                detController.setSn(etController.getText().toString());
                controllerSn.setText(etController.getText().toString());

            }
        });

        dialog.show();
    }

    @OnClick(R.id.company_code)
    public void inputCompanyDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this.getBaseContext()).inflate(R.layout.dialog_input_controller_sn, null, false);
        EditText etController = view.findViewById(R.id.et_controller);
        if (!StringUtils.isEmpty(detController.getCompanyCode()))
            etController.setText(detController.getCompanyCode());
        dialog.setView(view);
        dialog.setTitle("请输入公司名称");
        dialog.setCancelable(false);
        //设置对话框标题
        dialog.setIcon(R.mipmap.ic_launcher);
        dialog.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                showToast("你输入的是: " + edit.getText().toString());
                detController.setCompanyCode(etController.getText().toString());
                companyCode.setText(etController.getText().toString());

            }
        });

        dialog.show();
    }



    private void initView(DetController detController) {


        //        projectName.setMovementMethod(ScrollingMovementMethod.getInstance());
//        projectName.setOnTouchListener(touchListener);
        if (!StringUtils.isEmpty(detController.getContractId())) {
            contractCode.setText(detController.getContractId());
        }

        projectCode.setText(detController.getProjectId());
        companyCode.setText(detController.getCompanyCode());
//        companyName.setText(detController.getCompanyName());
        permisionLocation.setText(detController.getLongitude() + "," + detController.getLatitude());
        controllerSn.setText(detController.getSn());

        initRegTableView();


    }

    @OnClick(R.id.permision_location)
    public void changeLocationDialog() {
        if(!Globals.isTest){
            return;
        }
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this.getBaseContext()).inflate(R.layout.dialog_input_location, null, false);
        EditText etLongitude = view.findViewById(R.id.et_longitude);
        EditText etLatitude = view.findViewById(R.id.et_latitude);

        etLongitude.setText(detController.getLongitude() + "");
        etLatitude.setText(detController.getLatitude() + "");

//        etLongitude.setText("119.93863");
//        etLatitude.setText("26.61751");
        dialog.setView(view);
        dialog.setTitle("请输入地址信息！");
        //设置对话框标题
        dialog.setIcon(R.mipmap.ic_launcher);
        dialog.setPositiveButton("确认", (dialog1, which) -> {
//                showToast("你输入的是: " + edit.getText().toString());
            double longitude = Double.parseDouble(etLongitude.getText().toString());
            double latitude = Double.parseDouble(etLatitude.getText().toString());
            detController.setLongitude(longitude);
            detController.setLatitude(latitude);
            permisionLocation.setText(longitude + " , " + latitude);
            permisionLocation.setTextColor(getMyColor(R.color.goldenrod));


        });
        dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        });

        dialog.show();
    }

    @Override
    public void finish() {
//        String s1 = contractCode.getText().toString();
//        if (!StringUtils.isEmpty(s1)) {
//            detController.setContractId(s1);
//        }
//        String s2 = projectCode.getText().toString();
//        if (!StringUtils.isEmpty(s2)) {
//            detController.setProjectId(s2);
//        }
//        String s3 = companyCode.getText().toString();
//        if (!StringUtils.isEmpty(s3)) {
//            detController.setCompanyCode(s3);
//        }
//        String s4 = controllerName.getText().toString();
//        if (!StringUtils.isEmpty(s4)) {
//            detController.setSn(s4);
//        }
//
//        detController.getTokenByDetList();
//
//        Intent intent = new Intent();
//        intent.putExtra("Controller", detController);
//        intent.putExtra("Test", 1);
//        XLog.i("finish:" + detController);
//        //先设置ResultCode，再设置存储数据的意图
//        setResult(RESULT_OK, intent);

        super.finish();

    }

    @Override
    protected void onDestroy() {
        if (mLocClient != null) {
            mLocClient.stop();
            mLocClient = null;
        }
        String s1 = contractCode.getText().toString();
        if (!StringUtils.isEmpty(s1)) {
            detController.setContractId(s1);
        }
        String s2 = projectCode.getText().toString();
        if (!StringUtils.isEmpty(s2)) {
            detController.setProjectId(s2);
        }
        String s3 = companyCode.getText().toString();
        if (!StringUtils.isEmpty(s3)) {
            detController.setCompanyCode(s3);
        }
        String s4 = controllerSn.getText().toString();
        if (!StringUtils.isEmpty(s4)) {
            detController.setSn(s4);
        }
        XLog.d("save OnlineController");
        setStringInfo("OnlineController", JSON.toJSONString(detController));
        super.onDestroy();

//        detController

    }

    private void initRegTableView() {
//        detRegTable = findViewById(R.id.det_table);

//        WindowManager manager = this.getWindowManager();
//        DisplayMetrics outMetrics = new DisplayMetrics();
//        manager.getDefaultDisplay().getMetrics(outMetrics);
        int width2 = getWindowWidth();

        detRegTable.setHeaderNames("序号", "雷管编码", "状态");
        width2 = width2 - 20;
        detRegTable.setColumnWidth(0, width2 / 4);
        detRegTable.setColumnWidth(1, width2 / 2);
        detRegTable.setColumnWidth(2, width2 / 4);
//        detRegTable.setColumnWidth(3, width2 / 6);
//        detRegTable.setUnitTextColor(R.color.mediumseagreen);

        //其他可选设置项
//        detRegTable.setUnitSelectable(true);//单元格处理事件的时候是否可以选中
        detRegTable.setOnItemLongClickListener(new TableView.OnTableItemLongClickListener() {
            @Override
            public void onItemLongClick(int position, String[] rowData) {
                XLog.v("onItemLongClick row:" + position);
//                List<String[]> allData = detRegTable.getAllData();
//                String[] objects = (String[]) allData.toArray();
//                ArrayList<String> strings = new ArrayList<>(Arrays.asList(objects));
////                allData.remove(position);
//                strings.remove(position);
//                AlertDialog.Builder a = new AlertDialog.Builder();

                AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);

                dialog.setTitle("是否删除此雷管！");
                //设置对话框标题
                dialog.setIcon(R.drawable.menu_delete);
                dialog.setPositiveButton("确认", (dialog1, which) -> {
//                showToast("你输入的是: " + edit.getText().toString());
//                    detRegTable.deleteRowData(position);
//                    detController.getDetList().remove(position);
                    detRegTable.deleteRowData(position);
                    detRegTable.notifyAttributesChanged();
                    detController.getDetList().remove(position);

                });
                dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                    }
                });

                dialog.show();
//                updateDet(detController.getDetList());
            }

        });

        detRegTable.setOnUnitClickListener(new TableView.OnUnitClickListener() {
            @Override
            public void onUnitClick(int row, int column, String unitText) {
                XLog.v("row:" + row);
            }
        });
//        detRegTable.setOnItemClickListener(new TableView.OnTableItemClickListener() {
//            @Override
//            public void onItemClick(int position, String[] rowData) {
//                XLog.v("position:"+position);
//            }
//        });
        detRegTable.setEventMode(MODE_ITEM_EVENT);
        if (detController.getDetList() != null && !detController.getDetList().isEmpty()) {
            updateDet(detController.getDetList());
        }

    }

    private void updateDet(List<Detonator> detonatorList) {
        int size = detonatorList.size();
        String[][] detData = new String[size][3];
        int[][] detColor = new int[size][3];
        int i;
        for (i = 0; i < size; i++) {
            detData[i][0] = "" + (i + 1);
            detColor[i][0] = R.color.black;
            detData[i][1] = detonatorList.get(i).getDetCode();
            detColor[i][1] = R.color.black;
//            detColor[i][3] = R.color.black;
            if (detonatorList.get(i).getStatus() == 0) {
                detData[i][2] = "已注册";
                detColor[i][2] = R.color.mediumseagreen;
            } else if (detonatorList.get(i).getStatus() == 1) {
                detData[i][2] = "黑名单";
                detColor[i][2] = R.color.red_normal;
            } else if (detonatorList.get(i).getStatus() == 2) {
                detData[i][2] = "已使用";
                detColor[i][2] = R.color.blue;
            } else if (detonatorList.get(i).getStatus() == 3) {
                detData[i][2] = "不存在";
                detColor[i][2] = R.color.gray;
            } else if (detonatorList.get(i).getStatus() == 4) {
                detData[i][2] = "未校验";
                detColor[i][2] = R.color.gray;
            }


        }

        detRegTable.setmUnitTextColors(detColor);
        detRegTable.setTableData(detData);
        detRegTable.notifyAttributesChanged();

    }


    List<String> getControllerSnList(List<ControllerEntity> datas) {
        List<String> list = new ArrayList<>();
        for (ControllerEntity controller : datas) {
            list.add(controller.getName());
        }
        return list;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_pro_edit, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar det_rpt_item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_delete) {
            detController.getDetList().clear();
//            detRegTable.setTableData();
//           detRegTable.
            int i = 0;
            for (String[] allDatum : detRegTable.getAllData()) {
                detRegTable.deleteRowData(0);
            }

            detRegTable.notifyAttributesChanged();
        } else if (id == R.id.action_verify) {
            getVerifyResult();
        }

        return super.onOptionsItemSelected(item);
    }


    void getVerifyResult() {

//        detController.setLongitude(102.882804);
//        detController.setLatitude(23.326471);
        detController.setSn(controllerSn.getText().toString());
        OnlineCheckDto onlineCheckDto = new OnlineCheckDto();
        onlineCheckDto.setDetControllerWithoutDet(detController);
        onlineCheckDto.setDets(detController.getDetList());
        String rptJson = JSON.toJSONString(onlineCheckDto, SerializerFeature.WriteMapNullValue);
        XLog.v(rptJson);

        // jiangsheng
        Result result = RptUtil.getRptEncode(rptJson);
        if (!result.isSuccess()) {
            showStatusDialog("数据编码出错：" + result.getMessage());

            return;
        }
        String url = AppConstants.DanningServer + AppConstants.OnlineDownload;
//        XLog.v("url:", url);
        LinkedHashMap params = new LinkedHashMap();
        params.put("param", result.getData());    //
        String newUrl = SommerUtils.attachHttpGetParams(url, params, "UTF-8");


        AsyncHttpCilentUtil.httpPost(newUrl, null, new Callback() {


            @Override
            public void onFailure(Call call, IOException e) {
                dismissProgressBar();
                XLog.e("IOException:", e.getMessage());
                showStatusDialog("校验服务器出错：" + e.getMessage());

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                dismissProgressBar();
                String respStr = response.body().string();
                if (StringUtils.isEmpty(respStr)) {
                    XLog.w("respStr is null ");

                    return;
                }


                try {
                    Result rptDecode = RptUtil.getRptDecode(respStr);
                    if (rptDecode.isSuccess()) {
                        String data = (String) rptDecode.getData();
                        XLog.d("resp:" + data);
                        OnlineCheckStatusResp onlineCheckStatusResp = JSON.parseObject(data, OnlineCheckStatusResp.class);
                        if (onlineCheckStatusResp.getCwxx().equals("0")) {
                            OnlineCheckResp serverResult = JSON.parseObject(data, OnlineCheckResp.class);
                            int isUsed = 0;
                            for (Detonator detonator : detController.getDetList()) {
                                for (Lg lg : serverResult.getLgs().getLg()) {
                                    if (lg.getUid().equalsIgnoreCase(detonator.getUid())) {
                                        detonator.setStatus(lg.getGzmcwxx());
                                    }
                                    if (lg.getGzmcwxx() != 0) {
                                        isUsed = 1;
                                    }
                                }
                            }

                            if (isUsed != 0) {
                                showStatusDialog("雷管已经使用！");

                                runOnUiThread(() -> updateDet(detController.getDetList()));

                                return;

                            }

                            projectFile = new ProjectFileDto();


                            projectFile.setCompany(Globals.user.getCompanyName());
                            projectFile.setDwdm(Globals.user.getCompanyCode());
                            projectFile.setXmbh(detController.getProjectId());
                            projectFile.setHtbh(detController.getContractId());

                            proId = storeProjectInfo(projectFile, serverResult);
                            if (proId != 0) {
                               showInsureDialog("在线校验成功！");

                            } else {
                                showStatusDialog("已经存在有此项目");

                            }
                            for (Detonator detonator : detController.getDetList()) {
                                detonator.setStatus(0);
                            }
                            runOnUiThread(() -> updateDet(detController.getDetList()));

                        } else {
                            showStatusDialog(onlineCheckStatusResp.getCwxxms());

                        }
                    }


                } catch (Exception e) {
                    XLog.e("解析错误：" + e.getMessage());
                    showLongToast("解析错误：" + e.getMessage());


                }


            }
        });


     }

    protected void showInsureDialog(final String content) {
        runOnUiThread(() -> {
            android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(mContext);
            builder.setTitle(content);
            //设置对话框标题
//            builder.setIcon(R.mipmap.ic_launcher);
            builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
//                showToast("你输入的是: " + edit.getText().toString());
                    dialog.dismiss();
                    sendCmdMessage(MSG_SUCCESS);
                }
            });

            // 4.设置常用api，并show弹出
            builder.setCancelable(true); //设置按钮是否可以按返回键取消,false则不可以取消
            android.support.v7.app.AlertDialog dialog = builder.create(); //创建对话框
            dialog.setCanceledOnTouchOutside(false); //设置弹出框失去焦点是否隐藏,即点击屏蔽其它地方是否隐藏


            dialog.show();
        });

    }

    private long storeProjectInfo(final ProjectFileDto projectFile, OnlineCheckResp onlineCheckResp) {

//        ThreadPoolUtils.getThreadPool().execute(()->{
//        ProInfoDto mDetInfoDto = projectFile.getProInfo();
        XLog.v("onlineCheckResp:" + onlineCheckResp);
        ProjectInfoEntity projectInfoEntity = new ProjectInfoEntity();
        projectInfoEntity.setApplyDate(onlineCheckResp.getSqrq());
        projectInfoEntity.setProCode(projectFile.getXmbh());
        projectInfoEntity.setProName(projectFile.getXmmc());
        projectInfoEntity.setCompanyCode(projectFile.getDwdm());
        projectInfoEntity.setCompanyName(projectFile.getDwmc());
        projectInfoEntity.setContractCode(projectFile.getHtbh());
        projectInfoEntity.setContractName(projectFile.getHtmc());
        projectInfoEntity.setIsOnline(true);
        projectInfoEntity.setStatus(0);
        projectInfoEntity.setCreateTime(new Date());

        long proId = DBManager.getInstance().getProjectInfoEntityDao().insert(projectInfoEntity);
        if (proId == 0) {
            return 0;
        }
        XLog.v("proid:" + proId);

        Lgs lgs = onlineCheckResp.getLgs();
        if (!lgs.getLg().isEmpty()) {
            List<DetonatorEntity> detonatorEntityList = new ArrayList<>();
            for (Lg lg : lgs.getLg()) {
//                XLog.v("lg:" + lg);
                DetonatorEntity detonatorBean = new DetonatorEntity();
                if (StringUtils.isEmpty(lg.getFbh())) {
                    for (Detonator detonator : detController.getDetList()) {
//                        XLog.d(detonator.toString());
                        if (detonator.getUid().equalsIgnoreCase(lg.getUid())) {
                            lg.setFbh(detonator.getDetCode());
                            detonator.setStatus(lg.getGzmcwxx());
                        }
                    }
                }
                detonatorBean.setCode(lg.getFbh());
                detonatorBean.setWorkCode(lg.getGzm());
                detonatorBean.setUid(lg.getUid());
                detonatorBean.setValidTime(lg.getYxq());
                detonatorBean.setProjectInfoId(proId);
//                                detonatorBean.set
//                            detonatorBean.setProInfoBean(proInfoBean);
                detonatorBean.setStatus(lg.getGzmcwxx());
//                                detonatorBean.set
//                               detonatorBean.setProInfoBean(detInfoDto);
//                XLog.v("detonatorBean:" + detonatorBean);
                detonatorEntityList.add(detonatorBean);

//
            }
            DBManager.getInstance().getDetonatorEntityDao().insertInTx(detonatorEntityList);

        }


        Zbqys zbqys = onlineCheckResp.getZbqys();
        if (!zbqys.getZbqy().isEmpty()) {
            List<PermissibleZoneEntity> permissibleZoneEntityList = new ArrayList<>();
            for (Zbqy zbqy : zbqys.getZbqy()) {

//                                private String zbqssj;  //准爆起始时间
//
//                                private String zbjzsj;  //准爆截止时间
                PermissibleZoneEntity permissibleZone = new PermissibleZoneEntity();
//                            permissibleZoneBean.setProInfoBean(proInfoBean);
                permissibleZone.setName(zbqy.getZbqymc());
                permissibleZone.setLatitude(Double.parseDouble(zbqy.getZbqywd()));
                permissibleZone.setLongitude(Double.parseDouble(zbqy.getZbqyjd()));
                permissibleZone.setRadius(Integer.parseInt(zbqy.getZbqybj()));
                permissibleZone.setStartTime(zbqy.getZbqssj());
                permissibleZone.setStopTime(zbqy.getZbjzsj());
                permissibleZone.setProjectInfoId(proId);
                permissibleZoneEntityList.add(permissibleZone);
//                                Dao<PermissibleZoneBean, Long> permissibleZoneDao = DatabaseHelper.getInstance(mcontext).getDao(PermissibleZoneBean.class);
//                                permissibleZoneDao.create(permissibleZoneBean);
//                                permissibleZoneBean.setStartTime(zbqy.getZbqssj());
//                                permissibleZoneBean.setStopTime(zbqy.getZbjzsj());
            }
            DBManager.getInstance().getPermissibleZoneEntityDao().insertInTx(permissibleZoneEntityList);
        }
        Jbqys jbqys = onlineCheckResp.getJbqys();
        if (!jbqys.getJbqy().isEmpty()) {
            List<ForbiddenZoneEntity> forbiddenZoneEntityList = new ArrayList<>();
            for (Jbqy jbqy : jbqys.getJbqy()) {

//                                private String zbqssj;  //准爆起始时间
//
//                                private String zbjzsj;  //准爆截止时间
                ForbiddenZoneEntity forbiddenZoneEntity = new ForbiddenZoneEntity();

//                forbiddenZoneEntity.setName(jbqy.getJbjzsj());
                forbiddenZoneEntity.setLatitude(Double.parseDouble(jbqy.getJbqywd()));
                forbiddenZoneEntity.setLongitude(Double.parseDouble(jbqy.getJbqyjd()));
                forbiddenZoneEntity.setRadius(Integer.parseInt(jbqy.getJbqybj()));
                forbiddenZoneEntity.setStartTime(jbqy.getJbqssj());
                forbiddenZoneEntity.setStopTime(jbqy.getJbjzsj());
                forbiddenZoneEntity.setProjectInfoId(proId);
                forbiddenZoneEntityList.add(forbiddenZoneEntity);
//                                Dao<PermissibleZoneBean, Long> permissibleZoneDao = DatabaseHelper.getInstance(mcontext).getDao(PermissibleZoneBean.class);
//                                permissibleZoneDao.create(permissibleZoneBean);
//                                permissibleZoneBean.setStartTime(zbqy.getZbqssj());
//                                permissibleZoneBean.setStopTime(zbqy.getZbjzsj());
            }
            DBManager.getInstance().getForbiddenZoneEntityDao().insertInTx(forbiddenZoneEntityList);
        }
        List<Sbbhs> sbbhs = onlineCheckResp.getSbbhs();

        if (!sbbhs.isEmpty()) {
            List<ControllerEntity> controllerEntityList = new ArrayList<>();
            for (Sbbhs sbbh : sbbhs) {
                ControllerEntity controller = new ControllerEntity();
                controller.setName(sbbh.getSbbh());
                controller.setProjectInfoId(proId);
                controllerEntityList.add(controller);
//                            detControllerBean.setProInfoBean(proInfoBean);

//                            Dao<DetControllerBean, Long> detControllerDao = DatabaseHelper.getInstance(mcontext).getDao(DetControllerBean.class);
//                            detControllerDao.create(detControllerBean);
            }
            DBManager.getInstance().getControllerEntityDao().insertInTx(controllerEntityList);
        }

//        });
        return proId;
    }

    void sendCmdMessage(int msg) {
        Message message = new Message();
        message.what = msg;
        if (handler != null) {
            handler.sendMessage(message);
        }

    }

    public static final int MSG_SUCCESS = 0;
    Handler handler = new Handler(msg -> {

        if (msg.what == MSG_SUCCESS) {
//            String s1 = contractCode.getText().toString();
//            if (!StringUtils.isEmpty(s1)) {
//                detController.setContractId(s1);
//            }
//            String s2 = projectCode.getText().toString();
//            if (!StringUtils.isEmpty(s2)) {
//                detController.setProjectId(s2);
//            }
//            String s3 = companyCode.getText().toString();
//            if (!StringUtils.isEmpty(s3)) {
//                detController.setCompanyCode(s3);
//            }
//            String s4 = controllerSn.getText().toString();
//            if (!StringUtils.isEmpty(s4)) {
//                detController.setSn(s4);
//            }
//
//            detController.getTokenByDetList();
//
//            Intent intent = new Intent();
//            intent.putExtra("Controller", detController);
//            intent.putExtra("Test", 1);
//            XLog.i("finish:" + detController);
//            //先设置ResultCode，再设置存储数据的意图
//            setResult(RESULT_OK, intent);
            Intent intent = new Intent(this,OnlineCheckoutActivity2.class);
            startActivity(intent);

            finish();
        }
        return false;
    });


}
