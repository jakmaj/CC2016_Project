package cz.ccnull.cc2016project.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import cz.ccnull.cc2016project.model.Receiver;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "data.db";
    private static final int DB_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Receiver.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public long insertReceiver(ContentValues contentValues) {
        return getWritableDatabase().insert(Receiver.TABLE_NAME, null, contentValues);
    }

    public int deleteReceivers() {
        return getWritableDatabase().delete(Receiver.TABLE_NAME, null, null);
    }

    public Cursor getReceiverForPaymentCode(String code) {
        return getWritableDatabase().query(Receiver.TABLE_NAME, null, Receiver.COL_PAYMENT_CODE + " = ?", new String[]{code}, null, null, null);
    }


}
