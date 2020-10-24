<<<<<<< HEAD

package com.etek.sommerlibrary.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;

import java.util.List;

/**
 * Created by Sommer 190122
 */
public abstract class SommerBaseAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

    private LayoutInflater mInflater;
    protected Context mContext;
    protected List mList;

    public SommerBaseAdapter(Context context) {
        mContext = context;
        this.mInflater = LayoutInflater.from(context);
    }

    public SommerBaseAdapter(Context context, List list) {
        mContext = context;
        mList = list;
        this.mInflater = LayoutInflater.from(context);
    }

    public LayoutInflater getInflater() {
        return mInflater;
    }

    public abstract void notifyDataSetChanged(List<String> dataList);

    protected int getMyColor(int colorID) {
        return mContext.getResources().getColor(colorID);
    }


}
=======

package com.etek.sommerlibrary.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;

import java.util.List;

/**
 * Created by Sommer 190122
 */
public abstract class SommerBaseAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

    private LayoutInflater mInflater;
    protected Context mContext;
    protected List mList;

    public SommerBaseAdapter(Context context) {
        mContext = context;
        this.mInflater = LayoutInflater.from(context);
    }

    public SommerBaseAdapter(Context context, List list) {
        mContext = context;
        mList = list;
        this.mInflater = LayoutInflater.from(context);
    }

    public LayoutInflater getInflater() {
        return mInflater;
    }

    public abstract void notifyDataSetChanged(List<String> dataList);

    protected int getMyColor(int colorID) {
        return mContext.getResources().getColor(colorID);
    }


}
>>>>>>> 806c842... 雷管组网
