package com.etek.controller.activity.project;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.etek.controller.R;
import com.etek.controller.activity.project.view.SudokuView;
import com.etek.controller.activity.project.dialog.SudokuDialog;
import com.etek.controller.activity.BaseActivity;
import com.etek.sommerlibrary.utils.ToastUtils;

public class BombPassWordSettingActivity extends BaseActivity implements View.OnClickListener {

    private TextView startPassword;
    private TextView againPassword;
    private TextView savePassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bomb_pass_word_setting);
        initSupportActionBar(R.string.activity_bomb_password_setting);
        initView();
    }

    private void initView() {
        startPassword = findViewById(R.id.start_password);
        againPassword = findViewById(R.id.again_password);
        savePassword = findViewById(R.id.save_password);
        startPassword.setOnClickListener(this);
        againPassword.setOnClickListener(this);
        savePassword.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_password:
                SudokuDialog sudokuDialog = new SudokuDialog();
                sudokuDialog.setSudokuListener(new SudokuView.SudokuListener() {
                    @Override
                    public void onSbSelected(String result) {
                        startPassword.setText(result);
                        sudokuDialog.dismiss();
                    }
                });
                sudokuDialog.show(getSupportFragmentManager(),"");
                break;
            case R.id.again_password:
                SudokuDialog sudokuDialog1 = new SudokuDialog();
                sudokuDialog1.setSudokuListener(new SudokuView.SudokuListener() {
                    @Override
                    public void onSbSelected(String result) {
                        againPassword.setText(result);
                        sudokuDialog1.dismiss();
                    }
                });
                sudokuDialog1.show(getSupportFragmentManager(),"");
                break;
            case R.id.save_password:
                saveBombPassword();
                break;
        }
    }

    private void saveBombPassword() {
        String firstPassword = startPassword.getText().toString();
        String secondPassword = againPassword.getText().toString();
        if (TextUtils.isEmpty(firstPassword)) {
            ToastUtils.show(this,"请设置密码！");
            return;
        }

        if (TextUtils.isEmpty(secondPassword)) {
            ToastUtils.show(this,"请确认密码！");
            return;
        }

        if (!firstPassword.equals(secondPassword)) {
            ToastUtils.show(this,"两次密码不一致！");
            return;
        }

        setStringInfo("BombPassWord",firstPassword);
        ToastUtils.show(this,"设置成功！");
    }
}