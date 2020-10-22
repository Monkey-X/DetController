package com.etek.controller.adapter;

import android.support.annotation.Nullable;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.etek.controller.R;
import com.etek.controller.entity.AssistItem;
import java.util.List;

public class AssistAdapter extends BaseQuickAdapter<AssistItem, BaseViewHolder> {

    public AssistAdapter(int layoutResId, @Nullable List<AssistItem> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, AssistItem item) {
        helper.setText(R.id.text, item.getTitle());
        helper.setImageResource(R.id.icon, item.getImageResource());
    }
}
