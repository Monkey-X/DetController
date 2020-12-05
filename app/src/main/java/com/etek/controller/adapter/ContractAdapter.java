package com.etek.controller.adapter;

import android.support.annotation.Nullable;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.etek.controller.R;
import com.etek.controller.persistence.entity.ProjectInfoEntity;
import java.util.List;

import cz.msebera.android.httpclient.util.TextUtils;

public class ContractAdapter extends BaseQuickAdapter<ProjectInfoEntity, BaseViewHolder>  {

    public ContractAdapter(int layoutResId, @Nullable List<ProjectInfoEntity> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, ProjectInfoEntity item) {
        String contractName = item.getContractName();
        String contractCode = item.getContractCode();
        if (!TextUtils.isEmpty(contractName) || !TextUtils.isEmpty(contractCode)){
            ((TextView) helper.getView(R.id.contract_name)).setText(contractName);//合同名称
            ((TextView) helper.getView(R.id.contract_code)).setText(contractCode);//合同编号
        }
    }
}
