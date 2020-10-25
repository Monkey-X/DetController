package com.etek.controller.adapter;


import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.etek.controller.R;
import com.etek.controller.persistence.entity.ForbiddenZoneEntity;



/**
 * https://github.com/CymChad/BaseRecyclerViewAdapterHelper
 */
public class ForbiddenZoneAdapter extends BaseQuickAdapter<ForbiddenZoneEntity, BaseViewHolder> {
    public ForbiddenZoneAdapter() {
        super(R.layout.item_forbidden_zone);
    }

    @Override
    protected void convert(BaseViewHolder helper, ForbiddenZoneEntity item) {
        helper.setText(R.id.location_name, "经度：" + item.getLongitude() + "  维度：" + item.getLatitude()+ "  半径：" + item.getRadius());
    }
}
