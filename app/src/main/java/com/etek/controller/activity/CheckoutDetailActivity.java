<<<<<<< HEAD
package com.etek.controller.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;

import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;

import android.view.MotionEvent;
import android.view.View;

import android.widget.TextView;

import com.bin.david.form.core.SmartTable;
import com.bin.david.form.core.TableConfig;
import com.bin.david.form.data.CellInfo;
import com.bin.david.form.data.column.Column;
import com.bin.david.form.data.format.bg.BaseBackgroundFormat;
import com.bin.david.form.data.format.bg.BaseCellBackgroundFormat;
import com.bin.david.form.data.format.bg.ICellBackgroundFormat;
import com.bin.david.form.data.format.draw.MultiLineDrawFormat;
import com.bin.david.form.data.format.tip.MultiLineBubbleTip;
import com.bin.david.form.data.style.FontStyle;
import com.bin.david.form.data.table.TableData;
import com.elvishew.xlog.XLog;
import com.etek.controller.R;

import com.etek.controller.adapter.EasyDetonatorAdapter;
import com.etek.controller.entity.Detonator;
import com.etek.controller.enums.DetStatusEnum;
import com.etek.controller.persistence.DBManager;
import com.etek.controller.persistence.entity.ChkControllerEntity;

import com.etek.controller.persistence.entity.ChkDetonatorEntity;
import com.etek.controller.persistence.entity.ControllerEntity;

import com.etek.controller.persistence.entity.ProjectInfoEntity;
import com.etek.controller.persistence.gen.ChkControllerEntityDao;

import com.etek.sommerlibrary.activity.BaseActivity;
import com.etek.sommerlibrary.utils.ToastUtils;
import com.etek.sommerlibrary.widget.TableView;


import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class CheckoutDetailActivity extends BaseActivity {


//    TableView detRegTable;

    ProjectInfoEntity projectInfo;

    ChkControllerEntity chkController;

    @BindView(R.id.pro_name)
    TextView projectName;
    @BindView(R.id.pro_code)
    TextView projectCode;
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

    @BindView(R.id.controller_list)
    TextView controllerName;
    @BindView(R.id.det_reg)
    TextView detReg;
    @BindView(R.id.det_unused)
    TextView detUnused;
//
//    @BindView(R.id.rv_detonator_reg)
//    RecyclerView rvDetonatorReg ;
//
//    @BindView(R.id.rv_detonator_unused)
//    RecyclerView rvDetonatorUnused ;

    @BindView(R.id.det_reg_table)
    TableView detRegTable;

    @BindView(R.id.det_unreg_table)
    TableView detUnregTable;


//    @BindView(R.id.rl_unused)
//    RelativeLayout rlUnused ;
//
//    @BindView(R.id.rl_normal)
//    RelativeLayout rlNormal ;

//    EasyDetonatorAdapter regDetonatorAdapter;
//
//    EasyDetonatorAdapter unusedDetonatorAdapter;


    //    @OnClick({R.id.pro_map_sel})
    public void getMap() {
        Intent intent = new Intent(mContext, ProMapActivity.class);
        intent.putExtra("projectId", projectInfo.getId());
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout_detail);
        ButterKnife.bind(this);
        initSupportActionBar(R.string.title_activity_checkout_detail);
        getProjectInfo();


    }

    private void getProjectInfo() {
        long proId = getIntent().getLongExtra("chkId", 0);

        if (proId > 0) {

            chkController = DBManager.getInstance().getChkControllerEntityDao().
                    queryBuilder()
                    .where(ChkControllerEntityDao.Properties.Id.eq(proId)).unique();
            XLog.d(chkController.toString());
            if (chkController != null) {
                projectInfo = chkController.getProjectInfoEntity();
            }
            if (projectInfo != null) {
                initView(projectInfo);
            } else {
                showToast("项目详情出错！");
            }

//            List<ChkDetonatorEntity> list2 = chkController.getChkDetonatorList();
//            XLog.i("ctl: ",StringUtils.join(list2.toArray(),separator));
//            List<ChkDetonatorEntity> list = DBManager.getInstance().getChkDetonatorEntityDao().
//                    queryBuilder()
//                    .where(ChkDetonatorEntityDao.Properties.ChkId.eq(chkController.getId())).list();
//
//            XLog.i("self: ",StringUtils.join(list.toArray(),separator));

        } else {
            ToastUtils.showCustom(mContext, "没有此项目!");
            finish();

        }


    }


    private void initView(ProjectInfoEntity projectInfo) {

        if (!StringUtils.isEmpty(projectInfo.getProCode())) {
            projectName.setText(projectInfo.getProName());
            proCodeTitle.setText(R.string.pro_code);
            projectCode.setText(projectInfo.getProCode());
            proNameTitle.setText(R.string.pro_name);
        } else if (!StringUtils.isEmpty(projectInfo.getContractCode())) {
            projectName.setText(projectInfo.getContractName());
            proCodeTitle.setText(R.string.contract_code);
            projectCode.setText(projectInfo.getContractCode());
            proNameTitle.setText(R.string.contract_name);
        } else {

            projectName.setText("");
            proCodeTitle.setText("");
            projectCode.setText("");
            proNameTitle.setText("");
        }
        projectName.setMovementMethod(ScrollingMovementMethod.getInstance());
        projectName.setOnTouchListener(touchListener);
        companyCode.setText(projectInfo.getCompanyCode());
//        companyName.setText(projectInfo.getCompanyName());
        permisionLocation = findViewById(R.id.permision_location);
        permisionLocation.setText(chkController.getLongitude() + "," + chkController.getLatitude());
        controllerName.setText(chkController.getSn());


//        List<String> controllerList = getControllerSnList(projectInfo.getControllerList());
//        if (controllerList != null && controllerList.size() > 0) {
//
//            controllerName.setOnClickListener(v -> {
//                String[] items =controllerList.toArray(new String[0]);
//                AlertDialog.Builder listDialog =
//                        new AlertDialog.Builder(mContext);
//                listDialog.setTitle(R.string.controller_list);
//
//                listDialog.setItems(items, (dialog, which) -> {
//                    controllerName.setText(items[which]);
//                    dialog.dismiss();
//                });
//
//               listDialog.show();
//            });
//        }
        List<Detonator> regDets = new ArrayList<>();
        List<Detonator> unRegDets = new ArrayList<>();
        int regNum = 1;
        int unregNum = 1;
        for (ChkDetonatorEntity chkDetonatorEntity : chkController.getChkDetonatorList()) {
            if (chkDetonatorEntity.getStatus() == 0) {
                Detonator detonator = new Detonator(chkDetonatorEntity);
                detonator.setNum(regNum++);
                regDets.add(detonator);

            } else {
                Detonator detonator = new Detonator(chkDetonatorEntity);
                detonator.setNum(unregNum++);
                unRegDets.add(detonator);
            }
        }


        controllerName.setText(chkController.getSn());
        initRegTableView(regDets);
        if (unRegDets.size() > 0) {
            initUnRegTableView(unRegDets);
        } else {
            detUnregTable.setVisibility(View.GONE);
        }

//        regDetonatorAdapter = new EasyDetonatorAdapter();
//        rvDetonatorReg.setLayoutManager(new LinearLayoutManager(this));
//        rvDetonatorReg.setAdapter(regDetonatorAdapter);
//        rvDetonatorReg.setBackgroundColor(getColor(R.color.btn_white_pressed));
//        regDetonatorAdapter.setNewData(regDets);
        detReg.setText("" + regDets.size());
        detReg.setTextColor(getColor(R.color.mediumseagreen));
//        rlNormal.setOnClickListener(v -> {
//            if(rvDetonatorReg.getVisibility()==View.VISIBLE){
//                rvDetonatorReg.setVisibility(View.GONE);
//            }else {
//                rvDetonatorReg.setVisibility(View.VISIBLE);
//            }
//
//        });
//        unusedDetonatorAdapter = new EasyDetonatorAdapter();
//        rvDetonatorUnused.setLayoutManager(new LinearLayoutManager(this));
//        rvDetonatorUnused.setAdapter(unusedDetonatorAdapter);
//        rvDetonatorUnused.setBackgroundColor(getColor(R.color.face_detect_fail));
//
//        unusedDetonatorAdapter.setNewData(unusedDets);
        detUnused.setText(" " + unRegDets.size());
        detUnused.setTextColor(getColor(R.color.red_normal));
//        rlUnused.setOnClickListener(v -> {
//            if(rvDetonatorUnused.getVisibility()==View.VISIBLE){
//                rvDetonatorUnused.setVisibility(View.GONE);
//            }else {
//                rvDetonatorUnused.setVisibility(View.VISIBLE);
//            }
//
//        });
//        List<DetonatorEntity> detonatorBeans = projectInfo.getDetonatorList();

        // Let's get TableView
//        initTableView(detonatorBeans);


    }

    private void initRegTableView(List<Detonator> detonatorList) {
//        detRegTable = findViewById(R.id.det_table);

//        WindowManager manager = this.getWindowManager();
//        DisplayMetrics outMetrics = new DisplayMetrics();
//        manager.getDefaultDisplay().getMetrics(outMetrics);
        int width2 = getWindowWidth();

        detRegTable.setHeaderNames("序号", "雷管编码", "状态");
        width2 = width2 - 30;
        detRegTable.setColumnWidth(0, width2 / 4);
        detRegTable.setColumnWidth(1, width2 / 2);
        detRegTable.setColumnWidth(2, width2 / 4);
//        detRegTable.setUnitTextColor(R.color.mediumseagreen);

        //其他可选设置项
        detRegTable.setUnitSelectable(false);//单元格处理事件的时候是否可以选中


//                                List<String[]> datas = new ArrayList<>();
        int size = detonatorList.size();
        String[][] detData = new String[size][3];
        int[][] detColor = new int[size][3];
        int i;
        for (i = 0; i < size; i++) {
            detData[i][0] = "" + (i + 1);
            detColor[i][0] = R.color.black;
            detData[i][1] = detonatorList.get(i).getDetCode();
            detColor[i][1] = R.color.black;
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

    private void initUnRegTableView(List<Detonator> detonatorList) {

        int width2 = getWindowWidth();

        detUnregTable.setHeaderNames("序号", "雷管编码", "状态");
        width2 = width2 - 30;
        detUnregTable.setColumnWidth(0, width2 / 4);
        detUnregTable.setColumnWidth(1, width2 / 2);
        detUnregTable.setColumnWidth(2, width2 / 4);
//        detRegTable.setUnitTextColor(R.color.mediumseagreen);

        //其他可选设置项
        detUnregTable.setUnitSelectable(false);//单元格处理事件的时候是否可以选中


//                                List<String[]> datas = new ArrayList<>();
        int size = detonatorList.size();
        String[][] detData = new String[size][3];
        int[][] detColor = new int[size][3];
        int i;
        for (i = 0; i < size; i++) {
            detData[i][0] = "" + (i + 1);
            detColor[i][0] = R.color.black;
            detData[i][1] = detonatorList.get(i).getDetCode();
            detColor[i][1] = R.color.black;
            if (detonatorList.get(i).getStatus() == 0) {
                detData[i][2] = "正常";
                detColor[i][2] = R.color.mediumseagreen;
            } else if (detonatorList.get(i).getStatus() == 1) {
                detData[i][2] = "未注册";
                detColor[i][2] = R.color.red_normal;
            } else if (detonatorList.get(i).getStatus() == 2) {
                detData[i][2] = "已使用";
                detColor[i][2] = R.color.blue;
            } else if (detonatorList.get(i).getStatus() == 3) {
                detData[i][2] = "不存在";
                detColor[i][2] = R.color.gray;
            }


        }

        detUnregTable.setmUnitTextColors(detColor);
        detUnregTable.setTableData(detData);
        detUnregTable.notifyAttributesChanged();
    }

    //    @OnClick(R.id.chk_del)
    public void delProject() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("确认删除此文件吗？");
        //设置对话框标题
//        builder.setIcon(R.drawable.profile_setting);

        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                showToast("你输入的是: " + edit.getText().toString());
//                ProInfoDao projectFileDao = new ProInfoDao(mContext);
//                projectFileDao.delete(projectInfo);
                DBManager.getInstance().getChkControllerEntityDao().delete(chkController);
                finish();

            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        // 4.设置常用api，并show弹出
        builder.setCancelable(true); //设置按钮是否可以按返回键取消,false则不可以取消
        AlertDialog dialog = builder.create(); //创建对话框
        dialog.setCanceledOnTouchOutside(true); //设置弹出框失去焦点是否隐藏,即点击屏蔽其它地方是否隐藏
        dialog.show();

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
        getMenuInflater().inflate(R.menu.menu_delete, menu);
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
            delProject();
        }

        return super.onOptionsItemSelected(item);
    }

    private View.OnTouchListener touchListener = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN
                    || event.getAction() == MotionEvent.ACTION_MOVE) {
                //按下或滑动时请求父节点不拦截子节点
                v.getParent().requestDisallowInterceptTouchEvent(true);
            }
            if (event.getAction() == MotionEvent.ACTION_UP) {
                //抬起时请求父节点拦截子节点
                v.getParent().requestDisallowInterceptTouchEvent(false);
            }
            return false;
        }
    };
}
=======
package com.etek.controller.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;

import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;

import android.view.MotionEvent;
import android.view.View;

import android.widget.TextView;

import com.bin.david.form.core.SmartTable;
import com.bin.david.form.core.TableConfig;
import com.bin.david.form.data.CellInfo;
import com.bin.david.form.data.column.Column;
import com.bin.david.form.data.format.bg.BaseBackgroundFormat;
import com.bin.david.form.data.format.bg.BaseCellBackgroundFormat;
import com.bin.david.form.data.format.bg.ICellBackgroundFormat;
import com.bin.david.form.data.format.draw.MultiLineDrawFormat;
import com.bin.david.form.data.format.tip.MultiLineBubbleTip;
import com.bin.david.form.data.style.FontStyle;
import com.bin.david.form.data.table.TableData;
import com.elvishew.xlog.XLog;
import com.etek.controller.R;

import com.etek.controller.adapter.EasyDetonatorAdapter;
import com.etek.controller.entity.Detonator;
import com.etek.controller.enums.DetStatusEnum;
import com.etek.controller.persistence.DBManager;
import com.etek.controller.persistence.entity.ChkControllerEntity;

import com.etek.controller.persistence.entity.ChkDetonatorEntity;
import com.etek.controller.persistence.entity.ControllerEntity;

import com.etek.controller.persistence.entity.ProjectInfoEntity;
import com.etek.controller.persistence.gen.ChkControllerEntityDao;

import com.etek.sommerlibrary.activity.BaseActivity;
import com.etek.sommerlibrary.utils.ToastUtils;
import com.etek.sommerlibrary.widget.TableView;


import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class CheckoutDetailActivity extends BaseActivity {


//    TableView detRegTable;

    ProjectInfoEntity projectInfo;

    ChkControllerEntity chkController;

    @BindView(R.id.pro_name)
    TextView projectName;
    @BindView(R.id.pro_code)
    TextView projectCode;
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

    @BindView(R.id.controller_list)
    TextView controllerName;
    @BindView(R.id.det_reg)
    TextView detReg;
    @BindView(R.id.det_unused)
    TextView detUnused;
//
//    @BindView(R.id.rv_detonator_reg)
//    RecyclerView rvDetonatorReg ;
//
//    @BindView(R.id.rv_detonator_unused)
//    RecyclerView rvDetonatorUnused ;

    @BindView(R.id.det_reg_table)
    TableView detRegTable;

    @BindView(R.id.det_unreg_table)
    TableView detUnregTable;


//    @BindView(R.id.rl_unused)
//    RelativeLayout rlUnused ;
//
//    @BindView(R.id.rl_normal)
//    RelativeLayout rlNormal ;

//    EasyDetonatorAdapter regDetonatorAdapter;
//
//    EasyDetonatorAdapter unusedDetonatorAdapter;


    //    @OnClick({R.id.pro_map_sel})
    public void getMap() {
        Intent intent = new Intent(mContext, ProMapActivity.class);
        intent.putExtra("projectId", projectInfo.getId());
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout_detail);
        ButterKnife.bind(this);
        initSupportActionBar(R.string.title_activity_checkout_detail);
        getProjectInfo();


    }

    private void getProjectInfo() {
        long proId = getIntent().getLongExtra("chkId", 0);

        if (proId > 0) {

            chkController = DBManager.getInstance().getChkControllerEntityDao().
                    queryBuilder()
                    .where(ChkControllerEntityDao.Properties.Id.eq(proId)).unique();
            XLog.d(chkController.toString());
            if (chkController != null) {
                projectInfo = chkController.getProjectInfoEntity();
            }
            if (projectInfo != null) {
                initView(projectInfo);
            } else {
                showToast("项目详情出错！");
            }

//            List<ChkDetonatorEntity> list2 = chkController.getChkDetonatorList();
//            XLog.i("ctl: ",StringUtils.join(list2.toArray(),separator));
//            List<ChkDetonatorEntity> list = DBManager.getInstance().getChkDetonatorEntityDao().
//                    queryBuilder()
//                    .where(ChkDetonatorEntityDao.Properties.ChkId.eq(chkController.getId())).list();
//
//            XLog.i("self: ",StringUtils.join(list.toArray(),separator));

        } else {
            ToastUtils.showCustom(mContext, "没有此项目!");
            finish();

        }


    }


    private void initView(ProjectInfoEntity projectInfo) {

        if (!StringUtils.isEmpty(projectInfo.getProCode())) {
            projectName.setText(projectInfo.getProName());
            proCodeTitle.setText(R.string.pro_code);
            projectCode.setText(projectInfo.getProCode());
            proNameTitle.setText(R.string.pro_name);
        } else if (!StringUtils.isEmpty(projectInfo.getContractCode())) {
            projectName.setText(projectInfo.getContractName());
            proCodeTitle.setText(R.string.contract_code);
            projectCode.setText(projectInfo.getContractCode());
            proNameTitle.setText(R.string.contract_name);
        } else {

            projectName.setText("");
            proCodeTitle.setText("");
            projectCode.setText("");
            proNameTitle.setText("");
        }
        projectName.setMovementMethod(ScrollingMovementMethod.getInstance());
        projectName.setOnTouchListener(touchListener);
        companyCode.setText(projectInfo.getCompanyCode());
//        companyName.setText(projectInfo.getCompanyName());
        permisionLocation = findViewById(R.id.permision_location);
        permisionLocation.setText(chkController.getLongitude() + "," + chkController.getLatitude());
        controllerName.setText(chkController.getSn());


//        List<String> controllerList = getControllerSnList(projectInfo.getControllerList());
//        if (controllerList != null && controllerList.size() > 0) {
//
//            controllerName.setOnClickListener(v -> {
//                String[] items =controllerList.toArray(new String[0]);
//                AlertDialog.Builder listDialog =
//                        new AlertDialog.Builder(mContext);
//                listDialog.setTitle(R.string.controller_list);
//
//                listDialog.setItems(items, (dialog, which) -> {
//                    controllerName.setText(items[which]);
//                    dialog.dismiss();
//                });
//
//               listDialog.show();
//            });
//        }
        List<Detonator> regDets = new ArrayList<>();
        List<Detonator> unRegDets = new ArrayList<>();
        int regNum = 1;
        int unregNum = 1;
        for (ChkDetonatorEntity chkDetonatorEntity : chkController.getChkDetonatorList()) {
            if (chkDetonatorEntity.getStatus() == 0) {
                Detonator detonator = new Detonator(chkDetonatorEntity);
                detonator.setNum(regNum++);
                regDets.add(detonator);

            } else {
                Detonator detonator = new Detonator(chkDetonatorEntity);
                detonator.setNum(unregNum++);
                unRegDets.add(detonator);
            }
        }


        controllerName.setText(chkController.getSn());
        initRegTableView(regDets);
        if (unRegDets.size() > 0) {
            initUnRegTableView(unRegDets);
        } else {
            detUnregTable.setVisibility(View.GONE);
        }

//        regDetonatorAdapter = new EasyDetonatorAdapter();
//        rvDetonatorReg.setLayoutManager(new LinearLayoutManager(this));
//        rvDetonatorReg.setAdapter(regDetonatorAdapter);
//        rvDetonatorReg.setBackgroundColor(getColor(R.color.btn_white_pressed));
//        regDetonatorAdapter.setNewData(regDets);
        detReg.setText("" + regDets.size());
        detReg.setTextColor(getColor(R.color.mediumseagreen));
//        rlNormal.setOnClickListener(v -> {
//            if(rvDetonatorReg.getVisibility()==View.VISIBLE){
//                rvDetonatorReg.setVisibility(View.GONE);
//            }else {
//                rvDetonatorReg.setVisibility(View.VISIBLE);
//            }
//
//        });
//        unusedDetonatorAdapter = new EasyDetonatorAdapter();
//        rvDetonatorUnused.setLayoutManager(new LinearLayoutManager(this));
//        rvDetonatorUnused.setAdapter(unusedDetonatorAdapter);
//        rvDetonatorUnused.setBackgroundColor(getColor(R.color.face_detect_fail));
//
//        unusedDetonatorAdapter.setNewData(unusedDets);
        detUnused.setText(" " + unRegDets.size());
        detUnused.setTextColor(getColor(R.color.red_normal));
//        rlUnused.setOnClickListener(v -> {
//            if(rvDetonatorUnused.getVisibility()==View.VISIBLE){
//                rvDetonatorUnused.setVisibility(View.GONE);
//            }else {
//                rvDetonatorUnused.setVisibility(View.VISIBLE);
//            }
//
//        });
//        List<DetonatorEntity> detonatorBeans = projectInfo.getDetonatorList();

        // Let's get TableView
//        initTableView(detonatorBeans);


    }

    private void initRegTableView(List<Detonator> detonatorList) {
//        detRegTable = findViewById(R.id.det_table);

//        WindowManager manager = this.getWindowManager();
//        DisplayMetrics outMetrics = new DisplayMetrics();
//        manager.getDefaultDisplay().getMetrics(outMetrics);
        int width2 = getWindowWidth();

        detRegTable.setHeaderNames("序号", "雷管编码", "状态");
        width2 = width2 - 30;
        detRegTable.setColumnWidth(0, width2 / 4);
        detRegTable.setColumnWidth(1, width2 / 2);
        detRegTable.setColumnWidth(2, width2 / 4);
//        detRegTable.setUnitTextColor(R.color.mediumseagreen);

        //其他可选设置项
        detRegTable.setUnitSelectable(false);//单元格处理事件的时候是否可以选中


//                                List<String[]> datas = new ArrayList<>();
        int size = detonatorList.size();
        String[][] detData = new String[size][3];
        int[][] detColor = new int[size][3];
        int i;
        for (i = 0; i < size; i++) {
            detData[i][0] = "" + (i + 1);
            detColor[i][0] = R.color.black;
            detData[i][1] = detonatorList.get(i).getDetCode();
            detColor[i][1] = R.color.black;
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

    private void initUnRegTableView(List<Detonator> detonatorList) {

        int width2 = getWindowWidth();

        detUnregTable.setHeaderNames("序号", "雷管编码", "状态");
        width2 = width2 - 30;
        detUnregTable.setColumnWidth(0, width2 / 4);
        detUnregTable.setColumnWidth(1, width2 / 2);
        detUnregTable.setColumnWidth(2, width2 / 4);
//        detRegTable.setUnitTextColor(R.color.mediumseagreen);

        //其他可选设置项
        detUnregTable.setUnitSelectable(false);//单元格处理事件的时候是否可以选中


//                                List<String[]> datas = new ArrayList<>();
        int size = detonatorList.size();
        String[][] detData = new String[size][3];
        int[][] detColor = new int[size][3];
        int i;
        for (i = 0; i < size; i++) {
            detData[i][0] = "" + (i + 1);
            detColor[i][0] = R.color.black;
            detData[i][1] = detonatorList.get(i).getDetCode();
            detColor[i][1] = R.color.black;
            if (detonatorList.get(i).getStatus() == 0) {
                detData[i][2] = "正常";
                detColor[i][2] = R.color.mediumseagreen;
            } else if (detonatorList.get(i).getStatus() == 1) {
                detData[i][2] = "未注册";
                detColor[i][2] = R.color.red_normal;
            } else if (detonatorList.get(i).getStatus() == 2) {
                detData[i][2] = "已使用";
                detColor[i][2] = R.color.blue;
            } else if (detonatorList.get(i).getStatus() == 3) {
                detData[i][2] = "不存在";
                detColor[i][2] = R.color.gray;
            }


        }

        detUnregTable.setmUnitTextColors(detColor);
        detUnregTable.setTableData(detData);
        detUnregTable.notifyAttributesChanged();
    }

    //    @OnClick(R.id.chk_del)
    public void delProject() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("确认删除此文件吗？");
        //设置对话框标题
//        builder.setIcon(R.drawable.profile_setting);

        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                showToast("你输入的是: " + edit.getText().toString());
//                ProInfoDao projectFileDao = new ProInfoDao(mContext);
//                projectFileDao.delete(projectInfo);
                DBManager.getInstance().getChkControllerEntityDao().delete(chkController);
                finish();

            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        // 4.设置常用api，并show弹出
        builder.setCancelable(true); //设置按钮是否可以按返回键取消,false则不可以取消
        AlertDialog dialog = builder.create(); //创建对话框
        dialog.setCanceledOnTouchOutside(true); //设置弹出框失去焦点是否隐藏,即点击屏蔽其它地方是否隐藏
        dialog.show();

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
        getMenuInflater().inflate(R.menu.menu_delete, menu);
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
            delProject();
        }

        return super.onOptionsItemSelected(item);
    }

    private View.OnTouchListener touchListener = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN
                    || event.getAction() == MotionEvent.ACTION_MOVE) {
                //按下或滑动时请求父节点不拦截子节点
                v.getParent().requestDisallowInterceptTouchEvent(true);
            }
            if (event.getAction() == MotionEvent.ACTION_UP) {
                //抬起时请求父节点拦截子节点
                v.getParent().requestDisallowInterceptTouchEvent(false);
            }
            return false;
        }
    };
}
>>>>>>> 806c842... 雷管组网
