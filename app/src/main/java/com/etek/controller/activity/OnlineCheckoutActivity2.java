package com.etek.controller.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import com.alibaba.fastjson.JSON;
import com.elvishew.xlog.XLog;
import com.etek.controller.R;
import com.etek.controller.adapter.CheckOutAdapter2;
import com.etek.controller.common.AppConstants;
import com.etek.controller.common.Globals;
import com.etek.controller.dto.BLECmd;
import com.etek.controller.dto.ProjectInfoDto;
import com.etek.controller.entity.DetController;
import com.etek.controller.entity.Detonator;
import com.etek.controller.enums.CheckRuleEnum;
import com.etek.controller.persistence.DBManager;
import com.etek.controller.persistence.entity.DetonatorEntity;
import com.etek.controller.persistence.entity.ProjectInfoEntity;
import com.etek.controller.persistence.gen.ProjectInfoEntityDao;
import com.etek.controller.utils.AsyncHttpCilentUtil;
import com.etek.controller.utils.BeanPropertiesUtil;
import com.etek.controller.utils.DetUtil;
import com.etek.sommerlibrary.activity.BaseActivity;
import com.etek.sommerlibrary.utils.DateUtil;
import com.etek.sommerlibrary.utils.FileUtils;
import org.apache.commons.lang3.StringUtils;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class OnlineCheckoutActivity2 extends BaseActivity implements CheckOutAdapter2.OnItemClickListener {
    @BindView(R.id.rv_project_info)
    RecyclerView prv;

    @BindView(R.id.sl_project_info)
    SwipeRefreshLayout psl;

    @BindView(R.id.img_loading)
    ImageView img_loading;

    private DetController detController;
    private ProjectInfoEntity projectInfo;
    private CheckOutAdapter2 mProjectInfoAdapter;
    private static final int PAGE_SIZE = 10;
    private int mNextRequestPage = 1;
    private boolean isValid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_checkout2);
        ButterKnife.bind(this);
        initSupportActionBar(R.string.title_activity_checkout);
        initData();
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh();
    }

    private void initData() {
        detController = new DetController();
        detController.setUserIDCode(Globals.user.getIdCode());
        long proid = getLongInfo("projectId");
        XLog.d("proid: ", proid);
        projectInfo = DBManager.getInstance().getProjectInfoEntityDao().queryBuilder().where(ProjectInfoEntityDao.Properties.Id.eq(proid)).unique();
    }

    private void initView() {
        prv.setLayoutManager(new LinearLayoutManager(mContext));
        psl.setColorSchemeColors(Color.rgb(47, 223, 189));
        psl.setRefreshing(true);
        mProjectInfoAdapter = new CheckOutAdapter2();
        mProjectInfoAdapter.setOnLoadMoreListener(() -> new Handler().post(() -> loadMore()));
        prv.setAdapter(mProjectInfoAdapter);
        mProjectInfoAdapter.setOnItemClickListener(this);
        psl.setOnRefreshListener(() -> refresh());
    }

    /**
     * 条目的点击事件
     */
    @Override
    public void onItemCLick(ProjectInfoEntity projectInfoEntity, int position) {
        Intent intent = new Intent(mContext, CheckoutDetailActivity2.class);
        intent.putExtra("chkId", projectInfoEntity.getId());
        intent.putExtra("longitude",getStringInfo("Longitude"));
        intent.putExtra("latitude",getStringInfo("Latitude"));
        startActivity(intent);
    }

    /**
     * 校验的点击事件
     */
    @Override
    public void onCheckOutClick(ProjectInfoEntity projectInfoEntity, int position) {
        getVerifyResult();//该验证方式后续可能会改变
    }

    /**
     * 下拉刷下
     */
    private void refresh() {
        XLog.v("数据更新! ");
        mNextRequestPage = 1;
        mProjectInfoAdapter.setEnableLoadMore(false);//这里的作用是防止下拉刷新的时候还可以上拉加载
        List<ProjectInfoEntity> datas = DBManager.getInstance().getProjectInfoEntityDao().queryBuilder()
                .orderDesc(ProjectInfoEntityDao.Properties.Id)
                .limit(PAGE_SIZE)
                .build()
                .list();
        setChkData(true, datas);
        mProjectInfoAdapter.setEnableLoadMore(true);
        psl.setRefreshing(false);
    }

    /**
     * 上拉加载
     */
    private void loadMore() {
        XLog.v("加载更多! ");
        int offset = (mNextRequestPage - 1) * PAGE_SIZE;
        int limit = offset + PAGE_SIZE;
        List<ProjectInfoEntity> datas = DBManager.getInstance().getProjectInfoEntityDao().queryBuilder()
                .orderDesc(ProjectInfoEntityDao.Properties.Id)
                .offset(offset)
                .limit(limit)
                .build()
                .list();
        boolean isRefresh = mNextRequestPage == 1;
        setChkData(isRefresh, datas);
    }

    /**
     * 设置数据刷新页面
     */
    private void setChkData(boolean isRefresh, List datas) {
        mNextRequestPage++;
        final int size = datas == null ? 0 : datas.size();
        if (prv.isComputingLayout())
            return;
        if (isRefresh) {
            mProjectInfoAdapter.setNewData(datas);
        } else {
            if (size > 0) {
                mProjectInfoAdapter.addData(datas);
            }
        }
        if (size < PAGE_SIZE) {
            //第一页如果不够一页就不显示没有更多数据布局
            mProjectInfoAdapter.loadMoreEnd(isRefresh);
        } else {
            mProjectInfoAdapter.loadMoreComplete();
        }
    }

    /**
     * 验证
     */
    private BLECmd getVerifyResult() {
        isValid = false;
        BLECmd bleCmd;
        bleCmd = BLECmd.getVerify(CheckRuleEnum.SUCCESS.getCode(), 0, 0);

        boolean isControllerValid = false;

//        for (ControllerEntity detControllerValid : projectInfo.getControllerList()) {
//            if (detController.getSn().equalsIgnoreCase(detControllerValid.getName())) {
//                isControllerValid = true;
//            }
//        }

        if (!isControllerValid) {
            bleCmd = BLECmd.getVerify(CheckRuleEnum.OUT_CONTROLLER.getCode(), 0, 0);
            showStatusDialog(CheckRuleEnum.OUT_CONTROLLER.getMessage());
            return bleCmd;
        }

        int unUsedDet = projectInfo.getDetonatorList().size();
        int unRegDet = 0;
        boolean isUnreg;

        for (Detonator conDet : detController.getDetList()) {
            isUnreg = true;
            for (DetonatorEntity infoDet : projectInfo.getDetonatorList()) {
                String cUid = conDet.getUid().substring(conDet.getUid().length() - 8, conDet.getUid().length());
                String iUid = conDet.getUid().substring(infoDet.getUid().length() - 8, infoDet.getUid().length());
                XLog.v("verfiryUid: ", cUid, iUid);
                if (conDet.getDetCode().equalsIgnoreCase(infoDet.getCode()) && cUid.equalsIgnoreCase(iUid)) {
                    XLog.v("verfiryCode conDet:  infoDet:", conDet, infoDet);
                    if (!DetUtil.getAcCodeFromDet(infoDet).equalsIgnoreCase(infoDet.getWorkCode()))
                        break;
                    unUsedDet--;
                    if (unUsedDet < 0) {
                        unUsedDet = 0;
                    }
                    isUnreg = false;
                    conDet.setStatus(0);
                }
            }
            if (isUnreg) {
                conDet.setStatus(1);
                unRegDet++;
            }
        }

        if (unRegDet > 0) {
            isValid = true;
            showStatusDialog(CheckRuleEnum.UNREG_DET.getMessage() + unRegDet);
            bleCmd = BLECmd.getVerify(CheckRuleEnum.ERR_DET.getCode(), unRegDet, 0);
            return bleCmd;
        }
        isValid = true;
        showStatusDialog(CheckRuleEnum.SUCCESS.getMessage());
        return bleCmd;
    }

    /**
     * 发送检测报告
     */
    private void sendCheckoutReport() {
        String rptJson = getReportDto();
        String fdName = "report_info" + DateUtil.getDateDoc(new Date()) + ".json";
        FileUtils.saveFileToSDcard("detonator/json", fdName, rptJson);
        showToast("保存完成！");
        String url = AppConstants.ETEKTestServer + AppConstants.CheckoutReport;
        AsyncHttpCilentUtil.httpPostJson(url, rptJson, new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                XLog.e("IOException:", e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String respStr = response.body().string();
                if (!StringUtils.isEmpty(respStr)) {
                    XLog.w("respStr is  ", respStr);
                }
            }
        });
    }


    private String getReportDto() {
        ProjectInfoDto projectInfoDto = new ProjectInfoDto();
        try {
            BeanPropertiesUtil.copyProperties(projectInfo, projectInfoDto);
        } catch (Exception e) {
            e.printStackTrace();
        }
        projectInfoDto.setCreateTime(new Date());
        detController.setContractId("");
        projectInfoDto.addDetControllers(detController);
        return JSON.toJSONString(projectInfoDto);
    }
}
