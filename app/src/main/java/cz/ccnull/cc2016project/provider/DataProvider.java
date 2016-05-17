package cz.ccnull.cc2016project.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import cz.ccnull.cc2016project.BuildConfig;
import cz.ccnull.cc2016project.db.DatabaseHelper;
import cz.ccnull.cc2016project.model.Receiver;

public class DataProvider extends ContentProvider {

    public static final Uri RECEIVERS_URI = Uri.parse("content://" + BuildConfig.APPLICATION_ID + "/receivers");

    private DatabaseHelper mOpenHelper;

    @Override
    public boolean onCreate() {
        mOpenHelper = new DatabaseHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        String code = uri.getLastPathSegment();
        Cursor cursor = mOpenHelper.getReceiverForPaymentCode(code);

        if (cursor != null) {
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
        }
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        mOpenHelper.insertReceiver(values);
        notifyUri(uri);
        return Uri.withAppendedPath(RECEIVERS_URI, values.getAsString(Receiver.COL_PAYMENT_CODE));
    }

    private void notifyUri(Uri uri) {
        getContext().getContentResolver().notifyChange(uri, null);
    }


    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        String code = uri.getLastPathSegment();
        int count = mOpenHelper.deleteReceiversForPaymentCode(code);
        notifyUri(uri);
        return count;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
