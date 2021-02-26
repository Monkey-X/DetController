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

import com.etek.controller.R;
import com.etek.controller.activity.project.view.SudokuView;

public class SudokuDialog extends DialogFragment implements View.OnClickListener {

    private SudokuView.SudokuListener listener;
    private SudoCancelListenr sudoCancelListener;

    @Override
    public void onStart() {
        super.onStart();
        Window win = getDialog().getWindow();
        // 一定要设置Background，如果不设置，window属性设置无效
        getDialog().setCanceledOnTouchOutside(false);
        getDialog().setCancelable(false);
        win.setBackgroundDrawable(new ColorDrawable(Color.WHITE));

        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);

        WindowManager.LayoutParams params = win.getAttributes();
        params.gravity = Gravity.BOTTOM;

        // 使用ViewGroup.LayoutParams，以便Dialog 宽度充满整个屏幕
        params.width = (int) (dm.widthPixels );
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        win.setAttributes(params);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.dialog_sudoku, container, false);

        SudokuView sudokuView = view.findViewById(R.id.sudokuView);

        View cancelBomb = view.findViewById(R.id.cancel_bomb);
        if (listener != null) {
            sudokuView.setSudokuListener(listener);
        }

        cancelBomb.setOnClickListener(this);
        return view;
    }

    public void setSudokuListener(SudokuView.SudokuListener listener) {
        this.listener = listener;
    }

    public interface SudoCancelListenr {
        void onSudoCancel();
    }

    public void setSudoCancelListener(SudoCancelListenr listener) {
        this.sudoCancelListener = listener;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancel_bomb:
                this.dismiss();
                if (sudoCancelListener != null) {
                    sudoCancelListener.onSudoCancel();
                }
                break;
        }

    }
}
