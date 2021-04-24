package com.etek.controller.adapter;

import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.etek.controller.R;
import com.etek.controller.hardware.util.DetLog;
import com.etek.controller.persistence.entity.PendingProject;

import java.util.List;

public class ProjectReportAdapter extends BaseQuickAdapter<PendingProject, BaseViewHolder> {


    public ProjectReportAdapter(int layoutResId, @Nullable List<PendingProject> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, PendingProject item) {
        String strlog =String.format("工程:%s\tID:%d\t数量：%d",
                item.getProjectCode(),
                item.getId(),
                item.getDetonatorList().size());
        DetLog.writeLog("数据上报",strlog);

        helper.setText(R.id.contrl_sn, item.getProjectCode());
        helper.setText(R.id.det_size, item.getDetonatorList().size() + "");
        helper.setText(R.id.rpt_time, item.getDate());
        String reportStatus = item.getReportStatus();
        if (TextUtils.isEmpty(reportStatus)) {
            reportStatus = "0";
        }
        int status = Integer.parseInt(reportStatus);
        if (status == 0) {
            helper.setText(R.id.contrl_status, "未上报");
            helper.setTextColor(R.id.contrl_status,getMyColor(R.color.red));
        } else if (status == 1) {
            helper.setText(R.id.contrl_status, "已上报");
            helper.setTextColor(R.id.contrl_status,getMyColor(R.color.green));
        } else if (status == 2) {
            helper.setText(R.id.contrl_status, "上报错误");
            helper.setTextColor(R.id.contrl_status,getMyColor(R.color.orange));
        }


    }

    private int getMyColor(int green) {
        return mContext.getResources().getColor(green);
    }
}
