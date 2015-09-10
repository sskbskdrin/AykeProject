package com.ayke.library.download;

/**
 * 下载回调
 * @author keayuan
 *
 */
public interface IDownloadListener {

	/**
	 * 下载开始
	 */
	public void onDlStart(String tag);
	/**
	 * 下载完成
	 */
	public void onDlCompleted(String tag, byte[] data, int length);
	
	/**
	 * 下载出错
	 * @param errorCode
	 * 		错误号
	 */
	public void onDlError(String tag, int errorCode);
}
