package com.etek.controller.yunnan;

import android.support.annotation.Nullable;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.etek.controller.R;
import com.etek.controller.utils.DateStringUtils;
import com.etek.controller.yunnan.enetity.YunnanAuthBobmEntity;

import java.util.List;

public class YunAuthDataAdapter extends BaseQuickAdapter<YunnanAuthBobmEntity, BaseViewHolder> {

    public YunAuthDataAdapter(int layoutResId, @Nullable List<YunnanAuthBobmEntity> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, YunnanAuthBobmEntity item) {
        ((TextView) helper.getView(R.id.contract_code)).setText("文件ID："+item.getFileId());
        ((TextView) helper.getView(R.id.contract_name)).setText("雷管数量："+ item.getLgmCount());
        ((TextView) helper.getView(R.id.create_time)).setText("申请时间："+ DateStringUtils.getDateString(item.getDate()));
    }
}
