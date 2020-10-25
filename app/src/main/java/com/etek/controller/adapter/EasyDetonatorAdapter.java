package com.etek.controller.adapter;



import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.etek.controller.R;
import com.etek.controller.entity.Detonator;
import com.etek.controller.enums.DetStatusEnum;
import com.etek.controller.utils.SommerUtils;


public class EasyDetonatorAdapter extends BaseQuickAdapter<Detonator, BaseViewHolder> {
    public EasyDetonatorAdapter() {
        super(R.layout.item_easy_detonator);
    }

//    public ConDetonatorAdapter(List<Detonator> detonatorList) {
//        this.addData();
//        super(R.layout.item_detonator);
//    }

    @Override
    protected void convert(BaseViewHolder helper, Detonator item) {

        helper.setText(R.id.det_code, mContext.getString(R.string.det_code,item.getDetCode()));
//        helper.setText(R.id.det_uid, mContext.getString(R.string.det_uid,item.getUid()));
        DetStatusEnum detStatusEnum = DetStatusEnum.getByStatus(item.getStatus());
        helper.setText(R.id.det_status,detStatusEnum.getName());
        helper.setBackgroundColor(R.id.det_status,mContext.getColor(detStatusEnum.getColor()));
    }
}
