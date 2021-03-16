package com.etek.controller.activity.project.dialog;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.etek.controller.R;
import com.etek.controller.activity.project.comment.AppSpSaveConstant;
import com.etek.controller.activity.project.manager.SpManager;

public class ReprotDialog extends DialogFragment implements View.OnClickListener {

    private TextView danlingReport;
    private TextView danlingReturn;
    private TextView zhongbaoReprot;
    private TextView zhongbaoReturn;
    private String danlingReturnString;
    private String zhongbaoReturnString;

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

    public void setReturnString(String danlingReturnString,String zhongbaoReturnString){
        this.danlingReturnString = danlingReturnString;
        this.zhongbaoReturnString = zhongbaoReturnString;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.dialog_report, null);
        danlingReport = rootView.findViewById(R.id.danling_report);
        danlingReturn = rootView.findViewById(R.id.danling_return);
        zhongbaoReprot = rootView.findViewById(R.id.zhongbao_reprot);
        zhongbaoReturn = rootView.findViewById(R.id.zhongbao_return);
        TextView makeSure = rootView.findViewById(R.id.make_sure);
        makeSure.setOnClickListener(this);
        setData();
        return rootView;
    }

    private void setData() {
        boolean zhongBaoOn = SpManager.getIntance().getSpBoolean(AppSpSaveConstant.SEVER_ZHONGBAO_ON);
        boolean danlingOn = SpManager.getIntance().getSpBoolean(AppSpSaveConstant.SEVER_DANNING_ON);
        danlingReport.setText(getStatusString(danlingOn));
        zhongbaoReprot.setText(getStatusString(zhongBaoOn));

        zhongbaoReturn.setText(zhongbaoReturnString);
        danlingReturn.setText(danlingReturnString);
    }

    private String getStatusString(boolean status) {
        if (status) {
            return "开";
        }else{
            return "关";
        }
    }

    @Override
    public void onClick(View v) {
        dismiss();
    }
}
