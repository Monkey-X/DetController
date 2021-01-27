package com.etek.controller.fragment;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.etek.controller.R;
import com.etek.controller.entity.FastEditBean;
import com.etek.sommerlibrary.utils.ToastUtils;

public class FastEditDialog extends DialogFragment implements View.OnClickListener {

    private int number = -1;
    private EditText startNum;
    private EditText endNum;
    private EditText holeNum;
    private EditText startTime;
    private EditText holeOutTime;
    private EditText holeInTime;
    private OnMakeSureListener listener;

    @Override
    public void onStart() {
        super.onStart();
        Window win = getDialog().getWindow();
        // 一定要设置Background，如果不设置，window属性设置无效
        getDialog().setCanceledOnTouchOutside(false);
        win.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);

        WindowManager.LayoutParams params = win.getAttributes();
        params.gravity = Gravity.CENTER;
        // 使用ViewGroup.LayoutParams，以便Dialog 宽度充满整个屏幕
        params.width = (int) (dm.widthPixels * 0.8);
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        win.setAttributes(params);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View dialogView = inflater.inflate(R.layout.dialog_fast_edit, container, false);
        startNum = dialogView.findViewById(R.id.startNum);
        endNum = dialogView.findViewById(R.id.endNum);
        startTime = dialogView.findViewById(R.id.startTime);
        holeNum = dialogView.findViewById(R.id.holeNum);
        holeOutTime = dialogView.findViewById(R.id.holeOutTime);
        holeInTime = dialogView.findViewById(R.id.holeInTime);
        TextView cancel = dialogView.findViewById(R.id.cancel);
        TextView makeSure = dialogView.findViewById(R.id.makeSure);
        cancel.setOnClickListener(this);
        makeSure.setOnClickListener(this);
        return dialogView;
    }

    public void setSerialNumber(int number) {
        this.number = number;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancel:
                this.dismiss();
                break;
            case R.id.makeSure:
                // 点击确定，进行批量的修改
                batchEditDatas();
                break;
        }
    }

    private void batchEditDatas() {
        String startNumString = getNumString(startNum);
        if (TextUtils.isEmpty(startNumString)) {
            ToastUtils.show(getContext(), "请输入起始序号！");
            return;
        }
        String endNumString = getNumString(endNum);
        if (TextUtils.isEmpty(endNumString)) {
            ToastUtils.show(getContext(), "请输入截止序号！");
            return;
        }
        String startTimeString = getNumString(startTime);
        if (TextUtils.isEmpty(startTimeString)) {
            ToastUtils.show(getContext(), "请输入起始时间！");
            return;
        }

        int startTime = Integer.parseInt(startTimeString);
        if (startTime >= 15000) {
            ToastUtils.show(getContext(), "延时请设置在0ms---15000ms范围内！");
            return;
        }

        String holeNumString = getNumString(holeNum);
        if (TextUtils.isEmpty(holeNumString)) {
            ToastUtils.show(getContext(), "请输入每孔雷管数！");
            return;
        }
        String holeOutTimeString = getNumString(holeOutTime);
        if (TextUtils.isEmpty(holeOutTimeString)) {
            ToastUtils.show(getContext(), "请输入孔间延时！");
            return;
        }
        String holeInTimeString = getNumString(holeInTime);
        if (TextUtils.isEmpty(holeInTimeString)) {
            ToastUtils.show(getContext(), "请输入孔内延时！");
            return;
        }

        if (getNumFormString(startNumString) == 0 ||
                getNumFormString(startNumString) > number || getNumFormString(endNumString) > number
                || getNumFormString(startNumString) >= getNumFormString(endNumString)) {
            ToastUtils.show(getContext(), "请输入正确的序号！");
            return;
        }

        FastEditBean fastEditBean = new FastEditBean();
        fastEditBean.setStartNum(getNumFormString(startNumString));
        fastEditBean.setEndNum(getNumFormString(endNumString));
        fastEditBean.setStartTime(getNumFormString(startTimeString));
        fastEditBean.setHoleNum(getNumFormString(holeNumString));
        fastEditBean.setHoleOutTime(getNumFormString(holeOutTimeString));
        fastEditBean.setHoleInTime(getNumFormString(holeInTimeString));
        if (listener != null) {
            listener.makeSure(fastEditBean);
        }
        this.dismiss();
    }

    public interface OnMakeSureListener {
        void makeSure(FastEditBean bean);
    }

    public void setOnMakeSureListener(OnMakeSureListener listener) {
        this.listener = listener;
    }


    public String getNumString(EditText text) {
        return text.getText().toString().trim();
    }

    public int getNumFormString(String numString) {
        return Integer.parseInt(numString);
    }
}
