package com.talent.taskmanager.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.talent.taskmanager.file.FileInfo;

/**
 * Created by chris on 14-12-29.
 */
public class UploadFileDao {
    DBHelper mDbHelper = null;

    public UploadFileDao(Context context) {
        this.mDbHelper = new DBHelper(context);
    }

    public void insertUploadFileInfo(FileInfo fileInfo) {
        int taskId = fileInfo.getTaskId();
        int userId = fileInfo.getUserId();
        boolean isPicture = fileInfo.isPicture();
        String filePath = fileInfo.getFilePath();
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBHelper.FIELD_TASK_ID, taskId);
        values.put(DBHelper.FIELD_USER_ID, userId);
        values.put(DBHelper.FIELD_IS_PICTURE, isPicture ? 1 : 0);
        values.put(DBHelper.FIELD_FILE_PATH, filePath);
        values.put(DBHelper.FIELD_RESULT, 0);
        db.insert(DBHelper.TABLE_FILES, null, values);
        db.close();
    }

    public void updateUploadStatus(String filePath) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        String where = DBHelper.FIELD_FILE_PATH + "=?";
        String[] whereValue = {filePath};
        ContentValues values = new ContentValues();
        values.put(DBHelper.FIELD_RESULT, 1);
        db.update(DBHelper.TABLE_FILES, values, where, whereValue);
        db.close();
    }

    public FileInfo getUnfinishedFiles() {
        FileInfo fileInfo = null;
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String selection = DBHelper.FIELD_RESULT + "=?";
        String[] selectionArgs = {"0"};
        String orderBy = DBHelper.FIELD_ID + " DESC";
        Cursor cursor = db.query(DBHelper.TABLE_FILES, null, selection, selectionArgs, null, null, orderBy);
        if (cursor.moveToFirst()) {
            int userId = cursor.getInt(cursor.getColumnIndex(DBHelper.FIELD_USER_ID));
            int taskId = cursor.getInt(cursor.getColumnIndex(DBHelper.FIELD_TASK_ID));
            String filePath = cursor.getString(cursor.getColumnIndex(DBHelper.FIELD_FILE_PATH));
            boolean isPicture = cursor.getInt(cursor.getColumnIndex(DBHelper.FIELD_IS_PICTURE)) == 1 ? true : false;
            int uploadResult = cursor.getInt(cursor.getColumnIndex(DBHelper.FIELD_RESULT));
            fileInfo = new FileInfo(userId, taskId, filePath, isPicture, uploadResult, 1);
            Log.d("Chris", "getUnfinishedFiles, unFinished file: " + fileInfo.toString());
        } else {
            Log.d("Chris", "getUnfinishedFiles, null");
        }
        cursor.close();
        db.close();
        return fileInfo;
    }

    public int getUnfinishedFilesCount() {
        int count = 0;
        FileInfo fileInfo = null;
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String selection = DBHelper.FIELD_RESULT + "=?";
        String[] selectionArgs = {"0"};
        Cursor cursor = db.query(DBHelper.TABLE_FILES, null, selection, selectionArgs, null, null, null);
        count = cursor.getCount();
        cursor.close();
        db.close();
        return count;
    }

    public boolean isExsitInDatabase(FileInfo fileInfo) {
        boolean isExsit = false;
        String userId = String.valueOf(fileInfo.getUserId());
        String taskId = String.valueOf(fileInfo.getTaskId());
        String filePath = fileInfo.getFilePath();
        String isPicture = fileInfo.isPicture() ? "1" : "0";
        String uploadResult = String.valueOf(fileInfo.getUploadResult());
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String selection = String.format("%s = ? AND %s = ? AND %s = ? AND %s = ? AND %s = ? ",
                DBHelper.FIELD_USER_ID, DBHelper.FIELD_TASK_ID, DBHelper.FIELD_FILE_PATH,
                DBHelper.FIELD_IS_PICTURE, DBHelper.FIELD_RESULT);
        String[] selectionArgs = {userId, taskId, filePath, isPicture, uploadResult};
        Cursor cursor = db.query(DBHelper.TABLE_FILES, null, selection, selectionArgs, null, null, null);
        if (cursor.moveToFirst()) {
            isExsit = true;
        }
        cursor.close();
        db.close();
        Log.d("Chris", "isExsitInDatabase, " +  isExsit);
        return isExsit;
    }

}
