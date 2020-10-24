<<<<<<< HEAD
package com.etek.controller.adapter;

import android.support.annotation.NonNull;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.etek.controller.R;
import com.etek.controller.persistence.entity.ProjectInfoEntity;

import org.apache.commons.lang3.StringUtils;

public class ProjectInfoAdapter extends BaseQuickAdapter<ProjectInfoEntity, BaseViewHolder> {
    public ProjectInfoAdapter() {
        super(R.layout.item_project_info, null);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, ProjectInfoEntity item) {
//        ((TextView) helper.getView(R.id.pro_index)).setText(""+item.getId());

        if (!StringUtils.isEmpty(item.getContractCode())) {
            ((TextView) helper.getView(R.id.contract_code)).setText(mContext.getString(R.string.contract_code_param, item.getContractCode()));
            ((TextView) helper.getView(R.id.contract_sn_name)).setText(item.getContractName());
            helper.getView(R.id.rl_contract_name).setVisibility(View.VISIBLE);
            helper.getView(R.id.rl_contract_code).setVisibility(View.VISIBLE);

        } else if (!StringUtils.isEmpty(item.getProCode())) {
            ((TextView) helper.getView(R.id.contract_code)).setText(mContext.getString(R.string.project_code_param, item.getProCode()));
            ((TextView) helper.getView(R.id.contract_sn_name)).setText(item.getProName());
            helper.getView(R.id.rl_contract_name).setVisibility(View.VISIBLE);
            helper.getView(R.id.rl_contract_code).setVisibility(View.VISIBLE);
        } else {
            ((TextView) helper.getView(R.id.contract_code)).setText("");
            ((TextView) helper.getView(R.id.contract_sn_name)).setText("");
            helper.getView(R.id.rl_contract_name).setVisibility(View.GONE);
            helper.getView(R.id.rl_contract_code).setVisibility(View.GONE);
        }

        ((TextView) helper.getView(R.id.company_name)).setText(item.getCompanyName());
        if (item.getControllerList() != null && !item.getControllerList().isEmpty())
            ((TextView) helper.getView(R.id.det_size)).setText("" + item.getDetonatorList().size());
//        Button isSelect =  ((Button) helper.getView(R.id.is_select));
//        if(item.isSelect()){
//            isSelect.setVisibility(View.VISIBLE);
//        }else {
//            isSelect.setVisibility(View.INVISIBLE);
//        }

    }


}
=======
package com.etek.controller.adapter;

import android.support.annotation.NonNull;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.etek.controller.R;
import com.etek.controller.persistence.entity.ProjectInfoEntity;

import org.apache.commons.lang3.StringUtils;

public class ProjectInfoAdapter extends BaseQuickAdapter<ProjectInfoEntity, BaseViewHolder> {
    public ProjectInfoAdapter() {
        super(R.layout.item_project_info, null);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, ProjectInfoEntity item) {
//        ((TextView) helper.getView(R.id.pro_index)).setText(""+item.getId());

        if (!StringUtils.isEmpty(item.getContractCode())) {
            ((TextView) helper.getView(R.id.contract_code)).setText(mContext.getString(R.string.contract_code_param, item.getContractCode()));
            ((TextView) helper.getView(R.id.contract_sn_name)).setText(item.getContractName());
            helper.getView(R.id.rl_contract_name).setVisibility(View.VISIBLE);
            helper.getView(R.id.rl_contract_code).setVisibility(View.VISIBLE);

        } else if (!StringUtils.isEmpty(item.getProCode())) {
            ((TextView) helper.getView(R.id.contract_code)).setText(mContext.getString(R.string.project_code_param, item.getProCode()));
            ((TextView) helper.getView(R.id.contract_sn_name)).setText(item.getProName());
            helper.getView(R.id.rl_contract_name).setVisibility(View.VISIBLE);
            helper.getView(R.id.rl_contract_code).setVisibility(View.VISIBLE);
        } else {
            ((TextView) helper.getView(R.id.contract_code)).setText("");
            ((TextView) helper.getView(R.id.contract_sn_name)).setText("");
            helper.getView(R.id.rl_contract_name).setVisibility(View.GONE);
            helper.getView(R.id.rl_contract_code).setVisibility(View.GONE);
        }

        ((TextView) helper.getView(R.id.company_name)).setText(item.getCompanyName());
        if (item.getControllerList() != null && !item.getControllerList().isEmpty())
            ((TextView) helper.getView(R.id.det_size)).setText("" + item.getDetonatorList().size());
//        Button isSelect =  ((Button) helper.getView(R.id.is_select));
//        if(item.isSelect()){
//            isSelect.setVisibility(View.VISIBLE);
//        }else {
//            isSelect.setVisibility(View.INVISIBLE);
//        }

    }


}
>>>>>>> 806c842... 雷管组网
