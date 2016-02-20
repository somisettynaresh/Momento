package crazyfour.hackisu.isu.edu.momento.utilities;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import crazyfour.hackisu.isu.edu.momento.constants.SQLConstants;

/**
 * Created by nishanthsivakumar on 2/20/16.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Momento.db";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(SQLConstants.CREATE_EVENT_ENTRY_TABLE);
        db.execSQL(SQLConstants.CREATE_EVENT_BACKUP_TIME_TABLE);
        db.execSQL(SQLConstants.CREATE_LOCATION_DATA);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQLConstants.DELETE_EVENT_ENTRY_TABLE);
        db.execSQL(SQLConstants.DELETE_EVENT_BACKUP_TIME_TABLE);
        db.execSQL(SQLConstants.DELETE_LOCATION_DATA);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
