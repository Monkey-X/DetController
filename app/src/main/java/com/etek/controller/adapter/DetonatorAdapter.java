package com.etek.controller.adapter;



import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.etek.controller.R;
import com.etek.controller.entity.Detonator;
import com.etek.controller.persistence.entity.DetonatorEntity;
import com.etek.controller.utils.SommerUtils;

import java.util.List;


public class DetonatorAdapter extends BaseQuickAdapter<Detonator, BaseViewHolder> {
    public DetonatorAdapter() {
        super(R.layout.item_detonator);
    }

//    public ConDetonatorAdapter(List<Detonator> detonatorList) {
//        this.addData();
//        super(R.layout.item_detonator);
//    }

    @Override
    protected void convert(BaseViewHolder helper, Detonator item) {
        helper.setText(R.id.det_num, ""+item.getNum());
        helper.setText(R.id.det_code, mContext.getString(R.string.det_code,item.getDetCode()));
        helper.setText(R.id.det_uid, mContext.getString(R.string.det_uid,item.getUid()));
        helper.setText(R.id.det_accode, mContext.getString(R.string.det_workcode, SommerUtils.bytesToHexString(item.getAcCode())));
    }
}
