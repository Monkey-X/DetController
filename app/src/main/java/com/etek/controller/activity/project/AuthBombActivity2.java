package com.etek.controller.activity.project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.elvishew.xlog.XLog;
import com.etek.controller.R;
import com.etek.controller.common.AppIntentString;
import com.etek.controller.hardware.util.DetLog;
import com.etek.controller.persistence.DBManager;
import com.etek.controller.persistence.entity.PendingProject;
import com.etek.controller.persistence.gen.PendingProjectDao;
import com.etek.sommerlibrary.activity.BaseActivity;


/**
 * 检查授权
 */
public class AuthBombActivity2 extends BaseActivity implements View.OnClickListener {

    private final String TAG="AuthBombActivity2";
    private PendingProject projectInfoEntity;
    private long proId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth_bomb2);
        initSupportActionBar(R.string.check_authorize);
        getProjectId();
        initView();
    }

    /**
     * 获取项目id
     */
    private void getProjectId() {
        proId = getIntent().getLongExtra(AppIntentString.PROJECT_ID, -1);
        XLog.d("proId: " + proId);
    }

    /**
     * 初始化View
     */
    private void initView() {
        View online = findViewById(R.id.online);
        View offline = findViewById(R.id.offline);
        online.setOnClickListener(this);
        offline.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshData();
    }

    /**
     * 刷新页面
     */
    private void refreshData() {
        projectInfoEntity = null;
        if (proId >= 0) {
            projectInfoEntity = DBManager.getInstance().getPendingProjectDao().queryBuilder().where(PendingProjectDao.Properties.Id.eq(proId)).unique();
        }
        if (projectInfoEntity == null) {
            return;
        }
        DetLog.writeLog(TAG,"项目状态:"+projectInfoEntity.getProjectStatus());
    }

    @Override
    public void onClick(View v) {
        if(projectInfoEntity!=null){
            int nstatus = projectInfoEntity.getProjectStatus();
            if(nstatus>=AppIntentString.PROJECT_IMPLEMENT_DATA_REPORT1){
                showDialogMessage("已经起爆，不能再次检查");
                return;
            }
        }

        switch (v.getId()) {
            case R.id.online://在线
                Intent onlineIntent = new Intent(this, CheckDetailActivity.class);
                onlineIntent.putExtra("type","online");
                onlineIntent.putExtra(AppIntentString.PROJECT_ID,proId);
                startActivity(onlineIntent);
                break;

            case R.id.offline://离线
                Intent offlineIntent = new Intent(this, CheckDetailActivity.class);
                offlineIntent.putExtra("type","offline");
                offlineIntent.putExtra(AppIntentString.PROJECT_ID,proId);
                startActivity(offlineIntent);
                break;
        }
    }
}
