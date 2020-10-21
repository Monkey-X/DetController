package com.etek.controller.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.etek.controller.R;
import com.etek.controller.adapter.HomeAdapter;
import com.etek.controller.common.Globals;
import com.etek.controller.entity.FuncationActivity;
import com.etek.controller.entity.HomeItem;
import com.etek.sommerlibrary.activity.BaseActivity;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


public class OnlineCheckActivity extends BaseActivity {


    @BindView(R.id.offline_act_list)
    RecyclerView offlineActList;
    private ArrayList<HomeItem> mDataList;
    public static FuncationActivity[] offlineFuncation = {
            new FuncationActivity(OnlineAuthorizeActivity.class,"授权",R.string.title_act_online_authorize,R.drawable.authority),
            new FuncationActivity(OnlineCheckoutActivity.class,"规则检查",R.string.title_activity_checkout,R.drawable.yun),
//			new FuncationActivity(CheckoutActivity.class,"数据检查",R.string.title_activity_checkout,R.drawable.check),
			new FuncationActivity(ReportActivity.class,"数据上报",R.string.activity_det_report,R.drawable.report),
//			new FuncationActivity(ReportActivity.class,"数据上报",R.string.title_activity_report,R.drawable.gv_section),

    };

    private void initData() {
        mDataList = new ArrayList<>();
        for (int i = 0; i < offlineFuncation.length; i++) {
            HomeItem item = new HomeItem();
            item.setTitle(getString(offlineFuncation[i].getTitle()));
            item.setActivity(offlineFuncation[i].getFuncation());
            item.setImageResource(offlineFuncation[i].getImage());
            mDataList.add(item);
        }

    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline_check);
        ButterKnife.bind(this);
        initSupportActionBar(R.string.title_act_online_checkout);
//        getUserCompanyCode();
        initData();
        BaseQuickAdapter homeAdapter = new HomeAdapter(R.layout.home_item_view, mDataList);
        homeAdapter.openLoadAnimation();
//        View top = getLayoutInflater().inflate(R.layout.top_view, (ViewGroup) mRecyclerView.getParent(), false);
//        homeAdapter.addHeaderView(top);
        homeAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

                Intent intent = new Intent(OnlineCheckActivity.this, offlineFuncation[position].getFuncation());
                startActivity(intent);


            }
        });
        offlineActList.setLayoutManager(new GridLayoutManager(this, 2));
        offlineActList.setAdapter(homeAdapter);
        Globals.isOnline = true;
    }



}
