package com.etek.controller.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import com.alibaba.fastjson.JSON;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.elvishew.xlog.XLog;
import com.etek.controller.R;
import com.etek.controller.adapter.CheckOutAdapter;
import com.etek.controller.common.AppConstants;
import com.etek.controller.common.Globals;
import com.etek.controller.dto.BLECmd;
import com.etek.controller.dto.BLEDevResp;
import com.etek.controller.dto.ProjectInfoDto;
import com.etek.controller.entity.DetController;
import com.etek.controller.entity.Detonator;
import com.etek.controller.enums.CheckRuleEnum;
import com.etek.controller.enums.DevCommonEnum;
import com.etek.controller.persistence.DBManager;
import com.etek.controller.persistence.entity.ChkControllerEntity;
import com.etek.controller.persistence.entity.ChkDetonatorEntity;
import com.etek.controller.persistence.entity.ControllerEntity;
import com.etek.controller.persistence.entity.DetonatorEntity;
import com.etek.controller.persistence.entity.ProjectInfoEntity;
import com.etek.controller.persistence.gen.ChkControllerEntityDao;
import com.etek.controller.persistence.gen.ProjectInfoEntityDao;
import com.etek.controller.utils.AsyncHttpCilentUtil;
import com.etek.controller.utils.BeanPropertiesUtil;
import com.etek.controller.utils.DetUtil;
import com.etek.controller.utils.SommerUtils;
import com.etek.sommerlibrary.activity.BaseActivity;
import com.etek.sommerlibrary.utils.DateUtil;
import com.etek.sommerlibrary.utils.FileUtils;
import com.etek.sommerlibrary.utils.MD5Util;
import org.apache.commons.lang3.StringUtils;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class OnlineCheckoutActivity2 extends BaseActivity {
    @BindView(R.id.rv_project_info)
    RecyclerView prv;

    @BindView(R.id.sl_project_info)
    SwipeRefreshLayout psl;

    @BindView(R.id.img_loading)
    ImageView img_loading;

    private DetController detController;
    private ProjectInfoEntity projectInfo;
    private CheckOutAdapter mProjectInfoAdapter;
    private static final int PAGE_SIZE = 10;
    private boolean isValid;
    private int number = 0;
    private int mNextRequestPage = 1;
    private int totalDet;


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


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initData() {
        detController = new DetController();
        detController.setUserIDCode(Globals.user.getIdCode());
//        detList = new ArrayList<>();
//        nPage = 0;
//        long proid = getLongInfo("projectId");
//        projectInfo = DBManager.getInstance().getProjectInfoEntityDao().queryBuilder()
//                .where(ProjectInfoEntityDao.Properties.Id.eq(proid)).unique();
//        getReportDto();
    }

    private void initView() {
        prv.setLayoutManager(new LinearLayoutManager(mContext));
        psl.setColorSchemeColors(Color.rgb(47, 223, 189));
        psl.setRefreshing(true);
        mProjectInfoAdapter = new CheckOutAdapter();
        mProjectInfoAdapter.setOnLoadMoreListener(() -> new Handler().post(() -> loadMore()));
//        mAdapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_LEFT);
//        mAdapter.setPreLoadNumber(3);

        prv.setAdapter(mProjectInfoAdapter);
        prv.addOnItemTouchListener(new OnItemClickListener() {
            @Override
            public void onSimpleItemClick(final BaseQuickAdapter adapter, final View view, final int position) {
//                ToastUtils.show(mContext, "position:" + position);
                // 获取itemView的位置

                Intent intent = new Intent(mContext, CheckoutDetailActivity.class);
                ChkControllerEntity chkControllerEntity = mProjectInfoAdapter.getData().get(position);
                XLog.d("projectInfoEntity:", chkControllerEntity);
//                showToast("proid:" + projectInfoEntity.getId());
                intent.putExtra("chkId", chkControllerEntity.getId());
                startActivity(intent);
            }
        });
        psl.setOnRefreshListener(() -> refresh());

    }


    private void refresh() {
//        showToast("数据更新！");
        XLog.v("数据更新! ");
        mNextRequestPage = 1;
        mProjectInfoAdapter.setEnableLoadMore(false);//这里的作用是防止下拉刷新的时候还可以上拉加载
        List<ChkControllerEntity> datas = DBManager.getInstance().getChkControllerEntityDao().queryBuilder()
                .orderDesc(ChkControllerEntityDao.Properties.Id)
                .limit(PAGE_SIZE)
                .build()
                .list();
        setChkData(true, datas);
        mProjectInfoAdapter.setEnableLoadMore(true);
//        mAdapter.setLoadMoreView(R.layout.item_load_more);
        psl.setRefreshing(false);
    }

    private void loadMore() {
        XLog.v("加载更多! ");
        int offset = (mNextRequestPage - 1) * PAGE_SIZE;
        int limit = offset + PAGE_SIZE;
        List<ChkControllerEntity> datas = DBManager.getInstance().getChkControllerEntityDao().queryBuilder()
                .orderDesc(ChkControllerEntityDao.Properties.Id)
                .offset(offset)
                .limit(limit)
                .build()
                .list();
        boolean isRefresh = mNextRequestPage == 1;
        setChkData(isRefresh, datas);
    }


    private void setChkData(boolean isRefresh, List datas) {
        mNextRequestPage++;
        final int size = datas == null ? 0 : datas.size();



//        XLog.v("iscomputing:", prv.isComputingLayout());
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
//            Toast.makeText(this, "没有数据", Toast.LENGTH_SHORT).show();
        } else {
            mProjectInfoAdapter.loadMoreComplete();
        }
//        for (ProjectInfoEntity datum : mProjectInfoAdapter.getData()) {
//            if (datum.getId() == curProId) {
//                datum.setSelect(true);
//                mProjectInfoAdapter.notifyDataSetChanged();
//            }
//        }
    }

    private void onNotificationReceived(byte[] bytes) {
        BLEDevResp devRsp = new BLEDevResp(bytes);
        if (devRsp.isValid()) {
            byte cmd = devRsp.getCmd();
            BLECmd bleCmd;
            DevCommonEnum devCheckoutEnum = DevCommonEnum.getBycode(cmd);
//            XLog.v("onNotificationReceived ", devCheckoutEnum.toString());
            switch (devCheckoutEnum) {

                case CONTROLLER:
//                    XLog.d("CONTROLLER ", SommerUtils.bytesToHexArrString(devRsp.getData()));
//                    byte[] d1 = devRsp.getData();
                    detController.clrDetonatorList();
                    detController.initData(devRsp.getData());
//                    detonatorAdapter.getData().clear();
                    totalDet = detController.getDetCount();
                    XLog.i(detController.toString());

                    showProgressBar("开始传输", detController.getDetCount());
//                    if (!StringUtils.isEmpty(detController.getContractId())) {
//                        bleCmd = BLECmd.getBlastTotalCmd(detController.getContractId());
//                        sendCmd2Controller(bleCmd);
//                    } else {
//                        bleCmd = BLECmd.getBlastTotalCmd("");
//                        sendCmd2Controller(bleCmd);
//                    }

//                    ControllerMultiItem controllerMultiItem = (ControllerMultiItem) detonatorAdapter.getData().get(0);
//                    controllerMultiItem.setContractId(detController.getContractId());
//
//                    detonatorAdapter.notifyDataSetChanged();
                    number = 1;
                    setProgressBar(number);
                    bleCmd = BLECmd.getDetCmd(number);
                    break;
                case DET:
//                    XLog.d("DET ", SommerUtils.bytesToHexArrString(devRsp.getData()));

                    Detonator detonator = new Detonator(devRsp.getData());
                    if (StringUtils.isEmpty(detonator.getDetCode())) {
//                        XLog.v("bytes ", SommerUtils.bytesToHexArrString(bytes));
                        return;
                    }
//                    XLog.d("detonator ", detonator.toString());
                    if (!detController.isDetExist(detonator)) {

                        detController.addDetonator(detonator);

                        if (number < detController.getDetCount()) {
                            number++;

                            bleCmd = BLECmd.getDetCmd(number);
                            setProgressBar(number);

                        } else {
                            XLog.d("isDetExist total");

                            long proId = isDetInProject();
                            if (proId > 0) {
                                projectInfo = DBManager.getInstance().getProjectInfoEntityDao().
                                        queryBuilder()
                                        .where(ProjectInfoEntityDao.Properties.Id.eq(proId)).unique();
                                if (projectInfo == null) {
                                    showStatusDialog("没有找到雷管规则所对应的项目");
                                    return;
                                }
//                                if (!StringUtils.isEmpty(projectInfo.getContractCode())) {
//                                    detController.setContractId(projectInfo.getContractCode());
//                                } else if (!StringUtils.isEmpty(projectInfo.getProCode())) {
//                                    detController.setContractId(projectInfo.getProCode());
//                                } else {
//                                    detController.setContractId("");
//                                }

                                bleCmd = getVerifyResult();
                                getToken();
//                                setLongInfo("projectId", proId);
                            } else {
                                showStatusDialog("没有找到雷管规则所对应的项目");
                            }
                        }
                    }

                    break;
                case VERIF:
//                    XLog.d("token:",detController.getToken());

                    if (projectInfo == null) {
                        showStatusDialog("项目文件出错！");
                        return;
                    }
                    String str = String.format("%015d", projectInfo.getId());
                    str += "0";
//                    if (!StringUtils.isEmpty(projectInfo.getContractCode())) {
//                        str +=  projectInfo.getContractCode();
//                    } else if (!StringUtils.isEmpty(projectInfo.getProCode())) {
//                        str += projectInfo.getProCode();
//                    } else {
//                        str += "00000000";
//                    }

                    break;
                case TOEXP_TOTAL:
//                    XLog.d("TOEXP_TOTAL ", SommerUtils.bytesToHexArrString(devRsp.getData()));
                    bleCmd = BLECmd.getOverCmd();

                    break;
                case OVER:
//                    projectInfo.setStatus(1);
//                    DBManager.getInstance().getProjectInfoEntityDao().update(projectInfo);
                    if (isValid) {
                        storeDetController();
                        refresh();
                    }
                    sendCheckoutReport();

                    break;
                default:
                    break;
            }
        } else {
            XLog.e("onReceive msg error: ", SommerUtils.bytesToHexArrString(bytes));
//            showToast("错误消息:" + SommerUtils.bytesToHexArrString(bytes));
        }
    }

    private String getToken() {
        StringBuilder sb = new StringBuilder();
        for (Detonator detonator : detController.getDetList()) {
            sb.append(detonator.getDetCode());
        }
//        XLog.i(JSON.toJSONString(detList));
//        XLog.i("sb:" + sb.toString());
        String token = MD5Util.md5(sb.toString());
        detController.setToken(token);
//        XLog.i("token:" + token);
        return token;
    }

    long isDetInProject() {
//        ProjectInfoEntity projectInfoEntity
        List<ProjectInfoEntity> projectInfoEntityList = DBManager.getInstance().getProjectInfoEntityDao().queryBuilder()
                .orderDesc(ProjectInfoEntityDao.Properties.CreateTime)
                .limit(100).list();
        for (ProjectInfoEntity projectInfoEntity : projectInfoEntityList) {
            for (DetonatorEntity detonatorEntity : projectInfoEntity.getDetonatorList()) {
                for (Detonator detonator : detController.getDetList()) {
                    if (detonator.getDetCode().equalsIgnoreCase(detonatorEntity.getCode())) {
                        return projectInfoEntity.getId();
                    }
                }
            }
        }
        return 0;
    }

    private BLECmd getVerifyResult() {
        isValid = false;
        BLECmd bleCmd;
        bleCmd = BLECmd.getVerify(CheckRuleEnum.SUCCESS.getCode(), 0, 0);

        boolean isControllerValid = false;

        for (ControllerEntity detControllerValid : projectInfo.getControllerList()) {
            if (detController.getSn().equalsIgnoreCase(detControllerValid.getName())) {
//                XLog.w("detController: controll==" + detController.getSn() + "  rule==" + detControllerValid.getName());
                isControllerValid = true;
            }
        }
        if (!isControllerValid) {
            bleCmd = BLECmd.getVerify(CheckRuleEnum.OUT_CONTROLLER.getCode(), 0, 0);
//            ToastUtils.showCustom(mContext, CheckRuleEnum.OUT_CONTROLLER.toString());
            showStatusDialog(CheckRuleEnum.OUT_CONTROLLER.getMessage());
//            checkoutInfo.setText(CheckRuleEnum.OUT_CONTROLLER.getMessage());
            return bleCmd;
        }


        int unUsedDet = projectInfo.getDetonatorList().size();
        int unRegDet = 0;
        boolean isUnreg;

        for (Detonator conDet : detController.getDetList()) {

            isUnreg = true;
            for (DetonatorEntity infoDet : projectInfo.getDetonatorList()) {
//                XLog.v(infoDet.toString());
//                XLog.v("verfiryCode: ",conDet.getDetCode(),infoDet.getCode());
                String cUid = conDet.getUid().substring(conDet.getUid().length() - 8, conDet.getUid().length());
                String iUid = conDet.getUid().substring(infoDet.getUid().length() - 8, infoDet.getUid().length());
//                XLog.v("verfiryUid: ",cUid,iUid);

                if (conDet.getDetCode().equalsIgnoreCase(infoDet.getCode())
                        && cUid.equalsIgnoreCase(iUid)) {
//                    XLog.d("conDet: ",conDet);
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

//        XLog.w("unRegDet:" + unRegDet + " unUsedDet:" + unUsedDet);
        if (unRegDet > 0) {
            isValid = true;
//            ToastUtils.showCustom(mContext, CheckRuleEnum.ERR_DET.toString());
            showStatusDialog(CheckRuleEnum.UNREG_DET.getMessage() + unRegDet);

//            XLog.w(CheckRuleEnum.ERR_DET.getMessage() + " : " + unUsedDet + " || " + unRegDet);
            bleCmd = BLECmd.getVerify(CheckRuleEnum.ERR_DET.getCode(), unRegDet, 0);
            return bleCmd;
        }
        isValid = true;
        showStatusDialog(CheckRuleEnum.SUCCESS.getMessage());
        return bleCmd;
    }


    private String getReportDto() {
        ProjectInfoDto projectInfoDto = new ProjectInfoDto();
        try {
            BeanPropertiesUtil.copyProperties(projectInfo, projectInfoDto);
        } catch (Exception e) {
            e.printStackTrace();
        }
//        XLog.d("from:", projectInfo);
        projectInfoDto.setCreateTime(new Date());
        detController.setContractId("");
        projectInfoDto.addDetControllers(detController);
//        XLog.v("to: ", projectInfoDto);
        return JSON.toJSONString(projectInfoDto);
    }

    private void storeDetController() {
        ChkControllerEntity chkControllerEntity = new ChkControllerEntity();
        XLog.i("storeDetController start");

        ChkControllerEntity oldControllerEntity = DBManager.getInstance().getChkControllerEntityDao().queryBuilder()
                .where(ChkControllerEntityDao.Properties.Token.eq(detController.getToken())).unique();
        if (oldControllerEntity != null) {
//            showStatusDialog("此规则检查已完成传输！");
            return;
        }
        try {
            BeanPropertiesUtil.copyProperties(detController, chkControllerEntity);
            chkControllerEntity.setProjectInfoId(projectInfo.getId());
            chkControllerEntity.setContractId(projectInfo.getContractCode());
            chkControllerEntity.setProjectId(projectInfo.getProCode());
            chkControllerEntity.setCompany(projectInfo.getCompanyName());
            long chkId = DBManager.getInstance().getChkControllerEntityDao().insert(chkControllerEntity);
            for (Detonator detonator : detController.getDetList()) {
                ChkDetonatorEntity chkDet = new ChkDetonatorEntity();
                chkDet.setSource(SommerUtils.bytesToHexString(detonator.getSource()));
                chkDet.setChipID(detonator.getChipID());
                chkDet.setDetIDs(SommerUtils.bytesToHexString(detonator.getIds()));
                chkDet.setStatus(detonator.getStatus());
                chkDet.setType(detonator.getType());
                chkDet.setNum(detonator.getNum());
                chkDet.setValidTime(detonator.getTime());
                chkDet.setCode(detonator.getDetCode());
                chkDet.setWorkCode(SommerUtils.bytesToHexString(detonator.getAcCode()));
                chkDet.setUid(detonator.getUid());
                chkDet.setRelay(detonator.getRelay());
                chkDet.setChkId(chkId);
                DBManager.getInstance().getChkDetonatorEntityDao().insert(chkDet);
            }
            XLog.i("copy: ", chkControllerEntity);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendCheckoutReport() {
        String rptJson = getReportDto();
        String fdName = "report_info" + DateUtil.getDateDoc(new Date()) + ".json";
        FileUtils.saveFileToSDcard("detonator/json", fdName, rptJson);
//        showToast("保存完成！");
        String url = AppConstants.ETEKTestServer + AppConstants.CheckoutReport;
//        showDialog(getMyString(R.string.report));
        AsyncHttpCilentUtil.httpPostJson(url, rptJson, new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                XLog.e("IOException:", e.getMessage());
//                closeDialog();
//                showStatusDialog("服务器报错");
//                sendCmdMessage(MSG_RPT_DANLING_ERR);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
//                closeDialog();
                String respStr = response.body().string();
                if (!StringUtils.isEmpty(respStr)) {
                    XLog.w("respStr is  ", respStr);
                }
            }
        });
    }
}
