<<<<<<< HEAD
package com.etek.controller.adapter;


import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.etek.controller.R;
import com.etek.controller.persistence.entity.PermissibleZoneEntity;



/**
 * https://github.com/CymChad/BaseRecyclerViewAdapterHelper
 */
public class PermissibleZoneAdapter extends BaseQuickAdapter<PermissibleZoneEntity, BaseViewHolder> {
    public PermissibleZoneAdapter() {
        super(R.layout.item_forbidden_zone);
    }

    @Override
    protected void convert(BaseViewHolder helper, PermissibleZoneEntity item) {
        helper.setText(R.id.location_name, "经度：" + item.getLongitude() + "  维度：" + item.getLatitude()+ "  半径：" + item.getRadius());
    }
}
=======
package com.etek.controller.adapter;


import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.etek.controller.R;
import com.etek.controller.persistence.entity.PermissibleZoneEntity;



/**
 * https://github.com/CymChad/BaseRecyclerViewAdapterHelper
 */
public class PermissibleZoneAdapter extends BaseQuickAdapter<PermissibleZoneEntity, BaseViewHolder> {
    public PermissibleZoneAdapter() {
        super(R.layout.item_forbidden_zone);
    }

    @Override
    protected void convert(BaseViewHolder helper, PermissibleZoneEntity item) {
        helper.setText(R.id.location_name, "经度：" + item.getLongitude() + "  维度：" + item.getLatitude()+ "  半径：" + item.getRadius());
    }
}
>>>>>>> 806c842... 雷管组网
