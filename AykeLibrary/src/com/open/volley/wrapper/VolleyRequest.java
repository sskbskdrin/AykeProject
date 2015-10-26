package com.open.volley.wrapper;

import com.ayke.library.util.L;
import com.open.volley.AuthFailureError;
import com.open.volley.DefaultRetryPolicy;
import com.open.volley.Request;
import com.open.volley.RetryPolicy;
import com.open.volley.VolleyError;

import java.util.HashMap;
import java.util.Map;

/**
 * VolleyRequest
 */

public abstract class VolleyRequest<T> extends Request<T> {
	public static final String TAG = "VolleyRequest";

	public static final int DEFAULT_ID = -1;

	private Map<String, String> mHeaderMap;
	private Map<String, String> mParamsMap;
	public int requestId = DEFAULT_ID;

	public VolleyRequest(String url) {
		this(url, Method.DEPRECATED_GET_OR_POST);
	}

	public VolleyRequest(String url, int method) {
		super(method, url, null);
		L.d(TAG, "VolleyRequest url=" + url);
	}

	@Override
	protected void deliverResponse(T response) {
		L.d(TAG, "VolleyRequest result=" + response.toString());
		onSuccessResponse(response, requestId);
	}

	protected abstract void onSuccessResponse(T response, int id);

	@Override
	public void deliverError(VolleyError error) {
		onErrorResponse(error, requestId);
	}

	protected abstract void onErrorResponse(VolleyError error, int id);

	public void setHeaders(Map<String, String> map) {
		mHeaderMap = map;
	}

	@Override
	public Map<String, String> getHeaders() throws AuthFailureError {
		return mHeaderMap == null ? super.getHeaders() : mHeaderMap;
	}

	public void addParams(String key, String value) {
		if (mParamsMap == null)
			mParamsMap = new HashMap<String, String>();
		mParamsMap.put(key, value);
	}

	public void setParams(HashMap<String, String> map) {
		mParamsMap = map;
	}

	@Override
	protected Map<String, String> getParams() throws AuthFailureError {
		if (mParamsMap != null) {
			StringBuilder sb = new StringBuilder("params : ");
			for (String key : mParamsMap.keySet()) {
				sb.append(key);
				sb.append("=");
				sb.append(mParamsMap.get(key));
				sb.append("&");
			}
			L.d(TAG, sb.toString());
		}
		return mParamsMap;
	}

	@Override
	public Request<?> setRetryPolicy(RetryPolicy retryPolicy) {
		retryPolicy = new DefaultRetryPolicy(20 * 1000,
				DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
		return super.setRetryPolicy(retryPolicy);
	}

	@Override
	public Request<?> setTag(Object tag) {
		return super.setTag(DefaultRetryPolicy.DEFAULT_MAX_RETRIES);
	}

}
