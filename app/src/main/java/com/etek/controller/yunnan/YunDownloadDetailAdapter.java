package com.etek.controller.yunnan;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.etek.controller.R;

import java.util.List;

public class YunDownloadDetailAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

    public YunDownloadDetailAdapter(int layoutResId, @Nullable List<String> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {
        helper.setText(R.id.det_sn, helper.getPosition() + "");
        helper.setText(R.id.det_code, item);
        helper.setGone(R.id.det_status,false);
    }
}
