package com.open.volley.wrapper;

import java.util.HashMap;

/**
 * 项目名称 ：  营销监测
 * 类名称  ： VolleySocketEncapsulation
 * 类描述 ： 封装网络的请求地址 、是否需要弹出网络等待框以及数据传输的Json所对应的HashMap
 * 注意： 这样封装，后期非常便于扩展，如是否支持Base64、是否支持Des、是否支持GZip  都可以将字段加载这里面
 * 创建人： 张姣发
 * 创建时间：2014-09-10
 * @version 1.0
 *
 */
public class VolleySocketEncapsulation {

	private boolean mIsSupportLoadingDlg = false;
	private String mCurrentUrl = "";
	private HashMap<String, String> mDataMap = new HashMap<String, String>();
	
	public VolleySocketEncapsulation(String url, boolean loadingdlg) {
		mIsSupportLoadingDlg = loadingdlg;
		mCurrentUrl = url;
	}
	
	/**
	 * 仿造HashMap填充数据的方式，避免上层构造HashMap对象
	 * @param key 数据的键
	 * @param value 数据的数值
	 */
	public void putSingleData(String key,String value) {
		mDataMap.put(key, value);
	}
	
	public HashMap<String, String> getDataHashMap() {
		return mDataMap;
	}
	
	public void setDataHashMap(HashMap<String, String> datahashmap) {
		mDataMap = datahashmap;
	}
	
	public String getVolleyUrl() {
		return mCurrentUrl;
	}
	
	public boolean isSupportShowLoadingDlg() {
		return mIsSupportLoadingDlg;
	}

	public void setSupportLoadingDlg(boolean mIsSupportLoadingDlg) {
		this.mIsSupportLoadingDlg = mIsSupportLoadingDlg;
	}
}
