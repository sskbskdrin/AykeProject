package com.ayke.library.download;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.http.NameValuePair;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

/**
 * 下载管理
 * 
 * @author keayuan
 */
public class DownloadManager implements IDownloadListener {
	private final static String TAG = "DownloadManager";
	private static DownloadManager mInstance;
	private Object mLocker = new Object();
	private ExecutorService threadPool;

	private final static int MSG_ON_DL_RESULT = 1001;
	private final static int MSG_ON_DL_ERROR = 1002;
	private final static int MSG_ON_DL_START = 1003;

	public final static int ERROR_OTHER = -1000;
	public final static int ERROR_RESULT_NULL = -1;
	public final static int ERROR_TIME_OUT = -2;

	/**
	 * 下载列表
	 */
	private HashMap<String, DlTaskInfo> mDownloadList = new HashMap<String, DlTaskInfo>();

	private DownloadManager() {
	}

	/**
	 * 创建静态实例
	 */
	public static DownloadManager createInstance() {
		mInstance = new DownloadManager();
		return mInstance;
	}

	/**
	 * 获取静态实例
	 */
	public static DownloadManager getInstance() {
		if (mInstance == null) {
			Log.e(TAG, "DownloadManager getInstance null.");
		}
		return mInstance;
	}

	/**
	 * 下载回调消息处理
	 */
	@SuppressLint("HandlerLeak")
	private Handler mDlRltHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_ON_DL_RESULT:
				DlResult result = (DlResult) msg.obj;
				DlTaskInfo info = mDownloadList.get(result.getTag());
				if (info != null && info.getDlListener() != null) {
					info.getDlListener().onDlCompleted(result.getTag(),
							result.getData(), result.getLength());
					removeDlTask(result.getTag());
				}
				break;
			case MSG_ON_DL_ERROR:
				DlTaskInfo info2 = mDownloadList.get(msg.obj);
				if (info2 != null && info2.getDlListener() != null) {
					info2.getDlListener().onDlError((String) msg.obj, msg.arg1);
					removeDlTask((String) msg.obj);
				}
				break;
			case MSG_ON_DL_START:
				DlTaskInfo info3 = mDownloadList.get(msg.obj);
				if (info3 != null && info3.getDlListener() != null) {
					info3.getDlListener().onDlStart((String) msg.obj);
				}
				break;
			}
		}
	};

	public void startDlTask(String tag) {
		if (!TextUtils.isEmpty(tag)) {
			if (threadPool == null)
				threadPool = Executors.newFixedThreadPool(5);
			threadPool.execute(mDownloadList.get(tag).mDlTask);
		} else {
			Log.e(TAG, "startDlTask fail!!! tag is null");
		}
	}

	/**
	 * 添加任务到队列
	 * 
	 * @param postData
	 *            postData为null时使用get方式请求，否则用post方式请求
	 */
	public void addDlTask(String tag, String url, List<NameValuePair> postData,
			IDownloadListener listener) {
		synchronized (mLocker) {
			Log.d(TAG, "addDlTask tag=" + tag);
			if (!TextUtils.isEmpty(tag) && !TextUtils.isEmpty(url)
					&& mDownloadList != null && listener != null) {
				DlTaskInfo info = new DlTaskInfo(new DownloadTask(tag, url,
						postData, this), listener);
				mDownloadList.put(tag, info);
			} else {
				Log.e(TAG, "addDlTask unexpected arguments tag=" + tag);
			}
		}
	}

	/**
	 * 添加任务到队列
	 * 
	 * @param postData
	 *            postData为null时使用get方式请求，否则用post方式请求
	 */
	public void addDlTask(String tag, String url, String postData,
			IDownloadListener listener) {
		synchronized (mLocker) {
			Log.d(TAG, "addDlTask tag=" + tag);
			if (!TextUtils.isEmpty(tag) && !TextUtils.isEmpty(url)
					&& mDownloadList != null && listener != null) {
				if (mDownloadList.containsKey(tag)) {
					Log.e(TAG, "addDlTask already exist tag=" + tag);
				} else {
					DlTaskInfo info = new DlTaskInfo(new DownloadTask(tag, url,
							postData, this), listener);
					mDownloadList.put(tag, info);
				}
			} else {
				Log.e(TAG, "addDlTask unexpected arguments tag=" + tag);
			}
		}
	}

	public DownloadTask getDlTask(String tag) {
		synchronized (mLocker) {
			Log.d(TAG, "getDlTask tag=" + tag);
			DownloadTask task = null;
			if (tag != null && mDownloadList != null)
				task = mDownloadList.get(tag).getTask();
			return task;
		}
	}

	public void removeDlTask(String tag) {
		synchronized (mLocker) {
			Log.d(TAG, "removeDlTask tag=" + tag);
			if (tag != null && mDownloadList != null
					&& mDownloadList.containsKey(tag)) {
				DownloadTask task = mDownloadList.get(tag).getTask();
				if (task != null) {
					task.stop();
				}
				mDownloadList.remove(tag);
			}
		}
	}

	public void clear() {
		synchronized (mLocker) {
			Log.d(TAG, "clear");
			if (mDownloadList != null) {
				Iterator<Entry<String, DlTaskInfo>> iter = mDownloadList
						.entrySet().iterator();
				while (iter.hasNext()) {
					Map.Entry<String, DlTaskInfo> entry = iter.next();
					String tag = (String) entry.getKey();
					DlTaskInfo task = (DlTaskInfo) entry.getValue();
					Log.d(TAG, "clear tag=" + tag);
					if (task != null) {
						task.getTask().stop();
					}
					iter.remove();
				}
			}
		}
	}

	@Override
	public void onDlStart(String tag) {
		mDlRltHandler.obtainMessage(MSG_ON_DL_START, 0, 0, tag).sendToTarget();
	}

	@Override
	public void onDlCompleted(String tag, byte[] data, int length) {
		mDlRltHandler.obtainMessage(MSG_ON_DL_RESULT, 0, 0,
				new DlResult(tag, data, length)).sendToTarget();
	}

	@Override
	public void onDlError(String tag, int errorCode) {
		mDlRltHandler.obtainMessage(MSG_ON_DL_ERROR, errorCode, 0, tag)
				.sendToTarget();
	}

	/**
	 * 下载任务信息
	 * 
	 * @author ydshu
	 */
	private class DlTaskInfo {
		private DownloadTask mDlTask;
		private IDownloadListener mListener;

		public DlTaskInfo(DownloadTask task, IDownloadListener listener) {
			mDlTask = task;
			mListener = listener;
		}

		public DownloadTask getTask() {
			return mDlTask;
		}

		public IDownloadListener getDlListener() {
			return mListener;
		}
	}

	/**
	 * 下载结果封装
	 * 
	 * @author ydshu
	 */
	private class DlResult {
		private byte[] mData;
		private int mLength;
		private String mTag;

		public DlResult(String tag, byte[] data, int length) {
			mData = data;
			mLength = length;
			mTag = tag;
		}

		public String getTag() {
			return mTag;
		}

		public int getLength() {
			return mLength;
		}

		public byte[] getData() {
			return mData;
		}
	}

}
