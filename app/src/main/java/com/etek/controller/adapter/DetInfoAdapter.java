package com.etek.controller.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextPaint;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.etek.controller.R;
import com.etek.controller.entity.DetReportInfo;
import com.etek.sommerlibrary.ui.SommerBaseAdapter;
import com.etek.sommerlibrary.utils.DateUtil;

import java.util.ArrayList;
import java.util.List;

public class DetInfoAdapter extends SommerBaseAdapter<DetInfoAdapter.ViewHolder> {


    public DetInfoAdapter(Context mcontext) {
        super(mcontext);
        mList = new ArrayList<DetReportInfo>();
        this.mContext = mcontext;

    }

    public DetInfoAdapter(Context mcontext, List list) {
        super(mcontext);
        mList = list;
        this.mContext = mcontext;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

//        LayoutInflater inflater = LayoutInflater.from(mcontext);
//        View view = inflater.inflate(R.layout.item_list_detinfo, parent, false);
//        RecyclerView.ViewHolder holder=null;
//        holder=new ViewHolder(view);

        return new ViewHolder(getInflater().inflate(R.layout.item_list_detinfo, parent, false));
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //先进行强转
        ViewHolder holder1 = (ViewHolder) holder;
//        SimpleDateFormat sdf = new SimpleDateFormat("MM月dd日 HH:mm:ss");
        DetReportInfo detReportInfo = (DetReportInfo) mList.get(position);
        String devId = mContext.getString(R.string.device_id_data, detReportInfo.getDevice());
        //绑定数据
        holder1.mDevice.setText(devId);
        String devCount = "" + detReportInfo.getCount();
        holder1.mDetCount.setText(devCount);
        String devTime = mContext.getString(R.string.dev_time_data, DateUtil.getDateDStr(detReportInfo.getDetTime()));
        holder1.mTime.setText(devTime);
        String devAddress = mContext.getString(R.string.dev_address_data, detReportInfo.getAddress());
        holder1.mAddress.setText(devAddress);
        holder1.mIndex.setText("" + detReportInfo.getIndex());
    }


    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    @Override
    public void notifyDataSetChanged(List dataList) {
        this.mList = dataList;
        super.notifyDataSetChanged();
    }

    //定义一个ViewHolder
    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mDevice;
        private TextView mTime;
        private TextView mDetCount;
        private TextView mIndex;
        private TextView mAddress;


        public ViewHolder(View itemView) {
            super(itemView);
            mDevice = (TextView) itemView.findViewById(R.id.det_device);
            mTime = (TextView) itemView.findViewById(R.id.det_time);
            mDetCount = (TextView) itemView.findViewById(R.id.det_count);
            TextPaint tp = mDetCount.getPaint();
            tp.setFakeBoldText(true);
            mIndex = (TextView) itemView.findViewById(R.id.det_index);
            mAddress = (TextView) itemView.findViewById(R.id.det_address);
        }
    }
}
