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
import com.etek.controller.entity.FastEditBean;
import com.etek.controller.model.User;
import com.etek.controller.persistence.entity.ProjectInfoEntity;
import com.etek.sommerlibrary.utils.ToastUtils;

import butterknife.ButterKnife;

/**
 * 创建项目的dialog
 */
public class ProjectDialog extends DialogFragment implements View.OnClickListener {


    private EditText proName;
    private EditText proId;
    private EditText companyName;
    private EditText companyId;
    private EditText contractId;
    private EditText contractName;
    private OnMakeProjectListener listener;

    @Override
    public void onStart() {
        super.onStart();
        Window win = getDialog().getWindow();
        // 一定要设置Background，如果不设置，window属性设置无效
        win.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        getDialog().setCanceledOnTouchOutside(false);
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

        View rootView = inflater.inflate(R.layout.dialog_make_project, null);
        proName = rootView.findViewById(R.id.pro_name);
        proId = rootView.findViewById(R.id.pro_id);
        companyName = rootView.findViewById(R.id.company_name);
        companyId = rootView.findViewById(R.id.company_id);
        contractId = rootView.findViewById(R.id.contract_id);
        contractName = rootView.findViewById(R.id.contract_name);

        getUserInfo();

        TextView cancel = rootView.findViewById(R.id.cancel);
        TextView makeSure = rootView.findViewById(R.id.makeSure);
        cancel.setOnClickListener(this);
        makeSure.setOnClickListener(this);
        return rootView;
    }

    private void getUserInfo() {
        SharedPreferences detInfo = getContext().getSharedPreferences("detInfo", getContext().MODE_PRIVATE);
        String userInfo = detInfo.getString("userInfo", "");
        if (!TextUtils.isEmpty(userInfo)) {
            User user = JSON.parseObject(userInfo, User.class);
            if (user != null) {
                companyName.setText(user.getCompanyName());
                companyId.setText(user.getCompanyCode());
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancel:
                this.dismiss();
                break;
            case R.id.makeSure:
                // 点击确定，进行批量的修改
                makeProject();
                break;
        }
    }

    private void makeProject() {

        String strProName = getString(proName);
        if (TextUtils.isEmpty(strProName)) {
            ToastUtils.show(getContext(), "请输入项目名称！");
            return;

        }
        String strProId = getString(proId);
        if (TextUtils.isEmpty(strProId)) {
            ToastUtils.show(getContext(), "请输入项目编号！");
            return;

        }
        String strCompanyName = getString(contractName);
        if (TextUtils.isEmpty(strCompanyName)) {
            ToastUtils.show(getContext(), "请输入合同名称！");
            return;

        }
        String strCompanyId = getString(contractId);
        if (TextUtils.isEmpty(strCompanyId)) {
            ToastUtils.show(getContext(), "请输入合同代码！");
            return;
        }

        ProjectInfoEntity projectInfoEntity = new ProjectInfoEntity();
        projectInfoEntity.setProName(strProName);
        projectInfoEntity.setProCode(strProId);
        projectInfoEntity.setCompanyName(strCompanyName);
        projectInfoEntity.setCompanyCode(strCompanyId);
        projectInfoEntity.setContractName(getString(contractName));
        projectInfoEntity.setContractCode(getString(contractId));

        if (listener != null) {
            listener.makeProject(projectInfoEntity);
        }
        dismiss();
    }

    public interface OnMakeProjectListener {
        void makeProject(ProjectInfoEntity bean);
    }

    public void setOnMakeProjectListener(OnMakeProjectListener listener) {
        this.listener = listener;
    }

    public String getString(EditText text) {
        return text.getText().toString().trim();
    }


}
