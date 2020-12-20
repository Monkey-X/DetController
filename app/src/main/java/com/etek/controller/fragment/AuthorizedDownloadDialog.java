package com.etek.controller.fragment;

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
import com.etek.controller.R;
import com.etek.controller.common.Globals;
import com.etek.sommerlibrary.utils.ToastUtils;

public class AuthorizedDownloadDialog extends DialogFragment implements View.OnClickListener {

    private EditText contractCode;
    private EditText authorizedCode;
    private TextView cancel;
    private TextView makeSure;
    private AuthorizedDownloadListener listener;

    @Override
    public void onStart() {
        super.onStart();
        Window win = getDialog().getWindow();
        win.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        getDialog().setCanceledOnTouchOutside(false);
        WindowManager.LayoutParams params = win.getAttributes();
        params.gravity = Gravity.CENTER;
        params.width = (int) (dm.widthPixels * 0.8);
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        win.setAttributes(params);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.authorized_download_dialog, null);
        contractCode = rootView.findViewById(R.id.contract_code);
        authorizedCode = rootView.findViewById(R.id.authorized_code);
        String companyCode = Globals.user.getCompanyCode();
        contractCode.setText(companyCode);
        cancel = rootView.findViewById(R.id.cancel);
        makeSure = rootView.findViewById(R.id.makeSure);
        cancel.setOnClickListener(this);
        makeSure.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancel://取消
                this.dismiss();
                break;

            case R.id.makeSure://确定
                String strContractCode = getString(contractCode);
                if (TextUtils.isEmpty(strContractCode)) {
                    ToastUtils.show(getContext(), "请输入单位编号！");
                    return;
                }

                String strAuthorizedCode = getString(authorizedCode);
                if (TextUtils.isEmpty(strAuthorizedCode) || authorizedCode.length() != 6) {
                    ToastUtils.show(getContext(), "请输入6个字的授权码！");
                    return;
                }

                if (listener != null) {
                    listener.getProjectFileContent(strContractCode,strAuthorizedCode);
                }
                dismiss();
                break;
        }
    }

    /**
     * 获取输入框文本
     */
    public String getString(EditText text) {
        return text.getText().toString().trim();
    }

    /**
     * 接口回调
     */
    public interface AuthorizedDownloadListener {
        void getProjectFileContent(String contractCode,String authorizedCode);
    }

    public void setOnMakeProjectListener(AuthorizedDownloadListener listener) {
        this.listener = listener;
    }
}
