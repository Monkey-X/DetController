package com.etek.controller.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.etek.controller.R;
import com.etek.sommerlibrary.activity.BaseActivity;

/**
 *  工程详情界面
 */
public class ProjectDetailActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_detail);
    }
}