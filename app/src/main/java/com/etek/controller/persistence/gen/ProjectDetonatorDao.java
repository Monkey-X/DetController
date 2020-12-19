package com.etek.controller.persistence.gen;

import java.util.List;
import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;
import org.greenrobot.greendao.query.Query;
import org.greenrobot.greendao.query.QueryBuilder;

import com.etek.controller.persistence.entity.ProjectDetonator;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "PROJECT_DETONATOR".
*/
public class ProjectDetonatorDao extends AbstractDao<ProjectDetonator, Long> {

    public static final String TABLENAME = "PROJECT_DETONATOR";

    /**
     * Properties of entity ProjectDetonator.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Uid = new Property(1, String.class, "uid", false, "UID");
        public final static Property Code = new Property(2, String.class, "code", false, "CODE");
        public final static Property DetId = new Property(3, String.class, "detId", false, "DET_ID");
        public final static Property Relay = new Property(4, int.class, "relay", false, "RELAY");
        public final static Property Status = new Property(5, int.class, "status", false, "STATUS");
        public final static Property HolePosition = new Property(6, String.class, "holePosition", false, "HOLE_POSITION");
        public final static Property DownLoadStatus = new Property(7, int.class, "downLoadStatus", false, "DOWN_LOAD_STATUS");
        public final static Property TestStatus = new Property(8, int.class, "testStatus", false, "TEST_STATUS");
        public final static Property ProjectInfoId = new Property(9, long.class, "projectInfoId", false, "PROJECT_INFO_ID");
    }

    private Query<ProjectDetonator> pendingProject_DetonatorListQuery;

    public ProjectDetonatorDao(DaoConfig config) {
        super(config);
    }
    
    public ProjectDetonatorDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"PROJECT_DETONATOR\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"UID\" TEXT," + // 1: uid
                "\"CODE\" TEXT," + // 2: code
                "\"DET_ID\" TEXT," + // 3: detId
                "\"RELAY\" INTEGER NOT NULL ," + // 4: relay
                "\"STATUS\" INTEGER NOT NULL ," + // 5: status
                "\"HOLE_POSITION\" TEXT," + // 6: holePosition
                "\"DOWN_LOAD_STATUS\" INTEGER NOT NULL ," + // 7: downLoadStatus
                "\"TEST_STATUS\" INTEGER NOT NULL ," + // 8: testStatus
                "\"PROJECT_INFO_ID\" INTEGER NOT NULL );"); // 9: projectInfoId
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"PROJECT_DETONATOR\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, ProjectDetonator entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String uid = entity.getUid();
        if (uid != null) {
            stmt.bindString(2, uid);
        }
 
        String code = entity.getCode();
        if (code != null) {
            stmt.bindString(3, code);
        }
 
        String detId = entity.getDetId();
        if (detId != null) {
            stmt.bindString(4, detId);
        }
        stmt.bindLong(5, entity.getRelay());
        stmt.bindLong(6, entity.getStatus());
 
        String holePosition = entity.getHolePosition();
        if (holePosition != null) {
            stmt.bindString(7, holePosition);
        }
        stmt.bindLong(8, entity.getDownLoadStatus());
        stmt.bindLong(9, entity.getTestStatus());
        stmt.bindLong(10, entity.getProjectInfoId());
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, ProjectDetonator entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String uid = entity.getUid();
        if (uid != null) {
            stmt.bindString(2, uid);
        }
 
        String code = entity.getCode();
        if (code != null) {
            stmt.bindString(3, code);
        }
 
        String detId = entity.getDetId();
        if (detId != null) {
            stmt.bindString(4, detId);
        }
        stmt.bindLong(5, entity.getRelay());
        stmt.bindLong(6, entity.getStatus());
 
        String holePosition = entity.getHolePosition();
        if (holePosition != null) {
            stmt.bindString(7, holePosition);
        }
        stmt.bindLong(8, entity.getDownLoadStatus());
        stmt.bindLong(9, entity.getTestStatus());
        stmt.bindLong(10, entity.getProjectInfoId());
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public ProjectDetonator readEntity(Cursor cursor, int offset) {
        ProjectDetonator entity = new ProjectDetonator( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // uid
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // code
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // detId
            cursor.getInt(offset + 4), // relay
            cursor.getInt(offset + 5), // status
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // holePosition
            cursor.getInt(offset + 7), // downLoadStatus
            cursor.getInt(offset + 8), // testStatus
            cursor.getLong(offset + 9) // projectInfoId
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, ProjectDetonator entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setUid(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setCode(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setDetId(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setRelay(cursor.getInt(offset + 4));
        entity.setStatus(cursor.getInt(offset + 5));
        entity.setHolePosition(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setDownLoadStatus(cursor.getInt(offset + 7));
        entity.setTestStatus(cursor.getInt(offset + 8));
        entity.setProjectInfoId(cursor.getLong(offset + 9));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(ProjectDetonator entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(ProjectDetonator entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(ProjectDetonator entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
    /** Internal query to resolve the "detonatorList" to-many relationship of PendingProject. */
    public List<ProjectDetonator> _queryPendingProject_DetonatorList(long projectInfoId) {
        synchronized (this) {
            if (pendingProject_DetonatorListQuery == null) {
                QueryBuilder<ProjectDetonator> queryBuilder = queryBuilder();
                queryBuilder.where(Properties.ProjectInfoId.eq(null));
                pendingProject_DetonatorListQuery = queryBuilder.build();
            }
        }
        Query<ProjectDetonator> query = pendingProject_DetonatorListQuery.forCurrentThread();
        query.setParameter(0, projectInfoId);
        return query.list();
    }

}
