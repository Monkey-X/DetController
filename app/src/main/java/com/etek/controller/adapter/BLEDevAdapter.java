<<<<<<< HEAD
package com.etek.controller.adapter;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.etek.controller.R;
import com.etek.controller.model.BleDevice;
import com.etek.controller.utils.BleUtil;

import java.util.ArrayList;
import java.util.List;

public class BLEDevAdapter extends   RecyclerView.Adapter<RecyclerView.ViewHolder>  {

    private Context context;
    private List<BleDevice> bleDeviceList;

    public BLEDevAdapter(Context context) {
        this.context = context;
        bleDeviceList = new ArrayList<>();

    }

    public void addDevice(BleDevice bleDevice) {
        removeDevice(bleDevice);
        bleDeviceList.add(bleDevice);
    }

    public int modifyDevice(BleDevice bleDevice) {
        removeDevice(bleDevice);
        bleDeviceList.add(bleDevice);
        for (int i = 0; i < bleDeviceList.size(); i++) {
            BleDevice device = bleDeviceList.get(i);
            if (bleDevice.getKey().equals(device.getKey())) {
                return  i;
            }
        }
        return  0;
    }

    public void removeDevice(BleDevice bleDevice) {
        for (int i = 0; i < bleDeviceList.size(); i++) {
            BleDevice device = bleDeviceList.get(i);
            if (bleDevice.getKey().equals(device.getKey())) {
                bleDeviceList.remove(i);
            }
        }
    }

    public void clearConnectedDevice() {
        for (int i = 0; i < bleDeviceList.size(); i++) {
            BleDevice device = bleDeviceList.get(i);
            if (BleUtil.isConnected(context,device)) {
                bleDeviceList.remove(i);
            }
        }
    }

    public void clearScanDevice() {
        for (int i = 0; i < bleDeviceList.size(); i++) {
            BleDevice device = bleDeviceList.get(i);
            if (!BleUtil.isConnected(context,device)) {
                bleDeviceList.remove(i);
            }
        }
    }

    public void clear() {
        clearConnectedDevice();
        clearScanDevice();
    }


    public int getCount() {
        return bleDeviceList.size();
    }


    public BleDevice getItem(int position) {
        if (position > bleDeviceList.size())
            return null;
        return bleDeviceList.get(position);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_ble_dev, parent, false);
        RecyclerView.ViewHolder holder=new BLEDevAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder1, int position) {
        //先进行强转
        BLEDevAdapter.ViewHolder holder = (BLEDevAdapter.ViewHolder) holder1;
        final BleDevice bleDevice = getItem(position);
        if (bleDevice != null) {
            boolean isConnected = BleUtil.isConnected(context,bleDevice);
            String name = bleDevice.getName();
            String mac = bleDevice.getMac();
            int rssi = bleDevice.getRssi();
            holder.txt_name.setText(name);
            holder.txt_mac.setText(mac);
            holder.txt_rssi.setText(String.valueOf(rssi));
            if (isConnected) {
                holder.img_blue.setImageResource(R.mipmap.ic_blue_connected);
                holder.txt_name.setTextColor(0xFF1DE9B6);
                holder.txt_mac.setTextColor(0xFF1DE9B6);
                holder.layout_idle.setVisibility(View.GONE);
                holder.layout_connected.setVisibility(View.VISIBLE);
            } else {
                holder.img_blue.setImageResource(R.mipmap.ic_blue_remote);
                holder.txt_name.setTextColor(0xFF000000);
                holder.txt_mac.setTextColor(0xFF000000);
                holder.layout_idle.setVisibility(View.VISIBLE);
                holder.layout_connected.setVisibility(View.GONE);
            }
        }

        holder.btn_connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onConnect(bleDevice);
                }
            }
        });

        holder.btn_disconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onDisConnect(bleDevice);
                }
            }
        });

        holder.btn_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onDetail(bleDevice);
                }
            }
        });


    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return bleDeviceList.size();
    }



    class ViewHolder extends RecyclerView.ViewHolder{
        ImageView img_blue;
        TextView txt_name;
        TextView txt_mac;
        TextView txt_rssi;
        LinearLayout layout_idle;
        LinearLayout layout_connected;
        Button btn_disconnect;
        Button btn_connect;
        Button btn_detail;

        public ViewHolder(View itemView) {
            super(itemView);
            img_blue = (ImageView) itemView.findViewById(R.id.img_blue);
            txt_name = (TextView) itemView.findViewById(R.id.txt_name);
            txt_mac = (TextView) itemView.findViewById(R.id.txt_mac);
            txt_rssi = (TextView) itemView.findViewById(R.id.txt_rssi);
            layout_idle = (LinearLayout) itemView.findViewById(R.id.layout_idle);
            layout_connected = (LinearLayout) itemView.findViewById(R.id.layout_connected);
            btn_disconnect = (Button) itemView.findViewById(R.id.btn_disconnect);
            btn_connect = (Button) itemView.findViewById(R.id.btn_connect);
            btn_detail = (Button) itemView.findViewById(R.id.btn_detail);
//            mDetCount=(TextView)itemView.findViewById(R.id.Det_count);
//            mIcon=(ImageView)itemView.findViewById(R.id.Detonator_icon);

        }
    }

    public interface OnDeviceClickListener {
        void onConnect(BleDevice bleDevice);

        void onDisConnect(BleDevice bleDevice);

        void onDetail(BleDevice bleDevice);
    }

    private OnDeviceClickListener mListener;

    public void setOnDeviceClickListener(OnDeviceClickListener listener) {
        this.mListener = listener;
    }



}
=======
package com.etek.controller.adapter;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.etek.controller.R;
import com.etek.controller.model.BleDevice;
import com.etek.controller.utils.BleUtil;

import java.util.ArrayList;
import java.util.List;

public class BLEDevAdapter extends   RecyclerView.Adapter<RecyclerView.ViewHolder>  {

    private Context context;
    private List<BleDevice> bleDeviceList;

    public BLEDevAdapter(Context context) {
        this.context = context;
        bleDeviceList = new ArrayList<>();

    }

    public void addDevice(BleDevice bleDevice) {
        removeDevice(bleDevice);
        bleDeviceList.add(bleDevice);
    }

    public int modifyDevice(BleDevice bleDevice) {
        removeDevice(bleDevice);
        bleDeviceList.add(bleDevice);
        for (int i = 0; i < bleDeviceList.size(); i++) {
            BleDevice device = bleDeviceList.get(i);
            if (bleDevice.getKey().equals(device.getKey())) {
                return  i;
            }
        }
        return  0;
    }

    public void removeDevice(BleDevice bleDevice) {
        for (int i = 0; i < bleDeviceList.size(); i++) {
            BleDevice device = bleDeviceList.get(i);
            if (bleDevice.getKey().equals(device.getKey())) {
                bleDeviceList.remove(i);
            }
        }
    }

    public void clearConnectedDevice() {
        for (int i = 0; i < bleDeviceList.size(); i++) {
            BleDevice device = bleDeviceList.get(i);
            if (BleUtil.isConnected(context,device)) {
                bleDeviceList.remove(i);
            }
        }
    }

    public void clearScanDevice() {
        for (int i = 0; i < bleDeviceList.size(); i++) {
            BleDevice device = bleDeviceList.get(i);
            if (!BleUtil.isConnected(context,device)) {
                bleDeviceList.remove(i);
            }
        }
    }

    public void clear() {
        clearConnectedDevice();
        clearScanDevice();
    }


    public int getCount() {
        return bleDeviceList.size();
    }


    public BleDevice getItem(int position) {
        if (position > bleDeviceList.size())
            return null;
        return bleDeviceList.get(position);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_ble_dev, parent, false);
        RecyclerView.ViewHolder holder=new BLEDevAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder1, int position) {
        //先进行强转
        BLEDevAdapter.ViewHolder holder = (BLEDevAdapter.ViewHolder) holder1;
        final BleDevice bleDevice = getItem(position);
        if (bleDevice != null) {
            boolean isConnected = BleUtil.isConnected(context,bleDevice);
            String name = bleDevice.getName();
            String mac = bleDevice.getMac();
            int rssi = bleDevice.getRssi();
            holder.txt_name.setText(name);
            holder.txt_mac.setText(mac);
            holder.txt_rssi.setText(String.valueOf(rssi));
            if (isConnected) {
                holder.img_blue.setImageResource(R.mipmap.ic_blue_connected);
                holder.txt_name.setTextColor(0xFF1DE9B6);
                holder.txt_mac.setTextColor(0xFF1DE9B6);
                holder.layout_idle.setVisibility(View.GONE);
                holder.layout_connected.setVisibility(View.VISIBLE);
            } else {
                holder.img_blue.setImageResource(R.mipmap.ic_blue_remote);
                holder.txt_name.setTextColor(0xFF000000);
                holder.txt_mac.setTextColor(0xFF000000);
                holder.layout_idle.setVisibility(View.VISIBLE);
                holder.layout_connected.setVisibility(View.GONE);
            }
        }

        holder.btn_connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onConnect(bleDevice);
                }
            }
        });

        holder.btn_disconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onDisConnect(bleDevice);
                }
            }
        });

        holder.btn_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onDetail(bleDevice);
                }
            }
        });


    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return bleDeviceList.size();
    }



    class ViewHolder extends RecyclerView.ViewHolder{
        ImageView img_blue;
        TextView txt_name;
        TextView txt_mac;
        TextView txt_rssi;
        LinearLayout layout_idle;
        LinearLayout layout_connected;
        Button btn_disconnect;
        Button btn_connect;
        Button btn_detail;

        public ViewHolder(View itemView) {
            super(itemView);
            img_blue = (ImageView) itemView.findViewById(R.id.img_blue);
            txt_name = (TextView) itemView.findViewById(R.id.txt_name);
            txt_mac = (TextView) itemView.findViewById(R.id.txt_mac);
            txt_rssi = (TextView) itemView.findViewById(R.id.txt_rssi);
            layout_idle = (LinearLayout) itemView.findViewById(R.id.layout_idle);
            layout_connected = (LinearLayout) itemView.findViewById(R.id.layout_connected);
            btn_disconnect = (Button) itemView.findViewById(R.id.btn_disconnect);
            btn_connect = (Button) itemView.findViewById(R.id.btn_connect);
            btn_detail = (Button) itemView.findViewById(R.id.btn_detail);
//            mDetCount=(TextView)itemView.findViewById(R.id.Det_count);
//            mIcon=(ImageView)itemView.findViewById(R.id.Detonator_icon);

        }
    }

    public interface OnDeviceClickListener {
        void onConnect(BleDevice bleDevice);

        void onDisConnect(BleDevice bleDevice);

        void onDetail(BleDevice bleDevice);
    }

    private OnDeviceClickListener mListener;

    public void setOnDeviceClickListener(OnDeviceClickListener listener) {
        this.mListener = listener;
    }



}
>>>>>>> 806c842... 雷管组网
