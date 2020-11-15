package com.etek.controller.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import com.elvishew.xlog.XLog;
import com.etek.controller.R;
import com.etek.controller.entity.Detonator;
import com.etek.controller.persistence.DBManager;
import com.etek.controller.persistence.entity.DetonatorEntity;
import com.etek.controller.persistence.entity.ProjectInfoEntity;
import com.etek.controller.persistence.gen.DetonatorEntityDao;
import com.etek.controller.persistence.gen.ProjectInfoEntityDao;
import com.etek.controller.utils.JsonUtils;
import com.etek.sommerlibrary.activity.BaseActivity;
import com.etek.sommerlibrary.utils.ToastUtils;
import com.etek.sommerlibrary.widget.TableView;
import org.apache.commons.lang3.StringUtils;
import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;


public class CheckoutDetailActivity2 extends BaseActivity {

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

    @BindView(R.id.permision_location)
    TextView permisionLocation;

    @BindView(R.id.controller_list)
    TextView controllerName;

    @BindView(R.id.det_reg)
    TextView detReg;

    @BindView(R.id.det_unused)
    TextView detUnused;

    @BindView(R.id.det_reg_table)
    TableView detRegTable;

    @BindView(R.id.det_unreg_table)
    TableView detUnregTable;

    private ProjectInfoEntity projectInfoEntity;
    private double latitude;
    private double longitude;
    private long proId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout_detail);
        ButterKnife.bind(this);
        initSupportActionBar(R.string.title_activity_checkout_detail);
        getProjectInfo();
    }

    private void getProjectInfo() {
        proId = getIntent().getLongExtra("chkId", 0);
        latitude = getIntent().getDoubleExtra("latitude", 0);
        longitude = getIntent().getDoubleExtra("longitude", 0);
        if (proId > 0) {
            projectInfoEntity = DBManager.getInstance().getProjectInfoEntityDao().queryBuilder()
                    .where(ProjectInfoEntityDao.Properties.Id.eq(proId)).unique();
            XLog.d(projectInfoEntity.toString());
//            JsonUtils.monitDetonatorEntity(proId);//模拟雷管数据
            if (projectInfoEntity != null) {
                initView(projectInfoEntity);
            } else {
                showToast("项目详情出错！");
            }
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
        permisionLocation = findViewById(R.id.permision_location);
        permisionLocation.setText(longitude + " , " + latitude);
        controllerName.setText("61000255");
        List<DetonatorEntity> regDets = new ArrayList<>();
        List<DetonatorEntity> unRegDets = new ArrayList<>();
        int regNum = 1;
        int unregNum = 1;
        List<DetonatorEntity> list = DBManager.getInstance().getDetonatorEntityDao().queryBuilder().where(DetonatorEntityDao.Properties.ProjectInfoId.eq(proId)).list();
        Log.e("雷管E联","DetonatorEntity: " + list.size());
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getStatus() == 0) {
                regDets.add(list.get(i));
            } else {
                unRegDets.add(list.get(i));
            }
        }

        controllerName.setText("61000255");
        initRegTableView(regDets);
        if (unRegDets.size() > 0) {
            initUnRegTableView(unRegDets);
        } else {
            detUnregTable.setVisibility(View.GONE);
        }

        detReg.setText("" + regDets.size());
        detReg.setTextColor(getColor(R.color.mediumseagreen));
        detUnused.setText(" " + unRegDets.size());
        detUnused.setTextColor(getColor(R.color.red_normal));
    }

    private void initRegTableView(List<DetonatorEntity> detonatorEntityList) {
        int width2 = getWindowWidth();
        detRegTable.setHeaderNames("序号", "雷管编码", "状态");
        width2 = width2 - 30;
        detRegTable.setColumnWidth(0, width2 / 4);
        detRegTable.setColumnWidth(1, width2 / 2);
        detRegTable.setColumnWidth(2, width2 / 4);
        //其他可选设置项
        detRegTable.setUnitSelectable(false);//单元格处理事件的时候是否可以选中
        int size = detonatorEntityList.size();
        String[][] detData = new String[size][3];
        int[][] detColor = new int[size][3];
        int i;
        for (i = 0; i < size; i++) {
            detData[i][0] = "" + (i + 1);
            detColor[i][0] = R.color.black;
            detData[i][1] = detonatorEntityList.get(i).getCode();
            detColor[i][1] = R.color.black;
            if (detonatorEntityList.get(i).getStatus() == 0) {
                detData[i][2] = "已注册";
                detColor[i][2] = R.color.mediumseagreen;
            } else if (detonatorEntityList.get(i).getStatus() == 1) {
                detData[i][2] = "黑名单";
                detColor[i][2] = R.color.red_normal;
            } else if (detonatorEntityList.get(i).getStatus() == 2) {
                detData[i][2] = "已使用";
                detColor[i][2] = R.color.blue;
            } else if (detonatorEntityList.get(i).getStatus() == 3) {
                detData[i][2] = "不存在";
                detColor[i][2] = R.color.gray;
            } else if (detonatorEntityList.get(i).getStatus() == 4) {
                detData[i][2] = "未校验";
                detColor[i][2] = R.color.gray;
            }
        }
        detRegTable.setmUnitTextColors(detColor);
        detRegTable.setTableData(detData);
        detRegTable.notifyAttributesChanged();
    }

    private void initUnRegTableView(List<DetonatorEntity> detonatorEntityList) {
        int width2 = getWindowWidth();
        detUnregTable.setHeaderNames("序号", "雷管编码", "状态");
        width2 = width2 - 30;
        detUnregTable.setColumnWidth(0, width2 / 4);
        detUnregTable.setColumnWidth(1, width2 / 2);
        detUnregTable.setColumnWidth(2, width2 / 4);
        //其他可选设置项
        detUnregTable.setUnitSelectable(false);//单元格处理事件的时候是否可以选中
        int size = detonatorEntityList.size();
        String[][] detData = new String[size][3];
        int[][] detColor = new int[size][3];
        int i;
        for (i = 0; i < size; i++) {
            detData[i][0] = "" + (i + 1);
            detColor[i][0] = R.color.black;
            detData[i][1] = detonatorEntityList.get(i).getCode();
            detColor[i][1] = R.color.black;
            if (detonatorEntityList.get(i).getStatus() == 0) {
                detData[i][2] = "正常";
                detColor[i][2] = R.color.mediumseagreen;
            } else if (detonatorEntityList.get(i).getStatus() == 1) {
                detData[i][2] = "未注册";
                detColor[i][2] = R.color.red_normal;
            } else if (detonatorEntityList.get(i).getStatus() == 2) {
                detData[i][2] = "已使用";
                detColor[i][2] = R.color.blue;
            } else if (detonatorEntityList.get(i).getStatus() == 3) {
                detData[i][2] = "不存在";
                detColor[i][2] = R.color.gray;
            }
        }
        detUnregTable.setmUnitTextColors(detColor);
        detUnregTable.setTableData(detData);
        detUnregTable.notifyAttributesChanged();
    }

    public void delProject() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("确认删除此文件吗？");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //删除项目及雷管
                DBManager.getInstance().getProjectInfoEntityDao().delete(projectInfoEntity);
                DBManager.getInstance().getDetonatorEntityDao().queryBuilder().where(DetonatorEntityDao.Properties.ProjectInfoId.eq(proId)).buildDelete();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_delete, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_delete) {
            delProject();
        }
        return super.onOptionsItemSelected(item);
    }

    private View.OnTouchListener touchListener = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {
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
