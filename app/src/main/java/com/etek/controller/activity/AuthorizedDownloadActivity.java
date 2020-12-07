package com.etek.controller.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.alibaba.fastjson.JSON;
import com.elvishew.xlog.XLog;
import com.etek.controller.R;
import com.etek.controller.adapter.ContractAdapter;
import com.etek.controller.common.AppConstants;
import com.etek.controller.common.Globals;
import com.etek.controller.dto.Jbqy;
import com.etek.controller.dto.Jbqys;
import com.etek.controller.dto.Lg;
import com.etek.controller.dto.Lgs;
import com.etek.controller.dto.ProInfoDto;
import com.etek.controller.dto.ProjectDownLoadEntity;
import com.etek.controller.dto.ProjectFileDto;
import com.etek.controller.dto.Sbbhs;
import com.etek.controller.dto.Zbqy;
import com.etek.controller.dto.Zbqys;
import com.etek.controller.fragment.AuthorizedDownloadDialog;
import com.etek.controller.persistence.DBManager;
import com.etek.controller.persistence.entity.ControllerEntity;
import com.etek.controller.persistence.entity.DetonatorEntity;
import com.etek.controller.persistence.entity.ForbiddenZoneEntity;
import com.etek.controller.persistence.entity.PermissibleZoneEntity;
import com.etek.controller.persistence.entity.ProjectInfoEntity;
import com.etek.controller.persistence.gen.ProjectDownLoadEntityDao;
import com.etek.controller.persistence.gen.ProjectInfoEntityDao;
import com.etek.controller.utils.AppUtils;
import com.etek.controller.utils.AsyncHttpCilentUtil;
import com.etek.controller.utils.SommerUtils;
import com.etek.sommerlibrary.activity.BaseActivity;
import com.etek.sommerlibrary.dto.Result;
import com.etek.sommerlibrary.utils.DateUtil;
import com.etek.sommerlibrary.utils.FileUtils;
import com.etek.sommerlibrary.utils.NetUtil;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 授权下载
 */
public class AuthorizedDownloadActivity extends BaseActivity implements AuthorizedDownloadDialog.AuthorizedDownloadListener {

    private RecyclerView recycleView;
    private LinearLayout noDataView;
    private List<ProjectDownLoadEntity> projectInfos = new ArrayList<>();
    private ContractAdapter contractAdapter;
    private String respStr = "";
    private static final int UPDATE = 10;
    private String TAG = "AuthorizedDownloadActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authorized_download);
        initSupportActionBar(R.string.contract_list);
        initView();
        initData();
        initDialog();
    }

    /**
     * 弹框输入合同编号以及授权码
     */
    private void initDialog() {
        AuthorizedDownloadDialog dialog = new AuthorizedDownloadDialog();
        dialog.setOnMakeProjectListener(this);
        dialog.show(getSupportFragmentManager(), "Dialog");
    }

    /**
     * 初始化View
     */
    private void initView() {
        recycleView = findViewById(R.id.authorized_download_recycleView);
        noDataView = findViewById(R.id.nodata_view);
        recycleView.setLayoutManager(new LinearLayoutManager(this));
        contractAdapter = new ContractAdapter(R.layout.contract_item_view, projectInfos);
        recycleView.setAdapter(contractAdapter);
    }

    /**
     * 初始化数据
     */
    private void initData() {
        List<ProjectDownLoadEntity> projectDownLoadEntities = DBManager.getInstance().getProjectDownLoadEntityDao().loadAll();

        projectInfos.clear();
        if (projectDownLoadEntities != null && projectDownLoadEntities.size() > 0) {
            noDataView.setVisibility(View.GONE);
            projectInfos.addAll(projectDownLoadEntities);
        } else {
            noDataView.setVisibility(View.VISIBLE);
        }
        contractAdapter.notifyDataSetChanged();
    }

    @Override
    public void getProjectFileContent(String contractCode, String authorizedCode) {
        getProjectFile(contractCode, authorizedCode);
    }

    /**
     * 请求后台验证并获取数据
     */
    private void getProjectFile(String contractCode, String authorizedCode) {
        if (NetUtil.getNetType(mContext) < 0) {
            showStatusDialog("请去设置网络！");
            return;
        }
        showProDialog("下载中...");
        LinkedHashMap params = new LinkedHashMap();
        params.put("dwdm", contractCode);    // 输入合同编号
        params.put("xlh", authorizedCode);   // 输入授权码
        String newUrl = SommerUtils.attachHttpGetParams(AppConstants.DanningServer + AppConstants.ProjectFileDownload, params);
        // 测试服务器
        XLog.v("newUrl:" + newUrl);
        AsyncHttpCilentUtil.getOkHttpClient(newUrl, new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                showLongToast(authorizedCode + "onFailure！" + e.getLocalizedMessage());
                Log.d(TAG, "onFailure: " + e.getLocalizedMessage());
                missProDialog();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d(TAG, "onSuccess: " + response.toString());
                setStringInfo("filesn", authorizedCode);
                respStr = response.body().string();
                Log.d(TAG, "onSuccess: respStr =" + respStr);
                missProDialog();
                if (respStr == null || respStr.length() < 10) {
                    showToast("下载数据错误！");
                    return;
                }
                ProjectFileDto projectFileDto = null;
                try {
                    projectFileDto = JSON.parseObject(respStr, ProjectFileDto.class);
                } catch (Exception e) {
                    XLog.e(e);
                    showToast("错误信息：" + e.getMessage());
                    return;
                }

                if (checkSameProject(projectFileDto)) {
                    showStatusDialog("项目已下载！");
                    return;
                }

                Result detInfoResult = projectFileDto.parseContentAndSave(authorizedCode);

                if (detInfoResult.isSuccess()) {
                    Log.d(TAG, "onResponse: " + detInfoResult.toString());

                    ProjectFileDto projectFile = (ProjectFileDto) detInfoResult.getData();
                    projectFile.setFileSn(authorizedCode);
                    if (Globals.user != null) {
                        projectFile.setCompany(Globals.user.getCompanyCode());
                    }
                    projectFile.setMingma("");
                    String strInfo = JSON.toJSONString(projectFile, AppUtils.filter);
                    XLog.i(strInfo);

                    sendCmdMessage(UPDATE, strInfo);

                    String msg = valifyProjectFile(projectFile);
                    if (msg != null && !StringUtils.isEmpty(msg)) {
                        showStatusDialog(msg);
                        return;
                    }
                    long proId = saveProjectInfo(projectFile, authorizedCode);
                } else {
                    showStatusDialog(detInfoResult.getMessage());
                }
            }
        });
    }

    /**
     * 检查是否有同样的项目
     *
     * @param projectFileDto
     */
    private boolean checkSameProject(ProjectFileDto projectFileDto) {
        if (projectFileDto == null) {
            return true;
        }

        List<ProjectDownLoadEntity> projectDownLoadEntities = DBManager.getInstance().getProjectDownLoadEntityDao().loadAll();
        if (projectDownLoadEntities != null && projectDownLoadEntities.size() != 0) {
            for (ProjectDownLoadEntity projectDownLoadEntity : projectDownLoadEntities) {
                if (projectDownLoadEntity.getHtbh().equals(projectFileDto.getHtbh())) {
                    return true;
                }
            }
        }

        return false;
    }

    private long saveProjectInfo(ProjectFileDto projectFile, String authorizedCode) {
        ProjectDownLoadEntity projectDownLoadEntity = new ProjectDownLoadEntity();
        projectDownLoadEntity.setXmbh(projectFile.getXmbh());
        projectDownLoadEntity.setXmmc(projectFile.getXmmc());
        projectDownLoadEntity.setDwdm(projectFile.getDwdm());
        projectDownLoadEntity.setDwmc(projectFile.getDwmc());
        projectDownLoadEntity.setHtbh(projectFile.getHtbh());
        projectDownLoadEntity.setHtmc(projectFile.getHtmc());
        projectDownLoadEntity.setHtmc(projectFile.getHtmc());
        projectDownLoadEntity.setMmwj(projectFile.getMmwj());
        projectDownLoadEntity.setFileSn(authorizedCode);
        DBManager.getInstance().getProjectDownLoadEntityDao().save(projectDownLoadEntity);
        refreshData();
        return 0;
    }

    private void refreshData() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                initData();
            }
        });
    }

    /**
     * 验证
     */
    private String valifyProjectFile(ProjectFileDto projectFile) {
        ProInfoDto mDetInfoDto = projectFile.getProInfo();
        if (mDetInfoDto == null) {
            return "信息为空！";
        }
        if (mDetInfoDto.getLgs() == null || mDetInfoDto.getLgs().getLg().isEmpty()) {
            return "缺少雷管信息，请检查报备是否正常";
        }
        if (mDetInfoDto.getZbqys() == null || mDetInfoDto.getZbqys().getZbqy().isEmpty()) {
            return "缺少经纬度信息，请检查报备是否正常！";
        }
        for (Zbqy zbqy : mDetInfoDto.getZbqys().getZbqy()) {
            if (StringUtils.isEmpty(zbqy.getZbqyjd()) || StringUtils.isEmpty(zbqy.getZbqywd())) {
                return "缺少经纬度信息，请检查报备是否正常！";
            }
        }
        if (mDetInfoDto.getSbbhs() == null || mDetInfoDto.getSbbhs().isEmpty()) {
            return "缺少起爆器信息，请检查报备是否正常";
        }
        return null;
    }


    /**
     * 存储项目数据
     */
    private long storeProjectInfo(final ProjectFileDto projectFile, String fileSn) {

//        ThreadPoolUtils.getThreadPool().execute(()->{
        ProInfoDto mDetInfoDto = projectFile.getProInfo();
        XLog.v("proinfo:", mDetInfoDto);
        ProjectInfoEntity projectInfoEntity = new ProjectInfoEntity();
        projectInfoEntity.setApplyDate(mDetInfoDto.getSqrq());
        projectInfoEntity.setProCode(projectFile.getXmbh());
        projectInfoEntity.setProName(projectFile.getXmmc());
        projectInfoEntity.setCompanyCode(projectFile.getDwdm());
        projectInfoEntity.setCompanyName(projectFile.getDwmc());
        projectInfoEntity.setContractCode(projectFile.getHtbh());
        projectInfoEntity.setContractName(projectFile.getHtmc());
        projectInfoEntity.setFileSn(fileSn);
        projectInfoEntity.setStatus(0);
        projectInfoEntity.setIsOnline(false);
        projectInfoEntity.setCreateTime(new Date());
        ProjectInfoEntity existProjectInfo = DBManager.getInstance().getProjectInfoEntityDao().queryBuilder()
                .where(ProjectInfoEntityDao.Properties.FileSn.eq(fileSn)).unique();
        if (existProjectInfo != null) {
            return 0;
        }
        long proId = DBManager.getInstance().getProjectInfoEntityDao().insert(projectInfoEntity);
        if (proId == 0) {
            return 0;
        }
        Lgs lgs = mDetInfoDto.getLgs();
        if (!lgs.getLg().isEmpty()) {
            List<DetonatorEntity> detonatorEntityList = new ArrayList<>();
            for (Lg lg : lgs.getLg()) {

                DetonatorEntity detonatorBean = new DetonatorEntity();
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
                detonatorEntityList.add(detonatorBean);
            }
            DBManager.getInstance().getDetonatorEntityDao().insertInTx(detonatorEntityList);
        }

        Zbqys zbqys = mDetInfoDto.getZbqys();
        if (!zbqys.getZbqy().isEmpty()) {
            List<PermissibleZoneEntity> permissibleZoneEntityList = new ArrayList<>();
            for (Zbqy zbqy : zbqys.getZbqy()) {
//                                private String zbqssj;  //准爆起始时间
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
        Jbqys jbqys = mDetInfoDto.getJbqys();
        if (!jbqys.getJbqy().isEmpty()) {
            List<ForbiddenZoneEntity> forbiddenZoneEntityList = new ArrayList<>();
            for (Jbqy jbqy : jbqys.getJbqy()) {
//                                private String zbqssj;  //准爆起始时间
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
        List<Sbbhs> sbbhs = mDetInfoDto.getSbbhs();

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

    /**
     * 发送handler消息
     */
    private void sendCmdMessage(int msg, String info) {
        Message message = new Message();
        message.what = msg;
        Bundle b = new Bundle();
        b.putString("info", info);
        message.setData(b);
        if (handler != null) {
            handler.sendMessage(message);
        }
    }

    //消息处理者,创建一个Handler的子类对象,目的是重写Handler的处理消息的方法(handleMessage())
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {


        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE:
                    Bundle b = msg.getData();
                    String info = b.getString("info");
                    if (StringUtils.isEmpty(info)) {
                        return;
                    }
                    String url = AppConstants.ETEKTestServer + AppConstants.DETUnCheck;
                    AsyncHttpCilentUtil.httpPostJson(url, info, new okhttp3.Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            XLog.e("IOException:" + e.getMessage());
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            String respStr = response.body().string();
                            if (!StringUtils.isEmpty(respStr)) {
                                XLog.w("respStr is  " + respStr);

                            }
                        }
                    });
                    break;
            }
        }
    };
}
