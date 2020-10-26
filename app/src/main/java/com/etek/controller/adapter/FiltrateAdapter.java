package com.etek.controller.adapter;

import android.support.annotation.Nullable;
import android.widget.TextView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.etek.controller.R;
import java.util.List;

public class FiltrateAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

    public FiltrateAdapter(int layoutResId, @Nullable List<String> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {
        ((TextView) helper.getView(R.id.filtrate_item)).setText(item);
    }
}
