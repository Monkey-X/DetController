package com.etek.controller.persistence;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.etek.controller.persistence.entity.ProjectDetonator;
import com.etek.controller.persistence.gen.ChkControllerEntityDao;
import com.etek.controller.persistence.gen.ChkDetonatorEntityDao;
import com.etek.controller.persistence.gen.ControllerEntityDao;
import com.etek.controller.persistence.gen.DaoMaster;
import com.etek.controller.persistence.gen.DaoSession;
import com.etek.controller.persistence.gen.DetReportEntityDao;
import com.etek.controller.persistence.gen.DetonatorEntityDao;
import com.etek.controller.persistence.gen.ForbiddenZoneEntityDao;
import com.etek.controller.persistence.gen.PendingProjectDao;
import com.etek.controller.persistence.gen.PermissibleZoneEntityDao;
import com.etek.controller.persistence.gen.ProjectDetonatorDao;
import com.etek.controller.persistence.gen.ProjectDownLoadEntityDao;
import com.etek.controller.persistence.gen.ProjectInfoEntityDao;
import com.etek.controller.persistence.gen.ReportEntityDao;
import com.etek.controller.persistence.gen.RptDetonatorEntityDao;
import com.etek.controller.persistence.gen.SingleCheckEntityDao;
import com.etek.controller.persistence.gen.YunnanAuthBobmEntityDao;

import org.greenrobot.greendao.database.Database;


public class DBManager {
    public static String DB_NAME = "et-detonator.db";
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

    public ForbiddenZoneEntityDao getForbiddenZoneEntityDao() {
        return mDaoSession.getForbiddenZoneEntityDao();
    }

    public PermissibleZoneEntityDao getPermissibleZoneEntityDao() {
        return mDaoSession.getPermissibleZoneEntityDao();
    }

    public ProjectInfoEntityDao getProjectInfoEntityDao() {
        return mDaoSession.getProjectInfoEntityDao();
    }


    public PendingProjectDao getPendingProjectDao(){
        return mDaoSession.getPendingProjectDao();
    }

    public ProjectDetonatorDao getProjectDetonatorDao(){
        return mDaoSession.getProjectDetonatorDao();
    }


    public YunnanAuthBobmEntityDao getYunnanAuthBombEntityDao(){
        return mDaoSession.getYunnanAuthBobmEntityDao();
    }

}
