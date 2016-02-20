package crazyfour.hackisu.isu.edu.momento.daos;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Sriram on 20-02-2016.
 */
public class LocationDataDAO {

    private SQLiteDatabase db;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public LocationDataDAO(SQLiteDatabase db){
        this.db = db;
    }

    public long insert(String location, Date locationTime){
        ContentValues values = new ContentValues();
        //
        values.put("LOCATION_TIME", dateFormat.format(locationTime));

        String selection = "LOCATION = ?";

        long newRowId;
        newRowId = db.update("LOCATION_DATA", values, selection, new String[]{""+location});

        if(newRowId <=0){
            values.put("LOCATION",location);
            newRowId = db.insert("LOCATION_DATA",null,values);
        }

        return newRowId;
    }
}
