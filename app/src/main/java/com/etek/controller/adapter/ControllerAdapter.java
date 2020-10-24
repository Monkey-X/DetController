<<<<<<< HEAD
package com.etek.controller.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.etek.controller.R;

import com.etek.controller.persistence.entity.ControllerEntity;

public class ControllerAdapter extends BaseQuickAdapter<ControllerEntity, BaseViewHolder> {
    public ControllerAdapter() {
        super(R.layout.item_controller);
    }

    @Override
    protected void convert(BaseViewHolder helper, ControllerEntity item) {
        helper.setText(R.id.controller_name, item.getName());
    }
}
=======
package com.etek.controller.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.etek.controller.R;

import com.etek.controller.persistence.entity.ControllerEntity;

public class ControllerAdapter extends BaseQuickAdapter<ControllerEntity, BaseViewHolder> {
    public ControllerAdapter() {
        super(R.layout.item_controller);
    }

    @Override
    protected void convert(BaseViewHolder helper, ControllerEntity item) {
        helper.setText(R.id.controller_name, item.getName());
    }
}
>>>>>>> 806c842... 雷管组网
