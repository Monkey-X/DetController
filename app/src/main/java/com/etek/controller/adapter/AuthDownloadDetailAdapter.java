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
       helper.setText(R.id.detCode,item.getCode());
       helper.setText(R.id.uidCode,item.getUid());
       helper.setText(R.id.workCode,item.getStatus());
       helper.setText(R.id.validDate,item.getValidTime().toString());
    }
}
