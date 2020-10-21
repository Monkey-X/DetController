package com.etek.controller.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.etek.controller.R;
import com.etek.sommerlibrary.activity.BaseActivity;

/**
 * 授权起爆
 */
public class AuthBombActivity extends BaseActivity implements View.OnClickListener {

    private View mOnlineApply;
    private View mOfflineApply;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth_bomb);
        initView();

    }

    private void initView() {
        mOnlineApply = findViewById(R.id.onlineApply);
        mOfflineApply = findViewById(R.id.offlineApply);
        mOfflineApply.setOnClickListener(this);
        mOnlineApply.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.onlineApply:
                startActivity(new Intent(this,OnlineCheckActivity.class));
                break;
            case R.id.offlineApply:
                startActivity(new Intent(this,OfflineCheckActivity.class));
                break;
        }
        finish();
    }
}