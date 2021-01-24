package com.etek.controller.activity.project;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.etek.controller.R;
import com.etek.controller.common.Globals;
import com.etek.sommerlibrary.activity.BaseActivity;
import com.github.angads25.toggle.interfaces.OnToggledListener;
import com.github.angads25.toggle.model.ToggleableView;
import com.github.angads25.toggle.widget.LabeledSwitch;

public class SettingsActivity2 extends BaseActivity implements OnToggledListener {

    private LabeledSwitch danningSwitch;
    private LabeledSwitch zhongbaoSwitch;
    private LabeledSwitch etekSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings2);
        initSupportActionBar(R.string.title_activity_settings);
        initView();
        initData();
    }

    private void initData() {
        Boolean isServerDanningOn = getBooleanInfo("isServerDanningOn");
        danningSwitch.setOn(isServerDanningOn);

        Boolean isServerZhongbaoOn = getBooleanInfo("isServerZhongbaoOn");
        zhongbaoSwitch.setOn(isServerZhongbaoOn);

        Boolean isServerEtekOn = getBooleanInfo("isServerEtekOn");
        etekSwitch.setOn(isServerEtekOn);
    }

    private void initView() {
        danningSwitch = findViewById(R.id.danling_switch);
        zhongbaoSwitch = findViewById(R.id.zhongbao_switch);
        etekSwitch = findViewById(R.id.etek_switch);
        danningSwitch.setOnToggledListener(this);
        zhongbaoSwitch.setOnToggledListener(this);
        etekSwitch.setOnToggledListener(this);
    }

    @Override
    public void onSwitched(ToggleableView toggleableView, boolean isOn) {
        switch (toggleableView.getId()) {
            case R.id.danling_switch:
                danningSwitch.setOn(isOn);
                setBooleanInfo("isServerDanningOn", isOn);
                break;
            case R.id.zhongbao_switch:
                zhongbaoSwitch.setOn(isOn);
                setBooleanInfo("isServerZhongbaoOn", isOn);
                break;
            case R.id.etek_switch:
                etekSwitch.setOn(isOn);
                setBooleanInfo("isServerEtekOn", isOn);
                break;
            default:
                break;
        }
    }
}