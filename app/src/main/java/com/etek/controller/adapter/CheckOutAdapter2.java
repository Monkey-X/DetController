package com.etek.controller.adapter;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.etek.controller.R;
import com.etek.controller.persistence.DBManager;
import com.etek.controller.persistence.entity.DetonatorEntity;
import com.etek.controller.persistence.entity.ProjectInfoEntity;
import com.etek.controller.persistence.gen.DetonatorEntityDao;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class CheckOutAdapter2 extends BaseQuickAdapter<ProjectInfoEntity, BaseViewHolder> {

    private OnItemClickListener onItemClickListener;

    public CheckOutAdapter2() {
        super(R.layout.item_checkout_info2, null);
    }


    @Override
    protected void convert(BaseViewHolder helper, ProjectInfoEntity item) {
        if(item == null){
            return;
        }
        RelativeLayout checkOut = helper.getView(R.id.check_out);
        LinearLayout checkOutItem = helper.getView(R.id.check_out_item);
        //点击条目
        checkOutItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener!=null) {
                    onItemClickListener.onItemCLick(item,helper.getAdapterPosition());
                }
            }
        });

        //点击校验
        checkOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener!=null) {
                    onItemClickListener.onCheckOutClick(item,helper.getAdapterPosition());
                }
            }
        });

        if(!StringUtils.isEmpty(item.getContractCode())){
            ((TextView) helper.getView(R.id.contract_code)).setText(mContext.getString(R.string.contract_code_param,item.getContractCode()));//合同编号
            ((TextView) helper.getView(R.id.contract_sn_name)).setText(item.getContractName());//合同名称
            helper.getView(R.id.contract_code).setVisibility(View.VISIBLE);
            helper.getView(R.id.contract_sn_name).setVisibility(View.VISIBLE);
        }else  if(!StringUtils.isEmpty(item.getProCode())){//
            ((TextView) helper.getView(R.id.contract_code)).setText(mContext.getString(R.string.project_code_param,item.getProCode()));//项目编号
            ((TextView) helper.getView(R.id.contract_sn_name)).setText(item.getProName());//项目名称
            helper.getView(R.id.contract_code).setVisibility(View.VISIBLE);
            helper.getView(R.id.contract_sn_name).setVisibility(View.VISIBLE);
        }else {
            ((TextView) helper.getView(R.id.contract_code)).setText(mContext.getString(R.string.project_code_param,"为空"));
            ((TextView) helper.getView(R.id.contract_sn_name)).setText("空");
            helper.getView(R.id.contract_code).setVisibility(View.GONE);
            helper.getView(R.id.contract_sn_name).setVisibility(View.GONE);
        }
        //公司名称
        ((TextView) helper.getView(R.id.company_name)).setText(item.getCompanyName());
        // 雷管数量
        List<DetonatorEntity> list = DBManager.getInstance().getDetonatorEntityDao().queryBuilder().where(DetonatorEntityDao.Properties.ProjectInfoId.eq(item.getId())).list();
        ((TextView) helper.getView(R.id.det_size)).setText(""+list.size());
        // 起爆器编号
        ((TextView) helper.getView(R.id.controller_name)).setText(mContext.getString(R.string.controller_param,"61000255"));
    }

    /**
     * 接口回调
     */
    public interface  OnItemClickListener{
        void onItemCLick(ProjectInfoEntity projectInfoEntity, int position);
        void onCheckOutClick(ProjectInfoEntity projectInfoEntity, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.onItemClickListener = listener;
    }
}
