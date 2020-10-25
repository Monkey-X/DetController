package com.etek.controller.adapter;

import android.support.annotation.Nullable;
import android.widget.TextView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.etek.controller.R;
import com.etek.controller.persistence.entity.DetonatorEntity;
import java.util.List;

public class ConnectTestAdapter extends BaseQuickAdapter<DetonatorEntity, BaseViewHolder> {

    public ConnectTestAdapter(int layoutResId, @Nullable List<DetonatorEntity> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, DetonatorEntity item) {
        ((TextView) helper.getView(R.id.serial_number)).setText(helper.getLayoutPosition()+ "");
        ((TextView) helper.getView(R.id.tube_code)).setText(item.getCode());
        ((TextView) helper.getView(R.id.hole_location)).setText(item.getHolePosition());
        ((TextView) helper.getView(R.id.connect)).setText(item.getStatus() == 0 ? "失败" : "成功");
    }
}
