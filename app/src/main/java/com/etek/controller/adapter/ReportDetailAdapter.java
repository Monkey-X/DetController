package com.etek.controller.adapter;

import android.support.annotation.Nullable;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.etek.controller.R;
import com.etek.controller.persistence.entity.ProjectDetonator;

import java.util.List;

public class ReportDetailAdapter extends BaseQuickAdapter<ProjectDetonator, BaseViewHolder> {

    public ReportDetailAdapter(int layoutResId, @Nullable List<ProjectDetonator> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, ProjectDetonator item) {
        helper.setText(R.id.number, helper.getAdapterPosition() + 1 + "");
        helper.setText(R.id.uid_num, item.getCode());
        if (item.getStatus() == 0) {
            helper.setText(R.id.status, "正常");
            helper.setTextColor(R.id.status,mContext.getColor(R.color.mediumseagreen));
        } else if (item.getStatus() == 1) {
            helper.setText(R.id.status, "未注册");
            helper.setTextColor(R.id.status,mContext.getColor(R.color.red_normal));
        } else if (item.getStatus() == 2) {
            helper.setText(R.id.status, "已使用");
            helper.setTextColor(R.id.status,mContext.getColor(R.color.blue));
        } else if (item.getStatus() == 3) {
            helper.setText(R.id.status, "不存在");
            helper.setTextColor(R.id.status,mContext.getColor(R.color.gray));
        }else{
            helper.setText(R.id.status, "--");
        }
    }
}
