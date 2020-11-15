package com.etek.controller.adapter;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextPaint;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.etek.controller.R;
import com.etek.controller.entity.DetController;
import com.etek.sommerlibrary.ui.SommerBaseAdapter;
import com.etek.sommerlibrary.utils.DateUtil;
import java.util.ArrayList;
import java.util.List;


public class DetReportAdapter2 extends SommerBaseAdapter<DetReportAdapter2.ViewHolder> {

    public DetReportAdapter2(Context mcontext) {
        super(mcontext);
        mList = new ArrayList<DetController>();
        this.mContext = mcontext;
    }

    public DetReportAdapter2(Context mcontext, List list) {
        super(mcontext);
        mList = list;
        this.mContext = mcontext;
    }

    @Override
    public void notifyDataSetChanged(List dataList) {

    }

    @Override
    public DetReportAdapter2.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new DetReportAdapter2.ViewHolder(getInflater().inflate(R.layout.item_det_report, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //先进行强转
        ViewHolder holder1 = (ViewHolder) holder;
//        SimpleDateFormat sdf = new SimpleDateFormat("MM月dd日 HH:mm:ss");
        DetController controller = (DetController) mList.get(position);

        //绑定数据
        holder1.controlSn.setText(controller.getSn());
        int size = 0;
        if (controller.getDetList() != null) {
            size = controller.getDetList().size();
        }

        holder1.mDetCount.setText(Integer.toString(size));
//        holder1.mIndex.setText(Long.toString(controller.getId()));
        holder1.rptTime.setText(DateUtil.getDateStr(controller.getBlastTime()));
        if (controller.getStatus() == 0) {

            holder1.ctrlStatus.setText(R.string.un_report);
            holder1.ctrlStatus.setTextColor(getMyColor(R.color.red));
//            viewHolder.setTextColor(R.id.contrl_status,0xff0000);
        } else if (controller.getStatus() == 1) {
            holder1.ctrlStatus.setText(R.string.reported);
            holder1.ctrlStatus.setTextColor(getMyColor(R.color.green));
//            ((TextView)viewHolder.getView(R.id.contrl_status)).setTextColor(0x008000);
//            viewHolder.setTextColor(R.id.contrl_status,);

        } else if (controller.getStatus() == 2) {
            holder1.ctrlStatus.setText(R.string.report_error);
            holder1.ctrlStatus.setTextColor(getMyColor(R.color.orange));
//            viewHolder.setTextColor(R.id.contrl_status,0x008000);

        }
    }


    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }


    public void dataChange(List dataList) {
        this.mList = dataList;
        notifyDataSetChanged();
    }

    //定义一个ViewHolder
    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView controlSn;
        private TextView mDetCount;
        private TextView rptTime;
        private TextView ctrlStatus;

        public ViewHolder(View itemView) {
            super(itemView);
            controlSn = itemView.findViewById(R.id.contrl_sn);
//            mIndex = itemView.findViewById(R.id.ctrl_index);
            mDetCount =  itemView.findViewById(R.id.det_size);
            TextPaint tp = mDetCount.getPaint();
            tp.setFakeBoldText(true);

            rptTime =itemView.findViewById(R.id.rpt_time);
            ctrlStatus = itemView.findViewById(R.id.contrl_status);
//            proId =itemView.findViewById(R.id.project_id);;
        }
    }
}
