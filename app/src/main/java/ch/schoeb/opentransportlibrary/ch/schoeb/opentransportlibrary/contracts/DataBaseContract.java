package ch.schoeb.opentransportlibrary.ch.schoeb.opentransportlibrary.contracts;

import android.provider.BaseColumns;

/**
 * Created by Helga on 18-Feb-17.
 */

public final class DataBaseContract {

    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private DataBaseContract() {
    }

    /* Inner class that defines the table contents */
    public static class FavoritEntry implements BaseColumns {
        public static final String TABLE_NAME = "favorites";
        public static final String COLUMN_NAME_FROM = "from_station";
        public static final String COLUMN_NAME_TO = "to_station";
        public static final String COLUMN_NAME_DATE = "date";
        public static final String COLUMN_NAME_TIME = "time";
        public static final String COLUMN_NAME_IS_ARRIVAL_TIME = "isArrivalTime";
    }

        public static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE IF NOT EXISTS " + FavoritEntry.TABLE_NAME + " (" +
                        FavoritEntry._ID + " INTEGER PRIMARY KEY," +
                        FavoritEntry.COLUMN_NAME_FROM + " TEXT," +
                        FavoritEntry.COLUMN_NAME_TO + " TEXT," +
                        FavoritEntry.COLUMN_NAME_DATE + " TEXT," +
                        FavoritEntry.COLUMN_NAME_TIME + " TEXT," +
                        FavoritEntry.COLUMN_NAME_IS_ARRIVAL_TIME + " INT)";

        public static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + FavoritEntry.TABLE_NAME;
}
