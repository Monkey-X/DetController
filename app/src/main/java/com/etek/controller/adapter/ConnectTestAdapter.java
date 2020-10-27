package com.etek.controller.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.etek.controller.R;
import com.etek.controller.persistence.entity.DetonatorEntity;
import java.util.List;

public class ConnectTestAdapter extends ProjectDetailAdapter {

    public ConnectTestAdapter(Context context, List<DetonatorEntity> datas) {
        super(context, datas);
    }

    @Override
    public void onBindViewHolder(@NonNull ProjectDetailViewHolder holder, int i) {
        DetonatorEntity detonatorEntity = datas.get(i);
        holder.holePosition.setText(detonatorEntity.getTestStatus()+" ");// 表示测试状态
        holder.uidNum.setText(detonatorEntity.getUid());
        holder.number.setText(String.valueOf(i + 1));
        holder.delayTime.setText(detonatorEntity.getHolePosition()); // 标示空位
        holder.rootview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(v, i);
                }
            }
        });
    }
}
