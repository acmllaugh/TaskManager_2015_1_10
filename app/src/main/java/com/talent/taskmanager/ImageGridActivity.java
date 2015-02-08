package com.talent.taskmanager;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.ImageColumns;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.GridView;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.talent.taskmanager.gallery.ImageAdapter;
import com.talent.taskmanager.gallery.ImageFolder;
import com.talent.taskmanager.gallery.ImageFolderPopupWindow;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;


public class ImageGridActivity extends Activity implements ImageFolderPopupWindow.OnImageDirSelected, ImageAdapter.ImageSelectListener {
	public static final String DCIM = Environment
			.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
			.toString();

	public static final String CAMERA_DIRECTORY = DCIM + "/Camera";
	public static final String CAMERA_BUCKET_ID = String
			.valueOf(CAMERA_DIRECTORY.toLowerCase().hashCode());

	private ProgressDialog mProgressDialog;
    private Toast mToast;

	/**
	 * 存储文件夹中的图片数量
	 */
	private int mPicsSize;
	/**
	 * 图片数量最多的文件夹
	 */
	private File mImgDir;
	/**
	 * 所有的图片
	 */
	private List<String> mImgs;

	private GridView mGirdView;
	private ImageAdapter mAdapter;
    private Button mBtnSend;
	/**
	 * 临时的辅助类，用于防止同一个文件夹的多次扫描
	 */
	private HashSet<String> mDirPaths = new HashSet<String>();

	/**
	 * 扫描拿到所有的图片文件夹
	 */
	private List<ImageFolder> mImageFloders = new ArrayList<ImageFolder>();

	private RelativeLayout mBottomLy;

	private TextView mChooseDir;
	private TextView mImageCount;
	int totalCount = 0;

	private int mScreenHeight;

	private ImageFolderPopupWindow mListImageDirPopupWindow;

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			mProgressDialog.dismiss();
			// 为View绑定数据
			data2View();
			// 初始化展示文件夹的popupWindw
			initListDirPopupWindw();
		}
	};

	/**
	 * 为View绑定数据
	 */
	private void data2View() {
		// 默认从相机相册中选择
		mImgDir = new File(CAMERA_DIRECTORY);
		mImgs = getImages(CAMERA_BUCKET_ID);

		if (mImgDir == null) {
			Toast.makeText(getApplicationContext(), "擦，一张图片没扫描到",
					Toast.LENGTH_SHORT).show();
			return;
		}

//		String[] arrayFiles = mImgDir.list(new FilenameFilter() {
//			@Override
//			public boolean accept(File dir, String filename) {
//				if (filename.endsWith(".jpg") || filename.endsWith(".png")
//						|| filename.endsWith(".jpeg"))
//					return true;
//				return false;
//			}
//		});
//
//		Arrays.sort(arrayFiles, Collections.reverseOrder());
//		mImgs = Arrays.asList(arrayFiles);

		/**
		 * 可以看到文件夹的路径和图片的路径分开保存，极大的减少了内存的消耗；
		 */
		mAdapter = new ImageAdapter(getApplicationContext(), mImgs,
				R.layout.grid_item, mImgDir.getAbsolutePath());
        mAdapter.mSelectedImage.clear();
        mAdapter.setImageSelectListener(this);
		mGirdView.setAdapter(mAdapter);
		mImageCount.setText(mImgs.size() + getResources().getString(R.string.picture_num));
	};

	/**
	 * 初始化展示文件夹的popupWindw
	 */
	private void initListDirPopupWindw() {
		mListImageDirPopupWindow = new ImageFolderPopupWindow(
				LayoutParams.MATCH_PARENT, (int) (mScreenHeight * 0.7),
				mImageFloders, LayoutInflater.from(getApplicationContext())
						.inflate(R.layout.list_dir, null));

		mListImageDirPopupWindow.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				// 设置背景颜色变暗
				WindowManager.LayoutParams lp = getWindow().getAttributes();
				lp.alpha = 1.0f;
				getWindow().setAttributes(lp);
			}
		});
		// 设置选择文件夹的回调
		mListImageDirPopupWindow.setOnImageDirSelected(this);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_gallery);

		DisplayMetrics outMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
		mScreenHeight = outMetrics.heightPixels;

		initView();
		getImages();
		initEvent();
	}

	/**
	 * 利用ContentProvider扫描手机中的图片，此方法在运行在子线程中 完成图片的扫描，最终获得jpg最多的那个文件夹
	 */
	private void getImages() {
        if (!Utils.isSDCardAvailable()) {
            Toast.makeText(this, getResources().getString(R.string.sd_card_not_available), Toast.LENGTH_SHORT).show();
            return;
        }
		// 显示进度条
		mProgressDialog = ProgressDialog.show(this, null, getResources().getString(R.string.loading_now));

		new Thread(new Runnable() {
			@Override
			public void run() {
				traverseImages();

				// 通知Handler扫描图片完成
				mHandler.sendEmptyMessage(0x110);

			}
		}).start();

	}

	/**
	 * 初始化View
	 */
	private void initView() {
		mGirdView = (GridView) findViewById(R.id.id_gridView);
		mChooseDir = (TextView) findViewById(R.id.id_choose_dir);
		mImageCount = (TextView) findViewById(R.id.id_total_count);
		mBottomLy = (RelativeLayout) findViewById(R.id.id_bottom_ly);
        mBtnSend = (Button) findViewById(R.id.btn_send);
	}

	private void initEvent() {
		/**
		 * 为底部的布局设置点击事件，弹出popupWindow
		 */
		mBottomLy.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mListImageDirPopupWindow
						.setAnimationStyle(R.style.anim_popup_dir);
				mListImageDirPopupWindow.showAsDropDown(mBottomLy, 0, 0);

				// 设置背景颜色变暗
				WindowManager.LayoutParams lp = getWindow().getAttributes();
				lp.alpha = .3f;
				getWindow().setAttributes(lp);
			}
		});

        mBtnSend.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("paths", (java.io.Serializable) mAdapter.mSelectedImage);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });
	}

	@Override
	public void selected(ImageFolder floder) {
		for (ImageFolder folder : mImageFloders) {
			folder.setSelected(false);
		}
		floder.setSelected(true);

		mImgDir = new File(floder.getDir());
		mImgs = getImages(floder.getBucketId());

		/**
		 * 可以看到文件夹的路径和图片的路径分开保存，极大的减少了内存的消耗；
		 */
		mAdapter = new ImageAdapter(getApplicationContext(), mImgs,
				R.layout.grid_item, mImgDir.getAbsolutePath());
		mGirdView.setAdapter(mAdapter);
        mAdapter.setImageSelectListener(this);
		// mAdapter.notifyDataSetChanged();
		mImageCount.setText(floder.getCount() + getResources().getString(R.string.picture_num));
		mChooseDir.setText(floder.getName());
		mListImageDirPopupWindow.dismiss();

	}

	private List<String> getImages(String bucketId) {
		List<String> imagesList = new ArrayList<String>();
		Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
		ContentResolver mContentResolver = getContentResolver();
		String selection = "(" + MediaStore.Images.Media.MIME_TYPE + "=? or "
				+ MediaStore.Images.Media.MIME_TYPE + "=?) and " + MediaStore.Images.Media.BUCKET_ID + "=? ";
		String[] selectionArgs = new String[] { "image/jpeg", "image/png", bucketId};
		String order = ImageColumns.DATE_TAKEN + " DESC, " + ImageColumns._ID
				+ " DESC";
		Cursor mCursor = mContentResolver.query(mImageUri, null, selection,
				selectionArgs, order);

		while (mCursor.moveToNext()) {
			imagesList.add(mCursor.getString(mCursor
					.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME)));
		}
		mCursor.close();
		return imagesList;
	}

	private void traverseImages() {
		Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
		ContentResolver mContentResolver = getContentResolver();
		String order = ImageColumns.DATE_TAKEN + " DESC,"
				+ ImageColumns._ID + " DESC";
		Cursor mCursor = mContentResolver.query(mImageUri, null,
				MediaStore.Images.Media.MIME_TYPE + "=? or "
						+ MediaStore.Images.Media.MIME_TYPE + "=?",
				new String[] { "image/jpeg", "image/png" }, order);

		while (mCursor.moveToNext()) {
			// 获取图片的路径
			String path = mCursor.getString(mCursor
					.getColumnIndex(MediaStore.Images.Media.DATA));
			String bucketId = mCursor.getString(mCursor
					.getColumnIndex(MediaStore.Images.Media.BUCKET_ID));

			// 获取该图片的父路径名
			File parentFile = new File(path).getParentFile();
			if (parentFile == null)
				continue;
			String dirPath = parentFile.getAbsolutePath();
			ImageFolder imageFloder = null;
			// 利用一个HashSet防止多次扫描同一个文件夹（不加这个判断，图片多起来还是相当恐怖的~~）
			if (mDirPaths.contains(dirPath)) {
				continue;
			} else {
				mDirPaths.add(dirPath);
				// 初始化imageFloder
				imageFloder = new ImageFolder();
				imageFloder.setDir(dirPath);
				imageFloder.setFirstImagePath(path);
				imageFloder.setBucketId(bucketId);
				if (imageFloder.getBucketId().equals(CAMERA_BUCKET_ID)) {
					imageFloder.setSelected(true);
				}
			}

			int picSize = parentFile.list(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String filename) {
					if (filename.endsWith(".jpg")
							|| filename.endsWith(".png")
							|| filename.endsWith(".jpeg"))
						return true;
					return false;
				}
			}).length;
			totalCount += picSize;

			imageFloder.setCount(picSize);
			mImageFloders.add(imageFloder);

			if (picSize > mPicsSize) {
				mPicsSize = picSize;
				mImgDir = parentFile;
			}
		}
		mCursor.close();

		// 扫描完成，辅助的HashSet也就可以释放内存了
		mDirPaths = null;

	}

    @Override
    public void onImageSelected(boolean selected) {
        if (!selected && mAdapter.mSelectedImage.size() == 0) {
            mBtnSend.setEnabled(false);
            mBtnSend.setText(getResources().getString(R.string.action_send));
        } else {
            mBtnSend.setEnabled(true);
            mBtnSend.setText(String.format("(%d/%d)", mAdapter.mSelectedImage.size(), mAdapter.MAX_NUMBER_SUPPORT)+ getResources().getString(R.string.action_send));
        }
    }

    @Override
    public void onImageNumMax() {
        Utils.showToast(mToast, getResources().getString(R.string.max_num_toast), getApplicationContext());
    }
}
