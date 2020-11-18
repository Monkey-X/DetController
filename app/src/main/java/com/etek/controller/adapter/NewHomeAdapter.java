package com.etek.controller.adapter;

import android.support.annotation.Nullable;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.etek.controller.R;
import com.etek.controller.entity.NewHomeItem;
import java.util.List;

public class NewHomeAdapter extends BaseQuickAdapter<NewHomeItem, BaseViewHolder> {

    public NewHomeAdapter(int layoutResId, @Nullable List<NewHomeItem> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, NewHomeItem item) {
        helper.setBackgroundRes(R.id.home_item,item.getBackground());
        helper.setText(R.id.title, item.getTitle());
        helper.setText(R.id.description, item.getDescription());
    }
}
