package com.etek.controller.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.etek.controller.R;
import com.etek.controller.entity.Detonator;

import java.util.List;

public class OfflineEditAdapter extends RecyclerView.Adapter<OfflineEditAdapter.OfflineEditViewHolder>  {


    private final Context context;
    private final List<Detonator> datas;
    private int selectedPosition = -1; // 表示选中的效果
    private OnItemClickListener onItemClickListener;

    public OfflineEditAdapter(Context context, List<Detonator> datas) {
        this.context = context;
        this.datas = datas;
    }

    @NonNull
    @Override
    public OfflineEditViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_offline_edit, viewGroup, false);
        OfflineEditAdapter.OfflineEditViewHolder projectViewHolder = new OfflineEditAdapter.OfflineEditViewHolder(view);
        return projectViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull OfflineEditViewHolder offlineEditViewHolder, int i) {

        offlineEditViewHolder.itemView.setSelected(selectedPosition == i);
        offlineEditViewHolder.numPostion.setText(String.valueOf(i+1));
        Detonator detonator = datas.get(i);
        offlineEditViewHolder.detCode.setText(detonator.getDetCode());
        offlineEditViewHolder.detStatus.setText(getStatusName(detonator.getStatus()));
        int strStautsColor = getStrStautsColor(detonator.getStatus());
        offlineEditViewHolder.detStatus.setTextColor(context.getColor(strStautsColor));

        offlineEditViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    selectedPosition = i;
                    notifyDataSetChanged();
                    onItemClickListener.onItemClick(v, i);
                }
            }
        });
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }


    private String  getStatusName(int status) {
        String statusName = "";
        if (status == 0) {
            statusName = "正常";
        } else if (status == 1) {
            statusName = "未注册";
        } else if (status == 2) {
            statusName = "已使用";
        } else if (status == 3) {
            statusName = "不存在";
        } else {
            statusName = "异常";
        }
        return statusName;
    }

    private int getStrStautsColor(int status) {
        int strStatusColor = 0;
        if (status == 0) {
            strStatusColor = R.color.mediumseagreen;
        } else if (status == 1) {
            strStatusColor =R.color.crimson;
        }else if (status == 2){
            strStatusColor =R.color.chat_item5_normal;
        }else if (status == 3){
            strStatusColor =R.color.gray_pressed;
        }else {
            strStatusColor= R.color.setting_text_on;
        }
        return strStatusColor;
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    public class OfflineEditViewHolder extends RecyclerView.ViewHolder {

        public TextView numPostion;
        public TextView detCode;
        public TextView detStatus;

        public OfflineEditViewHolder(@NonNull View itemView) {
            super(itemView);
            numPostion = itemView.findViewById(R.id.num_position);
            detCode = itemView.findViewById(R.id.det_code);
            detStatus = itemView.findViewById(R.id.det_status);
        }
    }
}
