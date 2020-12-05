package com.etek.controller.persistence;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.etek.controller.persistence.gen.ChkControllerEntityDao;
import com.etek.controller.persistence.gen.ChkDetonatorEntityDao;
import com.etek.controller.persistence.gen.ControllerEntityDao;
import com.etek.controller.persistence.gen.DaoMaster;
import com.etek.controller.persistence.gen.DaoSession;
import com.etek.controller.persistence.gen.DetReportEntityDao;
import com.etek.controller.persistence.gen.DetonatorEntityDao;
import com.etek.controller.persistence.gen.ForbiddenZoneEntityDao;
import com.etek.controller.persistence.gen.PermissibleZoneEntityDao;
import com.etek.controller.persistence.gen.ProjectDownLoadEntityDao;
import com.etek.controller.persistence.gen.ProjectInfoEntityDao;
import com.etek.controller.persistence.gen.ReportEntityDao;
import com.etek.controller.persistence.gen.RptDetonatorEntityDao;
import com.etek.controller.persistence.gen.SingleCheckEntityDao;

import org.greenrobot.greendao.database.Database;


public class DBManager {
    public static String DB_NAME = "et-detonator.db";
    //    private DaoMaster.DevOpenHelper mHelper;
    private SQLiteDatabase db;
    private DaoMaster mDaoMaster;
    private DaoSession mDaoSession;

    public static DBManager mDBManager;
    private DBHelper dbHelper;

    public static synchronized DBManager getInstance() {
        return mDBManager;
    }

    private DBManager(Context context) {
        dbHelper = new DBHelper(context);
//        mHelper = new DaoMaster.DevOpenHelper(context, DB_NAME, null);  //创建数据库
        db = dbHelper.getWritableDatabase();
        mDaoMaster = new DaoMaster(db);
        mDaoSession = mDaoMaster.newSession();
    }

    public static void init(Context context) {

        if (mDBManager == null)
            mDBManager = new DBManager(context);
    }

    public ControllerEntityDao getControllerEntityDao() {
        return mDaoSession.getControllerEntityDao();
    }

    public DetonatorEntityDao getDetonatorEntityDao() {
        return mDaoSession.getDetonatorEntityDao();
    }

    public SingleCheckEntityDao getSingleCheckEntityDao(){
        return mDaoSession.getSingleCheckEntityDao();
    }

    public DetReportEntityDao getDetReportEntityDao() {
        return mDaoSession.getDetReportEntityDao();
    }

    public ForbiddenZoneEntityDao getForbiddenZoneEntityDao() {
        return mDaoSession.getForbiddenZoneEntityDao();
    }

    public PermissibleZoneEntityDao getPermissibleZoneEntityDao() {
        return mDaoSession.getPermissibleZoneEntityDao();
    }

    public ProjectInfoEntityDao getProjectInfoEntityDao() {
        return mDaoSession.getProjectInfoEntityDao();
    }

    public ReportEntityDao getReportEntityDao() {
        return mDaoSession.getReportEntityDao();
    }


    public RptDetonatorEntityDao getRptDetonatorEntityDao() {
        return mDaoSession.getRptDetonatorEntityDao();
    }

    public ChkControllerEntityDao getChkControllerEntityDao() {
        return mDaoSession.getChkControllerEntityDao();
    }

    public ChkDetonatorEntityDao getChkDetonatorEntityDao() {
        return mDaoSession.getChkDetonatorEntityDao();
    }

    public ProjectDownLoadEntityDao getProjectDownLoadEntityDao(){
        return mDaoSession.getProjectDownLoadEntityDao();
    }

}
