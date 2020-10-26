package com.etek.controller.activity;


import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.etek.controller.R;
import com.etek.controller.adapter.ConnectTestAdapter;
import com.etek.controller.adapter.FiltrateAdapter;
import com.etek.controller.persistence.DBManager;
import com.etek.controller.persistence.entity.DetonatorEntity;
import com.etek.controller.persistence.entity.ProjectInfoEntity;
import com.etek.sommerlibrary.activity.BaseActivity;
import com.etek.sommerlibrary.utils.ToastUtils;
import java.util.ArrayList;
import java.util.List;

/**
 * 连接检测
 */
public class ConnectTestActivity extends BaseActivity implements View.OnClickListener {

    private RelativeLayout noDataView;
    private LinearLayout backImag;
    private TextView textTitle;
    private TextView textBtn;
    private RecyclerView recycleView;
    private ConnectTestAdapter connectTestAdapter;
    private List<String> itemData = new ArrayList<>();
    private List<DetonatorEntity> connectData = new ArrayList<>();
    private List<ProjectInfoEntity> projectInfoEntities;
    private PopupWindow popWindow;
    private RecyclerView rvFiltrate;
    private FiltrateAdapter filtrateAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_test);
        addData(); //模拟添加假数据，后续可去除
        initView();
        initDate();
    }

    private void addData() {
        List<DetonatorEntity> detonatorEntities = DBManager.getInstance().getDetonatorEntityDao().loadAll();
        if (detonatorEntities == null || detonatorEntities.size() == 0) {
            //模拟增加10条数据
            for (int i = 0; i < 10; i++) {
                DetonatorEntity detonatorEntitie = new DetonatorEntity();
                detonatorEntitie.setCode("123456789" + i);
                detonatorEntitie.setHolePosition("1-" + (1 + i));
                detonatorEntitie.setStatus(i % 2);
                DBManager.getInstance().getDetonatorEntityDao().insert(detonatorEntitie);
            }
        }
    }

    /**
     * 页面展示的数据
     */
    private void initDate() {
        projectInfoEntities = DBManager.getInstance().getProjectInfoEntityDao().loadAll();
        List<DetonatorEntity> detonatorEntities = DBManager.getInstance().getDetonatorEntityDao().loadAll();
        if (detonatorEntities != null && detonatorEntities.size() != 0) {
            connectData.addAll(detonatorEntities);
            connectTestAdapter.notifyDataSetChanged();
        } else {
            noDataView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 初始化View
     */
    private void initView() {
        noDataView = findViewById(R.id.no_data_view);
        backImag = findViewById(R.id.back_img);
        backImag.setOnClickListener(this);
        textTitle = findViewById(R.id.text_title);
        textTitle.setText(R.string.title_act_connect_state);
        textBtn = findViewById(R.id.text_btn);
        textBtn.setText(R.string.connect_filtrate);
        textBtn.setOnClickListener(this);
        recycleView = findViewById(R.id.recycleView);
        recycleView.setLayoutManager(new LinearLayoutManager(this));
        connectTestAdapter = new ConnectTestAdapter(R.layout.connect_test_item, connectData);
        recycleView.setAdapter(connectTestAdapter);

        connectTestAdapter.setOnItemLongClickListener(new BaseQuickAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(ConnectTestActivity.this);
                DetonatorEntity detonator = (DetonatorEntity) adapter.getData().get(position);
                dialog.setTitle("是否删除【" + detonator.getCode() + "】该条数据？");
                dialog.setCancelable(false);
                dialog.setPositiveButton("是", (dialog1, which) -> {
                    //集合和本地数据库同时去掉该条数据
                    connectData.remove(position);
                    connectTestAdapter.notifyDataSetChanged();
                    ToastUtils.show(ConnectTestActivity.this, "删除成功");
                });
                dialog.show();
                return false;
            }
        });
    }

    /**
     * 筛选框
     */
    private void showPopWindow() {
        View contentView = LayoutInflater.from(this).inflate(R.layout.filtrate_popup_window, null);
        popWindow = new PopupWindow(contentView, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, true);
        popWindow.setContentView(contentView);
        WindowManager.LayoutParams parms = this.getWindow().getAttributes();
        parms.alpha = 0.5f;
        this.getWindow().setAttributes(parms);
        popWindow.showAsDropDown(textBtn, 0, 25);
        popWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                bgAlpha();
            }
        });
        initFiltrate(contentView);
    }


    /**
     * showPopWindow消失后取消背景色
     */
    private void bgAlpha() {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = (float) 1.0; //0.0-1.0
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        getWindow().setAttributes(lp);
    }

    /**
     * 初始化列表,根据项目名称来进行筛选
     */
    private void initFiltrate(View contentView) {
        rvFiltrate = contentView.findViewById(R.id.rv_filtrate);
        rvFiltrate.setLayoutManager(new LinearLayoutManager(this));
        //动态设置rvFiltrate的高度
        if (projectInfoEntities.size() > 5) {
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, 600);
            rvFiltrate.setLayoutParams(lp);
        }
        //相同的项目名称就不用展示了
        for (int i = 0; i < projectInfoEntities.size(); i++) {
            if (!itemData.contains(projectInfoEntities.get(i).getProName())) {
                itemData.add(projectInfoEntities.get(i).getProName());
            }
        }

        filtrateAdapter = new FiltrateAdapter(R.layout.filtrate_item, itemData);
        rvFiltrate.setAdapter(filtrateAdapter);

        filtrateAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                showFiltrateData(position);
                popWindow.dismiss();
            }
        });
    }


    /**
     * 获取筛选的数据并展示
     */
    private void showFiltrateData(int position) {
        List<DetonatorEntity> detonatorEntities = DBManager.getInstance().getDetonatorEntityDao().loadAll();
        if (detonatorEntities != null && detonatorEntities.size() > 0){
            connectData.clear();
            if (position % 2 == 0){
                for (int i = 0; i < detonatorEntities.size(); i++) {
                    if (detonatorEntities.get(i).getStatus() % 2 == 0){
                        connectData.add(detonatorEntities.get(i));
                    }
                }
            }else{
                for (int i = 0; i < detonatorEntities.size(); i++) {
                    if (detonatorEntities.get(i).getStatus() % 2 != 0){
                        connectData.add(detonatorEntities.get(i));
                    }
                }
            }
            connectTestAdapter.notifyDataSetChanged();
        }else{
            ToastUtils.show(ConnectTestActivity.this,"暂无筛选的数据");
        }
    }

    /**
     * 点击事件
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_img://返回
                finish();
                break;

            case R.id.text_btn://筛选
                if (projectInfoEntities == null || projectInfoEntities.size() == 0) {
                    ToastUtils.show(this, this.getString(R.string.no_filtrate_project));
                } else {
                    showPopWindow();
                }
                break;
        }
    }
}