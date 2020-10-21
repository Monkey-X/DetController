package com.etek.controller.persistence.gen;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.etek.controller.persistence.entity.DetReportEntity;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "DET_REPORT_ENTITY".
*/
public class DetReportEntityDao extends AbstractDao<DetReportEntity, Void> {

    public static final String TABLENAME = "DET_REPORT_ENTITY";

    /**
     * Properties of entity DetReportEntity.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property ControllerId = new Property(0, String.class, "controllerId", false, "CONTROLLER_ID");
        public final static Property Longitude = new Property(1, double.class, "longitude", false, "LONGITUDE");
        public final static Property Latitude = new Property(2, double.class, "latitude", false, "LATITUDE");
        public final static Property BlastTime = new Property(3, java.util.Date.class, "blastTime", false, "BLAST_TIME");
        public final static Property IdCode = new Property(4, String.class, "idCode", false, "ID_CODE");
        public final static Property ContractId = new Property(5, String.class, "contractId", false, "CONTRACT_ID");
        public final static Property ProjectId = new Property(6, String.class, "projectId", false, "PROJECT_ID");
        public final static Property Status = new Property(7, int.class, "status", false, "STATUS");
        public final static Property Token = new Property(8, String.class, "token", false, "TOKEN");
    }


    public DetReportEntityDao(DaoConfig config) {
        super(config);
    }
    
    public DetReportEntityDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"DET_REPORT_ENTITY\" (" + //
                "\"CONTROLLER_ID\" TEXT," + // 0: controllerId
                "\"LONGITUDE\" REAL NOT NULL ," + // 1: longitude
                "\"LATITUDE\" REAL NOT NULL ," + // 2: latitude
                "\"BLAST_TIME\" INTEGER," + // 3: blastTime
                "\"ID_CODE\" TEXT," + // 4: idCode
                "\"CONTRACT_ID\" TEXT," + // 5: contractId
                "\"PROJECT_ID\" TEXT," + // 6: projectId
                "\"STATUS\" INTEGER NOT NULL ," + // 7: status
                "\"TOKEN\" TEXT);"); // 8: token
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"DET_REPORT_ENTITY\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, DetReportEntity entity) {
        stmt.clearBindings();
 
        String controllerId = entity.getControllerId();
        if (controllerId != null) {
            stmt.bindString(1, controllerId);
        }
        stmt.bindDouble(2, entity.getLongitude());
        stmt.bindDouble(3, entity.getLatitude());
 
        java.util.Date blastTime = entity.getBlastTime();
        if (blastTime != null) {
            stmt.bindLong(4, blastTime.getTime());
        }
 
        String idCode = entity.getIdCode();
        if (idCode != null) {
            stmt.bindString(5, idCode);
        }
 
        String contractId = entity.getContractId();
        if (contractId != null) {
            stmt.bindString(6, contractId);
        }
 
        String projectId = entity.getProjectId();
        if (projectId != null) {
            stmt.bindString(7, projectId);
        }
        stmt.bindLong(8, entity.getStatus());
 
        String token = entity.getToken();
        if (token != null) {
            stmt.bindString(9, token);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, DetReportEntity entity) {
        stmt.clearBindings();
 
        String controllerId = entity.getControllerId();
        if (controllerId != null) {
            stmt.bindString(1, controllerId);
        }
        stmt.bindDouble(2, entity.getLongitude());
        stmt.bindDouble(3, entity.getLatitude());
 
        java.util.Date blastTime = entity.getBlastTime();
        if (blastTime != null) {
            stmt.bindLong(4, blastTime.getTime());
        }
 
        String idCode = entity.getIdCode();
        if (idCode != null) {
            stmt.bindString(5, idCode);
        }
 
        String contractId = entity.getContractId();
        if (contractId != null) {
            stmt.bindString(6, contractId);
        }
 
        String projectId = entity.getProjectId();
        if (projectId != null) {
            stmt.bindString(7, projectId);
        }
        stmt.bindLong(8, entity.getStatus());
 
        String token = entity.getToken();
        if (token != null) {
            stmt.bindString(9, token);
        }
    }

    @Override
    public Void readKey(Cursor cursor, int offset) {
        return null;
    }    

    @Override
    public DetReportEntity readEntity(Cursor cursor, int offset) {
        DetReportEntity entity = new DetReportEntity( //
            cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0), // controllerId
            cursor.getDouble(offset + 1), // longitude
            cursor.getDouble(offset + 2), // latitude
            cursor.isNull(offset + 3) ? null : new java.util.Date(cursor.getLong(offset + 3)), // blastTime
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // idCode
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // contractId
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // projectId
            cursor.getInt(offset + 7), // status
            cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8) // token
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, DetReportEntity entity, int offset) {
        entity.setControllerId(cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0));
        entity.setLongitude(cursor.getDouble(offset + 1));
        entity.setLatitude(cursor.getDouble(offset + 2));
        entity.setBlastTime(cursor.isNull(offset + 3) ? null : new java.util.Date(cursor.getLong(offset + 3)));
        entity.setIdCode(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setContractId(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setProjectId(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setStatus(cursor.getInt(offset + 7));
        entity.setToken(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
     }
    
    @Override
    protected final Void updateKeyAfterInsert(DetReportEntity entity, long rowId) {
        // Unsupported or missing PK type
        return null;
    }
    
    @Override
    public Void getKey(DetReportEntity entity) {
        return null;
    }

    @Override
    public boolean hasKey(DetReportEntity entity) {
        // TODO
        return false;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
