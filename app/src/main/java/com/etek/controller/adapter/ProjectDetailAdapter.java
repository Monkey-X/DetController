package com.etek.controller.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.etek.controller.R;
import com.etek.controller.persistence.entity.DetonatorEntity;
import com.etek.controller.persistence.entity.ProjectInfoEntity;

import java.util.List;

public class ProjectDetailAdapter extends RecyclerView.Adapter<ProjectDetailAdapter.ProjectDetailViewHolder> {

    private List<DetonatorEntity> datas;
    private  Context context;

    public ProjectDetailAdapter(Context context, List<DetonatorEntity> datas){
        this.context = context;
        this.datas = datas;
    }

    @NonNull
    @Override
    public ProjectDetailAdapter.ProjectDetailViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View inflate = LayoutInflater.from(context).inflate(R.layout.project_detail_item_view, viewGroup, false);
        ProjectDetailAdapter.ProjectDetailViewHolder  projectViewHolder = new ProjectDetailAdapter.ProjectDetailViewHolder(inflate);
        return projectViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ProjectDetailAdapter.ProjectDetailViewHolder holder, int i) {
        DetonatorEntity detonatorEntity = datas.get(i);
        holder.holePosition.setText(detonatorEntity.getHolePosition());
        holder.uidNum.setText(detonatorEntity.getUid());
        holder.delayTime.setText(detonatorEntity.getRelay());
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    public class ProjectDetailViewHolder extends RecyclerView.ViewHolder {

        TextView holePosition;
        TextView uidNum;
        TextView delayTime;

        public ProjectDetailViewHolder(@NonNull View itemView) {
            super(itemView);
             holePosition = itemView.findViewById(R.id.holePosition);
             uidNum = itemView.findViewById(R.id.uid_num);
             delayTime = itemView.findViewById(R.id.delaytime);
        }
    }
}
