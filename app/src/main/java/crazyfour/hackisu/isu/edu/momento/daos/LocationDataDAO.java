package crazyfour.hackisu.isu.edu.momento.daos;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;

import crazyfour.hackisu.isu.edu.momento.models.LocationData;

/**
 * Created by Sriram on 20-02-2016.
 */
public class LocationDataDAO {

    private SQLiteDatabase db;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public LocationDataDAO(SQLiteDatabase db){
        this.db = db;
    }

    public long insert(LocationData locationData){
        ContentValues values = new ContentValues();
        //
        values.put("LOCATION_TIME", dateFormat.format(locationData.getLocationDate()));

        String selection = "LOCATION = ?";

        long newRowId;
        newRowId = db.update("LOCATION_DATA", values, selection, new String[]{""+locationData.getFeatureName()});

        if(newRowId <=0){
            values.put("LOCATION",locationData.getFeatureName());
            newRowId = db.insert("LOCATION_DATA",null,values);
        }
        return newRowId;
    }

    public LocationData getLastLocation() {
        String[] projection = {"LOCATION","LOCATION_TIME"};
        Cursor c = db.query("LOCATION_DATA",projection,null,null, null, null,"LOCATION_TIME DESC", "1");
        boolean stat = c.moveToFirst();
        LocationData locationData = null;
        if(stat) {
            try {
                locationData = new LocationData(c.getString(0), dateFormat.parse(c.getString(1)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        c.close();
        return locationData;
    }
}
