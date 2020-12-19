package com.etek.controller.fragment;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.etek.controller.R;
import com.etek.controller.common.AppIntentString;
import com.etek.controller.entity.DetDelayBean;
import com.etek.sommerlibrary.utils.ToastUtils;

import static android.content.Context.MODE_PRIVATE;

/**
 * 设置延时的dialog
 */
public class DelaySettingDialog extends DialogFragment implements View.OnClickListener {


    private EditText startTime;
    private EditText holeInTime;
    private EditText holeOutTime;
    private OnDelaySettingListener listener;

    @Override
    public void onStart() {
        super.onStart();
        Window win = getDialog().getWindow();
        // 一定要设置Background，如果不设置，window属性设置无效
        win.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        getDialog().setCanceledOnTouchOutside(false);
        setCancelable(false);
        WindowManager.LayoutParams params = win.getAttributes();
        params.gravity = Gravity.CENTER;
        // 使用ViewGroup.LayoutParams，以便Dialog 宽度充满整个屏幕
        params.width = (int) (dm.widthPixels * 0.9);
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        win.setAttributes(params);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dialog_delay_setting, null);
        startTime = rootView.findViewById(R.id.start_time);
        holeInTime = rootView.findViewById(R.id.hole_in_time);
        holeOutTime = rootView.findViewById(R.id.hole_out_time);

        String delaySetting = getDelaySetting();
        if (!TextUtils.isEmpty(delaySetting)) {
            DetDelayBean detDelayBean = JSON.parseObject(delaySetting, DetDelayBean.class);
            if (detDelayBean!=null) {
                startTime.setText(detDelayBean.getStartTime()+"");
                holeInTime.setText(detDelayBean.getHoleInTime()+"");
                holeOutTime.setText(detDelayBean.getHoleOutTime()+"");
            }
        }

        TextView cancel = rootView.findViewById(R.id.cancel);
        TextView makeSure = rootView.findViewById(R.id.makeSure);
        cancel.setOnClickListener(this);
        makeSure.setOnClickListener(this);
        return rootView;
    }

    private String getDelaySetting(){
        SharedPreferences preferences = getContext().getSharedPreferences("detInfo", MODE_PRIVATE);
        return preferences.getString(AppIntentString.DELAY_SETTING,"");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancel:
                this.dismiss();
                if (listener != null) {
                    listener.makeCancel();
                }
                break;
            case R.id.makeSure:
                // 点击确定，进行批量的修改
                setDelayTime();
                break;
        }
    }

    // 设置延时
    private void setDelayTime() {
        String strStartTime = getString(startTime);
        if (TextUtils.isEmpty(strStartTime)) {
            ToastUtils.show(getContext(), "请输入起始时间！");
            return;

        }
        String strHoleInTime = getString(holeInTime);
        if (TextUtils.isEmpty(strHoleInTime)) {
            ToastUtils.show(getContext(), "请输入孔内延时！");
            return;

        }
        String strHoleOutTime = getString(holeOutTime);
        if (TextUtils.isEmpty(strHoleOutTime)) {
            ToastUtils.show(getContext(), "请输入孔间延时！");
            return;
        }

        DetDelayBean detDelayBean = new DetDelayBean();
        detDelayBean.setStartTime(Integer.parseInt(strStartTime));
        detDelayBean.setHoleInTime(Integer.parseInt(strHoleInTime));
        detDelayBean.setHoleOutTime(Integer.parseInt(strHoleOutTime));
        if (listener!=null) {
            listener.setDelayTime(detDelayBean);
        }
        dismiss();
    }


    public interface OnDelaySettingListener {
        void setDelayTime(DetDelayBean bean);
        void makeCancel();
    }

    public void setOnDelaySettingListener(OnDelaySettingListener listener) {
        this.listener = listener;
    }

    public String getString(EditText text) {
        return text.getText().toString().trim();
    }


}
