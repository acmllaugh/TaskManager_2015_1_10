package com.talent.taskmanager.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.talent.taskmanager.task.TaskCommitInfo;

import java.sql.SQLException;

/**
 * Created by acmllaugh on 15-2-13.
 */
public class TaskCommitInfoDAO {

    private SQLiteDatabase mDatabase;
    private DBHelper mDBHelper;
    private String[] mCommitColums = {
            DBHelper.FIELD_NEED_VISIT_AGAIN, DBHelper.FIELD_ACTUAL_VISTOR, DBHelper.FIELD_REPORT
    };

    public TaskCommitInfoDAO(Context context) {
        mDBHelper = new DBHelper(context);
    }

    public void open() throws SQLException {
        mDatabase = mDBHelper.getWritableDatabase();
    }

    public void close(){
        mDBHelper.close();
    }

    public long saveTaskCommitInfo(TaskCommitInfo commitInfo) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.FIELD_TASK_ID, commitInfo.getTaskID());
        values.put(DBHelper.FIELD_USER_ID, commitInfo.getUserID());
        if (commitInfo.isNeedVisitAgain()) {
            values.put(DBHelper.FIELD_NEED_VISIT_AGAIN, 1);
        } else {
            values.put(DBHelper.FIELD_NEED_VISIT_AGAIN, 0);
        }
        values.put(DBHelper.FIELD_ACTUAL_VISTOR, commitInfo.getRealVisitUser());
        values.put(DBHelper.FIELD_REPORT, commitInfo.getVisitReport());
        return mDatabase.insert(DBHelper.TABLE_TASK_COMMIT, null, values);
    }

    public TaskCommitInfo getTaskCommitInfo(int userID, int taskID) {
        String selection = DBHelper.FIELD_USER_ID + "=" + userID + " AND " + DBHelper.FIELD_TASK_ID + "=" + taskID;
        Cursor cursor = mDatabase.query(DBHelper.TABLE_TASK_COMMIT, mCommitColums, selection, null, null, null, null);
        if (cursor.moveToNext()) {
            int needVisitAgainInt = cursor.getInt(cursor.getColumnIndex(DBHelper.FIELD_NEED_VISIT_AGAIN));
            String actualVistor = cursor.getString(cursor.getColumnIndex(DBHelper.FIELD_ACTUAL_VISTOR));
            String report = cursor.getString(cursor.getColumnIndex(DBHelper.FIELD_REPORT));
            boolean needVisitAgain = (needVisitAgainInt != 0);
            return new TaskCommitInfo(taskID, userID, needVisitAgain, report, actualVistor);
        }
        return null;
    }

    public long deleteTaskCommitInfo(TaskCommitInfo commitInfo) {
        return mDatabase.delete(DBHelper.TABLE_TASK_COMMIT, DBHelper.FIELD_TASK_ID + "=" + commitInfo.getTaskID()
                + " AND " + DBHelper.FIELD_USER_ID + "=" + commitInfo.getUserID(), null);
    }

    public long updateTaskCommitInfo(TaskCommitInfo commitInfo) {
        ContentValues values = new ContentValues();
        if (commitInfo.isNeedVisitAgain()) {
            values.put(DBHelper.FIELD_NEED_VISIT_AGAIN, 1);
        } else {
            values.put(DBHelper.FIELD_NEED_VISIT_AGAIN, 0);
        }
        values.put(DBHelper.FIELD_ACTUAL_VISTOR, commitInfo.getRealVisitUser());
        values.put(DBHelper.FIELD_REPORT, commitInfo.getVisitReport());
        return mDatabase.update(DBHelper.TABLE_TASK_COMMIT, values
                ,DBHelper.FIELD_TASK_ID + "=" + commitInfo.getTaskID()
                        + " AND " + DBHelper.FIELD_USER_ID + "=" + commitInfo.getUserID()
                ,null);
    }

}
