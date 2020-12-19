package com.etek.controller.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.elvishew.xlog.XLog;
import com.etek.controller.R;
import com.etek.controller.common.AppIntentString;
import com.etek.sommerlibrary.activity.BaseActivity;


/**
 * 检查授权
 */
public class AuthBombActivity2 extends BaseActivity implements View.OnClickListener {

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
    public void onClick(View v) {
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
