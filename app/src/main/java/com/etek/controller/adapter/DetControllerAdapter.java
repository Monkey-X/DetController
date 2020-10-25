package com.etek.controller.adapter;

import android.view.View;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.etek.controller.R;
import com.etek.controller.adapter.muitiitem.ControllerMultiItem;
import com.etek.controller.adapter.muitiitem.DetonatorMultiItem;
import com.etek.controller.adapter.muitiitem.DetonatorParentMultiItem;

import java.util.List;

public class DetControllerAdapter extends BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder> {
    public static final int TYPE_CONTROLLER = 0;
    public static final int TYPE_LOCATION = 1;
    public static final int TYPE_DETONATOR_PARENT = 2;
    public static final int TYPE_DETONATOR = 3;
    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    public DetControllerAdapter(List<MultiItemEntity> data) {
        super(data);
        addItemType(TYPE_CONTROLLER, R.layout.multiitem_controller);
        addItemType(TYPE_DETONATOR_PARENT, R.layout.multiitem_detonator_parent);
        addItemType(TYPE_DETONATOR, R.layout.item_list_detonator);
    }

    @Override
    protected void convert(final BaseViewHolder helper, MultiItemEntity item) {
        switch (helper.getItemViewType()) {
            case TYPE_DETONATOR_PARENT:
                final DetonatorParentMultiItem detonatorParentMultiItem = (DetonatorParentMultiItem) item;
                helper.setText(R.id.content, detonatorParentMultiItem.getContent());

                helper.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int pos = helper.getAdapterPosition();
                        if (detonatorParentMultiItem.isExpanded()) {
                            collapse(pos);
                        } else {
                            expand(pos);
                        }
                    }
                });
                break;
            case TYPE_CONTROLLER:
                final ControllerMultiItem controllerMultiItem = (ControllerMultiItem) item;
                helper.setText(R.id.content, controllerMultiItem.getContractId());

                break;

            case TYPE_DETONATOR:
                final DetonatorMultiItem detonatorMultiItem = (DetonatorMultiItem) item;
                helper.setText(R.id.det_code, detonatorMultiItem.getDetCode());
                helper.setText(R.id.det_num, ""+detonatorMultiItem.getNum());
                break;

        }
    }
}
