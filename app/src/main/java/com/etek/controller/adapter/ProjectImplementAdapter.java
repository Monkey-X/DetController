package com.etek.controller.adapter;

import android.support.annotation.Nullable;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.etek.controller.R;
import com.etek.controller.common.AppIntentString;
import com.etek.controller.entity.ProjectImplementItem;
import java.util.List;

public class ProjectImplementAdapter extends BaseQuickAdapter<ProjectImplementItem, BaseViewHolder> {

    public ProjectImplementAdapter(int layoutResId, @Nullable List<ProjectImplementItem> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, ProjectImplementItem item) {
        helper.setText(R.id.title, item.getTitle());
        int position = helper.getAdapterPosition();
        String status = item.getStatus();
        if (status == null){//如果为空，给个默认值（默认第一个是可点击的）
            status = AppIntentString.PROJECT_IMPLEMENT_CONNECT_TEST;
        }
        switch (status) {
            case AppIntentString.PROJECT_IMPLEMENT_CONNECT_TEST://第一个，前一个有颜色，其余四个置灰
                if (position == 0) {
                    helper.setBackgroundRes(R.id.project_implement_item, item.getBackground());
                    helper.itemView.setClickable(true);
                } else {
                    helper.setBackgroundRes(R.id.project_implement_item, item.getNuBackground());
                    helper.itemView.setClickable(false);
                }
                break;

            case AppIntentString.PROJECT_IMPLEMENT_DELAY_DOWNLOAD://第二个，前二个有颜色，其余三个置灰
                if (position == 0 || position == 1) {
                    helper.setBackgroundRes(R.id.project_implement_item, item.getBackground());
                    helper.itemView.setClickable(true);
                } else {
                    helper.setBackgroundRes(R.id.project_implement_item, item.getNuBackground());
                    helper.itemView.setClickable(false);
                }
                break;

            case AppIntentString.PROJECT_IMPLEMENT_ONLINE_AUTHORIZE://第三个，前三个有颜色，其余二个置灰
                if (position == 0 || position == 1 || position == 2) {
                    helper.setBackgroundRes(R.id.project_implement_item, item.getBackground());
                    helper.itemView.setClickable(true);
                } else {
                    helper.setBackgroundRes(R.id.project_implement_item, item.getNuBackground());
                    helper.itemView.setClickable(false);
                }
                break;

            case AppIntentString.PROJECT_IMPLEMENT_POWER_BOMB://第四个，前四个有颜色，其余一个置灰
                if (position == 0 || position == 1 || position == 2 || position == 3) {
                    helper.setBackgroundRes(R.id.project_implement_item, item.getBackground());
                    helper.itemView.setClickable(true);
                } else {
                    helper.setBackgroundRes(R.id.project_implement_item, item.getNuBackground());
                    helper.itemView.setClickable(false);
                }
                break;

            case AppIntentString.PROJECT_IMPLEMENT_DATA_REPORT://第五个，全部有颜色
                if (position == 0 || position == 1 || position == 2 || position == 3 || position == 4) {
                    helper.setBackgroundRes(R.id.project_implement_item, item.getBackground());
                    helper.itemView.setClickable(true);
                } else {
                    helper.setBackgroundRes(R.id.project_implement_item, item.getNuBackground());
                    helper.itemView.setClickable(false);
                }
                break;
        }
    }
}
