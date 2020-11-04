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
import com.etek.controller.adapter.DetReportAdapter;
import com.etek.controller.common.Globals;;
import com.etek.controller.entity.DetController;
import com.etek.controller.entity.Detonator;
import com.etek.controller.persistence.DBManager;
import com.etek.controller.persistence.entity.ReportEntity;
import com.etek.controller.persistence.entity.RptDetonatorEntity;
import com.etek.controller.persistence.gen.ReportEntityDao;
import com.etek.controller.utils.SommerUtils;
import com.etek.controller.widget.DefineLoadMoreView;
import com.etek.sommerlibrary.activity.BaseActivity;
import com.etek.sommerlibrary.utils.MD5Util;
import com.yanzhenjie.recyclerview.OnItemClickListener;
import com.yanzhenjie.recyclerview.OnItemMenuClickListener;
import com.yanzhenjie.recyclerview.SwipeMenuBridge;
import com.yanzhenjie.recyclerview.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.SwipeMenuItem;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;
import com.yanzhenjie.recyclerview.widget.DefaultItemDecoration;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import butterknife.ButterKnife;
import io.reactivex.disposables.Disposable;

public class ReportActivity2 extends BaseActivity {

    private ImageView img_loading;
    private SwipeRecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private DetReportAdapter mAdapter;
    int nPage = 0;
    private static final int PAGE_SIZE = 10;

    //ReportDao reportDao;
    List<DetController> rptCtlList;
    DetController cDetController;
    private int REQUESTCODE = 100;
    private Disposable scanDisposable;

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
//        reportDao = new ReportDao(mContext);
        cDetController = new DetController();
        rptCtlList = new ArrayList<>();
        nPage = 0;

        //模拟数据，显示页面
        List<ReportEntity> reportEntities = DBManager.getInstance().getReportEntityDao().loadAll();
        if (reportEntities != null && reportEntities.size() == 0){
            for (int i = 0; i < 10; i++) {
                ReportEntity reportEntity = new ReportEntity();
                reportEntity.setCompanyCode("111111111111");
                reportEntity.setContractId("222222222222");
                reportEntity.setControllerId("333333333333");
                reportEntity.setId((long) i);
                reportEntity.setLatitude(0.0000023);
                reportEntity.setLongitude(0.0000054);
                reportEntity.setStatus(i%2);
                reportEntity.setBlastTime(new Date());
                DBManager.getInstance().getReportEntityDao().insert(reportEntity);
            }
        }
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

        mAdapter = new DetReportAdapter(mContext, rptCtlList);

//        mAdapter.setPreLoadNumber(3);
        mRecyclerView.setAdapter(mAdapter);

        mSwipeRefreshLayout.setOnRefreshListener(() -> mSwipeRefreshLayout.setRefreshing(false));

//        refresh(0);
//        handler = new MyHandler();
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
//                    List<String> strings = createDataList(mAdapter.getItemCount());
//                    mDataList.addAll(strings);
//                    // notifyItemRangeInserted()或者notifyDataSetChanged().
//                    mAdapter.notifyItemRangeInserted(mDataList.size() - strings.size(), strings.size());
//                        showToast("更新吧");
                    // 数据完更多数据，一定要掉用这个方法。
                    // 第一个参数：表示此次数据是否为空。
                    // 第二个参数：表示是否还有更多数据。
                    mRecyclerView.loadMoreFinish(false, true);

                    // 如果加载失败调用下面的方法，传入errorCode和errorMessage。
                    // errorCode随便传，你自定义LoadMoreView时可以根据errorCode判断错误类型。
                    // errorMessage是会显示到loadMoreView上的，用户可以看到。
                    // mRecyclerView.loadMoreError(0, "请求网络失败");
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
//            Toast.makeText(mContext, "第" + position + "个", Toast.LENGTH_SHORT).show();
            cDetController = rptCtlList.get(position);
            Intent intent = new Intent(mContext, ReportDetailActivity.class);
            intent.putExtra("DetController", cDetController);
//        delayAction(intent,1000);
            startActivityForResult(intent, REQUESTCODE);
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
//                showToast("list第" + position + "; 右侧菜单第" + menuPosition);
                if (menuPosition == 0) {
                    showRemoveDialog(position);
                }
            }
        }
    };

    private void showRemoveDialog(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("是否删除此数据！");
        //设置对话框标题
        builder.setIcon(R.mipmap.ic_launcher);

        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                reportDao.deleteController(rptCtlList.get(position));
                ReportEntity reportEntity = rptCtlList.get(position).getReportEntity();
                reportEntity.setId(rptCtlList.get(position).getId());
                DBManager.getInstance().getReportEntityDao().delete(reportEntity);
//                DBManager.getInstance().getDetReportEntityDao().delete(rptCtlList.get(position));
                rptCtlList.remove(position);
                mAdapter.notifyItemRemoved(position);

            }
        });
        builder.setNegativeButton("取消", null);
        // 4.设置常用api，并show弹出
        builder.setCancelable(true); //设置按钮是否可以按返回键取消,false则不可以取消
        AlertDialog dialog = builder.create(); //创建对话框
        dialog.setCanceledOnTouchOutside(true); //设置弹出框失去焦点是否隐藏,即点击屏蔽其它地方是否隐藏
        dialog.show();
    }


    private void showSameDetDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("此次雷管传输已经存在！");
        //设置对话框标题
        builder.setIcon(R.mipmap.ic_launcher);

        builder.setPositiveButton("确认", null);
//        builder.setNegativeButton("取消", null);
        // 4.设置常用api，并show弹出
        builder.setCancelable(true); //设置按钮是否可以按返回键取消,false则不可以取消
        AlertDialog dialog = builder.create(); //创建对话框
        dialog.setCanceledOnTouchOutside(true); //设置弹出框失去焦点是否隐藏,即点击屏蔽其它地方是否隐藏
        dialog.show();
    }

    private String getToken(DetController detController) {
        StringBuilder sb = new StringBuilder();
        for (Detonator detonator : detController.getDetList()) {
            sb.append(detonator.getDetCode());
        }
//        XLog.i("sb:" + sb.toString());
        String token = MD5Util.md5(sb.toString());
        return token;
    }

    private long storeDetController(DetController detController) {

        detController.setStatus(0);
//        detController.setProjectId(proId);
        detController.setUserIDCode(Globals.user.getIdCode());
//        detController.setContractId(contractId);

//        XLog.i(" old token :", detController.getToken());

        String token = getToken(detController);
//        XLog.i(" new token :", token);
        detController.setToken(token);
//        ChkControllerEntity chkControllerEntity = DBManager.getInstance().getChkControllerEntityDao().queryBuilder()
//                .where(ChkControllerEntityDao.Properties.Token.eq(detController.getToken())).unique();
//        if(chkControllerEntity==null){
//            showStatusDialog("没有此对应的规则检查文件！");
//            return 0;
//        }
//        XLog.i(" chkControllerEntity :", chkControllerEntity);
//        detController.setContractId(chkControllerEntity.getContractId());
//        detController.setProjectId(chkControllerEntity.getProjectId());
        return storeReport(detController);

    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshInit();
    }

    @Override
    protected void onDestroy() {
        if (scanDisposable != null) {
            scanDisposable.dispose();
            scanDisposable = null;
        }
        super.onDestroy();
    }

    private void refresh(int page) {
        int offset = page * PAGE_SIZE;
        int limit = offset + PAGE_SIZE;
        List<ReportEntity> datas = DBManager.getInstance().getReportEntityDao().queryBuilder()
                .orderDesc(ReportEntityDao.Properties.Id)
                .offset(offset)
                .limit(limit)
                .build()
                .list();

//        List<ReportEntity> datas = DBManager.getInstance().getReportEntityDao().loadAll();

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

    private long storeReport(final DetController detController) {
        ReportEntity reportEntity = detController.getReportEntity();
        ReportEntity oldController = DBManager.getInstance().getReportEntityDao().queryBuilder()
                .where(ReportEntityDao.Properties.Token.eq(detController.getToken())).unique();
        if (oldController != null) {
            showSameDetDialog();
            return 0;
        }
//        detController.setContractId();
        if (detController.getDetList() == null || detController.getDetList().size() == 0) {
            return -10;
        }

        long rptId = DBManager.getInstance().getReportEntityDao().insert(reportEntity);


        for (Detonator detonator : detController.getDetList()) {
            RptDetonatorEntity rptDet = new RptDetonatorEntity();
            rptDet.setSource(SommerUtils.bytesToHexString(detonator.getSource()));
            rptDet.setChipID(detonator.getChipID());
            rptDet.setDetIDs(SommerUtils.bytesToHexString(detonator.getIds()));
            rptDet.setStatus(detonator.getStatus());
            rptDet.setType(detonator.getType());
            rptDet.setNum(detonator.getNum());
            rptDet.setValidTime(detonator.getTime());
            rptDet.setCode(detonator.getDetCode());
            rptDet.setWorkCode(SommerUtils.bytesToHexString(detonator.getAcCode()));
            rptDet.setUid(detonator.getUid());
            rptDet.setRelay(detonator.getRelay());
            rptDet.setReportId(rptId);
//            rptDet.setId(SommerUtils.bytesToLong(detonator.getIds()));
            DBManager.getInstance().getRptDetonatorEntityDao().insertOrReplace(rptDet);
        }
        return rptId;
    }
}
