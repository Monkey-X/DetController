<<<<<<< HEAD
package com.etek.controller.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.etek.controller.R;
import com.etek.controller.entity.DetReportDetail;
import com.etek.controller.utils.SommerUtils;
import com.etek.sommerlibrary.utils.DateUtil;

import java.util.ArrayList;


public class DetReportDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<DetReportDetail> mlist=new ArrayList<DetReportDetail>();
    private Context mContext;

    public DetReportDetailAdapter(ArrayList<DetReportDetail> mlist, Context mcontext) {
        this.mlist = mlist;
        this.mContext = mcontext;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.item_list_detdetail, parent, false);
        RecyclerView.ViewHolder holder=null;
        holder=new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        //先进行强转
        ViewHolder holder1 = (ViewHolder) holder;

        //绑定数据
        holder1.mDevice.setText(mlist.get(position).getDetonatorid());
        holder1.mDescription.setText(DateUtil.getDateShortStr(mlist.get(position).getDetTime())
                +" "+mlist.get(position).getLatitude()
                +" "+mlist.get(position).getLongitude());
//        holder1.mIcon.setImageResource(R.drawable.detonator_ico);

    }

    @Override
    public int getItemCount() {
        return mlist==null?0:mlist.size();
    }

    //定义一个ViewHolder
    class ViewHolder extends RecyclerView.ViewHolder{
        private TextView mDevice;
        private TextView mDescription;
//        private TextView mDetCount;
//        private ImageView mIcon;



        public ViewHolder(View itemView) {
            super(itemView);
            mDevice=(TextView)itemView.findViewById(R.id.Detail_device);
            mDescription=(TextView)itemView.findViewById(R.id.Detail_description);
//            mDetCount=(TextView)itemView.findViewById(R.id.Det_count);
//            mIcon=(ImageView)itemView.findViewById(R.id.Detonator_icon);

    }
}
}
=======
package com.etek.controller.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.etek.controller.R;
import com.etek.controller.entity.DetReportDetail;
import com.etek.controller.utils.SommerUtils;
import com.etek.sommerlibrary.utils.DateUtil;

import java.util.ArrayList;


public class DetReportDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<DetReportDetail> mlist=new ArrayList<DetReportDetail>();
    private Context mContext;

    public DetReportDetailAdapter(ArrayList<DetReportDetail> mlist, Context mcontext) {
        this.mlist = mlist;
        this.mContext = mcontext;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.item_list_detdetail, parent, false);
        RecyclerView.ViewHolder holder=null;
        holder=new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        //先进行强转
        ViewHolder holder1 = (ViewHolder) holder;

        //绑定数据
        holder1.mDevice.setText(mlist.get(position).getDetonatorid());
        holder1.mDescription.setText(DateUtil.getDateShortStr(mlist.get(position).getDetTime())
                +" "+mlist.get(position).getLatitude()
                +" "+mlist.get(position).getLongitude());
//        holder1.mIcon.setImageResource(R.drawable.detonator_ico);

    }

    @Override
    public int getItemCount() {
        return mlist==null?0:mlist.size();
    }

    //定义一个ViewHolder
    class ViewHolder extends RecyclerView.ViewHolder{
        private TextView mDevice;
        private TextView mDescription;
//        private TextView mDetCount;
//        private ImageView mIcon;



        public ViewHolder(View itemView) {
            super(itemView);
            mDevice=(TextView)itemView.findViewById(R.id.Detail_device);
            mDescription=(TextView)itemView.findViewById(R.id.Detail_description);
//            mDetCount=(TextView)itemView.findViewById(R.id.Det_count);
//            mIcon=(ImageView)itemView.findViewById(R.id.Detonator_icon);

    }
}
}
>>>>>>> 806c842... 雷管组网
