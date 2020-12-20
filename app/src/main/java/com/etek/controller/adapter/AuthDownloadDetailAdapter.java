package com.etek.controller.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.etek.controller.R;
import com.etek.controller.persistence.entity.DetonatorEntity;

import java.util.List;

public class AuthDownloadDetailAdapter extends BaseQuickAdapter<DetonatorEntity, BaseViewHolder> {

    public AuthDownloadDetailAdapter(int layoutResId, @Nullable List<DetonatorEntity> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, DetonatorEntity item) {
        helper.setText(R.id.det_sn, helper.getPosition() + "");
        helper.setText(R.id.det_code, item.getCode());
        String strStauts = getStrStauts(item.getStatus());
        helper.setText(R.id.det_status, strStauts);
    }

    private String getStrStauts(int status) {
        String strStatus = "";
        if (status == 0) {
            strStatus = "正常";
        } else if (status == 1) {
            strStatus ="黑名单";
        }else if (status == 2){
            strStatus ="已使用";
        }else if (status == 3){
            strStatus ="不存在";
        }else {
            strStatus= "未知";
        }
        return strStatus;
    }
}


