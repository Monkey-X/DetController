<<<<<<< HEAD
package com.etek.controller.persistence;

import android.content.Context;

import com.etek.controller.persistence.gen.ChkControllerEntityDao;
import com.etek.controller.persistence.gen.ChkDetonatorEntityDao;
import com.etek.controller.persistence.gen.ControllerEntityDao;
import com.etek.controller.persistence.gen.DaoMaster;
import com.etek.controller.persistence.gen.DetReportEntityDao;
import com.etek.controller.persistence.gen.DetonatorEntityDao;
import com.etek.controller.persistence.gen.ForbiddenZoneEntityDao;
import com.etek.controller.persistence.gen.PermissibleZoneEntityDao;
import com.etek.controller.persistence.gen.ProjectInfoEntityDao;
import com.etek.controller.persistence.gen.ReportEntityDao;
import com.etek.controller.persistence.gen.RptDetonatorEntityDao;

import org.greenrobot.greendao.database.Database;

/**
 * @作者:Sommer
 * @时间:2018-11-01
 * @描述:数据库辅助类
 */
public class DBHelper extends DaoMaster.DevOpenHelper {
    public DBHelper(Context context) {
        super(context, DBManager.DB_NAME, null);
    }
    @Override
    public void onUpgrade(Database db, int oldVersion, int newVersion) {
        //   需要进行数据迁移更新的实体类 ，新增的不用加
        DBMigrationHelper.getInstance().migrate(db,
                ControllerEntityDao.class,
                DetonatorEntityDao.class,
                DetReportEntityDao.class,
                ForbiddenZoneEntityDao.class,
                PermissibleZoneEntityDao.class,
                ProjectInfoEntityDao.class,
                ReportEntityDao.class,
                RptDetonatorEntityDao.class,
                ChkControllerEntityDao.class,
                ChkDetonatorEntityDao.class
        );
    }
}
=======
package com.etek.controller.persistence;

import android.content.Context;

import com.etek.controller.persistence.gen.ChkControllerEntityDao;
import com.etek.controller.persistence.gen.ChkDetonatorEntityDao;
import com.etek.controller.persistence.gen.ControllerEntityDao;
import com.etek.controller.persistence.gen.DaoMaster;
import com.etek.controller.persistence.gen.DetReportEntityDao;
import com.etek.controller.persistence.gen.DetonatorEntityDao;
import com.etek.controller.persistence.gen.ForbiddenZoneEntityDao;
import com.etek.controller.persistence.gen.PermissibleZoneEntityDao;
import com.etek.controller.persistence.gen.ProjectInfoEntityDao;
import com.etek.controller.persistence.gen.ReportEntityDao;
import com.etek.controller.persistence.gen.RptDetonatorEntityDao;

import org.greenrobot.greendao.database.Database;

/**
 * @作者:Sommer
 * @时间:2018-11-01
 * @描述:数据库辅助类
 */
public class DBHelper extends DaoMaster.DevOpenHelper {
    public DBHelper(Context context) {
        super(context, DBManager.DB_NAME, null);
    }
    @Override
    public void onUpgrade(Database db, int oldVersion, int newVersion) {
        //   需要进行数据迁移更新的实体类 ，新增的不用加
        DBMigrationHelper.getInstance().migrate(db,
                ControllerEntityDao.class,
                DetonatorEntityDao.class,
                DetReportEntityDao.class,
                ForbiddenZoneEntityDao.class,
                PermissibleZoneEntityDao.class,
                ProjectInfoEntityDao.class,
                ReportEntityDao.class,
                RptDetonatorEntityDao.class,
                ChkControllerEntityDao.class,
                ChkDetonatorEntityDao.class
        );
    }
}
>>>>>>> 806c842... 雷管组网
