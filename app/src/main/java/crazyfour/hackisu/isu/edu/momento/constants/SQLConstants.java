package crazyfour.hackisu.isu.edu.momento.constants;

/**
 * Created by nishanthsivakumar on 2/20/16.
 */
public class SQLConstants {

    public static final String CREATE_CALL_ENTRY_TABLE = "CREATE TABLE EVENT_ENTRY (EVENT_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "EVENT_NAME VARCHAR(20), EVENT_DESC VARCHAR(20), START_TIME DATETIME, END_TIME DATETIME);";

    public static final String DELETE_CALL_ENTRY_TABLE ="DROP TABLE IF EXISTS EVENT_ENTRY";


}
