package com.etek.controller.activity;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.elvishew.xlog.XLog;
import com.etek.controller.R;
import com.etek.controller.adapter.ProjectInfoAdapter;
import com.etek.controller.common.AppConstants;
import com.etek.controller.common.Globals;
import com.etek.controller.dto.Jbqy;
import com.etek.controller.dto.Jbqys;
import com.etek.controller.dto.Lg;
import com.etek.controller.dto.Lgs;
import com.etek.controller.dto.ProInfoDto;
import com.etek.controller.dto.ProjectFileDto;
import com.etek.controller.dto.Sbbhs;
import com.etek.controller.dto.Zbqy;
import com.etek.controller.dto.Zbqys;
import com.etek.controller.persistence.DBManager;
import com.etek.controller.persistence.entity.ControllerEntity;
import com.etek.controller.persistence.entity.DetonatorEntity;
import com.etek.controller.persistence.entity.ForbiddenZoneEntity;
import com.etek.controller.persistence.entity.PermissibleZoneEntity;
import com.etek.controller.persistence.entity.ProjectInfoEntity;
import com.etek.controller.persistence.gen.ProjectInfoEntityDao;
import com.etek.controller.utils.AppUtils;
import com.etek.controller.utils.AsyncHttpCilentUtil;
import com.etek.controller.utils.SommerUtils;
import com.etek.controller.widget.ClearableEditText;
import com.etek.sommerlibrary.activity.BaseActivity;
import com.etek.sommerlibrary.dto.Result;
import com.etek.sommerlibrary.utils.DateUtil;
import com.etek.sommerlibrary.utils.FileUtils;
import com.etek.sommerlibrary.utils.NetUtil;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.helper.StringUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class OnlineAuthProActivity extends BaseActivity {

    private static final int PAGE_SIZE = 10;
    private static final int REQUEST_CHOOSEFILE = 1;

    @BindView(R.id.rv_project_info)
    RecyclerView prv;
    @BindView(R.id.sl_project_info)
    SwipeRefreshLayout psl;

//    String companyCode;

    private int mNextRequestPage = 1;

    ProjectInfoAdapter mAdapter;
    String respStr = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authorize);
        ButterKnife.bind(this);
        initSupportActionBar(R.string.title_activity_authority);
        getUserCompanyCode();
        initView();
        initAdapter();
        initRefreshLayout();


//       XLog.d("path:",path);


    }


    private void getUserCompanyCode() {


        if (Globals.user == null
                || StringUtils.isEmpty(Globals.user.getCompanyCode())
                || StringUtils.isEmpty(Globals.user.getIdCode())) {
            showToast("公司代码或用户证件号为空，请去信息设置页面设置");
            delayAction(new Intent(mContext, UserInfoActivity.class), 1000);
        }

    }

    private void initView() {

        prv.setLayoutManager(new LinearLayoutManager(mContext));
        psl.setColorSchemeColors(Color.rgb(47, 223, 189));
        psl.setRefreshing(true);

    }

    private void initAdapter() {
        mAdapter = new ProjectInfoAdapter();
        mAdapter.setOnLoadMoreListener(() -> new Handler().post(() -> loadMore()), prv);
//        mAdapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_LEFT);
//        mAdapter.setPreLoadNumber(3);

        prv.setAdapter(mAdapter);
        prv.addOnItemTouchListener(new OnItemClickListener() {
            @Override
            public void onSimpleItemClick(final BaseQuickAdapter adapter, final View view, final int position) {
//                ToastUtils.show(mContext, "position:" + position);
                // 获取itemView的位置

                Intent intent = new Intent(mContext, ProDetailActivity.class);
                ProjectInfoEntity projectInfoEntity = mAdapter.getData().get(position);
                XLog.d("projectInfoEntity:", projectInfoEntity);
//                showToast("proid:" + projectInfoEntity.getId());
                intent.putExtra("projectId", projectInfoEntity.getId());
                startActivity(intent);
            }
        });
    }

    private void initRefreshLayout() {
        psl.setOnRefreshListener(() -> refresh());
    }

    private void refresh() {
//        showToast("数据更新！");
        mNextRequestPage = 1;

        mAdapter.setEnableLoadMore(false);//这里的作用是防止下拉刷新的时候还可以上拉加载
        List<ProjectInfoEntity> datas = DBManager.getInstance().getProjectInfoEntityDao().queryBuilder()
                .where(ProjectInfoEntityDao.Properties.IsOnline.eq(false))
                .orderDesc(ProjectInfoEntityDao.Properties.Id)
                .limit(PAGE_SIZE)
                .build()
                .list();
        setData(true, datas);
        mAdapter.setEnableLoadMore(true);
//        mAdapter.setLoadMoreView(R.layout.item_load_more);
        psl.setRefreshing(false);

    }

    private void loadMore() {

        XLog.v("加载更多");
        int offset = (mNextRequestPage - 1) * PAGE_SIZE;
        int limit = offset + PAGE_SIZE;
        List<ProjectInfoEntity> datas = DBManager.getInstance().getProjectInfoEntityDao().queryBuilder()
                .where(ProjectInfoEntityDao.Properties.IsOnline.eq(false))
                .orderDesc(ProjectInfoEntityDao.Properties.Id).offset(offset)
                .limit(limit)
                .build()
                .list();
        boolean isRefresh = mNextRequestPage == 1;
        setData(isRefresh, datas);

    }

    private void setData(boolean isRefresh, List data) {
        mNextRequestPage++;
        final int size = data == null ? 0 : data.size();
//        XLog.v("iscomputing:", prv.isComputingLayout());
        if (prv.isComputingLayout())
            return;
        if (isRefresh) {
            mAdapter.setNewData(data);
        } else {

            if (size > 0) {
                mAdapter.addData(data);
            }
        }
        if (size < PAGE_SIZE) {
            //第一页如果不够一页就不显示没有更多数据布局
            mAdapter.loadMoreEnd(isRefresh);
//            Toast.makeText(this, "没有数据", Toast.LENGTH_SHORT).show();
        } else {
            mAdapter.loadMoreComplete();
        }
    }







    void sendCmdMessage(int msg,String info) {
        Message message = new Message();
        message.what = msg;
        Bundle b = new Bundle();
        b.putString("info", info);
        message.setData(b);
        if (handler != null) {
            handler.sendMessage(message);
        }

    }
    private static final int UPDATE = 10;
    //消息处理者,创建一个Handler的子类对象,目的是重写Handler的处理消息的方法(handleMessage())
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){


        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case UPDATE:
                    Bundle b = msg.getData();
                    String info = b.getString("info");
                    if(StringUtils.isEmpty(info)){
                        return;
                    }
                    String url = AppConstants.ETEKTestServer + AppConstants.DETUnCheck;

                    AsyncHttpCilentUtil.httpPostJson(url, info, new okhttp3.Callback() {

                        @Override
                        public void onFailure(Call call, IOException e) {
                            XLog.e("IOException:"+e.getMessage());
//                closeDialog();
//                showStatusDialog("服务器报错");

//                sendCmdMessage(MSG_RPT_DANLING_ERR);
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
//                closeDialog();
                            String respStr = response.body().string();
                            if (!StringUtils.isEmpty(respStr)) {
                                XLog.w("respStr is  "+ respStr);
//                    showToast("上报返回值为空");

                            }

                        }
                    });
                    break;
            }
        }
    };

    private String valifyProjectFile(ProjectFileDto projectFile) {
        ProInfoDto mDetInfoDto = projectFile.getProInfo();
        if (mDetInfoDto == null) {
            return "信息为空！";
        }
        if (mDetInfoDto.getLgs() == null || mDetInfoDto.getLgs().getLg().isEmpty()) {
            return "缺少雷管信息，请检查报备是否正常";
        }
        if (mDetInfoDto.getZbqys() == null || mDetInfoDto.getZbqys().getZbqy().isEmpty()) {
            return "缺少经纬度信息，请检查报备是否正常！";
        }
        for (Zbqy zbqy : mDetInfoDto.getZbqys().getZbqy()) {
            if (StringUtils.isEmpty(zbqy.getZbqyjd()) || StringUtils.isEmpty(zbqy.getZbqywd())) {
                return "缺少经纬度信息，请检查报备是否正常！";
            }
        }

        if (mDetInfoDto.getSbbhs() == null || mDetInfoDto.getSbbhs().isEmpty()) {
            return "缺少起爆器信息，请检查报备是否正常";
        }

        return null;
    }


    private long storeProjectInfo(final ProjectFileDto projectFile, String fileSn) {

//        ThreadPoolUtils.getThreadPool().execute(()->{
        ProInfoDto mDetInfoDto = projectFile.getProInfo();
        XLog.v("proinfo:", mDetInfoDto);
        ProjectInfoEntity projectInfoEntity = new ProjectInfoEntity();
        projectInfoEntity.setApplyDate(mDetInfoDto.getSqrq());
        projectInfoEntity.setProCode(projectFile.getXmbh());
        projectInfoEntity.setProName(projectFile.getXmmc());
        projectInfoEntity.setCompanyCode(projectFile.getDwdm());
        projectInfoEntity.setCompanyName(projectFile.getDwmc());
        projectInfoEntity.setContractCode(projectFile.getHtbh());
        projectInfoEntity.setContractName(projectFile.getHtmc());
        projectInfoEntity.setFileSn(fileSn);
        projectInfoEntity.setStatus(0);
        projectInfoEntity.setIsOnline(false);
        projectInfoEntity.setCreateTime(new Date());
        ProjectInfoEntity existProjectInfo = DBManager.getInstance().getProjectInfoEntityDao().queryBuilder()
                .where(ProjectInfoEntityDao.Properties.FileSn.eq(fileSn)).unique();
        if (existProjectInfo != null) {
            return 0;
        }
        long proId = DBManager.getInstance().getProjectInfoEntityDao().insert(projectInfoEntity);
        if (proId == 0) {
            return 0;
        }
        // get detonators to database by sommer 19.01.07
        Lgs lgs = mDetInfoDto.getLgs();
        if (!lgs.getLg().isEmpty()) {
            List<DetonatorEntity> detonatorEntityList = new ArrayList<>();
            for (Lg lg : lgs.getLg()) {

                DetonatorEntity detonatorBean = new DetonatorEntity();
                detonatorBean.setCode(lg.getFbh());
                detonatorBean.setWorkCode(lg.getGzm());
                detonatorBean.setUid(lg.getUid());
                detonatorBean.setValidTime(lg.getYxq());
                detonatorBean.setProjectInfoId(proId);
//                                detonatorBean.set
//                            detonatorBean.setProInfoBean(proInfoBean);
                detonatorBean.setStatus(lg.getGzmcwxx());
//                                detonatorBean.set
//                               detonatorBean.setProInfoBean(detInfoDto);
                detonatorEntityList.add(detonatorBean);

//
            }
            DBManager.getInstance().getDetonatorEntityDao().insertInTx(detonatorEntityList);

        }


        Zbqys zbqys = mDetInfoDto.getZbqys();
        if (!zbqys.getZbqy().isEmpty()) {
            List<PermissibleZoneEntity> permissibleZoneEntityList = new ArrayList<>();
            for (Zbqy zbqy : zbqys.getZbqy()) {

//                                private String zbqssj;  //准爆起始时间
//
//                                private String zbjzsj;  //准爆截止时间
                PermissibleZoneEntity permissibleZone = new PermissibleZoneEntity();
//                            permissibleZoneBean.setProInfoBean(proInfoBean);
                permissibleZone.setName(zbqy.getZbqymc());
                permissibleZone.setLatitude(Double.parseDouble(zbqy.getZbqywd()));
                permissibleZone.setLongitude(Double.parseDouble(zbqy.getZbqyjd()));
                permissibleZone.setRadius(Integer.parseInt(zbqy.getZbqybj()));
                permissibleZone.setStartTime(zbqy.getZbqssj());
                permissibleZone.setStopTime(zbqy.getZbjzsj());
                permissibleZone.setProjectInfoId(proId);
                permissibleZoneEntityList.add(permissibleZone);
//                                Dao<PermissibleZoneBean, Long> permissibleZoneDao = DatabaseHelper.getInstance(mcontext).getDao(PermissibleZoneBean.class);
//                                permissibleZoneDao.create(permissibleZoneBean);
//                                permissibleZoneBean.setStartTime(zbqy.getZbqssj());
//                                permissibleZoneBean.setStopTime(zbqy.getZbjzsj());
            }
            DBManager.getInstance().getPermissibleZoneEntityDao().insertInTx(permissibleZoneEntityList);
        }
        Jbqys jbqys = mDetInfoDto.getJbqys();
        if (!jbqys.getJbqy().isEmpty()) {
            List<ForbiddenZoneEntity> forbiddenZoneEntityList = new ArrayList<>();
            for (Jbqy jbqy : jbqys.getJbqy()) {

//                                private String zbqssj;  //准爆起始时间
//
//                                private String zbjzsj;  //准爆截止时间
                ForbiddenZoneEntity forbiddenZoneEntity = new ForbiddenZoneEntity();

//                forbiddenZoneEntity.setName(jbqy.getJbjzsj());
                forbiddenZoneEntity.setLatitude(Double.parseDouble(jbqy.getJbqywd()));
                forbiddenZoneEntity.setLongitude(Double.parseDouble(jbqy.getJbqyjd()));
                forbiddenZoneEntity.setRadius(Integer.parseInt(jbqy.getJbqybj()));
                forbiddenZoneEntity.setStartTime(jbqy.getJbqssj());
                forbiddenZoneEntity.setStopTime(jbqy.getJbjzsj());
                forbiddenZoneEntity.setProjectInfoId(proId);
                forbiddenZoneEntityList.add(forbiddenZoneEntity);
//                                Dao<PermissibleZoneBean, Long> permissibleZoneDao = DatabaseHelper.getInstance(mcontext).getDao(PermissibleZoneBean.class);
//                                permissibleZoneDao.create(permissibleZoneBean);
//                                permissibleZoneBean.setStartTime(zbqy.getZbqssj());
//                                permissibleZoneBean.setStopTime(zbqy.getZbjzsj());
            }
            DBManager.getInstance().getForbiddenZoneEntityDao().insertInTx(forbiddenZoneEntityList);
        }
        List<Sbbhs> sbbhs = mDetInfoDto.getSbbhs();

        if (!sbbhs.isEmpty()) {
            List<ControllerEntity> controllerEntityList = new ArrayList<>();
            for (Sbbhs sbbh : sbbhs) {
                ControllerEntity controller = new ControllerEntity();
                controller.setName(sbbh.getSbbh());
                controller.setProjectInfoId(proId);
                controllerEntityList.add(controller);
//                            detControllerBean.setProInfoBean(proInfoBean);

//                            Dao<DetControllerBean, Long> detControllerDao = DatabaseHelper.getInstance(mcontext).getDao(DetControllerBean.class);
//                            detControllerDao.create(detControllerBean);
            }
            DBManager.getInstance().getControllerEntityDao().insertInTx(controllerEntityList);
        }

//        });
        return proId;
    }


    List<File> localFile = new ArrayList<>();
    boolean isSearchEnd = false;

    public void getLocalFileByName(String Path, String name, boolean IsIterative) //搜索目录，扩展名，是否进入子文件夹
    {
        if (isSearchEnd)
            return;
        File[] files = new File(Path).listFiles();

        for (int i = 0; i < files.length; i++) {
            File f = files[i];
            if (f.isFile()) {
//                XLog.d(f.toString());
                if (f.getName().equalsIgnoreCase(name)) {
                    localFile.add(f);
                    isSearchEnd = true;
                    return;
                }


                if (!IsIterative)
                    break;
            } else if (f.isDirectory() && f.getPath().indexOf("/.") == -1) //忽略点文件（隐藏文件/文件夹）
                getLocalFileByName(f.getPath(), name, IsIterative);
        }

    }


    @Override
    protected void onResume() {
        super.onResume();
        refresh();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // sommer jiang

        if (Globals.isTest) {
            getMenuInflater().inflate(R.menu.menu_auth_test, menu);
        } else {
            getMenuInflater().inflate(R.menu.menu_auth, menu);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar det_rpt_item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_file) {

            getLocalFile();

        } else if (id == R.id.action_local_file) {

            getLocalEncodeFile();

        }


        return super.onOptionsItemSelected(item);
    }

    private void getLocalFile() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("请输入本地文件 序列号！");
        //设置对话框标题
        builder.setIcon(R.mipmap.ic_launcher);
        String filesn = getStringInfo("filesn");
        final ClearableEditText edit = new ClearableEditText(mContext);
        edit.setHint("");
        if (!StringUtils.isEmpty(filesn)) {
            edit.setText(filesn);
        }
        edit.setInputType(InputType.TYPE_CLASS_NUMBER);
        //3.将输入框赋值给Dialog,并增加确定取消按键
        builder.setView(edit);
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                showToast("你输入的是: " + edit.getText().toString());

                String fileSn = edit.getText().toString();
                if (!StringUtil.isBlank(fileSn) && fileSn.length() == 6) {
//                    dialog.dismiss();
                    showProgressDialog("搜索开始");
                    localFile.clear();
                    isSearchEnd = false;
                    getLocalFileByName(Environment.getExternalStorageDirectory().getPath(), fileSn + ".json", true);
                    if (!localFile.isEmpty()) {
                        File f = localFile.get(0);
                        jsonFileParse(f);
                    }
                    closeProgressDialog();
                    dialog.dismiss();
                } else {
                    showToast("请输入6个字的文件序列号");
                }
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        });
        // 4.设置常用api，并show弹出
        builder.setCancelable(true); //设置按钮是否可以按返回键取消,false则不可以取消
        AlertDialog dialog = builder.create(); //创建对话框
        dialog.setCanceledOnTouchOutside(true); //设置弹出框失去焦点是否隐藏,即点击屏蔽其它地方是否隐藏
        dialog.show();
    }

    private void getLocalEncodeFile() {
        showDialog();


//        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);//关键！多选参数
//        intent.setType("text/plain"); //指定文件类型
//        intent.addCategory(Intent.CATEGORY_OPENABLE);
//        startActivityForResult(Intent.createChooser(intent, "选择文件"), REQUEST_CHOOSEFILE);
    }


    public void dataDecode(File file, String fileSn, String proCode, String contractCode, String companyCode) {

        String content = null;
        try {
//            content = FileUtils.readFileFromSD("detonator/json", "pf_20190105_161434.json");
            content = FileUtils.readFile(file);
        } catch (IOException e) {
            XLog.e(e.getMessage());
            return;
        }
//        XLog.v("path:",path);

        if (content != null) {
            XLog.w("content:" + content);
//            XLog.v("fileSn:",fileSn);
//            XLog.v("content:",path);
//            ProjectFileDto projectFileDto = JSON.parseObject(content, ProjectFileDto.class);
            ProjectFileDto projectFile = new ProjectFileDto();
            projectFile.setMmwj(content);
            Result detInfoResult = projectFile.parseContentAndSave(fileSn);
//            byte[] decode2 = DES3Utils.decryptMode(content.getBytes(), DES3Utils.CRYPT_KEY_FRONT + fileSn);
//            if(decode2!=null){
//                XLog.v("decode2:"+new String(decode2));
//            }
            XLog.v("detInfoResult:" + detInfoResult);
            projectFile.setCompany(companyCode);
            projectFile.setDwdm(companyCode);
            projectFile.setFileSn(fileSn);
            projectFile.setXmbh(proCode);
            projectFile.setHtbh(contractCode);
            String msg = valifyProjectFile(projectFile);
            if (msg != null && !StringUtils.isEmpty(msg)) {
                showStatusDialog(msg);
                return;
            }
            long proId = storeProjectInfo(projectFile, fileSn);
            if (proId == 0) {
                showStatusDialog("已经存在有此项目");

            } else {
                runOnUiThread(() -> refresh()
                );
            }
//            setLongInfo("projectId", proId);
//                    ProjectInfoEntity projectInfo = DBManager.getInstance().getProjectInfoEntityDao().
//                            queryBuilder()
//                            .where(ProjectInfoEntityDao.Properties.Id.eq(proId)).uniqueOrThrow();
//                    showToast("projectInfo！" + projectInfo);


        } else {
            showToast("内容为空！");
            XLog.d("content is null");
        }


    }

    public void jsonFileParse(File file) {

        String content = null;
        try {
//            content = FileUtils.readFileFromSD("detonator/json", "pf_20190105_161434.json");
            content = FileUtils.readFile(file);
        } catch (IOException e) {
            XLog.e(e.getMessage());
            return;
        }
//        XLog.v("path:",path);

        if (content != null) {
//            XLog.v("fileSn:",fileSn);
//            XLog.v("content:",path);
//            ProjectFileDto projectFileDto = JSON.parseObject(content, ProjectFileDto.class);
            ProjectFileDto projectFile = JSON.parseObject(content, ProjectFileDto.class);
            String fileSn = projectFile.getFileSn();
            String msg = valifyProjectFile(projectFile);
            if (msg != null && !StringUtils.isEmpty(msg)) {
                showStatusDialog(msg);
                return;
            }
            long proId = storeProjectInfo(projectFile, fileSn);
            if (proId == 0) {
                showStatusDialog("已经存在有此项目");

            } else {
                runOnUiThread(() -> {
                    refresh();
                });
            }
//            setLongInfo("projectId", proId);
//                    ProjectInfoEntity projectInfo = DBManager.getInstance().getProjectInfoEntityDao().
//                            queryBuilder()
//                            .where(ProjectInfoEntityDao.Properties.Id.eq(proId)).uniqueOrThrow();
//                    showToast("projectInfo！" + projectInfo);


        } else {
            showToast("内容为空！");
            XLog.d("content is null");
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CHOOSEFILE && resultCode == Activity.RESULT_OK && data != null) {
            Uri uri = data.getData();
            Toast.makeText(this, "文件路径：" + uri.getPath().toString(), Toast.LENGTH_SHORT).show();
            try {
                byte[] bytes = readBytes(data.getData());
                XLog.d(new String(bytes));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public byte[] readBytes(Uri inUri) throws IOException {
        InputStream inputStream = getContentResolver().openInputStream(inUri);

        // this dynamically extends to take the bytes you read
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();

        // this is storage overwritten on each iteration with bytes
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        // we need to know how may bytes were read to write them to the byteBuffer
        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
            //  Log.i("readBytes", "" + len);
        }

        // and then we can return your byte array.
        return byteBuffer.toByteArray();
    }

    public void showDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this.getBaseContext()).inflate(R.layout.dialog_input_info, null, false);
        EditText etProCode = view.findViewById(R.id.et_pro_code);
        EditText etContractCode = view.findViewById(R.id.et_contract_code);
        EditText etCompanyCode = view.findViewById(R.id.et_company_code);
        EditText edFileSn = view.findViewById(R.id.ed_file_sn);
        etCompanyCode.setText(Globals.user.getCompanyCode());
        dialog.setView(view);
        dialog.setTitle("请输入本地文件信息！");
        //设置对话框标题
        dialog.setIcon(R.mipmap.ic_launcher);
        dialog.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                showToast("你输入的是: " + edit.getText().toString());

                String fileSn = edFileSn.getText().toString();
                String contractCode = etContractCode.getText().toString();
                String companyCode = etCompanyCode.getText().toString();
                String proCode = etProCode.getText().toString();


                if (!StringUtil.isBlank(fileSn) && fileSn.length() == 6) {
//                    dialog.dismiss();
                    showProgressDialog("搜索开始");
                    localFile.clear();
                    isSearchEnd = false;
                    getLocalFileByName(Environment.getExternalStorageDirectory().getPath(), fileSn + ".txt", true);
                    if (!localFile.isEmpty()) {
                        File f = localFile.get(0);
                        dataDecode(f, fileSn, proCode, contractCode, companyCode);
                    }
                    closeProgressDialog();
                    dialog.dismiss();
                } else {
                    showToast("请输入6个字的文件序列号");
                }
            }
        });
        dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        });

        dialog.show();
    }


}
