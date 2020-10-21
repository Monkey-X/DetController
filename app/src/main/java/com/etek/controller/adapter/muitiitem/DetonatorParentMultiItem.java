package com.etek.controller.adapter.muitiitem;

import com.chad.library.adapter.base.entity.AbstractExpandableItem;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.etek.controller.adapter.DetControllerAdapter;


public class DetonatorParentMultiItem extends AbstractExpandableItem<DetonatorMultiItem> implements MultiItemEntity {
    private String content;

    public DetonatorParentMultiItem(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public int getLevel() {
        return 0;
    }

    @Override
    public int getItemType() {
        return DetControllerAdapter.TYPE_DETONATOR_PARENT;
    }
}
