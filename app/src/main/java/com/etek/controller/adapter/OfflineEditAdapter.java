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

    public OfflineEditAdapter(Context context, List<Detonator> datas) {
        this.context = context;
        this.datas = datas;
    }

    @NonNull
    @Override
    public OfflineEditViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View inflate = LayoutInflater.from(context).inflate(R.layout.item_offline_edit, viewGroup, false);
        OfflineEditAdapter.OfflineEditViewHolder projectViewHolder = new OfflineEditAdapter.OfflineEditViewHolder(inflate);
        return projectViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull OfflineEditViewHolder offlineEditViewHolder, int i) {
        offlineEditViewHolder.numPostion.setText(String.valueOf(i+1));
        Detonator detonator = datas.get(i);
        offlineEditViewHolder.detCode.setText(detonator.getDetCode());
        offlineEditViewHolder.detStatus.setText(detonator.getStatusName());
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
