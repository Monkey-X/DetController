package com.etek.controller.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.etek.controller.R;
import com.etek.controller.persistence.entity.DetonatorEntity;
import com.etek.controller.persistence.entity.ProjectDetonator;

import java.util.List;

public class ProjectDelayAdapter extends RecyclerView.Adapter<ProjectDelayAdapter.ProjectDelayViewHolder> {

    private List<ProjectDetonator> datas;
    private Context context;
    private ProjectDelayAdapter.OnItemClickListener onItemClickListener;
    private int selectedPosition = -1; // 表示选中的效果

    public ProjectDelayAdapter(Context context, List<ProjectDetonator> datas) {
        this.context = context;
        this.datas = datas;
    }


    @NonNull
    @Override
    public ProjectDelayViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View inflate = LayoutInflater.from(context).inflate(R.layout.project_delay_download_item, viewGroup, false);
        ProjectDelayAdapter.ProjectDelayViewHolder projectViewHolder = new ProjectDelayAdapter.ProjectDelayViewHolder(inflate);
        return projectViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ProjectDelayViewHolder holder, int i) {
        holder.itemView.setSelected(selectedPosition == i);
        ProjectDetonator detonatorEntity = datas.get(i);
        holder.holePosition.setText(detonatorEntity.getHolePosition());
        holder.uidNum.setText(detonatorEntity.getCode());
        holder.number.setText(String.valueOf(i + 1));
        holder.delayTime.setText(detonatorEntity.getRelay()+"");
        setDownLoadStatus(holder.itemStatus, detonatorEntity.getDownLoadStatus());
        holder.rootview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    selectedPosition = i;
                    notifyDataSetChanged();
                    onItemClickListener.onItemClick(v, i);
                }
            }
        });
        holder.delayTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onDelayTimeClick(i);
                }
            }
        });
    }


    public void setDownLoadStatus(TextView view, int status) {
        if (status == 0) {
            view.setText("OK");
            view.setTextColor(view.getContext().getColor(R.color.palegreen));
        } else if (status == -1){
           view.setText("--");
            view.setTextColor(view.getContext().getColor(R.color.lightgrey));
        }else{
            view.setText("X");
            view.setTextColor(view.getContext().getColor(R.color.red_normal));
        }
    }

    public void setSelectedPostion(int position){
        selectedPosition = position;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    public class ProjectDelayViewHolder extends RecyclerView.ViewHolder {

        TextView holePosition;
        TextView uidNum;
        TextView number;
        TextView delayTime;
        TextView itemStatus;
        View rootview;

        public ProjectDelayViewHolder(View itemView) {
            super(itemView);
            holePosition = itemView.findViewById(R.id.holePosition);
            uidNum = itemView.findViewById(R.id.uid_num);
            number = itemView.findViewById(R.id.number);
            delayTime = itemView.findViewById(R.id.delaytime);
            itemStatus = itemView.findViewById(R.id.item_status);
            rootview = itemView.findViewById(R.id.rootView);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);

        void onDelayTimeClick(int position);
    }

    public void setOnItemClickListener(ProjectDelayAdapter.OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }
}
