package com.etek.controller.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.etek.controller.R;
import com.etek.controller.adapter.DetReportAdapter2;
import com.etek.controller.entity.DetController;
import com.etek.controller.persistence.DBManager;
import com.etek.controller.persistence.entity.ReportEntity;
import com.etek.controller.persistence.gen.ReportEntityDao;
import com.etek.controller.utils.JsonUtils;
import com.etek.controller.widget.DefineLoadMoreView;
import com.etek.sommerlibrary.activity.BaseActivity;
import com.yanzhenjie.recyclerview.OnItemClickListener;
import com.yanzhenjie.recyclerview.OnItemMenuClickListener;
import com.yanzhenjie.recyclerview.SwipeMenuBridge;
import com.yanzhenjie.recyclerview.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.SwipeMenuItem;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;
import com.yanzhenjie.recyclerview.widget.DefaultItemDecoration;
import java.util.ArrayList;
import java.util.List;
import butterknife.ButterKnife;

public class ReportActivity2 extends BaseActivity {

    private ImageView img_loading;
    private SwipeRecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private DetReportAdapter2 mAdapter;
    private int nPage = 0;
    private static final int PAGE_SIZE = 10;
    private List<DetController> rptCtlList;
    private DetController cDetController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_det_report2);
        initSupportActionBar(R.string.title_activity_report);
        ButterKnife.bind(this);
        initData();
        initView();
    }

    private void initData() {
//        JsonUtils.monitReportEntity();//模拟数据
        cDetController = new DetController();
        rptCtlList = new ArrayList<>();
        nPage = 0;
    }

    private void initView() {
        img_loading = findViewById(R.id.img_loading);
        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new DefaultItemDecoration(ContextCompat.getColor(this, R.color.divider_color)));
        mRecyclerView.setOnItemClickListener(mItemClickListener);

        // 自定义的核心就是DefineLoadMoreView类。
        DefineLoadMoreView loadMoreView = new DefineLoadMoreView(this);
        mRecyclerView.addFooterView(loadMoreView); // 添加为Footer。
        mRecyclerView.setLoadMoreView(loadMoreView); // 设置LoadMoreView更新监听。
        mRecyclerView.setLoadMoreListener(mLoadMoreListener); // 加载更多的监听。
        mRecyclerView.setSwipeMenuCreator(swipeMenuCreator);
        mRecyclerView.setOnItemMenuClickListener(mMenuItemClickListener);

        mSwipeRefreshLayout = findViewById(R.id.refresh_layout);
        mSwipeRefreshLayout.setColorSchemeResources(
                com.etek.sommerlibrary.R.color.colorAccent,
                com.etek.sommerlibrary.R.color.activated,
                com.etek.sommerlibrary.R.color.colorPrimary,
                com.etek.sommerlibrary.R.color.colorPrimaryDark);

        mAdapter = new DetReportAdapter2(mContext, rptCtlList);
        mRecyclerView.setAdapter(mAdapter);
        mSwipeRefreshLayout.setOnRefreshListener(() -> mSwipeRefreshLayout.setRefreshing(false));
    }

    /**
     * 加载更多。
     */
    private SwipeRecyclerView.LoadMoreListener mLoadMoreListener = new SwipeRecyclerView.LoadMoreListener() {
        @Override
        public void onLoadMore() {
            mRecyclerView.postDelayed(new Runnable() {
                @Override
                public void run() {
//                    XLog.v("mLoadMoreListener");
                    refresh(nPage);
                    mRecyclerView.loadMoreFinish(false, true);
                }
            }, 1000);
        }
    };

    /**
     * Item点击监听。
     */
    private OnItemClickListener mItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(View itemView, int position) {
            //详情页逻辑变化，直接跳转会报错(该列表页后续不需要的)
//            cDetController = rptCtlList.get(position);
//            Intent intent = new Intent(mContext, ReportDetailActivity2.class);
//            intent.putExtra("DetController", cDetController);
//            startActivity(intent);
        }
    };

    /**
     * 菜单创建器，在Item要创建菜单的时候调用。
     */
    private SwipeMenuCreator swipeMenuCreator = (swipeLeftMenu, swipeRightMenu, position) -> {
        int width = getResources().getDimensionPixelSize(R.dimen.dp_72);
        int height = ViewGroup.LayoutParams.MATCH_PARENT;
        // 添加右侧的，如果不添加，则右侧不会出现菜单。
        SwipeMenuItem deleteItem = new SwipeMenuItem(mContext).setBackground(R.drawable.selector_red)
                .setImage(R.mipmap.delete)
                .setText("删除")
                .setTextColor(Color.WHITE)
                .setWidth(width)
                .setHeight(height);
        swipeRightMenu.addMenuItem(deleteItem);// 添加菜单到右侧。
    };

    /**
     * RecyclerView的Item的Menu点击监听。
     */
    private OnItemMenuClickListener mMenuItemClickListener = new OnItemMenuClickListener() {
        @Override
        public void onItemClick(SwipeMenuBridge menuBridge, int position) {
            menuBridge.closeMenu();
            int direction = menuBridge.getDirection(); // 左侧还是右侧菜单。
            int menuPosition = menuBridge.getPosition(); // 菜单在RecyclerView的Item中的Position。
            if (direction == SwipeRecyclerView.RIGHT_DIRECTION) {
                if (menuPosition == 0) {
                    showRemoveDialog(position);
                }
            }
        }
    };

    /**
     * 删除数据
     */
    private void showRemoveDialog(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("是否删除此数据！");
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ReportEntity reportEntity = rptCtlList.get(position).getReportEntity();
                reportEntity.setId(rptCtlList.get(position).getId());
                DBManager.getInstance().getReportEntityDao().delete(reportEntity);
                rptCtlList.remove(position);
                mAdapter.notifyItemRemoved(position);
            }
        });
        builder.setNegativeButton("取消", null);
        builder.setCancelable(true); //设置按钮是否可以按返回键取消,false则不可以取消
        AlertDialog dialog = builder.create(); //创建对话框
        dialog.setCanceledOnTouchOutside(true); //设置弹出框失去焦点是否隐藏,即点击屏蔽其它地方是否隐藏
        dialog.show();
    }

    /**
     *显示相同雷管对话框
     */
    private void showSameDetDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("此次雷管传输已经存在！");
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setPositiveButton("确认", null);
        builder.setCancelable(true); //设置按钮是否可以按返回键取消,false则不可以取消
        AlertDialog dialog = builder.create(); //创建对话框
        dialog.setCanceledOnTouchOutside(true); //设置弹出框失去焦点是否隐藏,即点击屏蔽其它地方是否隐藏
        dialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshInit();
    }

    /**
     * 下拉刷新
     */
    private void refresh(int page) {
        int offset = page * PAGE_SIZE;
        int limit = offset + PAGE_SIZE;
        List<ReportEntity> datas = DBManager.getInstance().getReportEntityDao().queryBuilder()
                .orderDesc(ReportEntityDao.Properties.Id)
                .offset(offset)
                .limit(limit)
                .build()
                .list();
        if (datas != null && !datas.isEmpty()) {
            for (ReportEntity data : datas) {
                DetController detCtrl = new DetController(data);
                rptCtlList.add(detCtrl);
            }
            nPage++;
            mAdapter.dataChange(rptCtlList);
        }
        mRecyclerView.loadMoreFinish(false, true);
        mSwipeRefreshLayout.setRefreshing(false);
    }

    private void refreshInit() {
        rptCtlList.clear();
        nPage = 0;
        refresh(nPage);
    }
}
