package com.ayke.library.download;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.AbstractHttpEntity;
import org.apache.http.entity.ContentProducer;
import org.apache.http.entity.EntityTemplate;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;

import android.util.Log;

/**
 * 下载任务
 * 
 * @author keayuan
 */
public class DownloadTask implements Runnable {
	private final static String TAG = "DownloadTask";
	private String mUrl;
	private IDownloadListener mDlListener;
	/**
	 * 下载到的数据资源
	 */
	private byte[] mDataRes;
	/**
	 * 下载到的数据长度
	 */
	private int mDataLen;
	/**
	 * 是否停止下载
	 */
	private boolean mIsStopDl = false;
	/**
	 * 唯一标识一个下载任务
	 */
	private String mTaskTag;

	private List<NameValuePair> mPostData;
	private String mPostString;

	private static final int CONNECTION_TIMEOUT = 30 * 1000;
	private static final int SOCKET_TIMEOUT = 30 * 1000;

	public DownloadTask(String tag, String url, List<NameValuePair> data,
			IDownloadListener listener) {
		mTaskTag = tag;
		mIsStopDl = false;
		mPostData = data;
		mUrl = url;
		mDlListener = listener;
	}

	public DownloadTask(String tag, String url, String data,
			IDownloadListener listener) {
		mTaskTag = tag;
		mIsStopDl = false;
		mPostString = data;
		mUrl = url;
		mDlListener = listener;
	}

	public void stop() {
		Log.d(TAG, "task stop: " + mTaskTag);
		mIsStopDl = true;
	}

	public void setDownloadListener(IDownloadListener listener) {
		mDlListener = listener;
	}

	@Override
	public void run() {
		Log.d(TAG, mTaskTag + " run start");
		if (mDlListener != null) {
			mDlListener.onDlStart(mTaskTag);
		}
		if (mPostData != null) {
			postRemoteRes(mUrl, mPostData);
		} else if (mPostString != null) {
			postRemoteRes(mUrl, mPostString);
		} else {
			getRemoteRes(mUrl);
		}
		Log.d(TAG, mTaskTag + " run end");
	}

	/**
	 * 在线下载
	 * 
	 * @param url
	 */
	private void getRemoteRes(String url) {
		// Log.d(TAG, "getRemoteRes url=" + url);
		try {
			// 设置HttpClient超时参数
			HttpParams httpParameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParameters,
					CONNECTION_TIMEOUT);
			HttpConnectionParams.setSoTimeout(httpParameters, SOCKET_TIMEOUT);

			// 创建http get
			HttpGet httpGet = new HttpGet(url);
			// 创建http client
			DefaultHttpClient httpClient = new DefaultHttpClient(httpParameters);
			// 执行http 请求
			HttpResponse httpResponse = httpClient.execute(httpGet);

			if (httpResponse != null
					&& httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				InputStream is = httpResponse.getEntity().getContent();

				byte[] buffer = new byte[512];
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				int readLength = 0;
				int offset = 0;
				mDataLen = 0;
				mDataRes = null;
				do {
					readLength = is.read(buffer);
					if (readLength > 0) {
						baos.write(buffer, 0, readLength);
						offset += readLength;
						mDataLen = offset;
					}
				} while (!mIsStopDl && readLength > 0);
				mDataRes = baos.toByteArray();
				baos.close();
				is.close();
			}

			// 下载完成
			Log.i(TAG, "downloadRemoteRes download completed. data length="
					+ (mDataRes == null ? "null" : mDataRes.length)
					+ " record length=" + mDataLen + " url=" + url);
			if (!mIsStopDl && (mDataRes == null || mDataRes.length == 0)) {
				Log.e(TAG, "data = null");
				if (mDlListener != null)
					mDlListener.onDlError(mTaskTag, -1);
				return;
			}
			if (!mIsStopDl && mDlListener != null) {
				Log.d(TAG, "downloadRemoteRes ---- callback in.");
				mDlListener.onDlCompleted(mTaskTag, mDataRes, mDataLen);
				Log.d(TAG, "downloadRemoteRes ---- callback out.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(TAG,
					"downloadRemoteRes exception url=" + url + "msg="
							+ e.getMessage());
			if (!mIsStopDl && mDlListener != null) {
				mDlListener.onDlError(mTaskTag, -1);
			}
		}
	}

	private void postRemoteRes(String url, List<NameValuePair> data) {
		// Log.d(TAG, "postRemoteRes url=" + url);
		try {
			// 设置HttpClient超时参数
			HttpParams httpParameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParameters,
					CONNECTION_TIMEOUT);
			HttpConnectionParams.setSoTimeout(httpParameters, SOCKET_TIMEOUT);

			// 创建http post
			HttpPost httpPost = new HttpPost(url);
			// 创建http client
			DefaultHttpClient httpClient = new DefaultHttpClient(httpParameters);
			httpPost.setEntity(new UrlEncodedFormEntity(data, HTTP.UTF_8));
			// 执行http 请求
			HttpResponse httpResponse = httpClient.execute(httpPost);

			if (httpResponse != null
					&& httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				InputStream is = httpResponse.getEntity().getContent();
				byte[] buffer = new byte[512];
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				int readLength = 0;
				int offset = 0;
				mDataLen = 0;
				mDataRes = null;
				do {
					readLength = is.read(buffer);
					if (readLength > 0) {
						baos.write(buffer, 0, readLength);
						offset += readLength;
						mDataLen = offset;
					}
				} while (!mIsStopDl && readLength > 0);
				mDataRes = baos.toByteArray();
				baos.close();
				is.close();
			}

			// 下载完成
			Log.i(TAG, "downloadRemoteRes download completed. data length="
					+ (mDataRes == null ? "null" : mDataRes.length)
					+ " record length=" + mDataLen + " url=" + url);
			if (!mIsStopDl && (mDataRes == null || mDataRes.length == 0)) {
				Log.e(TAG, "data = null");
				if (mDlListener != null)
					mDlListener.onDlError(mTaskTag, -1);
				return;
			}
			if (!mIsStopDl && mDlListener != null) {
				Log.d(TAG, "downloadRemoteRes ---- callback in.");
				mDlListener.onDlCompleted(mTaskTag, mDataRes, mDataLen);
				Log.d(TAG, "downloadRemoteRes ---- callback out.");
			}
		} catch (Exception e) {
			Log.e(TAG,
					"downloadRemoteRes exception url=" + url + "msg="
							+ e.getMessage());
			if (!mIsStopDl && mDlListener != null) {
				mDlListener.onDlError(mTaskTag, -1);
			}
		}
	}

	@SuppressWarnings("unused")
	private void postRemoteRes(String url, final String data) {
		try {
			// 设置HttpClient超时参数
			HttpParams httpParameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParameters,
					CONNECTION_TIMEOUT);
			HttpConnectionParams.setSoTimeout(httpParameters, SOCKET_TIMEOUT);

			DefaultHttpClient httpClient = new DefaultHttpClient(httpParameters);// 创建http
																					// client
			HttpPost httpPost = new HttpPost(url);// 创建http post
			AbstractHttpEntity entity = null;

			ContentProducer cp = new ContentProducer() {
				@Override
				public void writeTo(OutputStream outstream) throws IOException {
					// outstream.write(data.getBytes(HTTP.UTF_8));
					// outstream.flush();
					// outstream.close();
					Writer writer = new OutputStreamWriter(outstream, "UTF-8");
					writer.write(data);
					writer.flush();
					writer.close();
				}
			};
			entity = new EntityTemplate(cp);

			entity = new StringEntity(data, "utf-8");

			entity = new InputStreamEntity(new ByteArrayInputStream(
					data.getBytes(HTTP.UTF_8)),
					data.getBytes(HTTP.UTF_8).length);

			entity.setContentType("application/x-www-form-urlencoded");
			entity.setContentEncoding("utf-8");
			httpPost.setEntity(entity);

			// HttpResponse httpResponse = httpClient.execute(httpPost);//
			// 执行http 请求

			URL urls = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) urls.openConnection();
			conn.setConnectTimeout(CONNECTION_TIMEOUT);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.connect();
			OutputStream out = conn.getOutputStream();

			// 默认参数方式传递
			out.write(data.getBytes(HTTP.UTF_8));
			out.flush();
			out.close();
			InputStream is = conn.getInputStream();
			if (is != null) {

				// if (httpResponse != null
				// && httpResponse.getStatusLine().getStatusCode() ==
				// HttpStatus.SC_OK) {
				// InputStream is = httpResponse.getEntity().getContent();

				byte[] buffer = new byte[512];
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				int readLength = 0;
				int offset = 0;
				mDataLen = 0;
				mDataRes = null;
				do {
					readLength = is.read(buffer);
					if (readLength > 0) {
						baos.write(buffer, 0, readLength);
						offset += readLength;
						mDataLen = offset;
					}
				} while (!mIsStopDl && readLength > 0);
				mDataRes = baos.toByteArray();
				baos.close();
				is.close();
			}

			// 下载完成
			Log.i(TAG, "downloadRemoteRes download completed. data length="
					+ (mDataRes == null ? "null" : mDataRes.length)
					+ " record length=" + mDataLen + " url=" + url);
			if (!mIsStopDl && (mDataRes == null || mDataRes.length == 0)) {
				Log.e(TAG, "data = null");
				if (mDlListener != null)
					mDlListener.onDlError(mTaskTag,
							DownloadManager.ERROR_RESULT_NULL);
				return;
			}
			if (!mIsStopDl && mDlListener != null) {
				Log.d(TAG, "downloadRemoteRes ---- callback in.");
				mDlListener.onDlCompleted(mTaskTag, mDataRes, mDataLen);
				Log.d(TAG, "downloadRemoteRes ---- callback out.");
			}
		} catch (ConnectTimeoutException e) {
			Log.e(TAG,
					"downloadRemoteRes exception url=" + url + " msg="
							+ e.getMessage());
			if (!mIsStopDl && mDlListener != null) {
				mDlListener.onDlError(mTaskTag, DownloadManager.ERROR_TIME_OUT);
			}
		} catch (SocketTimeoutException e) {
			Log.e(TAG,
					"downloadRemoteRes exception url=" + url + " msg="
							+ e.getMessage());
			if (!mIsStopDl && mDlListener != null) {
				mDlListener.onDlError(mTaskTag, DownloadManager.ERROR_TIME_OUT);
			}
		} catch (Exception e) {
			Log.e(TAG,
					"downloadRemoteRes exception url=" + url + " msg="
							+ e.getMessage());
			if (!mIsStopDl && mDlListener != null) {
				mDlListener.onDlError(mTaskTag, DownloadManager.ERROR_OTHER);
			}
		}
	}

}
