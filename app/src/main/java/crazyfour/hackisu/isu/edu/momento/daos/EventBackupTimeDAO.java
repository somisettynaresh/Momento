package crazyfour.hackisu.isu.edu.momento.daos;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by nishanthsivakumar on 2/20/16.
 */
public class EventBackupTimeDAO {

    private SQLiteDatabase db;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public EventBackupTimeDAO(SQLiteDatabase db){
        this.db = db;
    }

    public long insert(int eventType, Date backupTime){
        ContentValues values = new ContentValues();
        //
        values.put("BACKUP_TIME", dateFormat.format(backupTime));

        String selection = "EVENT_TYPE = ?";

        long newRowId;
        newRowId = db.update("EVENT_BACKUP_TIME", values, selection, new String[]{""+eventType});

        if(newRowId <=0){
            values.put("EVENT_TYPE",eventType);
            newRowId = db.insert("EVENT_BACKUP_TIME",null,values);
        }

        return newRowId;

    }

    public Date getLastBackupTime(int eventType) throws ParseException {

        Date lastBackupTime = new Date();

        String[] projection = {"BACKUP_TIME"};

        String selection = "EVENT_TYPE = ?";

        Cursor c = db.query("EVENT_BACKUP_TIME",projection,selection,new String[]{""+eventType},null,null,null);
        if(c.moveToFirst()) lastBackupTime = dateFormat.parse(c.getString(0));

        return lastBackupTime;
    }
}
