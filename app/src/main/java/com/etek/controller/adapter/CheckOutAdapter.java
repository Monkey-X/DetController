package com.etek.controller.adapter;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.etek.controller.R;
import com.etek.controller.persistence.entity.ChkControllerEntity;
import com.etek.controller.persistence.entity.ProjectInfoEntity;

import org.apache.commons.lang3.StringUtils;

public class CheckOutAdapter extends BaseQuickAdapter<ChkControllerEntity, BaseViewHolder> {
    public CheckOutAdapter() {
        super(R.layout.item_checkout_info, null);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, ChkControllerEntity item) {
//        ((TextView) helper.getView(R.id.pro_index)).setText(""+item.getId());

        ProjectInfoEntity projectInfoEntity = item.getProjectInfoEntity();
        if(projectInfoEntity==null){
            return;
        }
        if(!StringUtils.isEmpty(item.getProjectInfoEntity().getContractCode())){
            ((TextView) helper.getView(R.id.contract_code)).setText(mContext.getString(R.string.contract_code_param,item.getProjectInfoEntity().getContractCode()));
            ((TextView) helper.getView(R.id.contract_sn_name)).setText(item.getProjectInfoEntity().getContractName());
            helper.getView(R.id.contract_code).setVisibility(View.VISIBLE);
            helper.getView(R.id.contract_sn_name).setVisibility(View.VISIBLE);

        }else  if(!StringUtils.isEmpty(item.getProjectInfoEntity().getProCode())){
            ((TextView) helper.getView(R.id.contract_code)).setText(mContext.getString(R.string.project_code_param,item.getProjectInfoEntity().getProCode()));
            ((TextView) helper.getView(R.id.contract_sn_name)).setText(item.getProjectInfoEntity().getProName());
            helper.getView(R.id.contract_code).setVisibility(View.VISIBLE);
            helper.getView(R.id.contract_sn_name).setVisibility(View.VISIBLE);
        }else {
            ((TextView) helper.getView(R.id.contract_code)).setText(mContext.getString(R.string.project_code_param,"为空"));
            ((TextView) helper.getView(R.id.contract_sn_name)).setText("空");
            helper.getView(R.id.contract_code).setVisibility(View.GONE);
            helper.getView(R.id.contract_sn_name).setVisibility(View.GONE);
        }

        ((TextView) helper.getView(R.id.company_name)).setText(item.getProjectInfoEntity().getCompanyName());

        ((TextView) helper.getView(R.id.det_size)).setText(""+item.getChkDetonatorList().size());

        ((TextView) helper.getView(R.id.controller_name)).setText(mContext.getString(R.string.controller_param,item.getSn()));
//        ((TextView) helper.getView(R.id.token)).setText(mContext.getString(R.string.token_param,item.getToken()));

//        Button isSelect =  ((Button) helper.getView(R.id.is_select));
//        if(item.isSelect()){
//            isSelect.setVisibility(View.VISIBLE);
//        }else {
//            isSelect.setVisibility(View.INVISIBLE);
//        }

    }




}
