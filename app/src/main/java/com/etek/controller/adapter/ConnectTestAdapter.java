package com.etek.controller.adapter;

import android.support.annotation.Nullable;
import android.widget.TextView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.etek.controller.R;
import com.etek.controller.entity.ConnectTestItem;
import java.util.List;

public class ConnectTestAdapter extends BaseQuickAdapter<ConnectTestItem, BaseViewHolder> {

    public ConnectTestAdapter(int layoutResId, @Nullable List<ConnectTestItem> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, ConnectTestItem item) {
        ((TextView) helper.getView(R.id.serial_number)).setText(item.getSerialNum() + "");
        ((TextView) helper.getView(R.id.tube_code)).setText(item.getTubeCode());
        ((TextView) helper.getView(R.id.hole_location)).setText(item.getHoleLocation());
        ((TextView) helper.getView(R.id.connect)).setText(item.getConnect());
    }
}
