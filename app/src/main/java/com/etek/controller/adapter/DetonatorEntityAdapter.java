<<<<<<< HEAD
package com.etek.controller.adapter;



import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.etek.controller.R;

import com.etek.controller.persistence.entity.DetonatorEntity;





public class DetonatorEntityAdapter extends BaseQuickAdapter<DetonatorEntity, BaseViewHolder> {
    public DetonatorEntityAdapter() {
        super(R.layout.item_detonator);
    }

    @Override
    protected void convert(BaseViewHolder helper, DetonatorEntity item) {
        helper.setText(R.id.det_num, ""+item.getId());
        helper.setText(R.id.det_code, mContext.getString(R.string.det_code,item.getCode()));
        helper.setText(R.id.det_uid, mContext.getString(R.string.det_uid,item.getUid()));
        helper.setText(R.id.det_accode, mContext.getString(R.string.det_workcode, item.getWorkCode()));
    }
}
=======
package com.etek.controller.adapter;



import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.etek.controller.R;

import com.etek.controller.persistence.entity.DetonatorEntity;





public class DetonatorEntityAdapter extends BaseQuickAdapter<DetonatorEntity, BaseViewHolder> {
    public DetonatorEntityAdapter() {
        super(R.layout.item_detonator);
    }

    @Override
    protected void convert(BaseViewHolder helper, DetonatorEntity item) {
        helper.setText(R.id.det_num, ""+item.getId());
        helper.setText(R.id.det_code, mContext.getString(R.string.det_code,item.getCode()));
        helper.setText(R.id.det_uid, mContext.getString(R.string.det_uid,item.getUid()));
        helper.setText(R.id.det_accode, mContext.getString(R.string.det_workcode, item.getWorkCode()));
    }
}
>>>>>>> 806c842... 雷管组网
