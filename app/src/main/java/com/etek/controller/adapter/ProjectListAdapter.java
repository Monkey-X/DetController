package com.etek.controller.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.etek.controller.R;
import com.etek.controller.persistence.entity.PendingProject;

import java.util.List;

public class ProjectListAdapter extends BaseQuickAdapter<PendingProject, BaseViewHolder> {

    private boolean isNewProject = false;

    public ProjectListAdapter(int layoutResId, @Nullable List<PendingProject> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, PendingProject item) {
        int adapterPosition = helper.getAdapterPosition();
        if (isNewProject && adapterPosition == 0) {
            helper.itemView.requestFocus();
            isNewProject = false;
        }
        helper.setText(R.id.project_code, item.getProjectCode());
        helper.setText(R.id.create_date, item.getDate());
    }

    public void isNewProject(boolean isNewProject) {
        this.isNewProject = isNewProject;
    }
}
