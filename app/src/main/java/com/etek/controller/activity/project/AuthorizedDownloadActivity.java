package com.etek.controller.activity.project;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.etek.controller.R;
import com.etek.controller.activity.BaseActivity;
import com.etek.controller.activity.project.comment.AppSpSaveConstant;
import com.etek.controller.activity.project.manager.SpManager;
import com.etek.controller.common.AppConstants;
import com.etek.controller.fragment.ProjectDialog;
import com.etek.controller.hardware.test.HttpCallback;
import com.etek.controller.persistence.DBManager;
import com.etek.controller.utils.AsyncHttpCilentUtil;
import com.etek.controller.yunnan.YunAuthDataAdapter;
import com.etek.controller.yunnan.YunDownloadDetailActivity;
import com.etek.controller.yunnan.bean.OfflineAuthBombBean;
import com.etek.controller.yunnan.bean.YunnanResponse;
import com.etek.controller.yunnan.enetity.YunnanAuthBobmEntity;
import com.etek.controller.yunnan.util.DataTransformUtil;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import okhttp3.Response;

/**
 * 授权下载
 */
public class AuthorizedDownloadActivity extends BaseActivity implements BaseQuickAdapter.OnItemClickListener, View.OnClickListener, BaseQuickAdapter.OnItemLongClickListener, ProjectDialog.OnMakeProjectListener {

    private RecyclerView recycleView;
    private LinearLayout noDataView;
//    private List<ProjectInfoEntity> projectInfos = new ArrayList<>();
    private List<YunnanAuthBobmEntity> projectInfos = new ArrayList<>();
//    private ContractAdapter contractAdapter;
    private String TAG = "AuthorizedDownloadActivity";
    private YunAuthDataAdapter yunAuthDataAdapter;
    // 文件下载的授权码
    private String authCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authorized_download);
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.text_btn:
//                goToOfflineEditActivity();
                showProjectDialog();
                break;
            case R.id.back_img:
                finish();
                break;
        }
    }

    private void showProjectDialog() {
        ProjectDialog projectDialog = new ProjectDialog();
        projectDialog.setOnMakeProjectListener(this);
        projectDialog.show(getSupportFragmentManager(),"projectDialog");
    }

    @Override
    public void makeProject(String strCompanyId, String strAuthCode) {
        // 进行离线文件的下载
        this.authCode = strAuthCode;
        showProDialog("正在得到检验数据中。。。");
        String url = String.format(AppConstants.YunNanFileDownload,strCompanyId,strAuthCode);
        AsyncHttpCilentUtil.httpPostNew(this, url, null, new HttpCallback() {
            @Override
            public void onFaile(IOException e) {
                missProDialog();
                ToastNewUtils.getInstance(AuthorizedDownloadActivity.this).showLongToast("请求数据失败！");
                Logger.w( "yunnan file download faile!");
            }

            @Override
            public void onSuccess(Response response) {
                missProDialog();
                try {
                    String string = response.body().string();
                    if (TextUtils.isEmpty(string)) {
                        Logger.w("YunNanFileDownload response is empty");
                        ToastNewUtils.getInstance(AuthorizedDownloadActivity.this).showLongToast("请求数据失败！");
                        return;
                    }
                    Logger.d(string);
                    YunnanResponse yunnanResponse = new Gson().fromJson(string, YunnanResponse.class);
                    if (yunnanResponse != null && yunnanResponse.getResult() != null) {
                       // 获取数据成功，存储到数据库，并刷新界面
                        OfflineAuthBombBean offlineAuthBombBean = yunnanResponse.getResult();
                        saveDataToDB(offlineAuthBombBean);
                    }else{
                        ToastNewUtils.getInstance(AuthorizedDownloadActivity.this).showLongToast("请求数据失败！");
                        Logger.w("yunnanResponse is null");
                    }
                } catch (IOException e) {
                    ToastNewUtils.getInstance(AuthorizedDownloadActivity.this).showLongToast("请求数据失败！");
                    e.printStackTrace();
                }
            }
        });
    }

    private void saveDataToDB(OfflineAuthBombBean offlineAuthBombBean) {
        if (projectInfos.size() !=0) {
            for (YunnanAuthBobmEntity projectInfo : projectInfos) {
                if (projectInfo.getFileId() == offlineAuthBombBean.getId()) {
                    ToastNewUtils.getInstance(this).showShortToast("准爆文件已下载！");
                    return;
                }
            }
        }
        YunnanAuthBobmEntity yunnanAuthBobmEntity = DataTransformUtil.tranToEntity(offlineAuthBombBean);
        yunnanAuthBobmEntity.setAuthCode(authCode);
        DBManager.getInstance().getYunnanAuthBombEntityDao().save(yunnanAuthBobmEntity);
        projectInfos.add(0,yunnanAuthBobmEntity);
        yunAuthDataAdapter.notifyDataSetChanged();
    }

    private void goToOfflineEditActivity() {
        String userStr = SpManager.getIntance().getSpString(AppSpSaveConstant.USER_INFO);
        if (TextUtils.isEmpty(userStr)) {
            Intent intent = new Intent(this, UserInfoActivity2.class);
            startActivity(intent);
            return;
        }
        Intent intent = new Intent(this, OfflineEditActivity.class);
        startActivityForResult(intent,200);
    }

    /**
     * 初始化View
     */
    private void initView() {
        TextView textTitle = findViewById(R.id.text_title);
        TextView textBtn = findViewById(R.id.text_btn);
        View backImg = findViewById(R.id.back_img);
        textTitle.setText("授权下载");
        textBtn.setText("添加项目");
        backImg.setOnClickListener(this);
        textBtn.setOnClickListener(this);
        recycleView = findViewById(R.id.authorized_download_recycleView);
        noDataView = findViewById(R.id.nodata_view);
        recycleView.setLayoutManager(new LinearLayoutManager(this));
//        contractAdapter = new ContractAdapter(R.layout.contract_item_view, projectInfos);
//        recycleView.setAdapter(contractAdapter);
//        contractAdapter.setOnItemClickListener(this);
//        contractAdapter.setOnItemLongClickListener(this);

        yunAuthDataAdapter = new YunAuthDataAdapter(R.layout.contract_item_view, projectInfos);
        recycleView.setAdapter(yunAuthDataAdapter);
        yunAuthDataAdapter.setOnItemClickListener(this);
        yunAuthDataAdapter.setOnItemLongClickListener(this);
    }

    /**
     * 初始化数据
     */
    private void initData() {
//        List<ProjectInfoEntity> projectDownLoadEntities = DBManager.getInstance().getProjectInfoEntityDao().loadAll();
//
//        projectInfos.clear();
//        if (projectDownLoadEntities != null && projectDownLoadEntities.size() > 0) {
//            noDataView.setVisibility(View.GONE);
//            Collections.reverse(projectDownLoadEntities);
//            projectInfos.addAll(projectDownLoadEntities);
//        } else {
//            noDataView.setVisibility(View.VISIBLE);
//        }
//        contractAdapter.notifyDataSetChanged();
        List<YunnanAuthBobmEntity> yunnanAuthBobmEntities = DBManager.getInstance().getYunnanAuthBombEntityDao().loadAll();
        projectInfos.clear();
        if (yunnanAuthBobmEntities != null && yunnanAuthBobmEntities.size() > 0) {
            noDataView.setVisibility(View.GONE);
            Collections.reverse(yunnanAuthBobmEntities);
            projectInfos.addAll(yunnanAuthBobmEntities);
        } else {
            noDataView.setVisibility(View.VISIBLE);
        }
        yunAuthDataAdapter.notifyDataSetChanged();
    }


    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
//        ProjectInfoEntity projectInfoEntity = projectInfos.get(position);
//        Long projectId = projectInfoEntity.getId();
//
//        Intent intent = new Intent(this, AuthDownLoadDetailActivity.class);
//        intent.putExtra(AuthDownLoadDetailActivity.PROJECT_ID,projectId);
//        startActivity(intent);

        YunnanAuthBobmEntity yunnanAuthBobmEntity = projectInfos.get(position);
        Long projectId = yunnanAuthBobmEntity.getId();
        Intent intent = new Intent(this, YunDownloadDetailActivity.class);
        intent.putExtra(YunDownloadDetailActivity.PROJECT_ID, projectId);
        startActivity(intent);
    }

    @Override
    public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {

        YunnanAuthBobmEntity yunnanAuthBobmEntity = projectInfos.get(position);

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("确定删除吗？");
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //删除数据
                DBManager.getInstance().getYunnanAuthBombEntityDao().delete(yunnanAuthBobmEntity);
                projectInfos.remove(position);
                yunAuthDataAdapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        });
        builder.create().show();

        return true;
    }

}
