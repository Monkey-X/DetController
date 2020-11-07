package com.etek.controller.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.etek.controller.R;
import com.etek.controller.persistence.entity.SingleCheckEntity;

import java.util.List;

public class SingleCheckAdapter extends BaseQuickAdapter<SingleCheckEntity, BaseViewHolder> {

    public SingleCheckAdapter(int layoutResId, @Nullable List<SingleCheckEntity> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, SingleCheckEntity item) {
        helper.setText(R.id.uid_num, item.getDC());
        helper.setText(R.id.delaytime, item.getRelay());
        String s = item.getTestStatus() == 1 ? "成功" : "失败";
        helper.setText(R.id.det_status, s+item.getTestStatus());
    }
}
