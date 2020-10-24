<<<<<<< HEAD
package com.etek.controller.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.etek.controller.R;

import com.polidea.rxandroidble2.RxBleConnection;
import com.polidea.rxandroidble2.RxBleDevice;
import com.polidea.rxandroidble2.scan.ScanResult;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ScanResultsAdapter extends RecyclerView.Adapter<ScanResultsAdapter.ViewHolder> {

    private final static String TAG = "ScanResultsAdapter";
    private Context context;
//    private List<BleDevice> bleDeviceList;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.img_blue)
        ImageView imgBlue;
        @BindView(R.id.txt_name)
        TextView txt_name;
        @BindView(R.id.txt_mac)
        TextView txt_mac;
        @BindView(R.id.txt_rssi)
        TextView txt_rssi;
        @BindView(R.id.layout_idle)
        LinearLayout layout_idle;
        @BindView(R.id.layout_connected)
        LinearLayout layout_connected;

        @BindView(R.id.btn_connect)
        Button btn_connect;

        @BindView(R.id.btn_disconnect)
        Button btn_disconnect;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnAdapterItemClickListener {

        void onAdapterViewClick(View view);
    }

    private static final Comparator<ScanResult> SORTING_COMPARATOR = (lhs, rhs) ->
            lhs.getBleDevice().getMacAddress().compareTo(rhs.getBleDevice().getMacAddress());
    private final List<ScanResult> data = new ArrayList<>();
    public OnAdapterItemClickListener onAdapterItemClickListener;
    public final View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if (onAdapterItemClickListener != null) {
                onAdapterItemClickListener.onAdapterViewClick(v);
            }
        }
    };


    public interface OnDeviceClickListener {
        void onConnect(ScanResult bleDevice);

        void onDisConnect(ScanResult bleDevice);


    }

    private ScanResultsAdapter.OnDeviceClickListener mListener;

    public void setOnDeviceClickListener(ScanResultsAdapter.OnDeviceClickListener listener) {
        this.mListener = listener;
    }

    public void addScanResult(ScanResult bleScanResult) {
        // Not the best way to ensure distinct devices, just for sake on the demo.

        if (StringUtils.isBlank(bleScanResult.getBleDevice().getName())) {
            return;
        }

        String name = bleScanResult.getBleDevice().getName();
        if(name.contentEquals(" ")){
            return;
        }
        String[] names = name.split("-");
        if (names.length < 2) {
            return;
        }

//        XLog.d(TAG, bleScanResult.toString() + StringUtils.isNumeric(names[1].trim()));
        if(!StringUtils.isNumeric(names[1].trim())){
            return;
        }
//        XLog.d(TAG,names[0]+"   "+names[1]);
//        if(!StringUtil.isNumeric(names[1])){
//            return;
//        }

        for (int i = 0; i < data.size(); i++) {

            if (data.get(i).getBleDevice().equals(bleScanResult.getBleDevice())) {
                data.set(i, bleScanResult);
                notifyItemChanged(i);
                return;
            }
        }

        data.add(bleScanResult);
//        Collections.sort(data, SORTING_COMPARATOR);
        notifyDataSetChanged();
    }

    public void clearScanResults() {
        data.clear();
        notifyDataSetChanged();
    }

    public ScanResult getItemAtPosition(int childAdapterPosition) {
        return data.get(childAdapterPosition);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final ScanResult rxBleScanResult = data.get(position);
        final RxBleDevice bleDevice = rxBleScanResult.getBleDevice();

//        holder.line1.setText(String.format(Locale.getDefault(), " ()", bleDevice.getMacAddress(), bleDevice.getName()));
//        holder.line2.setText(String.format(Locale.getDefault(), "RSSI: %d", rxBleScanResult.getRssi()));
        //先进行强转

//        final BleDevice bleDevice = getItem(position);
        if (bleDevice != null) {
//            if(bleDevice.getConnectionState().){
//
//            }
            int connect = 2;

            if (bleDevice.getConnectionState() == RxBleConnection.RxBleConnectionState.CONNECTED) {
                connect = 0;
            } else if (bleDevice.getConnectionState() == RxBleConnection.RxBleConnectionState.CONNECTING) {
                connect = 1;
            } else if (bleDevice.getConnectionState() == RxBleConnection.RxBleConnectionState.DISCONNECTED) {
                connect = 2;
            } else if (bleDevice.getConnectionState() == RxBleConnection.RxBleConnectionState.DISCONNECTING) {
                connect = 3;
            }


//            BleUtil.isConnected(context,bleDevice);
            String name = bleDevice.getName();
            String mac = bleDevice.getMacAddress();
            int rssi = rxBleScanResult.getRssi();
            holder.txt_name.setText(name);
            holder.txt_mac.setText(mac);
            holder.txt_rssi.setText(String.valueOf(rssi));
            if (connect == 0) {
                holder.imgBlue.setImageResource(R.mipmap.ic_blue_connected);
                holder.txt_name.setTextColor(0xFF1DE9B6);
                holder.txt_mac.setTextColor(0xFF1DE9B6);
                holder.layout_idle.setVisibility(View.GONE);
                holder.layout_connected.setVisibility(View.VISIBLE);
            } else {
                holder.imgBlue.setImageResource(R.mipmap.ic_blue_remote);
                holder.txt_name.setTextColor(0xFF000000);
                holder.txt_mac.setTextColor(0xFF000000);
                holder.layout_idle.setVisibility(View.VISIBLE);
                holder.layout_connected.setVisibility(View.GONE);
            }
        }

        holder.btn_connect.setOnClickListener(view -> {
            if (mListener != null) {
                mListener.onConnect(rxBleScanResult);
            }
        });

        holder.btn_disconnect.setOnClickListener(view -> {
            if (mListener != null) {
                mListener.onDisConnect(rxBleScanResult);
            }
        });

    }


    public ScanResultsAdapter(Context context) {
        this.context = context;
//        data = new ArrayList<>();

    }

    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_ble_dev, parent, false);
//        RecyclerView.ViewHolder holder=new ScanResultsAdapter.ViewHolder(view);
        return new ViewHolder(view);
//        return holder;
//        final View itemView = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.two_line_list_item, parent, false);
//        itemView.setOnClickListener(onClickListener);
//        return new ViewHolder(itemView);
    }

    public void setOnAdapterItemClickListener(OnAdapterItemClickListener onAdapterItemClickListener) {
        this.onAdapterItemClickListener = onAdapterItemClickListener;
    }
}
=======
package com.etek.controller.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.etek.controller.R;

import com.polidea.rxandroidble2.RxBleConnection;
import com.polidea.rxandroidble2.RxBleDevice;
import com.polidea.rxandroidble2.scan.ScanResult;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ScanResultsAdapter extends RecyclerView.Adapter<ScanResultsAdapter.ViewHolder> {

    private final static String TAG = "ScanResultsAdapter";
    private Context context;
//    private List<BleDevice> bleDeviceList;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.img_blue)
        ImageView imgBlue;
        @BindView(R.id.txt_name)
        TextView txt_name;
        @BindView(R.id.txt_mac)
        TextView txt_mac;
        @BindView(R.id.txt_rssi)
        TextView txt_rssi;
        @BindView(R.id.layout_idle)
        LinearLayout layout_idle;
        @BindView(R.id.layout_connected)
        LinearLayout layout_connected;

        @BindView(R.id.btn_connect)
        Button btn_connect;

        @BindView(R.id.btn_disconnect)
        Button btn_disconnect;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnAdapterItemClickListener {

        void onAdapterViewClick(View view);
    }

    private static final Comparator<ScanResult> SORTING_COMPARATOR = (lhs, rhs) ->
            lhs.getBleDevice().getMacAddress().compareTo(rhs.getBleDevice().getMacAddress());
    private final List<ScanResult> data = new ArrayList<>();
    public OnAdapterItemClickListener onAdapterItemClickListener;
    public final View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if (onAdapterItemClickListener != null) {
                onAdapterItemClickListener.onAdapterViewClick(v);
            }
        }
    };


    public interface OnDeviceClickListener {
        void onConnect(ScanResult bleDevice);

        void onDisConnect(ScanResult bleDevice);


    }

    private ScanResultsAdapter.OnDeviceClickListener mListener;

    public void setOnDeviceClickListener(ScanResultsAdapter.OnDeviceClickListener listener) {
        this.mListener = listener;
    }

    public void addScanResult(ScanResult bleScanResult) {
        // Not the best way to ensure distinct devices, just for sake on the demo.

        if (StringUtils.isBlank(bleScanResult.getBleDevice().getName())) {
            return;
        }

        String name = bleScanResult.getBleDevice().getName();
        if(name.contentEquals(" ")){
            return;
        }
        String[] names = name.split("-");
        if (names.length < 2) {
            return;
        }

//        XLog.d(TAG, bleScanResult.toString() + StringUtils.isNumeric(names[1].trim()));
        if(!StringUtils.isNumeric(names[1].trim())){
            return;
        }
//        XLog.d(TAG,names[0]+"   "+names[1]);
//        if(!StringUtil.isNumeric(names[1])){
//            return;
//        }

        for (int i = 0; i < data.size(); i++) {

            if (data.get(i).getBleDevice().equals(bleScanResult.getBleDevice())) {
                data.set(i, bleScanResult);
                notifyItemChanged(i);
                return;
            }
        }

        data.add(bleScanResult);
//        Collections.sort(data, SORTING_COMPARATOR);
        notifyDataSetChanged();
    }

    public void clearScanResults() {
        data.clear();
        notifyDataSetChanged();
    }

    public ScanResult getItemAtPosition(int childAdapterPosition) {
        return data.get(childAdapterPosition);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final ScanResult rxBleScanResult = data.get(position);
        final RxBleDevice bleDevice = rxBleScanResult.getBleDevice();

//        holder.line1.setText(String.format(Locale.getDefault(), " ()", bleDevice.getMacAddress(), bleDevice.getName()));
//        holder.line2.setText(String.format(Locale.getDefault(), "RSSI: %d", rxBleScanResult.getRssi()));
        //先进行强转

//        final BleDevice bleDevice = getItem(position);
        if (bleDevice != null) {
//            if(bleDevice.getConnectionState().){
//
//            }
            int connect = 2;

            if (bleDevice.getConnectionState() == RxBleConnection.RxBleConnectionState.CONNECTED) {
                connect = 0;
            } else if (bleDevice.getConnectionState() == RxBleConnection.RxBleConnectionState.CONNECTING) {
                connect = 1;
            } else if (bleDevice.getConnectionState() == RxBleConnection.RxBleConnectionState.DISCONNECTED) {
                connect = 2;
            } else if (bleDevice.getConnectionState() == RxBleConnection.RxBleConnectionState.DISCONNECTING) {
                connect = 3;
            }


//            BleUtil.isConnected(context,bleDevice);
            String name = bleDevice.getName();
            String mac = bleDevice.getMacAddress();
            int rssi = rxBleScanResult.getRssi();
            holder.txt_name.setText(name);
            holder.txt_mac.setText(mac);
            holder.txt_rssi.setText(String.valueOf(rssi));
            if (connect == 0) {
                holder.imgBlue.setImageResource(R.mipmap.ic_blue_connected);
                holder.txt_name.setTextColor(0xFF1DE9B6);
                holder.txt_mac.setTextColor(0xFF1DE9B6);
                holder.layout_idle.setVisibility(View.GONE);
                holder.layout_connected.setVisibility(View.VISIBLE);
            } else {
                holder.imgBlue.setImageResource(R.mipmap.ic_blue_remote);
                holder.txt_name.setTextColor(0xFF000000);
                holder.txt_mac.setTextColor(0xFF000000);
                holder.layout_idle.setVisibility(View.VISIBLE);
                holder.layout_connected.setVisibility(View.GONE);
            }
        }

        holder.btn_connect.setOnClickListener(view -> {
            if (mListener != null) {
                mListener.onConnect(rxBleScanResult);
            }
        });

        holder.btn_disconnect.setOnClickListener(view -> {
            if (mListener != null) {
                mListener.onDisConnect(rxBleScanResult);
            }
        });

    }


    public ScanResultsAdapter(Context context) {
        this.context = context;
//        data = new ArrayList<>();

    }

    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_ble_dev, parent, false);
//        RecyclerView.ViewHolder holder=new ScanResultsAdapter.ViewHolder(view);
        return new ViewHolder(view);
//        return holder;
//        final View itemView = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.two_line_list_item, parent, false);
//        itemView.setOnClickListener(onClickListener);
//        return new ViewHolder(itemView);
    }

    public void setOnAdapterItemClickListener(OnAdapterItemClickListener onAdapterItemClickListener) {
        this.onAdapterItemClickListener = onAdapterItemClickListener;
    }
}
>>>>>>> 806c842... 雷管组网
