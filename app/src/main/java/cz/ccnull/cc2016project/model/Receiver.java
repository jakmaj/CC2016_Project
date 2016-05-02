package cz.ccnull.cc2016project.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.provider.BaseColumns;

public class Receiver {
    public static final String COL_ID = BaseColumns._ID;
    public static final String COL_PAYMENT_CODE = "payment_code";
    public static final String COL_USER_ID = "user_id";
    public static final String COL_NAME = "name";
    public static final String TABLE_NAME = "receivers";

    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
            COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COL_PAYMENT_CODE + " TEXT, " +
            COL_USER_ID + " TEXT, " +
            COL_NAME + " TEXT)";

    private long id;
    private String paymentCode;
    private String userId;
    private String name;

    public Receiver(Cursor cursor) {
        this.id = cursor.getLong(cursor.getColumnIndex(COL_ID));
        this.paymentCode = cursor.getString(cursor.getColumnIndex(COL_PAYMENT_CODE));
        this.userId = cursor.getString(cursor.getColumnIndex(COL_USER_ID));
        this.name = cursor.getString(cursor.getColumnIndex(COL_NAME));
    }

    public ContentValues getContentValues() {
        ContentValues cv = new ContentValues();
        cv.put(COL_PAYMENT_CODE, paymentCode);
        cv.put(COL_USER_ID, userId);
        cv.put(COL_NAME, name);
        return cv;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPaymentCode() {
        return paymentCode;
    }

    public String getUserId() {
        return userId;
    }
}
