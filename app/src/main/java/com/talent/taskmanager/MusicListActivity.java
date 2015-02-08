package com.talent.taskmanager;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;


public class MusicListActivity extends Activity implements LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener {

    public static final int AUDIO_LOAD_ID = 1;
    public static final int SELECT_MAX = 5; // This is the max item numbers that user can choose in one time.
    private AudioListAdapter mAdapter;
    private ListView mListView;
    private HashSet<String> mSelectedItems = new HashSet<String>();
    private CompoundButton.OnCheckedChangeListener mCheckBoxChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            String audioPath = (String) (buttonView.getTag());
            if (isChecked) {
//                Toast.makeText(MainActivity.this, audioPath, Toast.LENGTH_SHORT).show();
                if (mSelectedItems.size() >= SELECT_MAX) {
                    buttonView.setOnCheckedChangeListener(null);
                    buttonView.setChecked(false);
                    buttonView.setOnCheckedChangeListener(mCheckBoxChangeListener);
                    Toast.makeText(MusicListActivity.this, String.format(getResources().getString(R.string.max_num_record_toast), SELECT_MAX), Toast.LENGTH_SHORT).show();
                    return;
                }
                mSelectedItems.add(audioPath);
            } else {
                mSelectedItems.remove(audioPath);
//                Toast.makeText(MainActivity.this, mSelectedItems.size() + "", Toast.LENGTH_SHORT).show();
            }
            updateSelectedCount(mSelectedItems.size());
        }
    };
    private Button mCancelButton;
    private Button mUploadButton;

    private void updateSelectedCount(int size) {
        mUploadButton.setText(getString(R.string.action_send) + "(" + size + "/" + SELECT_MAX + ")");
        mUploadButton.setEnabled(size > 0 ? true : false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_music);
        initTitleBar();
        initViews();
    }

    private void initTitleBar() {
        mCancelButton = (Button) findViewById(R.id.btn_cancel);
        mUploadButton = (Button) findViewById(R.id.btn_confirm);
        updateSelectedCount(0);
        mCancelButton.setOnClickListener(this);
        mUploadButton.setOnClickListener(this);
    }

    private void initViews() {
        mListView = (ListView) findViewById(R.id.list_audio);
        mAdapter = new AudioListAdapter(getApplicationContext(), null, 0);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                openAudioItem(position);
            }
        });
        getLoaderManager().restartLoader(AUDIO_LOAD_ID, null, this);
    }

    private void openAudioItem(int position) {
        long itemID = mAdapter.getItemId(position);
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        uri = Uri.withAppendedPath(uri, Long.toString(itemID));
//        Toast.makeText(this, uri.toString(), Toast.LENGTH_SHORT).show();
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "audio/*");
        startActivity(intent);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = new String[]{MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.DATE_MODIFIED,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.DATA};
        String timeDescOrder = MediaStore.Audio.Media.DATE_MODIFIED + " DESC";
        return new CursorLoader(getApplicationContext(), uri, projection, null, null, timeDescOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cancel:
                this.finish();
                break;
            case R.id.btn_confirm:
                sendSelectedResult();
                this.finish();
                break;
        }
    }

    private void sendSelectedResult() {
        // All selected paths are saved in selectedItems set.
        // We may send message to upload them here.
        Intent intent = new Intent();
        intent.putExtra("paths", mSelectedItems);
        setResult(Activity.RESULT_OK, intent);
    }

    private class AudioListAdapter extends CursorAdapter {

        private AudioListAdapter(Context context, Cursor c, int flags) {
            super(context, c, flags);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            View view = getLayoutInflater().inflate(R.layout.item_audio, null);
            Holder holder = new Holder();
            holder.audioNameTextView = (TextView) view.findViewById(R.id.txt_audio_name);
            holder.audioPathTextView = (TextView) view.findViewById(R.id.txt_audio_path);
            holder.lastModifiedTextView = (TextView) view.findViewById(R.id.txt_last_modified);
            holder.selectCheckBox = (CheckBox) view.findViewById(R.id.chbx_item);
            view.setTag(holder);
            return view;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            Holder holder = (Holder) view.getTag();
            String audioName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
            String audioPath = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
            long audioModifiedTime = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DATE_MODIFIED));
            File file = new File(audioPath);
            holder.audioNameTextView.setText(audioName);
            holder.audioPathTextView.setText(audioPath);
            holder.lastModifiedTextView.setText(getTimeString(file.lastModified()));
            holder.selectCheckBox.setTag(audioPath);
            holder.selectCheckBox.setOnCheckedChangeListener(null);
            if (isSelected(audioPath)) {
                holder.selectCheckBox.setChecked(true);
            } else holder.selectCheckBox.setChecked(false);
            holder.selectCheckBox.setOnCheckedChangeListener(mCheckBoxChangeListener);
        }
    }

    private String getTimeString(long audioModifiedTime) {
//        Date date = new Date(audioModifiedTime);
//        java.text.DateFormat dateFormat =
//                android.text.format.DateFormat.getDateFormat(getApplicationContext());
//        return dateFormat.format(date);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentTime = format.format(new Date(audioModifiedTime));
        return currentTime;
    }

    private boolean isSelected(String audioPath) {
        return mSelectedItems.contains(audioPath);
    }

    private class Holder {
        TextView audioNameTextView;
        TextView audioPathTextView;
        TextView lastModifiedTextView;
        CheckBox selectCheckBox;
    }
}
