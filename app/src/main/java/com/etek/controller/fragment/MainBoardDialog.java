package com.etek.controller.fragment;

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
import com.etek.controller.entity.MainBoardInfoBean;

public class MainBoardDialog extends DialogFragment {

    private MainBoardInfoBean mainBoardInfoBean;

    @Override
    public void onStart() {
        super.onStart();
        Window win = getDialog().getWindow();
        // 一定要设置Background，如果不设置，window属性设置无效
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

    public void setMainBoardInfo(MainBoardInfoBean mainBoardInfo){
                this.mainBoardInfoBean = mainBoardInfo;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View dialogView = inflater.inflate(R.layout.dialog_main_board, container, false);
        TextView hardver = dialogView.findViewById(R.id.hardVer);
        TextView updateHardwareVer = dialogView.findViewById(R.id.updateHardwareVer);
        TextView softwareVer = dialogView.findViewById(R.id.softwareVer);
        TextView sno = dialogView.findViewById(R.id.sno);
        TextView config = dialogView.findViewById(R.id.config);

        hardver.setText("v"+mainBoardInfoBean.getStrHardwareVer());
        updateHardwareVer.setText("v"+mainBoardInfoBean.getStrUpdateHardwareVer());
        softwareVer.setText("v"+mainBoardInfoBean.getStrSoftwareVer());
        sno.setText(mainBoardInfoBean.getStrSNO());
        config.setText(mainBoardInfoBean.getStrConfig());
        return dialogView;
    }
}
