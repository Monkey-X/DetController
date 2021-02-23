package com.etek.controller.adapter;

import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.etek.controller.R;
import com.etek.controller.dto.ProjectDownLoadEntity;
import com.etek.controller.persistence.entity.ProjectInfoEntity;
import com.etek.sommerlibrary.utils.DateUtil;
import com.google.gson.Gson;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import cz.msebera.android.httpclient.util.TextUtils;

public class ContractAdapter extends BaseQuickAdapter<ProjectInfoEntity, BaseViewHolder>  {

    public ContractAdapter(int layoutResId, @Nullable List<ProjectInfoEntity> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, ProjectInfoEntity item) {
        String proCode = item.getProCode();
        String contractCode = item.getContractCode();// 合同编号
        Date createTime = item.getApplyDate();

        if (!TextUtils.isEmpty(contractCode)) {
            ((TextView) helper.getView(R.id.contract_code)).setText("合同备案序号："+contractCode);
        }else{
            String strProID = item.getProCode();
            if(!TextUtils.isEmpty(strProID)){
                ((TextView) helper.getView(R.id.contract_code)).setText("项目编号："+strProID);
            }else{
                ((TextView) helper.getView(R.id.contract_code)).setVisibility(View.GONE);
            }
        }

        //  修改为：雷管数量
        int num = item.getDetonatorList().size();
        ((TextView) helper.getView(R.id.contract_name)).setText("雷管数量："+ num);
        /*
        if (!TextUtils.isEmpty(proCode)) {
            ((TextView) helper.getView(R.id.contract_name)).setText("项目编号："+proCode);
        }else{
            ((TextView) helper.getView(R.id.contract_name)).setVisibility(View.GONE);
        }
        */

        // 创建时间
        if (createTime !=null) {
            String dateStr = DateUtil.getDateStr(createTime);
            ((TextView) helper.getView(R.id.create_time)).setText(String.format("申请日期时间：%s",dateStr));//创建时间
        }else{
            ((TextView) helper.getView(R.id.create_time)).setVisibility(View.GONE);
        }
    }

}
