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
        int strStautsColor = getStrStautsColor(item.getStatus());
        helper.setTextColor(R.id.det_status,mContext.getColor(strStautsColor));
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

    private int getStrStautsColor(int status) {
        int strStatusColor = 0;
        if (status == 0) {
            strStatusColor = R.color.mediumseagreen;
        } else if (status == 1) {
            strStatusColor =R.color.crimson;
        }else if (status == 2){
            strStatusColor =R.color.chat_item5_normal;
        }else if (status == 3){
            strStatusColor =R.color.gray_pressed;
        }else {
            strStatusColor= R.color.setting_text_on;
        }
        return strStatusColor;
    }
}


