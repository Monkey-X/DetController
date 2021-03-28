package com.etek.controller.activity.project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.etek.controller.R;
import com.etek.controller.activity.BaseActivity;
import com.etek.controller.activity.project.comment.CheckType;
import com.etek.controller.common.AppIntentString;


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
    }

    /**
     * 初始化View
     */
    private void initView() {
        View online = findViewById(R.id.online);
        View offline = findViewById(R.id.offline);
        View dataCheck = findViewById(R.id.data_check);
        online.setVisibility(View.GONE);
        offline.setVisibility(View.GONE);
        online.setOnClickListener(this);
        offline.setOnClickListener(this);
        dataCheck.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.online://在线
                Intent onlineIntent = new Intent(this, CheckDetailActivity.class);
                onlineIntent.putExtra(CheckType.CHECK_TYPE, CheckType.ONLINE_TYPE);
                onlineIntent.putExtra(AppIntentString.PROJECT_ID, proId);
                startActivity(onlineIntent);
                break;

            case R.id.offline://离线
                Intent offlineIntent = new Intent(this, CheckDetailActivity.class);
                offlineIntent.putExtra(CheckType.CHECK_TYPE, CheckType.OFFLINE_TYPE);
                offlineIntent.putExtra(AppIntentString.PROJECT_ID, proId);
                startActivity(offlineIntent);
                break;
            case R.id.data_check:
                Intent dataCheckIntent = new Intent(this, CheckDetailActivity.class);
                dataCheckIntent.putExtra(CheckType.CHECK_TYPE, CheckType.YUNNAN_TYPE);
                dataCheckIntent.putExtra(AppIntentString.PROJECT_ID, proId);
                startActivity(dataCheckIntent);
                break;

        }
    }
}
