package com.talent.taskmanager.task;

import android.content.Context;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.talent.taskmanager.R;
import com.coal.black.bc.socket.dto.TaskDto;
import com.talent.taskmanager.Utils;

/**
 * Created by acmllaugh on 14-11-20.
 */
public class TaskItemView extends RelativeLayout {


    //private ImageView mTaskIconImgView;
    private TextView mTaskTitleTextView;
    private TextView mTaskStatusTextView;
    private TextView mTaskDateTextView;
    private TextView mContactInfoTextView;
    private TextView mAddressTextView;
    private Context mContext;

    public TaskItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context.getApplicationContext();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
       // mTaskIconImgView = (ImageView) findViewById(R.id.img_task_icon);
        mTaskTitleTextView = (TextView) findViewById(R.id.txt_task_title);
        mTaskStatusTextView = (TextView) findViewById(R.id.txt_task_status);
        mTaskDateTextView = (TextView) findViewById(R.id.txt_task_date);
        mContactInfoTextView = (TextView) findViewById(R.id.txt_contact_info);
        mAddressTextView = (TextView) findViewById(R.id.txt_address);
    }

    public void bindModel(TaskDto task) {
        mTaskTitleTextView.setText(task.getName());
        mTaskDateTextView.setText(Utils.getDateStringFromLong(task.getGrantTime(), mContext));
        mContactInfoTextView.setText(task.getContactInfo());
        mAddressTextView.setText(task.getAddress());
        showTaskStatus(task);
    }

    private void showTaskStatus(TaskDto task) {
        boolean isTaskUnread = false;
        String urgentText = task.isUrgent()? "(Urgent)":"";
        if (task.isUrgent()) {
            Log.d("acmllaugh1", "showTaskStatus (line 56): task : " + task.getName() + " is urgent.");
        }
        switch (task.getUserTaskStatus()) {
            case 1:
                mTaskStatusTextView.setTextColor(mContext.getResources().getColor(android.R.color.holo_red_light));
                mTaskStatusTextView.setText(mContext.getString(R.string.un_read) + urgentText);
                isTaskUnread = true;
                break;
            case 2:
                mTaskStatusTextView.setTextColor(mContext.getResources().getColor(android.R.color.holo_blue_light));
                mTaskStatusTextView.setText(mContext.getString(R.string.has_been_read) + urgentText);
                break;
            case 3:
                mTaskStatusTextView.setTextColor(mContext.getResources().getColor(android.R.color.holo_green_light));
                mTaskStatusTextView.setText(mContext.getString(R.string.task_processing) + urgentText);
                break;
        }
        setTextBold(isTaskUnread || task.isUrgent());
    }

    private void setTextBold(boolean needBold) {
        for (int i = getChildCount()-1; i >= 0; i--) {
            View view = getChildAt(i);
            if (view instanceof TextView) {
                TextView textView = (TextView) view;
                TextPaint tp = textView.getPaint();
                tp.setFakeBoldText(needBold);
            }
        }
    }

}
