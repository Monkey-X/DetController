package com.etek.controller.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.etek.controller.R;
import com.etek.controller.persistence.entity.ProjectInfoEntity;

import java.util.List;

public class ProjectAdapter extends RecyclerView.Adapter<ProjectAdapter.ProjectViewHolder> {

    private  Context context;
    private List<ProjectInfoEntity> datas;
    private OnItemClickListener onItemClickListener;

    public ProjectAdapter(Context context, List<ProjectInfoEntity> datas){
        this.context = context;
        this.datas = datas;
    }

    @NonNull
    @Override
    public ProjectViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View inflate = LayoutInflater.from(context).inflate(R.layout.project_item_view, viewGroup, false);
        ProjectViewHolder projectViewHolder = new ProjectViewHolder(inflate);
        return projectViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ProjectViewHolder projectViewHolder, int i) {
        ProjectInfoEntity projectInfoEntity = datas.get(i);
        projectViewHolder.projectNum.setText(String.valueOf(i+1));
        projectViewHolder.projectName.setText(projectInfoEntity.getProName());
        //添加项目的状态

        projectViewHolder.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener!=null) {
                    onItemClickListener.onItemClick(i);
                }
            }
        });
        projectViewHolder.rootView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (onItemClickListener!=null) {
                    onItemClickListener.onItemLongCLick(i);
                }
                return true;
            }
        });

    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    public class ProjectViewHolder extends ViewHolder{

        private TextView projectNum;
        private TextView projectName;
        private TextView projectStatus;
        private View rootView;

        public ProjectViewHolder(@NonNull View itemView) {
            super(itemView);
            projectNum = itemView.findViewById(R.id.project_num);
            projectName = itemView.findViewById(R.id.project_name);
            projectStatus = itemView.findViewById(R.id.project_status);
            rootView = itemView.findViewById(R.id.item_view);
        }
    }

    public interface  OnItemClickListener{
        void onItemClick(int position);
        void onItemLongCLick(int position);
    }
    public void setOnItemClickListener(OnItemClickListener listener){
        this.onItemClickListener = listener;
    }
}
