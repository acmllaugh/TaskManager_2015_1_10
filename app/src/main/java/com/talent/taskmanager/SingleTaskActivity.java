package com.talent.taskmanager;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.coal.black.bc.socket.client.ClientGlobal;
import com.coal.black.bc.socket.client.handlers.CommitTaskHandler;
import com.coal.black.bc.socket.client.handlers.UserTaskStatusChangeHandler;
import com.coal.black.bc.socket.client.returndto.CommitTaskResult;
import com.coal.black.bc.socket.client.returndto.UserTaskStatusChangeResult;
import com.coal.black.bc.socket.common.UserTaskStatusCommon;
import com.coal.black.bc.socket.dto.TaskDto;
import com.coal.black.bc.socket.exception.ExceptionBase;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.talent.taskmanager.dada.UploadFileDao;
import com.talent.taskmanager.file.FileInfo;
import com.talent.taskmanager.file.FileOperationUtils;
import com.talent.taskmanager.file.UploadFileThread;
import com.talent.taskmanager.network.NetworkState;

import java.io.File;
import java.io.IOException;
import java.util.List;

import de.greenrobot.event.EventBus;


public class SingleTaskActivity extends Activity {

    private static final int REQ_CODE_CAPTURE_PICTURE = 101;
    private static final int REQ_CODE_SELECT_PICTURE = 102;
    private static final int REQ_CODE_RECORD_AUDIO = 103;
    private static final int REQ_CODE_SELECT_AUDIO = 104;

    private static final int BUTTON_CAPTURE_IMAGE = R.id.btn_capture_image;
    private static final int BUTTON_SELECT_IMAGE = R.id.btn_select_image;
    private static final int BUTTON_RECORD_AUDIO = R.id.btn_record_audio;
    private static final int BUTTON_SELECT_AUDIO = R.id.btn_select_audio;

    private static final String FILE_TYPE_IMAGE = "image/*";
    private static final String FILE_TYPE_AUDIO = "audio/*";
    private static final String TEMP_FILE_NAME = "image.jpeg";

    private static final int MSG_CHANGE_TASK_STATUS = 1;
    private static final int MSG_UPLOAD_FILE_SUCCEED = 2;
    public static final int COMMIT_TASK_RESULT = 3;

    private Button mBtnCapture = null;
    private Button mBtnSelectImage = null;
    private Button mBtnSoundRecord = null;
    private Button mBtnSelectAudio = null;
    private LinearLayout mGridImages = null;
    private LinearLayout mGridAudios = null;
    private String mTaskFilePath = null;
    private FileInfo mFileInfo = null;
    private UploadFileDao mUploadFileDao = null;
    private UploadFileListener mUploadListener = null;
    public static final String DIRECTORY = Environment.getExternalStorageDirectory() + "/TaskFiles";
    private Menu mMenu;
    private ImageLoader mThumbnailLoader;

    private EventBus mEventBus = EventBus.getDefault();
    private TaskDto mTask;
    private TextView mTaskTitleView;
    private TextView mDetailButton;
    private AlertDialog mDetailDialog;
    private ProgressDialog mProgressDialog;
    private Toast mToast;
    private Handler mResultHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_CHANGE_TASK_STATUS: {
                    Utils.dissmissProgressDialog(mProgressDialog);
                    if (msg.obj instanceof UserTaskStatusChangeResult) {
                        UserTaskStatusChangeResult result = (UserTaskStatusChangeResult) msg.obj;
                        //TODO : Whether success or not, reload the task from server(but consider a min time for reload task).
                        if (result.isSuccess()) {
                            if (msg.arg1 == UserTaskStatusCommon.HAS_READED) {
                                //We changed a task from unread to readed.
                            }
                            if (msg.arg1 == UserTaskStatusCommon.IN_DEALING) {
                                Utils.showToast(mToast, getString(R.string.task_accept_success), getApplicationContext());
                                showMenuItem(R.id.action_start_task, false);
                                showMenuItem(R.id.action_start_commit, true);
                                showMenuItem(R.id.action_roll_back_task, true);
                                enableShowUploadButtons(true);
                            }
                            if (msg.arg1 == UserTaskStatusCommon.ROLL_BACK) {
                                SingleTaskActivity.this.finish();
                            }
                            mTask.setTaskStatus(msg.arg1);
                        } else {
                            Log.d("acmllaugh1", "handleMessage (line 38): result error code : " + result.getBusinessErrorCode());
                            if (result.isBusException()
                                    && result.getBusinessErrorCode() == ExceptionBase.USER_TASK_NOT_VALID) {
                                Utils.showToast(mToast, getString(R.string.task_status_not_valid), getApplicationContext());
                                if (msg.arg1 == UserTaskStatusCommon.ROLL_BACK) {
                                    Utils.showToast(mToast, getString(R.string.roll_back_fail), getApplicationContext());
                                } else {
                                    SingleTaskActivity.this.finish();
                                }
                            }
                            // Utils.showToast(mToast, getString(R.string.change_task_status_fail), getApplicationContext());
                        }
                    }
                    break;
                }
                case MSG_UPLOAD_FILE_SUCCEED:
                    if (msg.obj instanceof Boolean) {
                        Utils.showToast(mToast, getString((Boolean) msg.obj ? R.string.image_upload_succeed : R.string.audio_upload_succeed), getApplicationContext());
                    }
                    break;
                case COMMIT_TASK_RESULT:
                    Utils.dissmissProgressDialog(mProgressDialog);
                    if (msg.obj instanceof CommitTaskResult) {
                        CommitTaskResult result = (CommitTaskResult) msg.obj;
                        if (result.isSuccess()) {
                            Utils.showToast(mToast, getString(R.string.commit_task_success), SingleTaskActivity.this);
                            //Since task is committed, we don't want user to update more materials, so we finish this single task activity.
                            SingleTaskActivity.this.finish();
                        } else {
                            Utils.showToast(mToast, getString(R.string.commit_task_failed), SingleTaskActivity.this);
                            if (result.isBusException()) {
                                Log.e("acmllaugh1", "commit task failed : " + result.getBusinessErrorCode());
                            } else {
                                result.getThrowable().printStackTrace();
                            }
                        }
                    }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_task);
        initVariables();
        registerToEventBus();

        initFiles();
    }

    private void initVariables() {

    }

    private void showTaskDetailInformation() {
        ((TextView) findViewById(R.id.detail_person_name))
                .setText(mTask.getName());
        ((TextView) findViewById(R.id.detail_task_address))
                .setText(mTask.getAddress());
        ((TextView) findViewById(R.id.detail_task_contact_info)).setText(mTask.getContactInfo());
        ((TextView) findViewById(R.id.detail_task_identity_card)).setText(mTask.getIdentityCard());
        ((TextView) findViewById(R.id.detail_task_bank_card)).setText(mTask.getBankCard());
        ((TextView) findViewById(R.id.detail_task_case_amount)).setText(Double.toString(mTask.getCaseAmount()));
        ((TextView) findViewById(R.id.detail_task_has_payed)).setText(Double.toString(mTask.getHasPayed()));
    }

    private void registerToEventBus() {
        if (!mEventBus.isRegistered(this)) {
            //mEventBus.register(this);
            try {
                mEventBus.registerSticky(this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void unRegisterEventBus() {
        if (mEventBus.isRegistered(this)) {
            try {
                mEventBus.unregister(this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        mMenu = menu;
        getMenuInflater().inflate(R.menu.single_task, menu);
        checkTaskStatus();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_start_task:
                startTask();
                break;
            case R.id.action_start_commit:
                showCommitTaskDialog();
                break;
            case R.id.action_roll_back_task:
                showRollBackDialog();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showRollBackDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        AlertDialog dialog = builder.setTitle(getString(R.string.roll_back_title))
                .setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        changeTaskStatus(UserTaskStatusCommon.ROLL_BACK);
                    }
                }).setNegativeButton(getString(R.string.cancel), null).create();
        dialog.show();
    }

    private void showCommitTaskDialog() {
        final View view = getLayoutInflater().inflate(R.layout.dialog_commit_task, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        AlertDialog dialog = builder.setView(view)
                .setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Switch needVisitAgainSwitch = (Switch) view.findViewById(R.id.switch_continue_visit);
                        EditText actualVisitorEditText = (EditText) view.findViewById(R.id.edit_actual_visitor);
                        EditText taskCommitEditText = (EditText) view.findViewById(R.id.edit_visit_report);
                        boolean needVisitAgain = needVisitAgainSwitch.isChecked();
                        String actualVisitor = actualVisitorEditText.getText().toString();
                        String taskCommit = taskCommitEditText.getText().toString();
                        doCommitTask(needVisitAgain, actualVisitor, taskCommit);
                    }
                })
                .setNegativeButton(getString(R.string.cancel), null).create();
        dialog.show();
    }

    private void doCommitTask(final boolean needVisitAgain, final String actualVisitor, final String taskCommit) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                CommitTaskHandler handler = new CommitTaskHandler();
                CommitTaskResult result = handler.commitTask(
                        mTask.getId(), needVisitAgain, taskCommit, actualVisitor, mTask.getTaskFlowTimes());
                Message msg = new Message();
                msg.what = COMMIT_TASK_RESULT;
                msg.obj = result;
                mResultHandler.sendMessage(msg);
            }
        });
        mProgressDialog = Utils.showProgressDialog(mProgressDialog, this);
        thread.start();
    }

    private void startTask() {
        changeTaskStatus(UserTaskStatusCommon.IN_DEALING);
    }

    private void changeTaskStatus(final int targetStatus) {
        NetworkState state = Utils.getCurrentNetworkState(this.getApplicationContext());
        if (!state.isConnected()) {
            Utils.dissmissProgressDialog(mProgressDialog);
            Utils.showToast(mToast, getString(R.string.net_work_unavailable), this.getApplicationContext());
            return;
        }
        mProgressDialog = Utils.showProgressDialog(mProgressDialog, this);
        Thread changeTaskStatusThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d("acmllaugh1", "run (line 121): start change task status.");
                Log.d("acmllaugh1", "run (line 124): task id : " + mTask.getId());
                Log.d("acmllaugh1", "run (line 125): user id : " + ClientGlobal.getUserId());
                UserTaskStatusChangeHandler handler = new UserTaskStatusChangeHandler();
                UserTaskStatusChangeResult result = handler.changeUserTaskStatus(
                        mTask.getId(), targetStatus, mTask.getTaskFlowTimes());
                Message msg = new Message();
                msg.what = MSG_CHANGE_TASK_STATUS;
                msg.obj = result;
                msg.arg1 = targetStatus;
                Log.d("acmllaugh1", "run (line 128): result : " + result.isSuccess());
                mResultHandler.sendMessage(msg);
            }
        });
        changeTaskStatusThread.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unRegisterEventBus();
    }

    /**
     * Receive TaskDTO object from task list activity.
     *
     * @param task
     */
    public void onEvent(TaskDto task) {
        if (mTaskTitleView == null) {
            mTaskTitleView = (TextView) findViewById(R.id.txt_single_task_title);
        }
        mTaskTitleView.setText(task.getVisitReason());
        mTask = task;
        showTaskDetailInformation();
    }

    private void checkTaskStatus() {
        if (mTask == null) {
            return;
        }
        switch (mTask.getUserTaskStatus()) {
            case UserTaskStatusCommon.NOT_READ:
                changeTaskStatus(UserTaskStatusCommon.HAS_READED);
                showMenuItem(R.id.action_roll_back_task, false);
                showMenuItem(R.id.action_start_commit, false);
                break;
            case UserTaskStatusCommon.HAS_READED:
                showMenuItem(R.id.action_roll_back_task, false);
                showMenuItem(R.id.action_start_commit, false);
                break;
            case UserTaskStatusCommon.IN_DEALING:
                showMenuItem(R.id.action_start_task, false);
                break;
        }
    }

    private void showMenuItem(int itemID, boolean visiable) {
        int count = mMenu.size();
        for (int i = 0; i < count; i++) {
            MenuItem item = mMenu.getItem(i);
            if (item.getItemId() == itemID) {
                item.setVisible(visiable);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQ_CODE_CAPTURE_PICTURE:
                    captureImageResult(data);
                    break;
                case REQ_CODE_SELECT_PICTURE:
                    selectImageResult(data);
                    break;
                case REQ_CODE_RECORD_AUDIO:
                    recordAudioResult(data);
                    break;
                case REQ_CODE_SELECT_AUDIO:
                    selectAudioResult(data);
                    break;
                default:
                    break;
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void initFiles() {
        if (mTask == null)
            return;

        mFileInfo = new FileInfo(ClientGlobal.getUserId(), mTask.getId());
        mUploadFileDao = new UploadFileDao(getApplicationContext());
        mUploadListener = new UploadFileListener();

        mTaskFilePath = DIRECTORY + "/" + mTask.getId();
        if (!Utils.isSDCardAvailable()) {
            //TODO:
            return;
        }
        File dir = new File(DIRECTORY);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        // Create folder if it is not exist.
        dir = new File(mTaskFilePath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        // Add .nomedia file that will not be scanned
        File noMediaFile = new File(mTaskFilePath + "/" + ".nomedia");
        if (!noMediaFile.exists()) {
            try {
                noMediaFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        ButtonOnclickListener buttonOnclickListener = new ButtonOnclickListener();
        // Capture a image
        mBtnCapture = (Button) this.findViewById(BUTTON_CAPTURE_IMAGE);
        mBtnCapture.setOnClickListener(buttonOnclickListener);
        // Select a image
        mBtnSelectImage = (Button) findViewById(BUTTON_SELECT_IMAGE);
        mBtnSelectImage.setOnClickListener(buttonOnclickListener);
        mGridImages = (LinearLayout) findViewById(R.id.grid_images);
        // Record an audio
        mBtnSoundRecord = (Button) this.findViewById(BUTTON_RECORD_AUDIO);
        mBtnSoundRecord.setOnClickListener(buttonOnclickListener);
        // Select an audio
        mBtnSelectAudio = (Button) findViewById(BUTTON_SELECT_AUDIO);
        mBtnSelectAudio.setOnClickListener(buttonOnclickListener);
        mGridAudios = (LinearLayout) findViewById(R.id.grid_audios);
        File[] files = new File(mTaskFilePath).listFiles();
        for (int i = 0; i < files.length; i++) {
            if (FILE_TYPE_IMAGE.equals(FileOperationUtils.getMIMEType(files[i]))) {
                mGridImages.addView(createImageView(files[i].getPath()), mGridImages.getChildCount());
            } else if (FILE_TYPE_AUDIO.equals(FileOperationUtils.getMIMEType(files[i]))) {
                mGridAudios.addView(createAudioView(files[i].getPath()), mGridAudios.getChildCount());
            }
        }

        // Disable to upload file while not in dealing status.
        if (mTask.getTaskStatus() != UserTaskStatusCommon.IN_DEALING) {
            enableShowUploadButtons(false);
        }
    }


    /* Four actions: capture image, select image, record audio, and select audio */
    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri imageUri = Uri.fromFile(new File(mTaskFilePath, TEMP_FILE_NAME));
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, REQ_CODE_CAPTURE_PICTURE);
    }

    private void selectImage() {
//        Intent intent = new Intent();
//        intent.setType(FILE_TYPE_IMAGE);
//        intent.setAction(Intent.ACTION_GET_CONTENT);
        Intent intent = new Intent(getApplicationContext(), ImageGridActivity.class);
        startActivityForResult(intent, REQ_CODE_SELECT_PICTURE);
    }

    private void recordAudio() {
        Intent intent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
        startActivityForResult(intent, REQ_CODE_RECORD_AUDIO);
    }

    private void selectAudio() {
        Intent intent = new Intent();
        intent.setType(FILE_TYPE_AUDIO);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, REQ_CODE_SELECT_AUDIO);
    }

    /* Four corresponding results of actions above */
    private void captureImageResult(Intent data) {
        String name = Utils.getImageName(System.currentTimeMillis());
        File oldFile = new File(mTaskFilePath, TEMP_FILE_NAME);
        File newFile = new File(mTaskFilePath, name);
        oldFile.renameTo(newFile);
        MediaScannerConnection.scanFile(getApplication(), new String[]{
                mTaskFilePath, name}, null, null);
        String path = mTaskFilePath + "/" + name;
        Bitmap bitmap = FileOperationUtils.compressImageBySrc(path);
        if (bitmap != null) {
            // Delete original image, and save compressed image.
            newFile.delete();
            FileOperationUtils.saveBitmapToFile(bitmap, path);
        }
        Log.d("Chris", "captureImageResult, path = " + path);
        mGridImages.addView(createImageView(path), mGridImages.getChildCount());

        // upload image to server
        mFileInfo.setFilePath(path);
        mFileInfo.setPicture(true);
        mFileInfo.setTaskFlowTimes(mTask.getTaskFlowTimes());
        startUploadFile(mFileInfo);
    }

    private void selectImageResult(Intent data) {
//        Uri uri = data.getData();
//        String oldPath = FileOperationUtils.getFilePathByUri(uri, this);
//        if (FILE_TYPE_IMAGE.equals(FileOperationUtils.getMIMEType(new File(oldPath)))) {
//            Bitmap bitmap = FileOperationUtils.compressImageBySrc(oldPath);
//            String name = Utils.getImageName(System.currentTimeMillis());
//            final String newPath = mTaskFilePath + "/" + name;
//            FileOperationUtils.saveBitmapToFile(bitmap, newPath);
//            MediaScannerConnection.scanFile(getApplication(),
//                    new String[]{mTaskFilePath}, null, null);
//            Log.d("Chris", "selectImageResult, path = " + newPath);
//            mGridImages.addView(createImageView(newPath), mGridImages.getChildCount());
//            // upload image to server
//            mFileInfo.setFilePath(newPath);
//            mFileInfo.setPicture(true);
//            mFileInfo.setTaskFlowTimes(mTask.getTaskFlowTimes());
//            startUploadFile(mFileInfo);
//        } else {
//            Utils.showToast(mToast, getString(R.string.invalid_image), getApplicationContext());
//        }

        List<String> pathList = (List<String>)data.getSerializableExtra("paths");
        Log.d("Chris", "selectImageResult, data = " + pathList);
        if (pathList == null)
            return;
        for (int i = 0; i < pathList.size(); i++) {
            Bitmap bitmap = FileOperationUtils.compressImageBySrc(pathList.get(i));
            String name = Utils.getImageName(System.currentTimeMillis() + 1000 * i);
            final String newPath = mTaskFilePath + "/" + name;
            FileOperationUtils.saveBitmapToFile(bitmap, newPath);
            MediaScannerConnection.scanFile(getApplication(),
                    new String[]{mTaskFilePath}, null, null);
            Log.d("Chris", "selectImageResult, path = " + newPath);
            mGridImages.addView(createImageView(newPath), mGridImages.getChildCount());
            // upload image to server
            mFileInfo.setFilePath(newPath);
            mFileInfo.setPicture(true);
            mFileInfo.setTaskFlowTimes(mTask.getTaskFlowTimes());
            startUploadFile(mFileInfo);
        }
    }

    private void recordAudioResult(Intent data) {
        Uri uri = data.getData();
        String oldPath = FileOperationUtils.getFilePathByUri(uri, this);
        String newPath = mTaskFilePath + "/"
                + Utils.getAudioName(System.currentTimeMillis());
        File oldFile = new File(oldPath);
        File newFile = new File(newPath);
        oldFile.renameTo(newFile);
        MediaScannerConnection.scanFile(getApplication(),
                new String[]{mTaskFilePath}, null, null);
        mGridAudios.addView(createAudioView(newPath), mGridAudios.getChildCount());
        Log.d("Chris", "recordAudioResult, path = " + newPath);

        // upload audio to server
        mFileInfo.setFilePath(newPath);
        mFileInfo.setPicture(false);
        mFileInfo.setTaskFlowTimes(mTask.getTaskFlowTimes());
        startUploadFile(mFileInfo);
    }

    private void selectAudioResult(Intent data) {
        Uri uri = data.getData();
        String oldPath = FileOperationUtils.getFilePathByUri(uri, this);
        if (FILE_TYPE_AUDIO.equals(FileOperationUtils.getMIMEType(new File(oldPath)))) {
            String newPath = mTaskFilePath + "/"
                    + Utils.getAudioName(System.currentTimeMillis());
            FileOperationUtils.copyFile(oldPath, newPath);
            MediaScannerConnection.scanFile(getApplication(),
                    new String[]{mTaskFilePath}, null, null);

            mGridAudios.addView(createAudioView(newPath), mGridAudios.getChildCount());
            Log.d("Chris", "selectAudioResult, path = " + newPath);
            // upload audio to server
            mFileInfo.setFilePath(newPath);
            mFileInfo.setPicture(false);
            mFileInfo.setTaskFlowTimes(mTask.getTaskFlowTimes());
            startUploadFile(mFileInfo);
        } else {
            Utils.showToast(mToast, getString(R.string.invalid_audio), getApplicationContext());
        }
    }

    private ImageView createImageView(String path) {
        final ImageView image = new ImageView(this);
        //image.setImageURI(Uri.parse(path));
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                getResources().getDimensionPixelSize(R.dimen.image_width), getResources().getDimensionPixelSize(R.dimen.image_height));
        image.setLayoutParams(layoutParams);
        image.setPadding(5, 0, 5, 0);
        image.setTag(path);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(
                        Uri.fromFile(new File(image.getTag().toString())),
                        FILE_TYPE_IMAGE);
                startActivity(intent);
            }
        });
        DisplayImageOptions options = ImageLoaderUtil.getOption();
        String imageUri = "file://" + path;
        Log.d("Chris", "createImageView (line 458): image uri : " + imageUri);
        if (mThumbnailLoader == null || !mThumbnailLoader.isInited()) {
            mThumbnailLoader = ImageLoader.getInstance();
        }
        mThumbnailLoader.cancelDisplayTask(image);
        ImageLoader.getInstance().displayImage(imageUri, image, options, null);
        return image;
    }

    private ImageView createAudioView(String path) {
        final ImageView audio = new ImageView(this);
        audio.setImageDrawable(getResources().getDrawable(R.drawable.ic_launcher_record_audio));
        audio.setPadding(20, 0, 20, 0);
        audio.setTag(path);
        audio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                File file = new File(audio.getTag().toString());
                if (file != null && file.exists()) {
                    FileOperationUtils.openFile(file, getApplication());
                } else {
                    Utils.showToast(mToast, getString(R.string.file_not_exist), getApplicationContext());
                }
            }
        });
        return audio;
    }

    private class ButtonOnclickListener implements View.OnClickListener {
        @Override
        public void onClick(View arg0) {
            NetworkState state = Utils.getCurrentNetworkState(getApplicationContext());
            if (!state.isConnected()) {
                Utils.dissmissProgressDialog(mProgressDialog);
                Utils.showToast(mToast, getString(R.string.net_work_unavailable), getApplicationContext());
                return;
            }
            switch (arg0.getId()) {
                case BUTTON_CAPTURE_IMAGE:
                    captureImage();
                    break;
                case BUTTON_SELECT_IMAGE:
                    selectImage();
                    break;
                case BUTTON_RECORD_AUDIO:
                    recordAudio();
                    break;
                case BUTTON_SELECT_AUDIO:
                    selectAudio();
                    break;
                default:
                    break;
            }
        }
    }

    private class UploadFileListener implements UploadFileThread.UploadResultListener {

        @Override
        public void onUploadSucceed(FileInfo fileInfo) {
            Message msg = new Message();
            msg.what = MSG_UPLOAD_FILE_SUCCEED;
            msg.obj = fileInfo.isPicture();
            mResultHandler.sendMessage(msg);
            // set result to 1 as finished
            fileInfo.setUploadResult(1);
            mUploadFileDao.insertUploadFileInfo(fileInfo);
        }

        @Override
        public void onUploadFailed(FileInfo fileInfo) {
            fileInfo.setUploadResult(0);
            mUploadFileDao.insertUploadFileInfo(fileInfo);
        }
    }

    private void enableShowUploadButtons(boolean enable) {
        if (enable) {
            mBtnCapture.setVisibility(View.VISIBLE);
            mBtnSelectImage.setVisibility(View.VISIBLE);
            mBtnSoundRecord.setVisibility(View.VISIBLE);
            mBtnSelectAudio.setVisibility(View.VISIBLE);
        } else {
            mBtnCapture.setVisibility(View.GONE);
            mBtnSelectImage.setVisibility(View.GONE);
            mBtnSoundRecord.setVisibility(View.GONE);
            mBtnSelectAudio.setVisibility(View.GONE);
        }
    }

    private void startUploadFile(FileInfo fileInfo) {
        UploadFileThread uploadFileThread = new UploadFileThread(fileInfo, getApplicationContext());
        uploadFileThread.setListener(mUploadListener);
        uploadFileThread.start();
    }

}
