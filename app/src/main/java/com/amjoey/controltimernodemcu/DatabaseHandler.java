package com.amjoey.controltimernodemcu;


/**
 * Created by Administrator on 7/4/2561.
 */
        import android.content.ContentValues;
        import android.content.Context;
        import android.database.Cursor;
        import android.database.sqlite.SQLiteDatabase;
        import android.database.sqlite.SQLiteOpenHelper;
        import android.util.Log;


public class DatabaseHandler extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "mysetschedule.db";
    static final String TABLE_NAME = "mysettime";

    private static final int DATABASE_VERSION = 2;
    static final String COLUMN_TIME_ON = "timeON";
    static final String COLUMN_TIME_OFF = "timeOFF";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE "
                + TABLE_NAME
                + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_TIME_ON + " INTEGER, " + COLUMN_TIME_OFF + " INTEGER);");
    }


    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TABLE_NAME, "Upgrading database from version " + oldVersion + " to "
                + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }


    public boolean  addRecord(int timeON, int timeOFF) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_TIME_ON, timeON);
        values.put(COLUMN_TIME_OFF, timeOFF);


        long row = db.insert(DatabaseHandler.TABLE_NAME, null, values);
        Log.d(TABLE_NAME,"inserted at row " + row + " " + timeON + timeOFF);

        db.close();
        if(row == -1)
            return false;
        else
            return true;
    }

    public String getRecord(long id) {
        String data = null;

        SQLiteDatabase db = this.getWritableDatabase();
        String[] columns = { "_id", "timeON", "timeOFF"};
        Cursor c = db.query(TABLE_NAME,
                columns,
                //null,
                "_id=?", new String[] { String.valueOf(id) },
                null, null, null, null);

        Log.d(TABLE_NAME,"recID "+ id + " count " + c.getCount() );

        if (c != null) {
            Log.d(TABLE_NAME,"recID "+ c);
            if (c.moveToFirst()) {
                int idCol= c.getColumnIndex("_id");
                int timeONCol= c.getColumnIndex("timeON");
                int timeOFFCol= c.getColumnIndex("timeOFF");
                String strId = Integer.toString(c.getInt(idCol));
                String strTimeON = Integer.toString(c.getInt(timeONCol));
                String strTimeOFF = Integer.toString(c.getInt(timeOFFCol));
                data = "id "+ strId + "\nTimeON "+ strTimeON + "\nTimeOFF " + strTimeOFF + "\n";
            }
        }
        c.close();
        return data;
    }


    public Cursor getAllRecord() {

        SQLiteDatabase db = this.getWritableDatabase();
        String[] columns = { "_id", "timeON", "timeOFF"};
        Cursor cur = db.query(TABLE_NAME,
                columns,
                null,
                null, null, null, null);

        Log.d(TABLE_NAME, " count " + cur.getCount() );
        return cur;
    }
    public Cursor getEditRecord(long id) {

        SQLiteDatabase db = this.getWritableDatabase();
        String[] columns = { "_id", "timeON", "timeOFF"};
        Cursor cur = db.query(TABLE_NAME,
                columns,
                //null,
                "_id=?", new String[] { String.valueOf(id) },
                null, null, null, null);

        Log.d(TABLE_NAME, " count " + cur.getCount() );

        return cur;
    }

    public Cursor getSearchedRecord(String search) {

        SQLiteDatabase db = this.getWritableDatabase();
        String[] columns = { "_id", "timeON", "timeOFF"};
        Cursor cur = db.query(TABLE_NAME,
                columns,
                "time LIKE ?", new String[]{"%" + search + "%"},
                null, null, null, null);

        Log.d(TABLE_NAME, " count " + cur.getCount() );

        return cur;
    }


    public int getRecordCount() {
        String countQuery = "SELECT _id FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cur = db.rawQuery(countQuery, null);
        return cur.getCount();
    }

    public boolean updateTime(long recID, int timeON, int timeOFF) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_TIME_ON, timeON);
        values.put(COLUMN_TIME_OFF, timeOFF);
        long row= db.update(TABLE_NAME, values, "_id = ?",
                new String[] { String.valueOf(recID) });
        db.close();
        if(row == -1)
            return false;
        else
            return true;
    }


    public boolean deleteRecord(long recID) {
        SQLiteDatabase db = this.getWritableDatabase();
        long row = db.delete(TABLE_NAME, "_id = ?",
                new String[] { String.valueOf(recID) });
        db.close();


        if(row == -1)
            return false;
        else
            return true;
    }

}
