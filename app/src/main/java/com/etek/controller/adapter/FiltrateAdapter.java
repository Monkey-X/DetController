package com.etek.controller.adapter;

import android.support.annotation.Nullable;
import android.widget.TextView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.etek.controller.R;
import com.etek.controller.dto.ProjectInfoDto;
import com.etek.controller.persistence.entity.ProjectInfoEntity;

import java.util.List;

public class FiltrateAdapter extends BaseQuickAdapter<ProjectInfoEntity, BaseViewHolder> {

    public FiltrateAdapter(int layoutResId, @Nullable List<ProjectInfoEntity> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, ProjectInfoEntity item) {
        ((TextView) helper.getView(R.id.filtrate_item)).setText(item.getProName());
    }
}
