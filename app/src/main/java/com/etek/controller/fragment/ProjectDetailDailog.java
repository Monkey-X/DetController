package com.etek.controller.fragment;


import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;


import com.etek.controller.R;
import com.etek.controller.adapter.ControllerAdapter;
import com.etek.controller.adapter.DetonatorEntityAdapter;
import com.etek.controller.adapter.ForbiddenZoneAdapter;
import com.etek.controller.adapter.PermissibleZoneAdapter;
import com.etek.controller.persistence.DBManager;
import com.etek.controller.persistence.entity.ControllerEntity;
import com.etek.controller.persistence.entity.DetonatorEntity;
import com.etek.controller.persistence.entity.ForbiddenZoneEntity;
import com.etek.controller.persistence.entity.PermissibleZoneEntity;
import com.etek.controller.persistence.entity.ProjectInfoEntity;
import com.etek.controller.persistence.gen.ProjectInfoEntityDao;

import com.etek.sommerlibrary.utils.DateUtil;
import com.etek.sommerlibrary.utils.ToastUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.elvishew.xlog.XLog;


public class ProjectDetailDailog extends DialogFragment {

    private Context mContext;

    ProjectInfoEntity projectInfo;



    @BindView(R.id.pro_id)
    TextView proId;

    @BindView(R.id.pro_name)
    TextView proName;

    @BindView(R.id.company_id)
    TextView companyId;       //dwdm; //单位代码
    @BindView(R.id.company_name)
    TextView companyName;     //dwmc; //单位名称
    @BindView(R.id.contract_id)
    TextView contractId;         //htbh; //合同编号
    @BindView(R.id.contract_name)
    TextView contractName;        //htmc;    //合同名称

    @BindView(R.id.create_time)
    TextView createTime;

    @BindView(R.id.rv_device_list)
    RecyclerView rvDevice;
    @BindView(R.id.rv_controller_list)
    RecyclerView rvController;
    @BindView(R.id.rv_forbidden_list)
    RecyclerView rvForbidden;
    @BindView(R.id.rv_permissible_list)
    RecyclerView rvPermissible;



    @OnClick(R.id.dialog_return)
    public void OnClick() {
        dismiss();
    }






    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;

    }

    private ProjectInfoEntity getProjectInfo(long proId ) {

        ProjectInfoEntity projectInfo = null;
        if (proId > 0) {
            projectInfo = DBManager.getInstance().getProjectInfoEntityDao().
                    queryBuilder()
                    .where(ProjectInfoEntityDao.Properties.Id.eq(proId)).uniqueOrThrow();
            XLog.v(projectInfo.toString());

        } else {
            ToastUtils.showCustom(mContext, "没有此项目!");

            return projectInfo;
        }
        return projectInfo;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        View rootView = inflater.inflate(R.layout.dialog_fragment_project_info, null);
        getDialog().setCanceledOnTouchOutside(true);
        ButterKnife.bind(this, rootView);

        //得到activity数据
        Bundle bundle = getArguments();

        if (bundle != null) {
            long projectId =  bundle.getLong("projectId");
            if(projectId<=0){
              dismiss();
            }
            projectInfo =    getProjectInfo(projectId);

            proId.setText(projectInfo.getProCode());
            proName.setText(projectInfo.getProName());
            companyId.setText(projectInfo.getCompanyCode());
            companyName.setText(projectInfo.getCompanyName());
            contractId.setText(projectInfo.getContractCode());
            contractName.setText(projectInfo.getContractName());
            createTime.setText(DateUtil.getDateStr(projectInfo.getApplyDate()));

            List<PermissibleZoneEntity> permissibleZoneBeans = projectInfo.getPermissibleZoneList();
            if(permissibleZoneBeans!=null&&!permissibleZoneBeans.isEmpty()){
                PermissibleZoneAdapter permissibleZoneAdapter = new PermissibleZoneAdapter();

                rvPermissible.setLayoutManager(new LinearLayoutManager(mContext));
                rvPermissible.setAdapter(permissibleZoneAdapter);
                permissibleZoneAdapter.setNewData(permissibleZoneBeans);
            }
            List<ForbiddenZoneEntity> forbiddenZoneBeans = projectInfo.getForbiddenZoneList();
            if(forbiddenZoneBeans!=null&&!forbiddenZoneBeans.isEmpty()){
                ForbiddenZoneAdapter forbiddenZoneAdapter = new ForbiddenZoneAdapter();

                rvForbidden.setLayoutManager(new LinearLayoutManager(mContext));
                rvForbidden.setAdapter(forbiddenZoneAdapter);
                forbiddenZoneAdapter.setNewData(forbiddenZoneBeans);
            }
            List<DetonatorEntity> detonatorBeans = projectInfo.getDetonatorList();
            if(detonatorBeans!=null&&!detonatorBeans.isEmpty()){
                DetonatorEntityAdapter detonatorAdapter = new DetonatorEntityAdapter();

                rvDevice.setLayoutManager(new LinearLayoutManager(mContext));
                rvDevice.setAdapter(detonatorAdapter);
                detonatorAdapter.setNewData(detonatorBeans);
            }
            List<ControllerEntity> controllerBeans =projectInfo.getControllerList();
            if(controllerBeans!=null&&!controllerBeans.isEmpty()){
                ControllerAdapter controllerAdapter = new ControllerAdapter();

                rvController.setLayoutManager(new LinearLayoutManager(mContext));
                rvController.setAdapter(controllerAdapter);
                controllerAdapter.setNewData(controllerBeans);
            }


//            poData.setScanned(10000);
        }else{
            ToastUtils.show(mContext,"没有有效的规则限制");
            XLog.i("project is null");
        }


        return rootView;

    }

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
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        win.setAttributes(params);

    }

    @Override
    public void onDestroy() {


        super.onDestroy();
    }


}
