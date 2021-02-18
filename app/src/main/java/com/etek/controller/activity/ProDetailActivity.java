package com.etek.controller.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.method.ScrollingMovementMethod;
import android.text.method.SingleLineTransformationMethod;
import android.util.DisplayMetrics;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.elvishew.xlog.XLog;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;

import android.widget.Spinner;
import android.widget.TextView;

import com.bin.david.form.core.SmartTable;
import com.bin.david.form.core.TableConfig;
import com.bin.david.form.data.CellInfo;
import com.bin.david.form.data.column.Column;
import com.bin.david.form.data.format.bg.BaseBackgroundFormat;
import com.bin.david.form.data.format.bg.BaseCellBackgroundFormat;
import com.bin.david.form.data.format.bg.ICellBackgroundFormat;
import com.bin.david.form.data.format.draw.MultiLineDrawFormat;
import com.bin.david.form.data.format.draw.TextDrawFormat;
import com.bin.david.form.data.format.tip.MultiLineBubbleTip;
import com.bin.david.form.data.style.FontStyle;
import com.bin.david.form.data.table.TableData;
import com.etek.controller.R;

import com.etek.controller.entity.Detonator;
import com.etek.controller.enums.DetStatusEnum;
import com.etek.controller.persistence.DBManager;

import com.etek.controller.persistence.entity.ChkControllerEntity;
import com.etek.controller.persistence.entity.ControllerEntity;
import com.etek.controller.persistence.entity.DetonatorEntity;
import com.etek.controller.persistence.entity.PermissibleZoneEntity;
import com.etek.controller.persistence.entity.ProjectInfoEntity;
import com.etek.controller.persistence.gen.ChkControllerEntityDao;
import com.etek.controller.persistence.gen.ProjectInfoEntityDao;


import com.etek.controller.widget.MarqueeText;
import com.etek.sommerlibrary.activity.BaseActivity;
import com.etek.sommerlibrary.utils.ToastUtils;

import com.etek.sommerlibrary.widget.TableView;

import org.apache.commons.lang3.StringUtils;


import java.util.ArrayList;
import java.util.List;

public class ProDetailActivity extends BaseActivity {


    ProjectInfoEntity projectInfo;

    @BindView(R.id.pro_name)
    TextView projectName;
    @BindView(R.id.pro_code)
    TextView projectCode;
    @BindView(R.id.contract_code)
    TextView contractCode;
    @BindView(R.id.contract_name)
    TextView contractName;
    @BindView(R.id.company_code)
    TextView companyCode;
    @BindView(R.id.company_name)
    TextView companyName;
    @BindView(R.id.permision_location)
    TextView permisionLocation;

//    @BindView(R.id.det_table)
//    SmartTable table;

    @BindView(R.id.det_table)
    TableView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pro_detail);
        ButterKnife.bind(this);
        initSupportActionBar(R.string.title_activity_pro_detail);
        getProjectInfo();
    }

    private void getProjectInfo() {
        long proId = getIntent().getLongExtra("projectId", 0);

        if (proId > 0) {
            projectInfo = DBManager.getInstance().getProjectInfoEntityDao().
                    queryBuilder()
                    .where(ProjectInfoEntityDao.Properties.Id.eq(proId)).uniqueOrThrow();
            XLog.v(projectInfo.toString());
            initView(projectInfo);
        } else {
            ToastUtils.showCustom(mContext, "没有此项目!");
            finish();

        }
    }

    private void delProject() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("与该项目相关的规则检查列表也会被删除，您确认要删除此项目信息吗？");
        //设置对话框标题
//        builder.setIcon(R.drawable.profile_setting);


        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                showToast("你输入的是: " + edit.getText().toString());
//                ProInfoDao projectFileDao = new ProInfoDao(mContext);
//                projectFileDao.delete(projectInfo);
                List<ChkControllerEntity> chkControllerEntityList = DBManager.getInstance().getChkControllerEntityDao().queryBuilder()
                        .where(ChkControllerEntityDao.Properties.ProjectInfoId.eq(projectInfo.getId())).list();
                for (ChkControllerEntity chkControllerEntity : chkControllerEntityList) {
                    DBManager.getInstance().getChkControllerEntityDao().delete(chkControllerEntity);
                }
                DBManager.getInstance().getProjectInfoEntityDao().delete(projectInfo);

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

    private void initView(ProjectInfoEntity projectInfo) {


        projectName.setText(projectInfo.getProName());

        projectCode.setText(projectInfo.getProCode());

        contractCode.setText(projectInfo.getContractCode());

        contractName.setText(projectInfo.getContractName());
        contractName.setMovementMethod(ScrollingMovementMethod.getInstance());
        companyCode.setText(projectInfo.getCompanyCode());

        companyName.setText(projectInfo.getCompanyName());

        List<PermissibleZoneEntity> permissibleZoneList = projectInfo.getPermissibleZoneList();
        if (!permissibleZoneList.isEmpty()) {
            PermissibleZoneEntity permissibleZoneEntity = permissibleZoneList.get(0);
            permisionLocation.setText(permissibleZoneEntity.getLongitude() + "," + permissibleZoneEntity.getLatitude());
        }


        Spinner controllerName = findViewById(R.id.controller_list);
        List<String> controllerList = getControllerSnList(projectInfo.getControllerList());
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(mContext,
                R.layout.item_easy_controller, controllerList);
        controllerName.setAdapter(spinnerAdapter);
//        if (controllerList != null && controllerList.size() > 0) {
//            controllerName.setText(controllerList.get(0));
//            controllerName.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    String[] items = (String[]) controllerList.toArray(new String[0]);
//                    AlertDialog.Builder listDialog =
//                            new AlertDialog.Builder(mContext);
//                    listDialog.setTitle(R.string.controller_list);
//
//                    listDialog.setItems(items, new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            controllerName.setText(items[which]);
//                            dialog.dismiss();
//                        }
//                    });
//
//                   listDialog.show();
//                }
//            });
//        }


        List<DetonatorEntity> detonatorEntitys = projectInfo.getDetonatorList();
        List<Detonator> detonatorList = new ArrayList<>();
        int num = 1;
        for (DetonatorEntity detonatorEntity : detonatorEntitys) {
            Detonator detonator = new Detonator(detonatorEntity);
            detonator.setNum(num++);
            detonatorList.add(detonator);
        }

        // Let's get TableView
        initCommonTableView(detonatorList);

//        Button selectMap = findViewById(R.id.pro_map_sel);
//        selectMap.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(mContext, ProMapActivity.class);
//                intent.putExtra("projectId", projectInfo.getId());
//                startActivity(intent);
//            }
//        });

//        Button delPro = findViewById(R.id.pro_del);
//        delPro.setOnClickListener(v -> delProject());

    }

//    private void initTableView(List<Detonator> mlist) {
//
////        final List<Detonator> mlist = detController.getDetList();
//
//        int width2 = getWindowWidth();
////        showStatusDialog("宽度："+width2);
//        int columnWidth = (int) (width2/2);
//        final Column<String> codeColumn = new Column<>("雷管编号", "detCode", new MultiLineDrawFormat<>(columnWidth));
//
////        codeColumn.setFixed(true);
//        final Column<Integer> numColumn = new Column<>("序号", "num", new MultiLineDrawFormat<Integer>(width2 / 12));
//        numColumn.setFixed(true);
//
//        final Column stateColumn = new Column<>("状态", "statusName", new MultiLineDrawFormat<String>(width2 / 6));
//        stateColumn.setAutoCount(true);
////        stateColumn.setFixed(true);
////        ageColumn.setAutoCount(true);
////        int imgSize = DensityUtils.dp2px(this,25);
//        String title = getMyString(R.string.rpt_det_list)+": "+mlist.size();
//
//        final TableData<Detonator> tableData = new TableData<>(title, mlist, numColumn, codeColumn, stateColumn);
//        tableData.setShowCount(false);
//        table.getConfig().setShowTableTitle(true);
//
//        table.getConfig().setColumnTitleBackground(new BaseBackgroundFormat(getMyColor(R.color.windows_bg)));
////        table.getConfig().setCountBackground(new BaseBackgroundFormat(getMyColor(R.color.windows_bg)));
////        tableData.setOnItemClickListener(new TableData.OnItemClickListener() {
////            @Override
////            public void onClick(Column column, String value, Object o, int col, int row) {
////
////            }
////        });
//
//        FontStyle fontStyle = new FontStyle();
//        fontStyle.setTextColor(getResources().getColor(android.R.color.white));
//        MultiLineBubbleTip<Column> tip = new MultiLineBubbleTip<Column>(this, R.mipmap.round_rect, R.mipmap.triangle, fontStyle) {
//            @Override
//            public boolean isShowTip(Column column, int position) {
//                if (column == codeColumn) {
//                    return true;
//                }
//                return false;
//            }
//
//
//            @Override
//            public String[] format(Column column, int position) {
//                Detonator data = mlist.get(position);
//                String[] strings = {"UID： " + data.getUid(), "雷管发编号：" + data.getDetCode()};
//                return strings;
//            }
//        };
//        tip.setColorFilter(getMyColor(R.color.chat_item2_normal));
//        tip.setAlpha(0.8f);
//        table.getProvider().setTip(tip);
//
//        table.getConfig().setTableTitleStyle(new FontStyle(this, width2/48, getResources().getColor(R.color.arc1)).setAlign(Paint.Align.LEFT))
//                .setShowXSequence(false).setShowYSequence(false);
//        table.getConfig().setColumnTitleStyle(new FontStyle(this, width2/48, getResources().getColor(R.color.black)).setAlign(Paint.Align.CENTER));
//
//        ICellBackgroundFormat<CellInfo> backgroundFormat = new BaseCellBackgroundFormat<CellInfo>() {
//            @Override
//            public int getBackGroundColor(CellInfo cellInfo) {
////                if(cellInfo.row %2 == 0 && cellInfo.col>=2) {
////                    return ContextCompat.getColor(mContext, R.color.content_bg);
////                }
//                return TableConfig.INVALID_COLOR;
//            }
//
//            @Override
//            public int getTextColor(CellInfo cellInfo) {
////                XLog.d("cellInfo:"+JSON.toJSONString(cellInfo));
//                if (cellInfo.col == 2) {
//                    int status = mlist.get(cellInfo.row).getStatus();
//                    DetStatusEnum statusEnum = DetStatusEnum.getByStatus(status);
//                    assert statusEnum != null;
//                    return ContextCompat.getColor(mContext,statusEnum.getColor());
//
//
//                }
//                return TableConfig.INVALID_COLOR;
//            }
//        };
//        table.getConfig().setContentCellBackgroundFormat(backgroundFormat);
//        table.getConfig().setContentStyle(new FontStyle(width2/22,getResources().getColor(R.color.black)));
//
//        table.setTableData(tableData);
//
//    }

    private void initCommonTableView(List<Detonator> detonatorList) {
//        tv = findViewById(R.id.det_table);

//        WindowManager manager = this.getWindowManager();
//        DisplayMetrics outMetrics = new DisplayMetrics();
//        manager.getDefaultDisplay().getMetrics(outMetrics);
        int width2 = getWindowWidth();

        tv.setHeaderNames("序号", "雷管编码", "状态");
        width2 = width2 - 30;
        tv.setColumnWidth(0, width2 / 4);
        tv.setColumnWidth(1, width2 / 2);
        tv.setColumnWidth(2, width2 / 4);
//        tv.setUnitTextColor(R.color.mediumseagreen);

        //其他可选设置项
        tv.setUnitSelectable(false);//单元格处理事件的时候是否可以选中


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
                detData[i][2] = "黑名单";
                detColor[i][2] = R.color.red_normal;
            } else if (detonatorList.get(i).getStatus() == 2) {
                detData[i][2] = "已使用";
                detColor[i][2] = R.color.blue;
            } else if (detonatorList.get(i).getStatus() == 3) {
                detData[i][2] = "不存在";
                detColor[i][2] = R.color.gray;
            }


        }

        tv.setmUnitTextColors(detColor);
        tv.setTableData(detData);
        tv.notifyAttributesChanged();


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

        if (id == R.id.action_map) {


        }


        return super.onOptionsItemSelected(item);
    }
}
