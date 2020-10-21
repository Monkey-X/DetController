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

import com.etek.controller.persistence.entity.PermissibleZoneEntity;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "PERMISSIBLE_ZONE_ENTITY".
*/
public class PermissibleZoneEntityDao extends AbstractDao<PermissibleZoneEntity, Void> {

    public static final String TABLENAME = "PERMISSIBLE_ZONE_ENTITY";

    /**
     * Properties of entity PermissibleZoneEntity.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Radius = new Property(0, int.class, "radius", false, "RADIUS");
        public final static Property Name = new Property(1, String.class, "name", false, "NAME");
        public final static Property Longitude = new Property(2, double.class, "longitude", false, "LONGITUDE");
        public final static Property Latitude = new Property(3, double.class, "latitude", false, "LATITUDE");
        public final static Property StartTime = new Property(4, java.util.Date.class, "startTime", false, "START_TIME");
        public final static Property StopTime = new Property(5, java.util.Date.class, "stopTime", false, "STOP_TIME");
        public final static Property ProjectInfoId = new Property(6, long.class, "projectInfoId", false, "PROJECT_INFO_ID");
    }

    private Query<PermissibleZoneEntity> projectInfoEntity_PermissibleZoneListQuery;

    public PermissibleZoneEntityDao(DaoConfig config) {
        super(config);
    }
    
    public PermissibleZoneEntityDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"PERMISSIBLE_ZONE_ENTITY\" (" + //
                "\"RADIUS\" INTEGER NOT NULL ," + // 0: radius
                "\"NAME\" TEXT," + // 1: name
                "\"LONGITUDE\" REAL NOT NULL ," + // 2: longitude
                "\"LATITUDE\" REAL NOT NULL ," + // 3: latitude
                "\"START_TIME\" INTEGER," + // 4: startTime
                "\"STOP_TIME\" INTEGER," + // 5: stopTime
                "\"PROJECT_INFO_ID\" INTEGER NOT NULL );"); // 6: projectInfoId
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"PERMISSIBLE_ZONE_ENTITY\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, PermissibleZoneEntity entity) {
        stmt.clearBindings();
        stmt.bindLong(1, entity.getRadius());
 
        String name = entity.getName();
        if (name != null) {
            stmt.bindString(2, name);
        }
        stmt.bindDouble(3, entity.getLongitude());
        stmt.bindDouble(4, entity.getLatitude());
 
        java.util.Date startTime = entity.getStartTime();
        if (startTime != null) {
            stmt.bindLong(5, startTime.getTime());
        }
 
        java.util.Date stopTime = entity.getStopTime();
        if (stopTime != null) {
            stmt.bindLong(6, stopTime.getTime());
        }
        stmt.bindLong(7, entity.getProjectInfoId());
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, PermissibleZoneEntity entity) {
        stmt.clearBindings();
        stmt.bindLong(1, entity.getRadius());
 
        String name = entity.getName();
        if (name != null) {
            stmt.bindString(2, name);
        }
        stmt.bindDouble(3, entity.getLongitude());
        stmt.bindDouble(4, entity.getLatitude());
 
        java.util.Date startTime = entity.getStartTime();
        if (startTime != null) {
            stmt.bindLong(5, startTime.getTime());
        }
 
        java.util.Date stopTime = entity.getStopTime();
        if (stopTime != null) {
            stmt.bindLong(6, stopTime.getTime());
        }
        stmt.bindLong(7, entity.getProjectInfoId());
    }

    @Override
    public Void readKey(Cursor cursor, int offset) {
        return null;
    }    

    @Override
    public PermissibleZoneEntity readEntity(Cursor cursor, int offset) {
        PermissibleZoneEntity entity = new PermissibleZoneEntity( //
            cursor.getInt(offset + 0), // radius
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // name
            cursor.getDouble(offset + 2), // longitude
            cursor.getDouble(offset + 3), // latitude
            cursor.isNull(offset + 4) ? null : new java.util.Date(cursor.getLong(offset + 4)), // startTime
            cursor.isNull(offset + 5) ? null : new java.util.Date(cursor.getLong(offset + 5)), // stopTime
            cursor.getLong(offset + 6) // projectInfoId
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, PermissibleZoneEntity entity, int offset) {
        entity.setRadius(cursor.getInt(offset + 0));
        entity.setName(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setLongitude(cursor.getDouble(offset + 2));
        entity.setLatitude(cursor.getDouble(offset + 3));
        entity.setStartTime(cursor.isNull(offset + 4) ? null : new java.util.Date(cursor.getLong(offset + 4)));
        entity.setStopTime(cursor.isNull(offset + 5) ? null : new java.util.Date(cursor.getLong(offset + 5)));
        entity.setProjectInfoId(cursor.getLong(offset + 6));
     }
    
    @Override
    protected final Void updateKeyAfterInsert(PermissibleZoneEntity entity, long rowId) {
        // Unsupported or missing PK type
        return null;
    }
    
    @Override
    public Void getKey(PermissibleZoneEntity entity) {
        return null;
    }

    @Override
    public boolean hasKey(PermissibleZoneEntity entity) {
        // TODO
        return false;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
    /** Internal query to resolve the "permissibleZoneList" to-many relationship of ProjectInfoEntity. */
    public List<PermissibleZoneEntity> _queryProjectInfoEntity_PermissibleZoneList(long projectInfoId) {
        synchronized (this) {
            if (projectInfoEntity_PermissibleZoneListQuery == null) {
                QueryBuilder<PermissibleZoneEntity> queryBuilder = queryBuilder();
                queryBuilder.where(Properties.ProjectInfoId.eq(null));
                projectInfoEntity_PermissibleZoneListQuery = queryBuilder.build();
            }
        }
        Query<PermissibleZoneEntity> query = projectInfoEntity_PermissibleZoneListQuery.forCurrentThread();
        query.setParameter(0, projectInfoId);
        return query.list();
    }

}
