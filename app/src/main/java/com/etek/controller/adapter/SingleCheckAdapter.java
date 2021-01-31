package com.etek.controller.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.etek.controller.R;
import com.etek.controller.persistence.entity.SingleCheckEntity;

import java.util.List;

public class SingleCheckAdapter extends BaseQuickAdapter<SingleCheckEntity, BaseViewHolder> {

    private int selectedPosition = -1; // 表示选中的效果

    public SingleCheckAdapter(int layoutResId, @Nullable List<SingleCheckEntity> data) {
        super(layoutResId, data);
    }

    public void setSelectedPostion(int position){
        selectedPosition = position;
    }

    @Override
    protected void convert(BaseViewHolder helper, SingleCheckEntity item) {
        helper.itemView.setSelected(selectedPosition == helper.getAdapterPosition());
        helper.setText(R.id.uid_num, item.getDC());
        helper.setText(R.id.delaytime, item.getRelay());
    }
}
